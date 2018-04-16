package com.readboy.textbook.util;

import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtils {

	//使用该地址是否会有问题
	public static String baseUrl = "http://192.168.20.235";

	public static final String RESOURCE_HOST = "http://contres.readboy.com";
	
	/**
	 * 判断是不是wifi网络状态
	 * 
	 * @param paramContext
	 * @return
	 */
	public static boolean isWifi(Context paramContext) {
		return "2".equals(getNetType(paramContext)[0]);
	}

	/**
	 * 判断是不是2/3G网络状态
	 * 
	 * @param paramContext
	 * @return
	 */
	public static boolean isMobile(Context paramContext) {
		return "1".equals(getNetType(paramContext)[0]);
	}

	public static boolean isNetAvailable(Context paramContext) {
		if ("1".equals(getNetType(paramContext)[0])
				|| "2".equals(getNetType(paramContext)[0])) {
			return true;
		}
		return false;
	}

	/**
	 * 获取当前网络状态 返回2代表wifi,1代表2G/3G
	 * 
	 * @param paramContext
	 * @return
	 */
	public static String[] getNetType(Context paramContext) {
		String[] arrayOfString = { "Unknown", "Unknown" };
		PackageManager localPackageManager = paramContext.getPackageManager();
		if (localPackageManager.checkPermission(
				"android.permission.ACCESS_NETWORK_STATE",
				paramContext.getPackageName()) != 0) {
			arrayOfString[0] = "Unknown";
			return arrayOfString;
		}
		ConnectivityManager localConnectivityManager = (ConnectivityManager) paramContext
				.getSystemService(Service.CONNECTIVITY_SERVICE);
		if (localConnectivityManager == null) {
			arrayOfString[0] = "Unknown";
			return arrayOfString;
		}
		NetworkInfo localNetworkInfo1 = localConnectivityManager
				.getNetworkInfo(1);
		if (localNetworkInfo1 != null
				&& localNetworkInfo1.getState() == NetworkInfo.State.CONNECTED) {
			arrayOfString[0] = "2";
			return arrayOfString;
		}
		NetworkInfo localNetworkInfo2 = localConnectivityManager
				.getNetworkInfo(0);
		if (localNetworkInfo2 != null
				&& localNetworkInfo2.getState() == NetworkInfo.State.CONNECTED) {
			arrayOfString[0] = "1";
			arrayOfString[1] = localNetworkInfo2.getSubtypeName();
			return arrayOfString;
		}
		return arrayOfString;
	}
	
	/**
	 * 判断网络是否可用
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null)
		{
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null)
			{
				for (int i = 0; i < info.length; i++)
				{
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}
				}
			}
		}
		return false;
	}
}