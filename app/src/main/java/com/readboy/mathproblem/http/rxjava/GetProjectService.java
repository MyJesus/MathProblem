package com.readboy.mathproblem.http.rxjava;

import com.readboy.mathproblem.http.HttpConfig;
import com.readboy.mathproblem.http.response.ProjectEntity;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by oubin on 2017/10/31.
 */

public interface GetProjectService {

    @GET(HttpConfig.BASE_PATH + "{type}")
    Observable<ProjectEntity> getProjects(@Path("type") String type, @QueryMap Map<String, String> param);
}
