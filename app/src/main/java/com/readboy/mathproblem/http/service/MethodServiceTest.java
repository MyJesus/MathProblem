package com.readboy.mathproblem.http.service;

import com.readboy.mathproblem.http.HttpConfig;
import com.readboy.mathproblem.http.response.MethodEntity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by oubin on 2017/8/28.
 */

public interface MethodServiceTest {

    @GET(HttpConfig.METHOD)
    Call<MethodEntity> method(@Query("sn") String signature, @Query("grade") String grade);
}
