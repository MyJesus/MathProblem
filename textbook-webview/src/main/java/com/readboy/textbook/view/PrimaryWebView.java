package com.readboy.textbook.view;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.readboy.textbook.model.Comment;
import com.readboy.textbook.model.MyHtml;
import com.readboy.textbook.util.MyApplication;
import com.readboy.textbook.util.NetWorkUtils;
import com.readboy.textbook.util.XmlParser;
import com.readboy.textbookwebview.PrimaryWebViewJavaScriptObject;
import com.readboy.textbookwebview.WebViewClientImpl;

public class PrimaryWebView extends WebView
{

	private Context mContext;
	private PrimaryWebViewJavaScriptObject mJavaScriptObject;
	private Comment mComment;
	
	public PrimaryWebView(Context context)
	{
		super(context);
		mContext = context;
		init();
	}
	
	public PrimaryWebView(Context context, AttributeSet attrs)
	{
		super(context, attrs, 0);
		mContext = context;
		init();
	}
	
	public PrimaryWebView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}
	
	private void init()
	{
		WebSettings ws = getSettings();
		ws.setCacheMode(WebSettings.LOAD_DEFAULT);// LOAD_CACHE_ELSE_NETWORK
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
		
		mJavaScriptObject = new PrimaryWebViewJavaScriptObject(mContext, this);
		addJavascriptInterface(mJavaScriptObject, "JavaScriptObject");
		
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
		loadDataWithBaseURL(NetWorkUtils.baseUrl, htmlString,"text/html; charset=UTF-8", "UTF-8", "");
	}
	
	public void loadData(String html)
	{
		loadDataWithBaseURL(NetWorkUtils.baseUrl, html, "text/html; charset=UTF-8", "UTF-8", "");
	}
	
	public float getDeviceScale()
	{
		return MyApplication.mDeviceScale;
	}
}
