package com.readboy.mathproblem.http.service;

import com.readboy.mathproblem.http.HttpConfig;
import com.readboy.mathproblem.http.response.ResponseEntity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by oubin on 2017/8/26.
 */

public interface GuideServiceTest {

    @GET(HttpConfig.GUIDE)
    Call<ResponseEntity> guide(@Query("sn") String signature, @Query("grade") String grade);
}
