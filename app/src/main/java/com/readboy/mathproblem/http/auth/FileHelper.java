package com.readboy.mathproblem.http.auth;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by guh on 2017/10/16.
 */

public class FileHelper {
    private static final String TAG = "FileHelper";

    /**
     * 判断file相关的json问价是否存在
     *
     * @return
     */
    public static boolean isExist(String filePath) {
        boolean reback = false;
        File file = new File(filePath);
        if (file.exists()) {
            reback = true;
        }
        return reback;
    }

    /**
     * @param srcFl
     * @param dstFl 目标文件有的话，直接被覆盖；
     */
    public static boolean copy(String srcFl, String dstFl) {
        File src = new File(srcFl);
        boolean back = copy(src, dstFl);

        return back;
    }

    /**
     * 复制srcFl文件到dstpath
     *
     * @param srcFl：   源文件
     * @param dstpath： 目标文件有的话，直接被覆盖；
     * @return
     */
    public static boolean copy(File srcFl, String dstpath) {
        boolean back = false;
        InputStream srcIn = null;
        OutputStream dstOut = null;
        if (!srcFl.exists()) {
            return false;
        }
        File dst = new File(dstpath);
        if (!dst.exists()) {
            File pr = new File(dst.getParent());
            if (!pr.exists()) {
                pr.mkdir();
            }
        }
        try {
            srcIn = new FileInputStream(srcFl);
            dstOut = new FileOutputStream(dstpath, false);
            byte[] mBy = new byte[1024];
            int len = -1;
            try {
                while ((len = srcIn.read(mBy)) != -1) {
                    dstOut.write(mBy, 0, len);
                }
                back = true;
            } catch (IOException e) {
                Log.e("", "---- copy read e is " + e);
            }
        } catch (FileNotFoundException e1) {
            Log.e("", "---- copy new stream e1 is " + e1);
        } finally {
            try {
                if (srcIn != null) {
                    srcIn.close();
                }
                if (dstOut != null) {
                    dstOut.close();
                }
            } catch (IOException e) {
                Log.e("", "---- copy close e is " + e);
            }
        }
        return back;
    }


    public static String getAuthAbsolutePath(String filename) {
        return getFullPath(filename);
    }

    private static String getFullPath(String filename) {
        String filePath = Environment.getExternalStorageDirectory().getPath();
        filePath += File.separator + "Android/data/com.readboy.mediaplayer/cache";
        File parent = new File(filePath);
        if (!parent.exists() && !parent.mkdirs()) {
            Log.e(TAG, "getFullPath: can't mkdirs, path = " + parent.getAbsolutePath());
            return parent.getAbsolutePath() + File.separator + filename;
        }
        filePath += File.separator + filename;
        return filePath;
    }


}
