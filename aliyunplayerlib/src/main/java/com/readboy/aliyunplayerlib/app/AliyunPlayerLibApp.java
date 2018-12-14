package com.readboy.aliyunplayerlib.app;

import android.app.Application;

/**
 * 弃用用这个类，改成直接使用AliPlayerAppUtil 2018/9/18
 *
 * 使用播放器需要继承此Application，或者直接把Application里面的代码直接写到自己的Application中
 * Created by ldw on 2018/3/19.
 */

public class AliyunPlayerLibApp extends Application {

    //private static AliyunPlayerLibApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        //instance = this;

        AliPlayerAppUtil.init(this);
    }

    /*public static AliyunPlayerLibApp getInstance(){
        return instance;
    }

    public static Context getContext(){
        return instance.getApplicationContext();
    }*/

}
