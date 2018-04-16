package com.readboy.textbookwebview;


import com.readboy.textbook.view.PrimaryWebView;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class PrimaryWebViewJavaScriptObject
{
	
Context mContext;
	
	PrimaryWebView mMyWebView;
	
	public PrimaryWebViewJavaScriptObject(Context context, PrimaryWebView webView)
	{
		mContext = context;
		mMyWebView = webView;
	}
	
	@JavascriptInterface
	public float getDeviceScale()
	{
		return mMyWebView.getDeviceScale();
	}

}
