package com.readboy.textbook.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.readboy.textbook.model.MyHtml;
import com.readboy.textbook.model.Part;
import com.readboy.textbook.model.Section;

import android.text.TextUtils;
import android.util.Xml;

public class XmlParser
{
	public static final String SECTION_NAME = "name";
	public static final String SECTION_TYPE = "type";
	public static final String SECTION_TAG = "SECT";
	/**文本标签*/
	public static final String T_TAG = "T";
	/**段落标签*/
	public static final String PAR_TAG = "PAR";
	/**加点字标签**/
	public static final String E_TAG = "E";
	public static final String HL_TAG = "HL";
	/**注解标签*/
	public static final String CMT_TAG = "Cmt";
	/**图片标签**/
	public static final String P_TAG = "P";
	/**小学版缩进标签**/
	public static final String I_TAG = "I";
	/**声音标签***/
	public static final String SPH_TAG = "SPH";	
	/**拼音标签***/
	public static final String PY_TAG = "PY";
	
	public static final String V_ATTR = "v";
	public static final String M_ATTR = "m";
	/***Id属性*/
	public static final String ID_ATTR = "id";
	/**类型属性*/
	public static final String TYPE_ATTR = "type";
	/**图片src属性*/
	public static final String SRC_ATTR = "src";
	/**上下标标签*/
	public static final String S_TAG = "S";
	/***下横线标签**/
	public static final String U_TAG = "U";
	/***字体颜色标签*/
	public static final String FC_ATTR = "fc";
	/***flc 缩进标签*/
	public static final String FLC_ATTR = "flc";
	/*****/
	public static final String VAL_ATTR = "v";
	/***声音路径属性**/
	public static final String SND_ATTR = "snd";
	
	public static final XmlPullParser xmlParser = Xml.newPullParser(); 
	
	public ArrayList<Section> xmlPullParseSection(String xmlString)
	{
		XmlPullParser xmlParser = Xml.newPullParser();
		ArrayList<Section> sections = new ArrayList<Section>();
		try
		{
			xmlParser.setInput(new StringReader(xmlString));
			int eventType = xmlParser.getEventType();
			Section section = null;
			Part part = null;
			String hlType = null;
			String hlId = null;
			String cmtId = null;
			String psrc = null;
			boolean needFontColorSpanTag = false;
			while (eventType != XmlPullParser.END_DOCUMENT)
			{			
				if (eventType == XmlPullParser.START_DOCUMENT)
				{
					section = new Section(0, 0, "", "");
				}
				else if (eventType == XmlPullParser.START_TAG)
				{
					if(SECTION_TAG.equalsIgnoreCase(xmlParser.getName()))
					{
						String name = xmlParser.getAttributeValue(null, SECTION_NAME);
						String type = xmlParser.getAttributeValue(null, SECTION_TYPE);
						if(!TextUtils.isEmpty(type))
						{
							if(section != null)
							{
								sections.add(section);
							}
							section = new Section(0, Integer.parseInt(type), name, "");
						}
					}
					else if (PAR_TAG.equalsIgnoreCase(xmlParser.getName())) 
					{
						if(part != null)
						{
							if(needFontColorSpanTag)
							{
								part.addContent("</span>");
								needFontColorSpanTag = false;
							}
							addHlContent(part, hlType, hlId);
							section.getPartArrayList().add(part);
						}
						part = new Part();
						String m = xmlParser.getAttributeValue(null, M_ATTR);						
						if(!TextUtils.isEmpty(m))
						{
							part.setAlign(m);
						}
						String flc = xmlParser.getAttributeValue(null, FLC_ATTR);
						if(!TextUtils.isEmpty(flc))
						{
							part.setIndentation(flc);
						}
					}
					else if(T_TAG.equalsIgnoreCase(xmlParser.getName()))
					{
						if(xmlParser.getAttributeCount() > 0)
						{
							String fontColor = xmlParser.getAttributeValue(null, FC_ATTR);
							if(!TextUtils.isEmpty(fontColor))
							{
								if(needFontColorSpanTag)
								{
									part.addContent("</span>");
									needFontColorSpanTag = false;
								}
								part.addContent("<span style=\"color:#"+fontColor+"\">");
								needFontColorSpanTag = true;
							}
						}						
						String text = xmlParser.nextText();
						if(text != null && part != null)
						{
							
							if(hlId != null && hlType != null)
							{
								if("\n".equalsIgnoreCase(text))
								{
//									if("13".equalsIgnoreCase(hlType))
//									{
//										part.addContent("<img class=\"hl hl-12\" src=\""+MyHtml.HL_SENTENCE_IMG_SRC+"\"" + "id=\""+hlId+"\"/>");
//									}
//									else if("12".equalsIgnoreCase(hlType)) 
//									{
//										part.addContent("<img class=\"hl hl-12\" src=\""+MyHtml.HL_PART_IMG_SRC+"\"" + "id=\""+hlId+"\"/>");
//									}
								}
								else
								{
									part.getHlArrayList().add((part.new HL(hlId, hlType, text)));
									part.addContent(MyHtml.makeHtmlFromHL(part.new HL(hlId, hlType, text)));
								}
								hlId = null;
								hlType = null;
							}
							else if(cmtId != null)
							{
								part.getCmtMap().put(cmtId, text);
								cmtId = null;
								part.addContent(text);
							}
							else
							{
								part.addContent(text);
							}
						}
					}
					else if (E_TAG.equalsIgnoreCase(xmlParser.getName())) 
					{
						String v = xmlParser.getAttributeValue(null, V_ATTR);
						if(!TextUtils.isEmpty(v) && part != null)
						{
							part.getEAttrValues().add(v);
							part.addContent(v);	
						}
					}
					else if (HL_TAG.equalsIgnoreCase(xmlParser.getName())) 
					{
						addHlContent(part, hlType, hlId);
						hlType = xmlParser.getAttributeValue(null, TYPE_ATTR);
						hlId = xmlParser.getAttributeValue(null, ID_ATTR);	
					}
					else if (CMT_TAG.equalsIgnoreCase(xmlParser.getName())) 
					{
						cmtId = xmlParser.getAttributeValue(null, ID_ATTR);
					}
					else if (P_TAG.equalsIgnoreCase(xmlParser.getName())) 
					{
						psrc = xmlParser.getAttributeValue(null, SRC_ATTR);
						if(part != null && psrc != null)
						{
							part.addContent("<img src=\""+psrc+"\" />");
						}
					}
					else if (S_TAG.equalsIgnoreCase(xmlParser.getName()))
					{
						String v = xmlParser.getAttributeValue(null, V_ATTR);
						String m = xmlParser.getAttributeValue(null, M_ATTR);
						if("0".equalsIgnoreCase(m))
						{
							part.addContent("<SUP>"+v+"</SUP>");
						}
						else if("1".equalsIgnoreCase(m))
						{
							part.addContent("<SUB>"+v+"</SUB>");
						}
					}
					else if (U_TAG.equalsIgnoreCase(xmlParser.getName()))
					{
						String v = xmlParser.getAttributeValue(null, V_ATTR);
						if(v != null)
						{
							part.addContent("<U>"+v+"</U>");
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
			if(part != null)
			{
				if(needFontColorSpanTag)
				{
					part.addContent("</span>");
					needFontColorSpanTag = false;
				}
				addHlContent(part, hlType, hlId);
				section.getPartArrayList().add(part);
				sections.add(section);
			}
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
		return sections;

	}

	private void addHlContent(Part part, String hlType, String hlId) {
		if(hlType != null && hlId != null)
		{
			if("13".equalsIgnoreCase(hlType))
			{
				part.addContent("<span class='hl hl-13-icon' id='"+hlId+"'></span>");
			}
			else if("12".equalsIgnoreCase(hlType)) 
			{
				part.addContent("<span class='hl hl-12' id='"+hlId+"'></span>");
			}
		}
	}
	
	public ArrayList<Section> xmlPullParseWisdomSection(String xmlString)
	{
		XmlPullParser xmlParser = Xml.newPullParser();
		ArrayList<Section> sections = new ArrayList<Section>();
		try
		{
			xmlParser.setInput(new StringReader(xmlString));
			int eventType = xmlParser.getEventType();
			Section section = null;
			Part part = null;
			while (eventType != XmlPullParser.END_DOCUMENT)
			{			
				if (eventType == XmlPullParser.START_DOCUMENT)
				{
				}
				else if (eventType == XmlPullParser.START_TAG)
				{
					if(SECTION_TAG.equalsIgnoreCase(xmlParser.getName()))
					{
						String name = xmlParser.getAttributeValue(null, SECTION_NAME);
						String type = xmlParser.getAttributeValue(null, SECTION_TYPE);
						if(!TextUtils.isEmpty(type))
						{
							if(section != null)
							{
								sections.add(section);
							}
							section = new Section(0, Integer.parseInt(type), name, "");
						}
					}
					else if (PAR_TAG.equalsIgnoreCase(xmlParser.getName())) 
					{
						if(part != null)
						{
							section.getPartArrayList().add(part);
						}
						part = new Part();
					}
					else if(T_TAG.equalsIgnoreCase(xmlParser.getName()))
					{
						String text = xmlParser.nextText();
						if(!TextUtils.isEmpty(text) && part != null)
						{
							part.addContent(text);
						}
					}
					else if (P_TAG.equalsIgnoreCase(xmlParser.getName())) 
					{
						String src = xmlParser.getAttributeValue(null, SRC_ATTR);
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
		return sections;

	}
}
