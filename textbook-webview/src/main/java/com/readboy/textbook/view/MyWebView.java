package com.readboy.textbook.view;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import com.readboy.textbook.model.Comment;
import com.readboy.textbook.model.MyHtml;
import com.readboy.textbook.util.MyApplication;
import com.readboy.textbook.util.NetWorkUtils;
import com.readboy.textbook.util.XmlParser;
import com.readboy.textbookwebview.JavaScriptObject;
import com.readboy.textbookwebview.WebChromeClientImpl;
import com.readboy.textbookwebview.WebViewClientImpl;

public class MyWebView extends WebView
{

	private Context mContext;
	private JavaScriptObject mJavaScriptObject;
	private Comment mComment;
	
	public MyWebView(Context context)
	{
		super(context);
		mContext = context;
		init();
	}
	
	public MyWebView(Context context, AttributeSet attrs)
	{
		super(context, attrs, 0);
		mContext = context;
		init();
	}
	
	public MyWebView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		mContext = context;
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
		ws.setBuiltInZoomControls(true);
		ws.setSupportZoom(false);
		ws.setDefaultTextEncodingName("utf-8");
		ws.setBuiltInZoomControls(false);
//		ws.setUseWideViewPort(true);
//		ws.setLoadWithOverviewMode(true);
//		ws.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		ws.setAppCacheEnabled(true);
//		ws.setTextSize(WebSettings.TextSize.LARGER);
		
		mJavaScriptObject = new JavaScriptObject(mContext, this);
		addJavascriptInterface(mJavaScriptObject, "JavaScriptObject");
		
		setWebChromeClient(new WebChromeClientImpl(this));
		setWebViewClient(new WebViewClientImpl(mContext));
	}
	
	/**
	 * 显示一段content
	 */
	public void setContent(String content)
	{
		loadView(content);
	}
	
	/**
	 * 显示一段content
	 */
	public void setContent(JSONArray content)
	{

		if (content != null && content.length() > 0) 
		{
			StringBuilder explain = new StringBuilder();
			ArrayList<JSONArray> commentArrayList = new ArrayList<JSONArray>();
			for (int i = 0; i < content.length(); i++) 
			{
				JSONObject contentJson = content.optJSONObject(i);
				if (contentJson != null) 
				{
					explain.append(contentJson.optString("explain", ""));	
					JSONArray commentJsonArray = contentJson.optJSONArray("comment");
					if(commentJsonArray != null)
					{
						commentArrayList.add(commentJsonArray);
					}
				}
			}
			loadView(explain.toString());
			if(commentArrayList.size() > 0)
			{
				setComment(commentArrayList.get(0));
			}
		}		
		
	}
	
	/**
	 * set注解(文言文解释、作者、背景等引用)
	 */
	public void setComment(JSONArray comment)
	{
		if(comment != null)
		{
			mComment = new Comment();
			int count = comment.length();
			for(int i=0; i<count; i++)
			{
				JSONObject commentItemJsonObject = comment.optJSONObject(i);
				if(commentItemJsonObject != null)
				{
					String id = commentItemJsonObject.optString("id");
					String type = commentItemJsonObject.optString("type");
					String content = commentItemJsonObject.optString("content");
					if(!TextUtils.isEmpty(id) && !TextUtils.isEmpty(content))
					{
						mComment.addCommentItem(mComment.new Item(id, type, content));
					}
				}
			}
		}
	}
	
	/**
	 * 弹出注解
	 */
	public void showComment(String id)
	{
		String commentId = id.trim();
		if(!TextUtils.isEmpty(commentId) && mComment != null)
		{
			String commentText = mComment.getCommentText(commentId);
			if(commentText != null)
			{
				commentText = commentText.replaceAll("<PAR.*/>", "");
				commentText = commentText.replaceAll("<T>", "");
				commentText = commentText.replaceAll("</T>", "");
				if(commentText != "")
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
					builder.setMessage(commentText);
					builder.setCancelable(true);
					builder.show();
				}
			}
		}
	}
	
	private void loadView(String content)
	{
		String htmlString = MyHtml.tohtml(new XmlParser().xmlPullParseSection(content));
		loadDataWithBaseURL(NetWorkUtils.baseUrl, htmlString,"text/html", "UTF-8", "");
	}
	
	public void loadData(String html)
	{
		loadDataWithBaseURL(NetWorkUtils.baseUrl, html, "text/html", "UTF-8", "");
	}
	
	public float getDeviceScale()
	{
		return MyApplication.mDeviceScale;
	}

}
