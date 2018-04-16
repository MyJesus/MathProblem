package com.readboy.mathproblem.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.http.interceptor.HttpLoggingInterceptor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by oubin on 2016/10/28.
 * <p>
 */

public class HttpEngine {
    private static final String URL = HttpConfig.ENDPOINT;
    private static Retrofit mRetrofit;
    private static final int CONNECT_TIMEOUT = 60_000;

    private HttpEngine() {
    }

    private static synchronized void syncInit() {
        if (mRetrofit == null) {
            init();
        }
    }

    public static Retrofit getInstance() {
        if (mRetrofit == null) {
            syncInit();
        }
        return mRetrofit;
    }

    private static void init() {
        //TODO: OkHttp缓存处理, 使用OkHttp的缓存策略，CacheInterceptor等。
        File cacheFile = new File(MathApplication.getInstance().getCacheDir().getAbsolutePath(), "httpCache");
        Cache cache = new Cache(cacheFile, HttpConfig.CACHE_MAX_SIZE);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
//                .addInterceptor(new TimeAdjustInterceptor())
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(HttpConfig.READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(HttpConfig.WRITE_TIMEOUT, TimeUnit.MICROSECONDS)
//                .cache(cache)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mRetrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();

    }

    public static void cancelTag() {

    }


}