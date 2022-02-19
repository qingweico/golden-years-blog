package cn.qingweico.util;

import org.apache.commons.lang3.StringUtils;

import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期工具类
 *
 * @author zqw
 * @date 2021/9/6
 */
public final class DateUtils {

    /**
     * Base ISO 8601 Date format yyyyMMdd i.e., 20021225 for the 25th day of December in the year 2002
     */
    public static final String ISO_DATE_FORMAT = "yyyyMMdd";

    /**
     * Expanded ISO 8601 Date format yyyy-MM-dd i.e., 2002-12-25 for the 25th day of December in the year 2002
     */
    public static final String ISO_EXPANDED_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * yyyy-MM-dd hh:mm:ss
     */
    public static String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static String DATE_PATTERN = "yyyyMMddHHmmss";

    /**
     * 则个
     */
    private static final boolean LENIENT_DATE = false;


    private static final Random random = new Random();
    private static final int ID_BYTES = 10;

    public synchronized static String generateId() {
        StringBuilder result = new StringBuilder();
        result.append(System.currentTimeMillis());
        for (int i = 0; i < ID_BYTES; i++) {
            result.append(random.nextInt(10));
        }
        return result.toString();
    }

    protected static float normalizedJulian(float JD) {

        return Math.round(JD + 0.5f) - 0.5f;
    }

    /**
     * Returns the Date from a julian. The Julian date will be converted to noon GMT,
     * such that it matches the nearest half-integer (i.e., a julian date of 1.4 gets
     * changed to 1.5, and 0.9 gets changed to 0.5.)
     *
     * @param JD the Julian date
     * @return the Gregorian date
     */
    public static Date toDate(float JD) {

        /* To convert a Julian Day Number to a Gregorian date, assume that it is for 0 hours, Greenwich time (so
         * that it ends in 0.5). Do the following calculations, again dropping the fractional part of all
         * multiplications and divisions. Note: This method will not give dates accurately on the
         * Gregorian Proleptic Calendar, i.e., the calendar you get by extending the Gregorian
         * calendar backwards to years earlier than 1582. using the Gregorian leap year
         * rules. In particular, the method fails if Y<400. */
        float Z = (normalizedJulian(JD)) + 0.5f;
        float W = (int) ((Z - 1867216.25f) / 36524.25f);
        float X = (int) (W / 4f);
        float A = Z + 1 + W - X;
        float B = A + 1524;
        float C = (int) ((B - 122.1) / 365.25);
        float D = (int) (365.25f * C);
        float E = (int) ((B - D) / 30.6001);
        float F = (int) (30.6001f * E);
        int day = (int) (B - D - F);
        int month = (int) (E - 1);

        if (month > 12) {
            month = month - 12;
        }

        int year = (int) (C - 4715); //(if Month is January or February) or C-4716 (otherwise)

        if (month > 2) {
            year--;
        }

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1); // damn 0 offsets
        c.set(Calendar.DATE, day);

        return c.getTime();
    }

    /**
     * Returns the days between two dates. Positive values indicate that
     * the second date is after the first, and negative values indicate, well,
     * the opposite. Relying on specific times is problematic.
     *
     * @param early the "first date"
     * @param late  the "second date"
     * @return the days between the two dates
     */
    public static int daysBetween(Date early, Date late) {

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(early);
        c2.setTime(late);

        return daysBetween(c1, c2);
    }

    /**
     * Returns the days between two dates. Positive values indicate that
     * the second date is after the first, and negative values indicate, well,
     * the opposite.
     *
     * @param early the one day
     * @param late  the other day
     * @return the days between two dates.
     */
    public static int daysBetween(Calendar early, Calendar late) {

        return (int) (toJulian(late) - toJulian(early));
    }

    /**
     * Return a Julian date based on the input parameter. This is
     * based from calculations found at
     * <a href="http://quasar.as.utexas.edu/BillInfo/JulianDatesG.html">Julian Day Calculations
     * (Gregorian Calendar)</a>, provided by Bill Jeffrys.
     *
     * @param c a calendar instance
     * @return the julian day number
     */
    public static float toJulian(Calendar c) {

        int Y = c.get(Calendar.YEAR);
        int M = c.get(Calendar.MONTH);
        int D = c.get(Calendar.DATE);
        int A = Y / 100;
        int B = A / 4;
        int C = 2 - A + B;
        float E = (int) (365.25f * (Y + 4716));
        float F = (int) (30.6001f * (M + 1));

        return C + D + E + F - 1524.5f;
    }

    /**
     * Return a Julian date based on the input parameter. This is
     * based from calculations found at
     * <a href="http://quasar.as.utexas.edu/BillInfo/JulianDatesG.html">Julian Day Calculations
     * (Gregorian Calendar)</a>, provided by Bill Jeffrys.
     *
     * @param date date
     * @return the julian day number
     */
    public static float toJulian(Date date) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return toJulian(c);
    }

    /**
     * @param isoString isoString
     * @param fmt       fmt
     * @param field     Calendar.YEAR/Calendar.MONTH/Calendar.DATE
     * @param amount    amount
     * @return String
     */
    public static String dateIncrease(String isoString, String fmt,
                                      int field, int amount) {

        try {
            Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(
                    "GMT"));
            cal.setTime(stringToDate(isoString, fmt, true));
            cal.add(field, amount);

            return dateToString(cal.getTime(), fmt);

        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Time Field Rolling function.
     * Rolls (up/down) a single unit of time on the given time field.
     *
     * @param isoString isoString
     * @param field     the time field.
     * @param up        Indicates if rolling up or rolling down the field value.
     * @param fmt       use formatting char's
     * @throws ParseException if an unknown field value is given.
     */
    public static String roll(String isoString, String fmt, int field,
                              boolean up) throws ParseException {

        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(
                "GMT"));
        cal.setTime(stringToDate(isoString, fmt));
        cal.roll(field, up);

        return dateToString(cal.getTime(), fmt);
    }

    /**
     * Time Field Rolling function.
     * Rolls (up/down) a single unit of time on the given time field.
     *
     * @param isoString isoString
     * @param field     the time field.
     * @param up        Indicates if rolling up or rolling down the field value.
     * @throws ParseException if an unknown field value is given.
     */
    public static String roll(String isoString, int field, boolean up) throws
            ParseException {

        return roll(isoString, DATETIME_PATTERN, field, up);
    }

    /**
     * java.util.Date
     *
     * @param dateText String
     * @param format   String
     * @param lenient  boolean
     * @return Date
     */
    public static Date stringToDate(String dateText, String format,
                                    boolean lenient) {

        if (dateText == null) {
            return null;
        }

        DateFormat df;

        try {

            if (format == null) {
                df = new SimpleDateFormat();
            } else {
                df = new SimpleDateFormat(format);
            }

            // setLenient avoids allowing dates like 9/32/2001
            // which would otherwise parse to 10/2/2001
            df.setLenient(false);

            return df.parse(dateText);
        } catch (ParseException e) {

            return null;
        }
    }

    /**
     * @return Timestamp
     */
    public static java.sql.Timestamp getCurrentTimestamp() {
        return new java.sql.Timestamp(new Date().getTime());
    }

    /**
     * java.util.Date
     *
     * @param dateString String
     * @param format     String
     * @return Date
     */
    public static Date stringToDate(String dateString, String format) {

        return stringToDate(dateString, format, LENIENT_DATE);
    }

    /**
     * java.util.Date
     *
     * @param dateString String
     */
    public static Date stringToDate(String dateString) {
        return stringToDate(dateString, ISO_EXPANDED_DATE_FORMAT, LENIENT_DATE);
    }

    /**
     * @param pattern Date
     * @param date    String
     */
    public static String dateToString(Date date, String pattern) {

        if (date == null) {
            return null;
        }

        try {

            SimpleDateFormat sfDate = new SimpleDateFormat(pattern);
            sfDate.setLenient(false);

            return sfDate.format(date);
        } catch (Exception e) {

            return null;
        }
    }

    /**
     * yyyy-MM-dd
     *
     * @param date Date
     * @return String
     */
    public static String dateToString(Date date) {
        return dateToString(date, ISO_EXPANDED_DATE_FORMAT);
    }

    /**
     * @return Date
     */
    public static Date getCurrentDateTime() {
        Calendar calNow = Calendar.getInstance();
        return calNow.getTime();
    }

    /**
     * @param pattern String
     * @return String
     */
    public static String getCurrentDateString(String pattern) {
        return dateToString(getCurrentDateTime(), pattern);
    }

    /**
     * yyyy-MM-dd
     *
     * @return String
     */
    public static String getCurrentDateString() {
        return dateToString(getCurrentDateTime(), ISO_EXPANDED_DATE_FORMAT);
    }

    /**
     * 返回固定格式的当前时间
     * yyyy-MM-dd hh:mm:ss
     *
     * @return String
     */
    public static String dateToStringWithTime() {

        return dateToString(new Date(), DATETIME_PATTERN);
    }


    /**
     * yyyy-MM-dd hh:mm:ss
     *
     * @param date Date
     * @return String
     */
    public static String dateToStringWithTime(Date date) {

        return dateToString(date, DATETIME_PATTERN);
    }
    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * @param date Date
     * @param days int
     * @return java.util.Date
     */
    public static Date dateIncreaseByDay(Date date, int days) {

        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(
                "GMT"));
        cal.setTime(date);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    /**
     * @param date Date
     * @param mnt  int
     * @return java.util.Date
     */
    public static Date dateIncreaseByMonth(Date date, int mnt) {

        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(
                "GMT"));
        cal.setTime(date);
        cal.add(Calendar.MONTH, mnt);

        return cal.getTime();
    }

    /**
     * @param date Date
     * @param mnt  int
     * @return java.util.Date
     */
    public static Date dateIncreaseByYear(Date date, int mnt) {

        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(
                "GMT"));
        cal.setTime(date);
        cal.add(Calendar.YEAR, mnt);

        return cal.getTime();
    }

    /**
     * @param date yyyy-MM-dd
     * @param days int
     * @return yyyy-MM-dd
     */
    public static String dateIncreaseByDay(String date, int days) {
        return dateIncreaseByDay(date, ISO_DATE_FORMAT, days);
    }

    /**
     * @param date String
     * @param fmt  String
     * @param days int
     * @return String
     */
    public static String dateIncreaseByDay(String date, String fmt, int days) {
        return dateIncrease(date, fmt, Calendar.DATE, days);
    }

    /**
     * @param src    String
     * @param srcFmt String
     * @param desFmt String
     * @return String
     */
    public static String stringToString(String src, String srcFmt,
                                        String desFmt) {
        return dateToString(stringToDate(src, srcFmt), desFmt);
    }

    /**
     * @param date Date
     * @return string
     */
    public static String getYear(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy");
        return formatter.format(date);
    }

    /**
     * @param date Date
     * @return string
     */
    public static String getMonth(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "MM");
        return formatter.format(date);
    }

    /**
     * @param date Date
     * @return string
     */
    public static String getDay(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd");
        return formatter.format(date);
    }

    public static int getDayInt(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd");
        String curDay = formatter.format(date);
        return Integer.parseInt(curDay);
    }

    /**
     * @param date Date
     * @return string
     */
    public static String getHour(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "HH");
        return formatter.format(date);
    }

    public static int getMinFromDate(Date dt) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dt);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        return ((hour * 60) + min);
    }

    /**
     * Function to convert String to Date Object. If invalid input then current or next day date
     * is returned (Added by Ali Naqvi on 2006-5-16).
     *
     * @param str      String input in YYYY-MM-DD HH:MM[:SS] format.
     * @param isExpiry boolean if set and input string is invalid then next day date is returned
     * @return Date
     */
    public static Date convertToDate(String str, boolean isExpiry) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dt;
        try {
            dt = fmt.parse(str);
        } catch (ParseException ex) {
            Calendar cal = Calendar.getInstance();
            if (isExpiry) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
            } else {
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
            }
            dt = cal.getTime();
        }
        return dt;
    }
    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    public static Date convertToDate(String str) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date dt;
        try {
            dt = fmt.parse(str);
        } catch (ParseException ex) {
            dt = new Date();
        }
        return dt;
    }

    public static String dateFormat(Date date, int minute) {
        String dateFormat;
        int year = Integer.parseInt(getYear(date));
        int month = Integer.parseInt(getMonth(date));
        int day = Integer.parseInt(getDay(date));
        int hour = minute / 60;
        int min = minute % 60;
        dateFormat = year
                +
                (month > 9 ? String.valueOf(month) :
                        "0" + month)
                +
                (day > 9 ? String.valueOf(day) : "0" + day)
                + " "
                +
                (hour > 9 ? String.valueOf(hour) : "0" + hour)
                +
                (min > 9 ? String.valueOf(min) : "0" + min)
                + "00";
        return dateFormat;
    }

    public static String sDateFormat() {
        return new SimpleDateFormat(DATE_PATTERN).format(Calendar.getInstance().getTime());
    }

    /**
     * 获得本月的第一天日期
     *
     * @return String
     */
    public static String getFirstDateOfThisMonth() {

        SimpleDateFormat format = new SimpleDateFormat(ISO_EXPANDED_DATE_FORMAT);
        Calendar calendarFirst = Calendar.getInstance();
        calendarFirst.add(Calendar.MONTH, 0);
        calendarFirst.set(Calendar.DAY_OF_MONTH, 1);
        return format.format(calendarFirst.getTime());
    }

    /**
     * 获得本月的最后一天日期
     *
     * @return String
     */
    public static String getLastDateOfThisMonth() {
        SimpleDateFormat format = new SimpleDateFormat(ISO_EXPANDED_DATE_FORMAT);

        Calendar calendarLast = Calendar.getInstance();
        calendarLast.setTime(new Date());
        calendarLast.getActualMaximum(Calendar.DAY_OF_MONTH);
        return format.format(calendarLast.getTime());
    }
    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 判断字符串日期是否匹配指定的格式化日期
     */
    public static boolean isValidDate(String strDate, String formatter) {
        SimpleDateFormat sdf;
        ParsePosition pos = new ParsePosition(0);

        if (StringUtils.isBlank(strDate) || StringUtils.isBlank(formatter)) {
            return false;
        }
        try {
            sdf = new SimpleDateFormat(formatter);
            sdf.setLenient(false);
            Date date = sdf.parse(strDate, pos);
            if (date == null) {
                return false;
            } else {
                return pos.getIndex() <= sdf.format(date).length();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
