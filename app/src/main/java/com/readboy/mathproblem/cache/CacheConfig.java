package com.readboy.mathproblem.cache;

import android.os.Environment;

import java.io.File;

/**
 * Created by oubin on 2017/10/24.
 */

public interface CacheConfig {

    String KEY_LAST_UPDATE_TIME = "lastUpdateTime";
    /**
     * 单位：millisecond，更新周期1天
     */
    long DEFAULT_UPDATE_PERIOD = 3 * 24 * 60 * 60 * 1000;
    //    long DEFAULT_UPDATE_PERIOD = 1 * 60 * 1000; //测试
    int INVALID_INDEX = 0;
    /**
     * 不用于数据存储
     */
    String PATH = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "MathProblem";

//    String PATH = MathApplication.getInstance().getExternalCacheDir().getAbsolutePath();

    String CACHE_FILE_PATH = PATH + File.separator + "cache";
    String NEW_CACHE_FILE_PATH = PATH + File.separator + "project";
    String CACHE_FILE_EXTENSION = ".pro";
    String DATA_UPDATE_LOG = CACHE_FILE_PATH + File.separator + "Log.txt";

}
