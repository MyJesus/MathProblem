package com.readboy.mathproblem.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.alivc.player.AliVcMediaPlayer;

import com.aliyun.vodplayer.core.avmplayer.AVMPlayer;
import com.aliyun.vodplayer.downloader.AliyunDownloadConfig;
import com.aliyun.vodplayer.downloader.AliyunDownloadManager;
import com.readboy.aliyunplayerlib.utils.AliLogUtil;
import com.readboy.aliyunplayerlib.utils.AppUtil;
import com.readboy.aliyunplayerlib.utils.DataSnUtil;
import com.readboy.auth.Auth;
import com.readboy.mathproblem.cache.CacheEngine;
import com.readboy.mathproblem.cache.PicassoWrapper;
import com.readboy.mathproblem.http.OkHttp3Downloader;
import com.readboy.mathproblem.http.auth.AuthCallback;
import com.readboy.mathproblem.http.auth.AuthManager;
import com.readboy.mathproblem.util.FileUtils;
import com.readboy.textbook.util.MyApplication;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;

/**
 * Created by oubin on 2017/9/21.
 * 全屏播放界面是新开的进程，主要可能创建多个Application实例。
 */

public class MathApplication extends MyApplication {
    private static final String TAG = "MathApplication";

    private static volatile MathApplication instance = null;

    public RefWatcher mRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        BuildConfig.checkApkInDebug(this);
        new Auth(this);
        CrashReport.initCrashReport(getApplicationContext(), "e1e55bf8e9", false);
        if (BuildConfig.DEBUG) {
            mRefWatcher = setupLeakCanary();
        }
        String mode = Build.MODEL;
        Log.e(TAG, "onCreate: mode = " + mode);
//        String deviceId = BuildUtils.getDeviceId();
//        Auth.setParameters("device_id", deviceId);

        instance = this;
        CacheEngine.intiCacheEngine();
        Log.e(TAG, "onCreate: ");
        //初始化播放器。不初始化，错误字符串将获取不到。

        //文件下载框架初始化
//        initDownloader();
        initAliDownloader();
        initAliPayer();

        //初始化图片加载框架，picasso
        PicassoWrapper.initPicasso(this);

        String uri = "test";
//        initAuthManager(uri);

        //不对旧视频处理，4.1.17版本之前的视频无法使用阿里云播放器播放
//        File file = new File(Constants.VIDEO_PATH);
//        if (file.exists()){
//            asyncDeleteOldVideo();
//        }

    }

    private void initAliPayer() {
        AliVcMediaPlayer.init(getApplicationContext());
//        VcPlayerLog.enableLog();
//        AVMPlayer.enableNativeLog();
//        AliLogUtil.enableLog();
        //设置AppSecret，签名用，跟大数据部申请。必须
        DataSnUtil.setAppSecret("be916db6f0771c9053e5f44106c358b4");
        //名师辅导班的AppSecret，其他应用一定要改成自己的
//        DataSnUtil.setAppSecret("8e0fdd8110397e71aeac642a6095fe44");
        //设置测试包名，此处只为了方便demo测试才进行设置，其他应用不需设置，因为默认会找到应用的包名。不需
        AppUtil.setTestPackageName("com.readboy.mathproblem");
//        AppUtil.setTestPackageName("com.dream.tutorsplan");

    }

    private RefWatcher setupLeakCanary() {
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return RefWatcher.DISABLED;
//        }
        Log.e(TAG, "setupLeakCanary: ");
        return LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        MathApplication leakApplication = (MathApplication) context.getApplicationContext();
        return leakApplication.mRefWatcher;
    }

    public static void refWatch(Context context) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "refWatch: ");
            RefWatcher refWatcher = getRefWatcher(context);
            if (refWatcher != null) {
                refWatcher.watch(context);
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.e(TAG, "attachBaseContext: ");
        MultiDex.install(this);
    }

    private void initAuthManager(String uri) {
        AuthManager.registerAuth(this, uri, new AuthCallback() {
            @Override
            public void onAuth(String url) {

            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    public static MathApplication getInstance() {
        return instance;
    }

    private void initAliDownloader() {
//        copySecretFile(this);
        //设置保存密码。此密码如果更换，则之前保存的视频无法播放
        AliyunDownloadConfig config = new AliyunDownloadConfig();
        config.setSecretImagePath(Constants.getSecretImagePath(this));
//        config.setDownloadPassword("123456789");
        //设置保存路径。请确保有SD卡访问权限。
        config.setDownloadDir(Constants.getDownloadPath(this));
        //设置同时下载个数
        config.setMaxNums(3);

        AliyunDownloadManager.getInstance(this).setDownloadConfig(config);
    }

    public static void initFile(Context context) {
        copySecretFile(context);
        createNoMediaFile(context);
    }

    public static void copySecretFile(Context context) {
        File file = new File(Constants.getSecretImagePath(context));
        if (!file.exists()) {
            boolean result = FileUtils.copyAssetsToSD(context, Constants.ALIYUN_SECRET_IMAGE_NAME,
                    file.getAbsolutePath());
            Log.e(TAG, "copySecretFile: result = " + result);
        }
    }

    public static void createNoMediaFile(Context context) {
        File file = new File(Constants.getDownloadPath(context), ".nomedia");
        if (!file.exists()) {
            FileUtils.createNoMediaFile(file.getParent());
        }
    }

    private void asyncDeleteOldVideo() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                Log.e(TAG, "doInBackground: ");
                File file = new File(Constants.VIDEO_PATH);
                Log.e(TAG, "doInBackground: " + file.delete());
                return null;
            }
        }.execute();
    }

    /**
     * 初始化图片下载框架，picasso
     */
    private void initPicasso() {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        Picasso.Builder builder = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(client));
        Picasso.setSingletonInstance(builder.build());
    }

    public static boolean shouldUpdateCache(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        long lastTime = preferences.getLong(CacheEngine.KEY_LAST_UPDATE_TIME, 0);
        return System.currentTimeMillis() - lastTime > CacheEngine.DEFAULT_UPDATE_PERIOD;
    }

}
