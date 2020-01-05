package com.example.ciy;

import android.text.TextWatcher;
import android.widget.MultiAutoCompleteTextView;

public abstract class TextWatcherExtended implements TextWatcher {

    private int lastLength;

    public abstract void afterTextChanged(MultiAutoCompleteTextView s, boolean backSpace);

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        lastLength = s.length();
    }

    public void afterTextChanged(MultiAutoCompleteTextView s) {
        afterTextChanged(s, lastLength > s.length());
    }
}