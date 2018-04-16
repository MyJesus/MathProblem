package com.readboy.textbookwebview;

import com.readboy.textbook.view.MyWebView;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class WebChromeClientImpl extends WebChromeClient
{
	private MyWebView mWebView;
	
	public WebChromeClientImpl(MyWebView webView)
	{
		mWebView = webView;
	}
	
	@Override
	public void onProgressChanged(WebView view, int newProgress)
	{
		if (newProgress == 100)
		{

		}
	}

	@Override
	public boolean onJsAlert(WebView view, String url, String message, JsResult result)
	{
		//result.confirm();
		return super.onJsAlert(view, url, message, result);
	}

	@Override
	public boolean onJsConfirm(WebView view, String url, String message, JsResult result)
	{
		return super.onJsConfirm(view, url, message, result);
	}

	@Override
	public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
			JsPromptResult result)
	{
		return super.onJsPrompt(view, url, message, defaultValue, result);
	}
}
