package com.readboy.mathproblem.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

/**
 * Created by oubin on 2017/5/24.
 */

public final class BitmapUtils {

    private BitmapUtils() {
    }

    public static Bitmap readBitmap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeResource(context.getResources(), resId, opt);
    }

    public static Bitmap getBitmap(Context context, String name, int index) {
        return BitmapFactory.decodeResource(context.getResources(), getResID(context, name, index));
    }

    /**
     * 根据资源名获取ResID
     *
     * @param name
     * @param id
     * @return
     */
    public static int getResID(Context context, String name, int id) {
        return context.getResources().getIdentifier(name + id, "drawable",
                context.getPackageName());
    }

    public static Bitmap getVideoThumbnail(String videoPath, int width, int height) {
        return getVideoThumbnail(videoPath, width, height, MediaStore.Images.Thumbnails.MINI_KIND);
    }

    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap;
        // 获取视频的缩略图，视频最大关键帧
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);

        //获取视频第一个关键帧
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource("/sdcard/0001.mp4");
//        Bitmap bmp = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

        if (bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

}
