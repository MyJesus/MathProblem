package com.readboy.textbook.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.TextUtils;

public class CommentUtils
{
	public static HashMap<String, String> ParserPrimaryComment(String content)
	{
		HashMap<String, String> commentHashMap = new HashMap<String, String>();
		XmlPullParser xmlParser = XmlParser.xmlParser;
		try
		{
			xmlParser.setInput(new StringReader(content));
			int eventType = xmlParser.getEventType();
			String psrc = null;
			String snd = null;
			String val = null;
			StringBuilder textBuilder = new StringBuilder();
			StringBuilder sndBuilder = new StringBuilder();
			while (eventType != XmlPullParser.END_DOCUMENT)
			{			
				if (eventType == XmlPullParser.START_DOCUMENT)
				{
					
				}
				else if (eventType == XmlPullParser.START_TAG)
				{
					if(XmlParser.T_TAG.equalsIgnoreCase(xmlParser.getName()))
					{
						String text = xmlParser.nextText();
						if(!TextUtils.isEmpty(text))
						{
							textBuilder.append(text);
						}
					}
					else if(XmlParser.SPH_TAG.equalsIgnoreCase(xmlParser.getName())) //文本声音
					{
						snd = xmlParser.getAttributeValue(null, XmlParser.SND_ATTR);
						if(!TextUtils.isEmpty(snd))
						{
							sndBuilder.append(snd);
						}
					}
					else if(XmlParser.I_TAG.equalsIgnoreCase(xmlParser.getName())) //缩进
					{
						val = xmlParser.getAttributeValue(null, XmlParser.VAL_ATTR);
					}
					else if (XmlParser.P_TAG.equalsIgnoreCase(xmlParser.getName())) //图片
					{
						psrc = xmlParser.getAttributeValue(null, XmlParser.SRC_ATTR);
						if(psrc != null)
						{
//							sectionT.addContent("<img src=\""+psrc+"\" />");
						}
					}
					else if (XmlParser.S_TAG.equalsIgnoreCase(xmlParser.getName())) //上下标
					{
						String v = xmlParser.getAttributeValue(null, XmlParser.V_ATTR);
						String m = xmlParser.getAttributeValue(null, XmlParser.M_ATTR);
						if("0".equalsIgnoreCase(m))
						{
//							sectionT.addContent("<SUP>"+v+"</SUP>");
						}
						else if("1".equalsIgnoreCase(m))
						{
//							sectionT.addContent("<SUB>"+v+"</SUB>");
						}
					}
					else if (XmlParser.U_TAG.equalsIgnoreCase(xmlParser.getName())) //下横线
					{
						String v = xmlParser.getAttributeValue(null, XmlParser.V_ATTR);
						if(v != null)
						{
							
						}
					}
				}
				else if (eventType == XmlPullParser.END_TAG)
				{
					
				}
				else if (eventType == XmlPullParser.TEXT)
				{
				}
				eventType = xmlParser.next();
			}
			commentHashMap.put("text", textBuilder.toString());
			commentHashMap.put("snd", sndBuilder.toString());

		}
		catch (XmlPullParserException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return commentHashMap;

	}
}
