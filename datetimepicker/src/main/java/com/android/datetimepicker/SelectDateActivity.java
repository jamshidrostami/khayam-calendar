package com.android.datetimepicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.android.datetimepicker.date.DatePickerDialog.OnDismissListener;
import com.yeksefr.khayam.CustomFont;
import com.yeksefr.khayam.JalaliTime;

public class SelectDateActivity extends Activity implements OnDateSetListener
{
	public static final String DATEPICKER_TAG = "datepicker";
	
	@Override
	protected void onCreate(Bundle arg0)
	{
		super.onCreate(arg0);
		
//		getSupportFragmentManager()
//		.beginTransaction()
//		.replace(R.id.content_frame, new DatePickerDialog())
//		.commit();
        CustomFont.droidnaskh = Typeface.createFromAsset(getAssets(), "fonts/DroidNaskh-Regular.ttf");
        CustomFont.droidnaskhBold = Typeface.create(CustomFont.droidnaskh, Typeface.BOLD);

		
		
		JalaliTime time = new JalaliTime();
		time.setToNow();
		
		DatePickerDialog datePickerDialog = null;
		
		Intent sender = getIntent();
		if (sender != null)
		{
			Bundle bundle = sender.getExtras();
			if (bundle != null)
			{
				int min = bundle.getInt("min");
				int max = bundle.getInt("max");
				int year = bundle.getInt("year");
				int month = bundle.getInt("month");
				int day = bundle.getInt("day");
				
				datePickerDialog = DatePickerDialog.newInstance(this, year, month, day);
				datePickerDialog.setYearRange(min, max);
			}
		}

		if (datePickerDialog == null)
		{
			datePickerDialog = DatePickerDialog.newInstance(this, time.jalaliYear, time.jalaliMonth, time.jalaliMonthDay);
		}
		
        datePickerDialog.setCancelable(true);
        datePickerDialog.setDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				finish();
			}
		});
        datePickerDialog.show(getFragmentManager(), DATEPICKER_TAG);
	}
	

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Intent data = new Intent();
        data.putExtra("year", year);
        data.putExtra("month", month);
        data.putExtra("day", day);
        
        setResult(RESULT_OK, data); 
        finish();
    }
}