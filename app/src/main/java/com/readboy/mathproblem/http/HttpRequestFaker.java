package com.readboy.mathproblem.http;

import android.util.Log;

import com.readboy.mathproblem.http.response.ExampleEntity;
import com.readboy.mathproblem.http.response.ExerciseEntity;
import com.readboy.mathproblem.http.response.ProjectListEntity;
import com.readboy.mathproblem.http.response.ResponseEntity;
import com.readboy.mathproblem.http.response.MethodEntity;
import com.readboy.mathproblem.http.service.GuideServiceTest;
import com.readboy.mathproblem.http.service.MethodServiceTest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by oubin on 2017/8/28.
 */

public class HttpRequestFaker implements HttpRequestInterface {
    private static final String TAG = "HttpRequestFake";

    public void guide() {
        GuideServiceTest guideService = HttpEngine.getInstance().create(GuideServiceTest.class);
        Call<ResponseEntity> call = guideService.guide("tikuutil", "1");
        call.enqueue(new Callback<ResponseEntity>() {
            @Override
            public void onResponse(Call<ResponseEntity> call, Response<ResponseEntity> response) {
                Log.e(TAG, "onResponse: response = " + response.body());
            }

            @Override
            public void onFailure(Call<ResponseEntity> call, Throwable t) {
                Log.e(TAG, "onFailure: t = " + t.toString(), t);
            }
        });

    }

    public void method(){
        MethodServiceTest methodService = HttpEngine.getInstance().create(MethodServiceTest.class);
        Call<MethodEntity> call = methodService.method("tikuutil", "1");
        call.enqueue(new Callback<MethodEntity>() {
            @Override
            public void onResponse(Call<MethodEntity> call, Response<MethodEntity> response) {
                Log.e(TAG, "onResponse: response = " + response.body());
            }

            @Override
            public void onFailure(Call<MethodEntity> call, Throwable t) {
                Log.e(TAG, "onFailure: t = " + t.toString(), t);
            }
        });
    }

    @Override
    public void guideList(String grade, Callback<ProjectListEntity> callback) {

    }

    @Override
    public void methodList(String grade, Callback<ProjectListEntity> callback) {

    }

    @Override
    public void example(List<Integer> idList, Callback<ExampleEntity> callback) {

    }

    @Override
    public void exercise(List<Integer> idList, Callback<ExerciseEntity> callback) {

    }

    @Override
    public void cancelRequest(Object requestTag) {

    }
}
