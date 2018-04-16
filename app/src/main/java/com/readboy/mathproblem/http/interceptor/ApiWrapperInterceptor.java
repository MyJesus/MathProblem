package com.readboy.mathproblem.http.interceptor;

import android.util.Log;

import com.readboy.mathproblem.http.HttpConfig;
import com.readboy.mathproblem.http.response.ProjectEntity;
import com.readboy.mathproblem.util.JsonMapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 合并数据，和视频接口。
 * Created by oubin on 2017/10/31.
 */

public class ApiWrapperInterceptor implements Interceptor {
    private static final String TAG = "ApiWrapperInterceptor";
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response;

        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            Log.e(TAG, "intercept: e = " + e.toString());
            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new IOException(e.toString());
            }
        }

        Log.e(TAG, "intercept: request host = " + request.url().host());
        if (HttpConfig.HOST.equals(request.url().host())) {
            return response;
        }

        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            return response;
        }
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();

        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(UTF8);
            } catch (UnsupportedCharsetException e) {
                return response;
            }
        }
        String jsonString = buffer.clone().readString(charset);
        ProjectEntity entity = JsonMapper.fromJson(jsonString, ProjectEntity.class);
        Request.Builder videoRequestBuilder = new Request.Builder();

        return null;
    }
}
