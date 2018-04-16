package com.readboy.mathproblem.http.service;

import com.readboy.mathproblem.http.response.VideoInfoEntity;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by oubin on 2017/10/23.
 * http://api.video.readboy.com/ids?sn=videotest&ids=486738110,486468110
 */

public interface PostVideoInfoServiceTest {

    @FormUrlEncoded
    @POST("ids")
    Call<VideoInfoEntity> getVideoUrl(@Field("sn") String sn, @Field("ids") String ids);

}
