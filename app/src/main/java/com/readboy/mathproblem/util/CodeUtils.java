package com.readboy.mathproblem.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by oubin on 2017/11/6.
 */

public class CodeUtils {

    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\s\\u4e00-\\u9fa5]+");

    public static String urlEncode(String str) {
        // 查找中文
//        Pattern p = Pattern.compile("[\\s\\u4e00-\\u9fa5]+");
        Matcher mat = CHINESE_PATTERN.matcher(str);
        String dst = str;
        while (mat.find()) {
            try {
                dst = str.replace(mat.group(),
                        URLEncoder.encode(mat.group(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return dst;
    }

}
