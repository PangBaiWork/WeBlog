package com.pangbai.weblog.editor.text;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import com.select.file.*;
import java.io.*;
import java.util.*;
import android.widget.SearchView.*;

public class MainActivity extends Activity {

	private TextEditorView textEditor;
	private ImageView fileTypeImg;
	private List<String> pathList;
	private List<String> textList;
	private String sdcardPath;
	private CodeEditView codeEditor;
	private SearchView searchView;
	private boolean isGotoLine = false;
	private int screenWidth,screenHeight;
	
	private TextView fileNameText,filePathText;
	private final int REQUEST_CODE = 0;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		screenWidth = windowManager.getDefaultDisplay().getWidth();
		screenHeight = windowManager.getDefaultDisplay().getHeight();
		
		initView();
		
		
    }


	public void initView() {
		textEditor = (TextEditorView) super.findViewById(R.id.textEditor);

		//codeEditor = (CodeEditorView) super.findViewById(R.id.codeEditor);
		fileTypeImg = (ImageView) super.findViewById(R.id.fileTypeImg);
		fileNameText = (TextView) super.findViewById(R.id.fileNameText);
		filePathText = (TextView) super.findViewById(R.id.filePathText);

		textList = new ArrayList<String>();
		textEditor.setHighlightColor(getResources().getColor(R.color.hightlight_color));
		textEditor.setText("#include <stdio.h>\n\n/* int printf(const char*,...); */\n"
						   + "int main(int argc,char** argv) {"
						   + "\n\t\tprintf(\"hello world!\");\n\t\treturn 0;\n}");
		textEditor.setSelection(textEditor.getText().toString().length());
		createDefaultFile(textEditor.getText().toString());

		fileNameText.setText("hello.c");
		filePathText.setText(sdcardPath + "/文本编辑器/hello.c");


	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO: Implement this method
		super.onWindowFocusChanged(hasFocus);

	}


	/* 创建一个默认文件 */
	public void createDefaultFile(String text) {
		if (Environment.getExternalStorageState()
			.equals(Environment.MEDIA_MOUNTED)) {
			sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
			File file = new File(sdcardPath + "/文本编辑器/");
			OutputStream out = null;
			try {
				if (!file.exists()) {
					file.mkdir();
				}
				out = new FileOutputStream(sdcardPath + "/文本编辑器/hello.c");
				out.write(text.getBytes());
			} catch (Exception e) {

			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {}
				}
			}

		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO: Implement this method
		super.onPrepareOptionsMenu(menu);
		menu.clear();
		getMenuInflater().inflate(R.menu.main_menu, menu);
	
		MenuItem item = menu.findItem(R.id.search_view);
		if(isGotoLine==true){
			item.setVisible(true);
			getActionBar().setDisplayShowTitleEnabled(false);
			searchView = (SearchView) item.getActionView();
			searchView.setIconified(false);
			//不显示关闭的图标
			searchView.onActionViewExpanded();
			searchView.setMaxWidth(screenWidth*2/3);
			searchView.setQueryHint("Search…");
			
			searchView.setOnCloseListener(closeLis);
			searchView.setOnQueryTextListener(queryLis);
		}else{
			getActionBar().setDisplayShowTitleEnabled(true);
			item.setVisible(false);
		}
		
		return super.onPrepareOptionsMenu(menu);
	}

	
	private OnQueryTextListener queryLis = new OnQueryTextListener(){

		@Override
		public boolean onQueryTextSubmit(String query) {
			// TODO: Implement this method
			
			return false;
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			// TODO: Implement this method
			
			searchView.clearFocus();
			textEditor.setEnabled(false);
			if(newText.equals("")){
				searchView.onActionViewExpanded();
			}
			if(newText.matches("^\\d+$")){
				textEditor.gotoLine(Integer.parseInt(newText));
			}
			codeEditor.showIME(true);
			return true;
		}
		
	};
	
	
	private OnCloseListener closeLis = new OnCloseListener(){

		@Override
		public boolean onClose() {
			// TODO: Implement this method
			
			if(searchView.getQuery().equals("")||
			searchView.getQuery()==null){
				isGotoLine = false;
			}
			return true;
		}
	};
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO: Implement this method
		switch (item.getItemId()) {
			case android.R.id.home:
				if(isGotoLine==false){
					MainActivity.this.finish();
				}else{
					isGotoLine = false;
					textEditor.setEnabled(true);
				}
				
				break;
			case R.id.new_file:
				createFileDialog();
				break;
			case R.id.open_file:
				startActivityForResult(new Intent(MainActivity.this
				, FileActivity.class),REQUEST_CODE);
				break;
			case R.id.save_file:
				//开启保存文件线程,，传递一个boolean类型的标记用于判断是加载文件还是保存文件
				new FileThread(this,textEditor,filePathText.getText().toString(),false);
				break;
			case R.id.goto_line:
				isGotoLine = true;
				break;
		}
		invalidateOptionsMenu();
		return super.onOptionsItemSelected(item);
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, data);
		switch(resultCode){
			case RESULT_OK:
				if(requestCode==0){
					pathList = data.getStringArrayListExtra("list");
					
					if(pathList.size()>0){
						File f = new File(pathList.get(0));
						if(!f.isDirectory()){
							//开启加载文件线程,，传递一个boolean类型的标记用于判断是加载文件还是保存文件
							new FileThread(this,textEditor,pathList.get(0),true);
							fileNameText.setText(f.getName());
							filePathText.setText(f.getAbsolutePath());
							setFileIcon(f.getName());
						}
						
					}
				}
				break;
			case RESULT_CANCELED:
				break;
		}
	}



	public void createFileDialog() {
		View view = LayoutInflater.from(this).inflate(R.layout.create_file, null);
		final EditText fileEditText = (EditText) view.findViewById(R.id.fileEditText);
		final EditText dirEditText = (EditText) view.findViewById(R.id.dirEditText);
		Dialog dialog = new AlertDialog.Builder(this)
			.setTitle("创建一个新文件")
			.setView(view)
			.setPositiveButton("确定", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO: Implement this method
					checkFileName(dirEditText.getText().toString()
								  , fileEditText.getText().toString());
					dialog.dismiss();
				}

			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO: Implement this method
					dialog.dismiss();
				}
			})
			.create();
		dialog.show();
	}


	public void checkFileName(String dirName, String fileName) {
		if (!(dirName.equals("") || dirName == null)) {
			//文件目录名包含的一些特殊符号替换为空
			StringBuffer buf = new StringBuffer();
			dirName = dirName.replaceAll("\\\\", "").replaceAll("\\*", "")
				.replaceAll("\\\"", "").replaceAll("\\:", "")
				.replaceAll("\\?", "").replaceAll("\\|", "")
				.replaceAll("\\<", "").replaceAll("\\>", "");
			//按"/"进行目录拆分
			String str[] = dirName.split("/");
			for (String s:str) {
				if (!s.equals("") && s != null) {
					buf.append(s).append("/");
				}
			}
			dirName = buf.toString();
		} else {
			dirName = "";
		}

		//文件名判断，创建新的文件
		if (!(fileName.equals("") || fileName == null)) {
			String path = sdcardPath + "/文本编辑器/" + dirName + fileName;
			setFileIcon(fileName);
			textEditor.setText("\t");
			fileNameText.setText(fileName);
			filePathText.setText(path);
			File f = null;

			if (dirName == null || dirName.equals("")) {
				String fpath = sdcardPath + "/文本编辑器/" + fileName;
				f = new File(fpath);
				createNewFile(f, fpath, fileName, false);
			} else {
				String dpath = sdcardPath + "/文本编辑器/" + dirName;
				f = new File(dpath);
				createNewFile(f, dpath, fileName, true);
			}

		}
	}


	public void createNewFile(File f, String path
						   , String fileName, boolean flag) {
		OutputStream out = null;
		if (!f.exists()) {
			try {
				if (flag == true) {
					f.mkdirs();
					out = new FileOutputStream(path + fileName);
				} else {
					f.createNewFile();
					out = new FileOutputStream(path);
				}
				out.write("\t".getBytes());
			} catch (Exception e) {} finally {
				try {
					if (out != null)
						out.close();
				} catch (IOException e) {}
			}
		}
	}




	/* 设置文件类型图标 */
	public void setFileIcon(String fileName) {
		if (fileName.endsWith(".java")) {
			fileTypeImg.setImageResource(R.drawable.file_type_java);
		} else if (fileName.endsWith(".c")) {
			fileTypeImg.setImageResource(R.drawable.file_type_c);
		} else if (fileName.endsWith(".cpp")) {
			fileTypeImg.setImageResource(R.drawable.file_type_cpp);
		} else if (fileName.endsWith(".css")) {
			fileTypeImg.setImageResource(R.drawable.file_type_css);
		} else if (fileName.endsWith(".html")) {
			fileTypeImg.setImageResource(R.drawable.file_type_html);
		} else if (fileName.endsWith(".xml")) {
			fileTypeImg.setImageResource(R.drawable.file_type_xml);
		} else if (fileName.endsWith(".js")) {
			fileTypeImg.setImageResource(R.drawable.file_type_js);
		} else if (fileName.endsWith(".h")) {
			fileTypeImg.setImageResource(R.drawable.file_type_h);
		} else if (fileName.endsWith(".txt")) {
			fileTypeImg.setImageResource(R.drawable.file_type_txt);
		} else {
			fileTypeImg.setImageResource(R.drawable.file_type_unknown);
		}
	}
	
}
