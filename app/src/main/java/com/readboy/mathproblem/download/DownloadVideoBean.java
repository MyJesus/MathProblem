package com.readboy.myapplication.download;

import com.aliyun.vodplayer.downloader.AliyunDownloadMediaInfo;

/**
 *
 */

public class DownloadVideoBean{
	public static final int STATUS_STOP = -1;//下载停止
	public static final int STATUS_DOWNLOAD = 1;//
	public static final int STATUS_WAITWIFI = 2;//

	public long mDownloadSize;
	public boolean mIsDownloading;
	public String mInfo;
	public AliyunDownloadMediaInfo mAliyunDownloadMediaInfo;
	public int mStatus = STATUS_WAITWIFI;
	public int mDownloadIndex = -1;//下载的顺序
	public int mSid = -1;
	public String mName;

	public boolean mIsSelect = false;//是否被选中（删除）

}
