package com.yeksefr.khayam;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class CustomEditText extends EditText {

	public CustomEditText(Context context) {
		super(context);
		setTypeface(CustomFont.droidnaskh);
	}

	public CustomEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface(CustomFont.droidnaskh);
	}

	public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTypeface(CustomFont.droidnaskh);
	}	
}
