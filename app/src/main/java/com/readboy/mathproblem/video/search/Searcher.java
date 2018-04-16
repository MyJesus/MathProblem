package com.readboy.mathproblem.video.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class Searcher extends AsyncTask<Void, List<File>, Void> {
	private static final String TAG = "Searcher";
	private static final boolean DEBUG = true;
	private static final boolean TEST = false;
	
	private static final int UPDATE_THRESHOLD = 3000;

	private String searchDir;
	private String suffixReg;
	private String mExterSdPath;
	private boolean isRecursive;
	private ArrayList<File> fileArray = new ArrayList<File>();
	private ArrayList<File> updateArray = null;//new ArrayList<File>();
	private long updateTime = 0;

	public Searcher(String dir, String suffix, boolean isRecursive, String exterSdPath) {
		if (TextUtils.isEmpty(dir)) {
			this.searchDir = File.separator;
		} else if (!dir.startsWith(File.separator)) {
			this.searchDir = File.separator+dir;
		} else {
			this.searchDir = dir;
		}
		mExterSdPath = exterSdPath;
		if(DEBUG) {
			Log.w(TAG, "dir=" + dir + ", searchDir=" + searchDir);
		}
		
		if (TextUtils.isEmpty(suffix) || ",".equals(suffix) || "*".equals(suffix)) {
			suffix = "";
		} else {
			suffix = suffix.trim().replace(",", "|");
			suffix = suffix.replace(".", "\\.");
			suffix = "(" + suffix + ")$";
		}
		this.suffixReg = ".+" + suffix;
		this.isRecursive = isRecursive;
		
	}

	@Override
	protected void onPreExecute() {
		if(DEBUG) {
			Log.w(TAG, "searchPath=" + searchDir);
		}
		if(DEBUG) {
			Log.w(TAG, "suffixReg=" + suffixReg);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Void doInBackground(Void... params) {
		String path = null;
		Log.i("Searcher", " doInBackground 00 path: " + path);
		Pattern p = Pattern.compile(suffixReg, Pattern.UNICODE_CASE|Pattern.CASE_INSENSITIVE);
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			
			path =  Environment.getExternalStorageDirectory().getAbsolutePath()+searchDir;
			if (DEBUG) {
				Log.w(TAG, " MEDIA_MOUNTED start search " + path);
			}
			updateTime = System.currentTimeMillis();
			
			if (doSearch(path, p, isRecursive)==-2){
				Log.e(TAG, "searcher is interrupted");
				return null;
			}
		}

//		if(MovieConfig.USED_CEDARX) {
//			path = "/mnt/extsd"+searchDir;
//		} else {
//			path = "/storage/extsd"+searchDir;
//		}
		path = "/storage/sdcard1/";
		if (mExterSdPath != null) {
			path = mExterSdPath;
		}
		Log.i("Searcher", " doInBackground path: " + path);
		if(new File(path).exists()){
			if(DEBUG) {
				Log.w(TAG, "start search " + path);
			}
			updateTime = System.currentTimeMillis();
			if (doSearch(path, p, isRecursive)==-2) {
				Log.e(TAG, "searcher is interrupted");
				return null;
			}
		}
		if (updateArray!=null && updateArray.size()>0) {
			publishProgress(updateArray);
			updateArray = null;
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(List<File>... progress) {
		fileArray.addAll(progress[0]);

		if(DEBUG) {
			Log.w(TAG, "onProgressUpdate: count=" + fileArray.size());
		}
	}

	@Override
	protected void onPostExecute(Void result) {
//		if(updateArray!=null && updateArray.size()>0){
//			fileArray.addAll(updateArray);
//			updateArray.clear();
//		}
//		updateArray = null;
		if(DEBUG) {
			Log.w(TAG, "onPostExecute: count=" + fileArray.size());
		}
	}
	
	@Override
	protected void onCancelled(Void result) {
		super.onCancelled(result);
		
//		if(updateArray!=null && updateArray.size()>0){
//			fileArray.addAll(updateArray);
//			updateArray.clear();
//		}
//		updateArray = null;
		if(DEBUG) {
			Log.w(TAG, "onCancelled: count=" + fileArray.size());
		}
    }   

	public int getTotalNum() {
		return fileArray.size();
	}

	public String getPathByIndex(int idx) {
		if (idx < 0 || idx >= fileArray.size()) {
			return null;
		}

		return fileArray.get(idx).getPath();
	}

	public ArrayList<File> getFiles() {
		return fileArray;
	}

	@SuppressWarnings("unchecked")
	private int doSearch(String searchPath, Pattern pattern, boolean isRecursive) {
		if (Thread.currentThread().isInterrupted()) {
			Log.e(TAG, "searchPath=" + searchPath + " is interrupted");
			return -2;
		}
		
		if(TEST) {
			Log.i(TAG, "check dir=" + searchPath);
		}

		File file = new File(searchPath);
		if (!file.exists() || !file.isDirectory() || !file.canRead()) {
			Log.e(TAG, "file.exists(): " + file.exists() + ", file.isDirectory(): "+file.isDirectory()+", file.canRead(): "+file.canRead());
			Log.e(TAG, "searchPath=" + searchPath + " is not existed or not a dir or not readable");
			return -1;
		}
		
		File nomedia = new File(searchPath + "/.nomedia");
		if(nomedia.exists()) {
			Log.e(TAG, "searchPath=" + searchPath + " has .nomedia file, do not search it!");
			return -1;
		}

		File[] files = new File(searchPath).listFiles();
		if (files == null) {
			Log.e(TAG, "searchPath=" + searchPath + " list files error");
			return -1;
		}

		String path = null;
		long curTime = 0;
		for (File f : files) {
			if(Thread.currentThread().isInterrupted()){
				return -2;
			}
			curTime = System.currentTimeMillis(); 
			if(curTime-updateTime>=UPDATE_THRESHOLD && updateArray!=null && updateArray.size()>0){
				publishProgress(updateArray);
				updateArray = null;
				updateTime = curTime; 
			}
				
			if (!f.isHidden() && f.exists()) {
				if (f.isFile()) {
					path = f.getPath();
					if(TEST) {
						Log.i(TAG, "check file=" + path);
					}

					if (pattern.matcher(path).matches()) {
//						publishProgress(f);
						if(updateArray==null){
							updateArray = new ArrayList<File>();
						}
						updateArray.add(f);
					}
				} else if (f.isDirectory() && isRecursive && !ignore(f.getName())) {
					if(doSearch(f.getAbsolutePath(), pattern, isRecursive)==-2){
						return -2;
					}
				}
			}
		}

		return 0;
	}
	
	public boolean ignore(String name) {
		boolean back = false;
		if ("Android".equals(name) || "tencent".equals(name)) {
			back = true;
		}
		return back;
	}

}
