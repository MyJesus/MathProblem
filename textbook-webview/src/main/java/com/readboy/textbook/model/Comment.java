package com.readboy.textbook.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class Comment
{
	private HashMap<String, Item> mCommentItemMap = new HashMap<String, Item>();
	
	public HashMap<String, Item> getCommentItemMap()
	{
		return mCommentItemMap;
	}
	public void setCommentItemMap(HashMap<String, Item> commentItemMap)
	{
		mCommentItemMap = commentItemMap;
	}
	
	public void addCommentItem(Item item)
	{
		mCommentItemMap.put(item.mId, item);
	}
	
	public String getCommentText(String id)
	{
		Item item = mCommentItemMap.get(id);
		return item != null? item.getContent() : null;
	}
	
	public class Item
	{	
		private String mId;
		private String mType;
		private String mContent;
		
		public Item(String id, String type, String content)
		{
			mId = id;
			mType = type;
			mContent = content;
		}
		
		public String getId()
		{
			return mId;
		}
		public void setId(String id)
		{
			mId = id;
		}
		public String getType()
		{
			return mType;
		}
		public void setType(String type)
		{
			mType = type;
		}
		public String getContent()
		{
			return mContent;
		}
		public void setContent(String content)
		{
			mContent = content;
		}

		@Override
		public String toString()
		{
			return mContent;
		}		
	}
	
	@Override
	public String toString()
	{
		Iterator<Entry<String, Item>> iter = mCommentItemMap.entrySet().iterator();
		StringBuilder contentBuilder  = new StringBuilder();
		while (iter.hasNext()) 
		{
			Entry<String, Item> entry = iter.next();
			contentBuilder.append(entry.getValue().toString());
		}
		return contentBuilder.toString();
	}
	
}
