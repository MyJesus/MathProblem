package com.readboy.textbook.model;

public class WisdomTips
{
	private String mId;
	private String mTitle;
	private String mHtmlContent;
	
	public WisdomTips(String id, String title, String htmlContent)
	{
		mId = id;
		mTitle = title;
		mHtmlContent = htmlContent;
	}
	
	public String getId()
	{
		return mId;
	}
	public void setId(String id)
	{
		mId = id;
	}
	public String getTitle()
	{
		return mTitle;
	}
	public void setTitle(String title)
	{
		mTitle = title;
	}
	public String getHtmlContent()
	{
		return mHtmlContent;
	}
	public void setHtmlContent(String htmlContent)
	{
		mHtmlContent = htmlContent;
	}
}
