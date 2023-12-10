
package com.pangbai.weblog.editor.text;

import android.content.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.app.*;

/*
 * 手势监听处理，在子View以外单击时弹出输入法
 */
 
public class GestureListener extends GestureDetector.SimpleOnGestureListener
implements GestureDetector.OnGestureListener {

	
	private CodeEditView codeEditor;
	
	public GestureListener(CodeEditView codeEditor){
		this.codeEditor = codeEditor;
	}
	
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO: Implement this method
		return true;
	}


	/* down事件发生而move或up还没发生前，触发该事件 */
	@Override
	public void onShowPress(MotionEvent e) {
		// TODO: Implement this method

	}

	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO: Implement this method
		codeEditor.showIME(true);
		return true;
	}


	/* 在屏幕上拖动事件，在ACTION_MOVE动作发生时触发，会多次触发 */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// TODO: Implement this method

		return true;
	}


	/* 长按事件，Touch了不移动一直Touch down时触发 */
	@Override
	public void onLongPress(MotionEvent e) {
		// TODO: Implement this method
	}


	/* 滑动手势事件，Touch了滑动一点距离后，在抬起时才会触发 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// TODO: Implement this method
		return true;
	}


}
