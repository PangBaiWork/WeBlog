package com.pangbai.weblog.editor;

import static com.pangbai.weblog.tool.util.runOnUiThread;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.inputmethod.ExtractedTextRequest;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.pangbai.weblog.tool.IO;
import com.pangbai.weblog.tool.ThreadUtil;

import java.io.FileOutputStream;
import java.io.IOException;

import io.github.rosemoe.sora.text.ContentIO;
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
