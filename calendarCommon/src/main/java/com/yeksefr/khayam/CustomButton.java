package com.yeksefr.khayam;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class CustomButton extends Button {

	public CustomButton(Context context) {
		super(context);
		setTypeface(CustomFont.droidnaskh);
	}

	public CustomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface(CustomFont.droidnaskh);
	}

	public CustomButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTypeface(CustomFont.droidnaskh);
	}	
}
