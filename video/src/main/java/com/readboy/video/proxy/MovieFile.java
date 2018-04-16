package com.readboy.video.proxy;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import com.readboy.video.db.DatabaseProxy;
import com.readboy.video.db.VideoDatabaseInfo;
import com.readboy.video.db.VideoInfoDatabaseProxy;
import com.readboy.video.tools.MediaScannerMonster;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MovieFile {

    static public void scanFile(Context context, String filePath) {
//		String[] scalpaths = {filePath};
//		scanFile(context, scalpaths);
        new MediaScannerMonster(context, filePath);
    }

    //可能内存泄露，连接没有断开。
//    static public void scanFile(Context context, String[] scalpaths) {
//        MediaScannerConnection.scanFile(context, scalpaths, null, null);
//    }

    static public String getCachePath(Context context, String filename) {
        String mCacheRootPath = null;
        if (filename != null) {
            mCacheRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
            File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
            scanFile(context, appCacheDir.getAbsolutePath());
            mCacheRootPath = appCacheDir.getAbsolutePath() + "/" + filename;
        }
        return mCacheRootPath;
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
            scanFile(context, exterStoragePath);
            mCacheRootPath = exterStoragePath + "/" + filename;
        }
        return mCacheRootPath;
    }

    static public boolean saveMoviePlayInfo(String path, long currentPos, long duration) {
        boolean back = false;
        if (path != null) {
            DatabaseProxy.DataItem item = DatabaseProxy.queryItem(path);
            if (item != null) {
                long current = currentPos;
                int play = 0;
                if (current > duration - 1000) {
                    current = 0;
                    play = 1;
                }
                DatabaseProxy.updateItem(path, current, duration, item.mPlay | play);
                back = true;
            }
        }
        return back;
    }

    /**
     * @param datapath
     * @param dataname
     * @param dependency
     * @param type
     * @param currentPos
     * @param duration
     * @param size
     * @param cachepath
     * @param cachename
     * @return
     */
    static public boolean saveVideoInfo(String datapath, String dataname, String dependency, int type, long currentPos, long duration,
                                        long size, String cachepath, String cachename) {
        boolean back = false;
        if (datapath != null && dataname != null && dependency != null) {
            VideoInfoDatabaseProxy.VideoInfoItem item = VideoInfoDatabaseProxy.queryItem(datapath, dataname, dependency, type);
            if (item != null) {
                long current = currentPos;
                int play = 0;
                if (current > duration - 2000) {
                    current = 0;
                    play = 1;
                }
                if (size <= 0) {
                    size = item.mSize;
                }
                VideoInfoDatabaseProxy.updateItem(datapath, dataname, dependency, type, play, current, duration, size, cachepath, cachename);
                back = true;
                item.printf();
            }
        }
        return back;
    }

    static public boolean saveVideoInfo(VideoDatabaseInfo datainfo, long currentPos, long duration, long size) {
        boolean back = false;
        if (datainfo != null) {
            String datapath = datainfo.mDataPath;
            String dataname = datainfo.mDataName;
            String dependency = datainfo.mDependency;
            int type = datainfo.mType;
            if (datapath != null && dataname != null && dependency != null) {
                VideoInfoDatabaseProxy.VideoInfoItem item = VideoInfoDatabaseProxy.queryItem(datapath, dataname, dependency, type);
                if (item != null) {
                    long current = currentPos;
                    int play = 0;
                    if (current > duration - 2000) {
                        current = 0;
                        play = 1;
                    }
                    VideoInfoDatabaseProxy.updateItem(datapath, dataname, dependency, type, play, current, duration, size,
                            datainfo.mCacheFilePath, datainfo.mCacheName);
                    back = true;
                    item.printf();
                }
            }
        }
        return back;
    }


    /**
     * 判断本地视频文件是否是加密文件
     *
     * @param path
     * @return
     */
    static public boolean isEncryption(Context context, String path) {
        boolean encryption = false;
        try {
            int type = getMediaType(path);
//			Log.e("MovieFile", "media type: "+type + ", mPath: " + path);
            if (MovieConfig.USED_CEDARX) {
                encryption = (type == 3 || type == 4);
            } else {
                encryption = (type == 1 || type == 2 || type == 3 || type == 4);
            }
        } catch (IllegalArgumentException e) {
//			Toast.makeText(context, "视频文件损坏！", Toast.LENGTH_LONG).show();
            Log.e("", "isEncryption e: " + e);
        }
//		Log.e("MovieFile", "media encryption: "+encryption + ", mPath: " + path);
        return encryption;
    }


    /**
     * @param path
     * @return
     * @throws IllegalArgumentException
     */
    static public int getMediaType(String path) throws IllegalArgumentException {
        int type = 0;
        byte info[] = new byte[8];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            if (8 == fis.read(info)) {
                if (info[0] == 'R' && info[1] == 'M' && info[2] == 'R' && info[3] == 'B') {// Decode File
                    type = 1;
                } else if ((info[0] == 'R') && (info[1] == 'D') && (info[2] == 'M') && (info[3] == 'V')) {// the first decode type
                    type = 1;
                } else if (info[0] == 'R' && info[1] == 'e' && info[2] == 'a' && info[3] == 'd'
                        && info[4] == 'B' && info[5] == 'o' && info[6] == 'y' && info[7] == ' ') {
                    type = 2;
                } else if (info[0] == 'M' && info[1] == 'P' && info[2] == 'R' && info[3] == 'B') {// Decode File
                    type = 3;
                } else if (info[0] == 'R' && info[1] == 'M' && info[2] == 'X') {
                    type = 4;
                }
            }

            if (path.toUpperCase().endsWith(".RF5")) {
                if (type != 2) {
                    fis.close();
                    throw new IllegalArgumentException("bad type");
                }
            } else if (path.toUpperCase().endsWith(".RMVB") && type == 2) {
                fis.close();
                throw new IllegalArgumentException("bad type");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return type;
    }
}
