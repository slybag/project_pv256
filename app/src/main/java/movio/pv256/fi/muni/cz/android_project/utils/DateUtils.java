package movio.pv256.fi.muni.cz.android_project.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Michal on 30. 1. 2016.
 */
@SuppressWarnings("unused")
public class DateUtils extends android.text.format.DateUtils {

    public static final String DEFAULT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DEFAULT_DAY = "yyyy-MM-dd";
    public static final String HOUR_MINUTE = "HH:mm";
    public static final String HOUR_MINUTE_SECONDS = "HH:mm:ss";

    public static final String DAY_MONTH = "dd.MM.";

    public static final String WEEKDAY = "EEEE";

    /**
     * Converts Calendar object to string.
     *
     * @param date Calendar object.
     * @param format Used format.
     *
     * @return Date string.
     */
    @Nullable
    public static String formatCalendar(@Nullable Calendar date, @NonNull String format) {
        if (date == null) {
            return null;
        }
        return DateFormat.format(format, date).toString();
    }

    /**
     * Converts Calendar object to locale specific date string.
     *
     * @param date Date object.
     * @param context Application context. (Nullable, cause DateFormat do not use it :))
     *
     * @return Date string.
     */
    @Nullable
    public static String formatCalendar(@Nullable Calendar date, @Nullable Context context) {
        if (date == null) {
            return null;
        }
        java.text.DateFormat formatter = DateFormat.getDateFormat(context);
        return formatter.format(date.getTime());
    }

    /**
     * Parse date from string, using default pattern.
     *
     * @param date String with date in format yyyy-MM-dd'T'HH:mm:ss
     *
     * @return Timestamp
     */
    @NonNull
    public static Long parseDate(@Nullable String date) {
        Long timestamp;
        try {
            timestamp =
                    new SimpleDateFormat(DEFAULT, new Locale("cs", "CZ")).parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            timestamp = 0L;
        }
        return timestamp;
    }

    /**
     * Parse date from string, using default pattern.
     *
     * @param date String with date in format yyyy-MM-dd'T'HH:mm:ss
     *
     * @return Timestamp
     */
    @NonNull
    public static Long parseDate(@NonNull String format, @Nullable String date) {
        Long timestamp;
        try {
            timestamp =
                    new SimpleDateFormat(format, new Locale("cs", "CZ")).parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            timestamp = 0L;
        }
        return timestamp;
    }

    /**
     * Parse date from string, using default time pattern.
     *
     * @param time String with date in format HH:mm
     *
     * @return Timestamp
     */
    @NonNull
    public static Long parseTime(@Nullable String time) {
        Long timestamp;
        try {
            timestamp =
                    new SimpleDateFormat(HOUR_MINUTE_SECONDS, new Locale("cs", "CZ")).parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            timestamp = 0L;
        }
        return timestamp;
    }

    /**
     * Format timestamp to given format
     *
     * @param format String with format string
     * @param time Timestamp
     *
     * @return Formatted time
     */
    @NonNull
    public static String format(@NonNull String format, @Nullable Long time) {
        if (time == null) {
            return "00:00";
        }
        return new SimpleDateFormat(format, new Locale("cs", "CZ")).format(time)
                .toUpperCase(new Locale("cs", "CZ"));
    }

    /**
     * Get days left till Given Timestamp
     *
     * @param startUtc Timestamp
     *
     * @return Days left (floor)
     */
    public static long getDaysTo(@Nullable Long startUtc) {
        if (startUtc == null) {
            return 0;
        }
        return (startUtc - System.currentTimeMillis()) / DAY_IN_MILLIS;
    }

    public enum Type {
        DAY
    }

    public static long getMilis(Type type, int value) {
        switch (type) {
            case DAY:
                return DAY_IN_MILLIS * value;
        }
        return 0l;
    }

}
