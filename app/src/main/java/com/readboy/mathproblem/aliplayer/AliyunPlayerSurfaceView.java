package com.readboy.mathproblem.aliplayer;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.aliyun.vodplayer.media.AliyunVidSts;
import com.aliyun.vodplayer.media.AliyunVodPlayer;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.readboy.aliyunplayerlib.helper.VidStsHelper;
import com.readboy.mathproblem.video.IVideoPlayer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by oubin on 2018/4/25.
 */

public class AliyunPlayerSurfaceView extends SurfaceView implements IVideoPlayer,
        IAliyunVodPlayer.OnSeekCompleteListener,
        IAliyunVodPlayer.OnPreparedListener,
        IAliyunVodPlayer.OnFirstFrameStartListener,
        IAliyunVodPlayer.OnLoadingListener,
        IAliyunVodPlayer.OnStoppedListener,
        IAliyunVodPlayer.OnCompletionListener,
        IAliyunVodPlayer.OnTimeExpiredErrorListener,
        IAliyunVodPlayer.OnErrorListener,
        IAliyunVodPlayer.OnInfoListener {
    private static final String TAG = "AliyunPlayerSurfaceView";

    private List<IVideoPlayerListener> mPlayerListeners = new CopyOnWriteArrayList<>();

    private AliyunVodPlayer mAliyunVodPlayer;
    private AliyunVidSts mVidSts;
    private VidStsHelper mVidStsHelper = null;
    private String mVid;
    private long mSeekPosition;
    private PlayState mCurrentState = PlayState.IDLE;
    private PlayState mTargetState = PlayState.IDLE;
    private SurfaceHolder.Callback mSurfaceCallback;

    public AliyunPlayerSurfaceView(Context context) {
        this(context, null);
    }

    public AliyunPlayerSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AliyunPlayerSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPlayer();
        initView();
    }

    private void initPlayer() {
        mAliyunVodPlayer = new AliyunVodPlayer(getContext());
        String sdDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MathProblem/cache/";
        mAliyunVodPlayer.setPlayingCache(true, sdDir, 60 * 60, 300);
        mAliyunVodPlayer.setCirclePlay(true);

        mAliyunVodPlayer.setOnPreparedListener(this);
        mAliyunVodPlayer.setOnFirstFrameStartListener(this);
        mAliyunVodPlayer.setOnErrorListener(this);
        mAliyunVodPlayer.setOnCompletionListener(this);
        mAliyunVodPlayer.setOnSeekCompleteListener(this);
        mAliyunVodPlayer.setOnStoppedListner(this);
//        mAliyunVodPlayer.enableNativeLog();

        mVidStsHelper = new VidStsHelper();
    }

    private void initView() {
        mSurfaceCallback = new InnerCallback();
        getHolder().addCallback(mSurfaceCallback);
    }

    private void getVidsts() {
        if (mVidStsHelper.isGettingVidsts()) {
            return;
        }
        mVidStsHelper.getVidSts(new VidStsHelper.OnStsResultListener() {
            @Override
            public void onSuccess(String akid, String akSecret, String token) {
                Log.e(TAG, "onSuccess() called with: akid = " + akid + ", akSecret = " + akSecret + ", token = " + token + "");
                String vid = mVid;
//                Log.e(TAG, "onSuccess: resource =  " + mCurrentVideoResource.getVideoUri().toString());
//                Log.e(TAG, "onSuccess: vid = " + vid);
                mVidSts = new AliyunVidSts();
                mVidSts.setVid(vid);
                mVidSts.setAcId(akid);
                mVidSts.setAkSceret(akSecret);
                mVidSts.setSecurityToken(token);

                if (mTargetState == PlayState.PLAYING) {
                    prepareAsync();
                }
            }

            @Override
            public void onFail(int errno) {
                Log.e(TAG, "onFail: errno = " + errno);
                mVidSts = null;
            }
        });
    }

    private void prepareAsync() {
        if (mVidSts == null) {
            getVidsts();
        } else {
            mAliyunVodPlayer.prepareAsync(mVidSts);
        }
    }

    public void playWithVid(String vid) {
        fireOnInit();
        mVid = vid;
        prepareAsync();
    }

    public void playWithVid(String vid, long seekPosition) {
        fireOnInit();
        mVid = vid;
        this.mSeekPosition = seekPosition;
    }

    @Override
    public PlayState getPlayState() {
        return mCurrentState;
    }

    @Override
    public void play(IVideoResource videoResource) {
        fireOnInit();
        mCurrentState = PlayState.PREPARING;
    }

    @Override
    public void play(IVideoResource videoResource, long seekPosition) {

    }

    @Override
    public void pause() {
        if (isPlaying()) {
            mAliyunVodPlayer.pause();
            mCurrentState = PlayState.PAUSED;
            fireOnPaused();
        }
    }

    @Override
    public void stop() {
        mAliyunVodPlayer.stop();
        mCurrentState = PlayState.STOPPED;
    }

    @Override
    public void resume() {
        mAliyunVodPlayer.resume();
    }

    @Override
    public void seekTo(long milliseconds) {
        mAliyunVodPlayer.seekTo((int) milliseconds);
    }

    @Override
    public void release() {
        mAliyunVodPlayer.stop();
        mAliyunVodPlayer.release();
        mAliyunVodPlayer.setDisplay(null);
        getHolder().removeCallback(mSurfaceCallback);
        mSurfaceCallback = null;
    }

    @Override
    public void setVolume(float volume) {
        mAliyunVodPlayer.setVolume((int) volume);
    }

    @Override
    public float getVolume() {
        return mAliyunVodPlayer.getVolume();
    }

    @Override
    public void setMute(boolean mute) {
        mAliyunVodPlayer.setMuteMode(mute);
    }

    @Override
    public boolean getMute() {
        return false;
    }

    @Override
    public long getCurrentPosition() {
        return mAliyunVodPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mAliyunVodPlayer.getDuration();
    }

    @Override
    public float getBufferPercentage() {
        return mAliyunVodPlayer.getBufferingPosition();
    }

    @Override
    public void addMediaPlayerListener(IVideoPlayerListener listener) {
        if (!mPlayerListeners.contains(listener)) {
            mPlayerListeners.add(listener);
        }
    }

    @Override
    public void removeMediaPlayerListener(IVideoPlayerListener listener) {
        mPlayerListeners.remove(listener);
    }

    @Override
    public void setActive(boolean isActive) {
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void setPlaySpeed(float var1) {
        mAliyunVodPlayer.setPlaySpeed(var1);
    }

    @Override
    public void changeQuality(String var1) {
        mAliyunVodPlayer.changeQuality(var1);
    }


    @Override
    public void onCompletion() {
        fireOnCompletion();
    }

    @Override
    public void onError(int i, int i1, String s) {
        fireOnError(s, i);
    }

    @Override
    public void onFirstFrameStart() {
        fireOnPlaying();
    }

    @Override
    public void onInfo(int i, int i1) {
        fireOnInfo(i, i1);
    }

    @Override
    public void onLoadStart() {
        fireOnBuffingStart();
    }

    @Override
    public void onLoadEnd() {
        fireOnBuffingEnd();
    }

    @Override
    public void onLoadProgress(int i) {
        fireOnBuffingUpdate(i);
    }

    @Override
    public void onPrepared() {
        fireOnPrepared();
        if (mSeekPosition > 0) {
            mAliyunVodPlayer.seekTo((int) mSeekPosition);
        }
        if (mTargetState == PlayState.PLAYING) {
            mAliyunVodPlayer.start();
        }
    }

    @Override
    public void onSeekComplete() {

    }

    @Override
    public void onStopped() {
        fireOnStopped();
    }

    @Override
    public void onTimeExpiredError() {
        //4002是鉴权信息过期，跟onTimeExpiredError重复，所以不处理
        fireOnError("播放链接已过期", 4002);
    }

    public boolean isPlaying() {
        return mAliyunVodPlayer.isPlaying();
    }

    protected void fireOnInit() {
        mCurrentState = PlayState.PREPARING;
        mPlayerListeners.forEach(IVideoPlayerListener::onInit);
    }

    protected void fireOnPrepared() {
        mCurrentState = PlayState.PREPARED;
        mPlayerListeners.forEach(IVideoPlayerListener::onPrepared);
    }

    protected void fireOnPlaying() {
        mCurrentState = PlayState.PLAYING;
        mPlayerListeners.forEach(IVideoPlayerListener::onPlaying);
    }

    protected void fireOnPaused() {
        mCurrentState = PlayState.PAUSED;
        mPlayerListeners.forEach(IVideoPlayerListener::onPaused);
    }

    protected void fireOnStopped() {
        mCurrentState = PlayState.STOPPED;
        mPlayerListeners.forEach(IVideoPlayerListener::onStopped);
    }

    protected void fireOnCompletion() {
        mCurrentState = PlayState.COMPLETED;
        mPlayerListeners.forEach(IVideoPlayerListener::onCompletion);
    }

    protected void fireOnError(String error, int errorNo) {
        mCurrentState = PlayState.ERROR;
        for (IVideoPlayerListener mPlayerListener : mPlayerListeners) {
            mPlayerListener.onError(error, errorNo);
        }
    }

    protected void fireOnInfo(int arg1, int arg2) {
        for (IVideoPlayerListener mPlayerListener : mPlayerListeners) {
            mPlayerListener.onInfo(arg1, arg2);
        }
    }

    protected void fireOnBuffingStart() {
        mPlayerListeners.forEach(IVideoPlayerListener::onBufferingStart);
    }

    protected void fireOnBuffingEnd() {
        mPlayerListeners.forEach(IVideoPlayerListener::onBufferingEnd);
    }

    protected void fireOnBuffingUpdate(int percent) {
        for (IVideoPlayerListener mPlayerListener : mPlayerListeners) {
            mPlayerListener.onBufferingUpdate(percent);
        }
    }

    private class InnerCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.e(TAG, "surfaceCreated: ");
            mAliyunVodPlayer.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.e(TAG, "surfaceChanged: ");
            mAliyunVodPlayer.surfaceChanged();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.e(TAG, "surfaceDestroyed: ");
        }
    }
}
