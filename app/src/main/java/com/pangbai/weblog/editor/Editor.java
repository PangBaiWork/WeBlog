package com.pangbai.weblog.editor;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.inputmethod.ExtractedTextRequest;

import androidx.annotation.Nullable;

import io.github.rosemoe.sora.widget.CodeEditor;

public class Editor extends CodeEditor {
    public Editor(Context context) {
        super(context);
    }

    public Editor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Editor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
    }

    public Editor(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // Disable inputMethod suggestion when editor focused
    @Override
    protected void updateExtractedText(){
        Log.e("editor","调用了1");
     }


}
