package com.readboy.mathproblem.bean;

import android.support.annotation.IntDef;

import com.readboy.mathproblem.db.Video;
import com.readboy.mathproblem.http.response.VideoInfoEntity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oubin on 2017/9/20.
 */

public class ProjectHolder {

    //无视频， 网络数据资源无视频
    public static final int VIDEO_STATUS_NONE = Video.STATUS_NONE;
    //有视频，待下载
    public static final int VIDEO_STATUS_WAIT = Video.STATUS_WAIT;
    //正在下载
    public static final int VIDEO_STATUS_DOWNLOADING = Video.STATUS_DOWNLOAD;
    //下载完成
    public static final int VIDEO_STATUS_COMPLETED = Video.STATUS_COMPLETED;

    @IntDef({VIDEO_STATUS_NONE, VIDEO_STATUS_WAIT, VIDEO_STATUS_DOWNLOADING, VIDEO_STATUS_COMPLETED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VideoStatus {
    }

    private int id;
    private int score;
    private String name;
    private String explain;
    private List<Integer> videoIdList = new ArrayList<>();
    private List<VideoInfoEntity.VideoInfo> videoInfoList = new ArrayList<>();

    @VideoStatus
    private int videoStatus;

    public ProjectHolder() {
    }

    public ProjectHolder(int id, String name, @VideoStatus int videoStatus) {
        this.id = id;
        this.name = name;
        this.videoStatus = videoStatus;
    }

    public ProjectHolder(int id, int score, String name, int videoStatus) {
        this.id = id;
        this.score = score;
        this.name = name;
        this.videoStatus = videoStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public int getVideoStatus() {
        return videoStatus;
    }

    public void setVideoStatus(int videoStatus) {
        this.videoStatus = videoStatus;
    }

    public List<VideoInfoEntity.VideoInfo> getVideoInfoList() {
        return videoInfoList;
    }

    public void setVideoInfoList(List<VideoInfoEntity.VideoInfo> videoList) {
        this.videoInfoList = videoList;
    }

    public List<Integer> getVideoIdList() {
        return videoIdList;
    }

    public void setVideoIdList(List<Integer> list) {
        this.videoIdList.clear();
        if (videoIdList != null) {
            videoIdList.addAll(list);
        }
    }
}
