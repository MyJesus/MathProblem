package com.readboy.mathproblem.cache;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.IOException;

class ThumbnailRequestHandler extends RequestHandler {
    private static final String TAG = "VideoRequestHandler";

    public static String SCHEME_VIDEO = "video";

    @Override
    public boolean canHandleRequest(Request data) {
        if (data.uri == null) {
            return false;
        }
        String scheme = data.uri.getScheme();
        return (SCHEME_VIDEO.equals(scheme));
    }

    @Override
    public synchronized Result load(Request data, int arg1) throws IOException {
        //TODO java.lang.Throwable: Explicit termination method 'close' not called
//        at java.io.FileInputStream.<init>(FileInputStream.java:80)
//        at java.io.FileInputStream.<init>(FileInputStream.java:105)
//        at android.media.MediaMetadataRetriever.setDataSource(MediaMetadataRetriever.java:68)
//        at android.media.ThumbnailUtils.createVideoThumbnail(ThumbnailUtils.java:162)
        Bitmap bm = ThumbnailUtils.createVideoThumbnail(data.uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        //TODO 如果无法获取bm则返回错误的图片，不可使用Picasso.load.error，会多次调用该方法。
        return new Result(bm, Picasso.LoadedFrom.DISK);
    }

}