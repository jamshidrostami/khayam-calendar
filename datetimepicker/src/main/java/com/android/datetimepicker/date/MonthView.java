/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.android.datetimepicker.date;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;

import com.android.datetimepicker.R;
import com.android.datetimepicker.Utils;
import com.android.datetimepicker.date.MonthAdapter.CalendarDay;
import com.yeksefr.khayam.CustomFont;
import com.yeksefr.khayam.JalaliDateUtils;
import com.yeksefr.khayam.JalaliTime;

/**
 * A calendar-like view displaying a specified month and the appropriate selectable day numbers
 * within the specified month.
 */
public abstract class MonthView extends View {
    private static final String TAG = "MonthView";

    /**
     * These params can be passed into the view to control how it appears.
     * {@link #VIEW_PARAMS_WEEK} is the only required field, though the default
     * values are unlikely to fit most layouts correctly.
     */
    /**
     * This sets the height of this week in pixels
     */
    public static final String VIEW_PARAMS_HEIGHT = "height";
    /**
     * This specifies the position (or weeks since the epoch) of this week,
     * calculated using {@link Utils#getWeeksSinceEpochFromJulianDay}
     */
    public static final String VIEW_PARAMS_MONTH = "month";
    /**
     * This specifies the position (or weeks since the epoch) of this week,
     * calculated using {@link Utils#getWeeksSinceEpochFromJulianDay}
     */
    public static final String VIEW_PARAMS_YEAR = "year";
    /**
     * This sets one of the days in this view as selected {@link Time#SUNDAY}
     * through {@link Time#SATURDAY}.
     */
    public static final String VIEW_PARAMS_SELECTED_DAY = "selected_day";
    /**
     * Which day the week should start on. {@link Time#SUNDAY} through
     * {@link Time#SATURDAY}.
     */
    public static final String VIEW_PARAMS_WEEK_START = "week_start";
    /**
     * How many days to display at a time. Days will be displayed starting with
     * {@link #mWeekStart}.
     */
    public static final String VIEW_PARAMS_NUM_DAYS = "num_days";
    /**
     * Which month is currently in focus, as defined by {@link Time#month}
     * [0-11].
     */
    public static final String VIEW_PARAMS_FOCUS_MONTH = "focus_month";
    /**
     * If this month should display week numbers. false if 0, true otherwise.
     */
    public static final String VIEW_PARAMS_SHOW_WK_NUM = "show_wk_num";

    protected static int DEFAULT_HEIGHT = 32;
    protected static int MIN_HEIGHT = 10;
    protected static final int DEFAULT_SELECTED_DAY = -1;
    protected static final int DEFAULT_WEEK_START = Calendar.SATURDAY;
    protected static final int DEFAULT_NUM_DAYS = 7;
    protected static final int DEFAULT_SHOW_WK_NUM = 0;
    protected static final int DEFAULT_FOCUS_MONTH = -1;
    protected static final int DEFAULT_NUM_ROWS = 6;
    protected static final int MAX_NUM_ROWS = 6;

    private static final int SELECTED_CIRCLE_ALPHA = 60;

    protected static int DAY_SEPARATOR_WIDTH = 1;
    protected static int MINI_DAY_NUMBER_TEXT_SIZE;
    protected static int MONTH_LABEL_TEXT_SIZE;
    protected static int MONTH_DAY_LABEL_TEXT_SIZE;
    protected static int MONTH_HEADER_SIZE;
    protected static int DAY_SELECTED_CIRCLE_SIZE;

    // used for scaling to the device density
    protected static float mScale = 0;

    // affects the padding on the sides of this view
    protected int mPadding = 0;

    protected Paint mMonthNumPaint;
    protected Paint mMonthTitlePaint;
    protected Paint mMonthTitleBGPaint;
    protected Paint mSelectedCirclePaint;
    protected Paint mMonthDayLabelPaint;

    private final Formatter mFormatter;
    private final StringBuilder mStringBuilder;

    // The Julian day of the first day displayed by this item
    protected int mFirstJulianDay = -1;
    // The month of the first day in this week
    protected int mFirstMonth = -1;
    // The month of the last day in this week
    protected int mLastMonth = -1;

    protected int mMonth;

    protected int mYear;
    // Quick reference to the width of this view, matches parent
    protected int mWidth;
    // The height this view should draw at in pixels, set by height param
    protected int mRowHeight = DEFAULT_HEIGHT;
    // If this view contains the today
    protected boolean mHasToday = false;
    // Which day is selected [0-6] or -1 if no day is selected
    protected int mSelectedDay = -1;
    // Which day is today [0-6] or -1 if no day is today
    protected int mToday = DEFAULT_SELECTED_DAY;
    // Which day of the week to start on [0-6]
    protected int mWeekStart = DEFAULT_WEEK_START;
    // How many days to display
    protected int mNumDays = DEFAULT_NUM_DAYS;
    // The number of days + a spot for week number if it is displayed
    protected int mNumCells = mNumDays;
    // The left edge of the selected day
    protected int mSelectedLeft = -1;
    // The right edge of the selected day
    protected int mSelectedRight = -1;

    private final JalaliTime mCalendar;

    private int mNumRows = DEFAULT_NUM_ROWS;

    // Optional listener for handling day click actions
    private OnDayClickListener mOnDayClickListener;

    protected int mDayTextColor;
    protected int mTodayNumberColor;
    protected int mMonthTitleColor;
    protected int mMonthTitleBGColor;

    public MonthView(Context context) {
        super(context);

        Resources res = context.getResources();

        mCalendar = new JalaliTime();

        mDayTextColor = res.getColor(R.color.date_picker_text_normal);
        mTodayNumberColor = res.getColor(R.color.blue);
        mMonthTitleColor = res.getColor(R.color.white);
        mMonthTitleBGColor = res.getColor(R.color.circle_background);

        mStringBuilder = new StringBuilder(50);
        mFormatter = new Formatter(mStringBuilder, Locale.getDefault());

        MINI_DAY_NUMBER_TEXT_SIZE = res.getDimensionPixelSize(R.dimen.day_number_size);
        MONTH_LABEL_TEXT_SIZE = res.getDimensionPixelSize(R.dimen.month_label_size);
        MONTH_DAY_LABEL_TEXT_SIZE = res.getDimensionPixelSize(R.dimen.month_day_label_text_size);
        MONTH_HEADER_SIZE = res.getDimensionPixelOffset(R.dimen.month_list_item_header_height);
        DAY_SELECTED_CIRCLE_SIZE = res
                .getDimensionPixelSize(R.dimen.day_number_select_circle_radius);

        mRowHeight = (res.getDimensionPixelOffset(R.dimen.date_picker_view_animator_height)
                - MONTH_HEADER_SIZE) / MAX_NUM_ROWS;

        // Sets up any standard paints that will be used
        initView();
    }

    public void setOnDayClickListener(OnDayClickListener listener) {
        mOnDayClickListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                final int day = getDayFromLocation(event.getX(), event.getY());
                if (day >= 0) {
                    onDayClick(day);
                }
                break;
        }
        return true;
    }

    /**
     * Sets up the text and style properties for painting. Override this if you
     * want to use a different paint.
     */
    protected void initView() {
        mMonthTitlePaint = new Paint();
        mMonthTitlePaint.setFakeBoldText(true);
        mMonthTitlePaint.setAntiAlias(true);
        mMonthTitlePaint.setTextSize(MONTH_LABEL_TEXT_SIZE);
        mMonthTitlePaint.setColor(mDayTextColor);
        mMonthTitlePaint.setTextAlign(Align.CENTER);
        mMonthTitlePaint.setStyle(Style.FILL);
        mMonthTitlePaint.setTypeface(CustomFont.droidnaskhBold);

        mMonthTitleBGPaint = new Paint();
        mMonthTitleBGPaint.setFakeBoldText(true);
        mMonthTitleBGPaint.setAntiAlias(true);
        mMonthTitleBGPaint.setColor(mMonthTitleBGColor);
        mMonthTitleBGPaint.setTextAlign(Align.CENTER);
        mMonthTitleBGPaint.setStyle(Style.FILL);
        mMonthTitleBGPaint.setTypeface(CustomFont.droidnaskh);

        mSelectedCirclePaint = new Paint();
        mSelectedCirclePaint.setFakeBoldText(true);
        mSelectedCirclePaint.setAntiAlias(true);
        mSelectedCirclePaint.setColor(mTodayNumberColor);
        mSelectedCirclePaint.setTextAlign(Align.CENTER);
        mSelectedCirclePaint.setStyle(Style.FILL);
        mSelectedCirclePaint.setAlpha(SELECTED_CIRCLE_ALPHA);
        mSelectedCirclePaint.setTypeface(CustomFont.droidnaskh);

        mMonthDayLabelPaint = new Paint();
        mMonthDayLabelPaint.setAntiAlias(true);
        mMonthDayLabelPaint.setTextSize(MONTH_DAY_LABEL_TEXT_SIZE);
        mMonthDayLabelPaint.setColor(mDayTextColor);
        mMonthDayLabelPaint.setStyle(Style.FILL);
        mMonthDayLabelPaint.setTextAlign(Align.CENTER);
        mMonthDayLabelPaint.setFakeBoldText(true);
//        mMonthDayLabelPaint.setTypeface(Typeface.create(mDayOfWeekTypeface, Typeface.NORMAL));
        mMonthDayLabelPaint.setTypeface(CustomFont.droidnaskh);

        mMonthNumPaint = new Paint();
        mMonthNumPaint.setAntiAlias(true);
        mMonthNumPaint.setTextSize(MINI_DAY_NUMBER_TEXT_SIZE);
        mMonthNumPaint.setStyle(Style.FILL);
        mMonthNumPaint.setTextAlign(Align.CENTER);
        mMonthNumPaint.setFakeBoldText(false);
        mMonthNumPaint.setTypeface(CustomFont.droidnaskh);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawMonthTitle(canvas);
        drawMonthDayLabels(canvas);
        drawMonthNums(canvas);
    }

    private int mDayOfWeekStart = 0;

    /**
     * Sets all the parameters for displaying this week. The only required
     * parameter is the week number. Other parameters have a default value and
     * will only update if a new value is included, except for focus month,
     * which will always default to no focus month if no value is passed in. See
     * {@link #VIEW_PARAMS_HEIGHT} for more info on parameters.
     *
     * @param params A map of the new parameters, see
     *            {@link #VIEW_PARAMS_HEIGHT}
     */
    public void setMonthParams(HashMap<String, Integer> params) {
        if (!params.containsKey(VIEW_PARAMS_MONTH) && !params.containsKey(VIEW_PARAMS_YEAR)) {
            throw new InvalidParameterException("You must specify month and year for this view");
        }
        setTag(params);
        // We keep the current value for any params not present
        if (params.containsKey(VIEW_PARAMS_HEIGHT)) {
            mRowHeight = params.get(VIEW_PARAMS_HEIGHT);
            if (mRowHeight < MIN_HEIGHT) {
                mRowHeight = MIN_HEIGHT;
            }
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_DAY)) {
            mSelectedDay = params.get(VIEW_PARAMS_SELECTED_DAY);
        }

        // Allocate space for caching the day numbers and focus values
        mMonth = params.get(VIEW_PARAMS_MONTH);
        mYear = params.get(VIEW_PARAMS_YEAR);

        // Figure out what day today is
        final Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        mHasToday = false;
        mToday = -1;

        mCalendar.jalaliYear = mYear;
        mCalendar.jalaliMonth = mMonth;
        mCalendar.jalaliMonthDay = 1;
        mCalendar.jalaliToGregorian();
        mCalendar.normalize(true);
        
        mDayOfWeekStart = mCalendar.weekDay;

        if (params.containsKey(VIEW_PARAMS_WEEK_START)) {
            mWeekStart = params.get(VIEW_PARAMS_WEEK_START);
        } else {
            mWeekStart = Calendar.SATURDAY;// mCalendar.getFirstDayOfWeek();
        }

        mNumCells = mCalendar.getJalaliActualMaximum(JalaliTime.MONTH_DAY);//Utils.getDaysInMonth(mMonth, mYear);
        for (int i = 0; i < mNumCells; i++) {
            final int day = i + 1;
            if (sameDay(day, today)) {
                mHasToday = true;
                mToday = day;
            }
        }
        mNumRows = calculateNumRows();
    }

    public void reuse() {
        mNumRows = DEFAULT_NUM_ROWS;
        requestLayout();
    }

    private int calculateNumRows() {
        int offset = findDayOffset();
        int dividend = (offset + mNumCells) / mNumDays;
        int remainder = (offset + mNumCells) % mNumDays;
        return (dividend + (remainder > 0 ? 1 : 0));
    }

    private boolean sameDay(int day, Time today) {
        return mYear == today.year &&
                mMonth == today.month &&
                day == today.monthDay;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mRowHeight * mNumRows
                + MONTH_HEADER_SIZE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
    }

    private String getMonthAndYearString() {
        int flags = JalaliDateUtils.FORMAT_SHOW_DATE | JalaliDateUtils.FORMAT_SHOW_YEAR
                | JalaliDateUtils.FORMAT_NO_MONTH_DAY;
        mStringBuilder.setLength(0);
        long millis = mCalendar.toMillis(false);
        return JalaliDateUtils.formatDateRange(getContext(), mFormatter, millis, millis, flags).toString();
//    	return JalaliDateUtils.getMonthAndYearString(getContext(), mCalendar);
//        return "";
    }

    private void drawMonthTitle(Canvas canvas) {
        int x = (mWidth + 2 * mPadding) / 2;
        int y = (MONTH_HEADER_SIZE - MONTH_DAY_LABEL_TEXT_SIZE) / 2 + (MONTH_LABEL_TEXT_SIZE / 3);
        canvas.drawText(getMonthAndYearString(), x, y, mMonthTitlePaint);
    }

    private void drawMonthDayLabels(Canvas canvas) {
        int y = MONTH_HEADER_SIZE - (MONTH_DAY_LABEL_TEXT_SIZE / 2);
        int dayWidthHalf = (mWidth - mPadding * 2) / (mNumDays * 2);

        for (int i = 0; i < mNumDays; i++) {
            int calendarDay = (i + mWeekStart) % mNumDays;
            int x = mWidth - (2 * i + 1) * dayWidthHalf + mPadding;
//            mDayLabelCalendar.set(JalaliCalendar.DAY_OF_WEEK, calendarDay);
            canvas.drawText(JalaliDateUtils.getDayOfWeekString(getContext(), calendarDay, JalaliDateUtils.LENGTH_SHORT)
                    , x, y,
                    mMonthDayLabelPaint);
//            canvas.drawText(mDayLabelCalendar.getDisplayName(JalaliCalendar.DAY_OF_WEEK, JalaliCalendar.SHORT,
//            		Locale.getDefault()).toUpperCase(Locale.getDefault()), x, y,
//            		mMonthDayLabelPaint);
        }
    }

    /**
     * Draws the week and month day numbers for this week. Override this method
     * if you need different placement.
     *
     * @param canvas The canvas to draw on
     */
    protected void drawMonthNums(Canvas canvas) {
        int y = (((mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2) - DAY_SEPARATOR_WIDTH)
                + MONTH_HEADER_SIZE;
        int dayWidthHalf = (mWidth - mPadding * 2) / (mNumDays * 2);
        int j = findDayOffset();
//        j++;
        for (int dayNumber = 1; dayNumber <= mNumCells; dayNumber++) {
            int x = mWidth - (2 * j + 1) * dayWidthHalf + mPadding;

            int yRelativeToDay = (mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2 - DAY_SEPARATOR_WIDTH;

            int startX = x - dayWidthHalf;
            int stopX = x + dayWidthHalf;
            int startY = y - yRelativeToDay;
            int stopY = startY + mRowHeight;

            drawMonthDay(canvas, mYear, mMonth, dayNumber, x, y, startX, stopX, startY, stopY);

            j++;
            if (j == mNumDays) {
                j = 0;
                y += mRowHeight;
            }
        }
    }

    /**
     * This method should draw the month day.  Implemented by sub-classes to allow customization.
     *
     * @param canvas  The canvas to draw on
     * @param year  The year of this month day
     * @param month  The month of this month day
     * @param day  The day number of this month day
     * @param x  The default x position to draw the day number
     * @param y  The default y position to draw the day number
     * @param startX  The left boundary of the day number rect
     * @param stopX  The right boundary of the day number rect
     * @param startY  The top boundary of the day number rect
     * @param stopY  The bottom boundary of the day number rect
     */
    public abstract void drawMonthDay(Canvas canvas, int year, int month, int day,
            int x, int y, int startX, int stopX, int startY, int stopY);

    private int findDayOffset() {
         int d = (mDayOfWeekStart < mWeekStart ? (mDayOfWeekStart + mNumDays) : mDayOfWeekStart)
                - mWeekStart;
         if (d > 6)
        	 d = 0;
         return d;
    }


    /**
     * Calculates the day that the given x position is in, accounting for week
     * number. Returns the day or -1 if the position wasn't in a day.
     *
     * @param x The x position of the touch event
     * @return The day number, or -1 if the position wasn't in a day
     */
    public int getDayFromLocation(float x, float y) {
        int dayStart = mPadding;
        if (x < dayStart || x > mWidth - mPadding) {
            return -1;
        }
        // Selection is (x - start) / (pixels/day) == (x -s) * day / pixels
        int row = (int) (y - MONTH_HEADER_SIZE) / mRowHeight;
//        int column = (int) ((x - dayStart) * mNumDays / (mWidth - dayStart - mPadding));
        int column = (int) ((mWidth - x + dayStart) * mNumDays / (mWidth - dayStart - mPadding));

        int day = column - findDayOffset() + 1;
        day += row * mNumDays;
        if (day < 1 || day > mNumCells) {
            return -1;
        }
        return day;
    }

    /**
     * Called when the user clicks on a day. Handles callbacks to the
     * {@link OnDayClickListener} if one is set.
     *
     * @param day The day that was clicked
     */
    private void onDayClick(int day) {
        if (mOnDayClickListener != null) {
            mOnDayClickListener.onDayClick(this, new CalendarDay(mYear, mMonth, day));
        }
    }

    /**
     * Handles callbacks when the user clicks on a time object.
     */
    public interface OnDayClickListener {
        public void onDayClick(MonthView view, CalendarDay day);
    }
}
