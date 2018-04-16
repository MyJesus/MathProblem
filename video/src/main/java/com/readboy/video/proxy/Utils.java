package com.readboy.video.proxy;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.readboy.video.tools.MediaScannerMonster;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 工具类
 */
public class Utils {
    private static final String TAG = "com.readboy.proxy.utils";

    /**
     * 获取重定向后的URL，即真正有效的链接
     *
     * @param urlString
     * @return
     */
    static protected String getRedirectUrl(String urlString) {
        String result = urlString;
        // 取得取得默认的HttpClient实例

        DefaultHttpClient httpClient = new DefaultHttpClient();

        // 创建HttpGet实例
        HttpGet request = new HttpGet(urlString);
        try {
            // 重定向设置连接服务器
            httpClient.setRedirectHandler(new RedirectHandler() {
                @Override
                public URI getLocationURI(HttpResponse response,
                                          HttpContext context) throws ProtocolException {
                    int statusCode = response.getStatusLine().getStatusCode();
                    if ((statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
                            || (statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
                            || (statusCode == HttpStatus.SC_SEE_OTHER)
                            || (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
                        // 此处重定向处理
                        return null;
                    }
                    return null;
                }

                @Override
                public boolean isRedirectRequested(HttpResponse response,
                                                   HttpContext context) {
                    return false;
                }

            });
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if ((statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
                    || (statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
                    || (statusCode == HttpStatus.SC_SEE_OTHER)
                    || (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
                // 从头中取出转向的地址
                Header locationHeader = response.getFirstHeader("Location");
                if (locationHeader != null) {
                    String locationUrl = locationHeader.getValue();//Location Url
                    httpClient.getConnectionManager().shutdown();// 释放连接
                    return getRedirectUrl(locationUrl);//防止多次重定向
                }
            }
        } catch (ClientProtocolException ex) {
            Log.e(TAG, getExceptionMessage(ex));
        } catch (IOException ex) {
            Log.e(TAG, getExceptionMessage(ex));
        }
        httpClient.getConnectionManager().shutdown();// 释放连接
        return result;
    }

    static protected String getSubStringContainStartString(String source, String startStr, String endStr) {
        int startIndex = source.indexOf(startStr);
        int endIndex = source.indexOf(endStr, startIndex);
        return source.substring(startIndex, endIndex);
    }

    static protected String getSubString(String source, String startStr, String endStr) {
        int startIndex = source.indexOf(startStr) + startStr.length();
        int endIndex = source.indexOf(endStr, startIndex);
        return source.substring(startIndex, endIndex);
    }

    /**
     * 获取有效的文件名
     *
     * @param str
     * @return
     */
    static protected String getValidFileName(String str) {
        str = str.replace("\\", "");
        str = str.replace("/", "");
        str = str.replace(":", "");
        str = str.replace("*", "");
        str = str.replace("?", "");
        str = str.replace("\"", "");
        str = str.replace("<", "");
        str = str.replace(">", "");
        str = str.replace("|", "");
        str = str.replace(" ", "_");    //前面的替换会产生空格,最后将其一并替换掉
        return str;
    }

    /**
     * 获取外部存储器可用的空间
     *
     * @return
     */
    static protected long getAvailableSize(String dir) {
        StatFs stat = new StatFs(dir);//path.getPath());
        // 获取block数量
        long totalBlocks = stat.getBlockCountLong();
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        // 获取可用大小
        return availableBlocks * blockSize;
    }

    /**
     * 获取文件夹内的文件，按日期排序，从旧到新
     *
     * @param dirPath
     * @return
     */
    static private List<File> getFilesSortByDate(String dirPath) {
        List<File> result = new ArrayList<File>();
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return result;
        }

        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(
                        f2.lastModified());
            }
        });

        for (int i = 0; i < files.length; i++) {
            result.add(files[i]);
            Log.i(TAG, i + ":" + files[i].lastModified() + "---" + files[i].getPath());
        }
        return result;
    }

    /**
     * 删除多余的缓存文件
     *
     * @param dirPath 缓存文件的文件夹路径
     * @param maximun 缓存文件的最大数量
     */
    static protected void asynRemoveBufferFile(final String dirPath, final int maximun) {
        new Thread() {
            public void run() {
                List<File> lstBufferFile = Utils.getFilesSortByDate(dirPath);
                while (lstBufferFile.size() > maximun) {
                    Log.i(TAG, "---delete " + lstBufferFile.get(0).getPath());
                    lstBufferFile.get(0).delete();
                    lstBufferFile.remove(0);
                }
            }
        }.start();
    }

    public static String getExceptionMessage(Exception ex) {
        String result = "";
        StackTraceElement[] stes = ex.getStackTrace();
        for (int i = 0; i < stes.length; i++) {
            result = result + stes[i].getClassName()
                    + "." + stes[i].getMethodName()
                    + "  " + stes[i].getLineNumber() + "line"
                    + "\r\n";
        }
        return result;
    }


    static public String getCachePath(Context context, String filepath, String filename) {
        String mCacheRootPath = null;
        if (filepath != null) {
            if (filepath.charAt(filepath.length() - 1) == File.separatorChar) {
                filepath = filepath.substring(0, filepath.length() - 1);
            }

            String exterStoragePath = Environment.getExternalStorageDirectory().getPath();
            if (exterStoragePath.charAt(exterStoragePath.length() - 1) == File.separatorChar) {
                exterStoragePath += filepath;
            } else {
                exterStoragePath += File.separator + filepath;
            }

            File filedir = new File(exterStoragePath);
            if (!filedir.exists()) {
                filedir.mkdir();
            }
            //该方式会导致内存泄露，连接没有断开。
//			String[] scalpaths = {exterStoragePath};
//			MediaScannerConnection.scanFile(context, scalpaths, null, null);
            new MediaScannerMonster(context, exterStoragePath);
            mCacheRootPath = exterStoragePath + "/" + filename;
        }
        return mCacheRootPath;
    }
}
