package com.readboy.mathproblem.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final String DATE_FORMAT_SECOND = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT_MINUTE = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_DAY = "yyyy-MM-dd";

    public static Date stringToDate(String dateStr, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        if (dateStr == null) {
            return new Date();
        }
        return sdf.parse(dateStr);
    }

    public static String dateToString(Date date){
        return dateToString(date, DATE_FORMAT_MINUTE);
    }

    public static String dateToString(Date date, String format) {
        String ret;
        if (format != null) {
            ret = new SimpleDateFormat(format, Locale.CHINA).format(date);
        } else {
            ret = new SimpleDateFormat(DATE_FORMAT_SECOND, Locale.CHINA).format(date);
        }
        return ret;
    }

    public static String getCurDateString() {
        Date date = new Date();
        return dateToString(date, null);
    }

    public static boolean isToday(String dateStr, String format) throws ParseException {
        Calendar calendar = stringToCalendar(dateStr, format);
        return isToday(calendar);
    }

    public static boolean isToday(Calendar calendar) {
        Calendar curCalendar = Calendar.getInstance();
        return curCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                && curCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                && curCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean isTomorrow(String dateStr, String format) throws ParseException {
        Calendar calendar = stringToCalendar(dateStr, format);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return isToday(calendar);
    }

    public static Calendar stringToCalendar(String dateStr) throws ParseException {
        return stringToCalendar(dateStr, DATE_FORMAT_SECOND);
    }

    public static Calendar stringToCalendar(String dateStr, String format) throws ParseException {
        Date date = stringToDate(dateStr, format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
