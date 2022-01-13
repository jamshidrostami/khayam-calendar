package com.yeksefr.khayam;

import android.content.Context;
import android.util.AttributeSet;

import com.android.ex.chips.RecipientEditTextView;

public class CustomRecipientEditTextView extends RecipientEditTextView {

	public CustomRecipientEditTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface(CustomFont.droidnaskh);
	}
}