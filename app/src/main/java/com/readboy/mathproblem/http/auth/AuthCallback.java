package com.readboy.mathproblem.http.auth;

/**
 * Created by oubin on 2017/10/30.
 */

public interface AuthCallback {
    void onAuth(String url);

    void onError(Throwable throwable);
}
