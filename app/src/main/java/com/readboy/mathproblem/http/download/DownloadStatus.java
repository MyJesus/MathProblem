package com.readboy.mathproblem.http.download;

import android.util.Log;

import com.aliyun.vodplayer.downloader.AliyunDownloadMediaInfo;
import com.readboy.mathproblem.R;

/**
 * Created by oubin on 2017/9/29.
 * 下载状态信息。
 */

public enum DownloadStatus {
    /**
     * (界面显示的状态信息， 需要显示的图标)
     */
    WAIT("排队中", R.drawable.download_wait),
    PAUSE("已暂停", R.drawable.download_start),
    CONNECTING("连接中", R.drawable.download_pause),
    STARTED("已连接", R.drawable.download_pause),
    DOWNLOADING("下载中", R.drawable.download_pause),
    ERROR("下载失败", R.drawable.download_start),
    COMPLETED("已完成", R.drawable.download_start);

    private static final String TAG = "oubin_DownloadStatus";

    private String statusStr;
    private int resId;

    DownloadStatus(String statusStr) {

    }

    DownloadStatus(String statusStr, int resId) {
        this.statusStr = statusStr;
        this.resId = resId;
    }

    public static DownloadStatus convert(AliyunDownloadMediaInfo.Status status) {
        Log.e(TAG, "convert: status = " + status);
        switch (status) {
            case Idle:
                return CONNECTING;
            case Prepare:
                return CONNECTING;
            case Wait:
                return WAIT;
            case Start:
                return DOWNLOADING;
            case Stop:
                return PAUSE;
            case Complete:
                return COMPLETED;
            case Error:
                return ERROR;
            default:
                return ERROR;
        }
    }

    public int getDrawableResId() {
        return resId;
    }

    public String getDescribe() {
        return statusStr;
    }

}
