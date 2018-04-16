package com.readboy.mathproblem.http.interceptor;


import android.util.Log;

import com.readboy.mathproblem.NativeApi;
import com.readboy.mathproblem.http.HttpError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Date;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by oubin on 2017/9/18.
 * <p>
 *     自动校准时间，
 * 解决设备时间和服务器时间不同步问题。
 */

public class TimeAdjustInterceptor implements Interceptor {
    private static final String TAG = "TimeAdjustInterceptor";
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private long mTimeOffset = 0;

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
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            int errorNo = jsonObject.optInt("errno");
            Log.e(TAG, "intercept: errno = " + errorNo);
            if (errorNo == HttpError.ERROR_SIGNATURE_EXPIRE) {
                Date serverDate = response.headers().getDate("Date");
                if (serverDate == null) {
                    return response;
                }

                //毫秒级别换成秒
                long serverTime = serverDate.getTime() / 1000;

                Request.Builder builder = request.newBuilder();
                HttpUrl httpUrl = request.url();
                HttpUrl.Builder urlBuilder = httpUrl.newBuilder();

                //因为与服务器有效同步时间差为5分钟，所有直接用服务器的时间作为新的时间戳
                long newTime = serverTime;
                String newTimeString = String.valueOf(newTime);
                Set<String> queryNames = httpUrl.queryParameterNames();
                urlBuilder.setQueryParameter("t", newTimeString);
                urlBuilder.setQueryParameter("sn", NativeApi.getSignature(newTimeString));

                builder.url(urlBuilder.build());
                return chain.proceed(builder.build());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return response;
    }
}
