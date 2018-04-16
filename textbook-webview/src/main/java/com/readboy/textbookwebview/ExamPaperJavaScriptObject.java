package com.readboy.textbookwebview;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.readboy.textbook.view.MyExamPaperWebView;

public class ExamPaperJavaScriptObject
{
	MyExamPaperWebView mMyExamPaperWebView;
	
	public ExamPaperJavaScriptObject(Context context, MyExamPaperWebView myExamPaperWebView)
	{
		mMyExamPaperWebView = myExamPaperWebView;
	}
	
	@JavascriptInterface
	public void setUserAnswer(String id, String answer)
	{
		mMyExamPaperWebView.setUserAnswer(id, answer);
	}
	
	@JavascriptInterface
	public void setUserInputAnswer(String id, String answer)
	{
		mMyExamPaperWebView.setBlankUserAnswer(id, answer);
	}
	

	@JavascriptInterface
	public void setCheckUserInputAnswer(String id, String answer)
	{
		mMyExamPaperWebView.setBlankUserAnswer(id, answer);
	}
	
	@JavascriptInterface
	public void setBlankUserAnswer(String[] ids, String[] answers)
	{
		mMyExamPaperWebView.setUserAnswer(ids, answers);
	}
	
	@JavascriptInterface
	public void setSelfRating(String id)
	{
		mMyExamPaperWebView.setSelfRating(id);
	}
	
	@JavascriptInterface
	public String getChildrenQuestionId(String parentId, String index)
	{
		return mMyExamPaperWebView.getChildrenQuestionId(parentId, index);
	}
	
	@JavascriptInterface
	public void showSelfRatingPlan(String id, String isRight)
	{
		mMyExamPaperWebView.showSelfRatingPlan(id, isRight);
	}
	
	@JavascriptInterface
	public float getDeviceScale()
	{
		return mMyExamPaperWebView.getDeviceScale();
	}
	
	@JavascriptInterface
	public void getKeyPoint(String id)
	{
		mMyExamPaperWebView.getKeyPoint(id);
	}
}
