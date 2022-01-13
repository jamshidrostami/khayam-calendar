package com.yeksefr.khayam;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class LongSummeryPreference extends Preference {

    public LongSummeryPreference(Context context) {
        super(context);
    }

    public LongSummeryPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public LongSummeryPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView summaryView = (TextView) view.findViewById(android.R.id.summary);
        summaryView.setMaxLines(20);
    }
}