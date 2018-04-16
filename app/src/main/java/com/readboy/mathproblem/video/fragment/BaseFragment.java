package com.readboy.mathproblem.video.fragment;

import android.support.v4.app.Fragment;

import com.readboy.mathproblem.video.db.VideoDatabaseInfo;

public abstract class BaseFragment extends Fragment {
	
	public abstract boolean start();
	
	public abstract void pause();
	
	public abstract void seekTo(long mesc);
	
	public abstract void stopPlayback();
	
	public abstract boolean isPlaying();
	
	public abstract int getBufferPercentage();
	
	public abstract int getCurrentPosition();
	
	public abstract long getDuration();
	
	public abstract void setPlayPath(String playPath, VideoDatabaseInfo info);
	
	public abstract void setLayout();
	
	public abstract void setLayout(int type);

	public abstract boolean isInPlaybackState();


}
