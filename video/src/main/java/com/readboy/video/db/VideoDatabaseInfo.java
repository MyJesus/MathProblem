package com.readboy.video.db;

public class VideoDatabaseInfo {

	public static final String APP_DATA_PATH = "Android/data/com.dream.dreamplayer/";
	public static final String DBPATH = APP_DATA_PATH+"database";
	public static final String DBNAME = "videos";
	
	public String mDataPath = DBPATH;
	public String mDataName = "videos";
	
	/** 根据这个找到视频，可以根据地址类：视频本地地址，网络URL。  根据ID类：知识点微视频的vid等 */
	public String mDependency = null;
	
	/**  依赖的类别，0x0：本地地址，0x1： 网络地址， 0x4： 知识点微视频的vid  */
	public int mType = 0;
	
	/**   0: 没有播放, 1: 播放过 */
	public int mPlay = 0;
	
	public long mPosition = 0;
	public long mDuration = 0;
	public long mSize = 0;
	
	public String mCachePath = APP_DATA_PATH+"cache";
	public String mCacheFilePath = null;
	public String mCacheName = null;
	
	public VideoDatabaseInfo() {
		mDataPath = DBPATH;
		mDataName = "videos";
		mCachePath = APP_DATA_PATH+"cache";
	}
}
