package com.readboy.textbookwebview;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.readboy.textbook.view.MyQuestionWebView;

public class QuestionJavaScriptObject
{

	MyQuestionWebView mMyQuestionWebView;
	
	public QuestionJavaScriptObject(Context context, MyQuestionWebView myQuestionWebView)
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
	public void setSelfRating(String id)
	{
		mMyQuestionWebView.setSelfRating(id);
	}
	
	@JavascriptInterface
	public String getChildrenQuestionId(String parentId, String index)
	{
		return mMyQuestionWebView.getChildrenQuestionId(parentId, index);
	}


}
