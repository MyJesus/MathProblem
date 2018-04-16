package com.readboy.mathproblem.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashSet;


/**
 * Created by oubin on 2016/11/4.
 * 工具类，必须解耦，和其他非系统类无关。
 */

public class FileUtils {
    private static final String TAG = "FileUtils";
    public static final String TEMP_POSTFIX = ".download";
    private static final String SPEAK = "speak";
    private static final String VIDEO = "video";
    private static final String IMAGE = "image";


    public static final String[] VIDEO_EXTENSIONS =
            {"264", "3g2", "3gp", "3gp2", "3gpp", "3gpp2", "3mm", "3p2", "60d", "aep", "ajp", "amv",
                    "amx", "arf", "asf", "asx", "avb", "avd", "avi", "avs", "avs", "axm", "bdm",
                    "bdmv", "bik", "bix", "bmk", "box", "bs4", "bsf", "byu", "camre", "clpi", "cpi",
                    "cvc", "d2v", "d3v", "dav", "dce", "dck", "ddat", "dif", "dir", "divx", "dlx",
                    "dmb", "dmsm", "dmss", "dnc", "dpg", "dream", "dsy", "dv", "dv-avi", "dv4",
                    "dvdmedia", "dvr-ms", "dvx", "dxr", "dzm", "dzp", "dzt", "evo", "eye", "f4p",
                    "f4v", "fbr", "fbr", "fbz", "fcp", "flc", "flh", "fli", "flv", "flx", "gl", "grasp",
                    "gts", "gvi", "gvp", "hdmov", "hkm", "ifo", "imovi", "imovi", "iva", "ivf",
                    "ivr", "ivs", "izz", "izzy", "jts", "lsf", "lsx", "m15", "m1pg", "m1v", "m21",
                    "m21", "m2a", "m2p", "m2t", "m2ts", "m2v", "m4e", "m4u", "m4v", "m75", "meta",
                    "mgv", "mj2", "mjp", "mjpg", "mkv", "mmv", "mnv", "mod", "modd", "moff", "moi",
                    "moov", "mov", "movie", "mp21", "mp21", "mp2v", "mp4", "mp4v", "mpe", "mpeg",
                    "mpeg4", "mpf", "mpg", "mpg2", "mpgin", "mpl", "mpls", "mpv", "mpv2", "mqv",
                    "msdvd", "msh", "mswmm", "mts", "mtv", "mvb", "mvc", "mvd", "mve", "mvp", "mxf",
                    "mys", "ncor", "nsv", "nvc", "ogm", "ogv", "ogx", "osp", "par", "pds", "pgi",
                    "piv", "playlist", "pmf", "prel", "pro", "prproj", "psh", "pva", "pvr", "pxv",
                    "qt", "qtch", "qtl", "qtm", "qtz", "rcproject", "rdb", "rec", "rm", "rmd", "rmp",
                    "rmvb", "roq", "rp", "rts", "rts", "rum", "rv", "sbk", "sbt", "scm", "scm",
                    "scn", "sec", "seq", "sfvidcap", "smil", "smk", "sml", "smv", "spl", "ssm",
                    "str", "stx", "svi", "swf", "swi", "swt", "tda3mt", "tivo", "tix", "tod", "tp",
                    "tp0", "tpd", "tpr", "trp", "ts", "tvs", "vc1", "vcr", "vcv", "vdo", "vdr",
                    "veg", "vem", "vf", "vfw", "vfz", "vgz", "vid", "viewlet", "viv", "vivo",
                    "vlab", "vob", "vp3", "vp6", "vp7", "vpj", "vro", "vsp", "w32", "wcp", "webm",
                    "wm", "wmd", "wmmp", "wmv", "wmx", "wp3", "wpl", "wtv", "wvx", "xfl", "xvid",
                    "yuv", "zm1", "zm2", "zm3", "zmv"};

    private static final HashSet<String> VIDEO_EXTENSIONS_SET;

    static {
        VIDEO_EXTENSIONS_SET = new HashSet<>(Arrays.asList(VIDEO_EXTENSIONS));
    }

    private FileUtils() {
        throw new UnsupportedOperationException("u can't create me ...");
    }

    /**
     * 创建路径
     */
    public static boolean createOrExistsDir(File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 创建一个新的文件，如果存在，则删除在创建
     */
    public static boolean createNewFile(File file) {
        if (file == null) {
            return false;
        }
        if (file.exists() && !file.delete()) {
//            Log.e(TAG, "createNewFile: file is exists and file can not delete.");
            return false;
        }
        if (!createOrExistsDir(file.getParentFile())) {
            Log.e(TAG, "createNewFile: can not create dir, dir = " + file.getAbsolutePath());
            return false;
        }

        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "createNewFile: e = " + e.toString());
            return false;
        }
    }

    public static boolean createNewFile(String fileName) {
        return createNewFile(new File(fileName));
    }

    public static boolean createNewFile(String dir, String fileName) {
        return createNewFile(new File(dir, fileName));
    }

    public static boolean writeFileFromIS(File file, InputStream is, boolean append) {
        if (file == null || is == null) {
            return false;
        }
        if (!createNewFile(file)) {
            return false;
        }
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file, append));
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeIO(is);
            closeIO(os);
        }
    }

    public static void closeIO(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void closeIO(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }
    }

    public static void closeIo(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException | IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public static File pullXml(InputStream is, String fileName) throws IOException {
        int sumLength = is.available();
        BufferedInputStream bis = new BufferedInputStream(is);
        bis.mark(sumLength);
        byte[] bytes = new byte[512];
        int len = bis.read(bytes, 0, 256);
        String string = new String(bytes, 0, len);
        if (!string.contains("Success")) {
            Log.e(TAG, "pullXml: string = " + string);
            return null;
        }
        String mark = "</ResponseInfo>";
        int index = string.indexOf(mark);
        int textLength = string.substring(0, index + mark.length()).getBytes().length;
        File file = new File(fileName);
        bis.reset();
        int sum = textLength;
        while (sum > 0) {
            len = bis.read(bytes, 0, sum);
            sum = sum - len;
        }
        if (!writeFileFromIS(file, bis, false)) {
            Log.e(TAG, "pullXml: write file fail!");
        }
        closeIO(is);
        return file;
    }

    public static void addLog(String filePath, String text, boolean addTime) {
        RandomAccessFile raf = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                createNewFile(file);
            }
            raf = new RandomAccessFile(filePath, "rw");
            long length = raf.length();
            Log.d(TAG, "addLog: length = " + length);
            raf.seek(length);
            raf.write("\n".getBytes());
            if (addTime) {
                String time = DateUtils.getCurDateString() + "  ";
                raf.write(time.getBytes());
            }
            raf.write(text.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "addLog: e = " + e.toString() + ", filePath = " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "addLog: e = " + e.toString() + ", filePath = " + filePath);
        } finally {
            closeIO(raf);
        }
    }

    public static boolean isVideo(File f) {
        String ext = getFileExtension(f);
        return VIDEO_EXTENSIONS_SET.contains(ext);
    }

    public static String getFileExtension(File f) {
        if (f != null) {
            String filename = f.getName();
            int i = filename.lastIndexOf(".");
            if (i > 0 && i < filename.length() - 1) {
                return filename.substring(i + 1).toLowerCase();
            }
            return null;
        }
        return null;
    }

    public static void appendStringToFile(String content, String filePath) {
        appendStringToFile(content, new File(filePath));
    }

    /**
     * 日志追加文件
     *
     * @param content 追加的内容
     */
    public static void appendStringToFile(String content, File file) {
        if (file == null) {
            return;
        }
        if (!file.isFile() && file.delete()) {
            Log.e(TAG, "appendStringToFile: file is not file and can't delete.");
            return;
        }
        if (!file.exists()) {
            if (!createNewFile(file)) {
                return;
            }
        }

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write(content);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeIo(out);
        }
    }

    public static boolean writeString(String content, String filePath) {
        return writeString(content, new File(filePath));
    }

    public static boolean writeString(String content, File file) {
        if (file == null || file.isDirectory()) {
            return false;
        }
        createNewFile(file);
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);
            bw.write(content);
            bw.flush();
            fw.flush();
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
            return false;
        } finally {
            closeIO(fw);
            closeIO(bw);
        }
        return false;
    }

    public static String readString(String filePath) {
        if (!new File(filePath).exists()) {
            return null;
        }

        FileInputStream inStream = null;
        ByteArrayOutputStream bos;
        try {
            //FileInputStream 用于读取诸如图像数据之类的原始字节流。要读取字符流，请考虑使用 FileReader。
            inStream = new FileInputStream(filePath);
            bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inStream.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
            return bos.toString();
            // 为什么不一次性把buffer得大小取出来呢？为什么还要写入到bos中呢？ return new(buffer,"UTF-8") 不更好么?
            // return new String(bos.toByteArray(),"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeIO(inStream);
        }
    }

    private static String getSpeakDirPath() {
        String dirPath = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + SPEAK;
            File dir = new File(dirPath);
            if (!dir.exists() && dir.mkdirs()) {
                return "";
            }
        }
        return dirPath;
    }

    public static File getSpeakFile() {
        String dirPath = getSpeakDirPath();
        if (TextUtils.isEmpty(dirPath)) {
            return null;
        }
        return new File(dirPath,
                "dcs_" + System.currentTimeMillis() + ".mp3" + TEMP_POSTFIX);
    }

    public static void sendFileBroadcast(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(path)));
        context.sendBroadcast(intent);
    }

//    public static String getFileName(String uri) {
//        String result;
//        String regex = "/";
//        int index = uri.lastIndexOf(regex);
//        result = uri.substring(index + regex.length(), uri.length());
//        return result;
//    }

    public static String getFileName(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        String result;
        String regex = "/";
        int index = url.lastIndexOf(regex);
        //判断是否是uri
        if (!url.startsWith("http://")) {
            result = url.substring(index + regex.length(), url.length());
            return result;
        }
        int lastIndex = url.lastIndexOf("?auth_key");
        lastIndex = lastIndex < 0 ? url.length() : lastIndex;
        result = url.substring(index + regex.length(), lastIndex);
        try {
            return URLDecoder.decode(result, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return result;
        }
    }

    public static String getFileNameWithoutExtension(String url) {
        String result = getFileName(url);
        return result.substring(0, result.lastIndexOf("."));
    }

    public static boolean delete(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }
        File file = new File(path);
        return !file.exists() || file.delete();
    }

    /**
     * 获取外部存储器可用的空间
     *
     * @return 空闲内存大小，单位：Byte
     */
    public static long getAvailableSize(String dir) {
        StatFs stat = new StatFs(dir);
        // 获取block数量
        long totalBlocks = stat.getBlockCountLong();
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        //获取可用大小
        return availableBlocks * blockSize;
    }

}
