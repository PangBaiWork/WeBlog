package com.pangbai.weblog.editor.text;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import java.util.regex.*;

public class TextEditorView extends EditText {

	private Resources res;
	private Paint textPaint;
	private Paint rectPaint;		 	//绘制当前行的背景色画笔
	private int rectStartX,rectStartY;	//绘制当前行的背景色的开始X Y坐标
	private int rectEndX,rectEndY;	  	//绘制当前行的背景色的结束X Y坐标
	
	private int screenWidth,screenHeight;//屏幕的宽度和高度
	private float textX,textY;
	private Handler updateHandler ;
	private Paint.FontMetrics metrics;
	private int updateDelay,errorLine;
	private boolean textChange = false;
	private boolean modified = true;

	public Pattern line,number,headfile;
	public Pattern string,keyword;
	public Pattern pretreatment,builtin ;
	public Pattern comment,trailingWhiteSpace ;

	private final int MARGIN_LEFT = 100;//文本左侧的间距

	private OnTextChangedListener onTextChangedListener;

	private Runnable updateThread = new Runnable() {
		@Override
		public void run() {
			Editable edit = getText();

			if (onTextChangedListener != null)
				onTextChangedListener.onTextChanged(edit.toString());

			highlightWithoutChange(edit);
		}
	};


	public TextEditorView(Context context) {
		super(context, null);
	}

	public TextEditorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}


	/* 初始化一些参数 */ 
	public void init(Context context) {

		setGravity(Gravity.TOP);
		setPadding(100, 0, 0, 0);
		textPaint = new Paint();
		textPaint.setAntiAlias(false);
		textPaint.setTextSize(dip2px(context, 17));
		rectPaint = new Paint();
		rectPaint.setColor(context.getResources()
						   .getColor(R.color.current_row_backgroud));

		textPaint.setColor(0xff888888);

		setHorizontallyScrolling(true);
		updateHandler = new Handler();
		setFilters(new InputFilter[] {inputFilter});
		addTextChangedListener(watcher);
		res = context.getResources();

		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		screenWidth = windowManager.getDefaultDisplay().getWidth();
		screenHeight = windowManager.getDefaultDisplay().getHeight();
		
		metrics = textPaint.getFontMetrics();
		rectStartX = MARGIN_LEFT;
		textY = metrics.descent - metrics.ascent + metrics.leading;

		initPattern();
	}


	/* 初始化正则匹配的模板 */
	public void initPattern() {
		line = Pattern.compile(".*\\n");
		headfile = Pattern.compile("#\\b(include)\\b\\s*<\\w*(/?.*/?)[\\w+|h]>[^\"]");
		number = Pattern.compile("\\b(\\d*[.]?\\d+)\\b");
		string = Pattern.compile("\"(\\\"|.)*?\"");
		keyword = Pattern.compile("\\b(auto|int|short|double|float|void|long|signed|unsigned|char|struct|public|protected|private|class|union|bool|string|vector|typename|"
								  + "do|for|while|if|else|switch|case|default|new|delete|true|false|typedef|static|const|register|extern|volatile|"
								  + "goto|return|continue|break|using|namespace|try|catch|import|package)\\b");
		pretreatment = Pattern.compile("[^\"]#\\b(ifdef|ifndef|define|undef|if|else|elif|endif|pragma|"
									   + "error|line)\\b[^\"]");
		builtin = Pattern.compile("[^\"]\\b(printf|scanf|std::|cout|cin|cerr|clog|endl|template|"
								  + "sizeof)\\b[^\"]");		
		comment = Pattern.compile("/\\*(.|[\r\n])*?(\\*/)|/\\*(.|[\r\n])*|[^\"](?<!:)//.*[^\"]");
		trailingWhiteSpace = Pattern.compile("[\\t ]+$", Pattern.MULTILINE);		 

	}



	/*
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public float dip2px(Context context, float dpValue) {
		float scale =context.getResources().getDisplayMetrics().density;
		return dpValue * scale + 0.5f;
	}


	/*
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public float px2dip(Context context, float pxValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return pxValue / scale + 0.5f;
	}



	/* 对输入的内容进行过滤 */
	private InputFilter inputFilter = new InputFilter(){

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
								   Spanned dest, int dstart, int dend) {
			if (modified && end - start == 1 && start < source.length()
				&& dstart < dest.length()) {
				char c = source.charAt(start);

				if (c == '\n')
					return autoIndent(source, start, end, dest, dstart, dend);
			}
			return source;
		}
	};


	/* 对EditText输入内容进行判断 */
    private TextWatcher watcher = new TextWatcher(){

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO: Implement this method
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO: Implement this method
        }

        @Override
        public void afterTextChanged(Editable edit) {
            // TODO: Implement this method
            //匹配不包含中文[^\u4e00-\u9fa5]+   /*只包含中文[\u4e00-\u9fa5]+*/
			//[\\s|\\S].*匹配当前行任意字符
			cancelUpdate();

			if (!modified)
				return;
			textChange = true;
			updateHandler.postDelayed(updateThread, updateDelay);
        }
	};



	public void setTextHighlighted(CharSequence text) {
		cancelUpdate();

		errorLine = 0;
		textChange = false;

		modified = false;
		setText(highlight(new SpannableStringBuilder(text)));
		modified = true;

		if (onTextChangedListener != null)
			onTextChangedListener.onTextChanged(text.toString());
	}



	public String getCleanText() {
		return trailingWhiteSpace.matcher(getText()).replaceAll("");
	}



	public void refresh() {
		highlightWithoutChange(getText());
	}


	public void cancelUpdate() {
		updateHandler.removeCallbacks(updateThread);
	}


	public void highlightWithoutChange(Editable e) {
		modified = false;
		highlight(e);
		modified = true;
	}


	/* 实现语法高亮 */
	public Editable highlight(Editable edit) {
		try {
			clearSpans(edit);

			if (edit.length() == 0)
				return edit;

			if (errorLine > 0) {
				Matcher m = line.matcher(edit);

				for (int n = errorLine; n-- > 0 && m.find();)
					edit.setSpan(new BackgroundColorSpan(res.getColor(R.color.syntax_error)), m.start(),
								 m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			for (Matcher m = headfile.matcher(edit); m.find();)
				edit.setSpan(new ForegroundColorSpan(res.getColor(R.color.syntax_headfile)), m.start(),
							 m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			for (Matcher m = number.matcher(edit); m.find();)
				edit.setSpan(new ForegroundColorSpan(res.getColor(R.color.syntax_number)), m.start(),
							 m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			for (Matcher m = string.matcher(edit); m.find();)
				edit.setSpan(new ForegroundColorSpan(res.getColor(R.color.syntax_string)), m.start(),
							 m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			for (Matcher m = keyword.matcher(edit); m.find();)
				edit.setSpan(new ForegroundColorSpan(res.getColor(R.color.syntax_keyword)), m.start(),
							 m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			for (Matcher m = pretreatment.matcher(edit); m.find();)
				edit.setSpan(new ForegroundColorSpan(res.getColor(R.color.syntax_pretreatment)),
							 m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			for (Matcher m = builtin.matcher(edit); m.find();)
				edit.setSpan(new ForegroundColorSpan(res.getColor(R.color.syntax_builtin)), m.start(),
							 m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			for (Matcher m = comment.matcher(edit); m.find();)
				edit.setSpan(new ForegroundColorSpan(res.getColor(R.color.syntax_comment)), m.start(),
							 m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		} catch (Exception ex) {
		}

		return edit;
	}


	/* 移除span */
	public void clearSpans(Editable e) {

		ForegroundColorSpan foreSpan[] = e.getSpans(0, e.length(),
													ForegroundColorSpan.class);

		for (int n = foreSpan.length; n-- > 0;)
			e.removeSpan(foreSpan[n]);

		BackgroundColorSpan backSpan[] = e.getSpans(0, e.length(),
													BackgroundColorSpan.class);

		for (int n = backSpan.length; n-- > 0;)
			e.removeSpan(backSpan[n]);

	}

	/* 自动插入内容 */
	public CharSequence autoIndent(CharSequence source, int start, int end,
								   Spanned dest, int dstart, int dend) {
		String indent = "";
		int istart = dstart - 1;
		int iend = -1;

		boolean dataBefore = false;
		int pt = 0;

		for (; istart > -1; --istart) {
			char c = dest.charAt(istart);

			if (c == '\n')
				break;

			if (c != ' ' && c != '\t') {
				if (!dataBefore) {
					if (c == '{' || c == '+' || c == '-' || c == '*'
						|| c == '/' || c == '%' || c == '^' || c == '=')
						--pt;

					dataBefore = true;
				}

				if (c == '(')
					--pt;
				else if (c == ')')
					++pt;
			}
		}

		if (istart > -1) {
			char charAtCursor = dest.charAt(dstart);

			for (iend = ++istart; iend < dend; ++iend) {
				char c = dest.charAt(iend);

				if (charAtCursor != '\n' && c == '/' && iend + 1 < dend
					&& dest.charAt(iend) == c) {
					iend += 2;
					break;
				}

				if (c != ' ' && c != '\t')
					break;
			}

			indent += dest.subSequence(istart, iend);
		}

		if (pt < 0)
			indent += "\t";

		return source + indent;
	}



	/* 跳转到指定行 */
	public boolean gotoLine(int line) {
		--line;

		if (line > getLineCount()) {
			setSelection(getText().toString().length());
			return false;
		}

		Layout layout = getLayout();
		setSelection(layout.getLineStart(line), layout.getLineEnd(line));
		return true;
	}

	

	

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO: Implement this method
		canvas.save();
		drawText(canvas);
		canvas.restore();
		super.onDraw(canvas);
	}

	/* 绘制文本行 */
	public void drawText(Canvas canvas) {
		if (!hasSelection()) {
			rectEndX = getLayout().getWidth();
			rectStartY = getRowHeight() * (getCurrRow() - 1) + getRowHeight() / 8;
			rectEndY = getRowHeight() * getCurrRow() + getRowHeight() / 8;
			canvas.drawRect(rectStartX, rectStartY, rectEndX, rectEndY, rectPaint);
		}

		if (getText().toString().length() != 0) {
			for (int i=0;i < getLineCount();i++) {
				textY = (i + 1) * getLineHeight(); 
				canvas.drawText(String.valueOf(i + 1), textX, textY, textPaint);
			}
		} else {
			canvas.drawText(String.valueOf(1), textX, textY, textPaint);
		}
	}

	
	
	/* 获得行 行高 当前行 */
	public int getRowHeight() {

		return getLineHeight();
	}

	public int getTotalRows() {
		return getLineCount();
	}

	public int getCurrRow() {
		return getLayout().getLineForOffset(getSelectionStart()) + 1;
	}


	public interface OnTextChangedListener {
		public void onTextChanged(String text);
	}


}
