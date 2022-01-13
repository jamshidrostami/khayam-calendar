package com.android.datetimepicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import com.android.datetimepicker.date.DatePickerDialog.OnDismissListener;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.android.datetimepicker.time.TimePickerDialog.OnTimeSetListener;
import com.yeksefr.khayam.CustomFont;
import com.yeksefr.khayam.JalaliTime;

public class SelectTimeActivity extends Activity implements OnTimeSetListener
{
	public static final String TIMEPICKER_TAG = "timepicker";
	
	@Override
	protected void onCreate(Bundle arg0)
	{
		super.onCreate(arg0);
		
        CustomFont.droidnaskh = Typeface.createFromAsset(getAssets(), "fonts/DroidNaskh-Regular.ttf");
        CustomFont.droidnaskhBold = Typeface.create(CustomFont.droidnaskh, Typeface.BOLD);

		boolean is24hourMode = true;
		
		JalaliTime time = new JalaliTime();
		time.setToNow();
		
		Intent sender = getIntent();
		if (sender != null)
		{
			Bundle bundle = sender.getExtras();
			if (bundle != null)
			{
				time.hour = bundle.getInt("hour");
				time.minute = bundle.getInt("minute");
				is24hourMode = bundle.getBoolean("is24hour");
			}
		}
		
		final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, time.hour, time.minute, is24hourMode);
		timePickerDialog.setCancelable(true);
		timePickerDialog.setDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				finish();
			}
		});
		timePickerDialog.show(getFragmentManager(), TIMEPICKER_TAG);
	}
	
	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
		Intent data = new Intent();
		data.putExtra("hour", hourOfDay);
		data.putExtra("minute", minute);
		//TODO check 12h & 24h
		
		setResult(RESULT_OK, data); 
		finish();
	}
}