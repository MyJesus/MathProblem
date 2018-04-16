package com.readboy.mathproblem.http.auth;

import java.util.Properties;

import com.readboy.auth.Auth;

import android.content.Context;

public class AuthHelper {
    private static final String TAG = "AuthHelper";


    public final static String HTTP_COVER = "http://contres.readboy.com/";

    public final static String TEST = "http://192.168.24.222:8000"; // 内网测试网址
    public final static String INTRANCET = ""; // 内网域名
    //http://auth.cdn.readboy.com/auth?sn=tikutest&type=elpsky
    public final static String FORMAL_HOST = "http://auth.cdn.readboy.com/"; //"http://api.video.readboy.com/auth";// 外网域名p
    public final static String HOST = FORMAL_HOST;

    private final static String HTTP_AUTH = HOST;

    private static boolean mHasNew = false;

    public static void newAuth(Context context) {
        if (!mHasNew) {
//            mHasNew = true;
            new Auth(context);
        }
    }

//	public static String postUrlUserInfo() {
//		Properties pro = Auth.getSignature();
//		return HTTP_USER+"?"+"t="+pro.getProperty("t")+"&sn="+pro.getProperty("sn")+"&device_id="+pro.getProperty("device_id");
//	}

    public static String getAuth() {
        Properties pro = Auth.getSignature();
        return HTTP_AUTH + "?t=" + pro.getProperty("t") + "&sn=" + pro.getProperty("sn") + "&device_id=" + pro.getProperty("device_id") + "&type=elpsky";
    }
}
