package com.readboy.mathproblem.http.auth;

import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by guh on 2017/10/16.
 */

public class Authentication {
    private static final String TAG = "Authentication";

    static final boolean TEST_MODE = false;

    /**
     * 阿里添加的有效时常。1800s.
     */
    static final long DEFAULT_EFFECTIVE_SECOND_TIME = 30 * 60;

    /**
     * 测试用例，有效期1分钟。
     */
    private static final long APP_EFFECTIVE_SECOND_TIME_TEST = -24 * 60 * 60 + 60 - DEFAULT_EFFECTIVE_SECOND_TIME;

    /**
     * url有效时常, 默认一天。
     */
    static final long APP_EFFECTIVE_SECOND_TIME = TEST_MODE ? APP_EFFECTIVE_SECOND_TIME_TEST : 24 * 60 * 60;

    private String mDomain = null;

    private String mKey = null;

    private long mTimestamp = 0;

    Authentication(String domain, String key, int timestamp) {
        Log.e(TAG, "Authentication() called with: domain = " + domain + ", key = " + key + ", timestamp = " + timestamp + "");
        mDomain = domain;
        mKey = key;
        mTimestamp = timestamp;
    }

    public String getKey() {
        return mKey;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    String authUrl(String uri) {
        String uriUtf8 = encodeUri(uri);
        String result = uriUtf8;
        if (!uriUtf8.startsWith(mDomain)) {
            String wrapperUri = uriUtf8 + "-" + String.valueOf(mTimestamp
                    + APP_EFFECTIVE_SECOND_TIME) + "-0-0-" + getKey();
            String encryptUri = MidroVideoUrl.getMd5(wrapperUri);
            result = mDomain + uriUtf8 + "?auth_key="
                    + String.valueOf(mTimestamp + APP_EFFECTIVE_SECOND_TIME) + "-0-0-" + encryptUri;
        }
        return result;
    }

    private void logTime() {
        long time = mTimestamp + APP_EFFECTIVE_SECOND_TIME;
        Calendar calendar = Calendar.getInstance();
        long temp = mTimestamp * 1000;
        calendar.setTimeInMillis(temp);
            Log.e(TAG, "authUrl: original time = " + calendar.toString());
        calendar.setTimeInMillis(time * 1000);
            Log.e(TAG, "authUrl: vail time = " + calendar.toString());
    }

    private String formatUTF8(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        String regex = "/";
        String[] splitArray = url.split(regex);
        StringBuilder result = new StringBuilder();
        int size = splitArray.length;
        for (int i = 0; ; i++) {
            try {
                String encode = URLEncoder.encode(splitArray[i], "utf-8");
                result.append(encode);
                if (i == size - 1) {
                    return result.toString();
                }
                result.append(regex);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    /**
     * encodeUri：用于对网址编码(不包含参数),不编码字符有82个：!，#，$，&，'，(，
     * )，*，+，,，-，.，/，:，;，=，?，@，_，~，0-9，a-z，A-Z
     * 空格转"%20"不转"+","+"转"%2B"
     *
     * @author lzx
     */
    public static String encodeUri(String uri) {
        String s = "[^\\s!#$&'()*+,-./:;=?@_~0-9a-zA-Z]+";
        Pattern p = Pattern.compile(s);
        Matcher mat = p.matcher(uri);
        StringBuffer sb = new StringBuffer();
        while (mat.find()) {
            try {
                mat.appendReplacement(sb, URLEncoder.encode(mat.group(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                mat.appendReplacement(sb, mat.group());
            }
        }
        mat.appendTail(sb);
        String dst = sb.toString();
        dst = dst.replaceAll("\\s", "%20").replaceAll("\\+", "%2B");
        return dst;
    }

    /**
     * encodeUriComponent:用于对网址参数进行编码,不编码字符有71个：
     * !， '，(，)，*，-，.，_，~，0-9，a-z，A-Z
     * 空格转"%20"不转"+","+"转"%2B"
     *
     * @author lzx
     */
    public static String encodeUriComponent(String uriComponent) {
        String s = "[^\\s+!'()*-._~0-9a-zA-Z]+";
        Pattern p = Pattern.compile(s);
        Matcher mat = p.matcher(uriComponent);
        StringBuffer sb = new StringBuffer();
        while (mat.find()) {
            try {
                mat.appendReplacement(sb, URLEncoder.encode(mat.group(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                mat.appendReplacement(sb, mat.group());
            }
        }
        mat.appendTail(sb);
        String dst = sb.toString();
        dst = dst.replaceAll("\\s", "%20").replaceAll("\\+", "%2B");
        return dst;
    }

    @Override
    public String toString() {
        return "Authentication{" +
                "mDomain='" + mDomain + '\'' +
                ", mKey='" + mKey + '\'' +
                ", mTimestamp=" + mTimestamp +
                '}';
    }
}
