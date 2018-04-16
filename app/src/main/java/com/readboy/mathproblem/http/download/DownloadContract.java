package com.readboy.mathproblem.http.download;

/**
 * Created by oubin on 2017/10/8.
 */

public final class DownloadContract {

    //MaxNetworkThreadCount
    public static final int MAX_NETWORK_THREAD_COUNT = 2;

    public static final boolean SUPPORT_BACKGROUND_DOWNLOAD = false;

    /**
     * 下载数据库字段，
     * 无需保存进度，直接获取正在下载文件的大小，和文件总大小，获取进度值。
     */
    public interface DownloadColumns {
        String ID = "id";
        String NAME = "name";
        String URL = "url";
        String PATH = "path";
    }

}
