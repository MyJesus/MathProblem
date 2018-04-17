package com.readboy.aliyunplayerlib.utils;

import java.util.Locale;

/**
 * 大数据部接口签名等参数的工具类
 * Created by ldw on 2018/2/27.
 */

public class DataSnUtil {

    private static String APP_SECRET = "";//跟大数据部欧阳瑞荣申请

    public static final String DEVICE_ID = "device_id";
    public static final String T = "t";
    public static final String SN = "sn";

    /**
     * 设置appSecret
     */
    public static void setAppSecret(String appSecret){
        APP_SECRET = appSecret;
    }

    /**
     * 参数device_id
     */
    public static String getDeviceId(){
        return DeviceUtil.getModel() + "//" + AppUtil.getPackageName() + "////////";
    }

    /**
     * 参数t
     */
    public static String getT(){
        //时间戳取10位，单位为秒
        long timeStampSec = System.currentTimeMillis()/1000;
        return String.format(Locale.getDefault(), "%010d", timeStampSec);
    }

    /**
     * 参数sn
     */
    public static String getSn(){
        return MD5Util.getMd5(getDeviceId() + APP_SECRET + getT());
    }


}
