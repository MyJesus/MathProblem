package com.android.volley.toolbox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

/**
 * 自定义File下载请求
 * <p>1、参考volley机制，继承其Request类
 * <p>2、用于下载普通文件，不包括图片，因没有返回Bitmap对象
 * <p>3、文件可以保存到用户指定目录(包括文件名)，默认为sdcard/volleycache/下
 */
public class FileRequest extends Request<File> {
    private static final int MB = 1024 * 1024;
    private static final int CACHE_FREESPACE_MIN = 10;//剩余空间最小限制值(MB)

    private final Response.Listener<File> mListener;
    private String mPath;
    private String mFilename;

    /**
     * Decoding lock so that we don't decode more than one image at a time (to avoid OOM's)
     */
    private static final Object sDecodeLock = new Object();

    /**
     * 下载普通文件(不包括图片文件)
     *
     * @param url           网络请求地址
     * @param path          保存到文件指定全路径下
     * @param listener      文件接收监听器
     * @param errorListener 错误接收监听器
     */
    public FileRequest(String url, String path, Response.Listener<File> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        mListener = listener;
        if (path == null) {
            Response.error(new VolleyError("file path error"));
            return;
        }
        int index = path.lastIndexOf("/");
        if (index == -1) {
            Response.error(new VolleyError("file path error"));
            return;
        }
        mPath = path.substring(0, index + 1);
        mFilename = path.substring(index + 1);
    }

    /**
     * 下载普通文件(不包括图片文件)
     *
     * @param url           网络请求地址
     * @param path          要保存的全路径
     * @param listener      文件接收监听器
     * @param errorListener 错误接收监听器
     */
    public FileRequest(String url, Response.Listener<File> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        mListener = listener;
        mPath = getSDPath() + "/babycache/";
        mFilename = getFilename(url);
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }

    @Override
    protected Response<File> parseNetworkResponse(NetworkResponse response) {
        // Serialize all decode on a global lock to reduce concurrent heap usage.
        synchronized (sDecodeLock) {
            try {
                return doParse(response);
            } catch (OutOfMemoryError e) {
                VolleyLog.e("Caught OOM for %d byte image, url=%s", response.data.length, getUrl());
                return Response.error(new ParseError(e));
            }
        }
    }

    /**
     * The real guts of parseNetworkResponse. Broken out for readability.
     */
    private Response<File> doParse(NetworkResponse response) {
        byte[] data = response.data;
        Log.e("FileRequest", "doParse: data = " + new String(data.clone()));

        //判断sdcard上的空间  
        if (lowOnFreeSpace()) {
            Log.e("FileRequest", "[doParse] low on space");
            return null;
        }

        File file = new File(mPath);
        if (!file.exists())
            file.mkdirs();

        file = new File(mPath + mFilename);
        if (mFilename == null || mFilename == "")
            Response.error(new VolleyError("file path error"));
        if (file.exists())
            Response.error(new VolleyError("file exists"));

        try {
            if (!file.createNewFile()){
                Response.error(new VolleyError("FileCan'tCreateException"));
            }
            OutputStream outStream = new FileOutputStream(file);
            outStream.write(data);
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Response.error(new VolleyError("FileNotFoundException"));
        } catch (IOException e) {
            e.printStackTrace();
            Response.error(new VolleyError("IOException"));
        }

        return Response.success(file, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(File response) {
        mListener.onResponse(response);
    }

    private String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);  //判断sd卡是否存在  
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();  //获取根目录  
        }
        if (sdDir != null) {
            return sdDir.toString();
        } else {
            return "";
        }
    }

    private String getFilename(String path) {
        if (path == null)
            return "";

        int index = path.lastIndexOf("/");
        if (index == -1)
            return path;

        String name = "default";
        String subname = path.substring(index + 1);
        name = subname.equals("") ? "default" : subname;
        return name;
    }

    /**
     * 计算sdcard上的剩余空间
     *
     * @return 空间大小(MB)
     */
    private int getSdFreeSpace() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat.getBlockSize()) / MB;
        return (int) sdFreeMB;
    }

    /**
     * 剩余空间是否过低
     *
     * @return true/false
     */
    private boolean lowOnFreeSpace() {
        if (CACHE_FREESPACE_MIN > getSdFreeSpace())
            return true;

        return false;
    }
}
