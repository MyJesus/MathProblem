package com.readboy.mathproblem.notetool;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import com.readboy.mathproblem.util.FileUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * 笔记数据库， 表：fileInfo(file, ver, len), noteInfo(id, serial, width, height, img)
 * 原逻辑，id对应的高度一定要在ParcelCol#PARCEL_HEIGHT误差范围内，要不会清掉该id对应的数据。
 * 新逻辑，只填，不减不清，设最大值。
 */
public class NoteDatabase {
    private static final String TAG = "NoteDatabase";
    private static String DB_FILE_NAME = NoteConfig.DB_FILE_NAME;

    private static int DB_MAX_FILE_LEN = 260;
    private static int DB_MAX_VER_LEN = 260;
    private String mDbFile;
    private String mDbPath;
    private String mFileName;
    private String mSrcFile;
    private String mSrcFileVersion;
    private String mLastErrorInfo;
    private long mSrcFileLen;
    private SQLiteDatabase mDB;
    private Cursor mCursor;
    private int mParcelNum;
    private int mNoteId;
    private boolean mIsNewFile;

    public NoteDatabase() {
        mLastErrorInfo = "";
    }

    public String getLastError() {
        return mLastErrorInfo;
    }

    public void close() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        if (mDB != null) {
            mDB.close();
            mDB = null;
        }
    }

    /**
     * 初始化数据库，创建并打开
     *
     * @param dbDir   数据库父路径
     * @param version 版本号
     * @return 是否打开成功
     */
    public boolean init(String dbDir, String version) {
        /* 创建目录 */
        mDbPath = dbDir;
        File file = new File(mDbPath);
        if (!file.exists() && !file.mkdirs()) {
            mLastErrorInfo = "创建目录失败！";
            return false;
        }
        mSrcFileLen = file.length();
        mFileName = file.getName();
        mDbFile = mDbPath + File.separator + DB_FILE_NAME;
        mSrcFile = dbDir;
        mSrcFileVersion = version;
        return openAndCheck();
    }

    /**
     * 创建或者打开数据库文件。
     *
     * @see #init(String, String)
     */
    @Deprecated
    public boolean init2(String srcFile, String ver) {

        File file = new File(srcFile);
        if (!FileUtils.createOrExistsDir(file)) {
            mLastErrorInfo = "文件不存在，并且创建失败!";
            return false;
        }

        mSrcFileLen = file.length();
        if (mSrcFileLen <= 0) {
            mLastErrorInfo = "文件大小为零！";
            return false;
        }
        int idx;
        idx = srcFile.lastIndexOf(File.separator);
        if (!(idx > 0 && idx < srcFile.length() - 1)) {
            mLastErrorInfo = "文件名有误！";
            return false;
        }
        mFileName = srcFile.substring(idx + 1);

//		idx = srcFile.lastIndexOf('.');
//		if (idx <= 1) {
//			mLastErrorInfo = "文件名没有后缀名！";
//			return false;
//		}

		/* 创建目录 */
        mIsNewFile = false;
//		mDbPath = srcFile.substring(0, idx);
        mDbPath = srcFile;
        file = new File(mDbPath);
        if (!file.exists()) {
            mIsNewFile = true;
            if (!file.mkdirs()) {
                mLastErrorInfo = "创建目录失败！";
                return false;
            }
        }
        mDbFile = mDbPath + File.separator + DB_FILE_NAME;
        mSrcFile = srcFile;
        mSrcFileVersion = ver;
        return openAndCheck();
    }

    public String getDbPath() {
        return mDbPath;
    }

    //创建和检测版本
    private boolean openAndCheck() {
        Log.e(TAG, "openAndCheck: ");
        /* 创建或打开数据库文件 */
        try {
            if (mDB != null) {
                mDB.close();
                mDB = null;
            }
            mDB = SQLiteDatabase.openDatabase(mDbFile, null,
                    SQLiteDatabase.CREATE_IF_NECESSARY);
        } catch (SQLiteException e) {
            mLastErrorInfo = "创建或打开数据文件失败！";
            mDB = null;
            return false;
        }

        String sql;
        try {
            sql = "CREATE TABLE if not exists  fileInfo (file char("
                    + DB_MAX_FILE_LEN + "),  ver char(" + DB_MAX_VER_LEN
                    + "), len integer)";
            mDB.execSQL(sql);
            sql = "CREATE TABLE if not exists  noteInfo (id integer, serial integer, width integer, height integer,img blob)";
            mDB.execSQL(sql);

            sql = "SELECT * FROM fileInfo";
            mCursor = mDB.rawQuery(sql, null);
            if (mCursor == null || mCursor.getCount() == 0) {
                // 第一次需要写入
                sql = "INSERT INTO fileInfo (file, ver, len) VALUES ('"
                        + mFileName + "','" + mSrcFileVersion + "',"
                        + mSrcFileLen + ")";
                Log.e(TAG, "openAndCheck: first insert.");
                mDB.execSQL(sql);
            } else {
                // 需要check
                if (mCursor.moveToFirst()) {
                    String filename = mCursor.getString(mCursor
                            .getColumnIndex("file"));
                    String ver = mCursor.getString(mCursor
                            .getColumnIndex("ver"));
                    int len = mCursor.getInt(mCursor.getColumnIndex("len"));
                    if (!(checkStringEqual(filename, mFileName)
                            && checkStringEqual(ver, mSrcFileVersion) && len == mSrcFileLen)) {
                        // 不对应，时需要删除旧表创建新表
                        Log.e(TAG, "openAndCheck: delete old table.");
                        sql = "DROP TABLE fileInfo";
                        mDB.execSQL(sql);
                        sql = "DROP TABLE noteInfo";
                        mDB.execSQL(sql);
                        sql = "CREATE TABLE if not exists  fileInfo (file char("
                                + DB_MAX_FILE_LEN
                                + "),  ver char("
                                + DB_MAX_VER_LEN + "), len integer)";
                        mDB.execSQL(sql);
                        sql = "CREATE TABLE if not exists  noteInfo (id integer, serial integer, width integer, height integer,img blob)";
                        mDB.execSQL(sql);

                        sql = "INSERT INTO fileInfo (file, ver, len) VALUES ('"
                                + mFileName + "','" + mSrcFileVersion + "',"
                                + mSrcFileLen + ")";
                        mDB.execSQL(sql);
                    }
                }
            }
        } catch (SQLiteException e) {
            mLastErrorInfo = "创建或者读写数据库表失败！";
            mDB.close();
            mDB = null;
            return false;
        } finally {
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }
        }

        return true;
    }

    /**
     *
     * @param bmp 笔记图片
     * @param parcelIdx noteInfo数据表的serial字段
     * @return 是否保存成功。
     */
    public boolean saveParcel(Bitmap bmp, int parcelIdx) {
//        Log.e(TAG, "saveParcel: parcelId = " + parcelIdx + ", mNoteId = " + mNoteId);
        if (mCursor == null || mDB == null) {
            Log.e(Note.TAG, "saveParcel -1-, mCursor = " + mCursor + ", mDb = " + mDB);
            return false;
        }
        if (!(parcelIdx >= 0 && parcelIdx < mCursor.getCount())) {
            Log.e(Note.TAG, "saveParcel -2-");
            return false;
        }

        String sql = "UPDATE noteInfo set img = ? " + " WHERE id =" + mNoteId
                + " AND serial =" + parcelIdx;
        ByteArrayOutputStream stream = null;
        try {
            stream = new ByteArrayOutputStream();
            bmp.compress(CompressFormat.PNG, 50, stream);// 压缩为PNG格式,100表示跟原图大小一样
            Object[] args = new Object[]{stream.toByteArray()};
            Log.e(TAG, "saveParcel: parcelId = " + parcelIdx + ", length = " + stream.size()
                    + ", mNoteId = " + mNoteId);
            stream.close();
            mDB.execSQL(sql, args);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (stream != null){
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    /**
     * 在saveParcel()之后必须要调用reQueryAfterDbUpdate()，否则Cursor不是最新的。
     */
    public void reQueryAfterDbUpdate() {
        Log.e(TAG, "reQueryAfterDbUpdate: ");
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        String sql = "SELECT * FROM noteInfo where id = " + mNoteId;
        Log.e(TAG, "reQueryAfterDbUpdate: mNoteId = " + mNoteId);
        if (mDB != null) {
            mCursor = mDB.rawQuery(sql, null);
        }
    }

    public byte[] getParcelBytes(int parcelIdx) {
//        Log.e(TAG, "getParcelBytes() : parcelIdx = " + parcelIdx + ", mNoteId = " + mNoteId);
        byte[] bytes = null;

        if (mCursor == null) {
            Log.e(Note.TAG, "getPageBytes -1-");
            return null;
        }

        if (!(parcelIdx >= 0 && parcelIdx < mCursor.getCount())) {
            Log.e(Note.TAG, "getPageBytes -2-, parcelIdx="+parcelIdx+"; mCursor.getCount()="+mCursor.getCount());
            return null;
        }

        if (mCursor.moveToPosition(parcelIdx)) {
            try {
//                Log.e(TAG, "getParcelBytes: mCursor noteId = " + mCursor.getInt(mCursor.getColumnIndex("id")));
                bytes = mCursor.getBlob(mCursor.getColumnIndex("img"));
            } catch (Exception e) {
                e.printStackTrace();
//                Log.e(TAG, "getParcelBytes: bytes = null.", e);
            }
        }
        int length = bytes == null ? 0 : bytes.length;
//        Log.e(TAG, "getParcelBytes: bytes = " + length);
        return bytes;
    }

    public boolean deleteNote() {
        Log.e(TAG, "deleteNote: mNoteId = " + mNoteId);
        if (mDB == null) {
            return false;
        }
        String sql = "UPDATE noteInfo set img = null " + " WHERE id ="
                + mNoteId;
        try {
            mDB.execSQL(sql);
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }
            sql = "SELECT * FROM noteInfo where id = " + mNoteId;
            mCursor = mDB.rawQuery(sql, null);
        } catch (SQLiteException e) {
            return false;
        }

        return true;
    }

    public int setNoteInfo(int noteId, int parcelCount) {
        Log.e(TAG, "setNoteInfo: thread name = " + Thread.currentThread().getName());
        Log.e(TAG, "setNoteInfo() called with: noteId = " + noteId + ", parcelNum = " + parcelCount + "");
        mNoteId = noteId;
        mParcelNum = parcelCount;
        String sql = "SELECT * FROM noteInfo where id = " + mNoteId;
        if (mDB == null) {
            return -1;
        }

        try {
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }

            mCursor = mDB.rawQuery(sql, null);
            int count = mCursor.getCount();
            Log.e(TAG, "setNoteInfo: count = " + count + ", mParcelNum = " + mParcelNum);

            //新的处理逻辑，by oubin, 20171110.
            if (count < mParcelNum){
                //TODO：应使用事物操作，批量插入，提升效率。
                for (int i = count; i < mParcelNum; i++) {
                    sql = "INSERT INTO noteInfo (id, serial) VALUES ("
                            + mNoteId + "," + i + ")";
                    mDB.execSQL(sql);
                }

                mCursor.close();
                mCursor = null;

                sql = "SELECT * FROM noteInfo where id = " + mNoteId;
                mCursor = mDB.rawQuery(sql, null);

                if (mParcelNum != mCursor.getCount()) {
                    Log.e(Note.TAG, "setPagesInfo -1-, count = " + mCursor.getCount());
                    mCursor.close();
                    mCursor = null;
                    return -1;
                }
            }else {
                mParcelNum = count;
            }

//            if (count != mParcelNum) {
//                mCursor.close();
//                mCursor = null;
//                if (count != 0) {
//                    Log.e(TAG, "setNoteInfo: delete mNoteId = " + mNoteId);
//                    sql = "DELETE FROM noteInfo where id = " + mNoteId;
//                    mDB.execSQL(sql);
//                }
//
//				/* 插入每一项 */
//                for (int i = 0; i < mParcelNum; i++) {
//                    sql = "INSERT INTO noteInfo (id, serial) VALUES ("
//                            + mNoteId + "," + i + ")";
//                    mDB.execSQL(sql);
//                }
//
//                sql = "SELECT * FROM noteInfo where id = " + mNoteId;
//                mCursor = mDB.rawQuery(sql, null);
//
//                if (mParcelNum != mCursor.getCount()) {
//                    Log.e(Note.TAG, "setPagesInfo -1-, count =" + mCursor.getCount());
//                    mCursor.close();
//                    return false;
//                }
//            }
        } catch (SQLiteException e) {
            return -1;
        }

        return mParcelNum;
    }

    private boolean checkStringEqual(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        return str1 != null && str2 != null && str1.compareTo(str2) == 0;

    }

}
