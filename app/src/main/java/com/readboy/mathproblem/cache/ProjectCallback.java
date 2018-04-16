package com.readboy.mathproblem.cache;


/**
 * Created by oubin on 2017/10/31.
 */

public interface ProjectCallback {

    void onComplete();

    void onError(Throwable throwable);

}
