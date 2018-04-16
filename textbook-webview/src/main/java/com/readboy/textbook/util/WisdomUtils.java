package com.readboy.textbook.util;

import android.content.Context;
import android.text.TextUtils;

import com.readboy.textbook.http.MyHttpConnection;

public class WisdomUtils
{
	
	public static final String SPLIT_REGEX = "<SEP.*//>";
	
	public void getWindomFromHttp(Context context)
	{
//		MyHttpConnection.getInstance(context).httpStringRequest(MyHttpConnection.GET_BOOK_WISDOM_URL, null, new MyHttpConnection.UrlListener()
//		{
//			
//			@Override
//			public void onResult(String result)
//			{
//								
//			}
//			
//			@Override
//			public void onError(String msg)
//			{
//	
//			}
//		});
	}
	
	public String[] parserWisdom(String source)
	{
		if(TextUtils.isEmpty(source))
		{
			return null;
		}
		else
		{
			return source.split(SPLIT_REGEX);
		}
		
	}
}
