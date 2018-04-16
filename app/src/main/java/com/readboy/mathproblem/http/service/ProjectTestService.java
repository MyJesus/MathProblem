package com.readboy.mathproblem.http.service;

import com.readboy.mathproblem.http.HttpConfig;
import com.readboy.mathproblem.http.response.ProjectEntity;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by oubin on 2017/8/28.
 */

public interface ProjectTestService {

    @GET(HttpConfig.GUIDE)
    Call<ProjectEntity> guide(@QueryMap Map<String, String> param);
}
