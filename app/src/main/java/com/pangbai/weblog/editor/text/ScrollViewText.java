package com.pangbai.weblog.editor.text;
import android.content.*;
import android.widget.*;
import android.util.*;
import android.view.*;

public class ScrollViewText extends ScrollView {

	private HorScrollViewText HorScrollText;

	public ScrollViewText(Context context) {
		super(context);
	}


	public ScrollViewText(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO: Implement this method
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		HorScrollText = (HorScrollViewText) getChildAt(0);

	}


	@Override
	public boolean executeKeyEvent(KeyEvent event) {
		// TODO: Implement this method
		
		return super.executeKeyEvent(event);
	}

}
