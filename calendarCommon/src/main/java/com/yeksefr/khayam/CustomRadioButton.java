package com.yeksefr.khayam;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class CustomRadioButton extends RadioButton {

	public CustomRadioButton(Context context) {
		super(context);
		setTypeface(CustomFont.droidnaskh);
	}

	public CustomRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface(CustomFont.droidnaskh);
	}

	public CustomRadioButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTypeface(CustomFont.droidnaskh);
	}	
}
