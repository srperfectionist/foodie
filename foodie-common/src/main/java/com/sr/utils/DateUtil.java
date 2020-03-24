package com.sr.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Date;

/**
 * @author SR
 * @date 2017/11/22
 */
public class DateUtil {

    public static final String STANDARD_FORMATTER = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_PATTER = "yyyyMMddHHmmss";
    
    /**
     * 字符串转Date
     *
     * @param dateTimeStr
     * @return
     */
    public static Date strToDate(String dateTimeStr) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMATTER);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    /**
     * Date转字符串
     *
     * @param date
     * @return
     */
    public static String dateToString(Date date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMATTER);
    }

    /**
     * 字符串转Date
     *
     * @param dateTimeStr
     * @param formatterStr
     * @return
     */
    public static Date strToDate(String dateTimeStr, String formatterStr) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatterStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    /**
     * Date转字符串
     *
     * @param date
     * @param formatterStr
     * @return
     */
    public static String dateToString(Date date, String formatterStr) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatterStr);
    }

    public static final int daysBetween(Date early, Date late){
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(early);
        calendar2.setTime(late);

        return daysBetween(calendar1, calendar2);
    }

    public static final int daysBetween(Calendar early, Calendar late){
        return (int)(toJuLian(late) - toJuLian((early)));
    }

    public static final float toJuLian(Calendar calendar){
        int Y = calendar.get(Calendar.YEAR);
        int M = calendar.get(Calendar.MARCH);
        int D = calendar.get(Calendar.DATE);
        int A = Y / 100;
        int B = A / 4;
        int C = 2 - A + B;
        float E = (int)(365.25f * (Y + 4716));
        float F = (int)(30.6001f * (M + 1));
        float JD = C + D + E + F - 1524.5f;

        return JD;
    }

    public static Date getCurrentDateTime(){
        Calendar calNow = Calendar.getInstance();
        Date dtNow = calNow.getTime();

        return dtNow;
    }

    public static String getCurrentDateString(String pattern){
        return dateToString(getCurrentDateTime(), pattern);
    }
}
