package com.readboy.mathproblem.util;

import android.net.Uri;

import com.readboy.mathproblem.application.Constants;
import com.readboy.mathproblem.application.MathApplication;
import com.readboy.mathproblem.video.tools.Constant;

import java.io.File;

/**
 * Created by oubin on 2017/10/9.
 */

public final class VideoUtils {
    private static final String TAG = "Utils";

    public static boolean videoIsExist(String fileName) {
        return videoIsExist(new File(getVideoPath(fileName)));
    }

    public static boolean exists(String uri) {
        return videoIsExist(FileUtils.getFileName(uri));
    }

    public static boolean videoIsExist(int videoId) {
//        String filename = Video2DbController.getInstance().getFileName(videoId);
//        return !TextUtils.isEmpty(filename) && videoIsExist(filename);
        return false;
    }

    public static boolean videoIsExist(File file) {
        return file != null && file.exists();
    }

    public static String getVideoPath(String fileName) {
        int last = fileName.lastIndexOf(".");
        if (last > 0) {
            return Constants.getDownloadPath(MathApplication.getInstance()) + File.separator + fileName;
        } else {
            return Constants.getVideoPath(fileName);
        }
    }

    public static String getUri(String url) {
        Uri uri = Uri.parse(url);
        return uri.getPath();
    }

    public static void getVideoAbsolutePath(String uri) {

    }

}
