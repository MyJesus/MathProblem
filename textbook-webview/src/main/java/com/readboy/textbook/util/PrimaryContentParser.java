package com.readboy.textbook.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.TextUtils;
import android.util.Xml;

import com.readboy.textbook.model.MyHtml;
import com.readboy.textbook.model.Part;
import com.readboy.textbook.model.PrimarySection;
import com.readboy.textbook.model.Section;

public class PrimaryContentParser
{
	public ArrayList<PrimarySection> xmlPullParseSection(String xmlString)
	{
		XmlPullParser xmlParser = XmlParser.xmlParser;
		ArrayList<PrimarySection> sections = new ArrayList<PrimarySection>();
		try
		{
			xmlParser.setInput(new StringReader(xmlString));
			int eventType = xmlParser.getEventType();
			PrimarySection section = null;
			String hlType = null;
			String hlId = null;
			String psrc = null;
			String val = null;
			PrimarySection.T.SPH sph = null;
			PrimarySection.T sectionT = null;
			boolean needFontColorSpanTag = false;
			while (eventType != XmlPullParser.END_DOCUMENT)
			{			
				if (eventType == XmlPullParser.START_DOCUMENT)
				{
					section = new PrimarySection();
				}
				else if (eventType == XmlPullParser.START_TAG)
				{
					if(XmlParser.T_TAG.equalsIgnoreCase(xmlParser.getName()))
					{
						if(sectionT == null)
						{
							sectionT = section.new T();
						}					
						if(xmlParser.getAttributeCount() > 0)
						{
							String fontColor = xmlParser.getAttributeValue(null, XmlParser.FC_ATTR);
							if(!TextUtils.isEmpty(fontColor))
							{
								if(needFontColorSpanTag)
								{
									sectionT.addContent("</span>");
									needFontColorSpanTag = false;
								}
								sectionT.addContent("<span style=\"color:#"+fontColor+"\">");
								needFontColorSpanTag = true;
							}
							String m = xmlParser.getAttributeValue(null, XmlParser.M_ATTR);
							if(m != null)
							{
								if(!TextUtils.isEmpty(sectionT.getContent()) && !sectionT.getContent().equals(MyHtml.BR_TAG))
								{
									section.addT(sectionT);
									sectionT = section.new T();
								}
								sectionT.setAlign(m);
							}
							
						}						
						String text = xmlParser.nextText();
						if(!TextUtils.isEmpty(text))
						{	
							text = text.replaceAll("\n", MyHtml.BR_TAG);
							if(hlId != null && hlType != null)
							{
								if(!text.equals(MyHtml.BR_TAG))
								{
									sectionT.addHl((sectionT.new HL(hlId, hlType, text)));
									sectionT.addContent(MyHtml.makeHtmlFromPrimaryHL(sectionT.new HL(hlId, hlType, text)));	
								}
								hlId = null;
								hlType = null;
							}
							else
							{
								sectionT.addContent(text);
							}
							if(!TextUtils.isEmpty(val))
							{
								sectionT.setIndentation(val);
								val = null;
							}
							if(sph != null)
							{
								sectionT.addSph(sph);
								sph = null;
							}
							if(text.startsWith(MyHtml.BR_TAG))
							{
								section.addT(sectionT);
								sectionT = section.new T();
							}
						}
					}
					else if(XmlParser.SPH_TAG.equalsIgnoreCase(xmlParser.getName())) //文本声音
					{
						if(sectionT == null)
						{
							sectionT = section.new T();
						}
						String snd = xmlParser.getAttributeValue(null, XmlParser.SND_ATTR);
						String sndType = xmlParser.getAttributeValue(null, XmlParser.TYPE_ATTR);
						sph = sectionT.new SPH(snd, sndType);												
					}
					else if(XmlParser.I_TAG.equalsIgnoreCase(xmlParser.getName())) //缩进
					{
						if(sectionT != null && !TextUtils.isEmpty(sectionT.getContent()))
						{
							if(val != null)
							{
								sectionT.setIndentation(val);
							}
							section.addT(sectionT);
							sectionT = section.new T();
						}
						val = xmlParser.getAttributeValue(null, XmlParser.VAL_ATTR);
					}
					else if (XmlParser.E_TAG.equalsIgnoreCase(xmlParser.getName())) //加点字
					{
						String v = xmlParser.getAttributeValue(null, XmlParser.V_ATTR);
						if(!TextUtils.isEmpty(v) && sectionT != null)
						{
							sectionT.addAttrValue(v);
							sectionT.addContent(v);	
						}
					}
					else if (XmlParser.HL_TAG.equalsIgnoreCase(xmlParser.getName())) 
					{
						if(hlType != null && hlId != null)
						{
							if(sectionT == null)
							{
								sectionT = section.new T();
							}
							addHlContent(hlType, hlId, sectionT);
						}
						hlType = xmlParser.getAttributeValue(null, XmlParser.TYPE_ATTR);
						hlId = xmlParser.getAttributeValue(null, XmlParser.ID_ATTR);	
					}
					else if (XmlParser.P_TAG.equalsIgnoreCase(xmlParser.getName())) //图片
					{
						if(sectionT == null)
						{
							sectionT = section.new T();
						}
						psrc = xmlParser.getAttributeValue(null, XmlParser.SRC_ATTR);
						if(psrc != null)
						{
							sectionT.addContent("<img src='"+psrc+"' />");
						}
					}
					else if (XmlParser.PY_TAG.equalsIgnoreCase(xmlParser.getName())) //拼音
					{
						String text = xmlParser.nextText();
						if(!TextUtils.isEmpty(text))
						{
							if(sectionT == null)
							{
								sectionT = section.new T();
							}
							text = text.replaceAll("\\(", MyHtml.RT_START_TAG);
							text = text.replaceAll("\\)", MyHtml.RT_END_TAG);
							text = MyHtml.RUBY_START_TAG + text + MyHtml.RUBY_END_TAG;
							if(hlId != null && hlType != null)
							{

								sectionT.addHl((sectionT.new HL(hlId, hlType, text)));
								sectionT.addContent(MyHtml.makeHtmlFromPrimaryHL(sectionT.new HL(hlId, hlType, text)));							
								hlId = null;
								hlType = null;
							}
							else
							{
								sectionT.addContent(text);
							}
//							sectionT.addContent(text);
						}
					}
					else if (XmlParser.S_TAG.equalsIgnoreCase(xmlParser.getName())) //上下标
					{
						addSupAndSub(xmlParser, sectionT);
					}
					else if (XmlParser.U_TAG.equalsIgnoreCase(xmlParser.getName())) //下横线
					{
						String v = xmlParser.getAttributeValue(null, XmlParser.V_ATTR);
						if(v != null)
						{
							sectionT.addContent("<U>"+v+"</U>");
						}
					}
				}
				else if (eventType == XmlPullParser.END_TAG)
				{
					if(XmlParser.T_TAG.equalsIgnoreCase(xmlParser.getName())) //文本结束
					{
//						if(sectionT != null)
//						{
//							section.addT(sectionT);
//							sectionT = section.new T();
//						}
					}
				}
				else if (eventType == XmlPullParser.TEXT)
				{
				}
				eventType = xmlParser.next();
			}
			if(sectionT != null)
			{
				if(needFontColorSpanTag)
				{
					sectionT.addContent("</span>");
					needFontColorSpanTag = false;
				}
				if(hlType != null && hlId != null)
				{
					addHlContent(hlType, hlId, sectionT);
				}
				if(val != null)
				{
					sectionT.setIndentation(val);
				}
				section.addT(sectionT);
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

	/**
	 * 上下标
	 * @param xmlParser
	 * @param sectionT
	 */
	private void addSupAndSub(XmlPullParser xmlParser, PrimarySection.T sectionT) {
		String v = xmlParser.getAttributeValue(null, XmlParser.V_ATTR);
		String m = xmlParser.getAttributeValue(null, XmlParser.M_ATTR);
		if("0".equalsIgnoreCase(m))
		{
			sectionT.addContent("<SUP>"+v+"</SUP>");
		}
		else if("1".equalsIgnoreCase(m))
		{
			sectionT.addContent("<SUB>"+v+"</SUB>");
		}
	}

	/**
	 * 段导读、句导读
	 * @param hlType
	 * @param hlId
	 * @param sectionT
	 */
	private void addHlContent(String hlType, String hlId, PrimarySection.T sectionT) {
		if("13".equalsIgnoreCase(hlType))
		{
//			sectionT.addContent("<span class='hl hl-13-icon' id='"+hlId+"'></span>");
		}
		else if("12".equalsIgnoreCase(hlType)) 
		{
			sectionT.addContent("<span class='hl hl-12' id='"+hlId+"'></span>");
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
					if(XmlParser.SECTION_TAG.equalsIgnoreCase(xmlParser.getName()))
					{
						String name = xmlParser.getAttributeValue(null, XmlParser.SECTION_NAME);
						String type = xmlParser.getAttributeValue(null, XmlParser.SECTION_TYPE);
						if(!TextUtils.isEmpty(type))
						{
							if(section != null)
							{
								sections.add(section);
							}
							section = new Section(0, Integer.parseInt(type), name, "");
						}
					}
					else if (XmlParser.PAR_TAG.equalsIgnoreCase(xmlParser.getName())) 
					{
						if(part != null)
						{
							section.getPartArrayList().add(part);
						}
						part = new Part();
					}
					else if(XmlParser.T_TAG.equalsIgnoreCase(xmlParser.getName()))
					{
						String text = xmlParser.nextText();
						if(!TextUtils.isEmpty(text) && part != null)
						{
							part.addContent(text);
						}
					}
					else if (XmlParser.P_TAG.equalsIgnoreCase(xmlParser.getName())) 
					{
						String src = xmlParser.getAttributeValue(null, XmlParser.SRC_ATTR);
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
