package com.readboy.video.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

/**
 * Created by guh on 2017/4/14.
 */

public class DownloadInfoProxy {

    public static final int DB_MD5_SIZE = 64;
    public static final int DB_DATA_MAX_SIZE = 2048;
    public static String DEFAULT_DB_PATH = VideoDatabaseInfo.DBPATH;
    public static String DEFSULT_DB_NAME = "downloadinfo";
    private SQLiteDatabase mDb = null; // 数据库
    private String[] mTableNames = {"video", "video", "microVideo"};


    /**
     *
     * @param dataPath 数据库路径
     * @param dataName 数据库名称
     * @param table
     */
    private DownloadInfoProxy(String dataPath, String dataName, String table) {
        // 数据库文件夹及文件名
        String databaseFilePath = Environment.getExternalStorageDirectory().getPath();
        if (databaseFilePath.charAt(databaseFilePath.length() - 1) == File.separatorChar) {
            databaseFilePath += dataPath;
        } else {
            databaseFilePath += File.separator + dataPath;
        }
        String databaseFileName = dataName + ".db";
        // 没文件夹创建文件夹
        File file = new File(databaseFilePath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                if (!file.mkdirs()) {
                    return ;
                }
            }
        }
        // 创建数据库文件
        String fullPathName = databaseFilePath + File.separator + databaseFileName;
        mDb = SQLiteDatabase.openOrCreateDatabase(fullPathName, null);
        // 创建表
        createTable(table);
    }

    /** 插入一条信息,成功返回0,失败返回(-1) */
    private int insert(String table, int id, long beginposition, long endposition, long size, long beginBit, long endBit) {
        if (mDb == null) {
            return (-1);
        }
        ContentValues contentValues = new ContentValues();

        contentValues.put("id", id);
        contentValues.put("beginPosition", beginposition);
        contentValues.put("endPosition", endposition);
        contentValues.put("size", size);
        contentValues.put("beginBit", beginBit);
        contentValues.put("endBit", endBit);

        long curtime = System.currentTimeMillis();
        contentValues.put("time", curtime);
        long rowId = mDb.insert(table, "id", contentValues);
        if (rowId == (-1)) { // 插入失败,创建表再重试
            createTable(table);
            rowId = mDb.insert(table, "id", contentValues);
            if(rowId == (-1)) {
                return (-1);
            }
        }
        return 0;
    }


    /** 插入一条信息,成功返回0,失败返回(-1) */
    private int insert(String table, ContentValues contentValues) {
        if (mDb == null) {
            return (-1);
        }
        long rowId = mDb.insert(table, "null", contentValues);
        if (rowId == (-1)) { // 插入失败,创建表再重试
            createTable(table);
            rowId = mDb.insert(table, "null", contentValues);
            if(rowId == (-1)) {
                return (-1);
            }
        }
        return 0;
    }

    /** 删除一条信息,成功返回0,失败返回(-1) */
    private int delete(String table, int id) {
        if (mDb == null) {
            return (-1);
        }
        if(1 != mDb.delete(table, "id = \"" + id + "\"", null)) {
            return (-1);
        }
        return 0;
    }

    /** 删除多条信息,成功返回0,失败返回(-1) */
    private int deletes(String table, int id) {
        if (mDb == null) {
            return (-1);
        }
        if(0 == mDb.delete(table, "id in (" + id + ")", null)) {
            return (-1);
        }
        return 0;
    }

    /** 更新一条信息 , 没有这条信息则插入 */
    private int update(String table, int id, long beginposition, long endposition, long size,
                       long begin, long end) {
        if (mDb == null) {
            return (-1);
        }
        ContentValues contentValues = new ContentValues();

        contentValues.put("id", id);;
        contentValues.put("beginPosition", beginposition);
        contentValues.put("endPosition", endposition);
        contentValues.put("size", size);
        contentValues.put("beginBit", begin);
        contentValues.put("endBit", end);

        long curtime = System.currentTimeMillis();
        contentValues.put("time", curtime);
        int count = mDb.update(table, contentValues, "id = \"" + id + "\"", null);
        if (count == 0 || count ==-1) {
            count = insert(table, contentValues);
        }
        return count;
    }

    /** 信息查询 */
    private Cursor query(String table) {
        if (mDb == null) {
            return null;
        }
        return mDb.rawQuery("SELECT * FROM " + table, null);
    }

    /** 信息查询：单个pathUrl */
    private DataLoadInfoItem query(String table, String id) {
        DataLoadInfoItem item = new DataLoadInfoItem();
        if (mDb == null) {
            return item;
        }
        Cursor mCursor = mDb.rawQuery("SELECT * FROM " + table + " WHERE id = ?", new String[]{id});
        if (mCursor != null) {
            int count = mCursor.getCount();
            if (count > 0) {
                mCursor.moveToFirst();
                item.mId = mCursor.getInt(mCursor.getColumnIndex("id"));
                item.mBeginPosition = mCursor.getLong(mCursor.getColumnIndex("beginPosition"));
                item.mEndPosition = mCursor.getLong(mCursor.getColumnIndex("endPosition"));
                item.mSize = mCursor.getLong(mCursor.getColumnIndex("size"));
                item.mBeginBit = mCursor.getLong(mCursor.getColumnIndex("beginBit"));
                item.mEndBit = mCursor.getLong(mCursor.getColumnIndex("endBit"));
                item.mTime = mCursor.getLong(mCursor.getColumnIndex("time"));
            }
            mCursor.close();
        }
        return item;
    }


    /** 信息查询：dependency 为 dependency 的多条记录  */
    private ArrayList<DataLoadInfoItem> queryInfos(String table) {
        ArrayList<DataLoadInfoItem> items = new ArrayList<DataLoadInfoItem>();
        if (mDb == null) {
            return items;
        }
        Cursor mCursor = mDb.rawQuery("SELECT * FROM " + table + " WHERE id >= ?", new String[]{"0"});
        if (mCursor != null) {
            int count = mCursor.getCount();
            if (count > 0) {
                Log.i("DownloadInfoProxy", " queryInfos count: "+count);
                for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                    DataLoadInfoItem item = new DataLoadInfoItem();
                    item.mId = mCursor.getInt(mCursor.getColumnIndex("id"));
                    item.mBeginPosition = mCursor.getLong(mCursor.getColumnIndex("beginPosition"));
                    item.mEndPosition = mCursor.getLong(mCursor.getColumnIndex("endPosition"));
                    item.mSize = mCursor.getLong(mCursor.getColumnIndex("size"));
                    item.mBeginBit = mCursor.getLong(mCursor.getColumnIndex("beginBit"));
                    item.mEndBit = mCursor.getLong(mCursor.getColumnIndex("endBit"));
                    item.mTime = mCursor.getLong(mCursor.getColumnIndex("time"));
                    items.add(item);
                }
            }
            mCursor.close();
        }
        return items;
    }

    /**  清除信息    */
    private void clear(String table) {
        if (mDb != null) {
            mDb.execSQL("DROP TABLE \"" + table+"\"");
        }
    }

    /**  关闭数据库  */
    private void closeDatabase() {
        if (mDb != null) {
            mDb.close();
            mDb = null;
        }
    }

    /**  创建表   */
    private void createTable(String table) {
        if (mDb != null) {
            String sql = "CREATE TABLE IF NOT EXISTS " + table + "(id integer primary key, beginPosition long("+DB_MD5_SIZE+"), endPosition long("+DB_MD5_SIZE
                    +"), size long("+DB_MD5_SIZE+"), beginBit long("+DB_MD5_SIZE+"), endBit long("+DB_MD5_SIZE + "), time long)";
            mDb.execSQL(sql);
        }
    }


    /**   数据库插入数据
     *  */
    public static void insertItem(String datapath, String dataname, String table, int id, long beginposition, long endposition, long size, long beginbit, long endbit) {
        DownloadInfoProxy dm = new DownloadInfoProxy(datapath, dataname, table);
        dm.insert(table, id, beginposition, endposition, size, beginbit, endbit);
        dm.closeDatabase();
    }

    /** 数据库删除数据  */
    public static void deleteItem(String datapath, String dataname, String table, int id) {
        DownloadInfoProxy dm = new DownloadInfoProxy(datapath, dataname, table);
        dm.delete(table, id);
        dm.closeDatabase();
    }

    /**  数据库删除数据   */
    public static void deleteItems(String datapath, String dataname, String table, int id) {
        DownloadInfoProxy dm = new DownloadInfoProxy(datapath, dataname, table);
        dm.deletes(table, id);
        dm.closeDatabase();
    }

    /** 数据库查询数据：dependency  */
    public static DataLoadInfoItem queryItem(String datapath, String dataname, String table) {
        DownloadInfoProxy dm = new DownloadInfoProxy(datapath, dataname, table);
        DataLoadInfoItem item = dm.query(table, null);
        dm.closeDatabase();
        return item;
    }

    /** 数据库查询数据：dependency  */
    public static ArrayList<DataLoadInfoItem> queryItems(String datapath, String dataname, String table) {
        DownloadInfoProxy dm = new DownloadInfoProxy(datapath, dataname, table);
        ArrayList<DataLoadInfoItem> list = dm.queryInfos(table);
        dm.closeDatabase();
        return list;
    }

    /** 数据库更新数据  */
    public static int updateItem(String datapath, String dataname, String table, int id, long beginposition, long endposition,
                                 long size, long beginbit, int endbit) {
        DownloadInfoProxy dm = new DownloadInfoProxy(datapath, dataname, table);
        int rows = dm.update(table, id, beginposition, endposition, size, beginbit, endbit);
        if (rows == -1 || rows == 0) {

        }
        dm.closeDatabase();
        return rows;
    }

    /** 数据库更新数据  */
    public static int updateItems(String datapath, String dataname, String table, List<DataLoadInfoItem> list) {
        int rows = -1;
        if (list.size() > 0) {
            ArrayList<DataLoadInfoItem> templsts = new ArrayList<>();
            templsts.add(list.get(0));
            for (DataLoadInfoItem item: list) {
                DataLoadInfoItem tempItem = templsts.get(templsts.size()-1);
                if (tempItem.mEndBit == item.mBeginBit) {
                    tempItem.mEndBit = item.mEndBit;
                } else if (item.mBeginBit > tempItem.mEndBit) {
                    templsts.add(item);
                }
            }
            DownloadInfoProxy dm = new DownloadInfoProxy(datapath, dataname, table);
//            for (DataLoadInfoItem item: list) {
//                rows = dm.delete(table, item.mId);
//                if (rows == -1 || rows == 0) {
//
//                }
//            }
            int size = list.size();
            for (int i = size; i < 0; i--) {
                DataLoadInfoItem item = list.get(i);
                rows = dm.delete(table, item.mId);
                if (rows == -1 || rows == 0) {

                }
            }

            for (DataLoadInfoItem item: templsts) {
                rows = dm.update(table, item.mId, item.mBeginPosition, item.mEndPosition, item.mSize, item.mBeginBit, item.mEndBit);
                item.printf();
                if (rows == -1 || rows == 0) {

                }
            }
            dm.closeDatabase();
        }
        return rows;
    }

    /** 数据库清空数据  */
    public static void clearItem(String dataPath, String dataName, String table) {
        DownloadInfoProxy dm = new DownloadInfoProxy(dataPath, dataName, table);
        dm.clear(table);
        dm.closeDatabase();
    }


    public static void sortDataInfo(final String datapath, final String dataname, final String table) {
        new Thread(){

            @Override
            public void run() {
                Log.i("DownloadInfoProxy", " sortDataInfo run to begin!");
                ArrayList<DataLoadInfoItem> items = queryItems(datapath, dataname, table);
                int count = items.size();
                if (count > 0) {
                    for (int i=count-1;  i>0; i--) {
                        DataLoadInfoItem item = items.get(i);
                        DataLoadInfoItem itpre = items.get(i-1);
                        item.printf();
                        itpre.printf();
                        if (item.mBeginBit < itpre.mEndBit) {
                            if (item.mEndBit > itpre.mEndBit) {
                                itpre.mEndBit = item.mEndBit;
                            }
                            items.remove(item);
                        }
                    }


                }
            }

        }.start();
    }

}
