package com.readboy.aliyunplayerlib.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

/**
 * 文件工具类
 * Created by ldw on 2017/1/5.
 */
public class FileUtil {

    /**
     * 获取全部缓存大小，包括内置和外置SD卡的cache目录
     * @param context
     * @return
     */
    public static String getTotalCacheSize(Context context) {
        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
        }
        return getFormatSize(cacheSize);
    }

    /**
     * 清除全部的缓存
     * @param context
     */
    public static void cleanAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    /**
     * 删除目录
     * @param dir
     * @return
     */
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    /**
     * 删除文件和该文件的.tmp缓存文件
     * @param filePath
     */
    public static void deleteFileAndCacheFile(String filePath){
        if(!TextUtils.isEmpty(filePath)){
            File file = new File(filePath);
            if(file.exists()) {
                deleteFile(file);
            }
            File cacheFile = new File(filePath + ".tmp");
            if(cacheFile.exists()) {
                deleteFile(cacheFile);
            }
        }
    }

    public static boolean deleteFile(String filePath){
        if(!TextUtils.isEmpty(filePath)){
            File file = new File(filePath);
            return deleteFile(file);
        }else{
            return false;
        }
    }

    public static boolean deleteFile(File file){
        if(file != null) {
            return file.delete();
        }else{
            return false;
        }
    }

    // 获取文件
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public static long getFolderSize(File file) {
        long size = 0;
        File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            // 如果下面还有文件
            if (fileList[i].isDirectory()) {
                size = size + getFolderSize(fileList[i]);
            } else {
                size = size + fileList[i].length();
            }
        }
        return size;
    }

    /**
     * 格式化单位
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        if(size < 1024){
            return String.valueOf(size) + "B";
        }
        double kiloByte = size / 1024;

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    /**
     * 建assets下的单个文件拷贝到SD卡目录
     * @param context 上下文
     * @param assetsPath assets下文件路径，比如：test.txt
     * @param sdPath SD卡目录下路径，全路径，比如：sdcard/test.txt
     */
    public static boolean copyAssetToSD(Context context, String assetsPath,String sdPath ){
        AssetManager asset = context.getAssets();
        //循环的读取asset下的文件，并且写入到SD卡
        String[] filenames = null;
        FileOutputStream out = null;
        InputStream in = null;
        try {
            filenames = asset.list(assetsPath);
            if(filenames.length>0) {
                //说明是目录

            } else {
                //说明是文件，直接复制到SD卡
                File SDFlie = new File(sdPath);
                if(!SDFlie.exists()){
                    SDFlie.createNewFile();
                }
                //将内容写入到文件中
                in = asset.open(assetsPath);
                out = new FileOutputStream(SDFlie);
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while((byteCount=in.read(buffer))!=-1){
                    out.write(buffer, 0, byteCount);
                }
                out.flush();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 创建.nomedia文件在指定目录下
     */
    public static boolean createNomediaFile(String dir){
        String path = dir;
        if(!TextUtils.isEmpty(path) && !path.endsWith(File.separator)) {
            path += File.separator;
        }
        path += ".nomedia";
        File file = new File(path);
        if(!file.exists()) {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static void deleteNomediaFile(String dir){
        String path = dir;
        if(!TextUtils.isEmpty(path) && !path.endsWith(File.separator)) {
            path += File.separator;
        }
        path += ".nomedia";
        File file = new File(path);
        if(file.exists()) {
            file.delete();
        }
    }

    /**
     * 判断文件是否存在
     */
    public static boolean isFileExist(String path){
        if(TextUtils.isEmpty(path)){
            return false;
        }
        File file = new File(path);
        return file.exists();
    }

    /**
     * 创建路径
     * @param path
     * @return
     */
    public static boolean createDir(String path){
        if(TextUtils.isEmpty(path)){
            return false;
        }
        File file = new File(path);
        return file.mkdirs();
    }


    /**
     * 删除阿里云相关的视频信息
     * @param filePath
     */
    public static void deleteAliyunFiles(String filePath){
        if(!TextUtils.isEmpty(filePath)){
            File file = new File(filePath);
            File parentFile = file.getParentFile();
            String fileName = file.getName();
            String fileNameNoSuffix = fileName.substring(0, fileName.lastIndexOf("."));
            //Log.v("FileUtil", "---deleteAliyunFiles---fileNameNoSuffix = " + fileNameNoSuffix);

            deleteDir(new File(parentFile, fileNameNoSuffix));
            deleteFile(new File(parentFile, fileNameNoSuffix + ".m3u8"));
            deleteFile(file);
        }
    }

}
