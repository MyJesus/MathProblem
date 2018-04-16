package com.readboy.mathproblem.video.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * 数据库管理
 * @author guh
 *
 */
public class VideoInfoDatabaseProxy {
	
	public static final int TYPE_ADDRESS = 0x0;
	public static final int TYPE_URL = 0x1;
	public static final int TYPE_MICRO = 0x2;
	
	public static final int DB_MD5_SIZE = 64;
	public static final int DB_DATA_MAX_SIZE = 2048;
	public static String DEFAULT_DB_PATH = VideoDatabaseInfo.DEFAULT_DATABASE_PATH;
	public static String DEFSULT_DB_NAME = VideoDatabaseInfo.DBNAME;
	
	private SQLiteDatabase mDb = null; // 数据库
	private String[] mTableNames = {"video", "video", "microVideo"};
	
	/**
	 * 一条记录的相关信息
	 * @author guh
	 *
	 */
	public class VideoInfoItem {
		/** 根据这个找到视频播放信息，可以根据地址类：视频本地地址，网络URL。  根据ID类：知识点微视频的vid等 */
		public String mDependency = null;
		
		/**  依赖的类别，0x0：本地地址，0x1： 网络地址， 0x2： vid  */
		public int mType = 0;
		
		/**   0: 没有播放, 1: 播放过 */
		public int mPlay = 0;
		
		public long mPosition = 0;
		public long mDuration = 0;
		public long mSize = 0;
		
		public String mCachePath = null;
		public String mCacheName = null;
		
		public long mTime = 0;
		
		
		public VideoInfoItem() {

		}

		public void printf() {
			Log.i("", "DataItem mDependency: " + mDependency + ", mType: " + mType + ", mPlay: " + mPlay + ", mPosition: " + mPosition 
					+ ", mDuration: " + mDuration + ", mSize: " + mSize+ ", mCachePath: " 
					+ mCachePath + ", mCacheName: " + mCacheName + ", mTime: " + mTime);
		} 
	}

	
	/**
	 * 
	 * @param datapath 数据库路径
	 * @param dataname 数据库名称
	 * @param type 表的类型
	 */
	private VideoInfoDatabaseProxy(String datapath, String dataname, int type) {
		// 数据库文件夹及文件名
		String databaseFilePath = Environment.getExternalStorageDirectory().getPath();
		if (databaseFilePath.charAt(databaseFilePath.length() - 1) == File.separatorChar) {
			databaseFilePath += datapath;
		} else {
			databaseFilePath += File.separator + datapath;
		}
		String databaseFileName = dataname + ".db";
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
		createTable(type);
	}
	
	/** 插入一条信息,成功返回0,失败返回(-1) */
	private int insert(String dependency, int type, int play, long position, long duration, long size, 
			String cachepath, String cachename) {
		if (mDb == null) {
			return (-1);
		}
		ContentValues contentValues = new ContentValues();
		
		contentValues.put("dependency", dependency);
		contentValues.put("type", type);
		contentValues.put("play", play);
		contentValues.put("position", position);
		contentValues.put("duration", duration);
		contentValues.put("size", size);
		contentValues.put("cachePath", cachepath);
		contentValues.put("cacheName", cachename);
		
		long curtime = System.currentTimeMillis();
		contentValues.put("time", curtime);
		long rowId = mDb.insert(mTableNames[type], mTableNames[type], contentValues);
		if (rowId == (-1)) { // 插入失败,创建表再重试
			createTable(type);
			rowId = mDb.insert(mTableNames[type], mTableNames[type], contentValues);
			if(rowId == (-1)) {
				return (-1);
			}
		}
		return 0;
	}
	
	
	/** 插入一条信息,成功返回0,失败返回(-1) */
	private int insert(int type, ContentValues contentValues) {
		if (mDb == null) {
			return (-1);
		}
		long rowId = mDb.insert(mTableNames[type], mTableNames[type], contentValues);
		if (rowId == (-1)) { // 插入失败,创建表再重试
			createTable(type);
			rowId = mDb.insert(mTableNames[type], mTableNames[type], contentValues);
			if(rowId == (-1)) {
				return (-1);
			}
		}
		return 0;
	}
	
	/** 删除一条信息,成功返回0,失败返回(-1) */
	private int delete(String dependency, int type) {
		if (mDb == null) {
			return (-1);
		}
		if(1 != mDb.delete(mTableNames[type], "dependency = \"" + dependency + "\"", null)) {
			return (-1);
		}
		return 0;
	}
	
	/** 删除多条信息,成功返回0,失败返回(-1) */
	private int deletes(String dependency, int type) {
		if (mDb == null) {
			return (-1);
		}
		if(0 == mDb.delete(mTableNames[type], "dependency in (" + dependency + ")", null)) {
			return (-1);
		}
		return 0;
	}

	/** 更新一条信息 , 没有这条信息则插入 */
	private int update(String dependency, int type, int play, long position, long duration, long size,
			String cachepath, String cachename) {
		if (mDb == null) {
			return (-1);
		}
		ContentValues contentValues = new ContentValues();
		
		contentValues.put("dependency", dependency);
		contentValues.put("type", type);
		contentValues.put("play", play);
		contentValues.put("position", position);
		contentValues.put("duration", duration);
		contentValues.put("size", size);
		contentValues.put("cachePath", cachepath);
		contentValues.put("cacheName", cachename);
		
		long curtime = System.currentTimeMillis();
		contentValues.put("time", curtime);
		int count = mDb.update(mTableNames[type], contentValues, "dependency = \"" + dependency + "\"", null);
		if (count == 0 || count ==-1) {
			count = insert(type, contentValues);
		}
		return count;
	}
	
	/** 信息查询 */
	private Cursor query(int type) {
		if (mDb == null) {
			return null;
		}
		return mDb.rawQuery("SELECT * FROM " + mTableNames[type], null);
	}
	
	/** 信息查询：单个pathUrl */
	private VideoInfoItem query(String dependency, int type) {
		VideoInfoItem item = new VideoInfoItem();
		if (mDb == null) {
			return item;
		}
		Cursor mCursor = mDb.rawQuery("SELECT * FROM " + mTableNames[type] + " WHERE dependency = ?", new String[]{dependency});
		if (mCursor != null) {
			int count = mCursor.getCount();
			if (count > 0) {
				mCursor.moveToFirst();
				item.mDependency = mCursor.getString(mCursor.getColumnIndex("dependency"));
				item.mType = mCursor.getInt(mCursor.getColumnIndex("type"));
				item.mPlay = mCursor.getInt(mCursor.getColumnIndex("play"));
				item.mPosition = mCursor.getLong(mCursor.getColumnIndex("position"));
				item.mDuration = mCursor.getLong(mCursor.getColumnIndex("duration"));
				item.mSize = mCursor.getLong(mCursor.getColumnIndex("size"));
				item.mCachePath = mCursor.getString(mCursor.getColumnIndex("cachePath"));
				item.mCacheName = mCursor.getString(mCursor.getColumnIndex("cacheName"));
				item.mTime = mCursor.getLong(mCursor.getColumnIndex("time"));
			}
			mCursor.close();
		}
		return item;
	}
	
	
	/** 信息查询：dependency 为 dependency 的多条记录  */
	private ArrayList<VideoInfoItem> queryInfos(String dependency, int type) {
		ArrayList<VideoInfoItem> items = new ArrayList<VideoInfoItem>();
		if (mDb == null) {
			return items;
		}
		Cursor mCursor = mDb.rawQuery("SELECT * FROM " + mTableNames[type] + " dependency WHERE dependency = ?  ORDER BY time DESC", new String[]{dependency});
		if (mCursor != null) {
			int count = mCursor.getCount();
			if (count > 0) {
				for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
					VideoInfoItem item = new VideoInfoItem();
					if (mCursor.moveToFirst()) {
						item.mDependency = mCursor.getString(mCursor.getColumnIndex("dependency"));
						item.mType = mCursor.getInt(mCursor.getColumnIndex("type"));
						item.mPlay = mCursor.getInt(mCursor.getColumnIndex("play"));
						item.mPosition = mCursor.getLong(mCursor.getColumnIndex("position"));
						item.mDuration = mCursor.getLong(mCursor.getColumnIndex("duration"));
						item.mSize = mCursor.getLong(mCursor.getColumnIndex("size"));
						item.mCachePath = mCursor.getString(mCursor.getColumnIndex("cachePath"));
						item.mCacheName = mCursor.getString(mCursor.getColumnIndex("cacheName"));
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
	private void clear(int type) {
		if (mDb != null) {
			mDb.execSQL("DROP TABLE IF EXISTS " + mTableNames[type]);
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
	private void createTable(int type) {
		if (mDb != null) {
			String sql = "CREATE TABLE IF NOT EXISTS " + mTableNames[type] + "(dependency char("+DB_DATA_MAX_SIZE
					+"), type integer, play integer, position long("+DB_MD5_SIZE+"), duration long("+DB_MD5_SIZE
					+"), size long("+DB_MD5_SIZE+"), cachePath varchar("+DB_DATA_MAX_SIZE+"), cacheName varchar("+DB_DATA_MAX_SIZE
					+"), time long)";
			mDb.execSQL(sql);
		}
	}
	
	
	/**   数据库插入数据  */
	public static void insertItem(String datapath, String dataname, String dependency, int type, int play, long position, long duration, 
			long size, String cachepath, String cachename) {
		VideoInfoDatabaseProxy dm = new VideoInfoDatabaseProxy(datapath, dataname, type);
		dm.insert(dependency, type, play, position, duration, size, cachepath, cachename);
		dm.closeDatabase();
	}
	
	/** 数据库删除数据  */
	public static void deleteItem(String datapath, String dataname, String dependency, int type) {
		VideoInfoDatabaseProxy dm = new VideoInfoDatabaseProxy(datapath, dataname, type);
		dm.delete(dependency, type);
		dm.closeDatabase();
	}
	
	/**  数据库删除数据   */
	public static void deleteItems(String datapath, String dataname, String dependency, int type) {
		VideoInfoDatabaseProxy dm = new VideoInfoDatabaseProxy(datapath, dataname, type);
		dm.deletes(dependency, type);
		dm.closeDatabase();
	}
	
	/** 数据库查询数据：dependency  */
	public static VideoInfoItem queryItem(String datapath, String dataname, String dependency, int type) {
		VideoInfoDatabaseProxy dm = new VideoInfoDatabaseProxy(datapath, dataname, type);
		VideoInfoItem item = dm.query(dependency, type);
		dm.closeDatabase();
		return item;
	}
	
	/** 数据库查询数据：dependency  */
	public static ArrayList<VideoInfoItem> queryItems(String datapath, String dataname, String dependency, int type) {
		VideoInfoDatabaseProxy dm = new VideoInfoDatabaseProxy(datapath, dataname, type);
		ArrayList<VideoInfoItem> list = dm.queryInfos(dependency, type);
		dm.closeDatabase();
		return list;
	}
	
	/** 数据库更新数据  */
	public static int updateItem(String datapath, String dataname, String dependency, int type, int play, long position, long duration, 
			long size, String cachepath, String cachename) {
		VideoInfoDatabaseProxy dm = new VideoInfoDatabaseProxy(datapath, dataname, type);
		int rows = dm.update(dependency, type, play, position, duration, size, cachepath, cachename);
		if (rows == -1 || rows == 0) {
			
		}
		dm.closeDatabase();
		return rows;
	}
	
	/** 数据库清空数据  */
	public static void clearItem(String datapath, String dataname, int type) {
		VideoInfoDatabaseProxy dm = new VideoInfoDatabaseProxy(datapath, dataname, type);
		dm.clear(type);
		dm.closeDatabase();
	}
	
	public static void clearItemNotExist(final String datapath, final String dataname, final int type) {
		new Thread(){
			@Override
			public void run() {
				Log.i("DatabaseProxy", " clearItemNotExist run to begin!");
				VideoInfoDatabaseProxy dm = new VideoInfoDatabaseProxy(datapath, dataname, type);
				Cursor mCursor = dm.query(type);
				if (mCursor != null) {
					int count = mCursor.getCount();
					if (count > 0) {
						long curtime = System.currentTimeMillis();
						curtime -= 30 * 24 * 60 * 60 * 1000;
						for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
								int dataType = mCursor.getInt(mCursor.getColumnIndex("type"));
								String dependency = mCursor.getString(mCursor.getColumnIndex("dependency"));
								if (dataType==0) {
									File file = new File(dependency);
									if (!file.exists()) {
										dm.delete(dependency, type);
									}
								} else {
									long time = mCursor.getLong(mCursor.getColumnIndex("time"));
									if (curtime > time) {
										dm.delete(dependency, dataType);
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
