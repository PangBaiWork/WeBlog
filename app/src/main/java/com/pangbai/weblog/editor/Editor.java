package com.pangbai.weblog.editor;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

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
    public void setFont(@Nullable  String path){
        Typeface font;
        if (path==null) {
            font= Typeface.createFromAsset(getContext().getAssets(), "font/JetBrainsMono-Regular.ttf");
            setTypefaceText(font);

        }else {
            try {
                font=Typeface.createFromFile(path);
                setTypefaceText(font);
            }catch (Exception ignored){

            }


        }

    }

    // Disable inputMethod suggestion when editor focused
    @Override
    protected void updateExtractedText(){
        Log.e("editor","调用了1");
     }

}
