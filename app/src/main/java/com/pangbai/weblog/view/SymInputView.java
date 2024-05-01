package com.pangbai.weblog.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.SymbolInputView;

public class SymInputView extends LinearLayout {
    private int textColor = Color.BLACK;
    private CodeEditor editor;
    public String[] defaultSym = new String[]{
            "Tab",
            "B",
            "I",
            ">",
            "F",
            "H1",
            "H2",
            "Hr",
            "D̶e̶l̶",
            "Nt",
            "List",
            "Link",
            "Img",
            "Code",


    };
    public String[] insertText = new String[]{
            "\t",
            "****",
            "__",
            "> ",
            "``",
            "# ",
            "## ",
            "-----\n",
            "~~~~",
            "[^]:",
            " - [x] ",
            "[]( )",
            "![]( )",
            "```js\n\n```"


    };
    public int[] offset = {
            1,
            2,
            1,
            2,
            1,
            2,
            3,
            6,
            2,
            2,
            7,
            1,
            2,
            6


    };

    public SymInputView(Context context) {
        super(context);

    }

    public SymInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SymInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    public void init(CodeEditor editor) {
        this.editor = editor;
        setOrientation(HORIZONTAL);
        addSymbols(defaultSym, insertText, offset);
    }

    public void setTextColor(int color) {
        for (int i = 0; i < getChildCount(); i++) {
            ((Button) getChildAt(i)).setTextColor(color);
        }
        textColor = color;
    }

    public void addSymbols(@NonNull String[] display, @NonNull final String[] insertText, int[] insertOffset) {
        int count = Math.max(display.length, insertText.length);
        for (int i = 0; i < count; i++) {
            var btn = new Button(getContext(), null, android.R.attr.buttonStyleSmall);
            btn.setText(display[i]);
            btn.setBackgroundColor(Color.TRANSPARENT);
            btn.setTextColor(textColor);
            addView(btn, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            int finalI = i;
            btn.setOnClickListener((view) -> {
                if (editor != null && editor.isEditable()) {
                    if ("\t".equals(insertText[finalI]) && editor.getSnippetController().isInSnippet()) {
                        editor.getSnippetController().shiftToNextTabStop();
                    } else {
                        editor.insertText(insertText[finalI], insertOffset[finalI]);
                    }
                }
            });
        }
    }
}
