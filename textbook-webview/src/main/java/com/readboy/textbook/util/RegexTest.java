package com.readboy.textbook.util;

import java.util.HashMap;
import java.util.regex.Matcher;

public class RegexTest
{

	public void test(String string)
	{
		 String pattern = "(<PAR +.*?\\/>)([\\s\\S]*?)(?=<PAR +|$)";
		 String str = RegexStringUtils.replaceAll(string, pattern, new AbstractReplaceCallBack() 
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
		 
		 DebugLogger.getLogger().d(str);
		 
		 String testString = "<p>一个长方体的墨水盒长5.8 cm，宽5.5 cm，高6 cm，则它的表面积为<blk mlen=\"5\" mstyle=\"underline\"></blk>cm<sup>2</sup>。</p>";
		 testString = "<p>求该盒子的容积。</p><p>答：<blk mlen=\"44\" mstyle=\"underline\"></blk></p>";
		 pattern = "<blk mlen=\"(\\d*)\".*</blk>";
		 str = testString.replaceFirst(pattern, "<input maxlength='$1'></input>");
		 DebugLogger.getLogger().d(str);
		 str = RegexStringUtils.replaceAll(testString, pattern, new AbstractReplaceCallBack() 
		 {
			@Override
			public String doReplace(String text, int index, Matcher matcher)
			{
				return "<input maxlength=\"$1\"></input>";
			}
		 });
		 
		 DebugLogger.getLogger().d(str);

		 //过滤安全字符... TODO 应提取为一个方法
		 final HashMap<String, String> map = new HashMap<String, String>() {
		   private static final long serialVersionUID = 1L;
		   {
		      put("<", "&lt;");
		      put(">", "&gt;");
		      put("\"", "&quot;");
		      put("'", "&apos;");
		   }
		 };
		 ReplaceCallBack callBack = new ReplaceCallBack() {
		   public String replace(String text, int index, Matcher matcher) {
		     return map.get(text);
		  }
		 };
		 string = "<html><body>xxxxx 1<4 & 7>5</body></html>";
		 System.out.println(RegexStringUtils.replaceAll(string.replace("&", "&amp;"), 
		     "[<>\"\']", callBack));
		 // 输出: &lt;html&gt;&lt;body&gt;xxxxx 1&lt;4 &amp; 7&gt;5&lt;/body&gt;&lt;/html&gt;
	}
	

}
