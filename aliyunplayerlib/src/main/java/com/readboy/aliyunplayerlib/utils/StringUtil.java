package com.readboy.aliyunplayerlib.utils;

import android.content.Context;

import java.util.Locale;

/**
 *
 * Created by ldw on 2017/12/1.
 */

public class StringUtil {

    /**
     * 根据相应的格式，转换为相应的字符串
     */
    public static String format(Context context, int formatId, Object... args){
        String format = context.getString(formatId);
        return String.format(Locale.getDefault(), format, args);
    }

}
