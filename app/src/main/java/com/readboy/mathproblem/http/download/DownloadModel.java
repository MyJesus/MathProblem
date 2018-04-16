package com.readboy.mathproblem.http.download;

import com.liulishuo.filedownloader.BaseDownloadTask;

/**
 * Created by oubin on 2017/9/29.
 * 视频下载Model
 */

public class DownloadModel {
    private int taskId;
    private int videoId;
    private String fileName;
    private String url;
    private String path;
    private DownloadStatus status = DownloadStatus.PAUSE;
    private BaseDownloadTask downloadTask;
    private String thumbnailUrl;

    public DownloadModel() {
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

    public BaseDownloadTask getDownloadTask() {
        return downloadTask;
    }

    public void setDownloadTask(BaseDownloadTask downloadTask) {
        this.downloadTask = downloadTask;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getSpeed() {
        return downloadTask != null ? downloadTask.getSpeed() : 0;
    }

    public long getTotal() {
        return downloadTask != null ? downloadTask.getSmallFileTotalBytes() : 0;
    }

    public long getSoFar() {
        return downloadTask != null ? downloadTask.getSmallFileSoFarBytes() : 0;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
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
