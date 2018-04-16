package com.readboy.mathproblem.video.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;

/**
 * 数据库管理
 * @author guh
 *
 */
public class DatabaseProxy {
	
	public static final int DB_MD5_SIZE = 64;
	public static final int DB_DATA_MAX_SIZE = 2048;
	
	private SQLiteDatabase mDb = null; // 数据库
	private static final String MD_PATH = "Android/data/com.readboy.mathproblem.video.dreamplayer/database";
	private static final String DB_NAME = "data";
	private static final String TABLE_NAME = "data";
	
	/**
	 * 一条记录的相关信息
	 * @author guh
	 *
	 */
	public class DataItem {
		public String mPathUrl = null;
		public long mPosition = 0;
		public long mDuration = 0;
		/**   0: 没有播放, 1: 播放过 */
		public int mPlay = 0;
		/**   0: 本地地址， 1： 网络地址*/
		public int isUrl = 0;
		
		public long mTime = 0;
		
		public DataItem() {

		}

		public void printf() {
			Log.i("", " DataItem mPathUrl: " + mPathUrl + ", mPosition: " + mPosition + ", mDuration: " + mDuration 
					+ ", mPlay: " + mPlay + ", isUrl: " + isUrl + ", mTime: " + mTime);
		} 
	}

	
	// 构造方法
	private DatabaseProxy() {
		// 数据库文件夹及文件名
		String databaseFilePath = Environment.getExternalStorageDirectory().getPath();
		if (databaseFilePath.charAt(databaseFilePath.length() - 1) == File.separatorChar) {
			databaseFilePath += MD_PATH;
		} else {
			databaseFilePath += File.separator + MD_PATH;
		}
		String databaseFileName = DB_NAME + ".db";
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
		createTable();
	}
	
	/** 插入一条信息,成功返回0,失败返回(-1) */
	private int insert(String pathUrl, long position, long duration, int play, int isurl) {
		if (mDb == null) {
			return (-1);
		}
		ContentValues contentValues = new ContentValues();
		contentValues.put("pathUrl", pathUrl);
		contentValues.put("position", position);
		contentValues.put("duration", duration);
		contentValues.put("play", play);
		contentValues.put("isurl", isurl);
		long curtime = System.currentTimeMillis();
		contentValues.put("time", curtime);
		long rowId = mDb.insert(TABLE_NAME, TABLE_NAME, contentValues);
		if (rowId == (-1)) { // 插入失败,创建表再重试
			createTable();
			rowId = mDb.insert(TABLE_NAME, TABLE_NAME, contentValues);
			if(rowId == (-1)) {
				return (-1);
			}
		}
		return 0;
	}
	
	
	/** 插入一条信息,成功返回0,失败返回(-1) */
	private int insert(ContentValues contentValues) {
		if (mDb == null) {
			return (-1);
		}
		long rowId = mDb.insert(TABLE_NAME, TABLE_NAME, contentValues);
		if (rowId == (-1)) { // 插入失败,创建表再重试
			createTable();
			rowId = mDb.insert(TABLE_NAME, TABLE_NAME, contentValues);
			if(rowId == (-1)) {
				return (-1);
			}
		}
		return 0;
	}
	
	/** 删除一条信息,成功返回0,失败返回(-1) */
	private int delete(String pathUrl) {
		if (mDb == null) {
			return (-1);
		}
		if(1 != mDb.delete(TABLE_NAME, "pathUrl = \"" + pathUrl + "\"", null)) {
			return (-1);
		}
		return 0;
	}
	
	/** 删除多条信息,成功返回0,失败返回(-1) */
	private int deletes(String pathUrl) {
		if (mDb == null) {
			return (-1);
		}
		if(0 == mDb.delete(TABLE_NAME, "pathUrl in (" + pathUrl + ")", null)) {
			return (-1);
		}
		return 0;
	}

	/** 更新一条信息 , 没有这条信息则插入 */
	private int update(String pathUrl, long position, long duration, int play, int isurl) {
		if (mDb == null) {
			return (-1);
		}
		ContentValues contentValues = new ContentValues();
		contentValues.put("pathUrl", pathUrl);
		contentValues.put("position", position);
		contentValues.put("duration", duration);
		contentValues.put("play", play);
		contentValues.put("isurl", isurl);
		long curtime = System.currentTimeMillis();
		contentValues.put("time", curtime);
		int count = mDb.update(TABLE_NAME, contentValues, "pathUrl = \"" + pathUrl + "\"", null);
		if (count == 0 || count ==-1) {
			count = insert(contentValues);
		}
		return count;
	}
	
	/** 信息查询 */
	private Cursor query() {
		if (mDb == null) {
			return null;
		}
		return mDb.rawQuery("SELECT * FROM " + TABLE_NAME, null);
	}
	
	/** 信息查询：单个pathUrl */
	private DataItem query(String pathUrl) {
		DataItem item = new DataItem();
		if (mDb == null) {
			return item;
		}
		Cursor mCursor = mDb.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE pathUrl = ?", new String[]{pathUrl});
		if (mCursor != null) {
			int count = mCursor.getCount();
			if (count > 0) {
				mCursor.moveToFirst();
				item.mPathUrl = mCursor.getString(mCursor.getColumnIndex("pathUrl"));
				item.mPosition = mCursor.getLong(mCursor.getColumnIndex("position"));
				item.mDuration = mCursor.getLong(mCursor.getColumnIndex("duration"));
				item.mPlay = mCursor.getInt(mCursor.getColumnIndex("play"));
				item.isUrl = mCursor.getInt(mCursor.getColumnIndex("isurl"));
				item.mTime = mCursor.getLong(mCursor.getColumnIndex("time"));
			}
			mCursor.close();
		}
		return item;
	}
	
	
	/** 信息查询：ptdsn 为 ptdsn 的多条记录  */
	private ArrayList<DataItem> queryInfos(String pathUrl) {
		ArrayList<DataItem> items = new ArrayList<DataItem>();
		if (mDb == null) {
			return items;
		}
		Cursor mCursor = mDb.rawQuery("SELECT * FROM " + TABLE_NAME + " pathUrl WHERE pathUrl = ?  ORDER BY time DESC", new String[]{pathUrl});
		if (mCursor != null) {
			int count = mCursor.getCount();
			if (count > 0) {
				for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
					DataItem item = new DataItem();
					if (mCursor.moveToFirst()) {
						item.mPathUrl = mCursor.getString(mCursor.getColumnIndex("pathUrl"));
						item.mPosition = mCursor.getLong(mCursor.getColumnIndex("position"));
						item.mDuration = mCursor.getLong(mCursor.getColumnIndex("duration"));
						item.mPlay = mCursor.getInt(mCursor.getColumnIndex("play"));
						item.isUrl = mCursor.getInt(mCursor.getColumnIndex("isurl"));
						item.mTime = mCursor.getLong(mCursor.getColumnIndex("time"));
					}
					items.add(item);
				}
			}
			mCursor.close();
		}
		return items;
	}
	
	/**  清除信息    */
	private void clear() {
		if (mDb != null) {
			mDb.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
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
	private void createTable() {
		if (mDb != null) {
			String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(pathUrl char("+DB_DATA_MAX_SIZE+"), position long("+DB_MD5_SIZE+"), " + "duration long("+DB_MD5_SIZE+"), "
					+ "play int, isurl integer, time long)";
			mDb.execSQL(sql);
		}
	}
	
	
	/**   数据库插入数据  */
	public static void insertItem(String pathUrl, long position, long duration, int play) {
		DatabaseProxy dm = new DatabaseProxy();
		dm.insert(pathUrl, position, duration, play, 0);
		dm.closeDatabase();
	}
	
	/** 数据库删除数据  */
	public static void deleteItem(String pathUrl) {
		DatabaseProxy dm = new DatabaseProxy();
		dm.delete(pathUrl);
		dm.closeDatabase();
	}
	
	/**  数据库删除数据   */
	public static void deleteItems(String pathUrl) {
		DatabaseProxy dm = new DatabaseProxy();
		dm.deletes(pathUrl);
		dm.closeDatabase();
	}
	
	/** 数据库查询数据，返回cursor会出问题，因为数据库被关了，要改成返回ArrayList<String>，因为此方法未被调用到，未改。 */
	public static Cursor queryItem() {
		DatabaseProxy dm = new DatabaseProxy();
		Cursor cursor = dm.query();
		dm.closeDatabase();
		return cursor;
	}
	
	/** 数据库查询数据：pathUrl  */
	public static DataItem queryItem(String pathUrl) {
		DatabaseProxy dm = new DatabaseProxy();
		DataItem item = dm.query(pathUrl);
		dm.closeDatabase();
		return item;
	}
	
	/** 数据库查询数据：pathUrl  */
	public static ArrayList<DataItem> queryItems(String pathUrl) {
		DatabaseProxy dm = new DatabaseProxy();
		ArrayList<DataItem> list = dm.queryInfos(pathUrl);
		dm.closeDatabase();
		return list;
	}
	
	/** 数据库更新数据  */
	public static int updateItem(String pathUrl, long position, long duration, int play) {
		DatabaseProxy dm = new DatabaseProxy();
		int rows = dm.update(pathUrl, position, duration, play, 0);
		if (rows == -1 || rows == 0) {
		}
		dm.closeDatabase();
		return rows;
	}
	
	/** 数据库清空数据  */
	public static void clearItem() {
		DatabaseProxy dm = new DatabaseProxy();
		dm.clear();
		dm.closeDatabase();
	}
	
	public static void clearItemNotExist() {
		new Thread(){

			@Override
			public void run() {
				Log.i("DatabaseProxy", " clearItemNotExist run to begin!");
				DatabaseProxy dm = new DatabaseProxy();
				Cursor mCursor = dm.query();
				if (mCursor != null) {
					int count = mCursor.getCount();
					if (count > 0) {
						long curtime = System.currentTimeMillis();
						curtime -= 30 * 24 * 60 * 60 * 1000;
						for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
								int isurl = mCursor.getInt(mCursor.getColumnIndex("isurl"));
								String mPathUrl = mCursor.getString(mCursor.getColumnIndex("pathUrl"));
								if (isurl==0) {
									File file = new File(mPathUrl);
									if (!file.exists()) {
										dm.delete(mPathUrl);
									}
								} else {
									long time = mCursor.getLong(mCursor.getColumnIndex("time"));
									if (curtime > time) {
										dm.delete(mPathUrl);
									}
								}
						}
					}
					
					mCursor.close();
				}
				dm.closeDatabase();
				Log.i("DatabaseProxy", " clearItemNotExist run to end !");
			}
			
		}.start();
	}
}
