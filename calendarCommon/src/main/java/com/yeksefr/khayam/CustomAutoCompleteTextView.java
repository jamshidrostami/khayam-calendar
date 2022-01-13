package com.yeksefr.khayam;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class CustomAutoCompleteTextView extends AutoCompleteTextView {

	public CustomAutoCompleteTextView(Context context) {
		super(context);
		setTypeface(CustomFont.droidnaskh);
	}

	public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface(CustomFont.droidnaskh);
	}

	public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTypeface(CustomFont.droidnaskh);
	}	
}
