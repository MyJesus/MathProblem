package com.readboy.mathproblem.http.interceptor;

import android.util.Log;

import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.util.NetworkUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by oubin on 2017/11/28.
 * 缓存数据，
 */

public class CacheInterceptor implements Interceptor {
    private static final String TAG = "CacheInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Log.e(TAG, "intercept: request = " + request.toString());

        Response response = chain.proceed(request);
        Response responseBuilder;
        if (NetworkUtils.isConnected(MathApplication.getInstance())) {
            // 有网络的时候从缓存1天后失效
            int maxAge = 60 * 60 * 24;
            responseBuilder = response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        } else {
            // 无网络缓存保存四周
            int maxStale = 60 * 60 * 24 * 28;
            responseBuilder = response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
        }

        return responseBuilder;
    }
}
