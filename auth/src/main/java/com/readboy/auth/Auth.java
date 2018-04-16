package com.readboy.auth;


import java.util.Properties;

import android.content.Context;
import android.util.Log;


public class Auth {
	private static final String TAG = "Auth";

	/**
	 * 进入应用必须初始化该Auth对象，否者调用{@link #getSignature()}会抛出NullPointerException.
	 * @param context 调用者上下文，比如Activity本身
	 */
	public Auth(Context context) {
		_init(context);
	}

	/**
	 * 获取签名参数
	 * 
	 * @return Properties 通过getProperty("t"), getProperty("sn")获取参数t和sn
	 * 
	 */
	public static Properties getSignature() {
		String signature = getParameters("sn");
		Properties properties = new Properties();
		if (signature != null && signature.length() > 32) {
			properties.setProperty("t", signature.substring(32));
			properties.setProperty("sn", signature.substring(0, 32));
			properties.setProperty("device_id", getParameters("device_id"));
		}
		return properties;
	}

	/**
	 * 设置参数
	 * 
	 * @param key 关键字
	 * @param value 值
	 */
	public static final native void setParameters(String key, String value);

	/**
	 * 获取参数
	 * 
	 * @param key 关键字
	 * @return 值
	 */
	public static final native String getParameters(String key);

	private static final native int _init(Object context);

	static {
		System.loadLibrary("auth");
	}
}
