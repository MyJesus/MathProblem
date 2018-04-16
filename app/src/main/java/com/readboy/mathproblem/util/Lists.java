package com.readboy.mathproblem.util;

import java.util.List;

/**
 * Created by oubin on 2017/9/19.
 */

public class Lists {

    public static <T> String unite(String regex, List<T> list) {
        StringBuilder builder = new StringBuilder();
        int size = list.size();
        if (size == 0) {
            return null;
        }
        for (int i = 0; ; i++) {
            builder.append(list.get(i));
            if (i == size - 1) {
                return builder.toString();
            }
            builder.append(regex);
        }
    }

    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    public static boolean isNotEmpty(List list) {
        return !isEmpty(list);
    }

}
