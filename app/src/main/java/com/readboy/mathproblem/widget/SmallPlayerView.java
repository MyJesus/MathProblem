package com.readboy.mathproblem.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.application.SubjectType;
import com.readboy.mathproblem.cache.CacheEngine;
import com.readboy.mathproblem.cache.ProjectEntityWrapper;
import com.readboy.mathproblem.dialog.NoNetworkDialog;
import com.readboy.mathproblem.http.auth.AuthCallback;
import com.readboy.mathproblem.http.auth.AuthManager;
import com.readboy.mathproblem.http.response.ProjectEntity;
import com.readboy.mathproblem.http.response.VideoInfoEntity.VideoInfo;
import com.readboy.mathproblem.util.FileUtils;
import com.readboy.mathproblem.util.NetworkUtils;
import com.readboy.mathproblem.util.ToastUtils;
import com.readboy.mathproblem.util.VideoUtils;
import com.readboy.mathproblem.util.WakeUtil;
import com.readboy.mathproblem.video.db.VideoDatabaseInfo;
import com.readboy.mathproblem.video.movie.VideoExtraNames;
import com.readboy.mathproblem.video.proxy.VideoProxy;
import com.readboy.recyclerview.CommonAdapter;
import com.readboy.recyclerview.MultiItemTypeAdapter;
import com.readboy.recyclerview.base.ViewHolder;
import com.readboy.video.ErrorType;
import com.readboy.video.IVideoPlayerListener;
import com.readboy.video.view.VideoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oubin on 2017/10/17.
 *
 * @author oubin
 */

public class SmallPlayerView extends LinearLayout implements View.OnClickListener,
        IVideoPlayerListener {
    private static final String TAG = "oubin_SmallPlayerView";
    private static final int DELAY_SEND_HIDE_MESSAGE = 4_000;  //毫秒

    private Context mContext;

    private VideoView mVideoView;
    private TextView mCurrentVideoNameTv;
    private RecyclerView mVideoRv;
    private CommonAdapter<VideoInfo> mVideoAdapter;
    private View mVideoController;
    private View mFullscreenBtn;
    private GestureView mGestureView;
    private VideoHandler mVideoHandler = new VideoHandler(Looper.getMainLooper());
    private int mCurrentVideoIndex;
    private int mSeekPosition;
    private String mUriPath;
    private boolean isPlayUrl;
    //    private ProgressBar mVideoProgressBar;
    private ImageView mVideoProgressBar;

    private int mGrade;
    private SubjectType mSubjectType;
    private int mProjectPosition;
    /**
     * 更新VideoList时，后台加载视频，加载完成后，暂停播放，即默认不播放。
     */
    private boolean isFirstPlay = true;

    private final List<VideoInfo> mVideoInfoList = new ArrayList<>();

    private NoNetworkDialog mNoNetworkDialog;

    public SmallPlayerView(Context context) {
        this(context, null);
    }

    public SmallPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmallPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.player_view_small, this);
        initView();
    }

    private void initView() {
        mCurrentVideoNameTv = (TextView) findViewById(R.id.small_player_video_name);
        mVideoController = findViewById(R.id.small_player_controller);
        mVideoController.setOnClickListener(this);
        mFullscreenBtn = findViewById(R.id.small_player_full_screen);
        mFullscreenBtn.setOnClickListener(this);
        mGestureView = (GestureView) findViewById(R.id.gesture_view);
        mGestureView.setOnClickListener(this);
//        mVideoProgressBar = (ProgressBar) findViewById(R.id.video_progress_bar);
        mVideoProgressBar = (ImageView) findViewById(R.id.video_progress_bar);
        mVideoProgressBar.setOnClickListener(this);
        mVideoView = (VideoView) findViewById(R.id.video_small_window);
//        mVideoView.setOnPreparedListener(this);
//        mVideoView.setOnErrorListener(this);
//        mVideoView.setOnCompletionListener(this);
//        mVideoView.setOnSeekCompleteListener(this);
        mVideoView.addVideoPlayerListener(this);
        mVideoView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver observer = mVideoView.getViewTreeObserver();
                if (!observer.isAlive()) {
                    return;
                }
                observer.removeOnGlobalLayoutListener(this);
                int showWidth = mVideoView.getMeasuredWidth();
                int showHeight = mVideoView.getMeasuredHeight();
                if (showWidth <= 0 || showHeight <= 0) {
                    ViewGroup.LayoutParams layoutParams = mVideoView.getLayoutParams();
                    showWidth = layoutParams.width;
                    showHeight = layoutParams.height;
                    Log.e(TAG, "initView: new width = " + showWidth + ", new height = " + showHeight);
                }
                Log.e(TAG, "initView: showWidth = " + showWidth + ", height = " + showHeight);
                mVideoView.setLayout(showWidth, showHeight);
            }
        });
        mVideoRv = (RecyclerView) findViewById(R.id.small_player_video_list);
        mVideoRv.setLayoutManager(new LinearLayoutManager(mContext));
        mVideoAdapter = new CommonAdapter<VideoInfo>(mContext, R.layout.item_video_small, mVideoInfoList) {
            @Override
            protected void convert(ViewHolder holder, VideoInfo videoInfo, int position) {
                TextView videoIndex = (TextView) holder.itemView.findViewById(R.id.small_player_video_icon);
                videoIndex.setText(String.valueOf(position + 1));
                TextView videoName = (TextView) holder.itemView.findViewById(R.id.small_player_video_name);
                videoName.setText(FileUtils.getFileNameWithoutExtension(videoInfo.getVideoUri()));
                if (position == mCurrentVideoIndex) {
                    videoIndex.setSelected(true);
                    videoName.setSelected(true);
                } else {
                    videoIndex.setSelected(false);
                    videoName.setSelected(false);
                }
            }
        };
        mVideoAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener<VideoInfo>() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, VideoInfo video, int position) {
                if (mCurrentVideoIndex != position) {
                    mCurrentVideoIndex = position;
                    Log.e(TAG, "onItemClick: current index = " + mCurrentVideoIndex);
                    isFirstPlay = false;
                    initVideoData(0, video);
                    mVideoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, VideoInfo video, int position) {
                return false;
            }
        });
        mVideoRv.setAdapter(mVideoAdapter);
    }

    private void initVideoData(final int seekPosition, VideoInfo videoInfo) {
        Log.e(TAG, "initVideoData: current status = " + mVideoView.getCurrentState());
        stopVideo();
        hidePlayerController();
        showProgressBar();
        if (!isFirstPlay) {
            sendPlayBeforeEvent();
        }
        String fileName = FileUtils.getFileName(videoInfo.getVideoUri());
        mCurrentVideoNameTv.setText(fileName);
        if (VideoUtils.videoIsExist(fileName)) {
            isPlayUrl = false;
            playVideoUri(seekPosition, VideoUtils.getVideoPath(fileName), false);
        } else {
            String url = videoInfo.getUrl();
//            Log.e(TAG, "initVideoData: url = " + url);
            isPlayUrl = true;
            if (!TextUtils.isEmpty(url) && AuthManager.isValid(url)) {
                playVideoUri(seekPosition, url, true);
            } else {
                AuthManager.registerAuth(MathApplication.getInstance(), videoInfo.getVideoUri(), new AuthCallback() {
                    @Override
                    public void onAuth(String url) {
                        if (!TextUtils.isEmpty(url)) {
                            playVideoUri(seekPosition, url, true);
                            videoInfo.setUrl(url);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "onError: throwable = " + throwable, throwable);
                        if (FileUtils.getAvailableSize(getContext().getCacheDir().getPath())
                                < 50 * 1024 * 1024){
                            handleError("存储空间不足！");
                        }else {
                            handleError("无法获取视频资源，请检查网络。");
                        }
                    }
                });
            }
        }
    }

    /**
     * 初始化播放界面
     *
     * @param projectPosition project在当前ProjectWrapper位置
     * @param videoIndex      需要播放的初始位置
     * @param seekPosition    播放进度
     * @param videoList       视频Id
     * @param playVideo       是否播放视频
     */
    public void initVideoList(int projectPosition, int videoIndex, int seekPosition,
                              List<VideoInfo> videoList, boolean playVideo) {
        Log.e(TAG, "initVideoList() called with: projectPosition = " + projectPosition + ", videoIndex = "
                + videoIndex + ", seekPosition = " + seekPosition + ", playVideo = " + playVideo + "");
        isFirstPlay = !playVideo;
        mProjectPosition = projectPosition;
        mVideoInfoList.clear();

        if (videoList == null || videoList.size() == 0) {
//            pauseVideo();
            setVisibility(GONE);
            mVideoAdapter.notifyDataSetChanged();
            return;
        }

        mCurrentVideoIndex = videoIndex;
        Log.e(TAG, "initVideoList: current index = " + mCurrentVideoIndex);
        setVisibility(VISIBLE);
        mVideoInfoList.addAll(videoList);
        mVideoAdapter.notifyDataSetChanged();
        mVideoRv.smoothScrollToPosition(videoIndex);
        initVideoData(seekPosition, videoList.get(videoIndex));
    }

    public void initVideoList(int projectPosition, List<VideoInfo> videoList, boolean playVideo) {
        initVideoList(projectPosition, 0, 0, videoList, playVideo);
    }

    private void playVideoUri(int position, String uri, boolean isUrl) {
        Log.e(TAG, "playVideoUri() called with: position = " + position + ", uri = " + uri + ", isUrl = " + isUrl + "");
        hidePlayerController();
        showProgressBar();
        mUriPath = uri;
        mSeekPosition = position;
        this.isPlayUrl = isUrl;
        Log.e(TAG, "playVideoUri: isPlayUrl = " + isPlayUrl);
        if (isPlayUrl && !checkNetwork()) {
            mVideoView.stopPlayback();
            Log.e(TAG, "playVideoUri: return, is play url and no network. current status = " + mVideoView.getCurrentState());
            return;
        } else {
            enablePlayerController(true);
        }

//        mVideoView.setVideoPath(mUriPath);
        Log.e(TAG, "playVideoUri: firstPlay = " + isFirstPlay);
        if (isFirstPlay) {
            mVideoView.loadVideoUri(mUriPath, VideoView.DEFAULT_CACHE_PATH);
            isFirstPlay = false;
        } else {
            mVideoView.setVideoURI(mUriPath, VideoDatabaseInfo.DEFAULT_CACHE_PATH, true);
        }
        mVideoView.seekTo(position);
//        mVideoController.setChecked(true);
//        updateViewCausePlaying();
    }

    private void playVideo() {
        int state = mVideoView.getCurrentState();
        Log.e(TAG, "playVideo: videoState = state = " + state);
        if (state == VideoView.STATE_IDLE || state == VideoView.STATE_ERROR
                || state == VideoView.STATE_STOPPED
                || state == VideoView.STATE_PLAYBACK_COMPLETED) {
            isFirstPlay = false;
            Log.e(TAG, "playVideo: mCurrentVideoIndex = " + mCurrentVideoIndex + ", seek = " + mSeekPosition);
            playVideo(mCurrentVideoIndex, mSeekPosition);
        } else if (!mVideoView.isPlaying()) {
            sendPlayBeforeEvent();
            updateViewCausePlaying();
            mVideoView.start();
//            mVideoController.setChecked(true);
            WakeUtil.acquireCpuWakeLock(mContext);
        }
    }

    public void playVideo(int videoIndex, int seekPosition) {
//        Log.e(TAG, "playVideo: status = " + mVideoView.getCurrentState());
        Log.e(TAG, "playVideo() called with: videoIndex = " + videoIndex + ", seekPosition = " + seekPosition + "");
        mCurrentVideoIndex = videoIndex;
        initVideoData(seekPosition, mVideoInfoList.get(videoIndex));
        mVideoAdapter.notifyDataSetChanged();
    }

    public void pauseVideo() {
        mSeekPosition = mVideoView.getCurrentPosition();
        Log.e(TAG, "pauseVideo: seek = " + mSeekPosition);
        updateViewCausePause();
        hideProgressBar();
        mVideoView.pause();
        WakeUtil.releaseCpuLock();
    }

    public void stopVideo() {
//        if (mVideoView.isPlaying()) {
        mVideoView.stopPlayback();
//        }
    }

    private void resetData() {
        mSeekPosition = 0;
        mUriPath = null;
    }

    private void updateViewCausePlaying() {
        Log.e(TAG, "updateViewCausePlaying: ");
        mVideoController.setSelected(true);
        hidePlayerControllerDelayed();
    }

    private void updateViewCausePause() {
        Log.e(TAG, "updateViewCausePause: ");
        mVideoController.setSelected(false);
        mVideoHandler.removeMessages(VideoHandler.MESSAGE_HIDE_CONTROLLER);
        showPlayerController();
    }

    /**
     * @return true 网络可用，false 网络不可用。
     */
    private boolean checkNetwork() {
        if (!NetworkUtils.isConnected(mContext)) {
            showNoNetworkDialog();
            mVideoProgressBar.setVisibility(GONE);
            updateViewCausePause();
            enablePlayerController(false);
            return false;
        } else {
            enablePlayerController(true);
        }
        return true;
    }

    private void hidePlayerControllerDelayed() {
        Log.e(TAG, "hidePlayerControllerDelayed: ");
        mVideoHandler.removeMessages(VideoHandler.MESSAGE_HIDE_CONTROLLER);
        mVideoHandler.sendEmptyMessageDelayed(VideoHandler.MESSAGE_HIDE_CONTROLLER, DELAY_SEND_HIDE_MESSAGE);
    }

    private void showProgressBar() {
        mVideoProgressBar.setVisibility(VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.progress_bar);
        animation.setInterpolator(new LinearInterpolator());
        mVideoProgressBar.startAnimation(animation);
    }

    private void hideProgressBar() {
        mVideoProgressBar.clearAnimation();
        mVideoProgressBar.setVisibility(GONE);
    }

    private void hidePlayerController() {
        if (mVideoController.getVisibility() != View.GONE) {
            mVideoController.setVisibility(View.GONE);
            mFullscreenBtn.setVisibility(View.GONE);
        }
    }

    private void showPlayerController() {
        Log.e(TAG, "showPlayerController: selected = " + mVideoController.isSelected());
        mVideoHandler.removeMessages(VideoHandler.MESSAGE_HIDE_CONTROLLER);
        if (mVideoController.isSelected()) {
            hidePlayerControllerDelayed();
        }
        mVideoController.setVisibility(View.VISIBLE);
        mFullscreenBtn.setVisibility(View.VISIBLE);
    }

    private void showNoNetworkDialog() {
        if (mNoNetworkDialog == null) {
            mNoNetworkDialog = new NoNetworkDialog(mContext);
        }
        mNoNetworkDialog.show();
    }

    public boolean noNetworkDialogIsShowing() {
        return mNoNetworkDialog != null && mNoNetworkDialog.isShowing();
    }

    public void dismissNoNetworkDialog() {
        if (mNoNetworkDialog != null && mNoNetworkDialog.isShowing()) {
            mNoNetworkDialog.dismiss();
        }
    }

    //TODO: 视频播放出错，禁用播放控制。
    public void enablePlayerController(boolean enable) {
        Log.e(TAG, "enablePlayerController() called with: enable = " + enable + "");
//        if (!enable && !isPlayUrl) {
//            return;
//        }
//        mVideoController.setEnabled(enable);
//        mFullscreenBtn.setEnabled(enable);
//        updateViewCausePause();
        if (!enable) {
            showPlayerController();
        }
        mVideoController.setActivated(enable);
        mFullscreenBtn.setActivated(enable);
        Log.e(TAG, "enablePlayerController: current status = " + mVideoView.getCurrentState());
    }

    private void gotoMovieActivity() {
        CacheEngine.setCurrentIndex(mProjectPosition);
        pauseVideo();
        int seekPosition = mVideoView.getCurrentPosition();
        Log.e(TAG, "gotoMovieActivity: index = " + mCurrentVideoIndex + ", seekPosition = " + seekPosition);
//        VideoProxy.playWithCurrentProject(mCurrentVideoIndex, seekPosition, mContext);
        if (mSubjectType != null) {
            ProjectEntityWrapper wrapper = CacheEngine.getProject(mSubjectType, mGrade);
            if (wrapper != null) {
                VideoProxy.playWithProject(mCurrentVideoIndex, seekPosition, VideoExtraNames.TYPE_SET_RESULT,
                        mContext, wrapper.getProjectList().get(mProjectPosition));
                return;
            }
        }
        Log.e(TAG, "gotoMovieActivity: can not get the project from RAM, mGrade = " + mGrade
                + ", mSubjectType = " + mSubjectType);
        VideoProxy.playWithCurrentProject(mCurrentVideoIndex, seekPosition, mContext);
    }

    public int getPlayState() {
        return mVideoView.getCurrentState();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.small_player_controller:
                Log.e(TAG, "onClick: controller, isSelected = " + mVideoController.isSelected()
                        + ", activated = " + mVideoController.isActivated() + ", status = " + mVideoView.getCurrentState());
                if (!mVideoController.isActivated()) {
                    ToastUtils.showShort(mContext, "请连接网络");
                    return;
                }
                if (!mVideoController.isSelected()) {
                    playVideo();
                } else {
                    pauseVideo();
                }
                break;
            case R.id.small_player_full_screen:
                if (mFullscreenBtn.isActivated()) {
                    gotoMovieActivity();
                } else {
                    ToastUtils.showShort(mContext, "请连接网络");
                }
                break;
            case R.id.gesture_view:
                if (mVideoProgressBar.getVisibility() == VISIBLE) {
                    return;
                }
                if (mVideoController.getVisibility() != View.VISIBLE) {
                    showPlayerController();
                } else if (isPlaying()) {
                    hidePlayerController();
                }
                break;
            default:
                Log.e(TAG, "onClick: default = " + v.getId());
                break;
        }
    }

    public void setFirstPlay(boolean isFirstPlay) {
        this.isFirstPlay = isFirstPlay;
    }

    private boolean isPlaying() {
        return mVideoView.isPlaying();
//        return mVideoController.isChecked();
    }

    public void smoothScrollToPosition(int videoPosition) {
        mVideoRv.smoothScrollToPosition(videoPosition);
    }

    public boolean hasData() {
        return mVideoInfoList != null && mVideoInfoList.size() > 0;
    }

    public void setCurrentProject(int grade, SubjectType type) {
        this.mGrade = grade;
        this.mSubjectType = type;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == GONE) {
            Log.e(TAG, "setVisibility: video view gone.");
            //解决再例题讲解界面有残影问题。
            mVideoView.setVisibility(GONE);
//            mVideoView.setVisibility(INVISIBLE);
        } else if (visibility == VISIBLE) {
            Log.e(TAG, "setVisibility: video view visible.");
            mVideoView.setVisibility(VISIBLE);
        } else {
            mVideoView.setVisibility(visibility);
        }
    }

    @Override
    public void onInit() {
        Log.e(TAG, "onInit: ");
        showProgressBar();
        hidePlayerController();
    }

    @Override
    public void onPrepared() {
        Log.e(TAG, "onPrepared: ");
        sendPlayBeforeEvent();
    }

    @Override
    public void onRelease() {
        Log.e(TAG, "onRelease: ");
        hideProgressBar();
    }

    @Override
    public void onPlaying() {
        Log.e(TAG, "onPlaying: isFirstPlay = " + isFirstPlay);
        updateViewCausePlaying();
        hideProgressBar();
        showPlayerController();
//        if (isFirstPlay) {
//            pauseVideo();
//            isFirstPlay = false;
//        }
    }

    @Override
    public void onPaused() {
        Log.e(TAG, "onPaused: ");
        isFirstPlay = false;
        hideProgressBar();
        updateViewCausePause();
        showPlayerController();
    }

    @Override
    public void onStopped() {
        Log.e(TAG, "onStopped: ");
        hideProgressBar();
        updateViewCausePause();
        resetData();
    }

    @Override
    public void onCompletion() {
        Log.e(TAG, "onCompletion: ");
        hideProgressBar();
        showPlayerController();
        updateViewCausePause();
        resetData();
    }

    @Override
    public void onError(String error, ErrorType errorType) {
        Log.e(TAG, "onError() called with: error = " + error);
        handleError(error);
    }

    private void handleError(String error) {
        hideProgressBar();
        updateViewCausePause();
        if (isPlayUrl) {
            if (checkNetwork()) {
                ToastUtils.showLong(mContext, "视频播放出错：" + error);
            }
        } else {
            ToastUtils.showLong(mContext, "未知错误:" + error);
            enablePlayerController(true);
        }
        mVideoProgressBar.setVisibility(GONE);
    }

    @Override
    public void onBufferingUpdate(int percent) {
//        Log.e(TAG, "onBufferingUpdate: percent = " + percent);
    }

    @Override
    public void onBufferingStart() {
        Log.e(TAG, "onBufferingStart: ");
        hidePlayerController();
        showProgressBar();
    }

    @Override
    public void onBufferingEnd() {
        Log.e(TAG, "onBufferingEnd: ");
        hideProgressBar();
    }

    /**
     * 释放资源， Activity.onDestroy调用
     */
    public void release() {
        Log.e(TAG, "release: ");
        mVideoView.stopPlayback();
        mVideoView.removeVideoPlayerListener(this);
        mVideoView.release();
    }

    private class VideoHandler extends Handler {
        private static final int MESSAGE_HIDE_CONTROLLER = 2;

        VideoHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_HIDE_CONTROLLER:
                    hidePlayerController();
                    break;
                default:
                    Log.e(TAG, "handleMessage: msg = " + msg.what);
                    break;
            }
        }
    }

    private OnPlayBeforeListener mPlayListener;

    private void sendPlayBeforeEvent() {
        if (mPlayListener != null) {
            mPlayListener.onPlayBefore();
        }
    }

    public void setOnPlayBeforeListener(OnPlayBeforeListener listener) {
        this.mPlayListener = listener;
    }

    public interface OnPlayBeforeListener {
        void onPlayBefore();
    }

}
