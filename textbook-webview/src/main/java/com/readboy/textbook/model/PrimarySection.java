package com.readboy.textbook.model;

import java.util.ArrayList;

public class PrimarySection
{
	/**对齐*/
	private String mAlign = null;
	/**缩进*/
	private String mIndentation = null;
	/****/
	private String mSnd = null;
	private ArrayList<String> mEAttrValues = new ArrayList<String>();
	
	private ArrayList<T> mTArrayList = new ArrayList<T>();
	
	public void addT(T t)
	{
		mTArrayList.add(t);
	}
	
	public String getAlign()
	{
		return mAlign;
	}

	public void setAlign(String align)
	{
		mAlign = align;
	}
	
	public ArrayList<T> getSectionT()
	{
		return mTArrayList;
	}

	public class T
	{
		/**对齐*/
		private String mAlign = null;
		/**缩进*/
		private String mIndentation = null;
		/****/
		private String mText = "";
		
		private ArrayList<SPH> mSphArrayList = new ArrayList<SPH>();
		
		private ArrayList<HL> mHlArrayList = new ArrayList<HL>();
		
		public void addHl(HL hl)
		{
			mHlArrayList.add(hl);
		}
		
		public void addText(String text)
		{
			mText += text;
		}
		
		public void addContent(String text)
		{
			mText += text;
		}
		
		public void addSph(SPH sph)
		{
			mSphArrayList.add(sph);
		}
		
		public void setSph(ArrayList<SPH> sph)
		{
			mSphArrayList = sph;
		}
		
		public ArrayList<SPH> getSph()
		{
			return mSphArrayList;
		}
		
		public boolean isShowSndIcon()
		{
			if(mSphArrayList.size() > 0 && "1".equals(mSphArrayList.get(0).mType))
			{
				return true;
			}
			return false;
		}
		
		public void addAttrValue(String value)
		{
			mEAttrValues.add(value);
		}
		
		public String getContent()
		{
			return mText;
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
				return mText;
			}
		}
		
		public class SPH
		{
			/****/
			public String mSnd = null;
			/**声音形式，可以缺省(表示type=0) 0：普通声音(无图标显示) 1：带图标显示的普通声音**/
			public String mType = "0";			
			public String mMute = null;
			
			public SPH(String snd, String type)
			{
				mSnd = snd;
				if(type != null)
				{
					mType = type;
				}
			}
			
			public boolean isShowSndIcon()
			{
				if(mSnd != null && "1".equals(mType))
				{
					return true;
				}
				return false;
			}
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

	}
	
	
}
