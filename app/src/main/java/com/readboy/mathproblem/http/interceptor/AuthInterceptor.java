package com.readboy.mathproblem.http.interceptor;

import android.util.Log;

import com.readboy.mathproblem.http.HttpConfig;
import com.readboy.mathproblem.http.auth.Authentication;
import com.readboy.mathproblem.http.response.VideoInfoEntity;
import com.readboy.mathproblem.http.request.BaseRequestParams;
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
 * Created by oubin on 2017/10/30.
 */

public class AuthInterceptor implements Interceptor {
    private static final String TAG = "AuthInterceptor";
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String AUTH_HOST_TEST = "http://auth.cdn.readboy.com:8000";
    private static final String AUTH_HOST = "http://auth.cdn.readboy.com ";
    /**
     * url有效时常, 默认一天。
     */
    private static final int EFFECTIVE_TIME_SECOND = 24 * 60 * 60;

    private static Authentication sAuthentication;

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
        VideoInfoEntity.VideoInfo entity = JsonMapper.fromJson(jsonString, VideoInfoEntity.VideoInfo.class);
        if (isValid()) {

        } else {
            BaseRequestParams params = new BaseRequestParams();
            String url = AUTH_HOST + "?" +
                    params.unitParams() +
                    "&type=elpsky";
            Request.Builder authRequestBuilder = new Request.Builder()
                    .url(url);
            Response authResponse = chain.proceed(authRequestBuilder.build());
            if (authResponse != null && authResponse.body() != null) {
                String authJson = authResponse.body().toString();
                Log.e(TAG, "intercept: auth response = " + authJson);
                sAuthentication = JsonMapper.fromJson(authJson, Authentication.class);
                if (isValid()){

                }
            }
            Response.Builder resultBuilder = response.newBuilder();
//            ResponseBody resultBody =
//            resultBuilder.body()
        }

        return null;
    }

    private static boolean isValid() {
        return sAuthentication != null
                && System.currentTimeMillis() / 1000 < sAuthentication.getTimestamp();
    }
}
