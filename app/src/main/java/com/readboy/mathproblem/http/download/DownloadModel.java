package com.readboy.mathproblem.http.download;

import android.util.Log;

import com.aliyun.vodplayer.downloader.AliyunDownloadMediaInfo;

/**
 * Created by oubin on 2017/9/29.
 * 视频下载Model
 */

public class DownloadModel {
    private static final String TAG = "DownloadModel";

    private int taskId;
    private int videoId;
    private String fileName;
    /**
     * 也用于存储vid
     */
    private String url;
    private String path;
    private DownloadStatus status = DownloadStatus.PAUSE;
    private AliyunDownloadMediaInfo mediaInfo;
    private String thumbnailUrl;

    public DownloadModel() {
    }

    public DownloadModel(AliyunDownloadMediaInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
        thumbnailUrl = mediaInfo.getCoverUrl();
        fileName = mediaInfo.getTitle();
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public DownloadStatus getStatus() {
        return status;
    }

    public void setStatus(DownloadStatus status) {
        this.status = status;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getSpeed() {
        return 0;
    }

    public long getTotal() {
        return mediaInfo != null ? mediaInfo.getSize() : 0;
    }

    public long getSoFar() {
        if (mediaInfo == null || mediaInfo.getProgress() <= 0) {
            return 0;
        }
        Log.e(TAG, "getSoFar: progress = " + mediaInfo.getProgress());
        return (long) (mediaInfo.getSize() * 0.01F * mediaInfo.getProgress());
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public AliyunDownloadMediaInfo getMediaInfo() {
        return mediaInfo;
    }

    public void setMediaInfo(AliyunDownloadMediaInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
    }

    public String getVid() {
        return mediaInfo != null ? mediaInfo.getVid() : url;
    }

    @Override
    public String toString() {
        return "DownloadModel{" +
                "taskId=" + taskId +
                ", fileName='" + fileName + '\'' +
                ", url='" + url + '\'' +
                ", path='" + path + '\'' +
                ", status=" + status.getDescribe() +
                '}';
    }
}
