package com.readboy.mathproblem.video.movie;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import com.readboy.mathproblem.R;
import com.readboy.mathproblem.video.proxy.MidroVideoUrl;
import com.readboy.mathproblem.video.proxy.MovieFile;
import com.readboy.mathproblem.video.tools.Constant;
import com.readboy.mathproblem.video.db.VideoDatabaseInfo;
import com.readboy.mathproblem.video.db.VideoInfoDatabaseProxy;
import com.readboy.video.proxy.HttpGetProxy;

import java.io.IOException;
import java.util.ArrayList;

import static com.readboy.mathproblem.video.movie.MovieActivity.initVideoDbInfo;
import static com.readboy.mathproblem.video.proxy.MovieFile.saveVideoInfo;

//import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
//import io.vov.vitamio.MediaPlayer.OnCompletionListener;
//import io.vov.vitamio.MediaPlayer.OnPreparedListener;
//import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;
//import io.vov.vitamio.MediaPlayer;
//import io.vov.vitamio.Vitamio;
//import io.vov.vitamio.Metadata;

public class FloatingWindowService extends Service implements OnClickListener,
        OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener, OnVideoSizeChangedListener, SurfaceHolder.Callback {

    public final static String TAG = FloatingWindowService.class.getSimpleName();
    public final static String FLOATWIN_SHOW = "floatwinshow";

    private static boolean isRunning = false;

    View mContentView;
    View mTop;
    View mBottom;
    SeekBar mSeekbar;
    private MediaPlayer mMediaPlayer;
    private SurfaceView mPreview;
    private SurfaceHolder holder;

    private int mVideoWidth;
    private int mVideoHeight;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;
    private boolean mExit = true;

    private boolean mUserPresent = true;
    private boolean mBtnCanClick = true;

    WindowManager mWm;
    WindowManager.LayoutParams mWmParams;

    private HeartThread mThread;
    private boolean mIsRunning = true;
    ScreenLockReceiver mScreenLockReceiver;

    float mTouchStartX;
    float mTouchStartY;

    GestureDetector mGesture;

    private int mIndex = 0;
    private long mPosition = 0;
    private String mPath = null;
    private ArrayList<String> mPathList = null;

    boolean isFromUser = false;
    boolean isHiding = false;
    boolean isUrl = false;
    /**
     * 电话活跃
     */
    boolean mPhoneActive = false;
    private HttpGetProxy mProxy;

    private PowerManager.WakeLock mWakeLock;
    private RemoteControlClient mRemoteControlClient = null;

    VideoDatabaseInfo mVideoDbInfo = new VideoDatabaseInfo();
    /**
     * 监听电话状态
     */
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {  // 响铃
                mPhoneActive = true;
                if (mMediaPlayer != null) {
                    mMediaPlayer.pause();
                    isFromUser = true;
                    mContentView.findViewById(R.id.play).setVisibility(View.VISIBLE);
                    mContentView.findViewById(R.id.pause).setVisibility(View.GONE);
                }
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {  //  接听
                mPhoneActive = true;
                if (mMediaPlayer != null) {
                    mMediaPlayer.pause();
                }
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {  // 空闲
                mPhoneActive = false;
                // start playing again
                if (mMediaPlayer != null) {
//	            	mMediaPlayer.start();
                }
            }
        }
    };

    public static boolean isRunning() {
        return isRunning;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, " -------- onBind");
        return null;
    }


    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        Log.d(TAG, " -------- onStart");
    }

    @Override
    public void onRebind(Intent intent) {
        // TODO Auto-generated method stub
        super.onRebind(intent);
        Log.d(TAG, " -------- onRebind");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, " -------- onTaskRemoved");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, " -------- onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        mScreenLockReceiver = new ScreenLockReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(ScreenLockReceiver.CHANGE_ACTION);
        filter.addAction(ScreenLockReceiver.FLOAT_VIEW_CLOSE);
        filter.addAction(MediaButtonIntentReceiver.ACTION_PAUSE);
        filter.addAction(MediaButtonIntentReceiver.ACTION_START);
        filter.addAction(MediaButtonIntentReceiver.ACTION_START_PAUSE);
//		filter.addAction("com.android.intent.action.CAMERA_BUTTON");

        registerReceiver(mScreenLockReceiver, filter);
        Log.d(TAG, " -------- onCreate instance: " + this);

        SharedPreferences sharePreference = getSharedPreferences(MovieActivity.DREAM_PLAYER_SHADER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePreference.edit();
        editor.putBoolean(FLOATWIN_SHOW, true);
        editor.apply();
        TelephonyManager tmgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tmgr.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        mRemoteControlClient = MediaButtonIntentReceiver.registerMediaButton(this);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
        mWakeLock.setReferenceCounted(false);
        mWakeLock.acquire();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, " -------- onStartCommand instance" + this);
        parseIntent(intent);
        createView();
        return super.onStartCommand(intent, flags, startId);
    }


    private void createView() {
        Log.d(TAG, " -------- createView instance" + this);
        mGesture = new GestureDetector(getApplicationContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        Log.d(TAG, "onSingleTap");
                        if (isHiding) {
                            isHiding = false;
                            mTop.setVisibility(View.VISIBLE);
                            mBottom.setVisibility(View.VISIBLE);
                        } else {
                            isHiding = true;
                            mTop.setVisibility(View.INVISIBLE);
                            mBottom.setVisibility(View.INVISIBLE);
                        }
                        return true;
                    }

                    @Override
                    public boolean onDoubleTapEvent(MotionEvent e) {
                        Log.d(TAG, "onDoubleTapEvent");
                        if (e.getAction() == MotionEvent.ACTION_UP) {
                            gotoMovieActivity();
                        }
                        return true;
                    }
                });
        mContentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.float_window, null);
        mContentView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x, y;
                mGesture.onTouchEvent(event);
                x = event.getRawX();
                y = event.getRawY() - Constant.statusBarHeight;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateVidePosition((int) (x - mTouchStartX),
                                (int) (y - mTouchStartY));
                        break;
                    case MotionEvent.ACTION_UP:
                        updateVidePosition((int) (x - mTouchStartX),
                                (int) (y - mTouchStartY));
                        mTouchStartX = mTouchStartY = 0;
                        break;
                }
                return true;
            }
        });

        mTop = mContentView.findViewById(R.id.top);
        mBottom = mContentView.findViewById(R.id.bottom);
        mSeekbar = (SeekBar) mContentView.findViewById(R.id.seek_bar);

        int[] clickables = {R.id.back, R.id.prev, R.id.play, R.id.pause, R.id.next, R.id.gotomovie};
        for (int ctrl : clickables) {
            View v = mContentView.findViewById(ctrl);
            if (v != null) {
                v.setOnClickListener(this);
            }
        }

        mWm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mWmParams = new WindowManager.LayoutParams();
        mWmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//		mWmParams.format = PixelFormat.RGB_888;
        mWmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWmParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWmParams.x = (int) getResources().getDimension(R.dimen.float_window_margin_left); //Constant.WINDOW_WIDTH / 4;
        mWmParams.y = (int) getResources().getDimension(R.dimen.float_window_margin_top);
        mWmParams.width = (int) getResources().getDimension(R.dimen.float_window_w);//Constant.WINDOW_WIDTH * 2 / 3;
        mWmParams.height = (int) getResources().getDimension(R.dimen.float_window_h);
        mWm.addView(mContentView, mWmParams);

        mPreview = (SurfaceView) mContentView.findViewById(R.id.surface_view);
        holder = mPreview.getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.RGBA_8888);

        mIsRunning = true;
        if (mThread != null) {
            mThread.pause();
        }
        mThread = new HeartThread(mHandler);
        mThread.start();
    }

    private void parseIntent(Intent intent) {
        if (intent == null) {
//			stopSelf();
            stopService();
            return;
        }
        mPath = intent.getStringExtra("path");
        mPosition = intent.getLongExtra("position", 0);
        isUrl = intent.getBooleanExtra("isUrl", false);
        mPathList = intent.getStringArrayListExtra("medialist");
        if (mPathList == null) {
            mPathList = new ArrayList<String>();
            mPathList.add(mPath);
        }
        for (int i = 0; i < mPathList.size(); i++) {
            if (mPathList.get(i).equals(mPath)) {
                mIndex = i;
                break;
            }
        }

        if (isUrl) {
            mVideoDbInfo.mType = VideoInfoDatabaseProxy.TYPE_URL;
        }
        mVideoDbInfo = initVideoDbInfo(intent, mVideoDbInfo);

    }

    private void updatePlay() {
        Log.i(TAG, "-------- FloatingWindowServicexxx updatePlay start : ");
        mBtnCanClick = false;
        releaseMediaPlayer();
        doCleanUp();
        if (mPath == null) {
//			stopSelf();
            stopService();
            return;
        }
        Log.i(TAG, "-------- FloatingWindowServicexxx mPath: " + mPath);
        boolean encryption = MovieFile.isEncryption(this, mPath);
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        sendBroadcast(i);

//		mMediaPlayer = new MediaPlayer(getApplicationContext());
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);

        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        boolean isproxy = encryption || mVideoDbInfo.mType == VideoInfoDatabaseProxy.TYPE_URL;
        new AsynVideoSetDataSource().execute(isproxy);

        mMediaPlayer.setDisplay(holder);
        Log.i(TAG, "-------- FloatingWindowServicexxx updatePlay end encryption: " + encryption);
        /*FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) mVideoContainer.getLayoutParams();
		params1.width = mWmParams.width;
		params1.height = params1.width * mMediaPlayer.getVideoHeight() / mMediaPlayer.getVideoWidth();
		mVideoContainer.setLayoutParams(params1);

		CenterLayout.LayoutParams params2 = (CenterLayout.LayoutParams) mPreview.getLayoutParams();
		params2.width = mWmParams.width;
		params2.height = params2.width * mMediaPlayer.getVideoHeight() / mMediaPlayer.getVideoWidth();
		mPreview.setLayoutParams(params2);*/

        //updateInterface();
    }

    private void updateInterface() {
        if (mMediaPlayer == null) {
            return;
        }
        if (!(mIsVideoReadyToBePlayed && mIsVideoSizeKnown)) {
            return;
        }

        if (mMediaPlayer.isPlaying()) {
            mContentView.findViewById(R.id.play).setVisibility(View.GONE);
            mContentView.findViewById(R.id.pause).setVisibility(View.VISIBLE);
        } else {
            mContentView.findViewById(R.id.play).setVisibility(View.VISIBLE);
            mContentView.findViewById(R.id.pause).setVisibility(View.GONE);
        }

        mSeekbar.setProgress(mMediaPlayer.getCurrentPosition());
    }

    public void updateVidePosition(int pX, int pY) {
        mWmParams.x = pX;
        mWmParams.y = pY;
        mWm.updateViewLayout(mContentView, mWmParams);
    }

    public String getNextMediaPath(boolean increase) {
        if (increase) {
            if (++mIndex >= mPathList.size()) {
                mIndex = 0;
            }
        } else {
            if (--mIndex < 0) {
                mIndex = mPathList.size() - 1;
            }
        }

        return mPathList.get(mIndex);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, " -------- onDestroy instance: " + this);
        if (mThread != null) {
            mThread.pause();
        }

        mWakeLock.release();
        if (mWm != null && mContentView != null) {
            mWm.removeView(mContentView);
        }
        isRunning = false;
        unregisterReceiver(mScreenLockReceiver);

//		releaseMediaPlayer();
        doCleanUp();

        if (mRemoteControlClient != null) {
            MediaButtonIntentReceiver.unregisterMediaButton(this, mRemoteControlClient);
        }
        mIsRunning = false;
//		try {
//			mThread.join(3000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
        SharedPreferences sharePreference = getSharedPreferences(MovieActivity.DREAM_PLAYER_SHADER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePreference.edit();
        editor.putBoolean(FLOATWIN_SHOW, false);
        editor.apply();
        if (mExit) {
//            System.exit(0);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int percent) {
//        Log.e(TAG, " -------- onBufferingUpdate percent:" + percent);

    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        Log.e(TAG, "-------- FloatingWindowServicexxx onCompletion called");
        mHandler.sendEmptyMessage(VideoMessage.FINISHED);
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.e(TAG, " --------- FloatingWindowServicexxx onVideoSizeChanged called");
        if (width == 0 || height == 0) {
            Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
            return;
        }
        mIsVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaplayer) {
        Log.d(TAG, " -------- onPrepared called mUserPresent: " + mUserPresent);
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
        mBtnCanClick = true;
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        mUserPresent = !keyguardManager.inKeyguardRestrictedInputMode();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
        Log.d(TAG, "surfaceChanged called this: " + this);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        Log.d(TAG, "surfaceDestroyed called this: " + this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated called holder: " + holder);
        updatePlay();
    }

    public void stopService() {
        releaseMediaPlayer();
        doCleanUp();
        if (mWm != null && mContentView != null) {
            mWm.removeView(mContentView);
        }
        stopSelf();
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
//			saveMoviePlayInfo(mPath, mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration());
            mPosition = mMediaPlayer.getCurrentPosition();
            Intent othersintent = new Intent("com.readboy.mathproblem.video.playinfo");
            if (othersintent != null) {
                othersintent.putExtra("path", mPath);
                othersintent.putExtra("position", mMediaPlayer.getCurrentPosition());
                othersintent.putExtra("duration", mMediaPlayer.getDuration());
                sendBroadcast(othersintent);
            }
//			Metadata meda = mMediaPlayer.getMetadata();
//			if (meda != null) {
//				saveVideoInfo(mVideoDbInfo, mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration(), meda.getInt(Metadata.LENGTH));
//			}
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }

    private void startVideoPlayback() {
        Log.v(TAG, "startVideoPlayback mUserPresent: " + mUserPresent);

        int width = mWmParams.width;
        int height = width * mVideoHeight / mVideoWidth;
        holder.setFixedSize(width, height);

        if (mPosition > 0) {
//			mMediaPlayer.seekTo(mPosition);
        }
        if (mUserPresent) {
            mMediaPlayer.start();
        }

        mSeekbar.setMax(mMediaPlayer.getDuration());
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.play) {
            if (!mPhoneActive) {
                videoStart();
            } else {
                Toast.makeText(this, "通话中无法播放", Toast.LENGTH_LONG).show();
            }

        } else if (i == R.id.pause) {
            if (mMediaPlayer.isPlaying()) {
//				isFromUser = true;
//				mMediaPlayer.pause();
//				mContentView.findViewById(R.id.play).setVisibility(View.VISIBLE);
//				mContentView.findViewById(R.id.pause).setVisibility(View.GONE);
                videoPause();
            }

        } else if (i == R.id.prev) {
            if (!mBtnCanClick) {
                return;
            }
            if (mIndex > 0) {
//				saveMoviePlayInfo(mPath, mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration());
//				saveVideoInfo(mVideoDbInfo, mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration(), mMediaPlayer.getMetadata().getInt(Metadata.LENGTH));
                mPath = getNextMediaPath(false);
                mPosition = 0;
                updatePlay();
            } else {
                Toast.makeText(this, "没有多余的视频了", Toast.LENGTH_LONG).show();
            }

        } else if (i == R.id.next) {
            if (!mBtnCanClick) {
                return;
            }
            if (mIndex < mPathList.size() - 1) {
//				saveMoviePlayInfo(mPath, mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration());
//				saveVideoInfo(mVideoDbInfo, mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration(), mMediaPlayer.getMetadata().getInt(Metadata.LENGTH));
                mPath = getNextMediaPath(true);
                mPosition = 0;
                updatePlay();
            } else {
                Toast.makeText(this, "没有多余的视频了", Toast.LENGTH_LONG).show();
            }

        } else if (i == R.id.gotomovie) {
            gotoMovieActivity();

        } else if (i == R.id.back) {
            stopService();

        }
    }

    private void gotoMovieActivity() {
        mExit = false;
        Intent intent = new Intent(this, MovieActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isUrl) {
            intent.putExtra("url", mPath);
        } else {
            intent.putExtra("path", mPath);
        }
        long curposition = mPosition;
        if (mMediaPlayer != null) {
            curposition = mMediaPlayer.getCurrentPosition();
        }
        intent.putExtra("position", curposition);
        intent.putStringArrayListExtra("medialist", mPathList);

        intent.putExtra("databasepath", mVideoDbInfo.mDataPath);
        intent.putExtra("databasename", mVideoDbInfo.mDataName);
        intent.putExtra("cachepath", mVideoDbInfo.mCachePath);
//		intent.putExtra("id", mVid);

        startActivity(intent);

        stopService();
    }

    private void videoPause() {
        if (mMediaPlayer != null) {
            isFromUser = true;
            mMediaPlayer.pause();
            mPosition = mMediaPlayer.getCurrentPosition();
        }
        mContentView.findViewById(R.id.play).setVisibility(View.VISIBLE);
        mContentView.findViewById(R.id.pause).setVisibility(View.GONE);
    }

    private void videoStart() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            isFromUser = false;
            mMediaPlayer.start();
            mContentView.findViewById(R.id.play).setVisibility(View.GONE);
            mContentView.findViewById(R.id.pause).setVisibility(View.VISIBLE);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VideoMessage.READY:
                    Log.i(TAG, "-------- FloatingWindowServicexxx mHandler Ready ");
                    updatePlay();
                    break;
                case VideoMessage.FINISHED:
                    mPath = getNextMediaPath(true);
                    mPosition = 0;
//				saveMoviePlayInfo(mPath, mMediaPlayer.getDuration(), mMediaPlayer.getDuration());
                    if (mMediaPlayer != null) {
//					Metadata meta = mMediaPlayer.getMetadata();
                        long length = 0;
//					if (meta != null) {
//						length = meta.getInt(Metadata.LENGTH);
//					}
                        saveVideoInfo(mVideoDbInfo, mMediaPlayer.getDuration(), mMediaPlayer.getDuration(), length);
                    }
                    if (mPathList != null && mPathList.size() > 1) {
                        updatePlay();
                    } else {
                        stopService();
                    }
                    break;
                case VideoMessage.HEART:
                    updateInterface();
                    if (mIsVideoReadyToBePlayed) {
                        if (isFromUser) {
                            videoPause();
                        }
                    }
                    if (!mUserPresent && !isFromUser) { // 防止屏保还播放
                        videoPause();
                    }
                    break;
                case VideoMessage.ERROR:
                    new AlertDialog.Builder(getApplicationContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.ERROR)
                            .setMessage(R.string.cantplay)
                            .setOnCancelListener(new OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
//								stopSelf();
                                    stopService();
                                }
                            })
                            .setPositiveButton(R.string.OK,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
//										stopSelf();
                                            stopService();
                                        }
                                    }).show();
                    break;
            }
        }
    };

    public class ScreenLockReceiver extends BroadcastReceiver {

        public final static String CHANGE_ACTION = "com.readboy.mathproblem.video.dreamvideoplayer.CHANGE";
        public final static String FLOAT_VIEW_CLOSE = "android.intent.action.floatviewclose";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "************** onReceive action: " + intent.getAction());
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                videoPause();
                mUserPresent = false;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

            } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                mUserPresent = true;
//				if (mMediaPlayer != null && !isFromUser)
//					mMediaPlayer.start();
//				Log.i(TAG, "-------- FloatingWindowServicexxx mHandler ACTION_USER_PRESENT ");
            } else if (intent.getAction().equals(CHANGE_ACTION)) {
                if (mMediaPlayer != null) {
                    Log.i(TAG, "-------- FloatingWindowServicexxx mHandler CHANGE_ACTION ");
                    parseIntent(intent);
                    updatePlay();
                }
            } else if (intent.getAction().equals(FLOAT_VIEW_CLOSE)) {
                mExit = false;
                stopService();
            } else if (intent.getAction().equals(MediaButtonIntentReceiver.ACTION_PAUSE)) {
                if (mIsVideoReadyToBePlayed && mUserPresent) {
                    videoPause();
                }
            } else if (MediaButtonIntentReceiver.ACTION_START_PAUSE.equals(intent.getAction())) {
                if (mIsVideoReadyToBePlayed && mUserPresent) {
                    if (isFromUser && !mPhoneActive) {
                        videoStart();
                    } else {
                        videoPause();
                    }
                }
            } else if ("com.android.intent.action.CAMERA_BUTTON".equals(intent.getAction())) {
                Log.e(TAG, "camera come");
//				stopSelf();
                stopService();
            }
        }
    }

    private class AsynVideoSetDataSource extends AsyncTask<Boolean, Void, String> {

        @Override
        protected String doInBackground(Boolean... params) {
            if (mProxy != null) {
                mProxy.stopProxy();
            }
            Log.d("", " ---- AsynVideoSetDataSource doInBackground: params[0]: " + params[0]);
            String back = mPath;
            if (params[0]) {
                if (isUrl) {
                    String md5 = MidroVideoUrl.getMd5(mPath);
                    String cachepath = MovieFile.getCachePath(getBaseContext(), mVideoDbInfo.mCachePath, md5);
                    mProxy = new HttpGetProxy(getBaseContext(), mPath, cachepath, md5, isUrl, null);
                } else {
                    mProxy = new HttpGetProxy(getBaseContext(), mPath, null);
                }
                back = mProxy.getLocalURL();
            }

            return back;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
//			mVideoView.setVideoURI(Uri.parse(proxy.getLocalURL()));
            try {
                Log.d("FloatingWindowService", " ---- AsynVideoSetDataSource onPostExecute: holder: " + holder);
                Uri uri = Uri.parse(result);
                Log.d("FloatingWindowService", " ---- AsynVideoSetDataSource onPostExecute: result: " + result + ", uri: " + uri);
                mMediaPlayer.setDataSource(result);
//				mMediaPlayer.setDataSource(getApplicationContext(), uri, null);
                mMediaPlayer.prepareAsync();
                if (mVideoDbInfo.mType != VideoInfoDatabaseProxy.TYPE_MICRO) {
                    mVideoDbInfo.mDependency = mPath;
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
