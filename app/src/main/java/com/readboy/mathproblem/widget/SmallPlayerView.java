package com.readboy.mathproblem.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliyun.vodplayer.media.AliyunLocalSource;
import com.aliyun.vodplayer.media.AliyunVidSts;
import com.aliyun.vodplayer.media.AliyunVodPlayer;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.aliyun.vodplayer.media.IAliyunVodPlayer.PlayerState;
import com.readboy.aliyunplayerlib.helper.VidStsHelper;
import com.readboy.aliyunplayerlib.utils.AliLogUtil;
import com.readboy.mathproblem.R;
import com.readboy.mathproblem.application.Constants;
import com.readboy.mathproblem.application.SubjectType;
import com.readboy.mathproblem.cache.CacheEngine;
import com.readboy.mathproblem.cache.ProjectEntityWrapper;
import com.readboy.mathproblem.dialog.NoNetworkDialog;
import com.readboy.mathproblem.http.response.VideoInfoEntity.VideoInfo;
import com.readboy.mathproblem.util.NetworkUtils;
import com.readboy.mathproblem.util.ToastUtils;
import com.readboy.mathproblem.util.WakeUtil;
import com.readboy.mathproblem.video.movie.VideoExtraNames;
import com.readboy.mathproblem.video.proxy.VideoProxy;
import com.readboy.mathproblem.video.resource.IVideoResource;
import com.readboy.mathproblem.video.resource.VidVideoResource;
import com.readboy.recyclerview.CommonAdapter;
import com.readboy.recyclerview.MultiItemTypeAdapter;
import com.readboy.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oubin on 2017/10/17.
 *
 * @author oubin
 */

public class SmallPlayerView extends LinearLayout implements View.OnClickListener,
        IAliyunVodPlayer.OnSeekCompleteListener,
        IAliyunVodPlayer.OnPreparedListener,
        IAliyunVodPlayer.OnFirstFrameStartListener,
        IAliyunVodPlayer.OnLoadingListener,
        IAliyunVodPlayer.OnStoppedListener,
        IAliyunVodPlayer.OnCompletionListener,
        IAliyunVodPlayer.OnTimeExpiredErrorListener,
        IAliyunVodPlayer.OnErrorListener,
        IAliyunVodPlayer.OnInfoListener {
    private static final String TAG = "oubin_SmallPlayerView";
    //毫秒
    private static final int DELAY_SEND_HIDE_MESSAGE = 4_000;
    private static final int MAX_RETRY_TIME = 2;

    private Context mContext;

    private AliyunVodPlayer mAliyunVodPlayer;
    private AliyunVidSts mVidSts;
    private VidStsHelper mVidStsHelper = null;

    private SurfaceView mSurfaceView;
    private SurfaceHolder.Callback mCallback;
    private TextView mCurrentVideoNameTv;
    private RecyclerView mVideoRv;
    private CommonAdapter<VideoInfo> mVideoAdapter;
    private View mVideoController;
    private View mFullscreenBtn;
    private GestureView mGestureView;
    private VideoHandler mVideoHandler = new VideoHandler(Looper.getMainLooper());
    private int mCurrentVideoIndex;
    private long mSeekPosition;
    private ImageView mVideoProgressBar;

    private int mGrade;
    private SubjectType mSubjectType;
    private int mProjectPosition;
    /**
     * 更新VideoList时，后台加载视频，加载完成后，暂停播放，即默认不播放。
     */
    private boolean isFirstPlay = true;
    private boolean isPaused = false;
    private boolean isPreparing = false;
    private int retryTimes = 0;
    private PlayerState mTargetState = PlayerState.Idle;

    private final List<VideoInfo> mVideoInfoList = new ArrayList<>();
    private IVideoResource mCurrentVideoResource;

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
        initVodPlayer();
    }

    private void initView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.video_small_window);
        mCallback = new InnerCallback();
        mSurfaceView.getHolder().addCallback(mCallback);
        mCurrentVideoNameTv = (TextView) findViewById(R.id.small_player_video_name);
        mVideoController = findViewById(R.id.small_player_controller);
        mVideoController.setOnClickListener(this);
        mVideoController.setActivated(true);
        mFullscreenBtn = findViewById(R.id.small_player_full_screen);
        mFullscreenBtn.setOnClickListener(this);
        mFullscreenBtn.setActivated(true);
        mGestureView = (GestureView) findViewById(R.id.gesture_view);
        mGestureView.setOnClickListener(this);
//        mVideoProgressBar = (ProgressBar) findViewById(R.id.video_progress_bar);
        mVideoProgressBar = (ImageView) findViewById(R.id.video_progress_bar);
        mVideoProgressBar.setOnClickListener(this);

        mVideoRv = (RecyclerView) findViewById(R.id.small_player_video_list);
        mVideoRv.setLayoutManager(new LinearLayoutManager(mContext));
        mVideoAdapter = new CommonAdapter<VideoInfo>(mContext, R.layout.item_video_small, mVideoInfoList) {
            @Override
            protected void convert(ViewHolder holder, VideoInfo videoInfo, int position) {
                TextView videoIndex = (TextView) holder.itemView.findViewById(R.id.small_player_video_icon);
                videoIndex.setText(String.valueOf(position + 1));
                TextView videoName = (TextView) holder.itemView.findViewById(R.id.small_player_video_name);
//                videoName.setText(FileUtils.getFileNameWithoutExtension(videoInfo.getVideoUri()));
                videoName.setText(videoInfo.getName());
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
                    stopVideo();
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

    private void initVodPlayer() {
        mAliyunVodPlayer = new AliyunVodPlayer(getContext());
        String sdDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MathProblem/cache/";
        mAliyunVodPlayer.setPlayingCache(true, sdDir, 60 * 60, 300);
        mAliyunVodPlayer.setCirclePlay(false);

        mAliyunVodPlayer.setOnPreparedListener(this);
        mAliyunVodPlayer.setOnFirstFrameStartListener(this);
        mAliyunVodPlayer.setOnErrorListener(this);
        mAliyunVodPlayer.setOnCompletionListener(this);
        mAliyunVodPlayer.setOnSeekCompleteListener(this);
        mAliyunVodPlayer.setOnStoppedListner(this);
        mAliyunVodPlayer.setOnLoadingListener(this);
//        mAliyunVodPlayer.enableNativeLog();

        mVidStsHelper = new VidStsHelper();
    }

    private void initVideoData(final long seekPosition, VideoInfo videoInfo) {
        initVideoData(seekPosition, videoInfo, true);
    }

    private void initVideoData(final long seekPosition, VideoInfo videoInfo, boolean playVideo) {
        this.mSeekPosition = seekPosition;
        //清除画面内容。
        stopVideo();
//        showProgressBar();
//        if (!isFirstPlay) {
//            sendPlayBeforeEvent();
//        }
//        String fileName = FileUtils.getFileName(videoInfo.getVideoUri());
        mCurrentVideoNameTv.setText(videoInfo.getName());
        mCurrentVideoResource = new VidVideoResource(videoInfo);

        if (playVideo) {
            playVideo(mCurrentVideoResource);
        } else {
            updateViewCausePause();
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
                + videoIndex + ", seekPosition = " + seekPosition + ", playOrResumeVideo = " + playVideo + "");
        isFirstPlay = !playVideo;
        mProjectPosition = projectPosition;
        mVideoInfoList.clear();

        if (videoList == null || videoList.size() == 0) {
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
        initVideoData(seekPosition, videoList.get(videoIndex), playVideo);
    }

    public void initVideoList(int projectPosition, List<VideoInfo> videoList, boolean playVideo) {
        initVideoList(projectPosition, 0, 0, videoList, playVideo);
    }

    private void playVideo(IVideoResource resource) {
        sendPlayBeforeEvent();
        mTargetState = PlayerState.Started;
        onInit();
        if (resource.isDownloaded()) {
            playWithLocalPath(Constants.getVideoPath(resource.getVideoName()));
        } else {
            playWithVid(resource.getVideoUri().getAuthority());
        }
    }

    private void playWithLocalPath(String path) {
        setPlayerControllerEnabled(true);
        AliyunLocalSource.AliyunLocalSourceBuilder asb = new AliyunLocalSource.AliyunLocalSourceBuilder();
        asb.setSource(path);
        AliyunLocalSource localSource = asb.build();
        mAliyunVodPlayer.prepareAsync(localSource);
    }

    /**
     * vid在线点播
     *
     * @param vid vid
     */
    public void playWithVid(String vid) {
        if (!checkNetwork()) {
            return;
        }
        Log.e(TAG, "playWithVid() called with: vid = " + vid + "");
        if (mAliyunVodPlayer.getPlayerState() != IAliyunVodPlayer.PlayerState.Idle) {
            mAliyunVodPlayer.stop();
        }
        if (mVidSts != null) {
            mVidSts.setVid(vid);
        }
        prepareAsync(true);
    }

    private void prepareAsync(boolean allowMobilePlay) {
        if (mVidSts == null) {
            getVidsts();
        } else {
            if (!allowMobilePlay && NetworkUtils.is4G(getContext())) {
                Log.e(TAG, "prepareAsync: do nothing.");
            } else {
                Log.e(TAG, "prepareAsync: ");
                mAliyunVodPlayer.prepareAsync(mVidSts);
            }
        }
    }

    private void getVidsts() {
        AliLogUtil.v(TAG, "---getVidsts---");
        showProgressBar();
        if (mVidStsHelper.isGettingVidsts()) {
            return;
        }
        mVidStsHelper.getVidSts(new VidStsHelper.OnStsResultListener() {
            @Override
            public void onSuccess(String akid, String akSecret, String token) {
                Log.e(TAG, "onSuccess() called with: akid = " + akid + ", akSecret = " + akSecret + ", token = " + token + "");
                String vid = mCurrentVideoResource.getVideoUri().getAuthority();
//                Log.e(TAG, "onSuccess: resource =  " + mCurrentVideoResource.getVideoUri().toString());
                Log.e(TAG, "onSuccess: vid = " + vid);
                mVidSts = new AliyunVidSts();
                mVidSts.setVid(vid);
                mVidSts.setAcId(akid);
                mVidSts.setAkSceret(akSecret);
                mVidSts.setSecurityToken(token);

                if (mTargetState == PlayerState.Started) {
                    prepareAsync(true);
                }
            }

            @Override
            public void onFail(int errno) {
                Log.e(TAG, "onFail: errno = " + errno);
                mVidSts = null;
                if (checkNetwork()) {
                    if (errno == VidStsHelper.ERRNO_SIGNATURE_INVALID) {
                        ToastUtils.show(getResources().getString(com.readboy.aliyunplayerlib.R.string.player_load_status_view_text_error_signature_invalid));
                    } else if (errno == VidStsHelper.ERRNO_DEVICE_UNAUTH) {
                        ToastUtils.show(getResources().getString(com.readboy.aliyunplayerlib.R.string.player_load_status_view_text_error_device_unauth));
                    } else {
                        ToastUtils.show("未知错误");
                    }
                }
            }
        });
    }

    private void playOrResumeVideo() {
        PlayerState state = mAliyunVodPlayer.getPlayerState();
        Log.e(TAG, "playOrResumeVideo: state = " + state + ", isPlaying = " + isPlaying());
        if (isStopState(state)) {
            isFirstPlay = false;
            Log.e(TAG, "playOrResumeVideo: mCurrentVideoIndex = " + mCurrentVideoIndex + ", seek = " + mSeekPosition);
            playVideo(mCurrentVideoIndex, mSeekPosition);
        } else if (!mAliyunVodPlayer.isPlaying()) {
            resumePlay();
        } else if (state == PlayerState.Paused
                || state == PlayerState.Prepared) {
            resumePlay();
        } else if (state == PlayerState.Started) {
            updateViewCausePlaying();
        }
    }

    private void resumePlay() {
        Log.e(TAG, "resumePlay: ");
        sendPlayBeforeEvent();
        mTargetState = PlayerState.Started;
//        sendPlayBeforeEvent();
        updateViewCausePlaying();
        mAliyunVodPlayer.resume();
        WakeUtil.acquireCpuWakeLock(mContext);
    }

    public void playVideo(int videoIndex, long seekPosition) {
//        Log.e(TAG, "playOrResumeVideo: status = " + mVideoView.getCurrentState());
        Log.e(TAG, "playOrResumeVideo() called with: videoIndex = " + videoIndex + ", seekPosition = " + seekPosition + "");
        mCurrentVideoIndex = videoIndex;
        initVideoData(seekPosition, mVideoInfoList.get(videoIndex));
        mVideoAdapter.notifyDataSetChanged();
    }

    public void pauseVideo() {
        mTargetState = PlayerState.Paused;
        if (!isPreparing) {
//            mSeekPosition = mAliyunVodPlayer.getCurrentPosition();
        }
        Log.e(TAG, "pauseVideo: seek = " + mSeekPosition + ", state = " + mAliyunVodPlayer.getPlayerState());
        Log.e(TAG, "pauseVideo: isPlaying() = " + mAliyunVodPlayer.isPlaying());
        mAliyunVodPlayer.pause();
        WakeUtil.releaseCpuLock();
        onPaused();
    }

    public void stopVideo() {
        mTargetState = PlayerState.Stopped;
        Log.e(TAG, "stopVideo: isPlaying = " + isPlaying() + ", state = " + mAliyunVodPlayer.getPlayerState());
//        if (mAliyunVodPlayer.isPlaying()) {
//        mAliyunVodPlayer.stopPlayback();
//        }
        mAliyunVodPlayer.stop();
        onPaused();
    }

    public void clearSurfaceView() {
        Canvas canvas = null;
        try {
            canvas = mSurfaceView.getHolder().lockCanvas(null);
            canvas.drawColor(Color.BLACK);
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (canvas != null) {
                mSurfaceView.getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    private void resetData() {
        mSeekPosition = 0;
        mTargetState = PlayerState.Idle;
//        mCurrentVideoResource = null;
    }

    private void updateViewCausePlaying() {
        Log.e(TAG, "updateViewCausePlaying: ");
        mVideoController.setSelected(true);
        hidePlayerControllerDelayed();
        setKeepScreenOn(true);
    }

    private void updateViewCausePause() {
        Log.e(TAG, "updateViewCausePause: ");
        hideProgressBar();
        mVideoController.setSelected(false);
        mVideoHandler.removeMessages(VideoHandler.MESSAGE_HIDE_CONTROLLER);
        showPlayerController();
        setKeepScreenOn(false);
    }

    /**
     * @return true 网络可用，false 网络不可用。
     */
    private boolean checkNetwork() {
        if (!NetworkUtils.isConnected(mContext)) {
            showNoNetworkDialog();
            hideProgressBar();
            isPreparing = false;
            setPlayerControllerEnabled(false);
            return false;
        } else {
            setPlayerControllerEnabled(true);
        }
        return true;
    }

    private void hidePlayerControllerDelayed() {
        Log.e(TAG, "hidePlayerControllerDelayed: ");
        mVideoHandler.removeMessages(VideoHandler.MESSAGE_HIDE_CONTROLLER);
        mVideoHandler.sendEmptyMessageDelayed(VideoHandler.MESSAGE_HIDE_CONTROLLER, DELAY_SEND_HIDE_MESSAGE);
    }

    private void showProgressBar() {
        Log.e(TAG, "showProgressBar: ");
        hidePlayerController();
        mVideoProgressBar.setVisibility(VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.progress_bar);
        animation.setInterpolator(new LinearInterpolator());
        mVideoProgressBar.startAnimation(animation);
    }

    private void hideProgressBar() {
        Log.e(TAG, "hideProgressBar: ");
        mVideoProgressBar.clearAnimation();
        mVideoProgressBar.setVisibility(GONE);
    }

    private void hidePlayerController() {
//        Log.e(TAG, "hidePlayerController: " + mVideoController.getVisibility());
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

    /**
     * @param enable false 代表不可用状态
     */
    public void setPlayerControllerEnabled(boolean enable) {
        Log.e(TAG, "setPlayerControllerEnabled() called with: enable = " + enable + "");
//        mVideoController.setEnabled(enable);
//        mFullscreenBtn.setEnabled(enable);
        if (!enable) {
            showPlayerController();
        }
        mVideoController.setActivated(enable);
        mFullscreenBtn.setActivated(enable);
    }

    private void gotoMovieActivity() {
        CacheEngine.setCurrentIndex(mProjectPosition);
        pauseVideo();
        long seekPosition = mAliyunVodPlayer.getCurrentPosition();
        seekPosition = Math.max(0, seekPosition - 3000);
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
        return -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.small_player_controller:
                if (!mVideoController.isActivated()) {
                    ToastUtils.showShort(mContext, "请连接网络");
                    return;
                }
                if (!mVideoController.isSelected()) {
                    playOrResumeVideo();
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

    public boolean isPlaying() {
        return mAliyunVodPlayer.isPlaying();
//        return mVideoController.isChecked();
    }

    public boolean isPreparing() {
        return isPreparing;
    }

    private boolean needNewwork() {
        return !mCurrentVideoResource.isDownloaded();
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
            mSurfaceView.setVisibility(GONE);
        } else if (visibility == VISIBLE) {
            Log.e(TAG, "setVisibility: video view visible.");
            mSurfaceView.setVisibility(VISIBLE);
        } else {
            mSurfaceView.setVisibility(visibility);

        }
    }

    private void onInit() {
        Log.e(TAG, "onInit: ");
        isPreparing = true;
        retryTimes = 0;
        showProgressBar();
        hidePlayerController();
    }

    private void onPlaying() {
        Log.e(TAG, "onPlaying: isFirstPlay = " + isFirstPlay);
        updateViewCausePlaying();
        hideProgressBar();
        showPlayerController();
        hidePlayerControllerDelayed();
//        if (isFirstPlay) {
//            pauseVideo();
//            isFirstPlay = false;
//        }
    }

    private void onPaused() {
        Log.e(TAG, "onPaused: ");
        isFirstPlay = false;
        updateViewCausePause();
        showPlayerController();
    }

    private void handleError(String error) {
        updateViewCausePause();
        if (!checkNetwork()) {
            ToastUtils.showLong(mContext, "视频播放出错：" + error);
        } else {
            ToastUtils.showLong(mContext, "未知错误:" + error);
            setPlayerControllerEnabled(true);
        }
    }

    private boolean isStopState(PlayerState state) {
        return state == PlayerState.Error
                || state == PlayerState.Completed
                || state == PlayerState.Stopped
                || state == PlayerState.Idle;
    }

    /**
     * 释放资源， Activity.onDestroy调用
     */
    public void release() {
        Log.e(TAG, "release: ");
        mSurfaceView.getHolder().removeCallback(mCallback);
        if (mAliyunVodPlayer != null) {
            mAliyunVodPlayer.stop();
            mAliyunVodPlayer.release();
            mAliyunVodPlayer = null;
        }
    }

    @Override
    public void onPrepared() {
        Log.e(TAG, "onPrepared: seekPosition = " + mSeekPosition
                + ", state = " + mAliyunVodPlayer.getPlayerState()
                + ", mTargetState = " + mTargetState);
        if (mSeekPosition > 0) {
            mAliyunVodPlayer.seekTo((int) mSeekPosition);
        } else {
        }
        if (mTargetState == PlayerState.Started) {
            mAliyunVodPlayer.start();
        } else {
            updateViewCausePause();
        }
        isPreparing = false;
//        }
    }

    @Override
    public void onStopped() {
        Log.e(TAG, "onStopped: state = " + mAliyunVodPlayer.getPlayerState() + ", isPreparing = " + isPreparing);
        if (!isPreparing) {
            resetData();
            updateViewCausePause();
        }
    }

    @Override
    public void onCompletion() {
        Log.e(TAG, "onCompletion: ");
        updateViewCausePause();
        resetData();
    }

    @Override
    public void onError(int i, int i1, String s) {
        Log.e(TAG, "onError() called with: i = " + i + ", i1 = " + i1 + ", s = " + s + "");
        //防止是鉴权过期问题。
        mVidSts = null;
        if (i == 4502 || i == 4002) {
            //请求saas服务器错误，可能是AliyunVidSts参数无效
            //需要重新请求，刷新mVidSts，对用户不可见
            if (retryTimes < MAX_RETRY_TIME) {
                retryTimes ++;
                prepareAsync(true);
            }
        }
        handleError(s);
        isPreparing = false;
    }

    @Override
    public void onFirstFrameStart() {
        Log.e(TAG, "onFirstFrameStart: state = " + mAliyunVodPlayer.getPlayerState()
                + ", mTargetState = " + mTargetState);
        if (mTargetState == PlayerState.Started) {
            onPlaying();
        }
    }

    @Override
    public void onLoadStart() {
        Log.e(TAG, "onLoadStart: ");
        showProgressBar();
        isPreparing = true;
    }

    @Override
    public void onLoadEnd() {
        Log.e(TAG, "onLoadEnd: ");
        onPlaying();
        isPreparing = false;
    }

    @Override
    public void onLoadProgress(int i) {
//        Log.e(TAG, "onLoadProgress() called with: i = " + i + "");

    }

    @Override
    public void onSeekComplete() {
        Log.e(TAG, "onSeekComplete: ");
//        updateViewCausePause();
    }

    @Override
    public void onTimeExpiredError() {
        Log.e(TAG, "onTimeExpiredError: ");
//        updateViewCausePause();
    }

    @Override
    public void onInfo(int i, int i1) {
        Log.e(TAG, "onInfo() called with: i = " + i + ", i1 = " + i1 + "");
    }

    private class InnerCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.e(TAG, "surfaceCreated: ");
            mAliyunVodPlayer.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.e(TAG, "surfaceChanged() called with: holder = " + holder + ", format = " + format + ", width = " + width + ", height = " + height + "");
            mAliyunVodPlayer.surfaceChanged();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.e(TAG, "surfaceDestroyed: ");
        }
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
