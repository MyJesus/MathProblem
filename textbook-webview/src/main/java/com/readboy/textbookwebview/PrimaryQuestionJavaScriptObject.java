package com.readboy.textbookwebview;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.readboy.textbook.util.MyApplication;
import com.readboy.textbook.view.MyPrimaryQuestionWebView;

public class PrimaryQuestionJavaScriptObject
{
	MyPrimaryQuestionWebView mMyQuestionWebView;
	
	public PrimaryQuestionJavaScriptObject(Context context, MyPrimaryQuestionWebView myQuestionWebView)
	{
		mMyQuestionWebView = myQuestionWebView;
	}
	
	@JavascriptInterface
	public void setUserAnswer(String id, String answer)
	{
		mMyQuestionWebView.setUserAnswer(id, answer);
	}
	
	@JavascriptInterface
	public void setBlankUserAnswer(String id, String answer)
	{
		mMyQuestionWebView.setBlankUserAnswer(id, answer);
	}
	
	@JavascriptInterface
	public void setBlankUserAnswer(String[] ids, String[] answers)
	{
		mMyQuestionWebView.setUserAnswer(ids, answers);
	}
	
	@JavascriptInterface
	public void showAnswer(String id)
	{
		mMyQuestionWebView.showAnswer(id);
	}
	
	
	@JavascriptInterface
	public void setSelfRating(String id)
	{
		mMyQuestionWebView.setSelfRating(id);
	}
	
	@JavascriptInterface
	public String getChildrenQuestionId(String parentId, String index)
	{
		return mMyQuestionWebView.getChildrenQuestionId(parentId, index);
	}
	
	@JavascriptInterface
	public float getDeviceScale()
	{
		return mMyQuestionWebView.getDeviceScale();
	}
	
	@JavascriptInterface
	public void startCheckPasswordActivity()
	{
		mMyQuestionWebView.startParentPasswordActivity();
	}
	
	@JavascriptInterface
	public boolean checkPasssword()
	{
		return MyApplication.mCanSeenAnswer;
	}
}
