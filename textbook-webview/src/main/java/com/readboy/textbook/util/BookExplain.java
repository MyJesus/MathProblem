package com.readboy.textbook.util;

import java.util.regex.Matcher;

public class BookExplain
{
	public static final String REPLACE_PAR_TAG_PATTERN = "(<PAR +.*?\\/>)([\\s\\S]*?)(?=<PAR +|$)";
	public static final String REPLACE_HL_TAG_PATTERN = "<HL +type=\"([\\d]*)\" +id=\"([\\d]*)\">(.*?)<\\/HL>";
	public static final String REPLACE_P_TAG_PATTERN = "<P +src=\"([^>]+)\" *\\/>";
	public static final String REPLACE_CMT_TAG_PATTERN = "<Cmt +id=\"(\\d*)\" *>(.*?)<\\/Cmt>";
	public static final String REPLACE_E_TAG_PATTERN = "<E +v=\"([^>]+)\" *\\/>";
	
	/**
	 * PAR => p
	 * @param source
	 * @return
	 */
	public static String changeToHtmlPTag(String source)
	{
		String dest = RegexStringUtils.replaceAll(source, REPLACE_PAR_TAG_PATTERN, new AbstractReplaceCallBack() 
		 {
			@Override
			public String doReplace(String text, int index, Matcher matcher)
			{
				String classStr = "class='";
				classStr += $(1).replaceAll("flc=\"(\\d*)\"", "art-p-flc-$1");
				classStr = classStr.replaceAll("hc=\"(\\d*)\"", "art-p-hc-$1");
				classStr = classStr.replaceAll("lc=\"(\\d*)\"", "art-p-lc-$1");
				classStr = classStr.replace("<PAR", "");
				classStr = classStr.replace("/>", "");
				classStr += "'";
				return $(2) == null ? "" : "<p "+classStr+">"+ $(2) + "</p>";
			}
		 });
		return dest;
	}
	
	public static String changeToHtmlSpanTag(String source)
	{
		String dest = RegexStringUtils.replaceAll(source, REPLACE_HL_TAG_PATTERN, new AbstractReplaceCallBack() 
		 {
			@Override
			public String doReplace(String text, int index, Matcher matcher)
			{
				String typeClass="class='hl-type-"+$(1)+" hl'";
				String rel = " rel='"+$(2)+"'";
				return "<a "+typeClass + rel +">"+ $(3)+"</a>";
			}
		 });
		return dest;
	}
	
	public static String changeToHtmlImgTag(String source)
	{
		String dest = RegexStringUtils.replaceAll(source, REPLACE_P_TAG_PATTERN, new AbstractReplaceCallBack() 
		 {
			@Override
			public String doReplace(String text, int index, Matcher matcher)
			{
				return "<img class=\"img\" src=\""+$(1)+"\"/>";
			}
		 });
		return dest;
	}
	
	public static String changeCmtTagToHtmlSpanTag(String source)
	{
		String dest = RegexStringUtils.replaceAll(source, REPLACE_CMT_TAG_PATTERN, new AbstractReplaceCallBack() 
		 {
			@Override
			public String doReplace(String text, int index, Matcher matcher)
			{
				return "<span style='display:none' id='cmt-"+$(1)+"'>"+$(2)+"</span>";
			}
		 });
		return dest;
	}
	
	public static String changeETagToHtmlSpanTag(String source)
	{
		String dest = RegexStringUtils.replaceAll(source, REPLACE_E_TAG_PATTERN, new AbstractReplaceCallBack() 
		 {
			@Override
			public String doReplace(String text, int index, Matcher matcher)
			{
				return "<span class='E'>"+$(1)+"</span>";
			}
		 });
		return dest;
	}
}
