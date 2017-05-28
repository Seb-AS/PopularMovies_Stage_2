package com.sebas.popularmovies.utilities;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sebas on 5/24/17.
 */

public final class DateUtils {

    private static final String TAG = DateUtils.class.getSimpleName();

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * This method returns a year string from an input date string.
     *
     * @param dateString The date string to get the year.
     * @return Year as string.
     */
    public static String getYearFromDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        try {
            Date date = dateFormat.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.YEAR) + "";
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return dateString;
        }
    }

}
