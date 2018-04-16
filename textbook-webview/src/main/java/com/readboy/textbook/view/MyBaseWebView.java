package com.readboy.textbook.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import com.readboy.textbookwebview.WebViewClientImpl;

/**
 * Created by 1 on 2016/3/23.
 */
public class MyBaseWebView extends WebView
{

    public MyBaseWebView(Context context) {
        super(context);
        init();
    }

    public MyBaseWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyBaseWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        WebSettings ws = getSettings();
        ws.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// LOAD_CACHE_ELSE_NETWORK
        ws.setDatabaseEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setJavaScriptEnabled(true);
        ws.setAllowFileAccess(true);
        ws.setBuiltInZoomControls(false);
        ws.setSupportZoom(false);
        ws.setDefaultTextEncodingName("utf-8");
        ws.setBuiltInZoomControls(false);
//		WebView.setWebContentsDebuggingEnabled(true);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        ws.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
//		setVerticalScrollBarEnabled(false);
//		setVerticalScrollbarOverlay(false);
		setHorizontalScrollBarEnabled(false);
		setHorizontalScrollbarOverlay(false);
        ws.setAppCacheEnabled(true);
        setWebViewClient(new WebViewClientImpl(getContext()));
        cancelLongClickListener(this);
    }
    
	public void initWebViewSetting(WebView webView)
	{
		WebSettings ws = webView.getSettings();
		ws.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// LOAD_CACHE_ELSE_NETWORK
		ws.setDatabaseEnabled(true);
	    ws.setAllowFileAccess(true);
		ws.setDefaultTextEncodingName("utf-8");
		ws.setUseWideViewPort(true);
		ws.setLoadWithOverviewMode(true);
		ws.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		ws.setAppCacheEnabled(true);
		ws.setJavaScriptEnabled(true);
//		setVerticalScrollBarEnabled(false);
//		setVerticalScrollbarOverlay(false);
		setHorizontalScrollBarEnabled(false);
		setHorizontalScrollbarOverlay(false);
		webView.setFocusableInTouchMode(true);
	}
	
	private void cancelLongClickListener(WebView webView)
	{
		webView.setOnLongClickListener(new OnLongClickListener()
		{
			
			@Override
			public boolean onLongClick(View arg0)
			{
				return true;
			}
		});
	}
}
