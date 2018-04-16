package com.readboy.mathproblem.http;

/**
 * Created by oubin on 2017/8/14.
 */

public final class HttpConfig {

    private static final boolean IS_TEST_MODE = true;

    // 请求https
    private static final String HTTP_PREFIX = "http://";
    // 请求host
    public static final String HOST = "api.mathproblem.readboy.com/";
    //测试接口
    private static final String TEST_HOST = "192.168.24.241/mathproblem/";
    //正式接口
    public static final String ENDPOINT = HTTP_PREFIX + HOST;
    //测试接口
    public static final String TEST_ENDPOINT = HTTP_PREFIX + TEST_HOST;

    public static final String TEST_AUDIO_HOST = "http://192.168.20.235/";

    public static final String AUDIO_HOST = "http://contres.readboy.com";

    public static final String RESOURCE_HOST = "http://contres.readboy.com";

    public static final String BASE_PATH = "yingyongti/";

    public static final String GUIDE_LIST = "guidelist";
    public static final String METHOD_LIST = "methodlist";
    public static final String TYPE_GUIDE = "guide";
    public static final String TYPE_METHOD = "method";

    //应用题指导
    public static final String GUIDE = "/mathproblem/yingyongti/guide";
    //应用题技巧
    public static final String METHOD = "/mathproblem/yingyongti/method";
    public static final String EXAMPLE = "/mathproblem/yingyongti/example";
    public static final String EXERCISE = "/mathproblem/yingyongti/exercise";

    public static final int CACHE_MAX_SIZE = 20 * 1024 * 1024;

    public static final int READ_TIMEOUT = 60_000;
    public static final int WRITE_TIMEOUT = 60_000;

    public static final String URL1 = "https://codeload.github.com/Bilibili/ijkplayer/zip/master";
    public static final String URL2 = "https://codeload.github.com/Bilibili/DanmakuFlameMaster/zip/master";

    public final static class Download {
        public static final String HOST = HTTP_PREFIX + "rbfdb.strongwind.cn/";
        public static final String HOST2 = HTTP_PREFIX + "api.video.readboy.com/";
    }

    public static class ContentTypes {
        public static final String JSON = "application/json";

        public static final String FORM_MULTIPART = "multipart/form-data boundary=dumi-boundory";
        public static final String APPLICATION_JSON = JSON + ";" + " charset=UTF-8";
        public static final String APPLICATION_AUDIO = "application/octet-stream";
    }

    public static class Parameters {
        public static final String BOUNDARY = "boundary";
        public static final String DATA_METADATA = "metadata";
        public static final String DATA_AUDIO = "audio";
    }

    public static class Path {
        //应用题指导
        public static final String GUIDE = "/mathproblem/yingyongti/guide";
        //应用题技巧
        public static final String METHOD = "/mathproblem/yingyongti/method";
    }

    public static int FETAL_NONE = 6000;  //无条件禁止
    public static int FETAL_SERVICE_OFF = 6001;  //服务器关闭
    public static int FETAL_CLIENT_NOT_SUPPORT = 6002;  //非法或不支持的客户端
    public static int FETAL_UNAUTH = 6003;  //未授权的机型
    public static int FETAL_BLACKLIST = 6004;  //已被列入黑名单
    public static int FETAL_OVER_DUE = 6005;  //授权超时
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


