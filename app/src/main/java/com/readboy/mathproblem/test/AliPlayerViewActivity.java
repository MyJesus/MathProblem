package com.readboy.mathproblem.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.readboy.aliyunplayerlib.utils.AliLogUtil;
import com.readboy.aliyunplayerlib.view.AliPlayerView;
import com.readboy.aliyunplayerlib.view.PlayerBottomViewDefault;
import com.readboy.aliyunplayerlib.view.PlayerCompleteViewDefault;
import com.readboy.aliyunplayerlib.view.PlayerTopViewDefault;
import com.readboy.mathproblem.R;
import com.readboy.mathproblem.util.ToastUtils;

import java.io.File;

public class AliPlayerViewActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "AliPlayerViewActivity";

    /**
     * vid方式播放
     */
    public static void startWithVid(Context context, String vid) {
        AliLogUtil.v("---startWithVid---"+vid);
        Intent intent = new Intent();
        intent.setClass(context, AliPlayerViewActivity.class);
        intent.putExtra("type", PLAY_TYPE_VID);
        intent.putExtra("vid", vid);
        context.startActivity(intent);
    }

    /**
     * 本地路径播放
     */
    public static void startWithLocalPath(Context context, String localPath) {
        Intent intent = new Intent();
        intent.setClass(context, AliPlayerViewActivity.class);
        intent.putExtra("type", PLAY_TYPE_LOCAL_PATH);
        intent.putExtra("localPath", localPath);
        context.startActivity(intent);
    }

    /**
     * 本地多个路径播放，比如需要多个连着播放
     */
    public static void startWithLocalPaths(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, AliPlayerViewActivity.class);
        intent.putExtra("type", PLAY_TYPE_LOCAL_PATHS);
        context.startActivity(intent);
    }

    private static final int PLAY_TYPE_VID = 1;
    private static final int PLAY_TYPE_LOCAL_PATH = 2;
    private static final int PLAY_TYPE_LOCAL_PATHS = 3;

    //播放类型
    private int mPlayType;

    private String mVid = null;
    private String mLocalPath = null;
    private String[] mLocalPaths = null;
    private int mCurrentLocalPathsIndex = 0;

    //本地视频路径
    private String localPath1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/名师辅导班/名师风采/刘丽琴.mp4";
    private String localPath2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/名师辅导班/名师风采/周傲.mp4";
    private String localPath3 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/名师辅导班/名师风采/沈园.mp4";
    private String localPath4 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/名师辅导班/名师风采/张丹丹.mp4";

    /**
     * 播放器控件
     */
    private AliPlayerView mAliPlayerView;

    /**
     * 播放控制界面顶部，可直接使用默认的PlayerTopViewDefault，
     * 或者自己参照PlayerTopViewDefault重写布局，只需继承PlayerTopViewBase即可。
     */
    private PlayerTopViewDefault mPlayerTopView;

    /**
     * 播放控制界面底部，可直接使用默认的PlayerBottomViewDefault，
     * 或者自己参照PlayerBottomViewDefault重写布局，只需继承PlayerBottomViewBase即可。
     */
    private PlayerBottomViewDefault mPlayerBottomView;

    /**
     * 播放结束界面，可直接使用默认的PlayerCompleteViewDefault，
     * 或者自己参照PlayerCompleteViewDefault重写布局，只需继承PlayerCompleteViewBase即可。
     */
    private PlayerCompleteViewDefault mPlayerCompleteView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: 1");
        setContentView(R.layout.activity_aliyun_player_view);
        //keepScreenOn();
        Log.e(TAG, "onCreate: 2");

        mPlayType = getIntent().getIntExtra("type", 0);
        if (mPlayType == PLAY_TYPE_VID) {
            mVid = getIntent().getStringExtra("vid");
        } else if (mPlayType == PLAY_TYPE_LOCAL_PATH) {
            mLocalPath = getIntent().getStringExtra("localPath");
        } else if (mPlayType == PLAY_TYPE_LOCAL_PATHS) {
            mLocalPaths = new String[]{localPath1, localPath2, localPath3, localPath4};
        } else {
            finish();
            return;
        }

        if (TextUtils.isEmpty(mLocalPath) && TextUtils.isEmpty(mVid) && (mLocalPaths == null || mLocalPaths.length == 0)) {
            finish();
            return;
        }

        //初始化播放器
        initPlayerView();


        //以下为有预处理需求的应用参考，比如需要预处理去网络获取vid信息，可以在预处理期间处理，或者自行处理都行。
        boolean needPreprocess = false;

        if (needPreprocess) {
            //预处理
            preprocess();
        } else {
            //播放
            playVideo();
        }
    }

    private void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.v(TAG, "---onNewIntent---");
    }


    @Override
    protected void onResume() {
        Log.v(TAG, "---onResume---");
        super.onResume();
        if (mAliPlayerView != null) {
            mAliPlayerView.onResume();
        }
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "---onPause---");
        super.onPause();
        if (mAliPlayerView != null) {
            mAliPlayerView.onPause();
        }
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "---onStop---");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "---onDestroy---");
        super.onDestroy();
        if (mAliPlayerView != null) {
            mAliPlayerView.setKeepScreenOn(false);
            mAliPlayerView.onDestroy();
            mAliPlayerView = null;
        }
        mPlayerTopView = null;
        mPlayerBottomView = null;
        mPlayerCompleteView = null;

        stopPreprocessSimulation();
    }

    /**
     * 初始化播放控件
     */
    private void initPlayerView() {
        //初始化播放器控件。必须
        mAliPlayerView = (AliPlayerView) findViewById(R.id.ali_player_view);

        //保持屏幕常亮，一般播放界面建议设置常亮，特殊情况自行考虑。选用
        mAliPlayerView.setKeepScreenOn(true);

        //打开阿里打印，即阿里播放器底层相关的打印，前期测试阶段建议打开。已改到Application中设置了。选用
        mAliPlayerView.enableNativeLog();

        //自定义界面，不需要自定义可以置null。选用
        mPlayerTopView = new PlayerTopViewDefault(this);
        mPlayerBottomView = new PlayerBottomViewDefault(this);
        mPlayerCompleteView = new PlayerCompleteViewDefault(this);

        //init必须调用，就算为null也得调用。必须
        mAliPlayerView.init(mPlayerTopView, mPlayerBottomView, mPlayerCompleteView);

        //自定义控件自行处理，选用
        mPlayerTopView.getBackView().setOnClickListener(this);
        mPlayerTopView.setTitle("视频名称");
        mPlayerCompleteView.getBackView().setOnClickListener(this);
        mPlayerCompleteView.getContinueView().setOnClickListener(this);
        mPlayerCompleteView.getCancelView().setOnClickListener(this);

        //播放结束回调，选用
        mAliPlayerView.setOnPlayCompleteListener(new AliPlayerView.OnPlayCompleteListener() {
            @Override
            public void onPlayCompleteListener() {
                Log.v(TAG, "---onPlayCompleteListener---播放时间：" + mAliPlayerView.getPlayTimeSec() + "秒");
                //多个本地视频播放的情况
                if (mPlayType == PLAY_TYPE_LOCAL_PATHS) {
                    mCurrentLocalPathsIndex++;
                    if (mCurrentLocalPathsIndex >= mLocalPaths.length) {
                        mCurrentLocalPathsIndex = 0;
                    }
                    mAliPlayerView.playWithPath(mLocalPaths[mCurrentLocalPathsIndex]);
                    mPlayerTopView.setTitle("视频名称 第" + (mCurrentLocalPathsIndex + 1) + "个");
                    ToastUtils.show("自动播放下一个");
                }
            }
        });
    }

    /**
     * 开始播放
     */
    private void playVideo() {
        Log.v(TAG, "---playVideo---");
        //播放
        if (mPlayType == PLAY_TYPE_VID) {
            mAliPlayerView.playWithVid(mVid);
        } else if (mPlayType == PLAY_TYPE_LOCAL_PATH) {
            File file = new File(mLocalPath);
            if (file.exists() && file.isFile()) {
                mAliPlayerView.playWithPath(mLocalPath);
            } else {
                ToastUtils.show("文件不存在");
            }
        } else if (mPlayType == PLAY_TYPE_LOCAL_PATHS) {
            boolean fileExist = true;
            for (String path : mLocalPaths) {
                File file = new File(path);
                if (!file.exists()) {
                    fileExist = false;
                    break;
                }
            }
            if (fileExist) {
                mCurrentLocalPathsIndex = 0;
                mAliPlayerView.playWithPath(mLocalPaths[mCurrentLocalPathsIndex]);
            } else {
                ToastUtils.show("有文件不存在");
            }
        }
    }

    //预处理
    private void preprocess() {
        Log.v(TAG, "---preprocess---");
        //设置预处理状态
        mAliPlayerView.setPreprocess();
        //预处理
        startPreprocessSimulation();
        //预处理出错界面点击处理
        mAliPlayerView.setOnPreprocessBtnListener(new AliPlayerView.OnPreprocessBtnListener() {
            @Override
            public void onPreContinueBtnClick() {
                Log.v(TAG, "---onPreContinueBtnClick---");
                //继续进行预处理
                mAliPlayerView.setPreprocess();
                startPreprocessSimulation();
            }

            @Override
            public void onPreCancelBtnClick() {
                Log.v(TAG, "---onPreCancelBtnClick---");
                //退出
                finish();
            }
        });
    }

    private Handler handler = new Handler();
    private Runnable runnable;

    //开始预处理模拟
    private void startPreprocessSimulation() {
        Log.v(TAG, "---startPreprocessSimulation---");
        runnable = new Runnable() {
            @Override
            public void run() {
                if (Math.random() > 0.5) {
                    Log.v(TAG, "---startPreprocessSimulation---success");
                    //预处理成功，播放
                    playVideo();
                } else {
                    Log.v(TAG, "---startPreprocessSimulation---fail");
                    //预处理失败
                    mAliPlayerView.setPreprocessError("预处理失败");
                }
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    //停止预处理模拟
    private void stopPreprocessSimulation() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }


    @Override
    public void onClick(View v) {
        if (v == mPlayerTopView.getBackView()) {
            finish();
        } else if (v == mPlayerCompleteView.getBackView()) {
            finish();
        } else if (v == mPlayerCompleteView.getContinueView()) {
            mAliPlayerView.replay();
        } else if (v == mPlayerCompleteView.getCancelView()) {
            finish();
        }
    }
}
