/*
 * Copyright (C) 2007 The Android Open Source Project
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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.RemoteViews;

import com.yeksefr.khayam.widget.ClockService;

public class CalendarApplication extends Application {
	
	static final String TAG = "KhayamAppWidgetProvider";
	static Timer timer = null;
	static String[] themeBackgroundString = { "white", "dark" };
	static String[] themeFontString = { "dark", "light" };
	static int[] themeNightRes = { R.drawable.ic_night_light,
			R.drawable.ic_night_dark };
	static int[] themeDayRes = { R.drawable.ic_day_light,
			R.drawable.ic_day_dark };
	static int[] themeColorRes = { R.color.widget_gray_light,
			R.color.widget_gray_dark };
	static int[] themeColorShadowRes = { R.color.widget_gray_shadow_light,
			R.color.widget_gray_shadow_dark };
	static int[] themeAlarmIcon = { R.drawable.ic_alarm_light,
			R.drawable.ic_alarm_dark };

	private BroadcastReceiver mStickyReceiver;
	private boolean mScreenOn = false;

    @Override
    public void onCreate() {
        super.onCreate();

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mScreenOn = pm.isScreenOn();

        /*
         * Ensure the default values are set for any receiver, activity,
         * service, etc. of Calendar
         */
        GeneralPreferences.setDefaultValues(this);

        CustomFont.droidnaskh = Typeface.createFromAsset(getAssets(), "fonts/DroidNaskh-Regular.ttf");
        CustomFont.droidnaskhBold = Typeface.create(CustomFont.droidnaskh, Typeface.BOLD);
        
        // Save the version number, for upcoming 'What's new' screen.  This will be later be
        // moved to that implementation.
        Utils.setSharedPreference(this, GeneralPreferences.KEY_VERSION,
                Utils.getVersionCode(this));

        // Initialize the registry mapping some custom behavior.
        ExtensionsFactory.init(getAssets());
        
		ClockService.start(getApplicationContext(), null, false);
		registerScreenStateReceiver();
    }
    

	@Override
	public void onTerminate() {
		removeScreenStateReceiver();
		super.onTerminate();
	}

	private void registerScreenStateReceiver() {

		IntentFilter ioff = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		IntentFilter ion = new IntentFilter(Intent.ACTION_SCREEN_ON);

		mStickyReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();

				if (Intent.ACTION_SCREEN_OFF.equals(action)) {
					setIsScreenOn(false);
				} else {
					setIsScreenOn(true);
					// start the service
					ClockService.start(getApplicationContext(), null, true);
				}
			}
		};

		registerReceiver(mStickyReceiver, ioff);
		registerReceiver(mStickyReceiver, ion);
	}

	private void removeScreenStateReceiver() {
		if (mStickyReceiver != null) {
			unregisterReceiver(mStickyReceiver);
		}
	}

	/**
	 * Updates the clock display for the given widget ids
	 */
	public void updateRemoteView(AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		if (appWidgetIds == null || appWidgetManager == null)
			return;

		for (int widgetId : appWidgetIds) {
			RemoteViews rviews = configureRemoteView(widgetId, true /* enableTouch */);
			appWidgetManager.updateAppWidget(new int[] { widgetId }, rviews);
		}
	}

	/**
	 * Configures an existing remote views mapping our clock widget
	 * {@link R.layout#clock_widget} with the current state of that widget id.
	 * 
	 * TODO this calls for some serious optimizations, e.g. add flags to only
	 * update what changed in the views (time, am/pm, intent, etc.) and avoid
	 * the expensive date format.
	 */
	public RemoteViews configureRemoteView(int widgetId, boolean enableTouch) {
		if (paint == null) {
			paint = new Paint();
			Typeface clock = Typeface.createFromAsset(getAssets(),
					"fonts/DroidNaskh-Regular.ttf");
			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);
			paint.setSubpixelText(true);
			paint.setTypeface(clock);
			paint.setStyle(Paint.Style.FILL);
			paint.setTextSize(36);
		}

		SharedPreferences prefs = GeneralPreferences
				.getSharedPreferences(getApplicationContext());
		String defaultThemetring = prefs.getString(
				GeneralPreferences.KEY_WIDGET_THEME,
				GeneralPreferences.THEME_LIGHT);
		theme = Integer.parseInt(defaultThemetring);

		// get our view so we can edit the time
		RemoteViews views = new RemoteViews(getPackageName(),
				R.layout.appwidget_farsi);

		JalaliTime cal = new JalaliTime();
		try {
			cal.setToNow();
		} catch (Exception e) {
			e.printStackTrace();
		}

		int hour = cal.hour;
		views.setViewVisibility(R.id.ampm, View.VISIBLE);
		if (hour > 6 && hour < 19) {
			views.setImageViewResource(R.id.ampm, themeDayRes[theme]);
		} else {
			views.setImageViewResource(R.id.ampm, themeNightRes[theme]);
		}
		
		if (!DateFormat.is24HourFormat(getApplicationContext())) {
			if (hour > 12) {
				hour -= 12;
			}
		} else {
			views.setViewVisibility(R.id.ampm, View.INVISIBLE);
		}

		views.setImageViewResource(
				R.id.hour_bg,
				getDrawableResource(getApplicationContext(), new StringBuilder(
						"lp_back_flaps_").append(themeBackgroundString[theme])
						.toString()));
		views.setImageViewResource(
				R.id.minute_bg,
				getDrawableResource(getApplicationContext(), new StringBuilder(
						"lp_back_flaps_").append(themeBackgroundString[theme])
						.toString()));

		views.setImageViewResource(
				R.id.hour1,
				getDrawableResource(getApplicationContext(), new StringBuilder(
						"digit_fa_bold_").append(themeFontString[theme])
						.append(hour / 10).toString()));
		views.setImageViewResource(
				R.id.hour2,
				getDrawableResource(getApplicationContext(), new StringBuilder(
						"digit_fa_bold_").append(themeFontString[theme])
						.append(hour % 10).toString()));
		views.setImageViewResource(
				R.id.minute1,
				getDrawableResource(getApplicationContext(), new StringBuilder(
						"digit_fa_normal_").append(themeFontString[theme])
						.append(cal.minute / 10).toString()));
		views.setImageViewResource(
				R.id.minute2,
				getDrawableResource(getApplicationContext(), new StringBuilder(
						"digit_fa_normal_").append(themeFontString[theme])
						.append(cal.minute % 10).toString()));

		long mills = cal.toMillis(true);
		buildDayMonth(getApplicationContext(),
				JalaliDateUtils.getDayAndMonthString(getApplicationContext(),
						mills));
		views.setImageViewBitmap(R.id.day_month, dayMonth);
		buildWeekDay(getApplicationContext(),
				JalaliDateUtils.getDayOfWeekString(getApplicationContext(),
						cal.weekDay, JalaliDateUtils.LENGTH_LONG));
		views.setImageViewBitmap(R.id.weekday, weekday);

		String whenString = Settings.System.getString(getContentResolver(),
				Settings.System.NEXT_ALARM_FORMATTED);

		if (whenString != null && whenString.length() != 0) {
			views.setViewVisibility(R.id.alarm_icon, View.VISIBLE);
			views.setImageViewResource(R.id.alarm_icon, themeAlarmIcon[theme]);

			views.setViewVisibility(R.id.alarm_time, View.VISIBLE);
			buildAlarmTime(getApplicationContext(),
					convertTime(getApplicationContext(), whenString));
			views.setImageViewBitmap(R.id.alarm_time, alarmTime);
		} else {
			views.setViewVisibility(R.id.alarm_icon, View.INVISIBLE);
			views.setViewVisibility(R.id.alarm_time, View.INVISIBLE);
		}

		return views;
	}

	private String convertTime(Context context, String date) {
		String[] array = context.getResources().getStringArray(
				R.array.shortWeekdayNames);
		int i = 0;
		date = date.replaceAll("Sun", array[i++]);
		date = date.replaceAll("Mon", array[i++]);
		date = date.replaceAll("Tue", array[i++]);
		date = date.replaceAll("Wed", array[i++]);
		date = date.replaceAll("Thu", array[i++]);
		date = date.replaceAll("Fri", array[i++]);
		date = date.replaceAll("Sat", array[i++]);

		date = date.replaceAll("AM", context.getString(R.string.am));
		date = date.replaceAll("am", context.getString(R.string.am));
		date = date.replaceAll("PM", context.getString(R.string.pm));
		date = date.replaceAll("pm", context.getString(R.string.pm));

		return Helper.convertEnglishNumbersToParsi(date);
	}

	Paint paint;
	int theme = 0;
	Bitmap dayMonth;
	Bitmap weekday;
	Bitmap alarmTime;

	public void buildDayMonth(Context context, String text) {
		paint.setTextSize(context.getResources().getDimension(
				R.dimen.widget_font_size));
		paint.setTextAlign(Align.CENTER);
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);

		dayMonth = null;
		int h = bounds.height() + 6;
		dayMonth = Bitmap.createBitmap(bounds.width(), h,
				Bitmap.Config.ARGB_4444);
		Canvas myCanvas = new Canvas(dayMonth);

		paint.setColor(context.getResources().getColor(
				themeColorShadowRes[theme]));
		myCanvas.drawText(text, bounds.width() / 2 + 1,
				paint.getFontMetrics().bottom + 3, paint);
		paint.setColor(context.getResources().getColor(themeColorRes[theme]));
		myCanvas.drawText(text, bounds.width() / 2,
				paint.getFontMetrics().bottom + 2, paint);
	}

	public void buildWeekDay(Context context, String text) {
		paint.setTextSize(context.getResources().getDimension(
				R.dimen.widget_font_size));
		paint.setTextAlign(Align.CENTER);
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);

		weekday = null;
		int h = bounds.height() + 6;
		weekday = Bitmap.createBitmap(bounds.width(), h,
				Bitmap.Config.ARGB_4444);
		Canvas myCanvas = new Canvas(weekday);

		paint.setColor(context.getResources().getColor(
				themeColorShadowRes[theme]));
		myCanvas.drawText(text, bounds.width() / 2 + 1,
				paint.getFontMetrics().bottom + 3, paint);
		paint.setColor(context.getResources().getColor(themeColorRes[theme]));
		myCanvas.drawText(text, bounds.width() / 2,
				paint.getFontMetrics().bottom + 2, paint);
	}

	public void buildAlarmTime(Context context, String text) {
		paint.setTextSize(context.getResources().getDimension(
				R.dimen.widget_font_size_2));
		paint.setTextAlign(Align.LEFT);
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);

		alarmTime = null;
		int h = bounds.height() + 6;
		alarmTime = Bitmap.createBitmap(bounds.width(), h,
				Bitmap.Config.ARGB_4444);
		Canvas myCanvas = new Canvas(alarmTime);

		paint.setColor(context.getResources().getColor(
				themeColorShadowRes[theme]));
		myCanvas.drawText(text, 1, paint.getFontMetrics().bottom + 4, paint);
		paint.setColor(context.getResources().getColor(themeColorRes[theme]));
		myCanvas.drawText(text, 0, paint.getFontMetrics().bottom + 3, paint);
	}

	public int getDrawableResource(Context context, String s) {
		return context.getResources().getIdentifier(s, "drawable",
				"com.yeksefr.khayam");
	}

	public void setIsScreenOn(boolean isScreenOn) {
		mScreenOn = isScreenOn;
	}

	public boolean isScreenOn() {
		return mScreenOn;
	}
}