/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.gehc.cpm.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 212579464
 */
public class DateTimeUtil {

    public static String MYSQL_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static String MYSQL_DATE_PATTERN = "yyyy-MM-dd";

    static Comparator<Date> dateComparator;

    public static Date parseDate(String timestr, String... formatArray) {
        if (null == timestr || timestr.isEmpty()) {
            return null;
        }
        Date time = null;
        for (String format : formatArray) {
            time = DateTimeUtil.getDateFromStr(timestr, format, true);
            if (time != null) {
                return time;
            }
        }
        return time;
    }

    public static Date getDateFromStr(String dateStr, String format, Boolean needLog) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            if (needLog) {
//                logger.error("time format exception ,str is " + dateStr + ",format is " + format);
            }
        }
        return null;
    }

    public static Date transformDate(Date date, String format) {
        if(date==null){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date res = null;
        try {
            res = sdf.parse(sdf.format(date));

        } catch (ParseException ex) {
            Logger.getLogger("can not transform date ["+ date + "]");
            Logger.getLogger(DateTimeUtil.class.getName()).log(Level.SEVERE, null, ex);
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static Date getEarlierDate(Date date1, Date date2) {
        if (null == date1) {
            return date2;
        }
        return date2 == null ? date1 : date1.after(date2) ? date2 : date1;
    }

    public static Date getLaterDate(Date date1, Date date2) {
        if (null == date1) {
            return date2;
        }
        return date2 == null ? date1 : date1.after(date2) ? date1 : date2;
    }

    public static String getStrDate(Date date, String format) {
        if (format == null) {
            format = "yyyy-MM-dd";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static Comparator<Date> getDateComparator() {
        if (dateComparator == null) {
            dateComparator = new Comparator<Date>() {
                @Override
                public int compare(Date o1, Date o2) {
                    return o1.compareTo(o2);
                }
            };
        }
        return dateComparator;
    }

    
    public static Date getDateFromSql(Object date){
        return date==null? null: parseDate(String.valueOf(date), MYSQL_TIME_PATTERN);
    }
    
}
