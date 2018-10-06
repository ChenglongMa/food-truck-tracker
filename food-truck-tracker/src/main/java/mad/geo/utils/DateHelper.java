package mad.geo.utils;

import android.support.annotation.NonNull;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateHelper {
    private static final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, Locale.getDefault());

    /**
     * Convert date string to {@link Date}
     *
     * @param dateStr the string of date
     * @return the instance of {@link Date}
     * @throws ParseException
     */
    public static Date toDate(String dateStr) throws ParseException {
        return dateFormat.parse(dateStr);
    }

    /**
     * Convert the instance of {@link Date} to String
     *
     * @param date the instance of {@link Date}
     * @return the string of {@link Date}
     */
    public static String dateToString(Date date) {
        return dateFormat.format(date);
    }

    /**
     * Convert the {@link Date} to {@link java.sql.Date}
     * in order to save to database.
     *
     * @param date
     * @return
     */
    public static java.sql.Date toSqlDate(@NonNull Date date) {
        return new java.sql.Date(date.getTime());
    }

    /**
     * Convert the {@link Date} to {@link Timestamp}
     * to save the accurate date time.
     * @param date
     * @return
     */
    public static Timestamp toSqlTime(@NonNull Date date) {
        return new Timestamp(date.getTime());
    }
    /**
     * Advance the date in specific mins
     *
     * @param date
     * @param mins
     * @return
     */
    public static Date advancedDate(Date date, int mins) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + mins);
        return calendar.getTime();
    }
}
