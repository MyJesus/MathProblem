package com.readboy.mathproblem.http.auth;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.FileRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;

/**
 * Created by guh on 2017/7/21.
 */

public class VolleyApi {

    private static RequestQueue mQueue = null;

    public static void getAuth(Context context, Response.Listener<File> okListener, Response.ErrorListener errListener) {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(context);
        }

        //TODO: 每次调用都需要创建一个新的Auth。
        AuthHelper.newAuth(context);

        String url = null;
        url = AuthHelper.getAuth();
        Log.e("Auth", "-- getAuthAbsolutePath url "+url);

        FileRequest fileRequest = new FileRequest(url, okListener, errListener);
        mQueue.add(fileRequest);
    }

}
