package com.readboy.textbook.model;

import java.util.ArrayList;
import java.util.HashMap;

import android.text.TextUtils;

import com.readboy.textbook.model.Part.HL;
import com.readboy.textbook.model.PrimarySection.T;
import com.readboy.textbook.model.PrimarySection.T.SPH;
import com.readboy.textbook.util.MyApplication;


public class MyHtml
{
    public static final String HTML_HEAD = "<!DOCTYPE html><html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />" +
//    		"<meta name='viewport' content='width=device-width'>" +
            "<meta name='viewport' content='width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no'>";
    
    public static final String EXPLAIN_1_0_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/textbook_mdip.css'>";
    public static final String EXPLAIN_1_5_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/textbook_mdip.css'>";
    public static final String EXPLAIN_2_0_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/textbook_mdip.css'>";
    public static final String EXPLAIN_2_5_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/textbook_mdip.css'>";
    public static final String EXPLAIN_3_0_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/textbook.css'>";
    
    public static final String QUESTION_1_0_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/question_mdip.css'>";
    public static final String QUESTION_1_5_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/question_mdip.css'>";
    public static final String QUESTION_2_0_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/question_mdip.css'>";
    public static final String QUESTION_2_5_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/question_mdip.css'>";
    public static final String QUESTION_3_0_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/question.css' />";
   
    public static final String SUBSIDIARY_BOOK_1_0_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/subsidiary_book_mdip.css'>";
    public static final String SUBSIDIARY_BOOK_1_5_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/subsidiary_book_mdip.css'>";
    public static final String SUBSIDIARY_BOOK_2_0_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/subsidiary_book_mdip.css'>";
    public static final String SUBSIDIARY_BOOK_2_5_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/subsidiary_book_mdip.css'>";
    public static final String SUBSIDIARY_BOOK_3_0_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/subsidiary_book.css' />";
    
    public static final String QUESTION_DETAIL_1_0_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/question_detail_mdip.css'>";
    public static final String QUESTION_DETAIL_1_5_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/question_detail_mdip.css'>";
    public static final String QUESTION_DETAIL_2_0_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/question_detail_mdip.css'>";
    public static final String QUESTION_DETAIL_2_5_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/question_detail_mdip.css'>";
    public static final String QUESTION_DETAIL_3_0_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/question_detail.css' />";
    
    public static final String JQUERY = "<script type='text/javascript' src='file:///android_asset/js/jquery-1.11.1.min.js'></script>";
    public static final String EXPLAIN_JS = "<script type='text/javascript' src='file:///android_asset/js/explain.js'></script>";
    public static final String PRIMARY_EXPLAIN_JS = "<script type='text/javascript' src='file:///android_asset/js/primary_explain.js'></script>";
    public static final String QUESTION_JS = "<script type='text/javascript' src='file:///android_asset/js/question.js'></script>";
    public static final String SHOW_EXPLAIN_JS = "<script type='text/javascript' src='file:///android_asset/js/show-explain.js'></script>";
    public static final String QUESTION_DETAIL_JS = "<script type='text/javascript' src='file:///android_asset/js/question_detail.js'></script>";
    public static final String SUBSIDIARY_BOOK_JS = "<script type='text/javascript' src='file:///android_asset/js/subsidiary.js'></script>";
    public static final String HTML_HEAD_END = "<title>无标题文档</title></head><body>";
    public static final String HTML_END = "</body></html>";
    public static final String P_START_TAG = "<p>";
    public static final String P_INDENT_START_TAG = "<p>";
    public static final String P_END_TAG = "</p>";
    public static final String SPAN_BLUE_START_TAG = "<span style='color:blue'>";
    public static final String SPAN_RED_START_TAG = "<span style='color:red'>";
    public static final String SPAN_END_TAG = "</span>";
    public static final String RUBY_START_TAG = "<ruby>";
    public static final String RUBY_END_TAG = "</ruby>";
    public static final String RT_START_TAG = "<rt>";
    public static final String RT_END_TAG = "</rt>";
    public static final String BR_TAG = "<br/>";
    
    public static final String MATHJAX_CONFIG = "<script type='text/x-mathjax-config'>MathJax.Hub.Register.MessageHook('TeX Jax - parse error', function (data) {});</script>";
    public static final String MATHJAX_JS = "<script type='text/javascript' src='file:///android_asset/js/MathJax.js?config=TeX-AMS-MML_HTMLorMML'></script><script type="+
    "'text/x-mathjax-config'>MathJax.Hub.Config({tex2jax: {inlineMath: [['$','$'], ['\\\\(','\\\\)']]},'HTML-CSS': { preferredFont: 'TeX', availableFonts: ['TeX'], EqnChunk: 40,EqnChunkDelay: 80, scale: 100},jax: ['input/TeX', 'output/HTML-CSS']});</script>"+MATHJAX_CONFIG;
    
        
    public static final String POPUP_DIV_CSS = "<link rel='stylesheet' type='text/css' href='file:///android_asset/css/popup_div.css'>";
    
    public static final String HL_PART_IMG_SRC = "file:///android_asset/icon/hl_part_nor.png";
    public static final String HL_SENTENCE_IMG_SRC = "file:///android_asset/icon/hl_sentence_nor.png";
    public static final String HL_PARAGRAPH_IMG_SRC = "file:///android_asset/icon/hl_mpr_nor.png";
    
    public static final String MATH_SEPARATOR = "\\$";
    public static final String MATH_START_REPLACE_REGEX = "<tex data-latex=\"";
    public static final String MATH_END_REPLACE_REGEX ="\"></tex>";
    public static final String AUDIO_TAG_REGEX = "<audio.*?</audio>";
    /**中文空格点位符*/
	public static final String PLACEHOLDER_CN = "&#12288;";
    
    /**加点字*/
    public static final String UNDER_POINT_STYLE = "'display: inline-block;background:url(file:///android_asset/icon/dot.png);background-position: 0px 1.5em;background-size: 20px 5px;background-repeat:repeat-x;height: 2.0em;'";
    
    public static enum ShowType
    {
    	QUESTION,
    	EXAMPAPER
    }
    
    public static String tohtml(ArrayList<Section> sections)
    {
    	StringBuilder htmlBuilder = new StringBuilder();
    	htmlBuilder.append(HTML_HEAD);
    	htmlBuilder.append(makeExplainCss());
    	htmlBuilder.append(JQUERY);
    	htmlBuilder.append(EXPLAIN_JS);
    	htmlBuilder.append(HTML_HEAD_END);
    	for(Section section : sections)
    	{
    		if(section.isHasPart())
    		{
    			ArrayList<Part> parts = section.getPartArrayList();
    			for(Part part : parts)
    			{
//    				if(part.isHasHl())
//    				{
//    					htmlBuilder.append(P_INDENT_START_TAG);
//    	    			makeHtmlFromHL(htmlBuilder, part.getHlArrayList());
//    	    			htmlBuilder.append(P_END_TAG);
//    				}
//    				else
    				{
//    					htmlBuilder.append(P_INDENT_START_TAG);
    	    			htmlBuilder.append(part.toString());
//    	    			htmlBuilder.append(P_END_TAG);
					}
	    			
    			}
    		}
    		else
    		{
    			if(!TextUtils.isEmpty(section.getName()))
    			{
	    			htmlBuilder.append(P_START_TAG);
	    			htmlBuilder.append(section.getName());
	    			htmlBuilder.append(P_END_TAG);
    			}
			}
    	}
    	htmlBuilder.append(HTML_END);
    	return htmlBuilder.toString();
    }
    
    public static void makeHtmlFromHL(StringBuilder htmlBuilder, ArrayList<HL> hls)
	{
    	for (HL hl :hls)
		{
			if("13".equalsIgnoreCase(hl.getType()))
			{
				htmlBuilder.append(SPAN_BLUE_START_TAG);
				htmlBuilder.append(hl.getHlContent());
				htmlBuilder.append(SPAN_END_TAG);
			}
			else if("15".equalsIgnoreCase(hl.getType()))
			{
				htmlBuilder.append(SPAN_RED_START_TAG);
				htmlBuilder.append(hl.getHlContent());
				htmlBuilder.append(SPAN_END_TAG);
			}
		}
	}
    
    public static String makeHtmlFromPrimaryHL(PrimarySection.T.HL hl)
	{
    	StringBuilder htmlBuilder = new StringBuilder();
		if("13".equalsIgnoreCase(hl.getType()))
		{			
//			htmlBuilder.append("<span class='hl hl-13'" + "id='"+hl.getId()+"'>");
			htmlBuilder.append(hl.getHlContent());
//			htmlBuilder.append(SPAN_END_TAG);
		}
		else if("15".equalsIgnoreCase(hl.getType()))
		{
//			htmlBuilder.append(SPAN_RED_START_TAG);
			htmlBuilder.append("<span class='hl hl-15'" + "id='"+hl.getId()+"'>");
			htmlBuilder.append(hl.getHlContent());
			htmlBuilder.append(SPAN_END_TAG);
		}
		else if ("12".equalsIgnoreCase(hl.getType())) 
		{
			htmlBuilder.append("<span class='hl hl-12' id='"+hl.getId()+"'></span>");
		}
		else if ("11".equalsIgnoreCase(hl.getType())) 
		{
//			htmlBuilder.append("<span class='hl hl-11' id='"+hl.getId()+"'></span>");
		}
		else if ("14".equalsIgnoreCase(hl.getType())) 
		{
			htmlBuilder.append("<span class='hl hl-14'" + "id='"+hl.getId()+"'>");
			htmlBuilder.append(hl.getHlContent());
			htmlBuilder.append(SPAN_END_TAG);
		}
		return htmlBuilder.toString();
		
	}
    
    public static HashMap<String, Object> PrimarySectionTohtml(ArrayList<PrimarySection> sections)
    {
    	StringBuilder htmlBuilder = new StringBuilder();
    	htmlBuilder.append(HTML_HEAD);
    	htmlBuilder.append(makeExplainCss());
    	htmlBuilder.append(JQUERY);
    	htmlBuilder.append(PRIMARY_EXPLAIN_JS);
    	htmlBuilder.append(HTML_HEAD_END);
    	int i = 0;
    	int k = 0;
    	HashMap<String, Object> sectionHashMap = new HashMap<String, Object>();
    	ArrayList<String> soundArrayList = new ArrayList<String>();
    	ArrayList<Integer> sectionSpnSize = new ArrayList<Integer>();
    	for (PrimarySection primarySection : sections)
		{
			int n = 0;
			for (T t : primarySection.getSectionT())
			{

				if("\n".equals(t.getContent()) || BR_TAG.equals(t.getContent()))
				{
//					if(n == 0)
//					{
//						htmlBuilder.append(BR_TAG);
//					}
//					n++;
					continue;
				}
				n = 0;
				ArrayList<SPH> sphArrayList = t.getSph();
				String align = makeAlignString(t);
				if(sphArrayList.size() > 0)
				{
					if(k != 0)
					{
						htmlBuilder.append("</div>");
					}
					htmlBuilder.append("<div id='div-"+k+"'>");
					int soundSize = 0;
					for (SPH sph : sphArrayList)
					{
						String snd = sph.mSnd;
						if(!TextUtils.isEmpty(snd))
						{
							String[] sndPath = snd.split("\\|");
//							if(i == 0 && "1".equalsIgnoreCase(sph.mType))
//							{
//								htmlBuilder.append("<button class='read_textbook_button' id='read-0-0'></button>");
//								htmlBuilder.append(align);
//							}
//							else
//							{
//								htmlBuilder.append(align);
//								if("1".equalsIgnoreCase(sph.mType))
//								{
//									htmlBuilder.append("<button class='read_button' id='read-"+i+'-'+sndPath.length+"'></button>");
//								}
//							}
							for (int j = 0; j < sndPath.length; j++)
							{
								soundArrayList.add(sndPath[j]);
								i++;
							}
							soundSize += sndPath.length;
						}
					}
					k++;
					sectionSpnSize.add(soundSize);
					htmlBuilder.append(align);
				}
				else
				{
					htmlBuilder.append(align);
//					sectionSpnSize.add(0);
				}
				htmlBuilder.append(t.getContent());
				if(align != "")
				{
					htmlBuilder.append("</p>");
				}								
			}
			htmlBuilder.append("</div>");
		}
    	htmlBuilder.append(HTML_END);
    	sectionHashMap.put("html", htmlBuilder.toString());
    	sectionHashMap.put("sound", soundArrayList);
    	sectionHashMap.put("soundSize", sectionSpnSize);
    	return sectionHashMap;
    }
    
    private static String makeAlignString(T t)
    {
		String align = "";
		String attr = t.getAlign();
		if("1".equalsIgnoreCase(attr))
		{
			align = "<p style = 'text-align:center'>";
		}
		else if("2".equalsIgnoreCase(attr))
		{
			align = "<p style = 'text-align:right'>";
		}
		else if(t.getIndentation() != null/* && !t.isShowSndIcon()*/)
		{
			align = "<p style = 'text-indent: 2em'>";
		}
		return align;
    }
    
    public static String makeHtmlFromHL(HL hl)
	{
    	StringBuilder htmlBuilder = new StringBuilder();
		if("13".equalsIgnoreCase(hl.getType()))
		{			
//			htmlBuilder.append("<span class='hl hl-13'" + "id='"+hl.getId()+"'>");
			htmlBuilder.append(hl.getHlContent());
//			htmlBuilder.append(SPAN_END_TAG);
		}
		else if("15".equalsIgnoreCase(hl.getType()))
		{
//			htmlBuilder.append(SPAN_RED_START_TAG);
			htmlBuilder.append("<span class='hl hl-15'" + "id='"+hl.getId()+"'>");
			htmlBuilder.append(hl.getHlContent());
			htmlBuilder.append(SPAN_END_TAG);
		}
		else if ("12".equalsIgnoreCase(hl.getType())) 
		{
			htmlBuilder.append("<span class='hl hl-12' id='"+hl.getId()+"'></span>");
		}
		else if ("11".equalsIgnoreCase(hl.getType())) 
		{
//			htmlBuilder.append("<span class='hl hl-11'" + "id='"+hl.getId()+"'>");
		}
		else if ("14".equalsIgnoreCase(hl.getType())) 
		{
			htmlBuilder.append("<span class='hl hl-13'" + "id='"+hl.getId()+"'>");
			htmlBuilder.append(hl.getHlContent());
			htmlBuilder.append(SPAN_END_TAG);
		}
		return htmlBuilder.toString();
		
	}
    
    public static String makeExplainCss()
    {
    	float deviceScale = MyApplication.mDeviceScale;
    	if(deviceScale < 1.5f)
    	{
    		return EXPLAIN_1_0_CSS;
    	}
    	else if (deviceScale < 2.0f) 
    	{
    		return EXPLAIN_1_5_CSS;
		}
    	else if (deviceScale < 2.5f) 
    	{
    		return EXPLAIN_2_0_CSS;
		}
    	else if (deviceScale < 3.0f) 
    	{
    		return EXPLAIN_2_5_CSS;
		}
    	else 
    	{
    		return EXPLAIN_3_0_CSS;
		}
    }
    
    public static String makeQuestionCss()
    {
    	float deviceScale = MyApplication.mDeviceScale;
    	if(deviceScale < 1.5f)
    	{
    		return QUESTION_1_0_CSS;
    	}
    	else if (deviceScale < 2.0f) 
    	{
    		return QUESTION_1_5_CSS;
		}
    	else if (deviceScale < 2.5f) 
    	{
    		return QUESTION_2_0_CSS;
		}
    	else if (deviceScale < 3.0f) 
    	{
    		return QUESTION_2_5_CSS;
		}
    	else 
    	{
    		return QUESTION_3_0_CSS;
		}
    }
    
    public static String makeSubsidiaryBookCss()
    {
    	float deviceScale = MyApplication.mDeviceScale;
    	if(deviceScale < 1.5f)
    	{
    		return SUBSIDIARY_BOOK_1_0_CSS;
    	}
    	else if (deviceScale < 2.0f) 
    	{
    		return SUBSIDIARY_BOOK_1_5_CSS;
		}
    	else if (deviceScale < 2.5f) 
    	{
    		return SUBSIDIARY_BOOK_2_0_CSS;
		}
    	else if (deviceScale < 3.0f) 
    	{
    		return SUBSIDIARY_BOOK_2_5_CSS;
		}
    	else 
    	{
    		return SUBSIDIARY_BOOK_3_0_CSS;
		}
    }
    
    public static String makeQuestionDetailCss()
    {
    	float deviceScale = MyApplication.mDeviceScale;
    	if(deviceScale < 1.5f)
    	{
    		return QUESTION_DETAIL_1_0_CSS;
    	}
    	else if (deviceScale < 2.0f) 
    	{
    		return QUESTION_DETAIL_1_5_CSS;
		}
    	else if (deviceScale < 2.5f) 
    	{
    		return QUESTION_DETAIL_2_0_CSS;
		}
    	else if (deviceScale < 3.0f) 
    	{
    		return QUESTION_DETAIL_2_5_CSS;
		}
    	else 
    	{
    		return QUESTION_DETAIL_3_0_CSS;
		}
    }
    
}
