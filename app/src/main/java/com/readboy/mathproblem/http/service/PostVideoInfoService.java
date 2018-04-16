package com.readboy.mathproblem.http.service;

import com.readboy.mathproblem.http.response.VideoInfoEntity;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by oubin on 2017/10/23.
 * 获取视频下载和播放链接
 */

public interface PostVideoInfoService {

    @FormUrlEncoded
    @POST("ids")
    Call<VideoInfoEntity> getVideoUrl(@FieldMap Map<String, String> params);
}
