package com.readboy.mathproblem.http.auth;

import java.util.Map;

/**
 * Created by oubin on 2017/10/30.
 */

public interface MultiAuthCallback {
    void onAuth(Map<String, String> urlMap);

    void onError(Throwable throwable);
}
