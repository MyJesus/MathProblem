package com.readboy.mathproblem.application;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Environment;

import com.readboy.aliyunplayerlib.utils.FileUtil;

import java.io.File;

/**
 * Created by oubin on 2017/9/4.
 */

public class Constants {

    private static final String APP_NAME = "MathProblem";

    public static final String EMPTY_URL = "about:blank";

    private static final String DIR = Environment.getExternalStorageDirectory()
            + File.separator + APP_NAME;

    /**
     * 旧视频文件目录，4.1.17版本及以前
     */
    public static final String VIDEO_PATH = DIR + File.separator + "video/";
    public static final String TEMP_PATH = DIR + File.separator + "temp";
    public static final String IMAGE_PATH = DIR + File.separator + "image";
    public static final String WEB_CACHE_PATH = DIR + File.separator + "web";

    public static final String ALIYUN_DOWNLOAD_DIR = DIR + File.separator + "download";
    public static final String ALIYUN_SECRET_IMAGE_PATH = DIR + "/aliyun/encryptedApp.dat";

//    public static final String VIDEO_PATH = ALIYUN_DOWNLOAD_DIR;

    public static String getVideoPath(String fileName){
        return ALIYUN_DOWNLOAD_DIR + File.separator + fileName + ".mp4";
    }

    public class Drawable {
        public static final String STAR_POSITIVE = "ic_star_positive";
        public static final String STAR_NEGATIVE = "ic_star_negative";
        public static final String STAR_POSITIVE_BIG = "ic_star_positive_big";
        public static final String STAR_NEGATIVE_BIG = "ic_star_negative_big";
    }

    public static class URL {
        public static final String VIDEO1 =
                "http://data.caidouenglish.com/video/2017/06/16/96/9b/969b8cf1851899a29e858752e952b196.mp4";
        //阿里云OSS
        public static final String VIDEO2 =
                "http://contres.readboy.com/resources/dub/2017/6000009/1ecb23dd58ae9b0870e4ad809c8e31c7.mp4";

        public static final String VIDEO3 =
                "http://xgj.elpsky.com:12680/download/mp4qpsp/探秘(行程)之路_钟表问题.mp4";

        public static final String VIDEO4 =
                "http://d.elpsky.com/download/mp4qpsp/%E6%AF%94%E4%BE%8B%E7%8E%8B%E5%9B%BD%E6%8E%A2%E9%99%A9%E8%AE%B0_%E6%AF%94%E4%BE%8B%E4%B8%AD%E7%9A%84%E5%B7%A5%E7%A8%8B%E9%97%AE%E9%A2%98.mp4?auth_key=1512359416-0-0-4bbf5b3182bc435b7d1884bf36e7fec8";

        public static final String VIDEO5 =
                "http://d.elpsky.com/download/mp4ywb/%E8%AF%AD%E6%96%87%E5%B0%8F%E5%AD%A62%E4%B8%8A%E5%9B%BD%E6%97%97%E5%92%8C%E5%A4%AA%E9%98%B3%E4%B8%80%E5%90%8C%E5%8D%87%E8%B5%B7_F2FF.mp4?auth_key=1512356892-0-0-8b04e023a84a9364985913eab1fd8fc2";

        public static final String VIDEO6 =
                "http://d.elpsky.com/download/mp4qpsp/%E4%B8%81%E4%B8%81%E5%A4%AA%E7%A9%BA%E5%A5%87%E9%81%87%E8%AE%B0_%E5%9C%86%E6%9F%B1%E7%9A%84%E4%BE%A7%E9%9D%A2%E7%A7%AF.mp4?auth_key=1512476420-0-0-98205e91731261a1a4f0e3c1bef6ab15";

        public static final String VIDEO7 =
                "http://d.elpsky.com/download/mp4qpsp/%E7%8B%90%E7%8B%B8%E7%AB%99%E9%95%BF%E4%B8%8A%E4%BB%BB%E5%AE%A3%E8%A8%80.mp4?auth_key=1512476420-0-0-f771041eddfec801b30aa756230009db";

        public static final String VIDEO8 =
                "http://d.elpsky.com/download/mp4qpsp/%E7%8B%90%E7%8B%B8%E7%AB%99%E9%95%BF%E7%9A%84%E6%83%85%E6%8A%A5%E7%AB%99_%E7%BB%9F%E8%AE%A1%E9%87%8F%E7%9A%84%E7%89%B9%E7%82%B9.mp4?auth_key=1512476420-0-0-bc93d7e9ee7f7119b47dcf25354bf582";

        public static final String VIDEO9 =
                "http://d.elpsky.com/download/mp4qpsp/%E7%8B%90%E7%8B%B8%E7%AB%99%E9%95%BF%E7%9A%84%E6%83%85%E6%8A%A5%E7%AB%99_%E7%BB%9F%E8%AE%A1%E8%A1%A8%E5%92%8C%E5%88%86%E7%B1%BB.mp4?auth_key=1512476420-0-0-5d1482a4c8a5c4713efa4f841521147e";
    }
}
