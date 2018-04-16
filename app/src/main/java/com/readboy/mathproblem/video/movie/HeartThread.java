package com.readboy.mathproblem.video.movie;

import android.os.Handler;

public class HeartThread extends Thread {
	
	private boolean mRun = false;
	private Handler mHandler = null;
	
	public HeartThread (Handler handler) {
		mRun = true;
		mHandler = handler;
	}

	@Override
	public void run() {
		while (mRun) {
			mHandler.sendEmptyMessage(VideoMessage.HEART);
			try {
				sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void pause() {
		mRun = false;
	}
	
}
