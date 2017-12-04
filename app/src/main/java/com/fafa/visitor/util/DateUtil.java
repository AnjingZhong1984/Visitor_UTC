package com.fafa.visitor.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ZhouBing on 2017/7/19.
 */
public class DateUtil {

    private static SimpleDateFormat sdf;

    public static String DateToStr(Date date){

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        return sdf.format(date);
    }
    public static String DateToStr(Date date,String pattern){

        sdf = new SimpleDateFormat(pattern);

        return sdf.format(date);
    }

    public static Date StrToDate(String dateStr){

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Date StrToDate(String dateStr,String pattern){

        sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得当前时间字符串 格式  yyyy-MM-dd HH:mm:ss.SSS
     * @return
     */
    public static String getCurDateStr(){

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        return  sdf.format(new Date());
    }
    /**
     * 获得当前时间字符串 格式
     * @return
     */
    public static String getCurDateStr(String partner){

        sdf = new SimpleDateFormat(partner);

        return  sdf.format(new Date());
    }

}
