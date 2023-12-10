package com.pangbai.weblog.editor.text;

import android.content.*;
import android.util.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;

public class HorScrollViewText extends HorizontalScrollView {

	
	private CodeEditView codeEditor;

	public HorScrollViewText(Context context) {
		super(context);
		
	}


	public HorScrollViewText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO: Implement this method
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		//codeEditor = (CodeEditorView) getChildAt(0);
		
		
	}


	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO: Implement this method
		
		return super.onTouchEvent(ev);
	}

	@Override
	public boolean executeKeyEvent(KeyEvent event) {
		// TODO: Implement this method
		if(event.getKeyCode()==KeyEvent.KEYCODE_ENTER){
			scrollTo(0,getScrollY());
		}
		
		
		return super.executeKeyEvent(event);
	}

	
	
	public void setContainerView(CodeEditView codeEditor) {
		this.codeEditor = codeEditor;
	}

	public CodeEditView getContainerView() {
		return codeEditor;
	}
	
	
}
