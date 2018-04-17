package com.readboy.aliyunplayerlib.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 获取设备信息
 * Created by ldw on 2017/3/15.
 */

public class DeviceUtil {
    private static final String TAG = "DeviceUtil";

    /**
     * 获取16位唯一标识
     * @param context
     * @return
     */
    public static String get16bitUUIDMd5(Context context){
        String ret = getUUIDMd5(context);
        ret = ret.substring(8, 24);
        return ret;
    }

    /**
     * 获取自定义唯一标识的32位MD5，防止唯一标识超过40位（UTF-8计算长度）
     * @param context
     * @return
     */
    public static String getUUIDMd5(Context context){
        return MD5Util.getMd5(getUUID(context));
    }

    /**
     * 自定义的设备唯一标识。DeviceId + MacAddress + SerialNum
     */
    public static String getUUID(Context context){
        String uuid;
        try {
            uuid = getDeviceId(context) + getMacAddress(context) + getSerialNum();
        } catch (Exception e) {
            uuid = "unknow";
        }
        Log.v(TAG, "---getUUID---"+uuid);
        return uuid;
    }

    public static String getDeviceId(Context context){
        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getDeviceId();
    }

    public static String getMacAddress(Context context){
        WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        return manager.getConnectionInfo().getMacAddress();
    }

    public static String getSerialNum(){
        return android.os.Build.SERIAL;
    }

    /**
     * 获得机器型号model
     */
    public static String getModel() {
        String module = "";
        try {
            Class<Build> build_class = android.os.Build.class;
            // 取得牌子
            // java.lang.reflect.Field manu_field = build_class
            // .getField("MANUFACTURER");
            // manufacturer = (String) manu_field.get(new android.os.Build());
            // 取得型號
            java.lang.reflect.Field field2 = build_class.getField("MODEL");
            module = (String) field2.get(new android.os.Build());
        } catch (Exception e) {
            module = "unkown";
        }
        return module;
    }

}
