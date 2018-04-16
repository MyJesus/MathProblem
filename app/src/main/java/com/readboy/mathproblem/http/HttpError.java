package com.readboy.mathproblem.http;

/**
 * Created by oubin on 2017/9/18.
 */

public final class HttpError {

    public static int ERROR_NONE = 6000;  //无条件禁止
    public static int ERROR_SERVICE_OFF = 6001;  //服务器关闭
    public static int ERROR_CLIENT_NOT_SUPPORT = 6002;  //非法或不支持的客户端
    public static int ERROR_UNAUTH = 6003;  //未授权的机型
    public static int ERROR_BLACKLIST = 6004;  //已被列入黑名单
    public static int ERROR_SIGNATURE_EXPIRE = 6005;  //授权超时
    public static int ERROR_UNKNOWN = 7000;  //未知错误
    public static int ERROR_SYSTEM = 7001;  //系统错误
    public static int ERROR_DATABASE = 7002;  //数据库错误
    public static int ERROR_BUSY = 7003;  //系统繁忙
    public static int ERROR_PARAM = 7004;  //参数错误
    public static int ERROR_TOOFAST = 7005;  //频繁调用
    public static int ERROR_ALREADY_EXISTS = 7006;  //已经存在
    public static int ERROR_NOT_FOUND = 7007;  //未找到
    public static int ERROR_BAD_REQUEST = 7008;  //错误请求
    public static int ERROR_ACCESS = 7009;  //未登录
    public static int ERROR_NOT_PERMIT = 7010;  //权限不足
    public static int ERROR_IO_ERROR = 7011;  //IO错误
    public static int ERROR_LIMIT = 7012;  //超出限额
    public static int ERROR_EMPTY_DATA = 7013;  //空数据
    public static int ERROR_NO_MONEY = 7014;  //资金不足
    public static int ERROR_BAD_STATE = 7015;  //状态错误
    public static int ERROR_BAD_CONTENT = 7016;  //错误数据
    public static int ERROR_BAD_TOKEN = 7200;  //token无效


}
