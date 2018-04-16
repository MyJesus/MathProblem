package com.readboy.mathproblem.http.auth;

import com.readboy.mathproblem.util.ToastUtils;

/**
 * Created by oubin on 2017/10/30.
 */

public class SimpleAuthCallback implements AuthCallback {
    @Override
    public void onAuth(String url) {

    }

    @Override
    public void onError(Throwable throwable) {
        ToastUtils.show(throwable.toString());
    }
}
