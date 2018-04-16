package com.readboy.mathproblem.application;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.*;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadLog;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.readboy.auth.Auth;
import com.readboy.mathproblem.cache.CacheEngine;
import com.readboy.mathproblem.cache.PicassoWrapper;
import com.readboy.mathproblem.http.OkHttp3Downloader;
import com.readboy.mathproblem.http.auth.AuthCallback;
import com.readboy.mathproblem.http.auth.AuthManager;
import com.readboy.mathproblem.http.download.DownloadContract;
import com.readboy.textbook.util.MyApplication;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.concurrent.TimeUnit;

import cn.dreamtobe.filedownloader.OkHttp3Connection;
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

        //文件下载框架初始化
        initDownloader();

        //初始化图片加载框架，picasso
        PicassoWrapper.initPicasso(this);

        String uri = "test";
//        initAuthManager(uri);

    }

    private RefWatcher setupLeakCanary() {
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return RefWatcher.DISABLED;
//        }
        return LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        MathApplication leakApplication = (MathApplication) context.getApplicationContext();
        return leakApplication.mRefWatcher;
    }

    public static void refWatch(Context context) {
        if (BuildConfig.DEBUG) {
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

    //初始化下载框架
    private void initDownloader() {

        FileDownloadLog.NEED_LOG = true;

        //使用OkHttpClient作为下载引擎，切记不可添加HttpLoggingInterceptor,影响下载速度。
        //使用其他Interceptor时，可以考虑清楚，导致的原因待分析。
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(60_000, TimeUnit.MILLISECONDS)
                .readTimeout(50_000, TimeUnit.MILLISECONDS)
                .writeTimeout(50_000, TimeUnit.MILLISECONDS);

        FileDownloader.setupOnApplicationOnCreate(this)
                .connectionCreator(new OkHttp3Connection.Creator(builder))
                .commit();

        //使用默认的下载引擎
//        FileDownloader.setupOnApplicationOnCreate(this)
//                .connectionCreator(new FileDownloadUrlConnection
//                        .Creator(new FileDownloadUrlConnection.Configuration()
//                        .connectTimeout(15_000) // set connection timeout.
//                        .readTimeout(15_000) // set read timeout.
//                        .proxy(Proxy.NO_PROXY) // set proxy
//                ))
//                .commit();

        //设置最大下载线程
        FileDownloader.getImpl().setMaxNetworkThreadCount(DownloadContract.MAX_NETWORK_THREAD_COUNT);

        FileDownloadUtils.setDefaultSaveRootPath(Constants.VIDEO_PATH);

    }


    //初始化图片下载框架，picasso
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
