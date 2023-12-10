package com.pangbai.weblog.editor.file;

import android.app.*;
import android.content.*;
import android.os.*;
import android.os.FileUtils;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.io.*;
import java.util.*;
import com.editor.text.*;



public class FileActivity extends Activity {

	private  ListView listView;
	private FileAdapter adapter;
	private TextView file_path;
	private String path;
	public static boolean visiable = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.file_activity);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		listView = (ListView) super.findViewById(R.id.listView);
		file_path = (TextView) super.findViewById(R.id.file_path);
		//判断sdcard是否存在
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			path = Environment.getExternalStorageDirectory().getPath();
		} else {
			path = java.io.File.separator;
		}

		file_path.setText(path);
		adapter = new FileAdapter(this, path);

		listView.setAdapter(adapter);

		listView.setOnItemLongClickListener(itemLongLis);
		listView.setOnItemClickListener(itemClickLis);
    }



	//处理ListView的监听事件
	private OnItemClickListener itemClickLis = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) { 

			String fileName = (String) adapter.list.get(position).get("textView");
			listFile(fileName,position,view);
		}
	};

	
	//列出所有文件
	public void listFile(String fileName, int position,View convertView) {
		File file = FileUtils.map.get(fileName);
		file_path.setText(file.getAbsolutePath());
		path = file.getAbsolutePath();

		if (file.isDirectory()) {
			FileActivity.this.adapter = null;
			adapter = new FileAdapter(FileActivity.this, file.getPath());
			//同步主线程,listView更新后主线程收不到通信，加个线程延迟ui更新
			new Handler().postDelayed(new Runnable(){

					@Override
					public void run() {
						// TODO: Implement this method
						listView.setAdapter(adapter);
						adapter.notifyDataSetChanged();
					}

				}, 200);
		} else {
			//设置选择的item的背景色
			adapter.setBackground(position);
			if(visiable==true){
				//如果CheckBox可见，按下文件时，同时选中CheckBox
				FileAdapter.ViewHolder holder = (FileAdapter.ViewHolder) convertView.getTag();
				holder.mCheckBox.toggle();
				adapter.notifyDataSetChanged();
			}
		}
	}


	//Item长按监听处理
	private OnItemLongClickListener itemLongLis = new OnItemLongClickListener(){

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View convertView, int position, long id) {
			// TODO: Implement this method
			visiable = true;
			FileAdapter.ViewHolder holder = (FileAdapter.ViewHolder) convertView.getTag();
			holder.mCheckBox.toggle();
			adapter.notifyDataSetChanged();
			return true;
		}

	};


	//创建ActionBar菜单 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO: Implement this method
		super.getMenuInflater().inflate(R.menu.file_menu, menu); 
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO: Implement this method
		switch (item.getItemId()) {
			case android.R.id.home:
				FileActivity.this.finish();
				break;
			case R.id.select_all:
				visiable = true;
				for (int i=0;i < adapter.getCount();i++) {
					adapter.getSelectItem().put(i, true);
				}
				adapter.notifyDataSetChanged();
				break;
			case R.id.uselect_all:
				visiable = true;
				for (int i=0;i < adapter.getCount();i++) {
					adapter.getSelectItem().put(i, false);
				}
				adapter.notifyDataSetChanged();
				break;
			case R.id.center:
				Intent intent = getIntent();
				List<String> list = new ArrayList<String>();
				Iterator<String> iter = adapter.getSavePath().iterator();
				while(iter.hasNext()){
					list.add(iter.next());
				}
				intent.putStringArrayListExtra("list", (ArrayList<String>)list);
				this.setResult(RESULT_OK, intent);
				FileActivity.this.finish();
				visiable = false;
				break;
			case R.id.cancel:
				visiable = false;
				if(!adapter.getSavePath().isEmpty()){
					adapter.getSavePath().clear();
				}
				for (int i=0;i < adapter.getCount();i++) {
					adapter.getSelectItem().put(i, false);
				}
				adapter.notifyDataSetChanged();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO: Implement this method
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			visiable = false;
			//按下返回键，则返回上一级目录
			if (path.equals(java.io.File.separator)) {
				FileActivity.this.finish();
			} else {
				listFile("/返回上一级...",0,null);
			}
			adapter.notifyDataSetChanged();
		}
		return false;
	} 

}
