package com.readboy.textbookwebview;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.readboy.textbook.view.MyWebView;

public class JavaScriptObject
{	
	Context mContext;
	
	MyWebView mMyWebView;
	
	public JavaScriptObject(Context context, MyWebView webView)
	{
		mContext = context;
		mMyWebView = webView;
	}
	
	@JavascriptInterface
	public void showComment(String id)
	{
		mMyWebView.showComment(id);
	}
	
	@JavascriptInterface
	public float getDeviceScale()
	{
		return mMyWebView.getDeviceScale();
	}
		
}
