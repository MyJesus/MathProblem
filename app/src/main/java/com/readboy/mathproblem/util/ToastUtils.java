package com.readboy.mathproblem.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.readboy.mathproblem.application.MathApplication;

/**
 * Created by 1 on 2016/8/30.
 */

public class ToastUtils {

    private static Toast toast;
    private static String oldMsg = "";
    private static long oneTime = 0;
    private static long twoTime = 0;


    /*
     * backgroundID = 0 代表使用默认背景 backgroundID = 1
     */
    private static void initToast(Context context, int backgroundID) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
        }
        if (backgroundID != 0) {
            View view = toast.getView();
            view.setBackgroundResource(backgroundID);
            view.getBackground().setAlpha(180);
            toast.setView(view);
        }
    }

    private ToastUtils() {
        throw new UnsupportedOperationException("u can't fuck me...");
    }

    public static void show(String msg) {
        show(MathApplication.getInstance(), msg);
    }

    public static void show(Context context, String msg) {
        show(context, msg, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, String msg, int time) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (toast == null) {
            initToast(context, 0);
            toast.setText(msg);
            toast.setDuration(time);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (msg.equals(oldMsg)) {
                if (twoTime - oneTime > time) {
                    toast.setDuration(time);
                    toast.show();
                }
            } else {
                oldMsg = msg;
                toast.setDuration(time);
                toast.setText(msg);
                toast.show();
            }
        }
        oneTime = twoTime;
    }

    public static void showShort(Context context, String msg) {
        show(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showLong(String msg) {
        show(MathApplication.getInstance(), msg);
    }

    public static void showLong(Context context, String msg) {
        show(context, msg, Toast.LENGTH_LONG);
    }

    public static void cancel() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
