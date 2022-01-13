package com.yeksefr.khayam;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Switch;

public class CustomSwitch extends Switch {

	public CustomSwitch(Context context) {
		super(context);
		setTypeface(CustomFont.droidnaskh);
	}

	public CustomSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface(CustomFont.droidnaskh);
	}

	public CustomSwitch(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTypeface(CustomFont.droidnaskh);
	}	
}
