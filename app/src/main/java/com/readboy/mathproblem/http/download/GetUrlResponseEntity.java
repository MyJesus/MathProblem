package com.readboy.mathproblem.http.download;

/**
 * Created by oubin on 2017/9/26.
 */

public class GetUrlResponseEntity {

    /**
     * downloadUrl : http://xgj.elpsky.com:12680/download/mp4qpsp/探秘(行程)之路_钟表问题.mp4
     * localPath : 名师辅导班\全品视频
     */

    private String downloadUrl;
    private String localPath;

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}
