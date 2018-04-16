package com.readboy.video.proxy;

import android.content.Context;

/**
 * Created by oubin on 2017/12/26.
 */

public abstract class BaseHttpGetProxy {

    public BaseHttpGetProxy(Context context, String fileName, HttpGetProxy2.OnErrorHttpStatusCodeListener listener) {
        this(context, fileName, null, null, false, listener);
    }

    public BaseHttpGetProxy(Context context, String url, String cacheDir, String cacheName, boolean isOnline, HttpGetProxy2.OnErrorHttpStatusCodeListener listener) {

    }

    public interface OnErrorHttpStatusCodeListener {
        public void onErrorCode(int httpStatusCode);
    }

}
