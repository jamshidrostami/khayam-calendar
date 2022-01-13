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

import java.util.TimeZone;

import android.text.format.Time;

/**
 * An alternative to the {@link java.util.Calendar} and
 * {@link java.util.GregorianCalendar} classes. An instance of the Time class represents
 * a moment in time, specified with second precision. It is modelled after
 * struct tm, and in fact, uses struct tm to implement most of the
 * functionality.
 */
public class JalaliTime extends Time {

	public final static int FARVARDIN = 0;
    public final static int ORDIBEHESHT = 1;
    public final static int KHORDAD = 2;
    public final static int TIR = 3;
    public final static int MORDAD = 4;
    public final static int SHAHRIVAR = 5;
    public final static int MEHR = 6;
    public final static int ABAN = 7;
    public final static int AZAR = 8;
    public final static int DEY = 9;
    public final static int BAHMAN = 10;
    public final static int ESFAND = 11;
    
    /**
     * Day of month [1-31]
     */
    public int jalaliMonthDay;

    /**
     * Month [0-11]
     */
    public int jalaliMonth;

    /**
     * Year. For example, 1970.
     */
    public int jalaliYear;

    /**
     * Day of year [0-365]
     */
    public int jalaliYearDay;

    /**
     * Construct a Time object in the timezone named by the string
     * argument "timezone". The time is initialized to Jan 1, 1970.
     * @param timezone string containing the timezone to use.
     * @see TimeZone
     */
    public JalaliTime(String timezone) {
        super(timezone);
        
        gregorianToJalali();
    }

    public JalaliTime()
	{
    	this(TimeZone.getDefault().getID());
	}
    
    @Override
    public String format(String format)
    {
    	int tmpYear = year;
    	int tmpMonth = month;
    	int tmpMonthDay = monthDay;
    	
    	gregorianToJalali();
    	
    	year = jalaliYear;
    	month = jalaliMonth; // test <- month = jalaliMonth - 1;
    	monthDay = jalaliMonthDay;

    	String f = super.format(format);
    	
    	year = tmpYear;
    	month = tmpMonth;
    	monthDay = tmpMonthDay;
    	
    	return Helper.convertEnglishNumbersToParsi(f);
//    	return f;
    }
    
    /**
     * A copy constructor.  Construct a Time object by copying the given
     * Time object.  No normalization occurs.
     *
     * @param other
     */
    public JalaliTime(Time other) {
        set(other);
    }

    private void gregorianToJalali() {
    	gregorianToJalali(year, month, monthDay);
    }
    
    private void gregorianToJalali(int year, int month, int monthDay) {
        int gregorianDayNo, jalaliDayNo;
        int jalaliNP;
        int i;

        year -= 1600;
        monthDay -= 1;

        gregorianDayNo = 365 * year + (int) Math.floor((year + 3) / 4)
                - (int) Math.floor((year + 99) / 100)
                + (int) Math.floor((year + 399) / 400);
        for (i = 0; i < month; ++i) {
            gregorianDayNo += DAYS_PER_MONTH[i];
        }

        if (month > 1 && ((year % 4 == 0 && year % 100 != 0)
                || (year % 400 == 0))) {
            ++gregorianDayNo;
        }

        gregorianDayNo += monthDay;

        jalaliDayNo = gregorianDayNo - 79;

        jalaliNP = (int) Math.floor(jalaliDayNo / 12053);
        jalaliDayNo = jalaliDayNo % 12053;

        jalaliYear = 979 + 33 * jalaliNP + 4 * (int) (jalaliDayNo / 1461);
        jalaliDayNo = jalaliDayNo % 1461;

        if (jalaliDayNo >= 366) {
            jalaliYear += (int) Math.floor((jalaliDayNo - 1) / 365);
            jalaliDayNo = (jalaliDayNo - 1) % 365;
        }

        for (i = 0; i < 11 && jalaliDayNo >= JALALI_DAYS_PER_MONTH[i]; ++i) {
            jalaliDayNo -= JALALI_DAYS_PER_MONTH[i];
        }
        jalaliMonth = i;
        jalaliMonthDay = jalaliDayNo + 1;
        
        setJalaliDayOfYear();
    }

    public void jalaliToGregorian() {
    	jalaliToGregorian(jalaliYear, jalaliMonth, jalaliMonthDay);
    }
    
    private void jalaliToGregorian(int jalaliYear, int jalaliMonth, int jalaliMonthDay) {
        int gregorianDayNo, jalaliDayNo;
        int leap;

        int i;
        jalaliYear -= 979;
        jalaliMonthDay -= 1;

        jalaliDayNo = 365 * jalaliYear + (int) (jalaliYear / 33) * 8
                + (int) Math.floor(((jalaliYear % 33) + 3) / 4);
        for (i = 0; i < jalaliMonth; ++i) {
            jalaliDayNo += JALALI_DAYS_PER_MONTH[i];
        }

        jalaliDayNo += jalaliMonthDay;

        gregorianDayNo = jalaliDayNo + 79;

        year = 1600 + 400 * (int) Math.floor(gregorianDayNo / 146097); /* 146097 = 365*400 + 400/4 - 400/100 + 400/400 */
        gregorianDayNo = gregorianDayNo % 146097;

        leap = 1;
        if (gregorianDayNo >= 36525) /* 36525 = 365*100 + 100/4 */ {
            gregorianDayNo--;
            year += 100 * (int) Math.floor(gregorianDayNo / 36524); /* 36524 = 365*100 + 100/4 - 100/100 */
            gregorianDayNo = gregorianDayNo % 36524;

            if (gregorianDayNo >= 365) {
                gregorianDayNo++;
            } else {
                leap = 0;
            }
        }

        year += 4 * (int) Math.floor(gregorianDayNo / 1461); /* 1461 = 365*4 + 4/4 */
        gregorianDayNo = gregorianDayNo % 1461;

        if (gregorianDayNo >= 366) {
            leap = 0;

            gregorianDayNo--;
            year += (int) Math.floor(gregorianDayNo / 365);
            gregorianDayNo = gregorianDayNo % 365;
        }

        for (i = 0; gregorianDayNo >= DAYS_PER_MONTH[i] + ((i == 1 && leap == 1) ? i : 0); i++) {
            gregorianDayNo -= DAYS_PER_MONTH[i] + ((i == 1 && leap == 1) ? i : 0);
        }
        
        month = i;
        monthDay = gregorianDayNo + 1;
    }
    
    private static final int[] DAYS_PER_MONTH = { 31, 28, 31, 30, 31, 30, 31,
            31, 30, 31, 30, 31 };

    public static int JALALI_DAYS_PER_MONTH[] = {31, 31, 31, 31, 31, 31,
        30, 30, 30, 30, 30, 29};

    /**
     * Return the maximum possible value for the given field given the value of
     * the other fields. Requires that it be normalized for MONTH_DAY and
     * YEAR_DAY.
     * @param field one of the constants for HOUR, MINUTE, SECOND, etc.
     * @return the maximum value for the field.
     */
    public int getJalaliActualMaximum(int field) {
        switch (field) {
        case SECOND:
            return 59; // leap seconds, bah humbug
        case MINUTE:
            return 59;
        case HOUR:
            return 23;
        case MONTH_DAY: {
            int n = JALALI_DAYS_PER_MONTH[this.jalaliMonth];
            if (n != 29) {
                return n;
            } else {
                return isLeapYear(jalaliYear) ? 30 : 29;
            }
        }
        case MONTH:
            return 11;
        case YEAR:
            return 2037;
        case WEEK_DAY:
            return 6;
        case YEAR_DAY: {
//            int y = this.year;
            // Year days are numbered from 0, so the last one is usually 364.
//            return ((y % 4) == 0 && ((y % 100) != 0 || (y % 400) == 0)) ? 365 : 364;
            return isLeapYear(this.jalaliYear) ? 366 : 365;
        }
        case WEEK_NUM:
            throw new RuntimeException("WEEK_NUM not implemented");
        default:
            throw new RuntimeException("bad field=" + field);
        }
    }

    public static boolean isLeapYear(int year) {
        //Algorithm from www.wikipedia.com
        if ((year % 33 == 1 || year % 33 == 5 || year % 33 == 9 || year % 33 == 13 ||
                year % 33 == 17 || year % 33 == 22 || year % 33 == 26 || year % 33 == 30)) {
            return true;
        } else return false;
    }

    private void setJalaliDayOfYear()
    {
    	jalaliYearDay = 0;
        int index = 0;
    	while (index < jalaliMonth) {
    		jalaliYearDay += JALALI_DAYS_PER_MONTH[index++];
        }
    	jalaliYearDay += jalaliMonthDay;
    }
    
    public int getJalaliWeekNumber()
    {
    	int dayOfYear = jalaliYearDay;
        switch (weekDay) {
            case 5:
                dayOfYear += 1;
                break;
            case 4:
                dayOfYear += 2;
                break;
            case 3:
                dayOfYear += 3;
                break;
            case 2:
                dayOfYear += 4;
                break;
            case 1:
                dayOfYear += 3;
                break;
            case 0:
                dayOfYear += 6;
                break;
            case 6:
            	dayOfYear += 7;
            	break;
        }
        dayOfYear = (int) Math.floor(dayOfYear / 7);
        return dayOfYear + 1;
    }
    
    @Override
    public void set(long millis)
    {
    	super.set(millis);
    	
    	gregorianToJalali();
    }
    
    @Override
    public long setJulianDay(int julianDay)
    {
//    	long jdd = fromJulian(julianDay);
    	long jd = super.setJulianDay(julianDay);
    	
    	// bug in s2 !! or android 4.3.1 saving time !
    	if (jd == -1 && isDst == -1)
    	{
    		jd = super.setJulianDay(julianDay + 1);
    		hour -= 23;
    		normalize(true);
    	}
    	
    	return jd;
    }
    
    @Override
    public long toMillis(boolean ignoreDst) {
    	long jd = super.toMillis(ignoreDst);
    	
    	if (jd == -1 && isDst == -1)
    	{
    		monthDay++;
    		hour -= 23;
    		normalize(true);
    		
    		jd = super.toMillis(ignoreDst);
    	}
    	
    	return jd;
    }
    
    public int JGREG = 15 + 31 * (10 + 12 * 1582);
    public double HALFSECOND = 0.5;

	public long fromJulian(double injulian) {
		int jalpha, ja, jb, jc, jd, je;
		double julian = injulian + HALFSECOND / 86400.0;
		ja = (int) julian;
		if (ja >= JGREG) {
			jalpha = (int) (((ja - 1867216) - 0.25) / 36524.25);
			ja = ja + 1 + jalpha - jalpha / 4;
		}

		jb = ja + 1524;
		jc = (int) (6680.0 + ((jb - 2439870) - 122.1) / 365.25);
		jd = 365 * jc + jc / 4;
		je = (int) ((jb - jd) / 30.6001);
		monthDay = jb - jd - (int) (30.6001 * je);
		month = je - 1;
		if (month > 12)
			month = month - 12;
		year = jc - 4715;
		if (month > 2)
			year--;
		if (year <= 0)
			year--;
		
		return normalize(true);
	}
    
    /**
     * Clears all values, setting the timezone to the given timezone. Sets isDst
     * to a negative value to mean "unknown".
     * @param timezone the timezone to use.
     */
    @Override
    public void clear(String timezone) {
    	super.clear(timezone);
    	
        this.jalaliMonthDay = 0;
        this.jalaliMonth = 0;
        this.jalaliYear = 0;
        this.jalaliYearDay = 0;
    }

    /**
     * Copy the value of that to this Time object. No normalization happens.
     */
    @Override
    public void set(Time that) {
        this.timezone = that.timezone;
        this.allDay = that.allDay;
        this.second = that.second;
        this.minute = that.minute;
        this.hour = that.hour;
        this.monthDay = that.monthDay;
        this.month = that.month;
        this.year = that.year;
        this.weekDay = that.weekDay;
        this.yearDay = that.yearDay;
        this.isDst = that.isDst;
        this.gmtoff = that.gmtoff;
        
        gregorianToJalali();
    }

    /**
     * Sets the fields. Sets weekDay, yearDay and gmtoff to 0, and isDst to -1.
     * Call {@link #normalize(boolean)} if you need those.
     */
    @Override
    public void set(int second, int minute, int hour, int monthDay, int month, int year) {
        this.allDay = false;
        this.second = second;
        this.minute = minute;
        this.hour = hour;
        this.monthDay = monthDay;
        this.month = month;
        this.year = year;
        this.weekDay = 0;
        this.yearDay = 0;
        this.isDst = -1;
        this.gmtoff = 0;
        
        gregorianToJalali();
    }

    /**
     * Sets the date from the given fields.  Also sets allDay to true.
     * Sets weekDay, yearDay and gmtoff to 0, and isDst to -1.
     * Call {@link #normalize(boolean)} if you need those.
     *
     * @param monthDay the day of the month (in the range [1,31])
     * @param month the zero-based month number (in the range [0,11])
     * @param year the year
     */
    @Override
    public void set(int monthDay, int month, int year) {
        this.allDay = true;
        this.second = 0;
        this.minute = 0;
        this.hour = 0;
        this.monthDay = monthDay;
        this.month = month;
        this.year = year;
        this.weekDay = 0;
        this.yearDay = 0;
        this.isDst = -1;
        this.gmtoff = 0;
        
        gregorianToJalali();
    }

    /**
     * Returns true if the time represented by this Time object occurs before
     * the given time.
     *
     * @param that a given Time object to compare against
     * @return true if this time is less than the given time
     */
//    public boolean before(Time that) {
//        return Time.compare(this, that) < 0;
//    }


    /**
     * Returns true if the time represented by this Time object occurs after
     * the given time.
     *
     * @param that a given Time object to compare against
     * @return true if this time is greater than the given time
     */
//    public boolean after(Time that) {
//        return Time.compare(this, that) > 0;
//    }
    
    

    public void update(int y, int m, int md, int yd, int wd)
    {
    	this.year = y;
    	this.month = m;
    	this.monthDay = md;
    	this.yearDay = yd;
    	this.weekDay = wd;
    }
    
    @Override
    public void setToNow()
    {
    	super.setToNow();
    	
    	gregorianToJalali();
    }
    
    @Override
    public long normalize(boolean flag)
    {
    	long d = super.normalize(flag);
    	
    	gregorianToJalali();
    	
    	return d;
    }
    
    public void addDay()
    {
    	monthDay++;
        normalize(true);
        
        gregorianToJalali();
    }
    
    public void addJalaliMonth(int increment)
    {
    	jalaliMonth += increment;
    	if (jalaliMonth > 11)
    	{
    		jalaliMonth %= 11;
    		jalaliYear /= 11;
    	}
    	
    	gregorianToJalali();
    }
    
    public void subDay()
    {
    	monthDay--;
    	normalize(true);
    	
    	gregorianToJalali();
    }
    
    public int getJalaliActualMaxMonthDays()
    {
    	if (jalaliMonth == 11 && isLeapYear(jalaliYear)) {
            return 30;
        } else
        {
        	return JALALI_DAYS_PER_MONTH[jalaliMonth];
        }
    }
    
    @Override
    public boolean parse(String s) {
    	boolean b = super.parse(s);
    	
    	gregorianToJalali();
    	
    	return b;
    }
}