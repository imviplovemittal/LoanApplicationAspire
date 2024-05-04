package com.aspireapp.loan.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TimeUtil {

    public static Long getDiffInHours(Date oldTime, Date newTime) {
        long milliseconds1 = oldTime.getTime();
        long milliseconds2 = newTime.getTime();
        long diff = milliseconds2 - milliseconds1;
        return diff / (60 * 60 * 1000);
    }

    public static Long getDiffInMinutes(Date oldTime, Date newTime) {
        long milliseconds1 = oldTime.getTime();
        long milliseconds2 = newTime.getTime();
        long diff = milliseconds2 - milliseconds1;
        return diff / (60 * 1000);
    }

    public static Long getDiffInSeconds(Date oldTime, Date newTime) {
        long milliseconds1 = oldTime.getTime();
        long milliseconds2 = newTime.getTime();
        long diff = milliseconds2 - milliseconds1;
        return diff / (1000);
    }

    public static Long getDiffInDays(Date oldTime, Date newTime) {
        return Duration.between(oldTime.toInstant(), newTime.toInstant()).toDays();
    }

    public static Date getStartDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static boolean isOnNewNBFCFlow(Date createdAt) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.DECEMBER, 10, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return createdAt.after(calendar.getTime());
    }

    public static Date getStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date time = calendar.getTime();
        calendar = null; // explicitly removing from memory
        return time;
    }

    public static Date getEndDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, 23);
        calendar.add(Calendar.MINUTE, 59);
        calendar.add(Calendar.SECOND, 59);
        calendar.add(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date getEndOfTheDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date getDatePlusXHrs(Date date, int hrs) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + hrs);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getDatePlus(Date date, int value, int timeUnit) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(timeUnit, value);
        return cal.getTime();
    }

    public static Date getDatePlusXMinutes(Date date, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minutes);
        return cal.getTime();
    }

    public static Date getDatePlusXdays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    public static Date getDatePlusXmonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    public static Date getDateByday(int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.DATE, day);
        return cal.getTime();
    }

    public static Date getDateFromEpoch(Long time) {
        if (!ObjectUtils.isEmpty(time)) {
            try {
                return Date.from(Instant.ofEpochMilli(time));
            } catch (NumberFormatException nex) {
            }
        }
        return null;
    }

    public static Date getDateFromEpoch(Long time, TimeUnit timeUnit) {
        if (!ObjectUtils.isEmpty(time)) {
            try {
                switch (timeUnit) {
                    case SECONDS:
                        return Date.from(Instant.ofEpochSecond(time));
                    case MILLISECONDS:
                        return Date.from(Instant.ofEpochMilli(time));
                }
            } catch (NumberFormatException nex) {
                log.warn("Exception while converting epoch to date with epoch : {} and unit : {}",
                        time, timeUnit);
            }
        }
        return null;
    }

    public static Date getDateByDayMonthYear(int day, int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, 0, 0, 0);
        return cal.getTime();
    }

    public static Date getReportDate(Date currTime) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        if (currTime.after(cal.getTime())) {
            cal.add(Calendar.DATE, 1);
        }
        return cal.getTime();
    }

    public static Date getCalendarForLastMonth(Date date) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        return calendar.getTime();
    }

    // this day we created day end from current balance but due to a bug of querying from date rather than created_at this got corrupted
    // this is to be fixed in a backdated manner (if even a single customer complains)
    public static Date getConsumerInterestIssueDate2() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DATE, 27);
        calendar.set(Calendar.MONTH, 3);
        return calendar.getTime();
    }

    public static Date getEarningCampaignTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getDatePlusXYears(Date date, int years) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.YEAR, years);
        return cal.getTime();
    }

    public static int getDateFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DATE);
    }

    public static String getDateString(Date date, SimpleDateFormat simpleDateFormat) {
        return simpleDateFormat.format(date);
    }

    public static Date getDateFromString(String date, SimpleDateFormat simpleDateFormat) {
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date getCurrentTimeWithoutMillis() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
