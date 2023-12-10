package com.pangbai.weblog.editor.text;
import android.content.*;
import android.graphics.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.inputmethod.*;

import com.pangbai.weblog.R;

public class CodeEditView extends ViewGroup {

	private int extraWidth;
	private GestureDetector detector;	//手势对象
	private InputMethodManager imm;		//输入法对象
	private TextEditorView textEditor;	//自定义的EditText对象
	private int screenWidth,screenHeight;//屏幕的宽度和高度


	public CodeEditView(Context context) {
		super(context, null);
	}


	public CodeEditView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	public CodeEditView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void setTextEditor(TextEditorView textEditor) {
		this.textEditor = textEditor;
	}

	public TextEditorView getTextEditor() {
		return textEditor;

	}




	/* 初始化一些参数 */
	public void init(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		screenWidth = windowManager.getDefaultDisplay().getWidth();
		screenHeight = windowManager.getDefaultDisplay().getHeight();

		imm = (InputMethodManager)getContext()
			.getSystemService(Context.INPUT_METHOD_SERVICE);
	
		detector = new GestureDetector(new GestureListener(this));
		setBackgroundColor(context.getResources()
						   .getColor(R.color.colorSurface));
		
		
	}




	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		//获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		textEditor = (TextEditorView) getChildAt(2);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMode);
		int heightSize = MeasureSpec.getSize(heightMode);

		calcuDimension(widthMode, heightMode, widthSize, heightSize);

		// 计算出所有的childView的宽和高
		measureChildren(widthMeasureSpec, heightMeasureSpec);

	}



	/* 计算子View的大小 */
	public void calcuDimension(int widthMode, int heightMode
							   , int widthSize, int heightSize) {

		//计算子View的尺寸
		int width=0,height=0;
		int childWidth=0,childHeight=0;
		MarginLayoutParams mLayoutParams = null;

		for (int i=0;i < getChildCount();i++) {
			View childView = getChildAt(i);
			childWidth = getChildAt(i).getMeasuredWidth();
			childHeight = getChildAt(i).getMeasuredHeight();
			mLayoutParams = (MarginLayoutParams)childView.getLayoutParams();

			width = childWidth + mLayoutParams.leftMargin + mLayoutParams.rightMargin ;
			height = childHeight + mLayoutParams.topMargin + mLayoutParams.bottomMargin;

		}

		//设置View的尺寸
		if (width < screenWidth) {
			width = screenWidth + screenWidth / 2 ;
		} else {
			width = width + screenWidth / 2;
		}

		if (height < screenHeight) {
			height = screenHeight + screenHeight / 2;
		} else {
			height = height + screenHeight / 2;
		}
		extraWidth = width;

		//如果是wrap_content设置为我们计算的值，否则：直接设置为父容器计算的值
		setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? widthSize
							 : width, (heightMode == MeasureSpec.EXACTLY) ? heightSize: height);
	}




	/* 
	 * 父View对子View进行布局，按垂直线性进行布局
	 * 设置子View的宽度在为 childWidth +rectEnd
	 * 否则在子View外单击，不会改变当前行的背景色
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {


		int childWidth=0,childHeight=0;
		MarginLayoutParams mLayoutParams = null;
		for (int i=0;i < getChildCount();i++) {
			View childView = getChildAt(i);
			childWidth = getChildAt(i).getMeasuredWidth();
			childHeight = getChildAt(i).getMeasuredHeight();
			mLayoutParams = (MarginLayoutParams) childView.getLayoutParams();


			l = mLayoutParams.leftMargin;
			if (i == 0) {
				t = mLayoutParams.topMargin;
			} else {
				t = b + mLayoutParams.topMargin;
			}

			r = l + childWidth + extraWidth;
			b = t + childHeight ;
			
			childView.layout(l, t, r, b);
		}
	}

	

	/* 这三个方法必须覆写，否则无法获得MarginLayoutParams的对象和属性 */
	@Override
	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new MarginLayoutParams(getContext(), attrs);
	}

	@Override
	protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
		return new MarginLayoutParams(LayoutParams.FILL_PARENT,
									  LayoutParams.FILL_PARENT);
	}

	@Override
	protected ViewGroup.LayoutParams generateLayoutParams(
		ViewGroup.LayoutParams p) {
		return new MarginLayoutParams(p);
	}




	/* 此处交给手势去处理，就能在子View以外单击也能弹出输入法 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO: Implement this method
		
		return detector.onTouchEvent(event);
	}


	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO: Implement this method
		
		return super.dispatchTouchEvent(ev);
	}


	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO: Implement this method
		
		return super.dispatchKeyEvent(event);
	}



	/* 显示隐藏输入法 */
	public void showIME(boolean show) {
		if (show) {
			imm.showSoftInput(textEditor, 0);
		} else {
			imm.hideSoftInputFromWindow(textEditor.getWindowToken(), 0); 
		}
	}

}
