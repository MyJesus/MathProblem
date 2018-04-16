package com.readboy.textbook.model;

import java.util.ArrayList;

public class Section
{
	private int mId;
	private int mType;
	private String mName;
	private String mContent;
	private ArrayList<Part> mPartArrayList =  new ArrayList<Part>();
	
	public Section()
	{
		
	}
	
	public Section(int id, int type, String name, String content)
	{
		mId = id;
		mType = type;
		mContent = content;
		mName = name;
	}
	
	public String getContent()
	{
		return mContent;
	}
	public void setContent(String content)
	{
		mContent = content;
	}
	public int getType()
	{
		return mType;
	}
	public void setType(int type)
	{
		mType = type;
	}
	public int getId()
	{
		return mId;
	}
	public void setId(int id)
	{
		mId = id;
	}

	public String getName()
	{
		return mName;
	}

	public void setName(String name)
	{
		mName = name;
	}

	public ArrayList<Part> getPartArrayList()
	{
		return mPartArrayList;
	}

	public void setPartArrayList(ArrayList<Part> partArrayList)
	{
		mPartArrayList = partArrayList;
	}
	
	public boolean isHasPart()
	{
		return mPartArrayList.size() > 0? true:false;
	}

	@Override
	public String toString()
	{
		String string = "";
		if(mPartArrayList != null)
		{			
			for (Part part: mPartArrayList)
			{
				string += part.toString();
			}
		}
		return string;
	}
	
	
}
