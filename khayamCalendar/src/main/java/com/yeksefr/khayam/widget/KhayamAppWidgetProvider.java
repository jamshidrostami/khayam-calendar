package com.yeksefr.khayam.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.yeksefr.khayam.CalendarApplication;

public class KhayamAppWidgetProvider extends AppWidgetProvider {

    /** Name of intent to broadcast to activate this receiver. */
	public static String ACTION_REFRESH = "com.yeksefr.intent.action.ACTION_REFRESH";

    private CalendarApplication mApp;

    public KhayamAppWidgetProvider() {
    }

    private CalendarApplication getApp(Context context) {
        if (mApp == null) {
            mApp = (CalendarApplication) context.getApplicationContext();
        }
        return mApp;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (ACTION_REFRESH.equals(action) || action.equals("android.intent.action.TIME_SET") || action.equals("android.intent.action.TIMEZONE_CHANGED")
        		|| action.equals("android.intent.action.DATE_CHANGED"))
        {
        	ClockService.start(context, null, true);
        	return;        	
        }
        
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context,
            AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {

        appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(
                    new ComponentName(context, this.getClass()));

        if (appWidgetIds == null || appWidgetIds.length == 0) {
            return;
        }

        ClockService.start(context, appWidgetIds, true);
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
        ClockService.stop();
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
}