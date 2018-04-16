package com.readboy.mathproblem.util;

import android.os.Build;

/**
 * Created by oubin on 2017/8/16.
 */

public class BuildUtils {

    public static String PACAKGE_NAME = "com.readboy.mathproblem";

    public static boolean isNMR1OrLater() {
//        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1;
        return Build.VERSION.SDK_INT >= 25;
    }

    /**
     * @return {@code true} if the device is prior to {@link Build.VERSION_CODES#LOLLIPOP}
     */
    public static boolean isPreL() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * @return {@code true} if the device is {@link Build.VERSION_CODES#LOLLIPOP} or
     * {@link Build.VERSION_CODES#LOLLIPOP_MR1}
     */
    public static boolean isLOrLMR1() {
        final int sdkInt = Build.VERSION.SDK_INT;
        return sdkInt == Build.VERSION_CODES.LOLLIPOP || sdkInt == Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    /**
     * @return {@code true} if the device is {@link Build.VERSION_CODES#LOLLIPOP} or later
     */
    public static boolean isLOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * @return {@code true} if the device is {@link Build.VERSION_CODES#LOLLIPOP_MR1} or later
     */
    public static boolean isLMR1OrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    /**
     * @return {@code true} if the device is {@link Build.VERSION_CODES#M} or later
     */
    public static boolean isMOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * @return {@code true} if the device is {@link Build.VERSION_CODES#N} or later
     */
    public static boolean isNOrLater() {
        return Build.VERSION.SDK_INT >= 24;
    }

    /**
     * 判断版本
     */
    public static boolean isKitKatOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static String getDeviceId() {
        String separator = "/";
        String separators = "////////";
        StringBuilder builder = new StringBuilder();
        builder.append(Build.MODEL);
        builder.append(separator);
        builder.append(separator);
        builder.append(PACAKGE_NAME);
        builder.append(separators);

        return builder.toString();
    }
}
