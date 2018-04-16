/*
 * Copyright (C) 2015 Federico Iosue (federico.iosue@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.readboy.textbook.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class StorageHelper {
	private static final String TAG = "StorageHelper";
	
	public static final String MIME_TYPE_IMAGE = "image/jpeg";
	public static final String MIME_TYPE_AUDIO = "audio/amr";
	public static final String MIME_TYPE_VIDEO = "video/mp4";
	public static final String MIME_TYPE_SKETCH = "image/png";
	public static final String MIME_TYPE_FILES = "file/*";

	public static final String MIME_TYPE_IMAGE_EXT = ".jpeg";
	public static final String MIME_TYPE_AUDIO_EXT = ".amr";
	public static final String MIME_TYPE_VIDEO_EXT = ".mp4";
	public static final String MIME_TYPE_SKETCH_EXT = ".png";
	public static final String MIME_TYPE_CONTACT_EXT = ".vcf";

    public static boolean checkStorage() {
        boolean mExternalStorageAvailable;
        boolean mExternalStorageWriteable;
        String state = Environment.getExternalStorageState();

        switch (state) {
            case Environment.MEDIA_MOUNTED:
                // We can read and write the media
                mExternalStorageAvailable = mExternalStorageWriteable = true;
                break;
            case Environment.MEDIA_MOUNTED_READ_ONLY:
                // We can only read the media
                mExternalStorageAvailable = true;
                mExternalStorageWriteable = false;
                break;
            default:
                // Something else is wrong. It may be one of many other states, but
                // all we need
                // to know is we can neither read nor write
                mExternalStorageAvailable = mExternalStorageWriteable = false;
                break;
        }
        return mExternalStorageAvailable && mExternalStorageWriteable;
    }


    public static String getStorageDir() {
        // return Environment.getExternalStorageDirectory() + File.separator +
        // Constants.TAG + File.separator;
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
    }


    public static File getAttachmentDir(Context mContext) {
        return mContext.getExternalFilesDir(null);
    }

    public static boolean copyFile(File source, File destination) {
        try {
            return copyFile(new FileInputStream(source), new FileOutputStream(destination));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Error copying file", e);
            return false;
        }
    }


    /**
     * Generic file copy method
     *
     * @param is Input
     * @param os Output
     * @return True if copy is done, false otherwise
     */
    public static boolean copyFile(InputStream is, OutputStream os) {
        boolean res = false;
        byte[] data = new byte[1024];
        int len;
        try {
            while ((len = is.read(data)) > 0) {
                os.write(data, 0, len);
            }
            is.close();
            os.close();
            res = true;
        } catch (IOException e) {
            Log.e(TAG, "Error copying file", e);
        }
        return res;
    }


    public static boolean deleteExternalStoragePrivateFile(Context mContext, String name) {
        boolean res = false;

        // Checks for external storage availability
        if (!checkStorage()) {
//            Toast.makeText(mContext, mContext.getString(R.string.storage_not_available), Toast.LENGTH_SHORT).show();
            return false;
        }

        File file = new File(mContext.getExternalFilesDir(null), name);
        file.delete();

        return true;
    }


    public static boolean delete(Context mContext, String name) {
        boolean res = false;

        // Checks for external storage availability
        if (!checkStorage()) {
//            Toast.makeText(mContext, mContext.getString(R.string.storage_not_available), Toast.LENGTH_SHORT).show();
            return false;
        }

        File file = new File(name);
        if (file.isFile()) {
            res = file.delete();
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file2 : files) {
                res = delete(mContext, file2.getAbsolutePath());
            }
            res = file.delete();
        }

        return res;
    }


    public static String getRealPathFromURI(Context mContext, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(contentUri, proj, null, null, null);
		if (cursor == null) {
			return null;
		}
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * Create a path where we will place our private file on external
     *
     * @param mContext
     * @param uri
     * @return
     */
    public static File copyToBackupDir(File backupDir, File file) {

        // Checks for external storage availability
        if (!checkStorage()) {
            return null;
        }

        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        File destination = new File(backupDir, file.getName());

        try {
            copyFile(new FileInputStream(file), new FileOutputStream(destination));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Error copying file to backup", e);
            destination = null;
        }

        return destination;
    }


    public static File getCacheDir(Context mContext) {
        File dir = mContext.getExternalCacheDir();
        if (!dir.exists())
            dir.mkdirs();
        return dir;
    }


    public static File getExternalStoragePublicDir() {
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + TAG + File
                .separator);
        if (!dir.exists())
            dir.mkdirs();
        return dir;
    }


    public static File getBackupDir(String backupName) {
        File backupDir = new File(getExternalStoragePublicDir(), backupName);
        if (!backupDir.exists())
            backupDir.mkdirs();
        return backupDir;
    }


    public static File getSharedPreferencesFile(Context mContext) {
        File appData = mContext.getFilesDir().getParentFile();
        String packageName = mContext.getApplicationContext().getPackageName();
        return new File(appData
                + System.getProperty("file.separator")
                + "shared_prefs"
                + System.getProperty("file.separator")
                + packageName
                + "_preferences.xml");
    }


    /**
     * Returns a directory size in bytes
     */
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
    public static long getSize(File directory) {
        StatFs statFs = new StatFs(directory.getAbsolutePath());
        long blockSize = 0;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = statFs.getBlockSizeLong();
            } else {
                blockSize = statFs.getBlockSize();
            }
            // Can't understand why on some devices this fails
        } catch (NoSuchMethodError e) {
            Log.e(TAG, "Mysterious error", e);
        }
        return getSize(directory, blockSize);
    }


    private static long getSize(File directory, long blockSize) {
        File[] files = directory.listFiles();
        if (files != null) {

            // space used by directory itself 
            long size = directory.length();

            for (File file : files) {
                if (file.isDirectory()) {
                    // space used by subdirectory
                    size += getSize(file, blockSize);
                } else {
                    // file size need to rounded up to full block sizes
                    // (not a perfect function, it adds additional block to 0 sized files
                    // and file who perfectly fill their blocks) 
                    size += (file.length() / blockSize + 1) * blockSize;
                }
            }
            return size;
        } else {
            return 0;
        }
    }


    public static boolean copyDirectory(File sourceLocation, File targetLocation) {
        boolean res = true;

        // If target is a directory the method will be iterated
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdirs();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < sourceLocation.listFiles().length; i++) {
                res = res && copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, 
                        children[i]));
            }

            // Otherwise a file copy will be performed
        } else {
            try {
                res = res && copyFile(new FileInputStream(sourceLocation), new FileOutputStream(targetLocation));
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Error copying directory");
                res = false;
            }
        }
        return res;
    }


    /**
     * Retrieves uri mime-type using ContentResolver
     *
     * @param mContext
     * @param uri
     * @return
     */
    public static String getMimeType(Context mContext, Uri uri) {
        ContentResolver cR = mContext.getContentResolver();
        String mimeType = cR.getType(uri);
        if (mimeType == null) {
            mimeType = getMimeType(uri.toString());
        }
        return mimeType;
    }


    /**
     * Tries to retrieve mime types from file extension
     *
     * @param url
     * @return
     */
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }


    /**
     * Retrieves uri mime-type between the ones managed by application
     *
     * @param mContext
     * @param uri
     * @return
     */
    public static String getMimeTypeInternal(Context mContext, Uri uri) {
        String mimeType = getMimeType(mContext, uri);
        mimeType = getMimeTypeInternal(mContext, mimeType);
        return mimeType;
    }


    /**
     * Retrieves mime-type between the ones managed by application from given string
     *
     * @param mContext
     * @param mimeType
     * @return
     */
    public static String getMimeTypeInternal(Context mContext, String mimeType) {
        if (mimeType != null) {
            if (mimeType.contains("image/")) {
                mimeType = MIME_TYPE_IMAGE;
            } else if (mimeType.contains("audio/")) {
                mimeType = MIME_TYPE_AUDIO;
            } else if (mimeType.contains("video/")) {
                mimeType = MIME_TYPE_VIDEO;
            } else {
                mimeType = MIME_TYPE_FILES;
            }
        }
        return mimeType;
    }


}
