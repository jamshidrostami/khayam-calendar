/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yeksefr.khayam;

import java.util.Formatter;
import java.util.Locale;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.format.DateFormat;
import android.text.format.Time;

import com.android.calendarcommon2.R;

/**
 * This class contains various date-related utilities for creating text for
 * things like elapsed time and date ranges, strings for days of the week and
 * months, and AM/PM text etc.
 */
public class JalaliDateUtils
{
	private static String[] sShortMonths;
	private static String[] sLongMonths;
	private static String[] sShortWeekdays;
	private static String[] sLongWeekdays;
//	private static String sTimeOnlyFormat;
//	private static String sDateOnlyFormat;
//	private static String sDateTimeFormat;
	private static String sAm;
	private static String sPm;

	/**
	 * Request the full spelled-out name. For use with the 'abbrev' parameter of
	 * {@link #getDayOfWeekString} and {@link #getMonthString}.
	 * 
	 * @more <p>
	 *       e.g. "Sunday" or "January"
	 */
	public static final int LENGTH_LONG = 10;

	/**
	 * Request a shorter abbreviated version of the name. For use with the
	 * 'abbrev' parameter of {@link #getDayOfWeekString} and
	 * {@link #getMonthString}.
	 * 
	 * @more <p>
	 *       e.g. "Su" or "Jan"
	 *       <p>
	 *       In most languages, the results returned for LENGTH_SHORT will be
	 *       the same as the results returned for {@link #LENGTH_MEDIUM}.
	 */
	public static final int LENGTH_SHORT = 30;

	static Resources res;

	public static String getAMtring(Context context)
	{
		if (res == null)
		{
			res = context.getResources();
		}
		return sAm;
	}
	
	public static String getPMString(Context context)
	{
		if (res == null)
		{
			res = context.getResources();
		}
		return sPm;
	}
	
	private static void init(Context context)
	{
		if (res == null)
		{
			res = context.getResources();

			sShortMonths = res.getStringArray(R.array.shortMonthNames);
			sLongMonths = res.getStringArray(R.array.longMonthNames);
			sShortWeekdays = res.getStringArray(R.array.shortWeekdayNames);
			sLongWeekdays = res.getStringArray(R.array.longWeekdayNames);

//			sTimeOnlyFormat = res.getString(R.string.time_of_day);
//			sDateOnlyFormat = res.getString(R.string.month_day_year);
//			sDateTimeFormat = res.getString(R.string.date_and_time);
			
			sAm = res.getString(R.string.am);
			sPm = res.getString(R.string.pm);
		}
	}

	public static String getMonthString(Context context, int month, int abbrev)
	{
		init(context);
		
		String[] list;
		switch (abbrev)
		{
		case LENGTH_LONG:
			list = sLongMonths;
			break;
		case LENGTH_SHORT:
			list = sShortMonths;
			break;
		default:
			list = sLongMonths;
			break;
		}

		return list[month];
	}

	public static String getDayOfWeekString(Context context, int dayOfWeek, int abbrev)
	{
		init(context);
		
		String[] list;
		switch (abbrev)
		{
		case LENGTH_LONG:
			list = sLongWeekdays;
			break;
		case LENGTH_SHORT:
			list = sShortWeekdays;
			break;
		default:
			list = sLongWeekdays;
			break;
		}

		// return r.getString(list[dayOfWeek - Calendar.SUNDAY]);
//		if (dayOfWeek == 0)
//		{
//			dayOfWeek = 7;
//		}
		dayOfWeek = dayOfWeek % 7;
		if (dayOfWeek < 0)
		{
			dayOfWeek += 7;
		}
		return list[dayOfWeek];
//		return list[dayOfWeek - 1];
	}

	public static String getYearString(long millis)
	{
		JalaliTime time = new JalaliTime();
		time.set(millis);
		return Helper.convertEnglishNumbersToParsi(String.valueOf(time.jalaliYear));
	}
	
	public static String getMonthString(Context context, long millis)
	{
		JalaliTime time = new JalaliTime();
		time.set(millis);
		return getMonthString(context, time.jalaliMonth, LENGTH_LONG);
//		JalaliCalendar jc = getJalaliByMillis(millis);
//		return getMonthString(context, jc.get(JalaliCalendar.MONTH), LENGTH_LONG);
	}
	
	public static String getDateAndYearString(Context context, long millis)
	{
		JalaliTime time = new JalaliTime();
		time.set(millis);
		return Helper.convertEnglishNumbersToParsi(String.valueOf(time.jalaliMonthDay)) + " "
		+ getMonthString(context, time.jalaliMonth, LENGTH_LONG) + " "
		+ Helper.convertEnglishNumbersToParsi(String.valueOf(time.jalaliYear));
	}
	
	public static String getDayAndMonthString(Context context, long millis)
	{
		JalaliTime time = new JalaliTime();
		time.set(millis);
		return Helper.convertEnglishNumbersToParsi(String.valueOf(time.jalaliMonthDay)) + " "
		+ getMonthString(context, time.jalaliMonth, LENGTH_LONG);
	}
	
	public static String getDayRangeAndMonthString(Context context, long start, long end, int flags)
	{
		JalaliTime startTime = new JalaliTime();
		startTime.set(start);
		JalaliTime endTime = new JalaliTime();
		endTime.set(end);
		if ((flags & FORMAT_ABBREV_MONTH) != 0)
		{
			return Helper.convertEnglishNumbersToParsi(String.valueOf(startTime.jalaliMonthDay)) + " "
				+ getMonthString(context, startTime.jalaliMonth, LENGTH_LONG) + " - " +
				Helper.convertEnglishNumbersToParsi(String.valueOf(endTime.jalaliMonthDay)) + " "
				+ getMonthString(context, endTime.jalaliMonth, LENGTH_LONG);
		} else
		{
			return Helper.convertEnglishNumbersToParsi(String.valueOf(startTime.jalaliMonthDay)) + " - " +
					Helper.convertEnglishNumbersToParsi(String.valueOf(endTime.jalaliMonthDay))
					 + " "
					+ getMonthString(context, startTime.jalaliMonth, LENGTH_SHORT)
					;
		}
	}
	
	public static String getMonthAndYearString(Context context, long millis, int abbrev)
	{
		JalaliTime time = new JalaliTime();
		time.set(millis);
		return getMonthString(context, time.jalaliMonth, abbrev) + " "
		+ Helper.convertEnglishNumbersToParsi(String.valueOf(time.jalaliYear));
	}
	
//	public static String getMonthAndYearString(Context context, JalaliCalendar jc)
//	{
//		return getMonthString(context, jc.get(JalaliCalendar.MONTH), LENGTH_LONG) + " "
//				+ Helper.convertEnglishNumbersToParsi(String.valueOf(jc.get(JalaliCalendar.YEAR)));
//	}
//
//	public static String getDayOfWeekString(Context context, JalaliCalendar jc)
//	{
//		return getDayOfWeekString(context, jc.get(JalaliCalendar.DAY_OF_WEEK), LENGTH_LONG);
//	}

	public static String getTimeAndWeekday(Context context, long millis)
	{
		boolean use24Hour = DateFormat.is24HourFormat(context);
		
		Time time = new Time();
		time.set(millis);
		
		String format;
		if (use24Hour) {
			format  = res.getString(R.string.hour_minute_24);
        } else {

            boolean startOnTheHour = time.minute == 0 && time.second == 0;
            if (startOnTheHour) {
            	format = res.getString(R.string.hour_ampm);
            } else {
            	format = res.getString(R.string.hour_minute_ampm);
            }
        }
		return time.format(format) + "," + getDayOfWeekString(context, time.weekDay, LENGTH_LONG);
	}
	
	private static final Object sLock = new Object();
	private static Configuration sLastConfig;
//	private static java.text.DateFormat sStatusTimeFormat;
	private static String sElapsedFormatMMSS;
	private static String sElapsedFormatHMMSS;

	public static final long SECOND_IN_MILLIS = 1000;
	public static final long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
	public static final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
	public static final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;
	public static final long WEEK_IN_MILLIS = DAY_IN_MILLIS * 7;
	/**
	 * This constant is actually the length of 364 days, not of a year!
	 */
	public static final long YEAR_IN_MILLIS = WEEK_IN_MILLIS * 52;

	// The following FORMAT_* symbols are used for specifying the format of
	// dates and times in the formatDateRange method.
	public static final int FORMAT_SHOW_TIME = 0x00001;
	public static final int FORMAT_SHOW_WEEKDAY = 0x00002;
	public static final int FORMAT_SHOW_YEAR = 0x00004;
	public static final int FORMAT_NO_YEAR = 0x00008;
	public static final int FORMAT_SHOW_DATE = 0x00010;
	public static final int FORMAT_NO_MONTH_DAY = 0x00020;
	@Deprecated
	public static final int FORMAT_12HOUR = 0x00040;
	@Deprecated
	public static final int FORMAT_24HOUR = 0x00080;
	@Deprecated
	public static final int FORMAT_CAP_AMPM = 0x00100;
	public static final int FORMAT_NO_NOON = 0x00200;
	@Deprecated
	public static final int FORMAT_CAP_NOON = 0x00400;
	public static final int FORMAT_NO_MIDNIGHT = 0x00800;
	@Deprecated
	public static final int FORMAT_CAP_MIDNIGHT = 0x01000;
	/**
	 * @deprecated Use
	 *             {@link #formatDateRange(Context, Formatter, long, long, int, String)
	 *             formatDateRange} and pass in {@link Time#TIMEZONE_UTC
	 *             Time.TIMEZONE_UTC} for the timeZone instead.
	 */
	@Deprecated
	public static final int FORMAT_UTC = 0x02000;
	public static final int FORMAT_ABBREV_TIME = 0x04000;
	public static final int FORMAT_ABBREV_WEEKDAY = 0x08000;
	public static final int FORMAT_ABBREV_MONTH = 0x10000;
	public static final int FORMAT_NUMERIC_DATE = 0x20000;
	public static final int FORMAT_ABBREV_RELATIVE = 0x40000;
	public static final int FORMAT_ABBREV_ALL = 0x80000;
	@Deprecated
	public static final int FORMAT_CAP_NOON_MIDNIGHT = (FORMAT_CAP_NOON | FORMAT_CAP_MIDNIGHT);
	@Deprecated
	public static final int FORMAT_NO_NOON_MIDNIGHT = (FORMAT_NO_NOON | FORMAT_NO_MIDNIGHT);

	// Date and time format strings that are constant and don't need to be
	// translated.
	/**
	 * This is not actually the preferred 24-hour date format in all locales.
	 * 
	 * @deprecated use {@link java.text.SimpleDateFormat} instead.
	 */
	@Deprecated
	public static final String HOUR_MINUTE_24 = "%H:%M";
	public static final String MONTH_FORMAT = "%B";
	/**
	 * This is not actually a useful month name in all locales.
	 * 
	 * @deprecated use {@link java.text.SimpleDateFormat} instead.
	 */
	public static final String NUMERIC_MONTH_FORMAT = "%m";
	public static final String MONTH_DAY_FORMAT = "%-d";
	public static final String YEAR_FORMAT = "%Y";
	public static final String YEAR_FORMAT_TWO_DIGITS = "%g";
//	public static final String WEEKDAY_FORMAT = "%A";
//	public static final String ABBREV_WEEKDAY_FORMAT = "%a";

	// private static final int[] sAmPm = new int[] {
	// R.string.am,
	// R.string.pm,
	// };

	// This table is used to lookup the resource string id of a format string
	// used for formatting a start and end date that fall in the same year.
	// The index is constructed from a bit-wise OR of the boolean values:
	// {showTime, showYear, showWeekDay}. For example, if showYear and
	// showWeekDay are both true, then the index would be 3.
	/** @deprecated do not use. */
	public static final int sameYearTable[] = { R.string.same_year_md1_md2, R.string.same_year_wday1_md1_wday2_md2,
			R.string.same_year_mdy1_mdy2, R.string.same_year_wday1_mdy1_wday2_mdy2,
			R.string.same_year_md1_time1_md2_time2,
			R.string.same_year_wday1_md1_time1_wday2_md2_time2,
			R.string.same_year_mdy1_time1_mdy2_time2,
			R.string.same_year_wday1_mdy1_time1_wday2_mdy2_time2,

			// Numeric date strings
			R.string.numeric_md1_md2, R.string.numeric_wday1_md1_wday2_md2, R.string.numeric_mdy1_mdy2,
			R.string.numeric_wday1_mdy1_wday2_mdy2, R.string.numeric_md1_time1_md2_time2,
			R.string.numeric_wday1_md1_time1_wday2_md2_time2, R.string.numeric_mdy1_time1_mdy2_time2,
			R.string.numeric_wday1_mdy1_time1_wday2_mdy2_time2, };

	// This table is used to lookup the resource string id of a format string
	// used for formatting a start and end date that fall in the same month.
	// The index is constructed from a bit-wise OR of the boolean values:
	// {showTime, showYear, showWeekDay}. For example, if showYear and
	// showWeekDay are both true, then the index would be 3.
	/** @deprecated do not use. */
	public static final int sameMonthTable[] = { R.string.same_month_md1_md2, R.string.same_month_wday1_md1_wday2_md2,
			R.string.same_month_mdy1_mdy2, R.string.same_month_wday1_mdy1_wday2_mdy2,
			R.string.same_month_md1_time1_md2_time2, R.string.same_month_wday1_md1_time1_wday2_md2_time2,
			R.string.same_month_mdy1_time1_mdy2_time2, R.string.same_month_wday1_mdy1_time1_wday2_mdy2_time2,

			R.string.numeric_md1_md2, R.string.numeric_wday1_md1_wday2_md2, R.string.numeric_mdy1_mdy2,
			R.string.numeric_wday1_mdy1_wday2_mdy2, R.string.numeric_md1_time1_md2_time2,
			R.string.numeric_wday1_md1_time1_wday2_md2_time2, R.string.numeric_mdy1_time1_mdy2_time2,
			R.string.numeric_wday1_mdy1_time1_wday2_mdy2_time2, };

	private static void initFormatStrings()
	{
		synchronized (sLock)
		{
			initFormatStringsLocked();
		}
	}

	private static void initFormatStringsLocked()
	{
		Resources r = Resources.getSystem();
		Configuration cfg = r.getConfiguration();
		if (sLastConfig == null || !sLastConfig.equals(cfg))
		{
			sLastConfig = cfg;
//			sStatusTimeFormat = java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT);
			sElapsedFormatMMSS = r.getString(R.string.elapsed_time_short_format_mm_ss);
			sElapsedFormatHMMSS = r.getString(R.string.elapsed_time_short_format_h_mm_ss);
		}
	}

	/**
	 * Return given duration in a human-friendly format. For example, "4
	 * minutes" or "1 second". Returns only largest meaningful unit of time,
	 * from seconds up to hours.
	 * 
	 * @hide
	 */
	public static CharSequence formatDuration(long millis)
	{
		final Resources res = Resources.getSystem();
		if (millis >= HOUR_IN_MILLIS)
		{
			final int hours = (int) ((millis + 1800000) / HOUR_IN_MILLIS);
			return res.getQuantityString(R.plurals.duration_hours, hours, hours);
		} else if (millis >= MINUTE_IN_MILLIS)
		{
			final int minutes = (int) ((millis + 30000) / MINUTE_IN_MILLIS);
			return res.getQuantityString(R.plurals.duration_minutes, minutes, minutes);
		} else
		{
			final int seconds = (int) ((millis + 500) / SECOND_IN_MILLIS);
			return res.getQuantityString(R.plurals.duration_seconds, seconds, seconds);
		}
	}

	/**
	 * Formats an elapsed time in the form "MM:SS" or "H:MM:SS" for display on
	 * the call-in-progress screen.
	 * 
	 * @param elapsedSeconds
	 *            the elapsed time in seconds.
	 */
	public static String formatElapsedTime(long elapsedSeconds)
	{
		return formatElapsedTime(null, elapsedSeconds);
	}

	/**
	 * Formats an elapsed time in a format like "MM:SS" or "H:MM:SS" (using a
	 * form suited to the current locale), similar to that used on the
	 * call-in-progress screen.
	 * 
	 * @param recycle
	 *            {@link StringBuilder} to recycle, or null to use a temporary
	 *            one.
	 * @param elapsedSeconds
	 *            the elapsed time in seconds.
	 */
	public static String formatElapsedTime(StringBuilder recycle, long elapsedSeconds)
	{
		// Break the elapsed seconds into hours, minutes, and seconds.
		long hours = 0;
		long minutes = 0;
		long seconds = 0;
		if (elapsedSeconds >= 3600)
		{
			hours = elapsedSeconds / 3600;
			elapsedSeconds -= hours * 3600;
		}
		if (elapsedSeconds >= 60)
		{
			minutes = elapsedSeconds / 60;
			elapsedSeconds -= minutes * 60;
		}
		seconds = elapsedSeconds;

		// Create a StringBuilder if we weren't given one to recycle.
		// TODO: if we cared, we could have a thread-local temporary
		// StringBuilder.
		StringBuilder sb = recycle;
		if (sb == null)
		{
			sb = new StringBuilder(8);
		} else
		{
			sb.setLength(0);
		}

		// Format the broken-down time in a locale-appropriate way.
		// TODO: use icu4c when http://unicode.org/cldr/trac/ticket/3407 is
		// fixed.
		Formatter f = new Formatter(sb, Locale.getDefault());
		initFormatStrings();
		if (hours > 0)
		{
			return f.format(sElapsedFormatHMMSS, hours, minutes, seconds).toString();
		} else
		{
			return f.format(sElapsedFormatMMSS, minutes, seconds).toString();
		}
	}

	public static String formatDateTime(Context context, long millis, int flags)
	{
//		JalaliTime time = new JalaliTime();
//		time.set(millis);
//		if ((flags & FORMAT_SHOW_DATE) != 0)
//		{
//			return Helper.convertEnglishNumbersToParsi(time.jalaliMonthDay + " " + getMonthString(context, time.jalaliMonth, LENGTH_LONG) + " " + time.jalaliYear);
//		} else if ((flags & FORMAT_SHOW_TIME) != 0)
//		{
//			return Helper.convertEnglishNumbersToParsi(time.hour + ":" + time.minute + " " + getAMtring(context));
//		} else
//		{
//			return "";
//		}
		
		return formatDateRange(context, millis, millis, flags);
	}
	
	/**
	 * Format a date / time such that if the then is on the same day as now, it
	 * shows just the time and if it's a different day, it shows just the date.
	 * 
	 * <p>
	 * The parameters dateFormat and timeFormat should each be one of
	 * {@link java.text.DateFormat#DEFAULT}, {@link java.text.DateFormat#FULL},
	 * {@link java.text.DateFormat#LONG}, {@link java.text.DateFormat#MEDIUM} or
	 * {@link java.text.DateFormat#SHORT}
	 * 
	 * @param then
	 *            the date to format
	 * @param now
	 *            the base time
	 * @param dateStyle
	 *            how to format the date portion.
	 * @param timeStyle
	 *            how to format the time portion.
	 */
//	public static final CharSequence formatSameDayTime(long then, long now, int dateStyle, int timeStyle)
//	{
//		JalaliCalendar thenCal = new JalaliCalendar();
//		thenCal.setTimeInMillis(then);
//		Date thenDate = thenCal.getTime();
//		JalaliCalendar nowCal = new JalaliCalendar();
//		nowCal.setTimeInMillis(now);
//
//		java.text.DateFormat f;
//
//		if (thenCal.get(JalaliCalendar.YEAR) == nowCal.get(JalaliCalendar.YEAR)
//				&& thenCal.get(JalaliCalendar.MONTH) == nowCal.get(JalaliCalendar.MONTH)
//				&& thenCal.get(JalaliCalendar.DAY_OF_MONTH) == nowCal.get(JalaliCalendar.DAY_OF_MONTH))
//		{
//			f = java.text.DateFormat.getTimeInstance(timeStyle);
//		} else
//		{
//			f = java.text.DateFormat.getDateInstance(dateStyle);
//		}
//		return f.format(thenDate);
//	}

	/**
	 * @return true if the supplied when is today else false
	 */
	public static boolean isToday(long when)
	{
		JalaliTime time = new JalaliTime();
        time.set(when);

        int thenYear = time.year;
        int thenMonth = time.month;
        int thenMonthDay = time.monthDay;

        time.set(System.currentTimeMillis());
        return (thenYear == time.year)
                && (thenMonth == time.month)
                && (thenMonthDay == time.monthDay);
	}

//	private static void setTimeFromCalendar(Time t, JalaliCalendar c)
//	{
//		t.hour = c.get(JalaliCalendar.HOUR_OF_DAY);
//		t.minute = c.get(JalaliCalendar.MINUTE);
//		t.month = c.get(JalaliCalendar.MONTH);
//		t.monthDay = c.get(JalaliCalendar.DAY_OF_MONTH);
//		t.second = c.get(JalaliCalendar.SECOND);
//		t.weekDay = c.get(JalaliCalendar.DAY_OF_WEEK) - 1;
//		t.year = c.get(JalaliCalendar.YEAR);
//		t.yearDay = c.get(JalaliCalendar.DAY_OF_YEAR);
//		t.isDst = (c.get(JalaliCalendar.DST_OFFSET) != 0) ? 1 : 0;
//		t.gmtoff = c.get(JalaliCalendar.ZONE_OFFSET) + c.get(JalaliCalendar.DST_OFFSET);
//		t.timezone = c.getTimeZone().getID();
//	}

//	public static JalaliCalendar getJalaliByMillis(long mMilliTime)
//	{
//        Calendar c = JalaliCalendar.getInstance();
//        c.setTimeInMillis(mMilliTime);
//        YearMonthDate y = JalaliCalendar.gregorianToJalali(new YearMonthDate(c.get(JalaliCalendar.YEAR), c.get(JalaliCalendar.MONTH), c.get(JalaliCalendar.DAY_OF_MONTH)));
//        return new JalaliCalendar(y.getYear(), y.getMonth(), y.getDate(), c.get(JalaliCalendar.HOUR_OF_DAY), c.get(JalaliCalendar.MINUTE), c.get(JalaliCalendar.SECOND), c.get(JalaliCalendar.MILLISECOND));
//	}

    /**
     * Formats a date or a time range according to the local conventions.
     * <p>
     * Note that this is a convenience method. Using it involves creating an
     * internal {@link java.util.Formatter} instance on-the-fly, which is
     * somewhat costly in terms of memory and time. This is probably acceptable
     * if you use the method only rarely, but if you rely on it for formatting a
     * large number of dates, consider creating and reusing your own
     * {@link java.util.Formatter} instance and use the version of
     * {@link #formatDateRange(Context, long, long, int) formatDateRange}
     * that takes a {@link java.util.Formatter}.
     *
     * @param context the context is required only if the time is shown
     * @param startMillis the start time in UTC milliseconds
     * @param endMillis the end time in UTC milliseconds
     * @param flags a bit mask of options See
     * {@link #formatDateRange(Context, Formatter, long, long, int, String) formatDateRange}
     * @return a string containing the formatted date/time range.
     */
    public static String formatDateRange(Context context, long startMillis,
            long endMillis, int flags) {
        Formatter f = new Formatter(new StringBuilder(50), Locale.getDefault());
        return formatDateRange(context, f, startMillis, endMillis, flags).toString();
    }

    /**
     * Formats a date or a time range according to the local conventions.
     * <p>
     * Note that this is a convenience method for formatting the date or
     * time range in the local time zone. If you want to specify the time
     * zone please use
     * {@link #formatDateRange(Context, Formatter, long, long, int, String) formatDateRange}.
     *
     * @param context the context is required only if the time is shown
     * @param formatter the Formatter used for formatting the date range.
     * Note: be sure to call setLength(0) on StringBuilder passed to
     * the Formatter constructor unless you want the results to accumulate.
     * @param startMillis the start time in UTC milliseconds
     * @param endMillis the end time in UTC milliseconds
     * @param flags a bit mask of options See
     * {@link #formatDateRange(Context, Formatter, long, long, int, String) formatDateRange}
     * @return a string containing the formatted date/time range.
     */
    public static Formatter formatDateRange(Context context, Formatter formatter, long startMillis,
            long endMillis, int flags) {
        return formatDateRange(context, formatter, startMillis, endMillis, flags, null);
    }


    /**
     * Formats a date or a time range according to the local conventions.
     *
     * <p>
     * Example output strings (date formats in these examples are shown using
     * the US date format convention but that may change depending on the
     * local settings):
     * <ul>
     *   <li>10:15am</li>
     *   <li>3:00pm - 4:00pm</li>
     *   <li>3pm - 4pm</li>
     *   <li>3PM - 4PM</li>
     *   <li>08:00 - 17:00</li>
     *   <li>Oct 9</li>
     *   <li>Tue, Oct 9</li>
     *   <li>October 9, 2007</li>
     *   <li>Oct 9 - 10</li>
     *   <li>Oct 9 - 10, 2007</li>
     *   <li>Oct 28 - Nov 3, 2007</li>
     *   <li>Dec 31, 2007 - Jan 1, 2008</li>
     *   <li>Oct 9, 8:00am - Oct 10, 5:00pm</li>
     *   <li>12/31/2007 - 01/01/2008</li>
     * </ul>
     *
     * <p>
     * The flags argument is a bitmask of options from the following list:
     *
     * <ul>
     *   <li>FORMAT_SHOW_TIME</li>
     *   <li>FORMAT_SHOW_WEEKDAY</li>
     *   <li>FORMAT_SHOW_YEAR</li>
     *   <li>FORMAT_NO_YEAR</li>
     *   <li>FORMAT_SHOW_DATE</li>
     *   <li>FORMAT_NO_MONTH_DAY</li>
     *   <li>FORMAT_12HOUR</li>
     *   <li>FORMAT_24HOUR</li>
     *   <li>FORMAT_CAP_AMPM</li>
     *   <li>FORMAT_NO_NOON</li>
     *   <li>FORMAT_CAP_NOON</li>
     *   <li>FORMAT_NO_MIDNIGHT</li>
     *   <li>FORMAT_CAP_MIDNIGHT</li>
     *   <li>FORMAT_UTC</li>
     *   <li>FORMAT_ABBREV_TIME</li>
     *   <li>FORMAT_ABBREV_WEEKDAY</li>
     *   <li>FORMAT_ABBREV_MONTH</li>
     *   <li>FORMAT_ABBREV_ALL</li>
     *   <li>FORMAT_NUMERIC_DATE</li>
     * </ul>
     *
     * <p>
     * If FORMAT_SHOW_TIME is set, the time is shown as part of the date range.
     * If the start and end time are the same, then just the start time is
     * shown.
     *
     * <p>
     * If FORMAT_SHOW_WEEKDAY is set, then the weekday is shown.
     *
     * <p>
     * If FORMAT_SHOW_YEAR is set, then the year is always shown.
     * If FORMAT_NO_YEAR is set, then the year is not shown.
     * If neither FORMAT_SHOW_YEAR nor FORMAT_NO_YEAR are set, then the year
     * is shown only if it is different from the current year, or if the start
     * and end dates fall on different years.  If both are set,
     * FORMAT_SHOW_YEAR takes precedence.
     *
     * <p>
     * Normally the date is shown unless the start and end day are the same.
     * If FORMAT_SHOW_DATE is set, then the date is always shown, even for
     * same day ranges.
     *
     * <p>
     * If FORMAT_NO_MONTH_DAY is set, then if the date is shown, just the
     * month name will be shown, not the day of the month.  For example,
     * "January, 2008" instead of "January 6 - 12, 2008".
     *
     * <p>
     * If FORMAT_CAP_AMPM is set and 12-hour time is used, then the "AM"
     * and "PM" are capitalized.  You should not use this flag
     * because in some locales these terms cannot be capitalized, and in
     * many others it doesn't make sense to do so even though it is possible.
     *
     * <p>
     * If FORMAT_NO_NOON is set and 12-hour time is used, then "12pm" is
     * shown instead of "noon".
     *
     * <p>
     * If FORMAT_CAP_NOON is set and 12-hour time is used, then "Noon" is
     * shown instead of "noon".  You should probably not use this flag
     * because in many locales it will not make sense to capitalize
     * the term.
     *
     * <p>
     * If FORMAT_NO_MIDNIGHT is set and 12-hour time is used, then "12am" is
     * shown instead of "midnight".
     *
     * <p>
     * If FORMAT_CAP_MIDNIGHT is set and 12-hour time is used, then "Midnight"
     * is shown instead of "midnight".  You should probably not use this
     * flag because in many locales it will not make sense to capitalize
     * the term.
     *
     * <p>
     * If FORMAT_12HOUR is set and the time is shown, then the time is
     * shown in the 12-hour time format. You should not normally set this.
     * Instead, let the time format be chosen automatically according to the
     * system settings. If both FORMAT_12HOUR and FORMAT_24HOUR are set, then
     * FORMAT_24HOUR takes precedence.
     *
     * <p>
     * If FORMAT_24HOUR is set and the time is shown, then the time is
     * shown in the 24-hour time format. You should not normally set this.
     * Instead, let the time format be chosen automatically according to the
     * system settings. If both FORMAT_12HOUR and FORMAT_24HOUR are set, then
     * FORMAT_24HOUR takes precedence.
     *
     * <p>
     * If FORMAT_UTC is set, then the UTC time zone is used for the start
     * and end milliseconds unless a time zone is specified. If a time zone
     * is specified it will be used regardless of the FORMAT_UTC flag.
     *
     * <p>
     * If FORMAT_ABBREV_TIME is set and 12-hour time format is used, then the
     * start and end times (if shown) are abbreviated by not showing the minutes
     * if they are zero.  For example, instead of "3:00pm" the time would be
     * abbreviated to "3pm".
     *
     * <p>
     * If FORMAT_ABBREV_WEEKDAY is set, then the weekday (if shown) is
     * abbreviated to a 3-letter string.
     *
     * <p>
     * If FORMAT_ABBREV_MONTH is set, then the month (if shown) is abbreviated
     * to a 3-letter string.
     *
     * <p>
     * If FORMAT_ABBREV_ALL is set, then the weekday and the month (if shown)
     * are abbreviated to 3-letter strings.
     *
     * <p>
     * If FORMAT_NUMERIC_DATE is set, then the date is shown in numeric format
     * instead of using the name of the month.  For example, "12/31/2008"
     * instead of "December 31, 2008".
     *
     * <p>
     * If the end date ends at 12:00am at the beginning of a day, it is
     * formatted as the end of the previous day in two scenarios:
     * <ul>
     *   <li>For single day events. This results in "8pm - midnight" instead of
     *       "Nov 10, 8pm - Nov 11, 12am".</li>
     *   <li>When the time is not displayed. This results in "Nov 10 - 11" for
     *       an event with a start date of Nov 10 and an end date of Nov 12 at
     *       00:00.</li>
     * </ul>
     *
     * @param context the context is required only if the time is shown
     * @param formatter the Formatter used for formatting the date range.
     * Note: be sure to call setLength(0) on StringBuilder passed to
     * the Formatter constructor unless you want the results to accumulate.
     * @param startMillis the start time in UTC milliseconds
     * @param endMillis the end time in UTC milliseconds
     * @param flags a bit mask of options
     * @param timeZone the time zone to compute the string in. Use null for local
     * or if the FORMAT_UTC flag is being used.
     *
     * @return the formatter with the formatted date/time range appended to the string buffer.
     */
    public static Formatter formatDateRange(Context context, Formatter formatter, long startMillis,
            long endMillis, int flags, String timeZone) {
//        Resources res = Resources.getSystem();
        Resources res = context.getResources();
        boolean showTime = (flags & FORMAT_SHOW_TIME) != 0;
        boolean showWeekDay = (flags & FORMAT_SHOW_WEEKDAY) != 0;
        boolean showYear = (flags & FORMAT_SHOW_YEAR) != 0;
        boolean noYear = (flags & FORMAT_NO_YEAR) != 0;
//        boolean useUTC = (flags & FORMAT_UTC) != 0;
        boolean abbrevWeekDay = (flags & (FORMAT_ABBREV_WEEKDAY | FORMAT_ABBREV_ALL)) != 0;
        boolean abbrevMonth = (flags & (FORMAT_ABBREV_MONTH | FORMAT_ABBREV_ALL)) != 0;
        boolean noMonthDay = (flags & FORMAT_NO_MONTH_DAY) != 0;
        boolean numericDate = (flags & FORMAT_NUMERIC_DATE) != 0;

        // If we're getting called with a single instant in time (from
        // e.g. formatDateTime(), below), then we can skip a lot of
        // computation below that'd otherwise be thrown out.
        boolean isInstant = (startMillis == endMillis);

//        Calendar startCalendar, endCalendar;
        JalaliTime startDate = new JalaliTime();
        startDate.set(startMillis);
//        if (timeZone != null) {
//            startCalendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
//        } else if (useUTC) {
//            startCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//        } else {
//            startCalendar = Calendar.getInstance();
//        }
//        startCalendar.setTimeInMillis(startMillis);
//        setTimeFromCalendar(startDate, startCalendar);

        JalaliTime endDate = new JalaliTime();
        endDate.set(endMillis);
        int dayDistance;
        if (isInstant) {
            endDate = startDate;
            dayDistance = 0;
        } else {
//            if (timeZone != null) {
//                endCalendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
//            } else if (useUTC) {
//                endCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//            } else {
//                endCalendar = Calendar.getInstance();
//            }
//            endCalendar.setTimeInMillis(endMillis);
//            setTimeFromCalendar(endDate, endCalendar);

            int startJulianDay = JalaliTime.getJulianDay(startMillis, startDate.gmtoff);
            int endJulianDay = JalaliTime.getJulianDay(endMillis, endDate.gmtoff);
            dayDistance = endJulianDay - startJulianDay;
        }

        if (!isInstant
            && (endDate.hour | endDate.minute | endDate.second) == 0
            && (!showTime || dayDistance <= 1)) {
            endDate.monthDay -= 1;
            endDate.normalize(true /* ignore isDst */);
        }

        int startDay = startDate.jalaliMonthDay;
        int startMonthNum = startDate.jalaliMonth;
        int startYear = startDate.jalaliYear;

        int endDay = endDate.jalaliMonthDay;
        int endMonthNum = endDate.jalaliMonth;
        int endYear = endDate.jalaliYear;

        // Get the month, day, and year strings for the start and end dates
//        String monthFormat;
        String startMonthString;// = startDate.format(monthFormat);
        String endMonthString;
        if (numericDate) {
//            monthFormat = NUMERIC_MONTH_FORMAT;
            startMonthString = Helper.convertEnglishNumbersToParsi(startMonthNum + "");
            endMonthString = isInstant ? null : Helper.convertEnglishNumbersToParsi(endMonthNum + "");
        } else if (abbrevMonth) {
        	startMonthString = getMonthString(context, startDate.jalaliMonth, LENGTH_SHORT);
        	endMonthString = isInstant ? null : getMonthString(context, endDate.jalaliMonth, LENGTH_SHORT);
//            monthFormat = res.getString(R.string.short_format_month);
        } else {
        	startMonthString = getMonthString(context, startDate.jalaliMonth, LENGTH_LONG);
        	endMonthString = isInstant ? null : getMonthString(context, endDate.jalaliMonth, LENGTH_LONG);
//            monthFormat = MONTH_FORMAT;
        }

        String startMonthDayString = startDate.format(MONTH_DAY_FORMAT);
        String startYearString = startDate.format(YEAR_FORMAT);

        String endMonthDayString = isInstant ? null : endDate.format(MONTH_DAY_FORMAT);
        String endYearString = isInstant ? null : endDate.format(YEAR_FORMAT);

        String startWeekDayString = "";
        String endWeekDayString = "";
        if (showWeekDay) {
//            String weekDayFormat = "";
            if (abbrevWeekDay) {
            	startWeekDayString = getDayOfWeekString(context, startDate.weekDay, LENGTH_SHORT);
            	endWeekDayString = getDayOfWeekString(context, endDate.weekDay, LENGTH_SHORT);
            } else {
            	startWeekDayString = getDayOfWeekString(context, startDate.weekDay, LENGTH_LONG);
            	endWeekDayString = getDayOfWeekString(context, endDate.weekDay, LENGTH_LONG);
            }
//            startWeekDayString = startDate.format(weekDayFormat);
//            endWeekDayString = isInstant ? startWeekDayString : endDate.format(weekDayFormat);
        }

        String startTimeString = "";
        String endTimeString = "";
        if (showTime) {
            String startTimeFormat = "";
            String endTimeFormat = "";
            boolean force24Hour = (flags & FORMAT_24HOUR) != 0;
            boolean force12Hour = (flags & FORMAT_12HOUR) != 0;
            boolean use24Hour;
            if (force24Hour) {
                use24Hour = true;
            } else if (force12Hour) {
                use24Hour = false;
            } else {
                use24Hour = DateFormat.is24HourFormat(context);
            }
            if (use24Hour) {
                startTimeFormat = endTimeFormat = res.getString(R.string.hour_minute_24);

                startTimeString = startDate.format(startTimeFormat);
                endTimeString = isInstant ? startTimeString : endDate.format(endTimeFormat);
            } else {
                boolean abbrevTime = (flags & (FORMAT_ABBREV_TIME | FORMAT_ABBREV_ALL)) != 0;
                boolean noNoon = (flags & FORMAT_NO_NOON) != 0;
                boolean noMidnight = (flags & FORMAT_NO_MIDNIGHT) != 0;

                boolean startOnTheHour = startDate.minute == 0 && startDate.second == 0;
                boolean endOnTheHour = endDate.minute == 0 && endDate.second == 0;
                
                String mAmString = JalaliDateUtils.getAMtring(context);
                String mPmString = JalaliDateUtils.getPMString(context);
                
                if (startDate.hour == 12 && startOnTheHour && !noNoon) {
                    startTimeFormat = res.getString(R.string.noon);
                    // Don't show the start time starting at midnight.  Show
                    // 12am instead.
                    startTimeString = startDate.format(startTimeFormat);
                } else {
	                if (abbrevTime && startOnTheHour) {
	                    startTimeFormat = res.getString(R.string.hour_ampm);
	                } else {
	                    startTimeFormat = res.getString(R.string.hour_minute_ampm);
	                }
	                String ampm = startDate.hour > 12 ? mPmString : mAmString;
	                startTimeString = startDate.format(startTimeFormat) + " " + ampm;
                }
                
                // Don't waste time on setting endTimeFormat when
                // we're dealing with an instant, where we'll never
                // need the end point.  (It's the same as the start
                // point)
                if (!isInstant) {
                    if (endDate.hour == 12 && endOnTheHour && !noNoon) {
                        endTimeFormat = res.getString(R.string.noon);
                        endTimeString = isInstant ? startTimeString : endDate.format(endTimeFormat);
                    } else if (endDate.hour == 0 && endOnTheHour && !noMidnight) {
                        endTimeFormat = res.getString(R.string.midnight);
                        endTimeString = isInstant ? startTimeString : endDate.format(endTimeFormat);
                    } else
                    {
	                    if (abbrevTime && endOnTheHour) {
                            endTimeFormat = res.getString(R.string.hour_ampm);
	                    } else {
                            endTimeFormat = res.getString(R.string.hour_minute_ampm);
	                    }
	                    
	                    String ampm = endDate.hour > 12 ? mPmString : mAmString;
	                    endTimeString = isInstant ? startTimeString : endDate.format(endTimeFormat) + " " + ampm;
                    }
                }
            }
        }

        // Show the year if the user specified FORMAT_SHOW_YEAR or if
        // the starting and end years are different from each other
        // or from the current year.  But don't show the year if the
        // user specified FORMAT_NO_YEAR.
        if (showYear) {
            // No code... just a comment for clarity.  Keep showYear
            // on, as they enabled it with FORMAT_SHOW_YEAR.  This
            // takes precedence over them setting FORMAT_NO_YEAR.
        } else if (noYear) {
            // They explicitly didn't want a year.
            showYear = false;
        } else if (startYear != endYear) {
            showYear = true;
        } else {
            // Show the year if it's not equal to the current year.
        	JalaliTime currentTime = new JalaliTime();
            currentTime.setToNow();
            showYear = startYear != currentTime.year;
        }

        String defaultDateFormat, fullFormat, dateRange;
        String startDateString, endDateString;
        if (numericDate) {
        	startDateString = res.getString(R.string.numeric_date);
        	endDateString = res.getString(R.string.numeric_date);
        } else if (showYear) {
            if (abbrevMonth) {
                if (noMonthDay) {
//                	out = res.getString(R.string.abbrev_month_year);
                	startDateString = getMonthAndYearString(context, startMillis, LENGTH_SHORT);
                	endDateString = getMonthAndYearString(context, endMillis, LENGTH_SHORT);
                } else {
//                	out = res.getString(R.string.abbrev_month_day_year);
                	startDateString = Helper.convertEnglishNumbersToParsi(startDate.jalaliMonthDay + "") + " " + getMonthAndYearString(context, startMillis, LENGTH_SHORT);
                	endDateString = Helper.convertEnglishNumbersToParsi(endDate.jalaliMonthDay + "") + " " + getMonthAndYearString(context, endMillis, LENGTH_SHORT);
                }
            } else {
                if (noMonthDay) {
                	startDateString = getMonthAndYearString(context, startMillis, LENGTH_LONG);
                	endDateString = getMonthAndYearString(context, endMillis, LENGTH_LONG);
//                	out = res.getString(R.string.month_year);
                } else {
                	startDateString = Helper.convertEnglishNumbersToParsi(startDate.jalaliMonthDay + "") + " " + getMonthAndYearString(context, startMillis, LENGTH_LONG);
                	endDateString = Helper.convertEnglishNumbersToParsi(endDate.jalaliMonthDay + "") + " " + getMonthAndYearString(context, endMillis, LENGTH_LONG);
//                	out = res.getString(R.string.month_day_year);
                }
            }
        } else {
            if (abbrevMonth) {
                if (noMonthDay) {
//                    defaultDateFormat = res.getString(R.string.abbrev_month);
                	startDateString = getMonthString(context, startDate.jalaliMonth, LENGTH_SHORT);
                	endDateString = getMonthString(context, endDate.jalaliMonth, LENGTH_SHORT);
                } else {
//                    defaultDateFormat = res.getString(R.string.abbrev_month_day);
                	startDateString = Helper.convertEnglishNumbersToParsi(startDate.jalaliMonthDay + "") + " " + getMonthString(context, startDate.jalaliMonth, LENGTH_SHORT);
                	endDateString = Helper.convertEnglishNumbersToParsi(endDate.jalaliMonthDay + "") + " " + getMonthString(context, endDate.jalaliMonth, LENGTH_SHORT);
                }
            } else {
                if (noMonthDay) {
//                    defaultDateFormat = res.getString(R.string.month);
                	startDateString = getMonthString(context, startDate.jalaliMonth, LENGTH_LONG);
                	endDateString = getMonthString(context, endDate.jalaliMonth, LENGTH_LONG);
                } else {
                	startDateString = Helper.convertEnglishNumbersToParsi(startDate.jalaliMonthDay + "") + " " + getMonthString(context, startDate.jalaliMonth, LENGTH_LONG);
                	endDateString = Helper.convertEnglishNumbersToParsi(endDate.jalaliMonthDay + "") + " " + getMonthString(context, endDate.jalaliMonth, LENGTH_LONG);
//                    defaultDateFormat = res.getString(R.string.month_day);
                }
            }
        }

        if (showWeekDay) {
            if (showTime) {
                fullFormat = res.getString(R.string.wday1_date1_time1_wday2_date2_time2);
            } else {
                fullFormat = res.getString(R.string.wday1_date1_wday2_date2);
            }
        } else {
            if (showTime) {
                fullFormat = res.getString(R.string.date1_time1_date2_time2);
            } else {
                fullFormat = res.getString(R.string.date1_date2);
            }
        }

        if (noMonthDay && startMonthNum == endMonthNum && startYear == endYear) {
            // Example: "January, 2008"
            return formatter.format("%s", startDateString);
//            return formatter.format("%s", startDate.format(defaultDateFormat));
        }

        if (startYear != endYear || noMonthDay) {
            // Different year or we are not showing the month day number.
            // Example: "December 31, 2007 - January 1, 2008"
            // Or: "January - February, 2008"
//            String startDateString = startDate.format(defaultDateFormat);
//            String endDateString = endDate.format(defaultDateFormat);

            // The values that are used in a fullFormat string are specified
            // by position.
            return formatter.format(fullFormat,
                    startWeekDayString, startDateString, startTimeString,
                    endWeekDayString, endDateString, endTimeString);
        }

//        // Get the month, day, and year strings for the start and end dates
//        String monthFormat;
//        if (numericDate) {
//            monthFormat = NUMERIC_MONTH_FORMAT;
//        } else if (abbrevMonth) {
//            monthFormat =
//                res.getString(R.string.short_format_month);
//        } else {
//            monthFormat = MONTH_FORMAT;
//        }
        
//        String startMonthString = startDate.format(monthFormat);
//        String startMonthDayString = startDate.format(MONTH_DAY_FORMAT);
//        String startYearString = startDate.format(YEAR_FORMAT);
//
//        String endMonthString = isInstant ? null : endDate.format(monthFormat);
//        String endMonthDayString = isInstant ? null : endDate.format(MONTH_DAY_FORMAT);
//        String endYearString = isInstant ? null : endDate.format(YEAR_FORMAT);

        String startStandaloneMonthString = startMonthString;
        String endStandaloneMonthString = endMonthString;
        // We need standalone months for these strings in Persian (fa): http://b/6811327
        if (!numericDate && !abbrevMonth && Locale.getDefault().getLanguage().equals("fa")) {
            startStandaloneMonthString = startDate.format("%-B");
            endStandaloneMonthString = endDate.format("%-B");
        }

        if (startMonthNum != endMonthNum) {
            // Same year, different month.
            // Example: "October 28 - November 3"
            // or: "Wed, Oct 31 - Sat, Nov 3, 2007"
            // or: "Oct 31, 8am - Sat, Nov 3, 2007, 5pm"

            int index = 0;
            if (showWeekDay) index = 1;
            if (showYear) index += 2;
            if (showTime) index += 4;
            if (numericDate) index += 8;
            int resId = sameYearTable[index];
            fullFormat = res.getString(resId);

            // The values that are used in a fullFormat string are specified
            // by position.
            return formatter.format(fullFormat,
                    startWeekDayString, startMonthString, startMonthDayString,
                    startYearString, startTimeString,
                    endWeekDayString, endMonthString, endMonthDayString,
                    endYearString, endTimeString,
                    startStandaloneMonthString, endStandaloneMonthString);
        }

        if (startDay != endDay) {
            // Same month, different day.
            int index = 0;
            if (showWeekDay) index = 1;
            if (showYear) index += 2;
            if (showTime) index += 4;
            if (numericDate) index += 8;
            int resId = sameMonthTable[index];
            fullFormat = res.getString(resId);

            // The values that are used in a fullFormat string are specified
            // by position.
            return formatter.format(fullFormat,
                    startWeekDayString, startMonthString, startMonthDayString,
                    startYearString, startTimeString,
                    endWeekDayString, endMonthString, endMonthDayString,
                    endYearString, endTimeString,
                    startStandaloneMonthString, endStandaloneMonthString);
        }

        // Same start and end day
        boolean showDate = (flags & FORMAT_SHOW_DATE) != 0;

        // If nothing was specified, then show the date.
        if (!showTime && !showDate && !showWeekDay) showDate = true;

        // Compute the time string (example: "10:00 - 11:00 am")
        String timeString = "";
        if (showTime) {
            // If the start and end time are the same, then just show the
            // start time.
            if (isInstant) {
                // Same start and end time.
                // Example: "10:15 AM"
                timeString = startTimeString;
            } else {
                // Example: "10:00 - 11:00 am"
                String timeFormat = res.getString(R.string.time1_time2);
                // Don't use the user supplied Formatter because the result will pollute the buffer.
                timeString = String.format(timeFormat, startTimeString, endTimeString);
            }
        }

        // Figure out which full format to use.
        fullFormat = "";
        String dateString = "";
        if (showDate) {
            if (numericDate)
            {
            	dateString = startDate.format(startDateString);
            } else
            {
            	dateString = startDateString;
            }
            if (showWeekDay) {
                if (showTime) {
                    // Example: "10:00 - 11:00 am, Tue, Oct 9"
                    fullFormat = res.getString(R.string.time_wday_date);
                } else {
                    // Example: "Tue, Oct 9"
                    fullFormat = res.getString(R.string.wday_date);
                }
            } else {
                if (showTime) {
                    // Example: "10:00 - 11:00 am, Oct 9"
                    fullFormat = res.getString(R.string.time_date);
                } else {
                    // Example: "Oct 9"
                    return formatter.format("%s", dateString);
                }
            }
        } else if (showWeekDay) {
            if (showTime) {
                // Example: "10:00 - 11:00 am, Tue"
                fullFormat = res.getString(R.string.time_wday);
            } else {
                // Example: "Tue"
                return formatter.format("%s", startWeekDayString);
            }
        } else if (showTime) {
            return formatter.format("%s", timeString);
        }

        // The values that are used in a fullFormat string are specified
        // by position.
        return formatter.format(fullFormat, timeString, startWeekDayString, dateString);
    }

//    private static void setTimeFromCalendar(JalaliTime t, Calendar c) {
//        t.hour = c.get(Calendar.HOUR_OF_DAY);
//        t.minute = c.get(Calendar.MINUTE);
//        t.second = c.get(Calendar.SECOND);
//        t.isDst = (c.get(Calendar.DST_OFFSET) != 0) ? 1 : 0;
//        t.gmtoff = c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET);
//        t.timezone = c.getTimeZone().getID();
//        
//        t.update(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.DAY_OF_YEAR), c.get(Calendar.DAY_OF_WEEK));
//    }
}