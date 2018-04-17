package com.readboy.aliyunplayerlib.app;

import android.app.Application;
import android.content.Context;

import com.alivc.player.AliVcMediaPlayer;

/**
 * 使用播放器需要继承此Application，或者直接把Application里面的代码直接写到自己的Application中
 * Created by ldw on 2018/3/19.
 */

public class AliyunPlayerLibApp extends Application {

    private static AliyunPlayerLibApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        //初始化播放器。不初始化，错误字符串将获取不到。
        AliVcMediaPlayer.init(getApplicationContext());
    }

    public static AliyunPlayerLibApp getInstance(){
        return instance;
    }

    public static Context getContext(){
        return instance.getApplicationContext();
    }

}
