package com.readboy.mathproblem.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by oubin on 2017/11/23.
 */

public final class HtmlRegexUtils {
    /**
     * 过滤所有以<开头以>结尾的标签
     */
    private final static String REGEX_FOR_HTML = "<([^>]*)>";

    private final static String REGEX_FOR_IMG_TAG = "<\\s*img\\s+([^>]*)\\s*>"; // 找出IMG标签

    /**
     * 找出IMG标签的SRC属性
     */
    private final static String REGEX_FOR_IMA_TAG_SRC_ATTRIB = "src=\"([^\"]+)\"";

    private HtmlRegexUtils() throws IllegalAccessException {
        throw new IllegalAccessException("u can not create me...");
//        throw new RuntimeException("");
    }

    /**
     * 基本功能：替换标记以正常显示
     * <p>
     *
     * @return String
     */
    public String replaceTag(String input) {
        if (!hasSpecialChars(input)) {
            return input;
        }
        StringBuilder filtered = new StringBuilder(input.length());
        char c;
        for (int i = 0; i <= input.length() - 1; i++) {
            c = input.charAt(i);
            switch (c) {
                case '<':
                    filtered.append("&lt;");
                    break;
                case '>':
                    filtered.append("&gt;");
                    break;
                case '"':
                    filtered.append("&quot;");
                    break;
                case '&':
                    filtered.append("&amp;");
                    break;
                default:
                    filtered.append(c);
            }

        }
        return (filtered.toString());
    }

    /**
     * 基本功能：判断标记是否存在
     * <p>
     *
     * @param input 标签， 如p标签
     * @return boolean
     */
    public boolean hasSpecialChars(String input) {
        boolean flag = false;
        if ((input != null) && (input.length() > 0)) {
            char c;
            for (int i = 0; i <= input.length() - 1; i++) {
                c = input.charAt(i);
                switch (c) {
                    case '>':
                        flag = true;
                        break;
                    case '<':
                        flag = true;
                        break;
                    case '"':
                        flag = true;
                        break;
                    case '&':
                        flag = true;
                        break;
                    default:
                        break;
                }
            }
        }
        return flag;
    }

    /**
     * 基本功能：过滤所有以"<"开头以">"结尾的标签
     * <p>
     *
     * @param str
     * @return String
     */
    public static String filterHtml(String str) {
        Pattern pattern = Pattern.compile(REGEX_FOR_HTML);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result1 = matcher.find();
        while (result1) {
            matcher.appendReplacement(sb, "");
            result1 = matcher.find();
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 基本功能：过滤指定标签
     * <p>
     *
     * @param str
     * @param tag 指定标签
     * @return String
     */
    public static String filterHtmlTag(String str, String tag) {
        String regex = "<\\s*" + tag + "+\\s*([^>]*)\\s*>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result1 = matcher.find();
        while (result1) {
            matcher.appendReplacement(sb, "");
            result1 = matcher.find();
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String filterHtmlPTag(String source){
        String regex = "<\\s*p+\\s*([^>]*)\\s*>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        StringBuffer sb = new StringBuffer();
        boolean result1 = matcher.find();
        while (result1) {
            matcher.appendReplacement(sb, "");
            result1 = matcher.find();
        }
        matcher.appendTail(sb);
        String endRegex = "</p>";
        return sb.toString().replaceAll(endRegex, "\n");
    }

    /**
     * 基本功能：替换指定的标签
     * <p>
     *
     * @param str
     * @param beforeTag 要替换的标签
     * @param tagAttribute 要替换的标签属性值
     * @param startTag  新标签开始标记
     * @param endTag    新标签结束标记
     * @return String
     * 如：替换img标签的src属性值为[img]属性值[/img]
     */
    public static String replaceHtmlTag(String str, String beforeTag,
                                        String tagAttribute, String startTag, String endTag) {
        String regexForTag = "<\\s*" + beforeTag + "\\s+([^>]*)\\s*>";
        String regexForTagAttribute = tagAttribute + "=\"([^\"]+)\"";
        Pattern patternForTag = Pattern.compile(regexForTag);
        Pattern patternForAttribute = Pattern.compile(regexForTagAttribute);
        Matcher matcherForTag = patternForTag.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result = matcherForTag.find();
        while (result) {
            StringBuffer buffer = new StringBuffer();
            Matcher matcherForAttribute = patternForAttribute.matcher(matcherForTag
                    .group(1));
            if (matcherForAttribute.find()) {
                matcherForAttribute.appendReplacement(buffer, startTag
                        + matcherForAttribute.group(1) + endTag);
            }
            matcherForTag.appendReplacement(sb, buffer.toString());
            result = matcherForTag.find();
        }
        matcherForTag.appendTail(sb);
        return sb.toString();
    }
}
