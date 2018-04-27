package com.readboy.aliyunplayerlib.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * url编码的参数device_id
     */
    public static String getDeviceIdEncodeUrl(){
        return encodeURIComponent(DeviceUtil.getModel() + "//" + AppUtil.getPackageName() + "////////");
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
     * deviceId要保持原样，不进行url编码
     */
    public static String getSn(String t){
        return MD5Util.getMd5(getDeviceId() + APP_SECRET + t);
    }



    /**
     * encodeURI：用于对网址编码(不包含参数),不编码字符有82个：!，#，$，&，'，(，
     * )，*，+，,，-，.，/，:，;，=，?，@，_，~，0-9，a-z，A-Z
     * 空格转"%20"不转"+","+"转"%2B"
     * @param uri
     * @return
     * @author lzx
     */
    public static String encodeURI(String uri){
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
     * encodeURIComponent:用于对网址参数进行编码,不编码字符有71个：
     * !， '，(，)，*，-，.，_，~，0-9，a-z，A-Z
     * 空格转"%20"不转"+","+"转"%2B"
     * @param uriComponent
     * @return
     * @author lzx
     */
    public static String encodeURIComponent(String uriComponent){
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

}
