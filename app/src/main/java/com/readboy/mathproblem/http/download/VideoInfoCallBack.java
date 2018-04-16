package com.readboy.mathproblem.http.download;

import com.readboy.mathproblem.http.response.VideoInfoEntity;

import java.util.List;

/**
 * Created by oubin on 2017/10/26.
 * 通过Video2（id）, 获取视频详情（视频链接等）包括鉴权后的完整链接，回调。
 * @author oubin
 */

public interface VideoInfoCallBack {

    /**
     * 数据回到，UI线程
     *
     * @param videoList 视频详情列表。
     */
    void onResponse(List<VideoInfoEntity.VideoInfo> videoList);

    /**
     * 错误处理，UI线程
     *
     * @param throwable 错误信息
     */
    void onError(Throwable throwable);
}


