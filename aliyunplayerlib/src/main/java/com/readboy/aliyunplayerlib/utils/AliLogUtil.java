package com.readboy.aliyunplayerlib.utils;

import android.util.Log;

/**
 * 打印工具类
 * Created by ldw on 2018/3/21.
 */

public class AliLogUtil {
    private static final String TAG = "AliLogUtil";

    private static boolean LOG_ENABLE = false;

    public static void enableLog(){
        LOG_ENABLE = true;
    }

    public static void disableLog(){
        LOG_ENABLE = false;
    }

    public static void v(String tag, String msg){
        if(LOG_ENABLE){
            Log.v(tag, msg);
        }
    }

    public static void i(String tag, String msg){
        if(LOG_ENABLE){
            Log.i(tag, msg);
        }
    }

    public static void e(String tag, String msg){
        if(LOG_ENABLE){
            Log.e(tag, msg);
        }
    }

    public static void v(String msg){
        v(TAG, msg);
    }

    public static void i(String msg){
        i(TAG, msg);
    }

    public static void e(String msg){
        e(TAG, msg);
    }

}
