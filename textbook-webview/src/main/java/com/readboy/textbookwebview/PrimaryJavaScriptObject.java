package com.readboy.textbookwebview;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.readboy.textbook.view.MyPrimaryWebView;

public class PrimaryJavaScriptObject
{
	Context mContext;
	
	MyPrimaryWebView mMyWebView;
	
	public PrimaryJavaScriptObject(Context context, MyPrimaryWebView webView)
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
	public void read(String id)
	{
		mMyWebView.read(id);
	}
	
	@JavascriptInterface
	public void playSound(String url)
	{
		mMyWebView.playSound(url);
	}
	
	@JavascriptInterface
	public float getDeviceScale()
	{
		return mMyWebView.getDeviceScale();
	}
	
	@JavascriptInterface
	public void scrollPositionToTop(String position)
	{
		mMyWebView.scrollPositionToTop(position);
	}
}
