package com.readboy.mathproblem.http.download;

import android.util.SparseArray;

/**
 * Created by oubin on 2017/10/30.
 */

public interface VideoUrlCallback {

    /**
     * 数据回到，UI线程
     *
     * @param videoArray 视频详情列表。
     */
    void onResponse(SparseArray<String> videoArray);

    /**
     * 错误处理，UI线程
     *
     * @param throwable 错误信息
     */
    void onError(Throwable throwable);
}
