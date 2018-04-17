package com.readboy.aliyunplayerlib.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * Created by dway on 2017/3/18.
 */

public class TimeUtil {

    /**
     * 格式化时间为mm:ss或者h:mm:ss
     * @param ms 单位毫秒
     * @return
     */
    public static String formatTime(long ms){
        int totalSeconds = (int) (ms/1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        if(hours > 0){
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        }else{
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }

    /**
     * 格式化时间戳为mm:ss
     * @param ms 时间戳，单位毫秒
     * @return
     */
    public static String formatTimeStamp(long ms){
        //SimpleDateFormat线程不安全，请勿设置为静态变量
        SimpleDateFormat formatter_mmss = new SimpleDateFormat("mm:ss", Locale.getDefault());
        return formatter_mmss.format(ms);
    }
    /**
     * 记录上一次按钮点击的时间
     */
    private static long lastClickTime = 0;

    /**
     * @return true 快速点击
     * false 非快速点击
     * @aim 判断是否是快速点击
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 <= timeD && timeD <= 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
    /**
     *将time转化为日期
     * @param time 单位秒
     * @return
     */
    public static String getDateTime(long time){
        final SimpleDateFormat formatter_yyyymmdd =new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter_yyyymmdd.format(new Date(time*1000));
    }

    /**
     *将time转化为日期
     * @param ms 单位毫秒
     * @return
     */
    public static String getDateTimeByMs(long ms){
        final SimpleDateFormat formatter_yyyymmdd =new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter_yyyymmdd.format(new Date(ms));
    }
	






}
