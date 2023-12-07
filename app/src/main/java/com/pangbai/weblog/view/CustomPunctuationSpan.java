package com.pangbai.weblog.view;

import android.text.style.ForegroundColorSpan;

import com.google.android.material.color.DynamicColors;
import com.pangbai.weblog.R;

public class CustomPunctuationSpan extends ForegroundColorSpan {
    public CustomPunctuationSpan() {
        super(com.google.android.material.R.attr.colorOnPrimary); // RED
    }
}
