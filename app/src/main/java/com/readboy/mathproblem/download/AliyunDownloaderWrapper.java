package com.readboy.mathproblem.download;

import android.content.Context;

import com.aliyun.vodplayer.downloader.AliyunDownloadInfoListener;
import com.aliyun.vodplayer.downloader.AliyunDownloadManager;
import com.aliyun.vodplayer.downloader.AliyunDownloadMediaInfo;

import java.util.List;

/**
 * Created by oubin on 2018/4/23.
 */

public class AliyunDownloaderWrapper {

    private AliyunDownloadManager mDownloadManager;
    private AliyunDownloadInfoListener mDownloadListener;

    private AliyunDownloaderWrapper(Context context){
        mDownloadManager = AliyunDownloadManager.getInstance(context);
        mDownloadListener = new AliyunDownloadListenerImpl();
        mDownloadManager.addDownloadInfoListener(mDownloadListener);
    }

    private class AliyunDownloadListenerImpl implements AliyunDownloadInfoListener{

        @Override
        public void onPrepared(List<AliyunDownloadMediaInfo> list) {
        }

        @Override
        public void onStart(AliyunDownloadMediaInfo aliyunDownloadMediaInfo) {

        }

        @Override
        public void onProgress(AliyunDownloadMediaInfo aliyunDownloadMediaInfo, int i) {

        }

        @Override
        public void onStop(AliyunDownloadMediaInfo aliyunDownloadMediaInfo) {

        }

        @Override
        public void onCompletion(AliyunDownloadMediaInfo aliyunDownloadMediaInfo) {

        }

        @Override
        public void onError(AliyunDownloadMediaInfo aliyunDownloadMediaInfo, int i, String s, String s1) {

        }

        @Override
        public void onWait(AliyunDownloadMediaInfo aliyunDownloadMediaInfo) {

        }

        @Override
        public void onM3u8IndexUpdate(AliyunDownloadMediaInfo aliyunDownloadMediaInfo, int i) {

        }
    }

}
