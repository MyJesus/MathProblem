package com.readboy.aliyunplayerlib.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.readboy.aliyunplayerlib.app.AliyunPlayerLibApp;

/**
 * Created by ldw on 2016/11/22.
 */

public class AppUtil {

    //测试包名
    private static String TEST_PACKAGE_NAME = null;

    public static int getVersionCode(Context context){
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            if (info != null && !TextUtils.isEmpty(info.versionName)) {
                return info.versionName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1.0";
    }

    public static void setTestPackageName(String testPackageName){
        TEST_PACKAGE_NAME = testPackageName;
    }

    public static String getPackageName(){
        if(!TextUtils.isEmpty(TEST_PACKAGE_NAME)){
            return TEST_PACKAGE_NAME;
        }else {
            Context context = AliyunPlayerLibApp.getContext();
            return context.getPackageName();
        }
    }

    /**
     * 获取当前调用地方的进程名字
     * 一般获取到的是自己包名的进程，也有可能一些第三方的，例如极光推送，会新开一个进程
     * @return
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }

    /**
     * 判断当前调用地方的进程是否是我包名的进程
     * @return
     */
    public static boolean isMyPackageProcess(Context context){
        String packageName = getPackageName();
        String curProcessName = getCurProcessName(context);
        if(!TextUtils.isEmpty(packageName)
                && !TextUtils.isEmpty(curProcessName)
                && packageName.equals(curProcessName)){
            return true;
        }
        return false;
    }

}
