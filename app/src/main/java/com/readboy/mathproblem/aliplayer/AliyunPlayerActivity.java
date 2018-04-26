package com.readboy.mathproblem.aliplayer;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore.Video;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.readboy.aliyunplayerlib.view.AliPlayerView;
import com.readboy.aliyunplayerlib.view.PlayerCompleteViewDefault;
import com.readboy.aliyunplayerlib.view.PlayerIdleViewBase;
import com.readboy.mathproblem.R;
import com.readboy.mathproblem.activity.StudyActivity;
import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.application.SubjectType;
import com.readboy.mathproblem.bean.ProjectParcelable;
import com.readboy.mathproblem.cache.CacheEngine;
import com.readboy.mathproblem.cache.ProjectEntityWrapper;
import com.readboy.mathproblem.http.response.VideoInfoEntity;
import com.readboy.mathproblem.util.FileUtils;
import com.readboy.mathproblem.util.Lists;
import com.readboy.mathproblem.util.NetworkUtils;
import com.readboy.mathproblem.util.ToastUtils;
import com.readboy.mathproblem.video.db.VideoDatabaseInfo;
import com.readboy.mathproblem.video.db.VideoInfoDatabaseProxy;
import com.readboy.mathproblem.video.dreamplayer.RequestPermissionsActivity;
import com.readboy.mathproblem.video.movie.MediaButtonIntentReceiver;
import com.readboy.mathproblem.video.movie.VideoExtraNames;
import com.readboy.mathproblem.video.movie.VideoMessage;
import com.readboy.mathproblem.video.proxy.VideoProxy;
import com.readboy.mathproblem.video.resource.IVideoResource;
import com.readboy.mathproblem.video.resource.LocalPathVideoResource;
import com.readboy.mathproblem.video.resource.VidVideoResource;
import com.readboy.mathproblem.video.tools.MyTrafficStatus;
import com.readboy.mathproblem.video.tools.UriProxy;
import com.readboy.mathproblem.video.tools.Utils;
import com.readboy.mathproblem.video.tools.WakeUtil;
import com.readboy.mathproblem.video.view.CustomProgressDialog;
import com.readboy.mathproblem.widget.LineItemDecoration;
import com.readboy.recyclerview.CommonAdapter;
import com.readboy.recyclerview.MultiItemTypeAdapter;
import com.readboy.recyclerview.base.ViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static com.readboy.mathproblem.video.proxy.MovieFile.saveVideoInfo;
import static com.readboy.mathproblem.video.tools.NetWorkAnalyst.getNetworkType;

public class AliyunPlayerActivity extends FragmentActivity implements VideoExtraNames, OnClickListener,
        PlayerLoadStatusView.OnCompletedListener {

    public static final String ACTION_APPSWITCH = "com.readboy.switchapp";
    public static final String ACTION_TIP = "tip.infomation";

    private static final int BRIGHT_CONTROL_FACTOR = 600;

    private static final int MAX_DIRDEPTH = 15;
    private static final String TAG = "oubin_AliyunPlayerAct";

    public static boolean onStopped = false;

    boolean isInit = false;
    private boolean mIsPlayUrl = true;
    private boolean mInHiding = false;
    private boolean mInShowing = false;
    private boolean mRmvbPauseByUser;
    private boolean mPlayOnce = false;
    private boolean mPlayPrepared = false;
    private boolean mButtonCanClick = true;
    private boolean mUserThinkNetworkAvailable = true;
    private boolean hasRegisterReceiver = false;
    private int mFinishType;
    private boolean isAdjustVoiceOrBrightness;

    /**
     * 是否解锁
     */
    private boolean mUserPresent = false;
    private boolean finishOnce = false;

    private boolean mExit = true;
    private boolean mNetWorkSpeedShow = true;

    private int mIndex = 0;
    private static final int HIDE_CONTROLLER_DELAY_DURATION = 10_000;
    private long mDuration = 0;
    private int mNetworkType = ConnectivityManager.TYPE_WIFI;
    private int sScreenLeft = 0;
    private int sScreenRight = 0;
    private int sScreenHeight = 0;

    private float downX = 0, moveX;
    private float downY = 0, moveY;
    private long mPosition = -1;

    private IVideoResource mVideoResource;

    private TextView percentTextView = null;
    private ImageView mGestureController = null;

    private AliPlayerView mPlayerView;
    private PlayerBottomView mBottomView;
    private PlayerTopView mTopView;
    private PlayerCompleteViewDefault mCompleteView;
    private PlayerLoadStatusView mLoadingView;

    /**
     * 视频列表相关
     */
    private RecyclerView mVideoRv;
    private CommonAdapter mVideoAdapter;

    /**
     * 视频是否 正在重新缓存中
     */
    private boolean isBuffering = false;
    private VideoDatabaseInfo mVideoDbInfo = new VideoDatabaseInfo();
    CustomProgressDialog mPD = null;
    AlertDialog mNetworkDialog = null;

    private AudioManager audioManager;
    private RemoteControlClient mRemoteControlClient = null;

    private MyTrafficStatus mTrafficStatus = null;

    private final ArrayList<IVideoResource> mVideoResourceList = new ArrayList<>();
    private boolean hasRequestAudioFocus = false;

    /**
     * 监听电话状态
     */
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                // 响铃
                mPlayerView.onPause();
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                //  接听

            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                // 空闲

            }
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        //		final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "receiving an action:" + intent.getAction());
            String action = intent.getAction();
            if (action == null) {
                return;
            }

            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION) && mIsPlayUrl) {
                handleNetwork();
            }
            if (action.equals(Intent.ACTION_USER_PRESENT)) {
                mUserPresent = true;
            }

            switch (action) {
                case Intent.ACTION_MEDIA_REMOVED:
                case Intent.ACTION_MEDIA_EJECT:
                case Intent.ACTION_MEDIA_UNMOUNTED:
                    if (mVideoResource != null) {
                        String path = mVideoResource.getVideoUri().getPath();
                        if (path.startsWith("/storage/emulated/0") || path.startsWith("storage/emulated/0")) {
                        } else {
                            if (!finishOnce && path.contains(intent.getData().getPath())) {
                                Log.e(TAG, "card removed.");
                                finishOnce = true;
                                Toast.makeText(AliyunPlayerActivity.this, "正在播放的文件不存在！", Toast.LENGTH_LONG).show();
                                finish();
                            }

                        }
                    }
                    break;
                case Intent.ACTION_BATTERY_LOW:
                    Log.e(TAG, "battery low.");
                    //finishOnce = true;
                    //finish();
                    break;
                case ACTION_APPSWITCH:
                    finishOnce = true;
                    mExit = true;
                    finishMyself();
                    break;
                case Intent.ACTION_CAMERA_BUTTON:
                case "com.android.intent.action.CAMERA_BUTTON":
                    Log.e(TAG, "camera come");
                    mPlayerView.onDestroy();
                    unregisterReceiver();
//                        System.exit(0);
                    finishMyself();
                    break;
                case Intent.ACTION_CLOSE_SYSTEM_DIALOGS:
                    String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                    if (reason != null) {
                        Log.e(TAG, "action:" + action + ",reason:" + reason);

                        if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                            Log.d(TAG, "home come 1");
                            mExit = false;
                            finishMyself();
                        } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                            Log.d(TAG, "home come 2");
                            mExit = false;
                            finishMyself();
                        }
                    }
                    break;
                case MediaButtonIntentReceiver.ACTION_PAUSE:
                    if (mPlayPrepared && !onStopped) {
                        videoViewPause();
                    }
                    break;
                case MediaButtonIntentReceiver.ACTION_START_PAUSE:
                    if (mPlayPrepared && !onStopped) {
                        if (mRmvbPauseByUser) {
                            videoViewStart();
                        } else {
                            videoViewPause();
                        }
                    }
                    break;
                case ConnectivityManager.CONNECTIVITY_ACTION:
                    Log.e(TAG, "onReceive: connectivity change.");
                    break;
                default:
                    Log.e(TAG, "handleMessage: default action = " + action);
            }
        }
    };

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
//            Log.e(TAG, "handleMessage: message = " + message.what);
            if (onStopped) {
                return;
            }
            switch (message.what) {
                case VideoMessage.READY:
                    Log.e(TAG, "VideoMessage.READY, mIsPlayUrl = " + mIsPlayUrl
                            + ", mUserThinkNetworkAvailable = " + mUserThinkNetworkAvailable);
                    if (mUserThinkNetworkAvailable) {
                        updatePlay();
                    }
                    break;
                case VideoMessage.HEART:
                    if (mNetWorkSpeedShow && mTrafficStatus != null) {
                        if (mPD != null) {
                            String speeds = mTrafficStatus.getRxSpeed(AliyunPlayerActivity.this);
//                            Log.e(TAG, "handleMessage: speeds = " + speeds);
                            if (speeds != null) {
                                mPD.setMessage(speeds);
                            }
                        }
                    }
                    break;
                case VideoMessage.NOOP:
                    if (mPlayerView.isPlaying()) {
                        showController(false);
                    } else {
                        Log.e(TAG, "handleMessage: no playing");
                    }
                    break;
                case VideoMessage.ERROR:
                    Log.e(TAG, "handleMessage: video error.");
                    if (onStopped) {
                        return;
                    }
                    Toast.makeText(AliyunPlayerActivity.this, (String) message.obj, Toast.LENGTH_LONG).show();
                    finish();
                    break;
                default:
                    Log.e(TAG, "handleMessage: default = " + message.what);
                    break;
            }
        }
    };

    private AnimationListener mDismissAnimListener = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
            if (mInShowing) {
                mTopView.setVisibility(View.VISIBLE);
                mBottomView.setVisibility(View.VISIBLE);
                mVideoRv.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mInHiding) {
                mTopView.setVisibility(View.GONE);
                mBottomView.setVisibility(View.GONE);
                mVideoRv.setVisibility(View.GONE);
                mInHiding = false;
            }
            if (mInShowing) {
                hideControllerDelayed();
                mInShowing = false;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "---- onCreate start Build.VERSION.SDK_INT: " + Build.VERSION.SDK_INT);
        super.onCreate(savedInstanceState);
        boolean permission = false;
        if (RequestPermissionsActivity.requiredPermission(this)) {
            Log.e(TAG, "onCreate: required permission.");
            finish();
        } else {
            Log.e(TAG, "onCreate: permission true.");
            permission = true;
        }

        initWindow();
//		Vitamio.isInitialized(getApplicationContext());
        isInit = true;

        mRemoteControlClient = MediaButtonIntentReceiver.registerMediaButton(this);

        setContentView(R.layout.activity_aliyun_player);
        registerReceiver();
        assignView();
//        initClickEvent();

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (permission) {
            if (!parseIntent(getIntent())) {
                return;
            }
            initMovieActivity();
            mHandler.sendEmptyMessage(VideoMessage.READY);
        }

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    private void initWindow() {
//        setSystemUiVisibility(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ActivityCompat.getColor(this, R.color.play_controller));
        }
//        hideNavigationBar();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        sScreenLeft = metrics.widthPixels / 4;
        sScreenRight = metrics.widthPixels * 3 / 4;
        sScreenHeight = metrics.heightPixels;
        Log.e(TAG, "initWindow: left = " + sScreenLeft + ", height = " + sScreenHeight);
    }

    private void registerReceiver() {
        if (hasRegisterReceiver) {
            return;
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addDataScheme("file");
        registerReceiver(mReceiver, filter);

        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        registerReceiver(mReceiver, filter);

        filter = new IntentFilter();
        filter.addAction(ACTION_APPSWITCH);
        filter.addAction(ACTION_TIP);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mReceiver, filter);

        filter = new IntentFilter();
        filter.addAction("com.android.intent.action.CAMERA_BUTTON");
        filter.addAction(Intent.ACTION_CAMERA_BUTTON);
        registerReceiver(mReceiver, filter);

//        filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter = new IntentFilter();
        filter.addAction(MediaButtonIntentReceiver.ACTION_PAUSE);
        filter.addAction(MediaButtonIntentReceiver.ACTION_START);
        filter.addAction(MediaButtonIntentReceiver.ACTION_START_PAUSE);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(mReceiver, filter);
        hasRegisterReceiver = true;
    }

    private void unregisterReceiver() {
        Log.e(TAG, "unregisterReceiver: ");
        if (hasRegisterReceiver) {
            unregisterReceiver(mReceiver);
            hasRegisterReceiver = false;
        }
    }

    private void assignView() {
        mTopView = new PlayerTopView(this);
        mTopView.setUnitOnClickListener(this);
        mBottomView = new PlayerBottomView(this);
        mBottomView.setUnitClickListener(this);
        mCompleteView = new PlayerCompleteViewDefault(this);
        mCompleteView.getContinueView().setOnClickListener(this);
        mCompleteView.getCancelView().setOnClickListener(this);
        mLoadingView = new PlayerLoadStatusView(this);
        mLoadingView.setOnCompletedListener(this);
        mLoadingView.setUnitOnClickListener(this);
//        mLoadingView = new PlayerLoadStatusViewDefault(this);
        mPlayerView = (AliPlayerView) findViewById(R.id.ali_player_view);
        mPlayerView.init(mTopView, mBottomView, mLoadingView, null, mCompleteView);
        //保持屏幕常亮，一般播放界面建议设置常亮，特殊情况自行考虑。选用
        mPlayerView.setKeepScreenOn(true);

        //打开阿里打印，即阿里播放器底层相关的打印，前期测试阶段建议打开。已改到Application中设置了。选用
        mPlayerView.enableNativeLog();

        mGestureController = (ImageView) findViewById(R.id.voice_or_bright);
        percentTextView = (TextView) findViewById(R.id.percent);

        initRecyclerView();
    }

    private void initRecyclerView() {
//        mVideoListParent = (LinearLayout) findViewById(R.id.video_list_parent);
//        LayoutTransition transition = new LayoutTransition();
//        transition.setDuration(200);
//        transition.setAnimator(LayoutTransition.APPEARING, null);
//        transition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, null);
//        mVideoListParent.setLayoutTransition(transition);
        mVideoRv = (RecyclerView) findViewById(R.id.small_player_video_list);
        mVideoRv.setLayoutManager(new LinearLayoutManager(this));
        mVideoRv.addItemDecoration(new LineItemDecoration(LinearLayout.VERTICAL, 1, Color.GRAY));
        mVideoAdapter = new CommonAdapter<IVideoResource>(this, R.layout.item_video_player, mVideoResourceList) {
            @Override
            protected void convert(ViewHolder holder, IVideoResource resource, int position) {
                TextView textView = (TextView) holder.itemView.findViewById(R.id.player_video_name);
                textView.setText(resource.getVideoName());
                if (mIndex == position) {
                    textView.setSelected(true);
                    textView.setMarqueeRepeatLimit(-1);
                    textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    textView.setFocusable(true);
                } else {
                    textView.setSelected(false);
                    textView.setMarqueeRepeatLimit(1);
                    textView.setEllipsize(TextUtils.TruncateAt.END);
                    textView.setFocusable(false);
                }
            }
        };
        mVideoAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
                Log.e(TAG, "onItemClick: position = " + position);
                if (mIndex == position) {
                    return;
                }
//                showProgressDialog();
                saveCurrentPosition();
                videoViewPauseOrStop();
                mIndex = position;
                mPosition = 0;
                updateCurrentVideoResource(mVideoResourceList.get(mIndex));
                mRmvbPauseByUser = false;
                updatePlay();
                hideVideoList();
                hideControllerDelayed();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
                return false;
            }
        });
        mVideoRv.setAdapter(mVideoAdapter);
        Log.e(TAG, "initRecyclerView: count = " + mVideoRv.getChildCount());
    }

    private void initClickEvent() {
        int[] resIds = {R.id.player_back, R.id.player_player, R.id.player_pause, R.id.player_exercise, R.id.fullscreen, R.id.player_video_list_switch};
        for (int resId : resIds) {
            View v = findViewById(resId);
            if (v != null) {
                v.setOnClickListener(this);
            }
        }
    }

    /**
     * 读书郎定制系统无需这样，只需在AndroidManifest对应Activity
     * 添加<action android:name="android.readboy.FLAG_HIDE_SYSTEMBAR" />
     * 在4.4系统可能会导致无法下拉通知栏。特别是0x02000000.
     */
    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//        getWindow().setFlags(0x02000000, 0x02000000); //隐藏系统条
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //TODO 关闭权限重新进入，需要重新初始化。
        Log.d(TAG, "---- onNewIntent intent: " + intent);
        if (intent != null) {
            String newPath = intent.getStringExtra("path");
            if (TextUtils.isEmpty(newPath)) {
                newPath = intent.getStringExtra("url");
            }
            if (TextUtils.isEmpty(newPath)) {
                Uri uri = intent.getData();
                if (uri != null) {
                    if (uri.toString().startsWith("file://")) {
                        newPath = uri.getPath();
                    } else {
                        newPath = uriParse(uri).getPath();
                    }
                }
            }
            if (TextUtils.isEmpty(newPath)) {
                ArrayList<String> lst = intent.getStringArrayListExtra("medialist");
                int index = intent.getIntExtra("index", 0);
                if (lst != null && lst.size() > index) {
                    newPath = lst.get(index);
                    updateVideoPath(lst);
                    mIndex = index;
                    long position = intent.getLongExtra("position", 0);
                    mPosition = (int) position;
                    Log.d(TAG, "---- onNewIntent mPosition: " + mPosition + ", postion: " + position);
                }
            }

            if (!TextUtils.isEmpty(newPath)) {
                Log.d(TAG, "---- onNewIntent newPath: " + newPath);
                parseIntent(intent);
                initMovieActivity();
                mRmvbPauseByUser = false;
                mHandler.sendEmptyMessage(VideoMessage.READY);
                cancelAutoHide();
            }
        }
    }

    private void updateVideoPath(ArrayList<String> pathList) {
        if (Lists.isEmpty(pathList)) {
            return;
        }
        for (String path : pathList) {
            mVideoResourceList.add(new LocalPathVideoResource(path));
        }
//        mVideoRv.post(() -> ViewUtils.setSelectedPosition(mIndex, R.id.player_video_name, mVideoRv));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e(TAG, "onConfigurationChanged: ");
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        Log.e(TAG, "onConfigurationChanged: point = " + point.toString());
        sScreenLeft = point.x / 4;
        sScreenRight = point.x * 3 / 4;
        sScreenHeight = point.y;

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
        onStopped = false;

//        keepScreenOn();

        hideControllerDelayed();
        WakeUtil.acquireCpuWakeLock(this);
//        if (mHearThread != null) {
        Log.e(TAG, "onResume: pause hear thread.");
//            mHearThread.pause();
//            mHearThread = null;
//        }
//        mHearThread = new HeartThread(mHandler);
//        mHearThread.start();
        if (!mUserThinkNetworkAvailable && mIsPlayUrl) {
            handleNetwork();
        }
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        mUserPresent = !keyguardManager.inKeyguardRestrictedInputMode();

        if (mIsPlayUrl) {
            mTrafficStatus = new MyTrafficStatus(this);
        }
        Log.e(TAG, "onResume: mButtonCanClick = " + mButtonCanClick + ", state = " + mPlayerView.getPlayerState());
        if (!mButtonCanClick) {
//                showProgressDialog();
        }
        Log.d(TAG, "-------- onResume ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
        cancelAutoHide();
        onStopped = true;
        if (isSaveState()) {
            mPosition = (int) Math.max(0, mPlayerView.getCurrentPosition() - 1200);
            Log.e(TAG, "onPause: mPosition = " + mPosition);
            mDuration = mPlayerView.getDuration();
        }

        videoViewPause();

        //解决加载中，按电源键系统到时的问题。
        dismissProgressDialog();
        mButtonCanClick = true;

//        mHearThread.pause();
//        mHearThread = null;
        Log.d(TAG, "-------- onPause mPosition: " + mPosition + ", string: " + Utils.formatTime(mPosition));

        WakeUtil.releaseCpuLock();
        saveCurrentPosition();

        disappearView();
        if (mIsPlayUrl) {
            mTrafficStatus = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e(TAG, "-------- onDestroy");

        if (isInit) {
            Log.e(TAG, "-------- unregisterReceiver(mReceiver)");
            unregisterReceiver();
        }

        dismissProgressDialog();
        cancelAutoHide();

        if (mNetworkDialog != null) {
            mNetworkDialog.dismiss();
            mNetworkDialog = null;
        }

        if (mRemoteControlClient != null) {
            MediaButtonIntentReceiver.unregisterMediaButton(this, mRemoteControlClient);
        }

        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        manager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        mPhoneStateListener = null;

        if (mExit) {
//            Log.e(TAG, "-------- System.exit(0) ");
//            System.exit(0);
        }

        if (mPlayerView != null) {
            mPlayerView.setKeepScreenOn(false);
            mPlayerView.onDestroy();
            mPlayerView = null;
        }
        mTopView = null;
        mBottomView = null;
        mCompleteView = null;

        MathApplication.refWatch(this);
    }

    @Override
    public void finish() {
        super.finish();
        Log.e(TAG, "finish: ");
        unregisterReceiver();
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "-------- onBackPressed ");
        finishMyself();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState: ");
    }

    private void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void clearScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static String getFileName(String path) {
        int start = path.lastIndexOf("/");
        int end = path.lastIndexOf(".");
        if (end != -1) {
            return path.substring(start + 1, end);
        } else {
            return path;
        }
    }

    private void hideControllerDelayed() {
        Log.e(TAG, "hideControllerDelayed: ");
        mHandler.removeMessages(VideoMessage.NOOP);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(VideoMessage.NOOP), HIDE_CONTROLLER_DELAY_DURATION);
    }

    public void cancelAutoHide() {
        Log.e(TAG, "cancelAutoHide: ");
        mHandler.removeMessages(VideoMessage.NOOP);
    }

    public IVideoResource getNextMediaPath(boolean increase) {
        if (increase) {
            if (++mIndex >= mVideoResourceList.size()) {
                mIndex = 0;
            }
        } else {
            if (--mIndex < 0) {
                mIndex = mVideoResourceList.size() - 1;
            }
        }
        Log.i(TAG, "-------- getNextMediaPath index: " + mIndex);
        return mVideoResourceList.get(mIndex);
    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, "onClick: v = " + v.getId());
        if (mInHiding) {
            mTopView.clearAnimation();
            mBottomView.clearAnimation();
            mVideoRv.clearAnimation();
            mInHiding = false;
        }
        hideControllerDelayed();
        switch (v.getId()) {
            case R.id.player_back:
                mPlayPrepared = false;
                finishMyself();
                break;
            case R.id.next:
//                next();
                break;
            case R.id.prev:
//                previous();
                break;
            case R.id.player_player:
                Log.e(TAG, "onClick: play mButtonCanClick = " + mButtonCanClick);
                if (mButtonCanClick) {
                    videoViewStart();
                }
                hideControllerDelayed();
                break;
            case R.id.player_pause:
                Log.e(TAG, "onClick: pause mButtonCanClick = " + mButtonCanClick);
                if (mButtonCanClick) {
                    videoViewPause();
                }
                break;
            case R.id.video_container:
            case R.id.surface_view:
                showController(!isControllerShowing());
                hideControllerDelayed();
                break;
            case R.id.player_exercise:
                gotoExerciseActivity();
                break;
            case R.id.fullscreen:
                if (mFinishType == TYPE_GOTO) {
                    gotoStudyActivity();
                } else {
                    finishMyself();
                }
                break;
            case R.id.player_complete_btn_cancel:
                finishMyself();
                break;
            case R.id.player_complete_btn_continue:
                replay();
                break;
            case R.id.player_video_list_switch:
                if (mVideoRv.getVisibility() == View.VISIBLE) {
                    hideVideoList();
                } else {
                    showVideoList();
                }
                break;
            case R.id.load_status_view_btn_continue:

                break;
            default:
                Log.e(TAG, "onClick: other view click = " + v.getId());
                break;
        }
    }

    @Override
    public void onCompleted() {
        int size = mVideoResourceList.size();
        Log.e(TAG, "onCompleted: size = " + size);
        if (mIndex < size - 1) {
            next();
        } else {
            mLoadingView.showFinishView();
        }
    }


    private void showVideoList() {
        Log.e(TAG, "showVideoList: ");
        mVideoRv.setVisibility(View.VISIBLE);
//        mVideoRv.post(() -> mSelectedVideoNameTv = ViewUtils.setSelectedPosition(mIndex, R.id.player_video_name, mVideoRv));
    }

    private void hideVideoList() {
        mVideoRv.setVisibility(View.GONE);
    }

    private void previous() {
        if (!mButtonCanClick) {
            return;
        }
        saveCurrentPosition();

        if (mIndex > 0) {
            mVideoResource = getNextMediaPath(false);
            mPosition = 0;
            mPlayerView.seekTo(mPosition);
            mRmvbPauseByUser = false;
            mHandler.sendEmptyMessage(VideoMessage.READY);
            cancelAutoHide();
        } else {
            Toast.makeText(this, "没有多余的视频了", Toast.LENGTH_LONG).show();
        }
    }

    private void next() {
//        if (!mButtonCanClick) {
//            return;
//        }
        saveCurrentPosition();
        if (mIndex < mVideoResourceList.size() - 1) {
            updateCurrentVideoResource(getNextMediaPath(true));
            mPosition = 0;
            mRmvbPauseByUser = false;
            cancelAutoHide();
            updatePlay();
        } else {
            Toast.makeText(this, "没有多余的视频了", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 重新播放
     */
    private void replay() {
        mIndex = 0;
        mPosition = 0;
        updateCurrentVideoResource(mVideoResourceList.get(0));
        updatePlay();
    }

    /**
     * 显示控制界面
     *
     * @param isShow 是否显示，下一个状态
     */
    public void showController(boolean isShow) {
        Log.e(TAG, "showController() called with: isShow = " + isShow + "");
        if (isControllerShowing()) {
            if (isShow) {
                Log.e(TAG, "showController: is showing.");
                hideControllerDelayed();
                return;
            }
        } else {
            if (!isShow) {
                Log.d(TAG, "showController: is hided.");
                return;
            }
        }
        if (isShow) {
            if (!mInShowing && !mInHiding && !isControllerShowing()) {
                //显示
                Animation ani1 = AnimationUtils.loadAnimation(this, R.anim.push_down);
                Animation ani2 = AnimationUtils.loadAnimation(this, R.anim.push_up);
                Animation ani3 = AnimationUtils.loadAnimation(this, R.anim.push_right);
                ani1.setAnimationListener(mDismissAnimListener);
                mInShowing = true;
                mTopView.startAnimation(ani1);
                mBottomView.startAnimation(ani2);
                mVideoRv.startAnimation(ani3);
            }
        } else {
            if (!mInShowing && !mInHiding && isControllerShowing()) {
                //隐藏
                mVideoRv.setVisibility(View.GONE);
                Animation ani1 = AnimationUtils.loadAnimation(this, R.anim.pull_up);
                Animation ani2 = AnimationUtils.loadAnimation(this, R.anim.pull_down);
                Animation animation3 = AnimationUtils.loadAnimation(this, R.anim.pull_right);
                ani1.setAnimationListener(mDismissAnimListener);
                mInHiding = true;
                mTopView.startAnimation(ani1);
                mBottomView.startAnimation(ani2);
                mVideoRv.startAnimation(animation3);

            }
        }
//        setSystemUiVisibility(false);
    }

    public void onStorageStateChanged(String path, String oldState, String newState) {
        Log.e(TAG, "path:" + path + ", state:" + newState);

        switch (newState) {
            case Environment.MEDIA_SHARED:
                break;
            case Environment.MEDIA_CHECKING:
                break;
            case Environment.MEDIA_MOUNTED:
                break;
            case Environment.MEDIA_REMOVED:
                break;
            default:
                Log.e(TAG, "onStorageStateChanged: other = newState = " + newState);
                break;
        }
    }

    public boolean isControllerShowing() {
        return mTopView.getVisibility() == View.VISIBLE || mBottomView.getVisibility() == View.VISIBLE;
    }

    private void showProgressDialog() {
//        Log.e(TAG, "showProgressDialog: ");
//        cancelAutoHide();
//        if (mNetworkDialog != null && mNetworkDialog.isShowing()) {
//            Log.e(TAG, "showProgressDialog: network dialog showing");
//            return;
//        }
//
//        createProgressDialog();
//        mPD.show();
//        mPD.setCancelClickListener(v -> {
//            mPlayerView.onStop();
//            mPlayPrepared = false;
//            finishMyself();
//        });
//        mNetWorkSpeedShow = true;
    }

    private void dismissProgressDialog() {
        Log.e(TAG, "dismissProgressDialog: ");
        if (mPD != null) {
            mPD.dismiss();
            mPD = null;
        }
        mNetWorkSpeedShow = false;
    }

    private boolean showNetworkSpeed() {
        return mPD != null && mPD.isShowing();
    }

    /**
     * 监听屏幕点击，控制亮度和音量；
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "onTouchEvent: action down.");
                isAdjustVoiceOrBrightness = false;
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = event.getX();
                moveY = event.getY();
                if (downY > sScreenHeight / 4) {
                    float delY = moveY - downY;
                    if (Math.abs(delY) > 5) {
                        if (downX > sScreenRight) {
                            WindowManager.LayoutParams lpLayoutParams = getWindow().getAttributes();
                            //屏幕变亮
                            if (lpLayoutParams.screenBrightness < 0.0f) {
                                try {
                                    int systemScreenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                                    lpLayoutParams.screenBrightness = systemScreenBrightness / 255.0f;
                                } catch (Settings.SettingNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            lpLayoutParams.screenBrightness = lpLayoutParams.screenBrightness
                                    + (downY - moveY) / BRIGHT_CONTROL_FACTOR;
                            if (lpLayoutParams.screenBrightness > 1.0f) {
                                lpLayoutParams.screenBrightness = 1.0f;
                            } else if (lpLayoutParams.screenBrightness < 0.0f) {
                                lpLayoutParams.screenBrightness = 0f;
                            }
                            getWindow().setAttributes(lpLayoutParams);
                            isAdjustVoiceOrBrightness = true;
                            showVolOrBri(false, (lpLayoutParams.screenBrightness));
                            downY = moveY;
                        } else if (downX < sScreenLeft) {
                            if (Math.abs(delY) < 2) {
                                break;
                            }
                            if (delY < 0) {
                                //声音变大
                                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
                            } else {
                                //声音变小
                                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
                            }
                            isAdjustVoiceOrBrightness = true;
                            showVolOrBri(true, (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / 15.0f));
                            downY = moveY;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "onTouchEvent: isAdjustVoiceOrBrightness = " + isAdjustVoiceOrBrightness);
                if (!isAdjustVoiceOrBrightness) {
                    showController(!isControllerShowing());
                }
                disappearView();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void seekComplete() {
        Log.e(TAG, "-------- seekComplete isBuffering = " + isBuffering);
//        if (!isBuffering) {
//            dismissProgressDialog();
//            hideControllerDelayed();
//        }
    }


    public void prepare() {
        Log.e(TAG, "prepare: ");
        mButtonCanClick = true;
        mPlayPrepared = true;
        isBuffering = false;
        requestAudioFocusTransient();
        Log.e(TAG, "prepare: seek to " + mPosition);
//            mFragment.seekTo(mPosition);
        Log.d(TAG, "-------- prepare mPosition: " + mPosition);
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "onError: what = " + what + ", extra = " + extra);
        Log.e(TAG, "onError: before mPosition = " + mPosition);
        mPosition = (int) mPlayerView.getCurrentPosition();
        Log.e(TAG, "onError: after mPosition = " + mPosition);
        dismissProgressDialog();
        mButtonCanClick = true;
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                if (extra == MediaPlayer.MEDIA_ERROR_IO) {
                    finishMyself();
                }
                break;
            default:
                Log.e(TAG, "onError: default: what = " + what + ", extra = " + extra);
        }
        return true;
    }

    public void handleNetwork() {
        int networkType = getNetworkType(this);
        Log.e(TAG, "handleNetwork: networkType = " + networkType);
        mUserThinkNetworkAvailable = networkType == ConnectivityManager.TYPE_WIFI;
        if (mNetworkType == networkType) {
            return;
        }

        if (mNetworkDialog != null && mNetworkDialog.isShowing()) {
            mNetworkDialog.dismiss();
        }
        switch (networkType) {
            case -1: //无网络
                if (isSaveState()) {
                    mPosition = (int) mPlayerView.getCurrentPosition();
                    Log.e(TAG, "handleNetwork: mPosition = " + mPosition);
                }
                videoViewPause();
                if (mIsPlayUrl) {
                    Log.e(TAG, "handleNetwork: mIsPlayUrl = " + mIsPlayUrl);
                    showNoNetworkWarningDialog();
                }
                break;
            case ConnectivityManager.TYPE_WIFI:
                Log.e(TAG, "handleNetwork: ");
                ToastUtils.showShort(this, "已连接上wifi网络了！");
//                videoViewStart();
                mRmvbPauseByUser = false;
                mHandler.sendEmptyMessage(VideoMessage.READY);
                break;
            default:
                //非wifi，使用流量提示
                mPlayerView.onPause();
                mPosition = (int) mPlayerView.getCurrentPosition();
                showNotUseWifiWarningDialog();
                break;
        }
        mNetworkType = networkType;
    }

    private void showNotUseWifiWarningDialog() {
        final Builder builder = new Builder(this);
        builder.setTitle("温馨提示");
        builder.setPositiveButton("退出", (dialog, which) -> finishMyself());
        builder.setOnCancelListener(dialog -> finishMyself());
        final int preNetworkType = mNetworkType;
        builder.setMessage("当前为非wifi情况下, 要继续播放吗？");
        builder.setNegativeButton("继续", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUserThinkNetworkAvailable = true;
                if (preNetworkType == -1) {
                    playVideo();
                    Log.e(TAG, "DialogInterface onClick: seek to " + mPosition);
                }
                if (!mPlayPrepared) {
                    mHandler.sendEmptyMessage(VideoMessage.READY);
                } else {

                }
            }
        });
        mNetworkDialog = builder.create();
        mNetworkDialog.setCanceledOnTouchOutside(false);
        mNetworkDialog.show();
    }

    private void showNoNetworkWarningDialog() {
        final Builder builder = new Builder(this);
        builder.setTitle("温馨提示");
        builder.setPositiveButton("退出", (dialog, which) -> finishMyself());
        builder.setOnCancelListener(dialog -> finishMyself());
        builder.setMessage("没有连接上网络，要退出视频！");
        mNetworkDialog = builder.create();
        if (mNetworkDialog.getWindow() != null) {
            mNetworkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        mNetworkDialog.setCanceledOnTouchOutside(false);
        mNetworkDialog.show();
    }

    private void saveCurrentPosition() {
        if (mPlayerView != null) {
            save(mVideoResource.getVideoName(), mPlayerView.getCurrentPosition(), mPlayerView.getDuration());
        }
    }

    private void save(String path, long curPos, long duration) {
        long size = 0;
        if (mIsPlayUrl) {

        } else {
//			saveMoviePlayInfo(path, curPos, duration);
            if (path != null) {
                File file = new File(path);
                size = file.length();
            }
        }
        Log.i(TAG, " mV size: " + size);
        saveVideoInfo(mVideoDbInfo.mDataPath, mVideoDbInfo.mDataName, mVideoDbInfo.mDependency, mVideoDbInfo.mType,
                curPos, duration, size, mVideoDbInfo.mCacheFilePath, mVideoDbInfo.mCacheName);
    }

    private Uri uriParse(Uri videoUri) {
        String path = null;
        Cursor c;

        ContentProviderClient mMediaProvider = getContentResolver().acquireContentProviderClient("media");
        if (mMediaProvider == null) {
            return videoUri;
        }
        String[] videoProjection = new String[]{Video.Media.DATA};

		/* valueAt video file */
        try {
            c = mMediaProvider.query(videoUri, videoProjection, null, null, null);
            if (c != null) {
                try {
                    while (c.moveToNext()) {
                        path = c.getString(0);
                    }
                } finally {
                    c.close();
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (SecurityException se) {
            se.printStackTrace();
            ToastUtils.show(se.toString());
        } finally {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mMediaProvider.close();
            } else {
                mMediaProvider.release();
            }
        }

        if (path != null) {
            return Uri.fromFile(new File(path));
        } else {
            return videoUri;
        }
    }

    static public VideoDatabaseInfo initVideoDbInfo(Intent intent, VideoDatabaseInfo datainfo) {
        String dbPath = intent.getStringExtra("databasepath");
        String dbName = intent.getStringExtra("databasename");
        String cachePath = intent.getStringExtra("cachepath");
        String vid = intent.getStringExtra("id");
        if (!TextUtils.isEmpty(dbPath)) {
            datainfo.mDataPath = dbPath;
        }
        if (!TextUtils.isEmpty(dbName)) {
            datainfo.mDataName = dbName;
        }
        if (!TextUtils.isEmpty(cachePath)) {
            datainfo.mCachePath = cachePath;
        }
        Log.i(TAG, "initVideoDbInfo: vid = " + vid);
        if (vid != null) {
            datainfo.mType = VideoInfoDatabaseProxy.TYPE_MICRO;
            datainfo.mDependency = vid;
            datainfo.mCachePath = null;
        }
        return datainfo;
    }

    /**
     * 优选播放path（本地视频），在选择url（网络视频），次之medialist内容
     */
    private boolean parseIntent(Intent intent) {
        mIsPlayUrl = true;
        mFinishType = intent.getIntExtra(EXTRA_FINISH_TYPE, TYPE_SET_RESULT);
        String mPath;
        mPath = intent.getStringExtra(VideoExtraNames.EXTRA_PATH);
        if (TextUtils.isEmpty(mPath)) {
            mPath = intent.getStringExtra(VideoExtraNames.EXTRA_URL);
        }
        Log.e(TAG, "PATH:" + mPath);
        boolean exerciseEnable = intent.getBooleanExtra(VideoExtraNames.EXTRA_EXERCISE_ENABLE, false);
        if (exerciseEnable) {
            mTopView.setViewVisibility(R.id.player_exercise, View.VISIBLE);
        } else {
            mTopView.setViewVisibility(R.id.player_exercise, View.GONE);
        }
        mPosition = intent.getLongExtra(VideoExtraNames.EXTRA_SEEK_POSITION, 0);
        mIndex = intent.getIntExtra(VideoExtraNames.EXTRA_INDEX, 0);

        parseVideoInfoList(intent);
        parsePathList(intent);

        if (!Lists.isEmpty(mVideoResourceList)) {
            mIndex = Math.min(mIndex, mVideoResourceList.size() - 1);
            Log.e(TAG, "parseVideoInfoList: mIndex = " + mIndex);
            updateCurrentVideoResource(mVideoResourceList.get(mIndex));
        }

//        updateStatus(mPath);
        Log.e(TAG, "parseIntent: paths = " + mVideoResourceList.size());
        Log.e(TAG, "parseIntent: mPath = " + mPath + ", \nmIndex = " + mIndex + ", \nseekPosition = " + mPosition);
        return true;
    }

    private boolean parsePathList(Intent intent) {
        String mPath = intent.getStringExtra(VideoExtraNames.EXTRA_PATH);
        Uri uri = intent.getData();
        ArrayList<String> pathList = intent.getStringArrayListExtra(VideoExtraNames.EXTRA_MEDIA_LIST);
        if (pathList == null) {
            pathList = new ArrayList<>();
        }
        boolean result = false;
        boolean listContainPath = false;

        if (!TextUtils.isEmpty(mPath)) {
            int whichIndex = 0;
            for (String path : pathList) {
                if (mPath.equals(path)) {
                    listContainPath = true;
                    mIndex = whichIndex;
                    break;
                }
                whichIndex++;
            }
            if (!listContainPath) {
                pathList.add(mPath);
                mIndex = pathList.indexOf(mPath);
            }
            result = true;
        } else if (mIndex >= 0 && mIndex < pathList.size()) {
            mPath = pathList.get(mIndex);
            result = true;
        } else if (uri != null) {
            if (uri.toString().startsWith("file://")) {
                mPath = uri.getPath();
            } else {
                //"content://"
//				mPath = uriParse(uri).getPath();
                mPath = UriProxy.getPath(this, uri);
            }
            pathList.add(mPath);
            Log.e(TAG, "mPath1=" + mPath);
            mPlayOnce = true;
            result = true;
        }
        Log.e(TAG, "parsePathList: " + pathList.size());
        updateVideoPath(pathList);
        return result;
    }

    private void parseVideoInfoList(Intent intent) {
        ArrayList<VideoInfoEntity.VideoInfo> videoInfos =
                intent.getParcelableArrayListExtra(VideoExtraNames.EXTRA_VIDEO_INFO_LIST);
        if (videoInfos != null && videoInfos.size() > 0) {
            mVideoResourceList.clear();
            for (VideoInfoEntity.VideoInfo videoInfo : videoInfos) {
                mVideoResourceList.add(new VidVideoResource(videoInfo));
            }
        }
    }

    /**
     * 更新因path修改，影响的其他状态
     */
    private void updateCurrentVideoResource(IVideoResource resource) {
        if (resource == null) {
            mIndex = -1;
            return;
        }
        mVideoResource = resource;
        mIsPlayUrl = true;

        Log.e(TAG, "updateCurrentVideoResource: current = " + mVideoResource.getVideoName());
        updateFavoriteAndDownload();
    }

    /**
     * 更新播放控制条中的收藏和下载
     */
    private void updateFavoriteAndDownload() {
        mBottomView.setVideoResource(mVideoResource);
    }

    /**
     * updateSeekBar
     */
    private void updateController() {

    }

    private void updatePlay() {
        Log.e(TAG, "updatePlay() called resource = " + mVideoResource + ", mPosition = " + mPosition);
        mPlayPrepared = false;

        if (mVideoResource == null) {
            return;
        }

        Log.e(TAG, "updatePlay: mButtonCanClick = false.");
        mButtonCanClick = false;

        if (mIsPlayUrl) {
            if (!onStopped) {
                showProgressDialog();
            }
        }

        mTopView.setVideoName(mVideoResource.getVideoName());

        if (mVideoResource instanceof VidVideoResource) {
            if (checkNetwork()) {
                mVideoResource.play(mPlayerView, mPosition);
                mBottomView.setFavoriteVisibility(View.VISIBLE);
                mBottomView.setDownloadVisibility(View.VISIBLE);
            }
        } else {
            mVideoResource.play(mPlayerView, mPosition);
            updateStatusCauseUri();
            mBottomView.setFavoriteVisibility(View.GONE);
            mBottomView.setDownloadVisibility(View.GONE);
        }

//        showController(true);
        Log.e(TAG, "updatePlay: mIndex = " + mIndex);
        mVideoAdapter.notifyDataSetChanged();
    }

    /**
     * 监听是否有网络。
     *
     * @return 是否有网络。无网返回false
     */
    private boolean checkNetwork() {
        if (!NetworkUtils.isConnected(this)) {
            showNoNetworkWarningDialog();
            return false;
        } else {
            return true;
        }
    }

    private void updateStatusCauseUri() {
        Log.e(TAG, "updateStatusCauseUri: mIsPlayUrl = " + mIsPlayUrl + ", mTrafficStatus = " + mTrafficStatus);
        if (mIsPlayUrl) {
            if (mTrafficStatus == null) {
                mTrafficStatus = new MyTrafficStatus(this);
            }
        } else {
            mTrafficStatus = null;
        }
    }

    private void createProgressDialog() {
        Log.e(TAG, "createProgressDialog() called");
        if (mPD == null) {
            mPD = new CustomProgressDialog(this);
//            String speed = mTrafficStatus != null ? mTrafficStatus.getRxSpeed(this) : "0kb/s";
            mPD.setMessage("0KB/s");
        }
    }

    private void showVolOrBri(boolean isVolume, double percent) {
        if (mGestureController != null && percentTextView != null) {
            mGestureController.setVisibility(View.VISIBLE);
            percentTextView.setVisibility(View.VISIBLE);
            if (isVolume) {
                if (percent == 0) {
                    mGestureController.setBackgroundResource(R.drawable.mute);
                    percentTextView.setText("静音");
                } else {
                    mGestureController.setBackgroundResource(R.drawable.volume);
                    percentTextView.setText((int) (percent * 100) + "%");
                }
            } else {
                mGestureController.setBackgroundResource(R.drawable.bright);
                percentTextView.setText((int) (percent * 100) + "%");
            }
        } else {
            Log.e(TAG, "没有初始化imageview和textview");
        }
    }

    private void disappearView() {
        if (mGestureController != null && mGestureController.getVisibility() == View.VISIBLE) {
            mGestureController.setVisibility(View.GONE);
        }
        if (percentTextView != null && percentTextView.getVisibility() == View.VISIBLE) {
            percentTextView.setVisibility(View.GONE);
        }
    }

    private void setSystemUiVisibility(boolean bShow) {
        if (bShow) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // 这个控制下边条隐藏跟显示，布局界面不拉伸
                            //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // 这个是控制下边条隐藏跟显示
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            //| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | 0x00002000);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | 0x00002000);
        }
    }

    /**
     * 播放视频
     */
    private void playVideo() {

    }

    /**
     * 暂停或者停止播放
     * 如果正在缓冲播放内容中，则停止播放器，
     * 其他则暂停。
     */
    private void videoViewPauseOrStop() {
        if (mPlayerView.isPreparing()) {
            videoViewStop();
        } else {
            videoViewPause();
        }
    }

    private void videoViewPause() {
        Log.e(TAG, "videoViewPause: ");
        mRmvbPauseByUser = true;
        mPlayerView.onPause();
//        hideControllerDelayed();
        abandonAudioFocus();
    }

    private void videoViewStop() {
        Log.e(TAG, "videoViewPause: ");
        mRmvbPauseByUser = true;

        mPlayerView.onStop();
        hideControllerDelayed();
        abandonAudioFocus();
    }

    private void videoViewStart() {
        mRmvbPauseByUser = false;
        if (isIdleState()) {
            updatePlay();
        } else if (!mPlayerView.isPlaying()) {
            Log.i(TAG, " videoViewStart mFragment start");
            requestAudioFocusTransient();
            mPlayerView.onResume();
            hideControllerDelayed();

        } else {
        }
    }

    private void finishMyself() {
        Intent intent = new Intent("com.readboy.mathproblem.video.playinfo");
        Log.e(TAG, "-------- finishMyself ");
        if (!onStopped) {
            Log.e(TAG, "finishMyself: mPosition = " + mPosition + ", getCurrentPosition = " + mPlayerView.getCurrentPosition());
            mPosition = (int) mPlayerView.getCurrentPosition();
            mDuration = mPlayerView.getDuration();
            Log.e(TAG, "finishMyself: position = " + mPosition + ", mIndex = " + mIndex);
            intent.putExtra(EXTRA_VIDEO_RESOURCE, mVideoResource);
            intent.putExtra(EXTRA_SEEK_POSITION, mPosition);
            intent.putExtra(EXTRA_DURATION, mDuration);
            intent.putExtra(EXTRA_INDEX, mIndex);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    private void gotoStudyActivity() {
        Intent intent = new Intent(this, StudyActivity.class);
        ProjectEntityWrapper wrapper = CacheEngine.getCurrentProjectWrapper();
        if (wrapper == null) {
            ToastUtils.showShort(this, "无法跳转到名师辅导界面");
            finish();
            return;
        }
        int position = CacheEngine.getCurrentIndex();
        int id = wrapper.getProjectList().get(position).getId();
        ProjectParcelable parcelable = new ProjectParcelable(
                position,
                id,
                wrapper.getType().ordinal(),
                wrapper.getGrade());
        updatePosition();
        parcelable.setVideoIndex(mIndex);
        parcelable.setSeekPosition((int) mPosition);
        intent.putExtra(ProjectParcelable.EXTRA_PROJECT_PARCELABLE, parcelable);
        intent.putExtra(EXTRA_FINISH_TYPE, TYPE_GOTO);

        Log.e(TAG, "gotoStudyActivity: parcelable = " + parcelable.toString());
        startActivity(intent);
        finish();
    }

    private void initMovieActivity() {
    }

    private void gotoExerciseActivity() {
        Intent intent = getIntent();
        //解决关闭权限再进入问题。
        if (intent != null) {
            SubjectType type = (SubjectType) intent.getSerializableExtra(EXTRA_PROJECT_SUBJECT);
            if (type != null) {
                int grade = intent.getIntExtra(EXTRA_PROJECT_GRADE, -1);
                int index = intent.getIntExtra(EXTRA_PROJECT_INDEX, -1);
                Log.e(TAG, "gotoExerciseActivity: subject= " + type + ", grade = " + grade
                        + ", index = " + index);
                if (grade != -1 && index != -1) {

                    ProjectEntityWrapper wrapper = CacheEngine.getProject(type, grade);
                    if (wrapper != null) {
                        CacheEngine.setCurrentProjectWrapper(index, wrapper);
                    }
                }
            }
        }
        VideoProxy.gotoExerciseActivity(this);
    }

    private int requestAudioFocusTransient() {
        Log.d(TAG, "requestAudioFocusTransient: ");
        if (hasRequestAudioFocus) {
            Log.e(TAG, "requestAudioFocusTransient: has not request audio focus.");
            return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
        }
        AudioManager sAudioManager =
                (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (sAudioManager != null) {
            int ret = sAudioManager.requestAudioFocus(mAudioFocusChangeListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            Log.e(TAG, "requestAudioFocusTransient: ret = " + ret);
            if (ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                hasRequestAudioFocus = true;
            } else {
                Log.e(TAG, "requestAudioFocus fail: ret = " + ret);
            }
            return ret;
        }
        Log.e(TAG, "requestAudioFocus: valueAt audio service fail");
        return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    }

    /**
     * 恢复播放
     *
     * @return 是否抢焦点成功，如果为{@link AudioManager#AUDIOFOCUS_GAIN}代表抢焦点成功，反之。
     */
    private int abandonAudioFocus() {
        Log.d(TAG, "abandonAudioFocus: abandon audio focus");
        if (!hasRequestAudioFocus) {
            Log.e(TAG, "abandonAudioFocus: has not request audio focus.");
            return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
        }
        AudioManager sAudioManager =
                (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (sAudioManager != null) {
            int ret = sAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
            Log.d(TAG, "abandonAudioFocus: ret = " + ret);
            if (ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                hasRequestAudioFocus = false;
            } else {
                Log.e(TAG, "abandonAudioFocus fail: ret = " + ret);
            }
            return ret;
        }
        Log.e(TAG, "abandonAudioFocus: valueAt audio service fail");
        return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    }

    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    Log.e(TAG, "onAudioFocusChange: loss focus. focus flag = " + focusChange);
                    //doSomething,
                    hasRequestAudioFocus = false;
                    videoViewPause();
                    break;
                default:
                    Log.e(TAG, "onAudioFocusChange: default focus = " + focusChange);
            }
        }
    };

    private void updatePosition() {
        mPosition = (int) mPlayerView.getCurrentPosition();
        mDuration = mPlayerView.getDuration();
    }

    /**
     * 是否需要保护现场，恢复播放进度。
     */
    private boolean isSaveState() {
        IAliyunVodPlayer.PlayerState state = mPlayerView.getPlayerState();
        Log.e(TAG, "isSaveState: state = " + state);
        return state != IAliyunVodPlayer.PlayerState.Idle
                && state != IAliyunVodPlayer.PlayerState.Stopped
                && state != IAliyunVodPlayer.PlayerState.Completed;
    }

    private boolean isIdleState() {
        IAliyunVodPlayer.PlayerState state = mPlayerView.getPlayerState();
        Log.e(TAG, "isSaveState: state = " + state);
        return state != IAliyunVodPlayer.PlayerState.Idle
                && state != IAliyunVodPlayer.PlayerState.Stopped
                && state != IAliyunVodPlayer.PlayerState.Completed
                && state != IAliyunVodPlayer.PlayerState.Error;
    }


}