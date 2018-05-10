package com.readboy.mathproblem;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.readboy.mathproblem.cache.CacheEngine;
import com.readboy.mathproblem.http.response.VideoInfoEntity;
import com.readboy.mathproblem.video.proxy.VideoProxy;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String TAG = "ExampleInstrumentedTest";


    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.readboy.mathproblem", appContext.getPackageName());
    }

    //使用MD5算法进行加密
    public static String md5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("MD5").digest(plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有MD5这个算法!");
        }
        //16进制数
        String md5code = new BigInteger(1, secretBytes).toString(16);
        //如果生成数字没满32位,需要前面补0
        StringBuilder md5Builder = new StringBuilder(new BigInteger(1, secretBytes).toString(16));
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    private void dateTest() {
        Calendar cal = Calendar.getInstance();

        // Locale.US用于将日期区域格式设为美国（英国也可以）。缺省改参数的话默认为机器设置，如中文系统星期将显示为汉子“星期六”
        SimpleDateFormat localDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.US);
        SimpleDateFormat greenwichDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        // 时区设为格林尼治
        greenwichDate.setTimeZone(TimeZone.getTimeZone("GMT"));

        Log.e(TAG, "onCreate: 当前时间：" + localDate.format(cal.getTime()));
        Log.e(TAG, "onCreate: 格林尼治时间：" + greenwichDate.format(cal.getTime()));
    }

    private void testPlay() {

    }

    private void uriTest2() {
        String url = "http://d.elpsky.com/download/mp4qpsp/%E6%8E%A2%E7%A7%98%28%E8%A1%8C%E7%A8%8B%29%E4%B9%8B%E8%B7%AF_%E6%B5%81%E6%B0%B4%E8%A1%8C%E8%88%B9%E9%97%AE%E9%A2%98.mp4?auth_key=1509593014-0-0-d2abb6a04725ea817b52a3dd809a4bcc";
//        VideoProxy.playWithUrl(url, this);
        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();
        String c = uri.getSchemeSpecificPart();
        String path = uri.getPath();
        String s = uri.getAuthority();
        String s1 = uri.getEncodedQuery();
        String s2 = uri.getEncodedFragment();
        Log.e(TAG, "onCreate: path = " + path);
        Log.e(TAG, "onCreate: authority = " + s);
        Log.e(TAG, "onCreate: query = " + s1);
        Log.e(TAG, "onCreate: fragment = " + s2);
    }

    private void uriTest() {
        String uriString = VideoProxy.SCHEME_VIDEO_URI + "://" + "/download/mp2qpsp/流水问题.mp4";
        Uri uri = Uri.parse(uriString);
        String scheme = uri.getScheme();
        String path = uri.getPath();
        String ePath = uri.getEncodedPath();
        String fragment = uri.getFragment();
        String authority = uri.getAuthority();
        boolean h = uri.isHierarchical();

        String uriString2 = "http://www.baidu.com";
        Uri uri2 = Uri.parse(uriString2);
        String host = uri2.getHost();
        String scheme2 = uri2.getScheme();
        boolean b = uri2.isHierarchical();
    }

    private void test() {
        ArrayList<Integer> idList = new ArrayList<>();
        idList.add(486868110);
        idList.add(486788110);
        idList.add(486298110);
        idList.add(486478110);
        idList.add(486488110);
        idList.add(486498110);
        CacheEngine.getVideoInfoFromHttp(idList, new Callback<VideoInfoEntity>() {
            @Override
            public void onResponse(Call<VideoInfoEntity> call, Response<VideoInfoEntity> response) {
                if (response.body() != null) {
                    Log.e(TAG, "onResponse: body = " + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<VideoInfoEntity> call, Throwable t) {
                Log.e(TAG, "onFailure: t = " + t.toString(), t);
            }
        });
    }


}
