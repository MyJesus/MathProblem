package com.readboy.textbook.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

import com.readboy.textbook.view.MyPrimaryWebView;

public class MediaPlayUtils implements OnBufferingUpdateListener, OnPreparedListener, OnErrorListener, OnCompletionListener
{
	private static MediaPlayer mMediaPlayer = null;
	
	private static MediaPlayUtils instance = null;
	
	private static DownloadUtils mDownloadUtils = null;
	
//	private ArrayList<String> mPlayList = new ArrayList<String>();
	
	private HashMap<Integer, String> mHashMap = new HashMap<>();
	
	private SparseArray<String> mPlayList = new SparseArray<>();
	
	private int mPlayIndex = 0;
	
	static Handler mHandler = null;
	
	static boolean isSoundStop = false;
	
	public static final int DELAY_CONFIGRM_MSG = 100001;
	private static final int DELAY_TIME = 1000;
	
	/**权限错误*/
	public static final int SECURITY_EXCEPTION_MSG = 100002;
	/**声音停止*/
	public static final int MUSIC_STOP_MSG = 100003; 

	public static MediaPlayUtils getInstance()
	{
		if (instance == null)
		{
			new MediaPlayUtils();
		}
		return instance;
	}
	
	public MediaPlayUtils()
	{
		try
		{
			if(mMediaPlayer == null)
			{
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mMediaPlayer.setOnBufferingUpdateListener(this);
				mMediaPlayer.setOnPreparedListener(this);
				mMediaPlayer.setOnErrorListener(this);
				mMediaPlayer.setOnCompletionListener(this);
			}
			mDownloadUtils = new DownloadUtils(new Handler()
			{
				@Override
				public void handleMessage(Message msg) 
				{
					switch (msg.arg1) {
					case DownloadUtils.DOWNLOAD_FINISH_MSG:
						if(!isSoundStop)
						{
							Message finshMessage = new Message();
							finshMessage.arg1 = msg.arg1;
							finshMessage.obj = msg.obj;
							sendMessageToHandler(finshMessage);
						}
						break;
					
					case DownloadUtils.DOWNLOAD_ERROR_MSG:
						Message errorMessage = new Message();
						errorMessage.arg1 = msg.arg1;
						errorMessage.obj = msg.obj;
						sendMessageToHandler(errorMessage);
						break;
						
					case DownloadUtils.DOWNLOAD_TIMEOUT_MSG:
						Message timeOutmessage = new Message();
						timeOutmessage.arg1 = msg.arg1;
						timeOutmessage.obj = msg.obj;
						sendMessageToHandler(timeOutmessage);
						break;
				
					default:
						break;
					}
				}
			});
			isSoundStop = false;
			instance = this;
		}
		catch (Exception e)
		{
			Log.e("mediaPlayer", "error", e);
			instance = null;
		}

	}
	
	 OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
	        public void onAudioFocusChange(int focusChange) {
	            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
	                // Pause playback
	            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
	                // Resume playback
	            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
	                // mAm.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
	            	MyApplication.getAudioManager().abandonAudioFocus(afChangeListener);
	                // Stop playback
	                stop();
	            }

	        }
	    };

	    private boolean requestFocus() {
	        // Request audio focus for playback
	        int result = MyApplication.getAudioManager().requestAudioFocus(afChangeListener,
	        // Use the music stream.
	                AudioManager.STREAM_MUSIC,
	                // Request permanent focus.
	                AudioManager.AUDIOFOCUS_GAIN);
	        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
	    }

	// *****************************************************

	public void play()
	{
		isSoundStop = false;
		mMediaPlayer.start();
	}

	public void playUrl(String videoUrl, boolean isdownload)
	{
		isSoundStop = false;
		String path = CacheUtils.getPageMediaByMediaUri(null, videoUrl);
		File file = new File(path);
		try
		{
//			MediaPlayer mediaPlayer = getMediaPlay();
			if(isdownload)
			{
				if(!file.exists())
				{
					mDownloadUtils.download(videoUrl, path);
				}
				else
				{
					mPlayIndex ++;
					mMediaPlayer.reset();
					mMediaPlayer.setDataSource(path);
					mMediaPlayer.prepareAsync();
				}
			}
			else 
			{
				if(file.exists())
				{
					videoUrl = path;
				}
				mPlayIndex ++;
				mMediaPlayer.reset();
				mMediaPlayer.setDataSource(videoUrl);
				mMediaPlayer.prepareAsync();
			}
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			deleteFileAftenPlayError(file);
		}
		catch (IllegalStateException e)
		{
			e.printStackTrace();
			deleteFileAftenPlayError(file);
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
			sendEmptyMessageToHandler(SECURITY_EXCEPTION_MSG);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			deleteFileAftenPlayError(file);
		}
	}
	
	public void playUrl(List<String> list)
	{
//		try
		{
//			mPlayList = playList;
			isSoundStop = false;
			mPlayIndex = 0;
			int size = list.size();
			for (int i=0; i<size; i++) 
			{
				String path = CacheUtils.getPageMediaByMediaUri(null, list.get(i));
				File file = new File(path);
				if(!file.exists())
				{
					mDownloadUtils.download(list.get(i), path);
				}
				else
				{
					mPlayList.put(i, path);
					if(i == 0)
					{
						playUrl(path, false);
					}
				}
			}
		}
	}
	
	public static boolean isPlaying()
	{
		if(mMediaPlayer != null)
		{
			return mMediaPlayer.isPlaying();
		}
		return false;
	}
	
	public static void release()
	{
		isSoundStop = true;
		if(mMediaPlayer != null)
		{
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		if(mHandler != null)
		{
			mHandler.sendEmptyMessageDelayed(DELAY_CONFIGRM_MSG, DELAY_TIME);
		}
		if(mHandler != null)
		{
			mHandler = null;
		}
		instance = null;
	}
	
	public static void pause()
	{
		pauseSound();
	}

	public static synchronized void stop()
	{
		isSoundStop = true;
		{
			if (mMediaPlayer != null && mMediaPlayer.isPlaying())
			{
				mMediaPlayer.stop();
			}
			if(mHandler != null)
			{
				mHandler.sendEmptyMessage(MUSIC_STOP_MSG);
			}
		}
	}
	
	public static synchronized void delayStop()
	{
		if (mMediaPlayer != null && mMediaPlayer.isPlaying() && isSoundStop)
		{
			mMediaPlayer.stop();
		}
	}
	
	public void stopSound()
	{
		isSoundStop = true;
		{
			if (mMediaPlayer != null)
			{
				synchronized(mMediaPlayer)
				{
					mMediaPlayer.stop();
					mMediaPlayer.release();
					mMediaPlayer = null;
				}
			}
			if(mHandler != null)
			{
				mHandler.sendEmptyMessageDelayed(DELAY_CONFIGRM_MSG, DELAY_TIME);
			}
		}
	}
	
	public static void pauseSound()
	{
		isSoundStop = true;
		if(mMediaPlayer != null && mMediaPlayer.isPlaying())
		{
			synchronized(mMediaPlayer)
			{
				mMediaPlayer.pause();
			}
		}
//		sendEmptyMessageToHandler(MyPrimaryWebView.RESET_READ_BTN_MSG);		
	}
	
	public void setHandler(Handler handler)
	{
		mHandler = handler;
	}
	
	public MediaPlayer getMediaPlay()
	{
		if(mMediaPlayer == null)
		{
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setOnBufferingUpdateListener(this);
			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.setOnErrorListener(this);
			mMediaPlayer.setOnCompletionListener(this);
		}
		return mMediaPlayer;
	}
	
	public void setCompletionListener(OnCompletionListener listener)
	{
		if(mMediaPlayer == null)
		{
			getMediaPlay();
		}
		mMediaPlayer.setOnCompletionListener(listener);
	}
	
	public void setErrorListener(OnErrorListener listener)
	{
		if(mMediaPlayer == null)
		{
			getMediaPlay();
		}
		mMediaPlayer.setOnErrorListener(listener);
	}

	@Override
	public void onPrepared(MediaPlayer arg0)
	{
		if(!isSoundStop)
		{
			arg0.start();
		}
		else
		{
//			arg0.stop();
		}
	}

	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress)
	{
		if(isSoundStop)
		{
			arg0.stop();
		}
	}

	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) 
	{
		isSoundStop = true;
		sendEmptyMessageToHandler(MyPrimaryWebView.RESET_READ_BTN_MSG);
		return true;
	}

	@Override
	public void onCompletion(MediaPlayer arg0) 
	{
		if(!isSoundStop)
		{
			sendEmptyMessageToHandler(MyPrimaryWebView.SOUND_PLAY_COMPLETION_MSG);
		}
	}
	
	private static void sendMessageToHandler(Message msg)
	{
		if(mHandler != null)
		{
			mHandler.sendMessage(msg);
		}
	}
	
	private static void sendEmptyMessageToHandler(int what)
	{
		if(mHandler != null)
		{
			mHandler.sendEmptyMessage(what);
		}
	}
	
	private void deleteFileAftenPlayError(File file)
	{
		isSoundStop = true;
		if(file.exists())
		{
			file.delete();
		}
		sendEmptyMessageToHandler(MyPrimaryWebView.RESET_READ_BTN_MSG);
	}

}
