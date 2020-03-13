package com.sanjay.openfire.utilies;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateandTimeUtils {

    public static String SimpleDatetoLongDate(String dateString) {
        String formattedDate = null;
        try {
            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("E dd MMM yyyy hh.mm aa");
            Date date = originalFormat.parse(dateString);
            formattedDate = targetFormat.format(date);
        } catch (Exception ex) {
        }
        return formattedDate;
    }

    public static String currentDateTime() {
        DateFormat date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
// you can get seconds by adding  "...:ss" to it
        Date todayDate = new Date();
        return date.format(todayDate);
    }

    public long getTimeInMilliSec(String DateAndTime) {

        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Calendar calendar = new GregorianCalendar();
            TimeZone timeZone = calendar.getTimeZone();
            sdf.setTimeZone(timeZone);
            date = sdf.parse(DateAndTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return getTimeInMilliSec(currentDateTime());
        } else {
            System.out.println("in milliseconds: " + date.getTime());
            return date.getTime();
        }
    }

    public static Date getLastmessageDate(String lastmessagedate) {
        String formattedDate = null;
        Date date = null;
        try {
            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("YYYY-MM-DD");
            date = originalFormat.parse(lastmessagedate);
            formattedDate = targetFormat.format(date);
        } catch (Exception ex) {

        }
        return date;
    }
}
