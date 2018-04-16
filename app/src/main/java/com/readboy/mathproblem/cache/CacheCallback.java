package com.readboy.mathproblem.cache;

/**
 * Created by oubin on 2017/9/22.
 * 当无网络时才会回调该方法。
 */

public interface CacheCallback {

    void onBefore();

    void onAfter();

    void onResponse(ProjectEntityWrapper entity);

    void onError(String message, Throwable e);

}
