package com.readboy.textbook.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Part
{
	private HL mHl = null;
	private ArrayList<String> mEAttrValues = new ArrayList<String>();
	private ArrayList<HL> mHlArrayList = new ArrayList<HL>();
	private HashMap<String, String> mCmtMap = new HashMap<String, String>();
	private StringBuilder mContent = new StringBuilder().append(MyHtml.P_INDENT_START_TAG);
	/**对齐*/
	private String mAlign = null;
	/**缩进*/
	private String mIndentation = null;
	/****/
	private String mSnd = null;
	
	private boolean mHasHl = false;
	
	public void addContent(String content)
	{
		mContent.append(content);
	}
	
	public ArrayList<HL> getHlArrayList()
	{
		return mHlArrayList;
	}

	public void setHlArrayList(ArrayList<HL> hlArrayList)
	{
		mHlArrayList = hlArrayList;
	}

	public boolean isHasHl()
	{
		return mHlArrayList.size() > 0? true:false;
	}

	public void setHasHl(boolean hasHl)
	{
		mHasHl = hasHl;
	}

	/**
	 * 高亮，古文、作者...高亮注释
	 * @author dgy
	 * @文件名 Part.java
	 * @version 1.0 2015-11-10
	 */
	public class HL
	{
		private String mId;
		private String mType;
		private String mHlContent;
		
		public HL(String id, String type, String hlContent)
		{
			mId = id;
			mType = type;
			mHlContent = hlContent;
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
		
		public String getHlContent()
		{
			return mHlContent;
		}
		
		@Override
		public String toString()
		{
			return mHlContent;
		}
	}
	
	public class Cmt
	{
		private String mId;
		private String mCmtContent;
		
		public Cmt(String id, String content)
		{
			mId = id;
			mCmtContent = content;
		}

		public String getId()
		{
			return mId;
		}

		public void setId(String id)
		{
			mId = id;
		}

		public String getContent()
		{
			return mCmtContent;
		}

		public void setContent(String content)
		{
			mCmtContent = content;
		}
		
		@Override
		public String toString()
		{
			return mCmtContent;
		}
	}

	public HL getHl()
	{
		return mHl;
	}

	public void setHl(HL hl)
	{
		mHl = hl;
	}
	
	public HashMap<String, String> getCmtMap()
	{
		return mCmtMap;
	}

	public void setCmtMap(HashMap<String, String> cmtMap)
	{
		mCmtMap = cmtMap;
	}

	public boolean isHasEAttrTag()
	{
		return mEAttrValues.size() > 0? true:false;
	}

	public ArrayList<String> getEAttrValues()
	{
		return mEAttrValues;
	}

	public void setEAttrValues(ArrayList<String> eAttrValues)
	{
		mEAttrValues = eAttrValues;
	}
	
	public String getAlign()
	{
		return mAlign;
	}

	public void setAlign(String align)
	{
		mAlign = align;
	}

	public String getIndentation()
	{
		return mIndentation;
	}

	public void setIndentation(String indentation)
	{
		mIndentation = indentation;
	}

	@Override
	public String toString()
	{
		String css = " class=\"p ";
		if(mAlign != null)
		{
			css += "align-center ";
		}
		if(mIndentation != null)
		{
			css += "indentation ";
		}
		css += "\"";
		mContent.insert(2, css);
		mContent.append(MyHtml.P_END_TAG);
		return mContent.toString();
	}
	
	
}
