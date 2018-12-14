package com.readboy.aliyunplayerlib.app;

import android.content.Context;
import android.support.annotation.NonNull;

import com.alivc.player.AliVcMediaPlayer;
import com.readboy.aliyunplayerlib.utils.AliLogUtil;
import com.readboy.auth.Auth;

public class AliPlayerAppUtil {

    private static Context mAppContext;

    private static boolean mSdkLogEnable = false;


    /**
     * 初始化，必须在Application里面先调用
     * @param context
     */
    public static void init(@NonNull Context context){
        mAppContext = context.getApplicationContext();

        //初始化播放器。不初始化，错误字符串将获取不到。
        AliVcMediaPlayer.init(mAppContext);

        //鉴权相关
        new Auth(mAppContext);
    }

    public static Context getContext(){
        return mAppContext;
    }

    /**
     * 打开阿里播放器控件上层的打印，即林典伟添加的打印
     */
    public static void enableAppLog(){
        AliLogUtil.enableLog();
    }

    /**
     * 关闭阿里播放器控件上层的打印，即林典伟添加的打印
     */
    public static void disableAppLog(){
        AliLogUtil.disableLog();
    }

    /**
     * 打开阿里播放器SDK的打印，即阿里云添加的打印，播放器初始化前设置才有效
     */
    public static void enableSdkLog(){
        mSdkLogEnable = true;
    }

    /**
     * 关闭阿里播放器SDK的打印，即阿里云添加的打印
     */
    public static void disableSdkLog(){
        mSdkLogEnable = false;
    }

    public static boolean getSdkLogEnable(){
        return mSdkLogEnable;
    }



}
