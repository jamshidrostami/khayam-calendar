package com.yeksefr.khayam;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckedTextView;

public class CustomCheckedTextView extends CheckedTextView {

	public CustomCheckedTextView(Context context) {
		super(context);
		setTypeface(CustomFont.droidnaskh);
	}

	public CustomCheckedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface(CustomFont.droidnaskh);
	}

	public CustomCheckedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTypeface(CustomFont.droidnaskh);
	}	
}
