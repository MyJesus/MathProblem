package com.readboy.mathproblem.http.service;

import com.readboy.mathproblem.http.HttpConfig;
import com.readboy.mathproblem.http.response.ProjectEntity;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by oubin on 2017/9/20.
 */

public interface ProjectService {

    @GET(HttpConfig.BASE_PATH + "{type}")
    Call<ProjectEntity> getProjects(@Path("type") String type, @QueryMap Map<String, String> param);
}
