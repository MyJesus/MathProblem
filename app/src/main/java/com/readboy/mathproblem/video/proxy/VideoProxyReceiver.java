package com.readboy.mathproblem.video.proxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.readboy.mathproblem.http.download.DownloadManager;
import com.readboy.mathproblem.util.FileUtils;
import com.readboy.mathproblem.util.VideoUtils;
import com.readboy.mathproblem.video.movie.VideoExtraNames;

/**
 * Created by oubin on 2017/11/2.
 */

public class VideoProxyReceiver extends BroadcastReceiver {
    private static final String TAG = "VideoProxyReceiver";

    public static final String ACTION_DOWNLOAD_VIDEO = "download";
    public static final String EXTRA_URL = VideoExtraNames.EXTRA_URL;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG, "onReceive: action = " + action);
        if (ACTION_DOWNLOAD_VIDEO.equals(action)) {
            String url = intent.getStringExtra(EXTRA_URL);
            Uri uri = Uri.parse(url);
            if (VideoUtils.videoIsExist(FileUtils.getFileName(url))) {
                Log.e(TAG, "onReceive: have downloaded. filename = " + FileUtils.getFileName(url));
            } else {
                String scheme = uri.getScheme();
                if (VideoProxy.VIDEO_URI_SCHEME.equalsIgnoreCase(scheme)) {
                    Log.e(TAG, "onReceive: video uri = " + uri.getPath());
                    DownloadManager.getInstance().addTaskWithUri(uri.getPath(), null);
                } else if (VideoProxy.HTTP_SCHEME.equalsIgnoreCase(scheme)) {
                    Log.e(TAG, "onReceive: video http = " + url);
                    DownloadManager.getInstance().addTaskWithUrl(url);
                } else {
                    Log.e(TAG, "onReceive: can not parse url, url = " + url);
                }
            }
        }
    }
}
