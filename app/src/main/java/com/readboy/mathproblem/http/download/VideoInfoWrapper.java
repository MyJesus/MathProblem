package com.readboy.mathproblem.http.download;

import com.readboy.mathproblem.http.response.VideoInfoEntity;

/**
 * Created by oubin on 2017/10/31.
 */

public class VideoInfoWrapper {

    private VideoInfoEntity.VideoInfo videoInfo;
    private String url;

    public VideoInfoWrapper(VideoInfoEntity.VideoInfo videoInfo, String url) {
        this.videoInfo = videoInfo;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public VideoInfoEntity.VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfoEntity.VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public String getVideoUri() {
        return videoInfo.getVideoUri();
    }

    public String getName() {
        return videoInfo.getName();
    }

    public int getId() {
        return videoInfo.getId();
    }

    public int getFileSize() {
        return videoInfo.getFileSize();
    }

    public double getDuration() {
        return videoInfo.getFileSize();
    }

    public String getThumbnailUrl() {
        return videoInfo.getThumbnailUrl();
    }

}
