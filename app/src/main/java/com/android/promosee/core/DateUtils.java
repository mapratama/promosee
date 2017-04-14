package com.android.promosee.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by imam on 26/09/16.
 */
public class DateUtils {

    public static Date fromUnixTime(long unixTime){
        return new Date(unixTime * 1000L);
    }

    public static Date fromDateString(String dateTimeString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (dateTimeString.contains(" "))
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = simpleDateFormat.parse(dateTimeString);
            return date;
        }
        catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toISOString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date);
    }

    public static String toDisplayString(Date date) {
        return new SimpleDateFormat("dd MMM yyyy", Locale.US).format(date);
    }
}
