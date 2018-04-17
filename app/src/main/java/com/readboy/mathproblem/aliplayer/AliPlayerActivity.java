package com.readboy.mathproblem.video.movie;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.activity.StudyActivity;
import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.application.SubjectType;
import com.readboy.mathproblem.bean.ProjectParcelable;
import com.readboy.mathproblem.cache.CacheEngine;
import com.readboy.mathproblem.cache.ProjectEntityWrapper;
import com.readboy.mathproblem.util.FileUtils;
import com.readboy.mathproblem.util.NetworkUtils;
import com.readboy.mathproblem.util.ToastUtils;
import com.readboy.mathproblem.video.dreamplayer.RequestPermissionsActivity;
import com.readboy.mathproblem.video.fragment.PlayerFragment;
import com.readboy.mathproblem.video.proxy.VideoProxy;
import com.readboy.mathproblem.video.tools.FloatListAdapter;
import com.readboy.mathproblem.video.tools.MyTrafficStatus;
import com.readboy.mathproblem.video.tools.UriProxy;
import com.readboy.mathproblem.video.tools.Utils;
import com.readboy.mathproblem.video.tools.WakeUtil;
import com.readboy.mathproblem.video.view.CustomProgressDialog;
import com.readboy.mathproblem.video.db.VideoDatabaseInfo;
import com.readboy.mathproblem.video.db.VideoInfoDatabaseProxy;
import com.readboy.mathproblem.widget.LineItemDecoration;
import com.readboy.recyclerview.CommonAdapter;
import com.readboy.recyclerview.MultiItemTypeAdapter;
import com.readboy.recyclerview.base.ViewHolder;
import com.readboy.video.EmptyVideoPlayerListener;
import com.readboy.video.ErrorType;
import com.readboy.video.proxy.HttpGetProxy;
import com.readboy.video.view.VideoView;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;

import static com.readboy.mathproblem.video.proxy.MovieFile.saveVideoInfo;
import static com.readboy.mathproblem.video.tools.NetWorkAnalyst.getNetworkType;

import static com.readboy.mathproblem.video.proxy.VideoProxy.VIDEO_URI_SCHEME;

public class MovieActivity extends FragmentActivity implements VideoExtraNames, OnClickListener,
        OnSeekBarChangeListener, HttpGetProxy.OnErrorHttpStatusCodeListener {

    public static final String DREAM_PLAYER_SHADER = "DreamPlayerShader";
    public static final String ACTION_APPSWITCH = "com.readboy.switchapp";
    public static final String ACTION_TIP = "tip.infomation";

    private static final int BRIGHT_CONTROL_FACTOR = 600;

    private static final int MAX_DIRDEPTH = 15;
    private static final String TAG = "oubin_MovieActivity";

    public static boolean onStopped = false;

    boolean isInit = false;
    private boolean mIsPlayUrl = false;
    private boolean mInTouchSeekBar = false;
    private boolean mInHiding = false;
    private boolean mInShowing = false;
    private boolean mPlayOnce = false;
    private boolean mPlayPrepared = false;
    private boolean mPlayRmvb = false;
    private boolean mRmvbPauseByUser = false;
    private boolean mRmvbPlaying = false;
    private boolean mButtonCanClick = true;
    private boolean mUserThinkNetworkAvailable = true;
    private boolean needAuth = false;
    private boolean hasRegisterReceiver = false;
    private int mFinishType;
    private boolean isAdjustVoiceOrBrightness;

    /**
     * 是否解锁
     */
    private boolean mUserPresent = false;
    private boolean finishOnce = false;
    private boolean mWinToFloat = false;

    private boolean mExit = true;
    private boolean mNetWorkSpeedShow = true;

    private int mIndex = 0;
    private static final int HIDE_CONTROLLER_DELAY_DURATION = 5_000;
    private long mDuration = 0;
    private int mVideoLayout = 1;
    private int mNetworkType = ConnectivityManager.TYPE_WIFI;
    private int sScreenLeft = 0;
    private int sScreenRight = 0;
    private int sScreenHeight = 0;
    private int mUrlConfig = 0;

    private float downX = 0, moveX;
    private float downY = 0, moveY;
    private long startTime = 0;
    private long endTime = 0;
    private int mPosition = -1;
    long size = 0;

    //	private String mVid = null;
    private String mPath = null;

    private View mTop;
    private View mBottom;
    private View mFloatLstVw = null;
    private View mPlay;
    private View mPause;
    private TextView mTimeCurrent;
    private TextView mTimeTotal;
    private TextView percentTextView = null;
    private SeekBar mSeekBar;
    private ImageView mGestureController = null;
    private View mFavoriteBtn;
    /**
     * selected = true代表已下载，或正在下载。
     */
    private View mDownloadBtn;
    private TextView mVideoName;
    private View mExerciseBtn;

    //视频列表相关
//    private LinearLayout mVideoListParent;
    private RecyclerView mVideoRv;
    private CommonAdapter mVideoAdapter;
    /**
     *
     */
    private int mSaveVideoState = VideoView.STATE_IDLE;

    private PlayerFragment mFragment = null;
    private VideoPlayerListener mVideoPlayerListener;
    /**
     * 视频是否 正在重新缓存中
     */
    private boolean isBuffering = false;
    private VideoDatabaseInfo mVideoDbInfo = new VideoDatabaseInfo();
    private HttpGetProxy proxy;
    private HeartThread mHearThread = null;
    CustomProgressDialog mPD = null;
    AlertDialog mNetworkDialog = null;

    private FloatListAdapter mFloatLstAdapter = null;

    private AudioManager audioManager;
    private RemoteControlClient mRemoteControlClient = null;

    private MyTrafficStatus mTrafficStatus = null;

    private int[] playsizeBackgroudIDs;
    private final ArrayList<String> mVideoPathList = new ArrayList<>();
    private boolean hasRequestAudioFocus = false;

    /**
     * 监听电话状态
     */
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {  // 响铃
                if (mFragment != null) {
                    mFragment.pause();
                }
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {  //  接听

            } else if (state == TelephonyManager.CALL_STATE_IDLE) {  // 空闲

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
                    if (mPath != null) {
                        if (mPath.startsWith("/storage/emulated/0") || mPath.startsWith("storage/emulated/0")) {
                            if (mVideoPathList.size() > 0) {
                                String path;
                                for (int i = mVideoPathList.size() - 1; i >= 0; i--) {
                                    path = mVideoPathList.get(i);
                                    if (!(path.startsWith("/storage/emulated/0") || path.startsWith("storage/emulated/0"))) {
                                        mVideoPathList.remove(path);
                                    }
                                }
                            }
                        } else {
                            if (!finishOnce && mPath.contains(intent.getData().getPath())) {
                                Log.e(TAG, "card removed.");
                                finishOnce = true;
                                Toast.makeText(MovieActivity.this, "正在播放的文件不存在！", Toast.LENGTH_LONG).show();
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
                    if (mFragment != null) {
                        mFragment.stopPlayback();
                        unregisterReceiver();
//                        System.exit(0);
                        finishMyself();
                    }
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
            if (onStopped && mFragment == null) {
                return;
            }
            switch (message.what) {
                case VideoMessage.READY:
                    Log.e(TAG, "VideoMessage.READY, mIsPlayUrl = " + mIsPlayUrl
                            + ", mUserThinkNetworkAvailable = " + mUserThinkNetworkAvailable);
                    if (!mIsPlayUrl || mUserThinkNetworkAvailable) {
                        updatePlay();
                    }
                    break;
                case VideoMessage.HEART:
//                    Log.e(TAG, "handleMessage: VideoMessage.HEART.");
                    //Vimatio rmvb can't recevie media completion message, so we send finish by ourself.
                    if (mPlayPrepared && mPlayRmvb && mFragment != null) {
                        if (mFragment.isPlaying() && mFragment.getDuration() - mFragment.getCurrentPosition() < 2000) {
                            mHandler.sendEmptyMessage(VideoMessage.FINISHED);
                        }
                        if (mRmvbPlaying) {
                            if (!mFragment.isPlaying() && !mRmvbPauseByUser && !onStopped) {
                                mPlay.setVisibility(View.GONE);
                                mPause.setVisibility(View.VISIBLE);
                            }
                        }
                        mRmvbPlaying = mFragment.isPlaying();
                    }
//                    Log.e(TAG, "handleMessage: mInTouchSeekBar = " + mInTouchSeekBar
//                            + ", mRmvbPauseByUser = " + mRmvbPauseByUser);
                    if (!mInTouchSeekBar && !mRmvbPauseByUser) {
                        updateController();
                    }
                    if (mRmvbPauseByUser && mFragment != null) {
//					mFragment.pause();
                    }
//                    Log.e(TAG, "handleMessage: mNetWorkSpeedShow = " + mNetWorkSpeedShow
//                            + ", trafficStatus = " + mTrafficStatus);
                    if (mNetWorkSpeedShow && mTrafficStatus != null) {
                        if (mPD != null) {
                            String speeds = mTrafficStatus.getRxSpeed(MovieActivity.this);
//                            Log.e(TAG, "handleMessage: speeds = " + speeds);
                            if (speeds != null) {
                                mPD.setMessage(speeds);
                            }
                        }
                    }
                    break;
                case VideoMessage.FINISHED:
                    if (mFragment != null) {
                        Log.e(TAG, "handleMessage: mPlayer state = " + mFragment.getPlayerState());
                        save(mPath, mFragment.getDuration(), mFragment.getDuration());
                    }

                    mPlayRmvb = false;
                    Log.e(TAG, "handleMessage: play once = " + mPlayOnce + ", mIndex = " + mIndex
                            + ", size = " + mVideoPathList.size());
                    if (mPlayOnce || mIndex >= mVideoPathList.size() - 1) {
//                        finishMyself();
                        Log.d(TAG, " -------- Pause unnormal, VideoMessage.FINISHED ");
                        finish();
                    } else {
                        mPath = getNextMediaPath(true);
                        mPosition = 0;
                        mFragment.seekTo(mPosition);
                        mHandler.sendEmptyMessage(VideoMessage.READY);
                    }
                    break;
                case VideoMessage.NOOP:
                    if (mFragment != null) {
                        if (mFragment.isPlaying()) {
                            if (mFloatLstAdapter == null || !mFloatLstAdapter.getActive()) {
                                showController(false);
                            } else {
                                mFloatLstAdapter.setActive(false);
                                hideControllerDelayed();
                            }
                        }
                    }
                    break;
                case VideoMessage.ERROR:
                    Log.e(TAG, "handleMessage: video error.");
                    if (onStopped) {
                        return;
                    }
                    Toast.makeText(MovieActivity.this, (String) message.obj, Toast.LENGTH_LONG).show();
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
                mTop.setVisibility(View.VISIBLE);
                mBottom.setVisibility(View.VISIBLE);
                Log.e(TAG, "onAnimationStart: video list size = " + mVideoPathList.size());
                mVideoRv.setVisibility(View.VISIBLE);
                if (mVideoPathList.size() > 1) {
//                    mVideoListParent.setVisibility(View.VISIBLE);
                }
//				mFloatLstVw.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mInHiding) {
                mTop.setVisibility(View.GONE);
                mBottom.setVisibility(View.GONE);
                mVideoRv.setVisibility(View.GONE);
//                mVideoListParent.setVisibility(View.GONE);
                if (mFloatLstVw != null) {
                    mFloatLstVw.setVisibility(View.GONE);
                }
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

        SharedPreferences sharePreference = getSharedPreferences(MovieActivity.DREAM_PLAYER_SHADER, Context.MODE_PRIVATE);
        boolean floatshow = sharePreference.getBoolean(FloatingWindowService.FLOATWIN_SHOW, false);
        Log.e(TAG, "---- onCreate floatshow: " + floatshow);
        if (floatshow) {
            sendBroadcast(new Intent("android.intent.action.floatviewclose"));
        }

        mRemoteControlClient = MediaButtonIntentReceiver.registerMediaButton(this);

        setContentView(R.layout.player_view);
        registerReceiver();

        assignView();
        initClickEvent();

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        Log.d(TAG, "-------- MovieActivity onCreate intent: " + getIntent());
        if (permission) {
            if (!parseIntent(getIntent())) {
                return;
            }
            initMovieActivity();
        }

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        mHandler.sendEmptyMessage(VideoMessage.READY);
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
        mTop = findViewById(R.id.player_top);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            View mPlayerSizePort = findViewById(R.id.playsize_port);
            mPlayerSizePort.setVisibility(View.GONE);
        }

        playsizeBackgroudIDs = new int[]{
                R.drawable.btn_playsize_full_portrait_selector,
                R.drawable.btn_palysize_width_portrait_selector
        };

        mSeekBar = (SeekBar) findViewById(R.id.player_seek_bar);
        mSeekBar.setOnSeekBarChangeListener(this);
        mPlay = findViewById(R.id.player_player);
        mPause = findViewById(R.id.player_pause);
        mTimeCurrent = (TextView) findViewById(R.id.player_current_time);
        mTimeTotal = (TextView) findViewById(R.id.player_total_time);
        mBottom = findViewById(R.id.player_control_bar);

        mFavoriteBtn = findViewById(R.id.favorite);
        mFavoriteBtn.setOnClickListener(this);
        mDownloadBtn = findViewById(R.id.download);
        mDownloadBtn.setOnClickListener(this);
        mVideoName = (TextView) findViewById(R.id.player_video_name);
        mExerciseBtn = findViewById(R.id.exercise);

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
        mVideoAdapter = new CommonAdapter<String>(this, R.layout.item_video_player, mVideoPathList) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                TextView textView = (TextView) holder.itemView.findViewById(R.id.player_video_name);
                textView.setText(FileUtils.getFileNameWithoutExtension(mVideoPathList.get(position)));
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

                showProgressDialog();
                if (mFragment != null) {
                    save(mPath, mFragment.getCurrentPosition(), mFragment.getDuration());
                }
                videoViewPauseOrStop();
                mIndex = position;
                mPath = mVideoPathList.get(mIndex);
                mPosition = 0;
//                mFragment.seekTo(0);
                updateStatus(mPath);
                mRmvbPauseByUser = false;
                mHandler.sendEmptyMessage(VideoMessage.READY);
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
        int[] resIds = {R.id.back, R.id.player_player, R.id.player_pause, R.id.exercise, R.id.fullscreen, R.id.video_list_switch};
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

            Log.d(TAG, "---- onNewIntent newPath: " + newPath + ", mPath: " + mPath);
            if (newPath != null && !newPath.equals(mPath)) {
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
        if (pathList == null) {
            return;
        }
        mVideoPathList.clear();
        mVideoPathList.addAll(pathList);
        mVideoAdapter.notifyDataSetChanged();
//        mVideoRv.post(() -> ViewUtils.setSelectedPosition(mIndex, R.id.player_video_name, mVideoRv));
        if (mVideoPathList.size() <= 1) {
            Log.e(TAG, "updateVideoPath: videoPathList size = " + mVideoPathList.size());
//            mVideoListParent.setVisibility(View.GONE);
        }
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

//        LayoutParams paramsTimeCurrent = (LayoutParams) mTimeCurrent.getLayoutParams();
//        LayoutParams paramsTimeTotal = (LayoutParams) mTimeTotal.getLayoutParams();
//        LayoutParams paramsSeekbar = (LayoutParams) mSeekBar.getLayoutParams();
//        android.view.ViewGroup.LayoutParams paramsTopLayout = mTop.getLayoutParams();
//
//        View mPlaysize = findViewById(R.id.playsize);
//        View mPlaysizePort = findViewById(R.id.playsize_port);
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            paramsTimeCurrent.removeRule(RelativeLayout.BELOW);
//            paramsTimeTotal.removeRule(RelativeLayout.BELOW);
//            paramsTimeTotal.rightMargin = (int) getResources().getDimension(R.dimen.player_total_time_l_margin_right);
//
//            paramsSeekbar.addRule(RelativeLayout.LEFT_OF, mTimeTotal.getId());
//            paramsSeekbar.addRule(RelativeLayout.RIGHT_OF, mTimeCurrent.getId());
//            paramsSeekbar.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//
//            paramsTopLayout.height = (int) getResources().getDimension(R.dimen.player_top_height_landscape);
//
//            mPlaysize.setVisibility(View.VISIBLE);
//            mPlaysizePort.setVisibility(View.GONE);
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            paramsTimeCurrent.addRule(RelativeLayout.BELOW, mSeekBar.getId());
//            paramsTimeTotal.addRule(RelativeLayout.BELOW, mSeekBar.getId());
//            paramsTimeTotal.rightMargin = (int) getResources().getDimension(R.dimen.player_total_time_p_margin_right);
//
//            paramsSeekbar.removeRule(RelativeLayout.LEFT_OF);
//            paramsSeekbar.addRule(RelativeLayout.RIGHT_OF, R.id.back);
//            paramsSeekbar.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//
//            paramsTopLayout.height = (int) getResources().getDimension(R.dimen.player_top_height_portrait);
//
//            mPlaysize.setVisibility(View.GONE);
//            mPlaysizePort.setVisibility(View.VISIBLE);
//        }
//        if (mFragment != null) {
//            mFragment.setLayout();
//        }
//
//        mSeekBar.setLayoutParams(paramsSeekbar);
//        mTimeCurrent.setLayoutParams(paramsTimeCurrent);
//        mTimeTotal.setLayoutParams(paramsTimeTotal);
//        mTop.setLayoutParams(paramsTopLayout);
        Log.i("", " onConfigurationChanged mFragment: " + mFragment);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
        onStopped = false;

//        keepScreenOn();

        hideControllerDelayed();
        WakeUtil.acquireCpuWakeLock(this);
        if (mHearThread != null) {
            Log.e(TAG, "onResume: pause hear thread.");
            mHearThread.pause();
            mHearThread = null;
        }
        mHearThread = new HeartThread(mHandler);
        mHearThread.start();
        if (!mUserThinkNetworkAvailable && mIsPlayUrl) {
            handleNetwork();
        }
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        mUserPresent = !keyguardManager.inKeyguardRestrictedInputMode();

        if (mIsPlayUrl) {
            mTrafficStatus = new MyTrafficStatus(this);
        }
        if (mFragment != null) {
//            mButtonCanClick = mFragment.isInPlaybackState();
            Log.e(TAG, "onResume: mButtonCanClick = " + mButtonCanClick + ", state = " + mFragment.getPlayerState());
            if (!mButtonCanClick) {
//                showProgressDialog();
            }
        }
        Log.d(TAG, "-------- onResume ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
        cancelAutoHide();
        onStopped = true;
        if (mFragment != null && isSaveState()) {
            mPosition = Math.max(0, mFragment.getCurrentPosition() - 1200);
            Log.e(TAG, "onPause: mPosition = " + mPosition);
            mDuration = mFragment.getDuration();
        }

        if (mFragment != null) {
            mSaveVideoState = mFragment.getPlayerState();
        }

        videoViewPause();

        //解决加载中，按电源键系统到时的问题。
        dismissProgressDialog();
        mButtonCanClick = true;

        mHearThread.pause();
        mHearThread = null;
        Log.d(TAG, "-------- onPause mPosition: " + mPosition + ", string: " + Utils.formatTime(mPosition));

        WakeUtil.releaseCpuLock();
        save(mPath, mPosition, mDuration);

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

        if (proxy != null) {
            proxy.stopProxy();
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

        if (mExit) {
//            Log.e(TAG, "-------- System.exit(0) ");
//            System.exit(0);
        }

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

    public void hideControllerDelayed() {
        Log.e(TAG, "hideControllerDelayed: ");
        mHandler.removeMessages(VideoMessage.NOOP);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(VideoMessage.NOOP), HIDE_CONTROLLER_DELAY_DURATION);
    }

    public void cancelAutoHide() {
        Log.e(TAG, "cancelAutoHide: ");
        mHandler.removeMessages(VideoMessage.NOOP);
    }

    public void changeVideoLayout(View view) {
        mVideoLayout++;
        if (mVideoLayout >= 2) {
            mVideoLayout = 0;
        }

        switch (mVideoLayout) {
            case 0:
                //初始状态
                mVideoLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
                break;
            case 1:
                //
                mVideoLayout = VideoView.VIDEO_LAYOUT_SCALE;
                break;
            default:
                Log.e(TAG, "changeVideoLayout: default = " + mVideoLayout);
                break;
        }
        Log.i(TAG, "-------- mVideoLayout: " + mVideoLayout);
        if (mFragment != null) {
            mFragment.setLayout(mVideoLayout);
        }
        ((ImageButton) view).setImageResource(playsizeBackgroudIDs[mVideoLayout]);
        hideControllerDelayed();
    }

    public String getNextMediaPath(boolean increase) {
        if (increase) {
            if (++mIndex >= mVideoPathList.size()) {
                mIndex = 0;
            }
        } else {
            if (--mIndex < 0) {
                mIndex = mVideoPathList.size() - 1;
            }
        }
        if (mFloatLstAdapter != null) {
            mFloatLstAdapter.setSelIdx(mIndex);
        }
        Log.i(TAG, "-------- getNextMediaPath index: " + mIndex);
        String path = mVideoPathList.get(mIndex);
        updateStatus(path);
        return path;
    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, "onClick: v = " + v.getId());
        if (mInHiding) {
            mTop.clearAnimation();
            mBottom.clearAnimation();
            mVideoRv.clearAnimation();
            mInHiding = false;
        }
        hideControllerDelayed();
        switch (v.getId()) {
            case R.id.back:
                mPlayPrepared = false;
                mPlayRmvb = false;
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
            case R.id.exercise:
                gotoExerciseActivity();
                break;
            case R.id.favorite:
                Log.e(TAG, "onClick: favorite isChecked = " + mFavoriteBtn.isSelected());
                if (mFavoriteBtn.isSelected()) {
                    if (VideoProxy.unFavoriteVideo(getContentResolver(), mPath)) {
                        mFavoriteBtn.setSelected(false);
                        ToastUtils.showShort(this, "取消收藏成功");
                    } else {
                        ToastUtils.show(this, "取消收藏失败！");
                    }
                } else {
                    if (VideoProxy.favoriteVideo(getContentResolver(), mPath)) {
                        ToastUtils.showShort(this, "收藏成功");
                        mFavoriteBtn.setSelected(true);
                    } else {
                        ToastUtils.showShort(this, "收藏失败");
                    }
                }
                break;
            case R.id.download:
                Log.e(TAG, "onClick: filename = " + FileUtils.getFileName(mPath));
                if (mIsPlayUrl) {
                    if (mDownloadBtn.isSelected() && VideoProxy.isDownloading(mPath)) {
                        ToastUtils.show(this, "正在下载中");
                    }
                    VideoProxy.downloadVideoWithUrl(this, mPath);
                    ToastUtils.showShort(this, "已添加到下载队列");
                    mDownloadBtn.setSelected(true);
                } else {
                    ToastUtils.show(this, "已下载");
                }
                break;
            case R.id.fullscreen:
                if (mFinishType == TYPE_GOTO) {
                    gotoStudyActivity();
                } else {
                    finishMyself();
                }
                break;
            case R.id.video_list_switch:
                if (mVideoRv.getVisibility() == View.VISIBLE) {
                    hideVideoList();
                } else {
                    showVideoList();
                }
                break;
            default:
                Log.e(TAG, "onClick: other view click = " + v.getId());
                break;
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

    private void showFloatWindow() {
        if (Build.VERSION.SDK_INT > 22) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intentss = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intentss.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intentss);
                return;
            }
        }
        if (!mWinToFloat && mPath != null) {
            mWinToFloat = true;
            Intent intent = new Intent();
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            startActivity(intent);

            Intent service = new Intent(getApplicationContext(), FloatingWindowService.class);
            service.putExtra("path", mPath);
            service.putExtra("position", mFragment.getCurrentPosition());
            service.putStringArrayListExtra("medialist", mVideoPathList);
            service.putExtra("isUrl", mIsPlayUrl);

            service.putExtra("databasepath", mVideoDbInfo.mDataPath);
            service.putExtra("databasename", mVideoDbInfo.mDataName);
            service.putExtra("cachepath", mVideoDbInfo.mCachePath);

            getApplicationContext().startService(service);
            if (mFragment != null) {
                if (mFragment.isPlaying()) {
                    mPosition = mFragment.getCurrentPosition();
                }
                mFragment.pause();
                mFragment.stopPlayback();
//						mVideoView = null;
            }
            mExit = false;
            finishMyself();
            Log.e(TAG, " -------- go to floatwindow ");
        }
    }

    private void previous() {
        if (!mButtonCanClick) {
            return;
        }
        if (mFragment != null) {
            save(mPath, mFragment.getCurrentPosition(), mFragment.getDuration());
        }

        if (mIndex > 0) {
            mPath = getNextMediaPath(false);
            mPosition = 0;
            mFragment.seekTo(mPosition);
            mRmvbPauseByUser = false;
            mHandler.sendEmptyMessage(VideoMessage.READY);
            cancelAutoHide();
        } else {
            Toast.makeText(this, "没有多余的视频了", Toast.LENGTH_LONG).show();
        }
    }

    private void next() {
        if (!mButtonCanClick) {
            return;
        }
        if (mFragment != null) {
            save(mPath, mFragment.getCurrentPosition(), mFragment.getDuration());
        }
        if (mIndex < mVideoPathList.size() - 1) {
            mPath = getNextMediaPath(true);
            mPosition = 0;
            mFragment.seekTo(mPosition);
            mRmvbPauseByUser = false;
            mHandler.sendEmptyMessage(VideoMessage.READY);
            cancelAutoHide();
        } else {
            Toast.makeText(this, "没有多余的视频了", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 显示控制界面
     *
     * @param isShow 是否显示，下一个状态
     */
    public void showController(boolean isShow) {
        Log.e(TAG, "showController() called with: isShow = " + isShow + "");
        if (isShow) {
            if (!mInShowing && !mInHiding && !isControllerShowing()) {
                //显示
                Animation ani1 = AnimationUtils.loadAnimation(this, R.anim.push_down);
                Animation ani2 = AnimationUtils.loadAnimation(this, R.anim.push_up);
                Animation ani3 = AnimationUtils.loadAnimation(this, R.anim.push_right);
                ani1.setAnimationListener(mDismissAnimListener);
                mInShowing = true;
                mTop.startAnimation(ani1);
                mBottom.startAnimation(ani2);
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
                mTop.startAnimation(ani1);
                mBottom.startAnimation(ani2);
                mVideoRv.startAnimation(animation3);

                if (mFloatLstVw != null && mFloatLstVw.getVisibility() != View.GONE) {
                    Animation ani3 = AnimationUtils.loadAnimation(this, R.anim.slip_righ_to_left_mis);
                    mFloatLstVw.startAnimation(ani3);
                }

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
        return mTop.getVisibility() == View.VISIBLE || mBottom.getVisibility() == View.VISIBLE;
    }

    /**
     * 下面是哪个都为seebar监听的相关函数
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mTimeCurrent.setText(Utils.formatTime(seekBar.getProgress()));
//			Log.i(TAG, "---- onProgressChanged mInTouchSeekBar: "+mInTouchSeekBar+", seekBar.getProgress(): "+seekBar.getProgress());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mInTouchSeekBar = true;
        cancelAutoHide();
        Log.d(TAG, "---- onStartTrackingTouch seekBar.getProgress: " + Utils.formatTime(seekBar.getProgress()));
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.e(TAG, "onStopTrackingTouch: isPlayUrl = " + mIsPlayUrl);
        mInTouchSeekBar = false;
        if (!onStopped) {
            mRmvbPauseByUser = false;
//            hideControllerDelayed();
        }
        if (mFragment != null) {
            Log.e(TAG, "onStopTrackingTouch: seek to " + seekBar.getProgress());
            mFragment.seekTo(seekBar.getProgress());
            if (seekBar.getProgress() > mFragment.getDuration() - 1000) {
                if (mIsPlayUrl) {
                    ToastUtils.show(this, "进度条已经最后了", Toast.LENGTH_SHORT);
                    mHandler.sendEmptyMessage(VideoMessage.FINISHED);
                }
            }
            if (mIsPlayUrl) {
                showProgressDialog();
            }
        }
        Log.d(TAG, "---- onStopTrackingTouch seekBar.getProgress: " + Utils.formatTime(seekBar.getProgress()));
    }

    private void showProgressDialog() {
        Log.e(TAG, "showProgressDialog: ");
//        cancelAutoHide();
        if (mNetworkDialog != null && mNetworkDialog.isShowing()) {
            Log.e(TAG, "showProgressDialog: network dialog showing");
            return;
        }

        createProgressDialog();
//        if (mPD.isShowing()){
//            return;
//        }
        mPD.show();
        mPD.setCancelClickListener(v -> {
            mFragment.stopPlayback();
            mPlayPrepared = false;
            mPlayRmvb = false;
            finishMyself();
        });
        mNetWorkSpeedShow = true;
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
                isAdjustVoiceOrBrightness = false;
                downX = event.getX();
                downY = event.getY();
                startTime = System.currentTimeMillis();
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

                if (isControllerShowing()) {
                    if (downX > sScreenRight && mVideoPathList.size() > 1 && mFloatLstVw != null) {
                        if (downX - moveX > 10) {
                            if (mFloatLstVw.getVisibility() != View.VISIBLE) {
                                Animation ani3 = AnimationUtils.loadAnimation(this, R.anim.slip_righ_to_left_show);
                                mFloatLstVw.startAnimation(ani3);
                                mFloatLstVw.setVisibility(View.VISIBLE);
                                mFloatLstAdapter.setActive(true);
                            }

                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //时间很短的话就显示下面的控制栏
                endTime = System.currentTimeMillis();
                if (!isAdjustVoiceOrBrightness) {
                    showController(!isControllerShowing());
                    hideControllerDelayed();
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
        if (mFragment != null) {
            mSeekBar.setMax((int) mFragment.getDuration());
            mButtonCanClick = true;
            mPlayPrepared = true;
            isBuffering = false;
            requestAudioFocusTransient();
            Log.e(TAG, "prepare: seek to " + mPosition);
//            mFragment.seekTo(mPosition);
        }
        Log.d(TAG, "-------- prepare mPosition: " + mPosition);
    }

    public void complete() {
        mHandler.sendEmptyMessage(VideoMessage.FINISHED);
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "onError: what = " + what + ", extra = " + extra);
        Log.e(TAG, "onError: before mPosition = " + mPosition);
        mPosition = mFragment.getCurrentPosition();
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
        int state = VideoView.STATE_IDLE;
        if (mFragment != null) {
            state = mFragment.getPlayerState();
        }
        Log.e(TAG, "handleNetwork: mPlayState = " + state);
        mUserThinkNetworkAvailable = networkType == ConnectivityManager.TYPE_WIFI;
        if (mNetworkType == networkType) {
            return;
        }

        if (mNetworkDialog != null && mNetworkDialog.isShowing()) {
            mNetworkDialog.dismiss();
        }
        switch (networkType) {
            case -1: //无网络
                if (mFragment != null && isSaveState()) {
                    mPosition = mFragment.getCurrentPosition();
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
                Log.e(TAG, "handleNetwork: playing = " + mFragment.isInPlaybackState());
                break;
            default:
                //非wifi，使用流量提示
                if (mFragment != null) {
                    mFragment.pause();
                    mPosition = mFragment.getCurrentPosition();
                }
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
                    if (proxy != null && mFragment != null) {
                        mFragment.setPlayPath(proxy.getLocalURL(), mVideoDbInfo);
                        Log.e(TAG, "DialogInterface onClick: seek to " + mPosition);
                        mFragment.seekTo(mPosition);
                        mFragment.pause();
                    }
                }
                if (!mPlayPrepared) {
                    mHandler.sendEmptyMessage(VideoMessage.READY);
                } else {
                    if (mFragment != null) {
                        mFragment.start();
                    }
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

    private void save(String path, long curPos, long duration) {
        long size = 0;
        if (mIsPlayUrl) {
            if (proxy != null) {
                size = proxy.getFileSize();
            }
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
        String[] VIDEO_PROJECTION = new String[]{Video.Media.DATA};

		/* valueAt video file */
        try {
            c = mMediaProvider.query(videoUri, VIDEO_PROJECTION, null, null, null);
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
        Uri uri = intent.getData();
        mIsPlayUrl = false;
        mFinishType = intent.getIntExtra(EXTRA_FINISH_TYPE, TYPE_SET_RESULT);

        mPath = intent.getStringExtra(VideoExtraNames.EXTRA_PATH);
        if (TextUtils.isEmpty(mPath)) {
            mPath = intent.getStringExtra(VideoExtraNames.EXTRA_URL);
        }
        Log.e(TAG, "PATH:" + mPath);
        boolean exerciseEnable = intent.getBooleanExtra(VideoExtraNames.EXTRA_EXERCISE_ENABLE, false);
        if (exerciseEnable) {
            mExerciseBtn.setVisibility(View.VISIBLE);
        } else {
            mExerciseBtn.setVisibility(View.GONE);
        }
        mUrlConfig = intent.getIntExtra(VideoExtraNames.EXTRA_URL_CONFIG, 0);
        mPosition = intent.getIntExtra(VideoExtraNames.EXTRA_SEEK_POSITION, 0);
        mIndex = intent.getIntExtra(VideoExtraNames.EXTRA_INDEX, 0);
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
            } else {    //"content://"
//				mPath = uriParse(uri).getPath();
                mPath = UriProxy.getPath(this, uri);
            }
            pathList.add(mPath);
            Log.e(TAG, "mPath1=" + mPath);
            mPlayOnce = true;
            result = true;
        }

        updateVideoPath(pathList);
        updateStatus(mPath);
        Log.e(TAG, "parseIntent: paths = " + Arrays.toString(mVideoPathList.toArray()));
        Log.e(TAG, "parseIntent: mPath = " + mPath + ", \nmIndex = " + mIndex + ", \nisPlayUrl = " + mIsPlayUrl);
        return result;
    }

    private void parseFileNameList(Intent intent) {

    }

    /**
     * 更新因path修改，影响的其他状态
     */
    private void updateStatus(String path) {
        if (TextUtils.isEmpty(path)) {
            mIndex = -1;
            return;
        }
        mIsPlayUrl = !TextUtils.isEmpty(mPath) && mPath.contains("http:");
        Uri uri = Uri.parse(path);
        mPath = path;
        if (VIDEO_URI_SCHEME.equals(uri.getScheme())) {
            Log.e(TAG, "updateStatus: is uri : " + path);
            if (!VideoProxy.isDownloaded(FileUtils.getFileName(mPath))) {
                mIsPlayUrl = true;
            }
        }
        Log.e(TAG, "updateStatus: mPath = " + mPath);
        if (!mIsPlayUrl) {
//			findViewById(R.id.floatwindow).setVisibility(View.VISIBLE);
            Log.e(TAG, "updateStatus: clear videoDbInfo cache path.");
            //不支持本地，网络混合视频播放列表，注释掉代码。
//            mVideoDbInfo.mCachePath = null;
        } else {
            mVideoDbInfo.mType = VideoInfoDatabaseProxy.TYPE_URL;
        }
        updateFavoriteAndDownload();

    }

    /**
     * 更新播放控制条中的收藏和下载
     */
    private void updateFavoriteAndDownload() {
        if (TextUtils.isEmpty(mPath)) {
            return;
        }
        Log.e(TAG, "updateFavoriteAndDownload: mIsPlayUrl = " + mIsPlayUrl);
        if (!mIsPlayUrl || VideoProxy.isDownloading(mPath)) {
            //代表是本地视频，或者正在下载中。
            mDownloadBtn.setSelected(true);
        } else {
            mDownloadBtn.setSelected(false);
        }
        if (VideoProxy.isFavorite(getContentResolver(), FileUtils.getFileName(mPath))) {
            mFavoriteBtn.setSelected(true);
        } else {
            mFavoriteBtn.setSelected(false);
        }
    }

    private void updateController() {
        if (mFragment != null) {
            mTimeCurrent.setText(Utils.formatTime(mFragment.getCurrentPosition()));
            mTimeTotal.setText("/" + Utils.formatTime(mFragment.getDuration()));
            mSeekBar.setProgress((int) mFragment.getCurrentPosition());
            if (mIsPlayUrl) {
                float percent = mFragment.getBufferPercentage() * 0.01f;
//			Log.i(TAG, " percent: "+percent+", mBufSize: "+mBufSize+", size: "+size);
                mSeekBar.setSecondaryProgress((int) (percent * mFragment.getDuration()));
            }

            if (mFragment.isPlaying()) {
                mPlay.setVisibility(View.GONE);
                mPause.setVisibility(View.VISIBLE);
            } else {
                mPlay.setVisibility(View.VISIBLE);
                mPause.setVisibility(View.GONE);
            }

//            ((ImageButton) findViewById(R.id.playsize)).setImageResource(playsizeBackgroudIDs[mVideoLayout]);
//            ((ImageButton) findViewById(R.id.playsize_port)).setImageResource(playsizeBackgroudIDs[mVideoLayout]);
        }
    }

    private void updatePlay() {
        Log.e(TAG, "updatePlay() called path = " + mPath + ", mPosition = " + mPosition);
        mPlayPrepared = false;

        if (mPath == null) {
            return;
        }

        if (mPath.split("/").length > MAX_DIRDEPTH) {    //文件夹深度超过MAX_DIRDEPTH不播放
            mHandler.sendEmptyMessage(VideoMessage.TOO_DEEP);
            return;
        }

        mPlayRmvb = mPath.endsWith(".rmvb");

//        boolean isProxy = false;
//        boolean proxyflag = false;
//
//        if (!mIsPlayUrl) {
//            proxyflag = isEncryption(this, mPath);
//        }
//        Log.i("", "---- proxyflag: " + proxyflag);
//        isProxy = proxyflag || mUrlConfig != 0;

        Log.e(TAG, "updatePlay: mButtonCanClick = false.");
        mButtonCanClick = false;

        if (mIsPlayUrl) {
            if (!onStopped) {
                showProgressDialog();
            }
        }

//		new AsynVideoSetDataSource().execute(isProxy);

        mVideoName.setText(Utils.getVideoName(mPath));

        Uri uri = Uri.parse(mPath);
        Log.e(TAG, "updatePlay: scheme = " + uri.getScheme());
        if (VIDEO_URI_SCHEME.equals(uri.getScheme())
                || (mIsPlayUrl && !VideoProxy.isValid(mPath))) {
            String path = uri.getPath();
            Log.e(TAG, "updatePlay: is url : " + mPath + ", path = " + path);
            if (TextUtils.isEmpty(path)) {
                path = uri.getEncodedPath();
            }
            playVideoAndAuth(path);
            mFavoriteBtn.setVisibility(View.VISIBLE);
        } else {
            if (mIsPlayUrl && !checkNetwork()) {
                Log.e(TAG, "updatePlay: not internet.");
                mButtonCanClick = true;
                return;
            }
            mFragment.setPlayPath(mPath, mVideoDbInfo);
            updateStatusCauseUri();
            mFavoriteBtn.setVisibility(View.GONE);
        }

        showController(true);
        hideControllerDelayed();
        Log.e(TAG, "updatePlay: mIndex = " + mIndex);
        mVideoAdapter.notifyDataSetChanged();
    }

    /**
     * 监听是否有网络。
     *
     * @return 是否有网络。
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

    private void playVideoAndAuth(String uri) {
        VideoProxy.getVideoAbsoluteUri(uri, this, new VideoProxy.AbsoluteUriCallback() {
            @Override
            public void onResponse(String uri, boolean isUrl) {
                Log.e(TAG, "onResponse() called with: uri = " + uri + ", isUrl = " + isUrl + "");
                mIsPlayUrl = isUrl;
//                mPath = uri;
                if (mIsPlayUrl && !checkNetwork()) {
                    Log.e(TAG, "playVideoAndAuth: not internet.");
                    return;
                }
                mFragment.setPlayPath(uri, mVideoDbInfo);
                updateStatusCauseUri();
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "onError: t = " + throwable, throwable);
                if (FileUtils.getAvailableSize(getCacheDir().getPath())
                        < 50 * 1024 * 1024) {
                    ToastUtils.showShort(MovieActivity.this, "播放错误：存储空间不足。");
                }else {
                    ToastUtils.showShort(MovieActivity.this, "无法获取视频资源");
                }
                mButtonCanClick = true;
            }
        });
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
            Log.i(TAG, "没有初始化imageview和textview");
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
     * 设置路径，videoView内部异步加载，并播放。
     *
     * @param uri
     */
    private void playVideo(String uri) {

    }

    /**
     * 暂停或者停止播放
     * 如果正在缓冲播放内容中，则停止播放器，
     * 其他则暂停。
     */
    private void videoViewPauseOrStop() {
        if (mFragment != null) {
            int state = mFragment.getPlayerState();
            if (state == VideoView.STATE_PREPARING) {
                videoViewStop();
            } else {
                videoViewPause();
            }
        }
    }

    //TODO: 当黑屏时，启动视频会有问题，视频会播放，但是不会更新进度。
    private void videoViewPause() {
        Log.e(TAG, "videoViewPause: ");
        mRmvbPauseByUser = true;

        if (mFragment != null) {
            int state = mFragment.getPlayerState();
            Log.e(TAG, "videoViewPause: state = " + state);
            mFragment.pause();
            hideControllerDelayed();
        }
        mPlay.setVisibility(View.VISIBLE);
        mPause.setVisibility(View.GONE);
        abandonAudioFocus();
    }

    private void videoViewStop() {
        Log.e(TAG, "videoViewPause: ");
        mRmvbPauseByUser = true;

        if (mFragment != null) {
            int state = mFragment.getPlayerState();
            Log.e(TAG, "videoViewPause: state = " + state);
            mFragment.stopPlayback();
            hideControllerDelayed();
        }
        mPlay.setVisibility(View.VISIBLE);
        mPause.setVisibility(View.GONE);
        abandonAudioFocus();
    }

    private void videoViewStart() {
        mRmvbPauseByUser = false;
        int state = mFragment.getPlayerState();
        Log.e(TAG, "videoViewStart: state = " + state);
        if (state == VideoView.STATE_ERROR
                || state == VideoView.STATE_STOPPED
                || state == VideoView.STATE_IDLE) {
            updatePlay();
        } else if (mFragment != null && !mFragment.isPlaying()) {
            Log.i(TAG, " MovieActivity videoViewStart mFragment start");
            requestAudioFocusTransient();
            if (mFragment.start()) {
//                cancelAutoHide();
                hideControllerDelayed();
                mPlay.setVisibility(View.GONE);
                mPause.setVisibility(View.VISIBLE);
            } else {
                Log.e(TAG, "videoViewStart: start play fail, update play.");
                updatePlay();
            }
        } else {
            //TODO：为什么会出现null?
            if (mFragment == null) {
                Log.e(TAG, "videoViewStart: mFragment = null");
            }
        }
    }

    private void finishMyself() {
        Intent intent = new Intent("com.readboy.mathproblem.video.playinfo");
        Log.e(TAG, "-------- finishMyself ");
        if (mFragment != null && !onStopped) {
            Log.e(TAG, "finishMyself: mPosition = " + mPosition + ", getCurrentPosition = " + mFragment.getCurrentPosition());
            mPosition = mFragment.getCurrentPosition();
            mDuration = mFragment.getDuration();
            Log.e(TAG, "finishMyself: position = " + mPosition + ", mIndex = " + mIndex);
            intent.putExtra(EXTRA_PATH, mPath);
            intent.putExtra(EXTRA_SEEK_POSITION, mPosition);
            intent.putExtra(EXTRA_DURATION, mDuration);
            intent.putExtra(EXTRA_INDEX, mIndex);
//		sendBroadcast(othersintent);
//			FragmentManager mFrgnr = getSupportFragmentManager();
//			FragmentTransaction mFrgTrntn = mFrgnr.beginTransaction();
//			mFrgTrntn.remove(mFragment);
//			mFrgTrntn.commit();
//			mFragment = null;
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
        mPosition = mFragment.getCurrentPosition();
        mDuration = mFragment.getDuration();
        parcelable.setVideoIndex(mIndex);
        parcelable.setSeekPosition(mPosition);
        intent.putExtra(ProjectParcelable.EXTRA_PROJECT_PARCELABLE, parcelable);
        intent.putExtra(EXTRA_FINISH_TYPE, TYPE_GOTO);

        Log.e(TAG, "gotoStudyActivity: parcelable = " + parcelable.toString());
        startActivity(intent);
        finish();
    }

    private void initMovieActivity() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (mFragment != null) {
            transaction.remove(mFragment);
        }
//		if (mIsPlayUrl) {
        mFragment = new PlayerFragment();
        mVideoPlayerListener = new VideoPlayerListener();
//		} else {
//			mFragment = new VitamioFragment();
//		}
        transaction.add(R.id.video_container, mFragment);
        transaction.commit();
        mFragment.addVideoPlayerListener(mVideoPlayerListener);
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

    @Override
    public void onErrorCode(int httpStatusCode) {
        Log.e(TAG, "onErrorCode() called with: httpStatusCode = " + httpStatusCode + "");
        Message msg = Message.obtain();
        msg.what = VideoMessage.ERROR;
        switch (httpStatusCode) {
            case HttpURLConnection.HTTP_NOT_FOUND:
                msg.obj = "访问的网址未找到";
                break;
            default:
                msg.obj = "网络错误:" + httpStatusCode;
                break;
        }
        mHandler.sendMessage(msg);
    }

    private int requestAudioFocusTransient() {
//        Log.d(TAG, "requestAudioFocusTransient: ");
        if (hasRequestAudioFocus) {
//            Log.e(TAG, "requestAudioFocusTransient: has not request audio focus.");
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
//        Log.d(TAG, "abandonAudioFocus: abandon audio focus");
        if (!hasRequestAudioFocus) {
//            Log.e(TAG, "abandonAudioFocus: has not request audio focus.");
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

    private class VideoPlayerListener extends EmptyVideoPlayerListener {

        @Override
        public void onInit() {
            super.onInit();
            Log.e(TAG, "onInit: ");
            showProgressDialog();
            mFragment.seekTo(mPosition);
        }

        @Override
        public void onPrepared() {
            super.onPrepared();
            Log.e(TAG, "onPrepared: ");
        }

        @Override
        public void onPlaying() {
            super.onPlaying();
            int state = mFragment.getPlayerState();
            Log.e(TAG, "onPlaying: state = " + state);
//            dismissProgressDialog();
            if (!isBuffering) {
                dismissProgressDialog();
                hideControllerDelayed();
            }
            if (onStopped) {
                Log.e(TAG, "onPlaying: pause video.");
                videoViewPause();
            }
        }

        @Override
        public void onPaused() {
            super.onPaused();
            Log.e(TAG, "video onPaused: ");
            updateController();
            dismissProgressDialog();
            hideControllerDelayed();
        }

        @Override
        public void onStopped() {
            super.onStopped();
            Log.e(TAG, "video onStopped: ");
            dismissProgressDialog();
            updateController();
            mButtonCanClick = true;
        }

        @Override
        public void onBufferingStart() {
            super.onBufferingStart();
            Log.e(TAG, "onBufferingStart: ");
            isBuffering = true;
            showProgressDialog();
        }

        @Override
        public void onBufferingEnd() {
            super.onBufferingEnd();
            Log.e(TAG, "onBufferingEnd: thread = " + Thread.currentThread().getName());
            isBuffering = false;
            dismissProgressDialog();
        }

        @Override
        public void onError(String error, ErrorType errorType) {
            super.onError(error, errorType);
            Log.e(TAG, "onError() called with: error = " + error + ", errorType = " + errorType + "");
            Log.e(TAG, "onError: before mPosition = " + mPosition);
            ToastUtils.showShort(MovieActivity.this, error);
            mPosition = mFragment.getCurrentPosition();
            Log.e(TAG, "onError: after mPosition = " + mPosition);
            dismissProgressDialog();
            mButtonCanClick = true;
        }
    }

    /**
     * 是否需要保护现场，恢复播放进度。
     */
    private boolean isSaveState() {
        int state = mFragment.getPlayerState();
        Log.e(TAG, "isSaveState: state = " + state);
        return state != VideoView.STATE_IDLE && state != VideoView.STATE_STOPPED
                && state != VideoView.STATE_PLAYBACK_COMPLETED;
    }

//	private class AsynVideoSetDataSource extends AsyncTask<Boolean, Void, String> {
//
//		@Override
//		protected String doInBackground(Boolean... params) {
//			if (proxy != null) {
//				proxy.stopProxy();
//			}
//			boolean isProxy = params[0];
//			String back = null;
//			if (isProxy) {
//				Log.d(TAG, "-------- doInBackground 走代理服务器  mIsPlayUrl："+mIsPlayUrl);
//				if(proxy != null) {
//					proxy.stopProxy();
//				}
//				if (mIsPlayUrl) {
//					String md5 = Md5Utils.getMd5(mPath);
//					String cachepath = MovieFile.getCachePath(MovieActivity.this, mVideoDbInfo.mCachePath, md5);
//					proxy = new HttpGetProxy(MovieActivity.this, mPath, cachepath, md5, mIsPlayUrl, MovieActivity.this);
//					mVideoDbInfo.mCacheName = md5;
//					mVideoDbInfo.mCacheFilePath = cachepath;
//					Log.i(TAG, "-------- cachepath: "+cachepath);
//				} else {
//					proxy = new HttpGetProxy(MovieActivity.this, mPath, null);
//				}
//				back = proxy.getLocalURL();
//			} else {
//				Log.e(TAG, "-------- doInBackground 不走代理服务器 mIsPlayUrl： "+mIsPlayUrl);
//				back = mPath;
//			}
//			return back;
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			super.onPostExecute(result);
//			Log.d(TAG, " ---- AsynVideoSetDataSource: onPostExecute result: "+result+", mPostion: "+mPosition);
//			if (result != null && mFragment!=null) {
//				if (mPosition > 0) {
//					mFragment.seekTo(mPosition);
//				}
//				mFragment.setPlayPath(result,VideoDatabaseInfo info);
//				if (mVideoDbInfo.mType != VideoInfoDatabaseProxy.TYPE_MICRO) {
//					mVideoDbInfo.mDependency = mPath;
//				}
//			}
//		}
//
//	}

}