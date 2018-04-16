package com.readboy.mathproblem.notetool;

import android.graphics.Bitmap;

public class Parcel {
	private static final String TAG = "Note_Parcel";

	public Bitmap mBitmap;	
	public int mIdx;	
	public ParcelType mCreateType;
	public boolean mIsReady;	
	
	public Parcel(Bitmap bmp, int parcelIdx, ParcelType createType, boolean bReady){
		mBitmap = bmp;
		mIdx = parcelIdx;
		mCreateType = createType;
		mIsReady = bReady;
	}
	
	public Parcel(){
		mBitmap = null;
		mIdx = -1;
		mCreateType = ParcelType.UnInit;
		mIsReady = false;
	}
	
	public void recycle() {
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
		}
		mBitmap = null;
	}
	
	public enum ParcelType{
		ByUiThread,
		ByCacheThread,
		UnInit
	}
}
