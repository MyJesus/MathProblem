package com.readboy.video;

/**
 * Created by oubin on 2017/11/30.
 */

public class ErrorCode {

    /**
     * Url过期等
     */
    public static final int AUTHENTICATION = 403;

    /**
     * 成功接收，但是不是全部内容，分段分发。
     */
    public static final int SUCCESS = 206;

    /**
     * Requested Range Not Satisfiable。
     * None of the range-specifier values in the Range request-header field overlap the current
     * extent of the selected resource.
     * 请求的range有误，如：起始位置大于终点位置
     */
    public static final int RANGE_NOT_SATISFIABLE = 416;

}
