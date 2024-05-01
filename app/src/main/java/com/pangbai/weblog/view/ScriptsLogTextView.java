package com.pangbai.weblog.view;

import android.content.Context;
import android.graphics.Color;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

public class ScriptsLogTextView extends  ScrollView{

 AppCompatTextView textView;
    public ScriptsLogTextView(Context context) {
        super(context);
        setPadding(10,10,10,10);

        setBackgroundColor(Color.WHITE);
        textView=  new AppCompatTextView(context);
        addView(textView);
        textView.setTextIsSelectable(true);

    }

    public ScriptsLogTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScriptsLogTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
   void autoScroll(){
     post(() -> fullScroll(ScrollView.FOCUS_UP));
    }
   public void appendScriptText(List<String> scripts) {
        textView.append("> "+scripts+'\n');
        autoScroll();
    }

   public void appendLogText(String script) {
      textView.append(script+'\n');
       autoScroll();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            ViewParent viewParent = findViewParentIfNeeds(this);
            if (viewParent != null) {
                viewParent.requestDisallowInterceptTouchEvent(true);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        if (clampedX) {
            ViewParent viewParent = findViewParentIfNeeds(this);
            if (viewParent != null) {
                viewParent.requestDisallowInterceptTouchEvent(false);
            }
        }
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    private ViewParent findViewParentIfNeeds(View tag) {
        ViewParent parent = tag.getParent();
        if (parent == null) {
            return null;
        }
        if ( parent instanceof ScrollView || parent instanceof ViewPager || parent instanceof AbsListView ||parent instanceof HorizontalScrollView) {
            return parent;
        } else {
            return parent;
        }


    }

}
