package com.readboy.textbook.util;



import android.app.Application;
import android.content.Context;
import android.media.AudioManager;

public class MyApplication extends Application
{
	private static MyApplication INSTANCE;
	public static float mDeviceScale;
	/**读书郎平板*/
	public static final int READBOY_PAD = 1;
	/**读书郎手机*/
	public static final int READBOY_PHONE = 2;
	/***平板*/
	public static final int PAD = 3;
	/***手机*/
	public static final int PHONE = 4;
	/**要家长输入密码，才能看答案么(这需求超级无语了！！，说不出话来啊)*/
	public static boolean mCanSeenAnswer = false;
	
	private static int mDeviceMode = READBOY_PAD;
	private static AudioManager mAudioManager;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mDeviceScale = getResources().getDisplayMetrics().density;
		INSTANCE = this;
		mCanSeenAnswer = false;
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		DebugLogger.setDebug(true);
	}

	public static float getDeviceScale()
	{
		return mDeviceScale;
	}

	public static Context getInstance()
	{
		return INSTANCE;
	}
	
	/**
	 * 
	 * @param deviceMode ({@value READBOY_PAD, READBOY_PHONE, PAD, PHONE}
	 */
	public static void setDeviceMode(int deviceMode)
	{
		mDeviceMode = deviceMode;
	}
	
	/**
	 * default {@link #READBOY_PAD}
	 * @return
	 */
	public static int getDeviceMode()
	{
		return mDeviceMode;
	}
	
	public static void setIsCanSeenAnswer(boolean flag)
	{
		mCanSeenAnswer = flag;
	}
	
	public static boolean getIsCanSeenAnswer()
	{
		return mCanSeenAnswer;
	}
	
	public static AudioManager getAudioManager()
	{
		return mAudioManager;
	}
}
