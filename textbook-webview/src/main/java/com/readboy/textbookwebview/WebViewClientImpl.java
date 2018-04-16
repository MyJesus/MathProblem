package com.readboy.textbookwebview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.readboy.textbook.util.DebugLogger;

public class WebViewClientImpl extends WebViewClient
{

	public static final String TAG = WebViewClientImpl.class.getSimpleName();

	private Context mContext = null;

	public WebViewClientImpl(Context context)
	{
		mContext = context;
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url)
	{
		return true;
	}

	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
	{
		DebugLogger.getLogger().e(errorCode+description+failingUrl);
		super.onReceivedError(view, errorCode, description, failingUrl);
	}

	@Override
	public void onLoadResource(WebView view, String url)
	{
		super.onLoadResource(view, url);
	}

	@Override
	public void onPageFinished(WebView view, String url)
	{
		super.onPageFinished(view, url);
	}

	@SuppressWarnings("deprecation")
	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, String url)
	{
		return super.shouldInterceptRequest(view, url);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request)
	{
		return super.shouldInterceptRequest(view, request);
	}

}
