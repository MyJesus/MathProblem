package com.readboy.mathproblem.util;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.File;

/**
 * Created by guh on 2018/3/30.
 */

public class Utils {
    /**
     * 获取文件的存储目录
     * @param context
     * @return
     */
    public static File getFileStoreDir(Context context){
        File targetDir = context.getExternalFilesDir(null);
        if (targetDir == null || !targetDir.exists()) {
            targetDir = context.getFilesDir();
        }
        return targetDir;
    }

    public final static int NETWORK_NULL = 0;//没有网络
    public final static int NETWORK_WIFI = 1;//当前网络为wifi
    public final static int NETWORK_MOBILE = 2;//当前网络为移动数据
    /**
     * 获取当前可用的网络
     *
     * @param context
     * @return：NETWORK_NULL无可用网络，NETWORK_WIFIwifi可用，NETWORK_MOBILE移动网络可用
     */
    public static int whichNetworkAvailable(Context context)
    {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null)
        {
            return NETWORK_NULL;
        }
        else
        {
            android.net.NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if(wifi.isAvailable() && wifi.isConnected())
            {
                return NETWORK_WIFI;
//				return NETWORK_MOBILE;
            }
            else if(mobile.isAvailable() && mobile.isConnected())
            {
                return NETWORK_MOBILE;
            }
        }
        return NETWORK_NULL;
    }
}
