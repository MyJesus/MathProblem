package com.readboy.mathproblem.video.db;

import java.io.File;

public class VideoDatabaseInfo {

    private static final String DEFAULT_DIR = "Android/data/com.readboy.mathproblem/video";

    public static final String DEFAULT_CACHE_PATH = DEFAULT_DIR + File.separator + "cache";
    public static final String DEFAULT_DATABASE_PATH = DEFAULT_DIR + File.separator + "database";
    public static final String DBNAME = "videos";

    public String mDataPath = DEFAULT_DIR + File.separator + "database";
    public String mDataName = "videos";

    /**
     * 根据这个找到视频，可以根据地址类：视频本地地址，网络URL。  根据ID类：知识点微视频的vid等
     */
    public String mDependency = null;

    /**
     * 依赖的类别，0x0：本地地址，0x1： 网络地址， 0x4： 知识点微视频的vid
     */
    public int mType = 0;

    /**
     * 0: 没有播放, 1: 播放过
     */
    public int mPlay = 0;

    public long mPosition = 0;
    public long mDuration = 0;
    public long mSize = 0;

    /**
     * 缓存文件路径，播放本地视频时，无需缓存。
     * 最终用于HttpGetProxy中，VideoView和HttpGetProxy对本地视频有处理，
     * 可以都传入一个有效的缓存路径。
     */
    public String mCachePath;
    public String mCacheFilePath = null;
    public String mCacheName = null;

    public VideoDatabaseInfo() {
        this.mDataPath = DEFAULT_DATABASE_PATH;
        this.mDataName = "videos";
        this.mCachePath = DEFAULT_CACHE_PATH;
    }

    @Override
    public String toString() {
        return "VideoDatabaseInfo{" +
                "mCachePath='" + mCachePath + '\'' +
                ", mCacheFilePath='" + mCacheFilePath + '\'' +
                ", mCacheName='" + mCacheName + '\'' +
                '}';
    }
}
