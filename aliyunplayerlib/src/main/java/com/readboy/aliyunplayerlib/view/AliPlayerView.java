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

import com.alivc.player.AliyunErrorCode;
import com.aliyun.vodplayer.media.AliyunLocalSource;
import com.aliyun.vodplayer.media.AliyunVidSts;
import com.aliyun.vodplayer.media.AliyunVodPlayer;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.readboy.aliyunplayerlib.R;
import com.readboy.aliyunplayerlib.helper.VidStsHelper;
import com.readboy.aliyunplayerlib.utils.AliLogUtil;
import com.readboy.aliyunplayerlib.utils.NetWatchdog;
import com.readboy.aliyunplayerlib.utils.StringUtil;

/**
 * 播放器控件
 * Created by ldw on 2018/3/21.
 */

public class AliPlayerView extends RelativeLayout implements View.OnClickListener,
        PlayerLoadStatusViewBase.OnBtnClickListener,
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
    private static final String TAG = "oubin_AliPlayerView";

    //版本号
    private static final String VERSION = "V1.0.180531001";

    //常量
    private static final int MSG_HEART = 1;
    private static final int MSG_HIDE_CONTROL_VIEW = 2;

    //控件
    private PlayerTopViewBase mTopView = null;
    private PlayerBottomViewBase mBottomView = null;
    //private PlayerLoadStatusView mLoadStatusView;
    private PlayerLoadStatusViewBase mLoadStatusView = null;
    private TextView mLoadProgressTextView;
    private SurfaceView mPlayerSurfaceView;

    //播放器
    private AliyunVodPlayer mAliyunVodPlayer = null;

    //闲置状态点击监听
    private OnIdleBtnListener mOnIdleBtnListener = null;

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

    //加载进度
    private int mLoadProgress = 0;

    //失败重连
    private int mAutoCount = 0;//出错自动尝试播放次数计算
    private static final int AUTO_COUNT_MAX = 3;//最多尝试重新播放3次

    //辅助标志
    private boolean mIsPaused = false;//是否pause了
    private boolean mIsSeekBarTouching = false;//是否正在对SeekBar进行拖动
    private boolean mIsSeekComplete = true;//是否seek完成
    private IAliyunVodPlayer.PlayerState mPlayerState;//pause时播放状态，在onResume判断恢复
    private boolean mIsPauseWhileGetVidsts = false;
    private boolean mIsPrepareAsyncSuccess = false;//是否prepareAsync成功

    //播放清晰度选择顺序
    /*private String[] QUALITYS = {
            IAliyunVodPlayer.QualityValue.QUALITY_LOW,
            IAliyunVodPlayer.QualityValue.QUALITY_FLUENT,
            IAliyunVodPlayer.QualityValue.QUALITY_STAND,
            IAliyunVodPlayer.QualityValue.QUALITY_HIGH,
            IAliyunVodPlayer.QualityValue.QUALITY_2K,
            IAliyunVodPlayer.QualityValue.QUALITY_4K,
            IAliyunVodPlayer.QualityValue.QUALITY_ORIGINAL
    };*/


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
        //mLoadStatusView = findViewById(R.id.video_player_loading);
        mLoadProgressTextView = findViewById(R.id.video_player_load_progress);
        mPlayerSurfaceView = findViewById(R.id.video_player_surface);
        //oubin
//        mPlayerSurfaceView.setOnClickListener(this);
        //mLoadStatusView.setOnBtnClickListener(this);

        mPlayerSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                AliLogUtil.v(TAG, "---surfaceCreated---");
                if(mAliyunVodPlayer != null) {
                    mAliyunVodPlayer.setDisplay(holder);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                AliLogUtil.v(TAG, "---surfaceChanged---");
                if(mAliyunVodPlayer != null) {
                    mAliyunVodPlayer.surfaceChanged();
                }
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
        mAliyunVodPlayer.setPlayingCache(false, cacheDir, 60 * 60 /*时长, s */, 300 /*大小，MB*/);
        //mAliyunVodPlayer.setCirclePlay(true);

        mAliyunVodPlayer.setOnPreparedListener(this);
        mAliyunVodPlayer.setOnFirstFrameStartListener(this);
        mAliyunVodPlayer.setOnErrorListener(this);
        mAliyunVodPlayer.setOnCompletionListener(this);
        mAliyunVodPlayer.setOnSeekCompleteListener(this);
        //mAliyunVodPlayer.setOnStoppedListner(new MyStoppedListener(this));

        //mAliyunVodPlayer.setAutoPlay(true);

        mAliyunVodPlayer.setOnTimeExpiredErrorListener(this);
        mAliyunVodPlayer.setOnLoadingListener(this);

        //mAliyunVodPlayer.enableNativeLog();
    }

    /**
     * 必须调用的初始化，可传自定义控件。默认则传null
     */
    public void init(
            PlayerTopViewBase topView,
            PlayerBottomViewBase bottomView,
            PlayerLoadStatusViewBase loadStatusView,
            PlayerIdleViewBase idleView,
            PlayerCompleteViewBase completeView){
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
        if(loadStatusView != null){
            mLoadStatusView = loadStatusView;
        }else{
            mLoadStatusView = new PlayerLoadStatusViewDefault(getContext());
        }
        if(idleView != null){
            mLoadStatusView.setIdleView(idleView);
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

        RelativeLayout.LayoutParams loadStatusParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mLoadStatusView, loadStatusParams);

        mBottomView.mPlayPauseView.setOnClickListener(this);
        mBottomView.mSeekBar.setOnSeekBarChangeListener(this);

        mLoadStatusView.setOnBtnClickListener(this);
        mLoadStatusView.setIdle();
    }

    public void init(PlayerTopViewBase topView, PlayerBottomViewBase bottomView, PlayerCompleteViewBase completeView){
        init(topView, bottomView, null, null, completeView);
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
        resetState();
        if (mAliyunVodPlayer.getPlayerState() != IAliyunVodPlayer.PlayerState.Idle) {
            mAliyunVodPlayer.stop();
        }

        mUrl = null;
        mlocalPath = null;//置为null，以免影响下一次本地播放
        mVidSts = null;//需要置为null，才能重新获取vidsts来播放

        mVid = vid;

        mIsSeekComplete = true;

        /*hideControlView();
        setLoadStatusViewBgBlack(true);
        prepareAsync(false);*/

        mAutoCount = 0;
        mCurrentPosition = 0;
        prepareAsync(false);
    }

    /**
     * localPath本地播放
     * @param localPath localPath
     */
    public void playWithPath(String localPath){
        AliLogUtil.v(TAG, "---playWithPath---localPath = " + localPath);
        resetState();
        if (mAliyunVodPlayer.getPlayerState() != IAliyunVodPlayer.PlayerState.Idle) {
            mAliyunVodPlayer.stop();
        }

        mVid = null;
        mUrl = null;
        mlocalPath = localPath;

        mIsSeekComplete = true;

        /*AliyunLocalSource.AliyunLocalSourceBuilder asb = new AliyunLocalSource.AliyunLocalSourceBuilder();
        asb.setSource(mlocalPath);
        AliyunLocalSource localSource = asb.build();
        hideControlView();
        setLoadStatusViewBgBlack(true);
        mLoadStatusView.setLoading();
        mAliyunVodPlayer.prepareAsync(localSource);
        mIsPrepareAsyncSuccess = false;*/

        mAutoCount = 0;
        mCurrentPosition = 0;
        prepareAsync(false);
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

        mIsSeekComplete = true;

        /*AliyunLocalSource.AliyunLocalSourceBuilder asb = new AliyunLocalSource.AliyunLocalSourceBuilder();
        asb.setSource(mUrl);
        AliyunLocalSource mUrlSource = asb.build();
        mAliyunVodPlayer.prepareAsync(mUrlSource);
        mIsPrepareAsyncSuccess = false;

        hideControlView();
        setLoadStatusViewBgBlack(true);
        mLoadStatusView.setLoading();*/

        mAutoCount = 0;
        mCurrentPosition = 0;
        prepareAsync(false);
    }

    private boolean isVidPlay(){
        return !TextUtils.isEmpty(mVid);
    }

    private boolean isLocalPlay(){
        return !TextUtils.isEmpty(mlocalPath);
    }

    private boolean isUrlPlay(){
        return !TextUtils.isEmpty(mUrl);
    }

    /**
     * 设置指定位置播放，必须在playWithXXX之后调用，因为playWithXXX默认是从0开始播放
     * @param ms 毫秒
     */
    public void seekTo(long ms){
        AliLogUtil.v(TAG, "---seekTo---ms = "+ms+", PlayerState = "+mAliyunVodPlayer.getPlayerState());
        mCurrentPosition = ms;
        //mAliyunVodPlayer.seekTo((int) mCurrentPosition);
        /*if(mAliyunVodPlayer.getPlayerState() != IAliyunVodPlayer.PlayerState.Idle){
            mAliyunVodPlayer.seekTo((int) mCurrentPosition);
        }*/

        IAliyunVodPlayer.PlayerState playerState = mAliyunVodPlayer.getPlayerState();
        if(playerState == IAliyunVodPlayer.PlayerState.Started || playerState == IAliyunVodPlayer.PlayerState.Paused || playerState == IAliyunVodPlayer.PlayerState.Prepared) {
            mAliyunVodPlayer.seekTo((int) mCurrentPosition);
            mIsSeekComplete = false;
            mBottomView.setSeekBarProgress((int) mCurrentPosition);
            delayHideControlView();
        }
    }

    /**
     * 获取当前播放进度
     * @return 单位毫秒
     */
    public long getCurrentPosition(){
        return mAliyunVodPlayer != null ? mAliyunVodPlayer.getCurrentPosition() : 0;
    }

    /**
     * 获取播放状态
     */
    public IAliyunVodPlayer.PlayerState getPlayerState(){
        return mAliyunVodPlayer.getPlayerState();
    }

    /**
     * 闲置状态点击回调
     */
    public void setOnIdleBtnListener(OnIdleBtnListener listener){
        mOnIdleBtnListener = listener;
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
        AliLogUtil.v(TAG, "---replay---");
        mLoadStatusView.setLoading();
        if(mIsPrepareAsyncSuccess) {
            mAliyunVodPlayer.replay();
            mAutoCount = 0;
        }else{
            mAutoCount = 0;
            prepareAsync(false);
        }
    }

    /**
     * 停止播放，回到闲置状态idle
     */
    public void setIdle(){
        AliLogUtil.v(TAG, "---setIdle---");
        mLoadStatusView.setIdle();
        if(mVidStsHelper != null) {
            mVidStsHelper.cancelRequest();
        }
        if(mAliyunVodPlayer != null) {
            mAliyunVodPlayer.stop();
        }
        stopHeart();
        stopHideControlView();
    }

    /**
     * 获取播放时间，只记录处于播放状态的时间，单位秒
     */
    public int getPlayTimeSec(){
        return mPlayTimeSec;
    }


    private void prepareAsync(boolean allowMobilePlay){
        AliLogUtil.v(TAG, "---prepareAsync---");
        if(isVidPlay()){
            //vidsts播放
            hideControlView();
            setLoadStatusViewBgBlack(true);
            mLoadStatusView.setLoading();
            //mAutoCount = 0;
            if(mVidSts == null){
                getVidsts();
            }else{
                if(!allowMobilePlay && NetWatchdog.is4GConnected(getContext())){
                    mLoadStatusView.setMobileNet();
                }else{
                    AliLogUtil.v(TAG, "---prepareAsync---prepareAsync(vidsts)");
                    mAliyunVodPlayer.prepareAsync(mVidSts);
                    mIsPrepareAsyncSuccess = false;
                }
            }
        }else if(isLocalPlay()){
            //本地播放
            AliyunLocalSource.AliyunLocalSourceBuilder asb = new AliyunLocalSource.AliyunLocalSourceBuilder();
            asb.setSource(mlocalPath);
            AliyunLocalSource localSource = asb.build();
            hideControlView();
            setLoadStatusViewBgBlack(true);
            mLoadStatusView.setLoading();
            mAliyunVodPlayer.prepareAsync(localSource);
            mIsPrepareAsyncSuccess = false;
            //mAutoCount = 0;
        }else if(isUrlPlay()){
            //在线url播放
            hideControlView();
            setLoadStatusViewBgBlack(true);
            mLoadStatusView.setLoading();
            //mAutoCount = 0;

            if(!allowMobilePlay && NetWatchdog.is4GConnected(getContext())){
                mLoadStatusView.setMobileNet();
            }else{
                AliyunLocalSource.AliyunLocalSourceBuilder asb = new AliyunLocalSource.AliyunLocalSourceBuilder();
                asb.setSource(mUrl);
                AliyunLocalSource mUrlSource = asb.build();
                mAliyunVodPlayer.prepareAsync(mUrlSource);
                mIsPrepareAsyncSuccess = false;
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

                if(!mIsPaused && mLoadStatusView.getStatus() != PlayerLoadStatusViewBase.STATUS_IDLE) {
                    mLoadStatusView.setHide();
                    prepareAsync(false);
                    stopHideControlView();
                    hideControlView();
                }
            }

            @Override
            public void onFail(int errno) {
                AliLogUtil.v(TAG, "---getVidsts---onFail: "+errno);
                mVidSts = null;
                if(mLoadStatusView.getStatus() == PlayerLoadStatusViewBase.STATUS_IDLE){
                    AliLogUtil.v(TAG, "---getVidsts---onFail: load status is idle, return");
                    return;
                }
                if(errno == VidStsHelper.ERRNO_SIGNATURE_INVALID) {
                    mLoadStatusView.setErrorNoVidsts(getResources().getString(R.string.player_load_status_view_text_error_signature_invalid));
                }else if(errno == VidStsHelper.ERRNO_DEVICE_UNAUTH) {
                    mLoadStatusView.setErrorNoVidsts(getResources().getString(R.string.player_load_status_view_text_error_device_unauth));
                }else{
                    if(NetWatchdog.hasNet(getContext())){
                        mLoadStatusView.setErrorNoVidsts(getResources().getString(R.string.player_load_status_view_text_error_vidsts_fail));
                    }else{
                        mLoadStatusView.setErrorNoVidsts(null);
                    }
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
                    if(mVidSts != null) {
                        mLoadStatusView.setHide();
                        //prepareAsync(false);
                        //stopHideControlView();
                        //hideControlView();
                        showControlView();
                        //delayHideControlView();
                    }
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
            mBottomView.setPlayPauseStatus(false);//播放暂停按钮ldw180508
            mLoadStatusView.setHide();
            showControlView();
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

    public long getDuration() {
        if (mAliyunVodPlayer == null) {
            return -1;
        }
        return mAliyunVodPlayer.getDuration();
    }

    public boolean isPlaying() {
        return mAliyunVodPlayer.isPlaying();
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
        //oubin
//        mTopView.setVisibility(View.VISIBLE);
//        mBottomView.setVisibility(View.VISIBLE);
    }

    private void hideControlView() {
        //oubin
//        mTopView.setVisibility(View.GONE);
//        mBottomView.setVisibility(View.GONE);
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
        int current = (int) mAliyunVodPlayer.getCurrentPosition();
        int duration = (int) mAliyunVodPlayer.getDuration();
        int max = Math.max(current, duration);//因为计算不准确，可能存在当前比总长大的情况，没办法只能这么处理
        mBottomView.setSeekBarMax(max);
        mBottomView.setSeekBarProgress(current);
        mBottomView.setSeekBarSecondaryProgress(mAliyunVodPlayer.getBufferingPosition());
        mBottomView.setCurrentText(mAliyunVodPlayer.getCurrentPosition());
        mBottomView.setDurationText(max);
    }

    private void resetState() {
        mIsSeekComplete = true;
        mIsSeekBarTouching = false;
        mIsPaused = false;
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
            }else if(playerState == IAliyunVodPlayer.PlayerState.Prepared){
                mAliyunVodPlayer.start();
                mBottomView.setPlayPauseStatus(true);
            }else{
                mAutoCount = 0;
                prepareAsync(false);
                mBottomView.setPlayPauseStatus(true);
            }
            delayHideControlView();
        }else if(v == mPlayerSurfaceView){
            if(mLoadStatusView.getStatus() == PlayerLoadStatusViewBase.STATUS_FINISH){
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
            case PlayerLoadStatusViewBase.STATUS_IDLE:
                if(mOnIdleBtnListener != null) {
                    mOnIdleBtnListener.onIdleContinueBtnClick();
                }
                break;
            case PlayerLoadStatusViewBase.STATUS_ERROR_NO_VIDSTS:
                mAutoCount = 0;
                prepareAsync(false);
                break;
            case PlayerLoadStatusViewBase.STATUS_ERROR_OTHER:
                mLoadStatusView.setLoading();
                mAliyunVodPlayer.seekTo((int) mCurrentPosition);
                IAliyunVodPlayer.PlayerState state = mAliyunVodPlayer.getPlayerState();
                if(state == IAliyunVodPlayer.PlayerState.Prepared || state == IAliyunVodPlayer.PlayerState.Paused || state == IAliyunVodPlayer.PlayerState.Started){
                    mAliyunVodPlayer.start();
                }else {
                    mAliyunVodPlayer.replay();
                }
                break;
            case PlayerLoadStatusViewBase.STATUS_MOBILE_NET:
                if(mAliyunVodPlayer.getPlayerState() == IAliyunVodPlayer.PlayerState.Paused){
                    mAliyunVodPlayer.resume();
                    mBottomView.setPlayPauseStatus(true);
                    mLoadStatusView.setHide();
                }else {
                    mAutoCount = 0;
                    prepareAsync(true);
                }
                break;
            case PlayerLoadStatusViewBase.STATUS_FINISH:
                mLoadStatusView.setLoading();
                mAliyunVodPlayer.replay();
                break;
            case PlayerLoadStatusViewBase.STATUS_PREPROCESS_ERROR:
                if(mOnPreprocessBtnListener != null){
                    mOnPreprocessBtnListener.onPreContinueBtnClick();
                }
                break;
            case PlayerLoadStatusViewBase.STATUS_CONTINUE:
                if(mAliyunVodPlayer.getPlayerState() == IAliyunVodPlayer.PlayerState.Prepared){
                    mAliyunVodPlayer.start();
                    mBottomView.setPlayPauseStatus(true);
                }else{
                    mAutoCount = 0;
                    prepareAsync(false);
                    mBottomView.setPlayPauseStatus(true);
                }
                break;
        }
    }

    @Override
    public void onCancelBtnClick() {
        AliLogUtil.v(TAG, "---onCancelBtnClick---");
        switch (mLoadStatusView.getStatus()){
            case PlayerLoadStatusViewBase.STATUS_PREPROCESS_ERROR:
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
        AliLogUtil.v(TAG, "---onStopTrackingTouch---playerState = "+mAliyunVodPlayer.getPlayerState()
                +", seekTo = "+seekBar.getProgress()+", mIsSeekComplete="+mIsSeekComplete);
        mIsSeekBarTouching = false;
        IAliyunVodPlayer.PlayerState playerState = mAliyunVodPlayer.getPlayerState();
        if(playerState == IAliyunVodPlayer.PlayerState.Started || playerState == IAliyunVodPlayer.PlayerState.Paused || playerState == IAliyunVodPlayer.PlayerState.Prepared) {
            if(mIsSeekComplete) {
                mIsSeekComplete = false;
                mAliyunVodPlayer.seekTo(seekBar.getProgress());
            }
        }
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
        //可设置播放时期望的清晰度。阿里云默认从高到低选择清晰度
        //mAliyunVodPlayer.changeQuality(IAliyunVodPlayer.QualityValue.QUALITY_LOW);
        AliLogUtil.v(TAG, "---onPrepared---videoId = "+mAliyunVodPlayer.getMediaInfo().getVideoId());
        if(isVidPlay() && !mVid.equals(mAliyunVodPlayer.getMediaInfo().getVideoId())){
            AliLogUtil.v(TAG, "---onPrepared---no this vid, prepareAsync, mVid = " + mVid +
            ", videoId = " + mAliyunVodPlayer.getMediaInfo().getVideoId());
            prepareAsync(false);
            return;
        }else if(isLocalPlay() && (!mlocalPath.equals(mAliyunVodPlayer.getMediaInfo().getVideoId())
        && !TextUtils.isEmpty(mAliyunVodPlayer.getMediaInfo().getVideoId()))){
            AliLogUtil.v(TAG, "---onPrepared---no this localPath, prepareAsync, mlocalPath = " + mlocalPath
            + ", videoId = " + mAliyunVodPlayer.getMediaInfo().getVideoId());
            prepareAsync(true);
            return;
        }else if(isUrlPlay() && !mUrl.equals(mAliyunVodPlayer.getMediaInfo().getVideoId())){
            AliLogUtil.v(TAG, "---onPrepared---no this url, prepareAsync");
            prepareAsync(false);
            return;
        }

        AliLogUtil.v(TAG, "---onPrepared---load status = "+mLoadStatusView.getStatus());
        //回调的时候已经回到idle状态的话，说明stop掉了，不进行播放
        if(mLoadStatusView.getStatus() == PlayerLoadStatusViewBase.STATUS_IDLE){
            AliLogUtil.v(TAG, "---onPrepared---idle status, return");
            return;
        }

        //seekTo
        if(mCurrentPosition > 0) {
            if(mCurrentPosition > mAliyunVodPlayer.getDuration()){
                mCurrentPosition = mAliyunVodPlayer.getDuration();
            }
            mAliyunVodPlayer.seekTo((int) mCurrentPosition);
        }
        //回调的时候可能已经进入onPause，所以需要暂停
        if(!mIsPaused){
            if( (isVidPlay() || isUrlPlay()) && !NetWatchdog.hasNet(getContext()) ){
                AliLogUtil.v(TAG, "---onPrepared---no net");
                mLoadStatusView.setErrorOther(getResources().getString(R.string.player_load_status_view_text_error_no_net));
            }else {
                mAliyunVodPlayer.start();
            }
        }else{
            AliLogUtil.v(TAG, "---onPrepared---mIsPaused = true");
            //mLoadStatusView.setHide();
            //showControlView();
            mLoadStatusView.setContinue();
        }
        mAutoCount = 0;
        mIsPrepareAsyncSuccess = true;
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
    }

    @Override
    public void onLoadStart() {
        //AliLogUtil.v(TAG, "---onLoadStart---"+mLoadStatusView.getStatus());
        mLoadProgress = 0;
        //以下为了不让两个界面重叠
        if(mLoadStatusView.getStatus() != PlayerLoadStatusViewBase.STATUS_HIDE){
            mLoadProgressTextView.setVisibility(GONE);
        }else {
            mLoadProgressTextView.setVisibility(VISIBLE);
            mLoadProgressTextView.setText(StringUtil.format(getContext(), R.string.player_loading_text, mLoadProgress));
        }
    }

    @Override
    public void onLoadEnd() {
        AliLogUtil.v(TAG, "---onLoadEnd---");
        mLoadProgress = 100;
        mLoadProgressTextView.setVisibility(GONE);
    }

    @Override
    public void onLoadProgress(int i) {
        //AliLogUtil.v(TAG, "---onLoadProgress--- "+i+", "+mLoadStatusView.getStatus());
        if(mLoadProgress == i){
            if(mLoadStatusView.getStatus() != PlayerLoadStatusViewBase.STATUS_HIDE){
                mLoadProgressTextView.setVisibility(GONE);
            }else {
                mLoadProgressTextView.setVisibility(VISIBLE);
                //mLoadProgressTextView.setText(StringUtil.format(getContext(), R.string.player_loading_text, mLoadProgress));
            }
        }else{
            mLoadProgress = i;
            if(mLoadStatusView.getStatus() != PlayerLoadStatusViewBase.STATUS_HIDE){
                mLoadProgressTextView.setVisibility(GONE);
            }else {
                mLoadProgressTextView.setVisibility(VISIBLE);
                mLoadProgressTextView.setText(StringUtil.format(getContext(), R.string.player_loading_text, mLoadProgress));
            }
        }
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
        if(isVidPlay()) {
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
        if(mAliyunVodPlayer == null){
            //可能已经退出界面了
            return;
        }

        mCurrentPosition = mAliyunVodPlayer.getCurrentPosition();
        AliLogUtil.v(TAG, "---onError---mCurrentPosition = "+mCurrentPosition);
        mAliyunVodPlayer.stop();
        AliLogUtil.v(TAG, "---onError---mAliyunVodPlayer.stop()");
        stopHeart();
        hideControlView();
        if((isVidPlay() || isUrlPlay()) && NetWatchdog.hasNet(getContext()) || isLocalPlay()){
            AliLogUtil.v(TAG, "---onError---mAutoCount = "+mAutoCount);
            mAutoCount++;
            if(mAutoCount <= AUTO_COUNT_MAX){
                mLoadStatusView.setLoading();
                if(mIsPrepareAsyncSuccess) {
                    mAliyunVodPlayer.seekTo((int) mCurrentPosition);
                    mAliyunVodPlayer.replay();
                }else{
                    prepareAsync(false);
                }
            }else {
                mAutoCount = 0;
                if(isVidPlay() || isUrlPlay()) {
                    if (i == AliyunErrorCode.ALIVC_ERR_INVALID_INPUTFILE.getCode()) {
                        mLoadStatusView.setErrorOther(StringUtil.format(getContext(),
                                R.string.player_load_status_view_text_error_code_msg, i,
                                getResources().getString(R.string.player_error_4003)));
                    } else if (i == AliyunErrorCode.ALIVC_ERROR_LOADING_TIMEOUT.getCode()) {
                        mLoadStatusView.setErrorOther(StringUtil.format(getContext(),
                                R.string.player_load_status_view_text_error_code_msg, i,
                                getResources().getString(R.string.player_error_4008)));
                    } else if (i == AliyunErrorCode.ALIVC_ERR_DATA_ERROR.getCode()) {
                        mLoadStatusView.setErrorOther(StringUtil.format(getContext(),
                                R.string.player_load_status_view_text_error_code_msg, i,
                                getResources().getString(R.string.player_error_4501)));
                    } else if (i == AliyunErrorCode.ALIVC_ERR_QEQUEST_SAAS_SERVER_ERROR.getCode()) {
                        mLoadStatusView.setErrorOther(StringUtil.format(getContext(),
                                R.string.player_load_status_view_text_error_code_msg, i,
                                getResources().getString(R.string.player_error_4502)));
                    } else {
                        mLoadStatusView.setErrorOther(StringUtil.format(getContext(),
                                R.string.player_load_status_view_text_error_code, i));
                    }
                }else{
                    mLoadStatusView.setErrorOther(StringUtil.format(getContext(),
                            R.string.player_load_status_view_text_error_code, i));
                }
            }
        }else{
            AliLogUtil.v(TAG, "---onError---no net");
            mLoadStatusView.setErrorOther(getResources().getString(R.string.player_load_status_view_text_error_no_net));
        }

        AliLogUtil.v(TAG, "---onError---end");
    }

    @Override
    public void onWifiTo4G() {
        AliLogUtil.v(TAG, "---onWifiTo4G---");
        //vidsts点播方式需要提醒，URL在线播放方式也需要提醒
        if(isVidPlay() || isUrlPlay()) {
            if(mLoadStatusView != null && mLoadStatusView.getStatus() == PlayerLoadStatusViewBase.STATUS_HIDE) {
                mAliyunVodPlayer.pause();
                mBottomView.setPlayPauseStatus(false);
                mLoadStatusView.setMobileNet();
                hideControlView();
            }
        }
    }

    @Override
    public void on4GToWifi() {
        AliLogUtil.v(TAG, "---on4GToWifi---");
        if(mLoadStatusView != null && mLoadStatusView.getStatus() == PlayerLoadStatusViewBase.STATUS_MOBILE_NET){
            mLoadStatusView.setHide();
            stopHideControlView();
            hideControlView();
        }
    }

    @Override
    public void onNetDisconnected() {
        AliLogUtil.v(TAG, "---onNetDisconnected---");
    }


    //预处理界面按钮点击监听接口
    public interface OnIdleBtnListener {
        void onIdleContinueBtnClick();
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
