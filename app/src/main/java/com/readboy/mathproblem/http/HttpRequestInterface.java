package com.readboy.mathproblem.http;

import com.readboy.mathproblem.http.response.ExampleEntity;
import com.readboy.mathproblem.http.response.ExerciseEntity;
import com.readboy.mathproblem.http.response.ProjectListEntity;

import java.util.List;

import retrofit2.Callback;

/**
 * Created by oubin on 2017/8/28.
 */

public interface HttpRequestInterface {

    void guideList(String grade, Callback<ProjectListEntity> callback);

    void methodList(String grade, Callback<ProjectListEntity> callback);

    void example(List<Integer> idList, Callback<ExampleEntity> callback);

    void exercise(List<Integer> idList, Callback<ExerciseEntity> callback);

    /**
     * 取消请求
     *
     * @param requestTag 请求标识
     */
    void cancelRequest(Object requestTag);

}
