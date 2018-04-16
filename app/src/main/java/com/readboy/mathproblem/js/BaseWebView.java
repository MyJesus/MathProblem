package com.readboy.mathproblem.js;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.readboy.mathproblem.application.Constants;

import java.io.File;

/**
 * Created by oubin on 2017/11/3.
 */

public class BaseWebView extends WebView {
    private static final String TAG = "ScrollBaseWebView";

    private static final String WEB_CACHE_PATH = Constants.WEB_CACHE_PATH;

    private float downX, downY;
    private boolean tryInterceptParent;

    public BaseWebView(Context context) {
        this(context, null);
    }

    public BaseWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
//        缓存模式(5种)
//        1，LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
//        2，LOAD_DEFAULT: 根据cache-control决定是否从网络上取数据。
//        3，LOAD_CACHE_NORMAL: API level 17中已经废弃, 从API level 11开始作用同LOAD_DEFAULT模式
//        4，LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
//        5，LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setAppCachePath(WEB_CACHE_PATH);
        setFadingEdgeLength(0);

        setOnLongClickListener(v -> true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //和外层RecyclerView或者ViewPager滑动冲突处理。
        int action = event.getAction();
//        Log.e(TAG, "onTouchEvent: action = " + action);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();
                downY = event.getRawY();
                tryInterceptParent = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!tryInterceptParent) {
                    break;
                }
                ViewParent parent = getParent();
                if (parent != null) {
                    float x = event.getRawX();
                    float y = event.getRawY();
                    boolean intercept = Math.abs(y - downY) > Math.abs(x - downX);
                    Log.e(TAG, "onTouchEvent: intercept = " + intercept);
                    parent.requestDisallowInterceptTouchEvent(intercept);
                }
                tryInterceptParent = false;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                ViewParent viewParent = getParent();
                if (viewParent != null) {
//                    Log.e(TAG, "onTouchEvent: up or cancel.");
                    viewParent.requestDisallowInterceptTouchEvent(false);
                }
            default:
                break;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 清除WebView缓存
     */
    public void clearWebViewCache() {
        /**清理Webview缓存数据库，缓存文件由程序自动生成
         * /data/data/package_name/database/webview.db
         * /data/data/package_name/database/webviewCache.db
         **/
        try {
            //因为他们都是文件，所以可以用io方式删除，具体方法可以自己写
//            deleteDatabase("webview.db");
//            deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //WebView 缓存文件
        File webViewCacheDir = new File(WEB_CACHE_PATH);
        //删除webView 缓存目录
        if (webViewCacheDir.exists()) {
            //具体的方法自己写
            if (!webViewCacheDir.delete()) {
                Log.e(TAG, "clearWebViewCache: delete web view cache fail!");
            }
        }
    }

}
