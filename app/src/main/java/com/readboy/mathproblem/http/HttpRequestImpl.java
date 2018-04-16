package com.readboy.mathproblem.http;

import com.readboy.mathproblem.http.request.IdsParams;
import com.readboy.mathproblem.http.request.RequestParams;
import com.readboy.mathproblem.http.response.ExampleEntity;
import com.readboy.mathproblem.http.response.ExerciseEntity;
import com.readboy.mathproblem.http.response.ProjectEntity;
import com.readboy.mathproblem.http.response.ProjectListEntity;
import com.readboy.mathproblem.http.service.ExampleService;
import com.readboy.mathproblem.http.service.ExerciseService;
import com.readboy.mathproblem.http.service.GuideListService;
import com.readboy.mathproblem.http.service.MethodListService;
import com.readboy.mathproblem.http.service.ProjectListService;
import com.readboy.mathproblem.http.service.ProjectService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by oubin on 2017/8/28.
 */

public class HttpRequestImpl implements HttpRequestInterface {
    private static final String TAG = "HttpRequestImpl";

    //
    //
    /**
     * 保存请求集合，方便取消请求。
     * TODO 需要重新考虑数据结构
     * String为Url路径，加共用参数。
     */
    private Map<String, Call> mCallMap = new HashMap<>();

    @Override
    public void guideList(String grade, Callback<ProjectListEntity> callback) {
        RequestParams params = new RequestParams(grade);
        GuideListService service = HttpEngine.getInstance().create(GuideListService.class);
        Call<ProjectListEntity> call = service.guideList(params.getMap());
        call.enqueue(callback);
    }

    @Override
    public void methodList(String grade, Callback<ProjectListEntity> callback) {
        RequestParams params = new RequestParams(grade);
        MethodListService service = HttpEngine.getInstance().create(MethodListService.class);
        Call<ProjectListEntity> call = service.methodList(params.getMap());
        call.enqueue(callback);
    }

    @Override
    public void example(List<Integer> idList, Callback<ExampleEntity> callback) {
        IdsParams params = new IdsParams(idList);
        ExampleService service = HttpEngine.getInstance().create(ExampleService.class);
        Call<ExampleEntity> call = service.example(params.getMap());
        call.enqueue(callback);
    }

    @Override
    public void exercise(List<Integer> idList, Callback<ExerciseEntity> callback) {
        IdsParams params = new IdsParams(idList);
        ExerciseService service = HttpEngine.getInstance().create(ExerciseService.class);
        Call<ExerciseEntity> call = service.example(params.getMap());
        call.enqueue(callback);
        call.request().tag();
    }

    public static void getProjectList(String type, String grade, Callback<ProjectListEntity> callback) {
        RequestParams params = new RequestParams(grade);
        ProjectListService service = HttpEngine.getInstance().create(ProjectListService.class);
        Call<ProjectListEntity> call = service.getProjects(type, params.getMap());
        call.enqueue(callback);
    }

    public static Call<ProjectEntity> getProjects(String type, String grade, Callback<ProjectEntity> callback) {
        RequestParams params = new RequestParams(grade);
        ProjectService service = HttpEngine.getInstance().create(ProjectService.class);
        Call<ProjectEntity> call = service.getProjects(type, params.getMap());
        call.enqueue(callback);
        return call;
    }

    //TODO 取消请求
    @Override
    public void cancelRequest(Object requestTag) {

    }

    //TODO 取消请求
    public void cancel() {

    }
}
