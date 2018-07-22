package com.blako.mensajero.Utils;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {
    public static String convertMillisecondsToDateTime(long dateInMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return formatter.format(new Date(dateInMillis));
    }

    public static String convertMillisecondsToHHmmss(long milliseconds) {
        long seconds = milliseconds/1000;
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", h,m,s);
    }

    public static String getTimestamp()
    {
        //DateFormat df = new DateFormat();
        //return df.format("yyyy-MM-dd-HH-mm-ss", new java.util.Date()).toString();
        return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new java.util.Date());
    }

    public static String getTimestampJustDate()
    {
        return new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
    }

    public static int getTimestampHours()
    {
        DateFormat df = new DateFormat();
        return Integer.valueOf(df.format("HH", new java.util.Date()).toString());
    }

    public static int getTimestampYear()
    {
        DateFormat df = new DateFormat();
        return Integer.valueOf(df.format("yyyy", new java.util.Date()).toString());
    }

    public static int getTimestampMonth()
    {
        DateFormat df = new DateFormat();
        return Integer.valueOf(df.format("MM", new java.util.Date()).toString());
    }

    public static int getTimestampDay()
    {
        DateFormat df = new DateFormat();
        return Integer.valueOf(df.format("dd", new java.util.Date()).toString());
    }

    public static String getTimestampWeekDay()
    {
        DateFormat df = new DateFormat();
        return df.format("E", new java.util.Date()).toString();
    }

    public static long getFullMillis()
    {
        long t = System.currentTimeMillis();
        return t;
    }

    public static int getMillis()   //  gets lsbs
    {
        long timebase = 10*60*1000;
        long t = System.currentTimeMillis();
        long t0  = t/timebase;
        return (int)(t - timebase*t0);
    }
}
