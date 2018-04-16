package com.readboy.mathproblem.http.download;

import android.text.TextUtils;

import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;


/**
 * Created by oubin on 2017/10/9.
 */

public class DownloadUtils {
    private static final String TAG = "DownloadUtils";

    public static byte getStatus(int id) {
        return getStatus(id, null);
    }

    public static byte getStatus(int id, String path) {
        return FileDownloader.getImpl().getStatus(id, path);
    }

    public static boolean isDownloading(int id) {
//        Log.e(TAG, "isDownloading: status = " + FileDownloader.getImpl().getStatusIgnoreCompleted(id));
        return FileDownloader.getImpl().getStatusIgnoreCompleted(id) > 0;
    }

    public static boolean isCompleted(int id) {
        return FileDownloader.getImpl().getStatus(id, null) == FileDownloadStatus.completed;
    }

    public static String createPath(final String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        return FileDownloadUtils.getDefaultSaveFilePath(url);
    }

    public static int getDownloadTaskId(final String url) {
        return getDownloadTaskId(url, createPath(url));
    }

    public static int getDownloadTaskId(final String url, final String path) {
        return FileDownloadUtils.generateId(url, path);
    }

}
