package com.readboy.video.view;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;

import com.readboy.video.ErrorCode;
import com.readboy.video.ErrorType;
import com.readboy.video.IVideoPlayerListener;
import com.readboy.video.proxy.HttpGetProxy;
import com.readboy.video.proxy.HttpGetProxy2;
import com.readboy.video.proxy.Md5Utils;
import com.readboy.video.proxy.Utils;
import com.readboy.video.tools.ScreenResolution;
import com.readboy.video.tools.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class VideoView extends SurfaceView implements HttpGetProxy.OnErrorHttpStatusCodeListener {

    public static final String DEFAULT_CACHE_PATH = "Android/data/com.readboy.mathproblem.video.dreamplayer/cache";

    public static final int STATE_ERROR = 0;
    public static final int STATE_IDLE = 1; // 1
    public static final int STATE_INITIALIZED = 1 << 1; // 2
    public static final int STATE_PREPARING = 1 << 2; // 4
    public static final int STATE_PREPARED = 1 << 3; // 8
    public static final int STATE_PLAYING = 1 << 4; // 16
    public static final int STATE_PAUSED = 1 << 5; // 32
    public static final int STATE_STOPPED = 1 << 6; // 64
    public static final int STATE_PLAYBACK_COMPLETED = 1 << 7; // 128

    /**
     * 缩放参数，原始画面大小
     */
    public static final int VIDEO_LAYOUT_ORIGIN = 0;
    /**
     * 缩放参数，画面全屏
     */
    public static final int VIDEO_LAYOUT_SCALE = 1;
    /**
     * 缩放参数，画面拉伸
     */
    public static final int VIDEO_LAYOUT_STRETCH = 2;
    /**
     * 缩放参数，画面裁剪
     */
    public static final int VIDEO_LAYOUT_ZOOM = 3;

    private String TAG = "oubin_VideoView";
    // settable by the client
    private Uri mUri;
    private String mPath;
    private boolean mIsPlayUrl;

    private Map<String, String> mHeaders;

    private SurfaceHolder mSurfaceHolder = null;
    private boolean mSurfaceReady = false;
    private boolean delayLoad = false;
    private MediaPlayer mMediaPlayer = null;
    private boolean mCanPause;
    private boolean mCanSeekBack;
    private boolean mCanSeekForward;
    private boolean isError38;
    private boolean shouldAdjustSeekComplete;

    /**
     * 是否正在加载，读取新的视频资源，外部设置连接{@link #setVideoURI(String, String, boolean)}
     * 到初始化MediaPlayer的时间。
     * 这过程有异步操作，防止过程surfaceCreated调用，进而调用openVideo().
     * 服务器已关闭。MediaPlayer无法加载数据。
     */
    private boolean isLoadingNewUri;
    private int mAudioSession;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private int mWindowWidth = 0;
    private int mWindowHeight = 0;
    private int mCurrentBufferPercentage;
    private int mSeekWhenPrepared;  // recording the seek position while preparing

    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;
    private int mSaveState = STATE_IDLE;

    private int mLayoutType = VIDEO_LAYOUT_STRETCH;

    private float mAspectRatio = 0.0f;
    private float mFrameScale = 1.0f;

    //拉动进度调整是否真的已经播放就绪。
    private int mInfoWhat = 3;

    private HttpGetProxy proxy;
    private String mCachePath;

    private OnErrorListener mExtraOnErrorListener;
    private OnCompletionListener mExtraOnCompletionListener = null;
    private OnPreparedListener mExtraOnPreparedListener = null;
    private OnSeekCompleteListener mExtraOnSeekCompleteListener = null;
    private List<IVideoPlayerListener> videoPlayerListeners;

    private OnCompletionListener mCompletionListener = new OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.d(TAG, "---- onCompletion ");
            if (mTargetState != STATE_ERROR && isInPlaybackState()) {
                mCurrentState = STATE_PLAYBACK_COMPLETED;
                mTargetState = STATE_PLAYBACK_COMPLETED;
                fireOnCompletion();
            }
            if (mExtraOnCompletionListener != null) {
                mExtraOnCompletionListener.onCompletion(mp);
            }
        }
    };

    private OnPreparedListener mPreparedListener = new OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.d(TAG, " onPrepared mCurrentState: " + mCurrentState + ", mTargetState = " + mTargetState);
            isError38 = false;
            mCurrentState = STATE_PREPARED;
            fireOnPrepared();
            Log.d(TAG, " onPrepared mSeekWhenPrepared: " + mSeekWhenPrepared + ", mCurrentState: " + mCurrentState);
            // mSeekWhenPrepared may be changed after seekTo() call
            int seekToPosition = mSeekWhenPrepared < 0 ? 0 : mSeekWhenPrepared;
            if (seekToPosition >= 0) {
                Log.i(TAG, "onPrepared: seekTo = " + seekToPosition);
                //准备好了，该由谁调用播放。
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    shouldAdjustSeekComplete = true;
                }
                mp.seekTo(seekToPosition);
            } else {
                if (mTargetState == STATE_PLAYING) {
                    Log.e(TAG, "onPrepared: mediaPlayer start ");
                    mp.start();
                    mCurrentState = STATE_PLAYING;
                    fireOnPlaying();
                }
            }

            if (mExtraOnPreparedListener != null) {
                mExtraOnPreparedListener.onPrepared(mp);
            }
        }
    };
    private OnErrorListener mErrorListener = new OnErrorListener() {

        /**
         * 返回值代表什么，请看源码。
         * @return true, 代表调用方已经处理该错误；
         * false，没有处理，MediaPlayer内部处理，会调用onCompletion。
         */
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.e(TAG, " onError what: " + what + ", extra: " + extra + ", hashCode = " + hashCode());
            Log.e(TAG, "onError: current state = " + mCurrentState);
//            if (what == -38) {
//                Log.e(TAG, "onError: 38.");
//                isError38 = true;
//                return false;
//            }
//            isError38 = false;
            //error (1, -2147483648)
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
//            if (proxy != null){
//                proxy.stopProxy();
//            }
//            toastErrorMessage(what, extra);

            handleErrorEvent(what, extra);
            if (mExtraOnErrorListener != null) {
                return mExtraOnErrorListener.onError(mp, what, extra);
            }
            return true;
        }
    };

    private void handleErrorEvent(int what, int extra) {
        String message = String.format(Locale.CHINESE, "未知错误：{%d, %d}", what, extra);
        ErrorType errorType;
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_IO:
                //-1004
                // Stream服务端返回错误 (bad request, unauthorized, forbidden, not found etc)
                message = "文件或网络相关操作错误, 可能网络不稳定";
                errorType = ErrorType.MEDIA_ERROR_INVALID_REQUEST;
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                //-110
                // 端无法连接stream服务端
                message = "某些耗时操作导致播放错误";
                errorType = ErrorType.MEDIA_ERROR_SERVICE_UNAVAILABLE;
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                //-1010
                // 端内部错误
                message = "媒体框架不支持此视频格式";
                errorType = ErrorType.MEDIA_ERROR_INTERNAL_DEVICE_ERROR;
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                //-1007
                // stream服务端接受请求，但未能正确处理 ?????
                message = "比特流不符合相关标准规范";
                errorType = ErrorType.MEDIA_ERROR_INTERNAL_SERVER_ERROR;
                break;
            default:
                // 未知错误
                errorType = ErrorType.MEDIA_ERROR_UNKNOWN;
                break;
        }
        fireOnError(message, errorType);
    }

    private void toastErrorMessage(int what, int extra) {
        String message;
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_IO:
                //-1004
                message = "文件或网络相关操作错误, 可能网络不稳定";
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                //-1007
                message = "比特流不符合相关标准规范";
                break;
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                //200
                message = "视频流及其容器不是有效的逐行播放";
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                //100
                message = "播放服务器出现问题";
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                //-110
                message = "某些耗时操作导致播放错误";
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                //1
                toastUnknownErrorMessage(extra);
                return;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                //-1010
                message = "媒体框架不支持此视频格式";
                break;
            default:
                //error (1, -2147483648)
                message = "视频文件未知错误: " + what + "," + extra;
                break;
        }
        Log.e(TAG, "toastErrorMessage: message = " + message);
        ToastUtils.showLong(getContext(), message);
    }

    private void toastUnknownErrorMessage(int extra) {
        String message;
        switch (extra) {
            case MediaPlayer.MEDIA_ERROR_IO:
                message = "文件或网络相关操作错误，可能网络不稳定";
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                message = "无法连接到服务器";
                break;
            default:
                message = "媒体播放器未知错误";
                break;
        }
        Log.e(TAG, "toastUnknownErrorMessage: message = " + message);
        if (!TextUtils.isEmpty(message)) {
            ToastUtils.showLong(getContext(), message);
        }
    }

    private void toastErrorMessage2(int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_IO:
                Toast.makeText(getContext(), "文件或网络相关操作错误， 将退出视频 ", Toast.LENGTH_LONG).show();
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Toast.makeText(getContext(), "比特流不符合相关标准规范， 将退出视频 ", Toast.LENGTH_LONG).show();
                break;
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(getContext(), "视频流及其容器不是有效的逐行播放 ， 将退出视频 ", Toast.LENGTH_LONG).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(getContext(), "播放服务器出现问题， 将退出视频 ", Toast.LENGTH_LONG).show();
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Toast.makeText(getContext(), "某些耗时操作导致播放错误， 将退出视频 ", Toast.LENGTH_LONG).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                if (extra == MediaPlayer.MEDIA_ERROR_IO) {
                    Toast.makeText(getContext(), "文件或网络相关操作错误，可能网络不稳定，将退出视频 ", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "媒体播放器未知错误， 将退出视频 ", Toast.LENGTH_LONG).show();
                }
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Toast.makeText(getContext(), "媒体框架不支持此视频格式， 将退出视频 ", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(getContext(), "视频文件未知错误，将退出播放 ", Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * 非常不可靠，有的系统，可能不会返回what=3。
     */
    private OnInfoListener mInfoListener = new OnInfoListener() {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            Log.d(TAG, "---- onInfo what: " + what + ", extra: " + extra + ", " + mp.getCurrentPosition());
            //TODO: ---- onInfo what: 3, extra: 0, 加载完整判断是否可靠。
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    //701, 开始缓冲中
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        ToastUtils.showShort(getContext(), "播放器正在加载资源文件");
                    } else {
                        if (mp.isPlaying()) {
                            fireOnBufferingStart();
                        }
                    }
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    //702, 缓冲结束
                    break;
                case 703:
                    // Bandwidth in recent past MEDIA_INFO_NETWORK_BANDWIDTH = 703
                    // Toast.makeText(getContext(), " 当前宽带: "+extra+"(kbps)", Toast.LENGTH_LONG).show();
                    break;
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    //3，视频开始整备中
                    Log.e(TAG, "onInfo: mCurrentState = " + mCurrentState + ", mTargetState = " + mTargetState);
                    if (shouldAdjustSeekComplete) {
                        fireOnPlaying();
                        shouldAdjustSeekComplete = false;
                    }
                    fireOnBufferingEnd();
                    break;
                case MediaPlayer.MEDIA_INFO_UNKNOWN:
                    //1, 未知信息
                    break;
                case 2:
                    break;
                default:
                    Log.e(TAG, "onInfo: other what = " + what);
                    break;
            }
            return true;
        }
    };

    private OnSeekCompleteListener mSeekCompleteListener = new OnSeekCompleteListener() {

        @Override
        public void onSeekComplete(MediaPlayer mp) {
            Log.d(TAG, "---- onSeekComplete mCurrentState: " + mCurrentState);
            Log.e(TAG, "onSeekComplete: mTargetState = " + mTargetState + ", mCurrentState = " + mCurrentState
                    + ", mSaveState = " + mSaveState);
            if (mSaveState == STATE_PAUSED || mSaveState == STATE_PREPARED
                    || mSaveState == STATE_PREPARING) {
                mTargetState = STATE_PAUSED;
            }
            if (mTargetState != STATE_PAUSED && mSaveState == STATE_PLAYING) {
                mTargetState = STATE_PLAYING;
                mSaveState = STATE_IDLE;
            }
            if (mExtraOnSeekCompleteListener != null) {
                mExtraOnSeekCompleteListener.onSeekComplete(mp);
            }
            Log.e(TAG, "onSeekComplete: after mTargetState = " + mTargetState);
            if (mTargetState == STATE_PLAYING) {
                Log.e(TAG, "onSeekComplete: mediaPlayer start. isPlaying = " + mp.isPlaying()
                        + ", shouldAdjustSeekComplete = " + shouldAdjustSeekComplete);
                mp.start();
                if (!shouldAdjustSeekComplete) {
                    mCurrentState = STATE_PLAYING;
                    fireOnPlaying();
                } else {
                    //监听OnInfoListener，what=3才分发fireOnPlaying();
                }
            } else if (mTargetState == STATE_PAUSED) {
                Log.e(TAG, "onSeekComplete: start and pause.");
//                mp.start();
//                mp.pause();
                shouldAdjustSeekComplete = false;
                mCurrentState = STATE_PAUSED;
                mSaveState = STATE_IDLE;
                fireOnPaused();
            } else {
                shouldAdjustSeekComplete = false;
            }
        }
    };
    private OnBufferingUpdateListener mBufferingUpdateListener = new OnBufferingUpdateListener() {

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
//            Log.e(TAG, "---- onBufferingUpdate percent: " + percent);
            mCurrentBufferPercentage = percent;
            fireOnBufferingUpdate(percent);
        }
    };
    private OnVideoSizeChangedListener mVideoSizeChangedListener = new OnVideoSizeChangedListener() {

        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            Log.d(TAG, "---- onVideoSizeChanged width: " + width + ", height: " + height);
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            setLayout();
        }
    };


    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            mSurfaceWidth = w;
            mSurfaceHeight = h;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.e(TAG, "surfaceCreated: VideoView = " + VideoView.this.hashCode());
            Log.i(TAG, " SurfaceHolder surfaceCreated holder: " + holder + ", delayLoad = " + delayLoad);
            Log.i(TAG, "surfaceCreated: mCurrentState = " + mCurrentState + " mTargetState = " + mTargetState
                    + ", mSaveState = " + mSaveState);
            Log.e(TAG, "surfaceCreated: isPlayUrl = " + mIsPlayUrl + ", isLoadingNewUri = " + isLoadingNewUri);
            mSurfaceHolder = holder;
//            恢复现场
            if (!isLoadingNewUri) {
                if (delayLoad || mSaveState == STATE_PREPARING
                        || mSaveState == STATE_PREPARED
                        || mSaveState == STATE_PAUSED
                        || mSaveState == STATE_PLAYING
                        || mTargetState == STATE_PLAYING) {
                    if (delayLoad || !mIsPlayUrl || isConnected(VideoView.this.getContext())) {
                        openVideo();
                    }
                }
            }
            Log.i(TAG, " SurfaceHolder surfaceCreated end mCurrentState: " + mCurrentState);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // after we return from this we can't use the surface any more
            Log.e(TAG, "surfaceDestroyed: VideoView = " + +VideoView.this.hashCode());
            Log.i(TAG, " SurfaceHolder surfaceDestroyed holder: " + holder);
            //保存现场
            mSaveState = mCurrentState;
            if (mSaveState == STATE_PAUSED
                    || mSaveState == STATE_PLAYING
                    || mSaveState == STATE_PREPARING
                    || mSaveState == STATE_PREPARED) {
                int seek = getCurrentPosition() - 1500;
                mSeekWhenPrepared = seek > 0 ? seek : 0;
            }
            Log.e(TAG, "surfaceDestroyed: saveState = " + mSaveState
                    + ", mSeekWhenPrepared = " + mSeekWhenPrepared);
//            release(true);

            //频繁退出，会导致播放出错，需要释放资源。
            if (mMediaPlayer != null) {
                mCurrentState = STATE_IDLE;
//                if (mMediaPlayer.isPlaying()) {
//                    Log.e(TAG, "surfaceDestroyed: mediaPlayer isPlaying");
//                    mMediaPlayer.stop();
//                }

                try {
                    mMediaPlayer.stop();
                } catch (IllegalStateException e) {
                    Log.e(TAG, "surfaceDestroyed: stop e = " + e.toString());
                }
                mMediaPlayer.reset();
                Log.e(TAG, "surfaceDestroyed: release MediaPlayer.");
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
//            if (mCurrentState == STATE_PLAYING) {
////                pause();
//            } else if (mCurrentState == STATE_PREPARING) {
//                if (mMediaPlayer != null) {
//                    mCurrentState = STATE_IDLE;
//                    mMediaPlayer.reset();
//                    mMediaPlayer.release();
//                    mMediaPlayer = null;
//                }
//            }
            mSurfaceHolder = null;
        }
    };

    private Context mContext = null;

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initVideoView();
    }

    public void setLayout() {
//		Log.i("", " setLayout() type: "+mLayoutType+", frameScale: "+mFrameScale+", aspectRatio: "+mAspectRatio);
//        mFrameScale = 0.0f;
//        mAspectRatio = 0.0f;
        setLayout(mLayoutType, mFrameScale, mAspectRatio);
    }

    public void setLayout(int showWidth, int showHeight) {
        mWindowWidth = showWidth;
        mWindowHeight = showHeight;
        setLayout(mLayoutType, mFrameScale, mAspectRatio);
    }

    public void setLayout(int layout, float videoScale, float aspectRatio) {
        LayoutParams lp = (LayoutParams) getLayoutParams();

        int windowWidth = mWindowWidth;
        int windowHeight = mWindowHeight;
        if (mWindowWidth < 1 || mWindowHeight < 1) {
            Pair<Integer, Integer> res = ScreenResolution.getResolution(getContext());
            windowWidth = res.first.intValue();
            windowHeight = res.second.intValue();
        }

        float windowRatio = windowWidth / (float) windowHeight;
        float videoShowW = lp.width;
        float videoShowH = lp.height;
        int videoResW = mVideoWidth;
        int videoResH = mVideoHeight;
        if (videoResW < 1) {
            videoResW = windowWidth;
        }
        if (videoResH < 1) {
            videoResH = windowHeight;
        }
        float resAspectRatio = videoResW / (float) videoResH;
        mAspectRatio = aspectRatio <= 0.01f ? resAspectRatio : aspectRatio;
//		Log.i("", " resAspectRatio: "+resAspectRatio+", aspectRatio: "+aspectRatio+", videoResW: "+videoResW+", videoResH: "+videoResH);
        if (VIDEO_LAYOUT_ORIGIN == layout) {
            videoShowW = videoResW;
            videoShowH = videoResH;
        } else if (layout == VIDEO_LAYOUT_ZOOM) {

        } else if (layout == VIDEO_LAYOUT_SCALE) {
            videoShowW = videoResW * videoScale;
            videoShowH = videoResH * videoScale;
            mFrameScale = videoScale;
        } else {
//			Log.i("", " 111 windowWidth: "+windowWidth+", windowHeight: "+windowHeight);
            videoShowW = windowWidth;
            videoShowH = windowHeight;

            if ((videoShowW / videoShowH) > resAspectRatio) {
                videoShowW = videoShowH * resAspectRatio;
            } else {
                videoShowH = videoShowW * videoResH / videoResW;
            }

//			Log.i("", " 111 videoResW: "+videoResW+", videoResH: "+videoResH);
        }

        if (aspectRatio > 0.01f) {
            if (aspectRatio > resAspectRatio) {
                videoShowW = aspectRatio * videoShowH;
            }
            if (aspectRatio < resAspectRatio) {
                videoShowH = videoShowW / aspectRatio;
            }
        }
//		Log.i("", " 000 videoShowW: "+videoShowW+", videoH: "+videoShowH);
        float parentRatioW = 1.0f;
        float parentRatioH = 1.0f;
        if (windowWidth < videoShowW) {
            parentRatioW = videoShowW / windowWidth;
        }
        if (windowHeight < videoShowH) {
            parentRatioH = videoShowH / windowHeight;
        }

        if (parentRatioW > 1.0f || parentRatioH > 1.0f) {
            float parentRatio = parentRatioH;
            if (parentRatioW > parentRatioH) {
                parentRatio = parentRatioW;
            }
            videoShowW /= parentRatio;
            videoShowH /= parentRatio;
        }
        lp.width = (int) videoShowW;
        lp.height = (int) videoShowH;
//		Log.i("", " videoShowW: "+videoShowW+", videoShowH: "+videoShowH);
        if (videoShowH < 0.001f) {
            videoShowH = windowHeight;
        }
        mLayoutType = layout;
        getHolder().setFixedSize((int) videoShowW, (int) videoShowH);
        setLayoutParams(lp);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		Log.i("", "---- onMeasure widthMeasureSpec: "+widthMeasureSpec+", heightMeasureSpec: "+heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        //wrap_content
//        System.out.println("sizeWidth:" + sizeWidth + ", sizeHeight:" + sizeHeight + "; modeWidth:" + modeWidth + ", modeHeight:" + modeHeight);

        setMeasuredDimension(sizeWidth, sizeHeight);
    }

    /**
     * Sets video path.
     *
     * @param path the path of the video.
     */
    public void setVideoPath(String path) {
//        setVideoURI(Uri.parse(path));
        new AsyncVideoSetDataSource().execute(path, true);
        mSaveState = STATE_IDLE;
    }

    /**
     * Sets video URI.
     *
     * @param uri     the URI of the video.
     * @param isProxy is deprecated
     */
    public void setVideoURI(String uri, String mCachePath, @Deprecated boolean isProxy) {
        Log.e(TAG, "setVideoURI() called with: uri = " + uri + ", mCachePath = " + mCachePath + ", isProxy = " + isProxy + "");
        this.mCachePath = mCachePath;
        new AsyncVideoSetDataSource().execute(uri, isProxy);
        mSaveState = STATE_IDLE;
    }

    /**
     * 仅加载视频内容，加载完不播放。
     *
     * @param uri        播放链接
     * @param mCachePath 缓存路径
     */
    public void loadVideoUri(String uri, String mCachePath) {
        Log.e(TAG, "loadVideoUri() called with: uri = " + uri + ", mCachePath = " + mCachePath + "");
        setVideoURI(uri, mCachePath, true);
        mSaveState = STATE_PAUSED;
    }

    /**
     * Sets video URI using specific headers.
     *
     * @param uri     the URI of the video.
     * @param headers the headers for the URI request.
     *                Note that the cross domain redirection is allowed by default, but that can be
     *                changed with key/value pairs through the headers parameter with
     *                "android-allow-cross-domain-redirect" as the key and "0" or "1" as the value
     *                to disallow or allow cross domain redirection.
     */
    private void setVideoURI(Uri uri, Map<String, String> headers) {
        Log.e(TAG, "setVideoURI: uri = " + uri.toString());
        mUri = uri;
        mHeaders = headers;
        mCurrentState = STATE_IDLE;
        openVideo();
        requestLayout();
        invalidate();
        Log.i(TAG, "---- setVideoURI end");
    }

    public void suspend() {
        release(false);
    }

    public boolean start() {
        boolean isStart = false;
        Log.d(TAG, " VideoView start mCurrentState: " + mCurrentState);
        mTargetState = STATE_PLAYING;
        if (isInPlaybackState()) {
            Log.i(TAG, " VideoView start mMediaPlayer start");
            mCurrentState = STATE_PLAYING;
            mMediaPlayer.start();
            isStart = true;
            fireOnPlaying();
        }
        return isStart;
    }

    public void pause() {
        Log.i(TAG, " ---- VideoView pause mCurrentState: " + mCurrentState);
        mTargetState = STATE_PAUSED;
        if (isInPlaybackState()) {
            Log.i(TAG, " ---- VideoView pause isInPlaybackState true, mMediaPlayer.isPlaying(): " + mMediaPlayer.isPlaying());
            if (mMediaPlayer.isPlaying()) {
                Log.e(TAG, "pause: mediaPlayer pause.");
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
                fireOnPaused();
            }
        }
    }

    public void stopPlayback() {
        Log.e(TAG, "stopPlayback: ");
        mTargetState = STATE_STOPPED;
//        if (proxy != null) {
//            proxy.stopProxy();
//            proxy = null;
//        }
        if (mMediaPlayer != null) {
            Log.e(TAG, "stopPlayback: MediaPlayer stop. mCurrentState = " + mCurrentState);
//            if (mMediaPlayer.isPlaying()) {
            if (mCurrentState == STATE_PREPARING || isInPlaybackState()) {
                try {
                    boolean isPlaying = mMediaPlayer.isPlaying();
                    Log.e(TAG, "stopPlayback: isPlaying = " + isPlaying);
                    if (isPlaying) {
                        mMediaPlayer.stop();
                    }
                } catch (IllegalStateException e) {
                    Log.e(TAG, "stopPlayback: stop e = " + e.toString());
                }

                try {
                    Log.e(TAG, "stopPlayback: mediaPlayer reset.");
                    mMediaPlayer.reset();
                } catch (IllegalStateException e) {
                    Log.e(TAG, "stopPlayback: stop e = " + e.toString());
                }
            } else {
                Log.e(TAG, "stopPlayback: no need stop MediaPlayer.");
            }
//            }
            //调用release()之后，调用其他方法，如stop, reset会导致抛出IllegalStateException异常。
            //如果无释放容易在加载新的数据时，MediaPlayer报错。
            mMediaPlayer.release();
            Log.e(TAG, "stopPlayback: release MediaPlayer.");
            mMediaPlayer = null;
//            mUri = null;
        }
//        if (proxy != null) {
//            proxy.stopProxy();
//            proxy = null;
//        }

        mCurrentState = STATE_STOPPED;
        fireOnStopped();
    }

    public int getDuration() {
        if (isInPlaybackState()) {
//            Log.e(TAG, "getDuration: ");
            return mMediaPlayer.getDuration();
        }

        return -1;
    }

    public int getCurrentPosition() {
        if (isInPlaybackState()) {
//            Log.e(TAG, "getCurrentPosition: ");
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void seekTo(int msec) {
        Log.e(TAG, "seekTo: msec = " + msec + ", isInPlay = " + isInPlaybackState());
        mTargetState = STATE_PLAYING;
        if (isInPlaybackState()) {
            Log.e(TAG, "seekTo: msec = " + msec);
            mMediaPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    public boolean isPlaying() {
//        Log.e(TAG, "isPlaying: current state = " + mCurrentState);
        boolean isState = isInPlaybackState();
        if (isState) {
            isState = mMediaPlayer.isPlaying();
        }
        return isState;
    }

    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    public void setOnCompletionListener(OnCompletionListener l) {
        this.mExtraOnCompletionListener = l;
    }

    @Deprecated
    public void setOnPreparedListener(OnPreparedListener l) {
        this.mExtraOnPreparedListener = l;
    }

    /**
     * @deprecated {@link IVideoPlayerListener#onError(String, ErrorType)}
     */
    @Deprecated
    public void setOnErrorListener(OnErrorListener listener) {
        this.mExtraOnErrorListener = listener;
    }

    /**
     * @deprecated {@link IVideoPlayerListener#onCompletion()}
     */
    @Deprecated
    public void setOnSeekCompleteListener(OnSeekCompleteListener l) {
        this.mExtraOnSeekCompleteListener = l;
    }

    private void openVideo() {
        if (!TextUtils.isEmpty(mPath) && !mPath.startsWith("http://")) {
            File file = new File(mPath);
            if (!file.exists()) {
                Log.e(TAG, "openVideo: file not exists, file:" + file.getAbsolutePath());
                fireOnError("文件已经不存在", ErrorType.MEDIA_ERROR_UNKNOWN);
                return;
            }
        }

        mTargetState = STATE_PLAYING;
        Log.e(TAG, "openVideo: mUri = " + mUri);
        if (mUri == null || mSurfaceHolder == null || proxy == null) {
            // not ready for playback just yet, will try again later
            Log.i(TAG, "openVideo: VideoView mUr = " + mUri + ", mSurfaceHolder: " + mSurfaceHolder
                    + ", proxy = " + proxy);
            if (mSurfaceHolder == null) {
                delayLoad = true;
            } else {
                delayLoad = false;
            }
            return;
        }
        delayLoad = false;
        final Context context = getContext();
//        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        // we shouldn't clear the target state, because somebody might have called start() previously
        release(false);
        try {
            fireOnInit();
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }
            // TODO: create SubtitleController in MediaPlayer, but we need
            // a context for the subtitle renderers
//            if (mAudioSession != 0) {
//                mMediaPlayer.setAudioSessionId(mAudioSession);
//            } else {
//                mAudioSession = mMediaPlayer.getAudioSessionId();
//            }
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mVideoSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);

            mCurrentBufferPercentage = 0;
//            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(context, mUri, mHeaders);
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
            Log.w(TAG, "openVideo: mCurrentState STATE_PREPARING ");
        } catch (IOException ex) {
            Log.e(TAG, "openVideo: Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_IO, 0);
            fireOnError("IOException play url :"
                    + mUri.getPath(), ErrorType.MEDIA_ERROR_INTERNAL_DEVICE_ERROR);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "openVideo: Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            fireOnError("IllegalArgumentException : " + ex.toString(), ErrorType.MEDIA_ERROR_INTERNAL_DEVICE_ERROR);
        } finally {
        }
    }

    private void initVideoView() {
        Log.e(TAG, "initVideoView: ");
        mVideoWidth = 0;
        mVideoHeight = 0;
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
        getHolder().addCallback(mSHCallback);
//        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        videoPlayerListeners = Collections.synchronizedList(new LinkedList<IVideoPlayerListener>());
    }

    /**
     * release the media player in any state
     */
    private void release(boolean clearTargetState) {
        Log.e(TAG, "release() called with: clearTargetState = " + clearTargetState + "");
        Log.e(TAG, "release: mTargetState = " + mTargetState + ", mCurrentState = " + mCurrentState);
        if (mMediaPlayer != null) {
            mCurrentState = STATE_IDLE;
//            if (mMediaPlayer.isPlaying()) {
//                mMediaPlayer.stop();
//            }

            try {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
            } catch (IllegalStateException e) {
                Log.e(TAG, "release: stop e = " + e.toString());
            }
//            mMediaPlayer.release();
//            mMediaPlayer = null;

//            AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
//            am.abandonAudioFocus(null);

            if (clearTargetState) {
                mTargetState = STATE_IDLE;
            }
        }
    }

    /**
     * 外部调用，释放资源。
     */
    public void release() {
        Log.e(TAG, "release: ");
        if (mMediaPlayer != null) {
            mCurrentState = STATE_IDLE;
//            if (mMediaPlayer.isPlaying()) {
//                mMediaPlayer.stop();
//            }

            try {
                if (mMediaPlayer.isPlaying()) {
                    Log.e(TAG, "release: isPlaying = true, stop MediaPlayer ");
                    mMediaPlayer.stop();
                }
            } catch (IllegalStateException e) {
                Log.e(TAG, "release: stop e = " + e.toString());
            }
            mMediaPlayer.reset();
            Log.e(TAG, "release: MediaPlayer.");
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        videoPlayerListeners.clear();
        if (proxy != null) {
            proxy.stopProxy();
            proxy = null;
        }
    }

    public boolean isInPlaybackState() {
//        Log.e(TAG, "isInPlaybackState: mCurrentState = " + mCurrentState);
        return (mMediaPlayer != null
                && mCurrentState != STATE_ERROR
                && mCurrentState != STATE_IDLE
                && mCurrentState != STATE_STOPPED
                && mCurrentState != STATE_PREPARING
        );
    }

    public int getCurrentState() {
        return mCurrentState;
    }

    @Override
    public void onErrorCode(int httpStatusCode) {
        Log.e(TAG, "onErrorCode() called with: httpStatusCode = " + httpStatusCode + "");
        //TODO: 403错误码处理。
        if (ErrorCode.AUTHENTICATION == httpStatusCode) {
            fireOnError("视频链接有问题", ErrorType.MEDIA_ERROR_SERVICE_UNAVAILABLE);
        }
    }

    public void addVideoPlayerListener(IVideoPlayerListener listener) {
        if (!videoPlayerListeners.contains(listener)) {
            videoPlayerListeners.add(listener);
        }
    }

    public void removeVideoPlayerListener(IVideoPlayerListener listener) {
        if (videoPlayerListeners.contains(listener)) {
            videoPlayerListeners.remove(listener);
        }
    }

    private void fireOnError(String error, ErrorType errorType) {
        Log.e(TAG, "fireOnError() called with: error = " + error + ", errorType = " + errorType + "");
        for (IVideoPlayerListener listener : videoPlayerListeners) {
            if (listener != null) {
                listener.onError(error, errorType);
            }
        }
    }

    private void fireOnInit() {
        Log.e(TAG, "fireOnInit: ");
        for (IVideoPlayerListener listener : videoPlayerListeners) {
            if (listener != null) {
                listener.onInit();
            }
        }
    }

    private void fireOnPaused() {
        Log.e(TAG, "fireOnPaused: ");
        for (IVideoPlayerListener listener : videoPlayerListeners) {
            if (listener != null) {
                listener.onPaused();
            }
        }
    }

    private void fireOnStopped() {
        Log.e(TAG, "fireOnStopped: ");
        for (IVideoPlayerListener listener : videoPlayerListeners) {
            if (listener != null) {
                listener.onStopped();
            }
        }
    }

    private void fireOnPlaying() {
        Log.e(TAG, "fireOnPlaying: ");
        for (IVideoPlayerListener listener : videoPlayerListeners) {
            if (listener != null) {
                listener.onPlaying();
            }
        }
    }

    private void fireOnRelease() {
        Log.e(TAG, "fireOnRelease: ");
        for (IVideoPlayerListener listener : videoPlayerListeners) {
            if (listener != null) {
                listener.onRelease();
            }
        }
    }

    private void fireOnPrepared() {
        Log.e(TAG, "fireOnPrepared: ");
        for (IVideoPlayerListener listener : videoPlayerListeners) {
            if (listener != null) {
                listener.onPrepared();
            }
        }
    }

    private void fireOnBufferingUpdate(int percent) {
//        Log.e(TAG, "fireOnBufferingUpdate: ");
        for (IVideoPlayerListener listener : videoPlayerListeners) {
            if (listener != null) {
                listener.onBufferingUpdate(percent);
            }
        }
    }

    private void fireOnCompletion() {
        Log.e(TAG, "fireOnCompletion: ");
        for (IVideoPlayerListener listener : videoPlayerListeners) {
            if (listener != null) {
                listener.onCompletion();
            }
        }
    }

    private void fireOnBufferingStart() {
        Log.e(TAG, "fireOnBufferingStart: ");
        for (IVideoPlayerListener listener : videoPlayerListeners) {
            if (listener != null) {
                listener.onBufferingStart();
            }
        }
    }

    private void fireOnBufferingEnd() {
        Log.e(TAG, "fireOnBufferingEnd: ");
        for (IVideoPlayerListener listener : videoPlayerListeners) {
            if (listener != null) {
                listener.onBufferingEnd();
            }
        }
    }

    private boolean saveStateEffective() {

        return false;
    }

    /**
     * 判断是否有网络
     */
    private boolean isConnected(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        Log.e(TAG, "isConnected: " + (info != null && info.isConnected()));
        return info != null && info.isConnected();
    }

    private NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    private class AsyncVideoSetDataSource extends AsyncTask<Object, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e(TAG, "onPreExecute: ");
            isLoadingNewUri = true;
            if (mCurrentState == STATE_PREPARING || isInPlaybackState()) {
                stopPlayback();
            }
        }

        @Override
        protected String doInBackground(Object... params) {
            if (proxy != null) {
                proxy.stopProxy();
            }

            String path = (String) params[0];
            mPath = path;
            boolean isProxy = (boolean) params[1];//是否是加密视频
            Log.e(TAG, "doInBackground: mPath = " + path);
            boolean isPlayUrl = false;
            if (path != null) {
                if (path.contains("http:")) {
                    isPlayUrl = true;
                    mIsPlayUrl = true;
                } else {
                    mIsPlayUrl = false;
                }
            }
            String back;

//			boolean isProxy =false;
//			if(mPath!=null){
//				if(!mIsPlayUrl){
//					isProxy= MovieFile.isEncryption(mContext, mPath);
//				}else {
//					isProxy=true;
//					//此处判断 网络视频  是否加密
//				}
//			}

            Log.e(TAG, "mIsPlayUrl:" + isPlayUrl);
            Log.e(TAG, "mPath4:" + path);
            if (isProxy) {   //是否是加密视频
                Log.d(TAG, "-------- doInBackground 走代理服务器  mIsPlayUrl：" + isPlayUrl);
                if (proxy != null) {
                    proxy.stopProxy();
                }
                if (isPlayUrl) {
                    String md5 = Md5Utils.getMd5(path);
                    String cachePath = Utils.getCachePath(mContext, mCachePath, md5);
                    proxy = new HttpGetProxy(mContext, path, cachePath, md5, true, VideoView.this);
                } else {
                    proxy = new HttpGetProxy(mContext, path, null);
                }
                back = proxy.getLocalURL();
            } else {
                Log.e(TAG, "-------- doInBackground 不走代理服务器 mIsPlayUrl： " + isPlayUrl);
                back = path;
            }
            return back;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e(TAG, " ---- AsyncVideoSetDataSource: onPostExecute result: " + result);
            isLoadingNewUri = false;
            if (result != null) {
                setVideoURI(Uri.parse(result), null);
//				if (mVideoDbInfo.mType != VideoInfoDatabaseProxy.TYPE_MICRO) {
//					mVideoDbInfo.mDependency = mPath;
//				}
            }
        }

    }

}
