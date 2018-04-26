package com.readboy.mathproblem.application;

import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * Created by oubin on 2018/1/12.
 */

public final class BuildConfig {
//    public static boolean APP_DEBUG = true;

    public static boolean DEBUG = false;
    public static final String APPLICATION_ID = "com.readboy.mathproblem";
    public static final String BUILD_TYPE = "debug";
    public static final String FLAVOR = "";
    public static final int VERSION_CODE = 180110001;
    public static final String VERSION_NAME = "4.1.15";

    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查是否是debug版本，某些代码只在debug版本上运行
     */
    public static void checkApkInDebug(Context context) {
        DEBUG = isApkInDebug(context);
    }
}
