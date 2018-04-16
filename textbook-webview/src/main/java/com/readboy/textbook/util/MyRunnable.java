package com.readboy.textbook.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.conn.ConnectTimeoutException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MyRunnable implements Runnable {
	private boolean cancleTask = false;
	private boolean cancleException = false;
	private Handler mHandler = null;
	private String mUrl;
	private String mFile;

	public MyRunnable(Handler handler, String url, String file) {
		mHandler = handler;
		mUrl = url;
		mFile = file;
	}

	/**
	 * Overriding methods
	 */
	@Override
	public void run() {
		runBefore();
		if (cancleTask == false) {
			running();
		}
		runAfter();
	}

	/**
	 * <Summary Description>
	 */
	private void runAfter() {
		Log.i("KKK", "runAfter()");
	}

	/**
	 * <Summary Description>
	 */
	private void running() {
			if (cancleTask == false && cancleException == false) {
				InputStream is = null;
				FileOutputStream os = null;
				try 
				{
					URL url = new URL(mUrl);
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.setConnectTimeout(2000);
					urlConnection.setReadTimeout(2000);
					urlConnection.setInstanceFollowRedirects(true);//允许重定向
					is = urlConnection.getInputStream();
					os = new FileOutputStream(mFile);
					int len = 0;
					byte[] bs = new byte[1024];
					while (((len = is.read(bs)) != -1)) {//未全部读取
						os.write(bs, 0, len);
					}
					Message msg = mHandler.obtainMessage();
					msg.arg1 = DownloadUtils.DOWNLOAD_FINISH_MSG;
					msg.obj = mUrl;
					mHandler.sendMessage(msg);
				}
				catch(ConnectTimeoutException e){
					cancleException = true;
					Message msg = mHandler.obtainMessage();
					msg.arg1 = DownloadUtils.DOWNLOAD_TIMEOUT_MSG;
					msg.obj = mUrl;
					mHandler.sendMessage(msg);
				}
				catch (Exception e) {
                    cancleException = true;
					Message msg = mHandler.obtainMessage();
					msg.arg1 = DownloadUtils.DOWNLOAD_ERROR_MSG;
					msg.obj = mUrl;
					mHandler.sendMessage(msg);
				}
				finally {
					if (os != null) {
						try {
							os.close();
						} catch (IOException e) {}
					}

					if (is != null) {
						try {
							is.close();
						} catch (IOException e){}
					}
					if(cancleException)
					{
						//异常删除文件
						File file = new File(mFile);
						if(file.exists()){
							file.delete();
						}
					}
				}
			} 
	}

	/**
	 * <Summary Description>
	 */
	private void runBefore() {
		// TODO Auto-generated method stub
	}

	public void setCancleTaskUnit(boolean cancleTask) {
		this.cancleTask = cancleTask;
		// mHandler.sendEmptyMessage(0);
	}

	@Override
	public boolean equals(Object object) {

		if(mFile != null)
		{
			MyRunnable myRunnable = (MyRunnable) object;
			if(mFile.equals(myRunnable.mFile))
			{
				return true;
			}
		}	
		return super.equals(object);
	}
	
	
}
