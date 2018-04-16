package com.readboy.mathproblem.video.tools;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class Utils {

    public static String formatTime(long millis) {

        int totalSeconds = (int) millis / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String getVideoName(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        String result;
        String regex = "/";
        int index = url.lastIndexOf(regex);
        //判断是否是uri
        String httpScheme = "http://";
        if (!url.startsWith(httpScheme)) {
            result = url.substring(index + regex.length(), url.length());
            int i = result.lastIndexOf(".");
            result = result.substring(0, i);
            return result;
        }
        int lastIndex = url.lastIndexOf("?auth_key");
        lastIndex = lastIndex < 0 ? url.length() : lastIndex;
        result = url.substring(index + regex.length(), lastIndex);
        result = result.substring(0, result.lastIndexOf("."));
        try {
            return URLDecoder.decode(result, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return result;
        }
    }
}
