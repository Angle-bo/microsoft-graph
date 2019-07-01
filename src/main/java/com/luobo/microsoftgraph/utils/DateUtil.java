package com.luobo.microsoftgraph.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Author: sp
 * Date: 2019/06/28
 * Describe: 获取日期时间工具类
 */
public class DateUtil {

    /**
     * 获取年份
     * @return
     */
    public static int getYear() {
        Calendar cal = Calendar.getInstance();
        return  cal.get(Calendar.YEAR);
    }

    /**
     * 获取日期
     * @return
     */
    public static int getMonth() {
        Calendar cal = Calendar.getInstance();
        return  cal.get(Calendar.MONTH)+1;
    }

    /**
     * 获取当前时间+天数后的日期
     * @param days
     * @return
     */
    public static Date plusHours(int days){
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        now.set(Calendar.DATE, now.get(Calendar.DATE) + days);//+后 -前
        return now.getTime();
    }

    /**
     * 格式化时间为 yyyy-MM-dd'T'HH:mm:ss'Z'
     * @param date
     * @return
     */
    public static String dfTZ(Date date){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return df.format(date);
    }

}
