package com.readboy.mathproblem.http.download;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by oubin on 2017/9/26.
 * 获取视频下载
 */

public interface GetUrlService {

    /**
     * @param fileName 视频名称
     */
    @GET("/")
    Call<GetUrlResponseEntity> getUrl(@Query("file") String fileName);
}
