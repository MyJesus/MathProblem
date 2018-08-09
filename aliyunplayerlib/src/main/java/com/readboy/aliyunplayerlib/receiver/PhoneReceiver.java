package com.readboy.aliyunplayerlib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class PhoneReceiver extends BroadcastReceiver {

	private OnPhoneListener mListener;

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent != null) {
			String action = intent.getAction();
			if(action == null){
				return;
			}
			if(action.equals(Intent.ACTION_NEW_OUTGOING_CALL)){
				if(mListener != null){
					mListener.onPhoneOutCall();
				}
			}else if(action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
				if(mListener != null){
					TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
					mListener.onPhoneStateChange(manager.getCallState());
				}
			}
		}
	}
	
	/**
	 * 监听器
	 */
	public interface OnPhoneListener {
		/**
		 * 去电
		 */
		void onPhoneOutCall();
		/**
		 * 来电状态
		 * @param state
         */
		void onPhoneStateChange(int state);
	}
	
	/**
	 * 设置监听
	 * @param listener
	 */
	public void setOnPhoneListener(OnPhoneListener listener) {
		mListener = listener;
	}



}
