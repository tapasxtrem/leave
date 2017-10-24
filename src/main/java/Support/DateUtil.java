/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Support;

import DBRelated.DBPool;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author DELL
 */
public class DateUtil {

    public static String NToDT(long seconds) {
        try {
            Date date = new Date(seconds);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            String formattedTime = sdf.format(date);
            return formattedTime;
        } catch (Exception pe) {
            //RCConnector.allExceptionsInServer.writeException(pe);
            DBPool.log(pe);
            //System.out.println("Can not convert from Nmber to Time.");
        }
        return "";
    }

    public static String NToDTFromDate(long seconds) {
        try {
            Date date = new Date(seconds);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            String formattedTime = sdf.format(date) + " 00:00:01";
            return formattedTime;
        } catch (Exception pe) {
            //RCConnector.allExceptionsInServer.writeException(pe);
            DBPool.log(pe);
            //System.out.println("Can not convert from Nmber to Time.");
        }
        return "";
    }

    public static String NToDTToDate(long seconds) {
        try {
            Date date = new Date(seconds);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            String formattedTime = sdf.format(date) + " 23:59:30";

            return formattedTime;
        } catch (Exception pe) {
            DBPool.log(pe);
        }
        return "";
    }

    public static long secondsFROMDate(long seconds) {
        try {
            Date date = new Date(seconds);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            String formattedTime = sdf.format(date) + " 00:00:01";

            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            Date sourceDate = sdf1.parse(formattedTime);

            return sourceDate.getTime();
        } catch (Exception pe) {
            DBPool.log(pe);
        }
        return 0;
    }

    public static long secondsTODate(long seconds) {
        try {
            Date date = new Date(seconds);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            String formattedTime = sdf.format(date) + " 23:59:59";

            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            Date sourceDate = sdf1.parse(formattedTime);

            return sourceDate.getTime();
        } catch (Exception pe) {
            DBPool.log(pe);
        }
        return 0;
    }

    public static String ConvertDataFormat1(String sdate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            Date sourceDate = sdf.parse(sdate);

            SimpleDateFormat dsdf = new SimpleDateFormat("ddMMMYY HH:mm:ss");
            dsdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            String formattedTime = dsdf.format(sourceDate);
            return formattedTime;
        } catch (Exception pe) {
            DBPool.log(pe);
        }
        return sdate;
    }

    public static String ConvertDataFormat2(String sdate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyy");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            Date sourceDate = sdf.parse(sdate);

            SimpleDateFormat dsdf = new SimpleDateFormat("yyyy-MM-dd");
            dsdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

            String formattedTime = dsdf.format(sourceDate);
            return formattedTime;
        } catch (Exception pe) {
            DBPool.log(pe);
        }
        return sdate;
    }

    public static String NToDTLeaveDate(long seconds) {
        try {
            Date date = new Date(seconds);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            String formattedTime = sdf.format(date);
            return formattedTime;
        } catch (Exception pe) {
            //RCConnector.allExceptionsInServer.writeException(pe);
            DBPool.log(pe);
            //System.out.println("Can not convert from Nmber to Time.");
        }
        return "";
    }

    public static String NToDTLeaveNetReportDate(long seconds) {
        try {
            Date date = new Date(seconds);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            String formattedTime = sdf.format(date);
            return formattedTime;
        } catch (Exception pe) {
            //RCConnector.allExceptionsInServer.writeException(pe);
            DBPool.log(pe);
            //System.out.println("Can not convert from Nmber to Time.");
        }
        return "";
    }

    public static String LtoddMMMyy(long seconds) {
        try {
            Date date = new Date(seconds);
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyy");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            String formattedTime = sdf.format(date);
            return formattedTime;
        } catch (Exception pe) {
            DBPool.log(pe);
        }
        return "";
    }

    public static String LtoHHMMSS(long millis) {
        try {
            Date date = new Date(millis);
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            String dateFormatted = formatter.format(date);
            return dateFormatted;
        } catch (Exception pe) {
            DBPool.log(pe);
        }
        return "";
    }

    public static String LtoGetHours(long millis) {
        try {
            long hours = TimeUnit.MILLISECONDS.toHours(millis);
            millis -= TimeUnit.HOURS.toMillis(hours);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            millis -= TimeUnit.MINUTES.toMillis(minutes);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
            String hh = hours <10 ? "0"+hours : hours+"";
            String mm = minutes <10 ? "0"+minutes : minutes+"";
            String ss = seconds <10 ? "0"+seconds : seconds+"";

           return hh + ":" + mm + ":" + ss;
        } catch (Exception pe) {
            DBPool.log(pe);
        }
        return "";
    }
}
