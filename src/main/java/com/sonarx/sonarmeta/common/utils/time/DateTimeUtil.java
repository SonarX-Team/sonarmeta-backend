package com.sonarx.sonarmeta.common.utils.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description: 时间日期工具类
 * @author: liuxuanming
 */

public class DateTimeUtil {

    /**
     * @description: 时间 yyyy-MM-dd 转字符串
     * @author: liuxuanming
     */
    public static String datetimeToString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    /**
     * @description: 字符串转时间 yyyy-MM-dd
     * @author: liuxuanming
     */
    public static Date stringToDatetime(String str) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(str);
    }

    public static Date getCurrentDateTime() {
        return Calendar.getInstance().getTime();
    }

    public static Date longToDatetime(Long l) throws ParseException {
        return stringToDatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(l)));
    }

    /**
     * @description: 获取 yyyy-MM-dd 字符串
     * @author: liuxuanming
     */
    public static String getCurrentDateString() {
        return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    }

    /**
     * @description: 获取 yyyy-MM-dd HH:mm:ss 字符串
     * @author: liuxuanming
     */
    public static String getCurrentDateTimeString() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    /**     
     * @description: 获取每日开始时间
     * @author: liuxuanming
     */
    public static Date atStartOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return localDateTimeToDate(startOfDay);
    }
    
    /**
     * @description: 获取每日结束时间
     * @author: liuxuanming
     */
    public static Date atEndOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
    }

    /**
     * @description: 日期到本地日期转换
     * @author: liuxuanming
     */
    private static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * @description: 本地日期到日期转换
     * @author: liuxuanming
     */
    private static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
