package com.readboy.aliyunplayerlib.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aliyun.vodplayer.media.AliyunLocalSource;
import com.aliyun.vodplayer.media.AliyunVidSts;
import com.aliyun.vodplayer.media.AliyunVodPlayer;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.aliyun.vodplayerview.utils.NetWatchdog;
import com.readboy.aliyunplayerlib.R;
import com.readboy.aliyunplayerlib.helper.VidStsHelper;
import com.readboy.aliyunplayerlib.utils.AliLogUtil;
import com.readboy.aliyunplayerlib.utils.StringUtil;

/**
 * 播放器控件
 * Created by ldw on 2018/3/21.
 */

public class AliPlayerView extends RelativeLayout implements View.OnClickListener,
        PlayerLoadStatusView.OnBtnClickListener,
        SeekBar.OnSeekBarChangeListener,
        IAliyunVodPlayer.OnSeekCompleteListener,
        IAliyunVodPlayer.OnPreparedListener,
        IAliyunVodPlayer.OnFirstFrameStartListener,
        IAliyunVodPlayer.OnLoadingListener,
        IAliyunVodPlayer.OnCompletionListener,
        IAliyunVodPlayer.OnChangeQualityListener,
        IAliyunVodPlayer.OnTimeExpiredErrorListener,
        IAliyunVodPlayer.OnErrorListener,
        NetWatchdog.NetChangeListener {
    private static final String TAG = "AliPlayerView";

    //版本号
    private static final String VERSION = "V1.0.180416001";

    //常量
    private static final int MSG_HEART = 1;
    private static final int MSG_HIDE_CONTROL_VIEW = 2;

    //控件
    private PlayerTopViewBase mTopView = null;
    private PlayerBottomViewBase mBottomView = null;
    private PlayerLoadStatusView mLoadStatusView;
    private TextView mLoadProgressTextView;
    private SurfaceView mPlayerSurfaceView;

    //播放器
    private AliyunVodPlayer mAliyunVodPlayer = null;

    //播放器回调
    private OnPlayCompleteListener mOnPlayCompleteListener = null;//播放完成回调

    //预处理点击监听器
    private OnPreprocessBtnListener mOnPreprocessBtnListener = null;

    //播放方式一：视频vidsts播放方式
    private String mVid = null;
    private AliyunVidSts mVidSts = null;

    //播放方式二：视频本地播放方式
    private String mlocalPath = null;

    //播放方式三：在线url播放方式
    private String mUrl = null;

    //获取vidsts临时授权帮助类
    private VidStsHelper mVidStsHelper = null;

    //网络监听切换提示相关
    private NetWatchdog mNetWatchdog;

    //播放时间记录，只记录处于播放状态的时间，单位为秒
    private int mPlayTimeSec = 0;

    //当前播放位置
    private long mCurrentPosition = 0;

    //辅助标志
    private boolean mIsPaused = false;//是否pause了
    private boolean mIsSeekBarTouching = false;//是否正在对SeekBar进行拖动
    private boolean mIsSeekComplete = true;//是否seek完成
    private IAliyunVodPlayer.PlayerState mPlayerState;//pause时播放状态，在onResume判断恢复
    private boolean mIsPauseWhileGetVidsts = false;


    public AliPlayerView(Context context) {
        super(context);
        init();
    }

    public AliPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AliPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        Log.v(TAG, "---init--- version = " + VERSION);

        initViews();
        initPlayer();

        mVidStsHelper = new VidStsHelper();

        mNetWatchdog = new NetWatchdog(getContext());
        mNetWatchdog.setNetChangeListener(this);
        mNetWatchdog.startWatch();

    }

    private void initViews(){
        AliLogUtil.v(TAG, "---initViews---");
        LayoutInflater.from(this.getContext()).inflate(R.layout.ali_player_view, this, true);
        mLoadStatusView = findViewById(R.id.video_player_loading);
        mLoadProgressTextView = findViewById(R.id.video_player_load_progress);
        mPlayerSurfaceView = findViewById(R.id.video_player_surface);

        mPlayerSurfaceView.setOnClickListener(this);
        mPlayerSurfaceView.setOnClickListener(this);
        mLoadStatusView.setOnBtnClickListener(this);

        mPlayerSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                AliLogUtil.v(TAG, "---surfaceCreated---");
                mAliyunVodPlayer.setDisplay(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                AliLogUtil.v(TAG, "---surfaceChanged---");
                mAliyunVodPlayer.surfaceChanged();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                AliLogUtil.v(TAG, "---surfaceDestroyed---");
            }
        });
    }

    private void initPlayer(){
        AliLogUtil.v(TAG, "---initPlayer---SDKVersion: " + AliyunVodPlayer.getSDKVersion());

        mAliyunVodPlayer = new AliyunVodPlayer(getContext());
        // /mnt/sdcard/Android/data/com.dway/cache/aliyun_player_cache
        String cacheDir = getContext().getExternalCacheDir().getPath() + "/aliyun_player_cache";
        mAliyunVodPlayer.setPlayingCache(true, cacheDir, 60 * 60 /*时长, s */, 300 /*大小，MB*/);
        //mAliyunVodPlayer.setCirclePlay(true);

        mAliyunVodPlayer.setOnPreparedListener(this);
        mAliyunVodPlayer.setOnFirstFrameStartListener(this);
        mAliyunVodPlayer.setOnErrorListener(this);
        mAliyunVodPlayer.setOnCompletionListener(this);
        mAliyunVodPlayer.setOnSeekCompleteListener(this);
        //mAliyunVodPlayer.setOnStoppedListner(new MyStoppedListener(this));

        mAliyunVodPlayer.setAutoPlay(true);

        mAliyunVodPlayer.setOnTimeExpiredErrorListener(this);
        mAliyunVodPlayer.setOnLoadingListener(this);
    }

    /**
     * 必须调用的初始化，可传自定义控件
     * @param topView 默认则传null
     * @param bottomView 默认则传null
     */
    public void init(PlayerTopViewBase topView, PlayerBottomViewBase bottomView, PlayerCompleteViewBase completeView){
        if(topView != null) {
            mTopView = topView;
        }else{
            mTopView = new PlayerTopViewDefault(getContext());
        }
        if(bottomView != null) {
            mBottomView = bottomView;
        }else{
            mBottomView = new PlayerBottomViewDefault(getContext());
        }
        if(completeView != null){
            mLoadStatusView.setCompleteView(completeView);
        }

        RelativeLayout.LayoutParams topParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        topParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        addView(mTopView, topParams);

        RelativeLayout.LayoutParams bottomParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(mBottomView, bottomParams);

        mBottomView.mPlayPauseView.setOnClickListener(this);
        mBottomView.mSeekBar.setOnSeekBarChangeListener(this);
    }

    /**
     * 打开阿里的打印
     */
    public void enableNativeLog(){
        mAliyunVodPlayer.enableNativeLog();
    }

    /**
     * 关闭阿里的打印
     */
    public void disableNativeLog(){
        mAliyunVodPlayer.disableNativeLog();
    }

    /**
     * 设置视频缩放模式
     * @param videoScalingMode IAliyunVodPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT 全部显示但不裁剪
     *                         IAliyunVodPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING 会有裁剪
     */
    public void setVideoScalingMode(IAliyunVodPlayer.VideoScalingMode videoScalingMode){
        mAliyunVodPlayer.setVideoScalingMode(videoScalingMode);
    }

    /**
     * 设置预处理状态
     */
    public void setPreprocess(){
        hideControlView();
        mLoadStatusView.setPreprocess();
    }

    /**
     * 设置预处理出错状态
     */
    public void setPreprocessError(String errorMsg){
        mLoadStatusView.setPreprocessError(errorMsg);
    }

    /**
     * 预处理出错界面按钮监听
     * @param listener
     */
    public void setOnPreprocessBtnListener(OnPreprocessBtnListener listener){
        mOnPreprocessBtnListener = listener;
    }

    /**
     * vid在线点播
     * @param vid vid
     */
    public void playWithVid(String vid){
        AliLogUtil.v(TAG, "---playWithVid---vid = " + vid);
        if(mAliyunVodPlayer.getPlayerState() != IAliyunVodPlayer.PlayerState.Idle) {
            mAliyunVodPlayer.stop();
        }

        mUrl = null;
        mlocalPath = null;//置为null，以免影响下一次本地播放
        mVidSts = null;//需要置为null，才能重新获取vidsts来播放

        mVid = vid;
        hideControlView();
        setLoadStatusViewBgBlack(true);
        prepareAsync(false);
    }

    /**
     * localPath本地播放
     * @param localPath localPath
     */
    public void playWithPath(String localPath){
        AliLogUtil.v(TAG, "---playWithPath---localPath = " + localPath);
        if(mAliyunVodPlayer.getPlayerState() != IAliyunVodPlayer.PlayerState.Idle) {
            mAliyunVodPlayer.stop();
        }

        mVid = null;
        mUrl = null;
        mlocalPath = localPath;

        AliyunLocalSource.AliyunLocalSourceBuilder asb = new AliyunLocalSource.AliyunLocalSourceBuilder();
        asb.setSource(mlocalPath);
        AliyunLocalSource localSource = asb.build();
        hideControlView();
        setLoadStatusViewBgBlack(true);
        mLoadStatusView.setLoading();
        mAliyunVodPlayer.prepareAsync(localSource);
    }

    /**
     * 在线url地址播放
     */
    public void playWithUrl(String url){
        AliLogUtil.v(TAG, "---playWithUrl---url = " + url);
        if(mAliyunVodPlayer.getPlayerState() != IAliyunVodPlayer.PlayerState.Idle) {
            mAliyunVodPlayer.stop();
        }

        mUrl = url;
        mlocalPath = null;//置为null，以免影响下一次本地播放
        mVidSts = null;//需要置为null，才能重新获取vidsts来播放
        mVid = null;

        AliyunLocalSource.AliyunLocalSourceBuilder asb = new AliyunLocalSource.AliyunLocalSourceBuilder();
        asb.setSource(mUrl);
        AliyunLocalSource mUrlSource = asb.build();
        mAliyunVodPlayer.prepareAsync(mUrlSource);

        hideControlView();
        setLoadStatusViewBgBlack(true);
        mLoadStatusView.setLoading();
    }

    /**
     * 播放结束回调
     */
    public void setOnPlayCompleteListener(OnPlayCompleteListener listener){
        mOnPlayCompleteListener = listener;
    }

    /**
     * 重播
     */
    public void replay(){
        mLoadStatusView.setLoading();
        mAliyunVodPlayer.replay();
    }

    /**
     * 获取播放时间，只记录处于播放状态的时间，单位秒
     */
    public int getPlayTimeSec(){
        return mPlayTimeSec;
    }


    private void prepareAsync(boolean allowMobilePlay){
        mLoadStatusView.setLoading();
        if(mVidSts == null){
            getVidsts();
        }else{
            if(!allowMobilePlay && NetWatchdog.is4GConnected(getContext())){
                mLoadStatusView.setMobileNet();
            }else{
                AliLogUtil.v(TAG, "---prepareAsync---2");
                mAliyunVodPlayer.prepareAsync(mVidSts);
            }
        }
    }

    private void getVidsts(){
        AliLogUtil.v(TAG, "---getVidsts---");
        mLoadStatusView.setLoading();
        if(mVidStsHelper.isGettingVidsts()){
            return;
        }
        mVidStsHelper.getVidSts(new VidStsHelper.OnStsResultListener() {
            @Override
            public void onSuccess(String akid, String akSecret, String token) {
                AliLogUtil.v(TAG, "---getVidsts---success");
                mVidSts = new AliyunVidSts();
                mVidSts.setVid(mVid);
                mVidSts.setAcId(akid);
                mVidSts.setAkSceret(akSecret);
                mVidSts.setSecurityToken(token);

                if(!mIsPaused) {
                    mLoadStatusView.setHide();
                    prepareAsync(false);
                    stopHideControlView();
                    hideControlView();
                }
            }

            @Override
            public void onFail(int errno) {
                AliLogUtil.v(TAG, "---getVidsts---fail");
                mVidSts = null;
                if(errno == VidStsHelper.ERRNO_SIGNATURE_INVALID) {
                    mLoadStatusView.setErrorNoVidsts(getResources().getString(R.string.player_load_status_view_text_error_signature_invalid));
                }else{
                    mLoadStatusView.setErrorNoVidsts(null);
                }
            }
        });
    }






    public void onResume(){
        if(mIsPaused) {
            mIsPaused = false;
            AliLogUtil.v(TAG, "---onResume---mPlayerState = "+mPlayerState+", mIsPauseWhileGetVidsts = "+mIsPauseWhileGetVidsts);
            if (mPlayerState == IAliyunVodPlayer.PlayerState.Started) {
                /*if (mAliyunVodPlayer.getPlayerState() == IAliyunVodPlayer.PlayerState.Paused) {
                    mAliyunVodPlayer.resume();
                } else if (mPlayerState == IAliyunVodPlayer.PlayerState.Prepared) {
                    mAliyunVodPlayer.start();
                }*/
            } else {
                if (mIsPauseWhileGetVidsts) {
                    mIsPauseWhileGetVidsts = false;
                    /*if (mVidSts != null) {//mVidSts不为null代表获取鉴权成功
                        prepareAsync(false);
                        mLoadStatusView.setHide();
                        stopHideControlView();
                        hideControlView();
                    }*/
                }
            }
        }
    }

    public void onPause(){
        mIsPaused = true;
        AliLogUtil.v(TAG, "---onPause---");
        if(mVidStsHelper.isGettingVidsts()){
            mIsPauseWhileGetVidsts = true;
        }
        mPlayerState = mAliyunVodPlayer.getPlayerState();
        AliLogUtil.v(TAG, "---onPause---"+mPlayerState);
        if(mPlayerState == IAliyunVodPlayer.PlayerState.Started){
            mAliyunVodPlayer.pause();
        }
    }

    public void onDestroy(){
        AliLogUtil.v(TAG, "---onDestroy---");
        if(mVidStsHelper != null) {
            mVidStsHelper.cancelRequest();
        }
        if(mAliyunVodPlayer != null) {
            mAliyunVodPlayer.stop();
            mAliyunVodPlayer.release();
        }
        if(mNetWatchdog != null){
            mNetWatchdog.stopWatch();
        }
        stopHeart();
        stopHideControlView();
        mNetWatchdog = null;
        mAliyunVodPlayer = null;
    }

    /**
     * 设置播放画面可见性，播放器加载阶段设置不可见，避免切换播放源的时候，界面还停留在上一次播放的最后一帧。
     * 这样在INVISIBLE后，surface会destroy掉，感觉不是很好。
     * 所以最后改成去修改LoadStatusView的背景颜色为全黑和半透明，来遮住surface
     */
    private void setLoadStatusViewBgBlack(boolean black){
        //mPlayerSurfaceView.setVisibility(visible ? VISIBLE : INVISIBLE);
        mLoadStatusView.setBgBlack(black);
    }

    /**
     * 上下播放控件可见性
     */
    private boolean getControlViewVisibility(){
        return mTopView.getVisibility() == View.VISIBLE || mBottomView.getVisibility() == View.VISIBLE;
    }

    private void showControlView() {
        mTopView.setVisibility(View.VISIBLE);
        mBottomView.setVisibility(View.VISIBLE);
    }

    private void hideControlView() {
        mTopView.setVisibility(View.GONE);
        mBottomView.setVisibility(View.GONE);
    }

    private void delayHideControlView(){
        mHandler.removeMessages(MSG_HIDE_CONTROL_VIEW);
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROL_VIEW, 10000);
    }

    private void stopHideControlView(){
        mHandler.removeMessages(MSG_HIDE_CONTROL_VIEW);
    }

    private void startHeart(){
        stopHeart();
        Message message = mHandler.obtainMessage(MSG_HEART);
        mHandler.sendMessage(message);
    }

    private void continueHeart(){
        Message message = mHandler.obtainMessage(MSG_HEART);
        mHandler.sendMessageDelayed(message, 1000);
    }

    private void stopHeart(){
        mHandler.removeMessages(MSG_HEART);
    }

    private void heartProcess(){
        AliLogUtil.v(TAG, "---heartProcess--- PlayerState = " + mAliyunVodPlayer.getPlayerState()
                + ", " + mAliyunVodPlayer.getCurrentPosition()
                + ", " + mAliyunVodPlayer.getBufferingPosition()
                + ", " + mAliyunVodPlayer.getDuration()
                + ", " + mIsSeekBarTouching
                + ", " + mIsSeekComplete);
        //设置播放进度和缓存进度
        if(!mIsSeekBarTouching && mIsSeekComplete){
            setPlayProgressInfo();
        }
        //播放暂停按钮
        if(mAliyunVodPlayer.getPlayerState() == IAliyunVodPlayer.PlayerState.Started){
            mPlayTimeSec++;
            mBottomView.setPlayPauseStatus(true);
        }else{
            mBottomView.setPlayPauseStatus(false);
        }
    }

    private void setPlayProgressInfo(){
        mBottomView.setSeekBarMax((int) mAliyunVodPlayer.getDuration());
        mBottomView.setSeekBarProgress((int) mAliyunVodPlayer.getCurrentPosition());
        mBottomView.setSeekBarSecondaryProgress(mAliyunVodPlayer.getBufferingPosition());
        mBottomView.setCurrentText(mAliyunVodPlayer.getCurrentPosition());
        mBottomView.setDurationText(mAliyunVodPlayer.getDuration());
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MSG_HEART:
                    heartProcess();
                    continueHeart();
                    break;
                case MSG_HIDE_CONTROL_VIEW:
                    hideControlView();
                    break;
            }
            return true;
        }
    });

    @Override
    public void onClick(View v) {
        AliLogUtil.v(TAG, "---onClick---"+v);
        if(v == mBottomView.mPlayPauseView){
            IAliyunVodPlayer.PlayerState playerState = mAliyunVodPlayer.getPlayerState();
            AliLogUtil.v(TAG, "---onClick---playerState = " + playerState);
            if(playerState == IAliyunVodPlayer.PlayerState.Started){
                mAliyunVodPlayer.pause();
                mBottomView.setPlayPauseStatus(false);
            }else if(playerState == IAliyunVodPlayer.PlayerState.Paused){
                mAliyunVodPlayer.resume();
                mBottomView.setPlayPauseStatus(true);
            }else{
                mAliyunVodPlayer.start();
                mBottomView.setPlayPauseStatus(true);
            }
            delayHideControlView();
        }else if(v == mPlayerSurfaceView){
            if(mLoadStatusView.getStatus() == PlayerLoadStatusView.STATUS_FINISH){
                return;
            }
            if(getControlViewVisibility()){
                hideControlView();
            }else{
                showControlView();
                delayHideControlView();
            }
        }
    }

    @Override
    public void onContinueBtnClick() {
        AliLogUtil.v(TAG, "---onContinueBtnClick---"+ mLoadStatusView.getStatus());
        switch (mLoadStatusView.getStatus()){
            case PlayerLoadStatusView.STATUS_ERROR_NO_VIDSTS:
                prepareAsync(false);
                break;
            case PlayerLoadStatusView.STATUS_ERROR_OTHER:
                mLoadStatusView.setLoading();
                mAliyunVodPlayer.seekTo((int) mCurrentPosition);
                mAliyunVodPlayer.replay();
                break;
            case PlayerLoadStatusView.STATUS_MOBILE_NET:
                prepareAsync(true);
                break;
            case PlayerLoadStatusView.STATUS_FINISH:
                mLoadStatusView.setLoading();
                mAliyunVodPlayer.replay();
                break;
            case PlayerLoadStatusView.STATUS_PREPROCESS_ERROR:
                if(mOnPreprocessBtnListener != null){
                    mOnPreprocessBtnListener.onPreContinueBtnClick();
                }
                break;
        }
    }

    @Override
    public void onCancelBtnClick() {
        AliLogUtil.v(TAG, "---onCancelBtnClick---");
        switch (mLoadStatusView.getStatus()){
            case PlayerLoadStatusView.STATUS_PREPROCESS_ERROR:
                if(mOnPreprocessBtnListener != null){
                    mOnPreprocessBtnListener.onPreCancelBtnClick();
                }
                break;
            default:
                Context context = getContext();
                if(context instanceof Activity) {
                    ((Activity)context).finish();
                }
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser) {
            mBottomView.setCurrentText(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        AliLogUtil.v(TAG, "---onStartTrackingTouch---");
        mIsSeekBarTouching = true;
        stopHideControlView();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        AliLogUtil.v(TAG, "---onStopTrackingTouch---");
        mIsSeekBarTouching = false;
        mIsSeekComplete = false;
        mAliyunVodPlayer.seekTo(seekBar.getProgress());
        delayHideControlView();
    }

    @Override
    public void onSeekComplete() {
        AliLogUtil.v(TAG, "---onSeekComplete---");
        mIsSeekComplete = true;
    }

    @Override
    public void onPrepared() {
        AliLogUtil.v(TAG, "---onPrepared---" + mAliyunVodPlayer.getCurrentQuality()+", "+mAliyunVodPlayer.getPlayerState());
        //可设置播放时期望的清晰度。默认为原画清晰度
        //mAliyunVodPlayer.changeQuality(IAliyunVodPlayer.QualityValue.QUALITY_LOW);
    }

    @Override
    public void onFirstFrameStart() {
        AliLogUtil.v(TAG, "---onFirstFrameStart---");
        setLoadStatusViewBgBlack(false);
        mPlayTimeSec = 0;
        mLoadStatusView.setHide();
        showControlView();
        delayHideControlView();
        startHeart();
        //回调的时候可能已经进入onPause，所以需要暂停
        if(mIsPaused){
            AliLogUtil.v(TAG, "---onFirstFrameStart---pause "+mAliyunVodPlayer.getPlayerState());
            mAliyunVodPlayer.pause();
        }
    }

    @Override
    public void onLoadStart() {
        AliLogUtil.v(TAG, "---onLoadStart---");
        mLoadProgressTextView.setVisibility(VISIBLE);
        mLoadProgressTextView.setText(StringUtil.format(getContext(), R.string.player_loading_text, 0));
    }

    @Override
    public void onLoadEnd() {
        AliLogUtil.v(TAG, "---onLoadEnd---");
        mLoadProgressTextView.setVisibility(GONE);
        //mLoadStatusView.setHide();
    }

    @Override
    public void onLoadProgress(int i) {
        //AliLogUtil.v(TAG, "---onLoadProgress--- " + i);
        mLoadProgressTextView.setVisibility(VISIBLE);
        mLoadProgressTextView.setText(StringUtil.format(getContext(), R.string.player_loading_text, i));
    }

    @Override
    public void onCompletion() {
        AliLogUtil.v(TAG, "---onCompletion---");
        mIsSeekComplete = true;//播放完有可能没回调onSeekComplete
        mLoadStatusView.setFinish();
        stopHeart();
        stopHideControlView();
        setPlayProgressInfo();
        hideControlView();
        if(mOnPlayCompleteListener != null){
            mOnPlayCompleteListener.onPlayCompleteListener();
        }
    }

    @Override
    public void onChangeQualitySuccess(String s) {
        AliLogUtil.v(TAG, "---onChangeQualitySuccess---"+s);
    }

    @Override
    public void onChangeQualityFail(int i, String s) {
        AliLogUtil.v(TAG, "---onChangeQualityFail---"+i+", "+s);
    }

    @Override
    public void onTimeExpiredError() {
        AliLogUtil.v(TAG, "---onTimeExpiredError---");
        if(!TextUtils.isEmpty(mVid)) {
            mVidSts = null;//需要置为null，才能重新获取vidsts来播放
            prepareAsync(false);
        }
    }

    @Override
    public void onError(int i, int i1, String s) {
        AliLogUtil.v(TAG, "---onError---"+i+", "+i1+", "+s);
        if(i == 4002){
            //4002是鉴权信息过期，跟onTimeExpiredError重复，所以不处理
            return;
        }
        mCurrentPosition = mAliyunVodPlayer.getCurrentPosition();
        AliLogUtil.v(TAG, "---onError---mCurrentPosition = "+mCurrentPosition);
        mAliyunVodPlayer.stop();
        AliLogUtil.v(TAG, "---onError---stop");
        stopHeart();
        hideControlView();
        if(NetWatchdog.hasNet(getContext())){
            mLoadStatusView.setErrorOther(StringUtil.format(getContext(), R.string.player_load_status_view_text_error_code, i));
        }else{
            AliLogUtil.v(TAG, "---onError---no net");
            mLoadStatusView.setErrorOther(getResources().getString(R.string.player_load_status_view_text_error_no_net));
        }
        AliLogUtil.v(TAG, "---onError---end");
        if(!TextUtils.isEmpty(mlocalPath)){
            //本地播放

        }else if(!TextUtils.isEmpty(mVid)){
            //vidsts播放

        }else if(!TextUtils.isEmpty(mUrl)){
            //在线url播放

        }
    }

    @Override
    public void onWifiTo4G() {
        AliLogUtil.v(TAG, "---onWifiTo4G---");
        //vidsts点播方式需要提醒，URL在线播放方式也需要提醒
        if(!TextUtils.isEmpty(mVid) || !TextUtils.isEmpty(mUrl)) {
            mAliyunVodPlayer.pause();
            mLoadStatusView.setMobileNet();
        }
    }

    @Override
    public void on4GToWifi() {
        AliLogUtil.v(TAG, "---on4GToWifi---");
        if(mLoadStatusView.getStatus() == PlayerLoadStatusView.STATUS_MOBILE_NET){
            mLoadStatusView.setHide();
            stopHideControlView();
            hideControlView();
        }
    }

    @Override
    public void onNetDisconnected() {
        AliLogUtil.v(TAG, "---onNetDisconnected---");
    }



    //播放结束接听接口
    public interface OnPlayCompleteListener{
        void onPlayCompleteListener();
    }

    //预处理界面按钮点击监听接口
    public interface OnPreprocessBtnListener {
        void onPreContinueBtnClick();
        void onPreCancelBtnClick();
    }

}
