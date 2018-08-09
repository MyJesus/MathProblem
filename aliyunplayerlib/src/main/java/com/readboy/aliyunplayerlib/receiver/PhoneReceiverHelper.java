package com.readboy.aliyunplayerlib.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;


public class PhoneReceiverHelper {

	private Context mContext;
	private PhoneReceiver mReceiver;

	public PhoneReceiverHelper(Context context){
		mContext = context;
		mReceiver = new PhoneReceiver();
	}

	public void setOnListener(PhoneReceiver.OnPhoneListener listener){
		if(mReceiver != null){
			mReceiver.setOnPhoneListener(listener);
		}
	}

	public void register() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);//去电监听
		filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);//来电状态监听
		mContext.registerReceiver(mReceiver, filter);
	}

	public void unregister() {
		if(mContext != null && mReceiver != null){
			mContext.unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}


    /**
     * 获取电话状态
     * @return
     * {@link TelephonyManager#CALL_STATE_RINGING}响铃,
     * {@link TelephonyManager#CALL_STATE_OFFHOOK}接听电话中,
     * {@link TelephonyManager#CALL_STATE_IDLE}空闲状态
     */
	public static int getCallState(Context context){
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getCallState();
    }

}
