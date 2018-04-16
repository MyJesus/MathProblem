package com.readboy.textbook.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexStringUtils
{

	/**
	 * 将String中的所有regex匹配的字串全部替换掉
	 * 
	 * @param string
	 *            代替换的字符串
	 * @param regex
	 *            替换查找的正则表达式
	 * @param replacement
	 *            替换函数
	 * @return
	 */
	public static String replaceAll(String string, String regex, ReplaceCallBack replacement)
	{
		return replaceAll(string, Pattern.compile(regex), replacement);
	}

	/**
	 * 将String中的所有pattern匹配的字串替换掉
	 * 
	 * @param string
	 *            代替换的字符串
	 * @param pattern
	 *            替换查找的正则表达式对象
	 * @param replacement
	 *            替换函数
	 * @return
	 */
	public static String replaceAll(String string, Pattern pattern, ReplaceCallBack replacement)
	{
		if (string == null)
		{
			return null;
		}
		Matcher m = pattern.matcher(string);
		if (m.find())
		{
			StringBuffer sb = new StringBuffer();
			int index = 0;
			while (true)
			{
				m.appendReplacement(sb, replacement.replace(m.group(0), index++, m));
				if (!m.find())
				{
					break;
				}
			}
			m.appendTail(sb);
			return sb.toString();
		}
		return string;
	}

	/**
	 * 将String中的regex第一次匹配的字串替换掉
	 * 
	 * @param string
	 *            代替换的字符串
	 * @param regex
	 *            替换查找的正则表达式
	 * @param replacement
	 *            替换函数
	 * @return
	 */
	public static String replaceFirst(String string, String regex, ReplaceCallBack replacement)
	{
		return replaceFirst(string, Pattern.compile(regex), replacement);
	}

	/**
	 * 将String中的pattern第一次匹配的字串替换掉
	 * 
	 * @param string
	 *            代替换的字符串
	 * @param pattern
	 *            替换查找的正则表达式对象
	 * @param replacement
	 *            替换函数
	 * @return
	 */
	public static String replaceFirst(String string, Pattern pattern, ReplaceCallBack replacement)
	{
		if (string == null)
		{
			return null;
		}
		Matcher m = pattern.matcher(string);
		StringBuffer sb = new StringBuffer();
		if (m.find())
		{
			m.appendReplacement(sb, replacement.replace(m.group(0), 0, m));
		}
		m.appendTail(sb);
		return sb.toString();
	}

}
