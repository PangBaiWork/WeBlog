package com.pangbai.weblog.editor.file;

import android.content.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.editor.text.*;

public class FileAdapter extends BaseAdapter {

	private int mPosition = -1;		 //记录item按下的位置
	private LayoutInflater inflater ;//加载布局文件

	private Map<Integer,Boolean> selectItem;//保存选择的Item
	private Set<String> savePath;//保存选择的所有路径

	//定义List集合
	public static List<Map<String,Object>> list;

	public FileAdapter(Context context, String filePath) {
		list = new ArrayList<Map<String,Object>>();
		//改变list中的内容
		new FileUtils(context, list).execute(new File(filePath));
		this.inflater = LayoutInflater.from(context);
		selectItem = new HashMap<Integer,Boolean>();
		savePath = new HashSet<String>();
		//初始化时设置所有的Item都未选中
		for (int i=0;i < list.size();i++) {
			selectItem.put(i, false);
		}
	}


	@Override
	public int getCount() {
		// TODO: Implement this method
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO: Implement this method
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO: Implement this method
		return position;
	}

	//获得ListView，每一行item的View视图
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();

			//加载listview布局文件
			convertView = inflater.inflate(R.layout.file_item, null);

			//组件初始化
			holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
			holder.textView = (TextView) convertView.findViewById(R.id.textView);
			holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.mCheckBox);

			convertView.setTag(holder);
		} else {
			holder = (FileAdapter.ViewHolder) convertView.getTag();
		}
		//设置组件显示的内容

		holder.imageView.setImageBitmap((Bitmap)list.get(position).get("imageView"));
		holder.textView.setText(list.get(position).get("textView").toString());
		//设置checkBox是否显示
		if (FileActivity.visiable == true) {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        } else {
            holder.mCheckBox.setVisibility(View.GONE);
        }
		//设置选中的item的背景色
		if (position == mPosition) {
			convertView.setBackgroundColor(0x88ff8000);
			File file = FileUtils.map.get(list.get(position).get("textView").toString());
			savePath.add(file.getAbsolutePath());

		} else {
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}

		holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
					// TODO: Implement this method
					if (holder.mCheckBox.isChecked()) {
						File file = FileUtils.map.get(list.get(position).get("textView").toString());
						selectItem.put(position, true);
						savePath.add(file.getAbsolutePath());

					} else {
						selectItem.put(position, false);
						File file = FileUtils.map.get(list.get(position).get("textView").toString());
						savePath.add(file.getAbsolutePath());
					}
				}
			});

		//这里是关键判断是否为空
        if (selectItem.get(position) != null) {
            holder.mCheckBox.setChecked(selectItem.get(position));
        } else {
            holder.mCheckBox.setChecked(false);
        }


		return convertView;
	}


	public void setBackground(int position) {
		if (position != mPosition) {
			mPosition = position;
		} else {
			mPosition = -1;
		}
		notifyDataSetChanged();
	}

	public void setSavePath(Set<String> savePath) {
		this.savePath = savePath;
	}

	public Set<String> getSavePath() {
		return savePath;
	}


	public void setSelectItem(Map<Integer, Boolean> selectItem) {
		this.selectItem = selectItem;
	}

	public Map<Integer, Boolean> getSelectItem() {
		return this.selectItem;
	}


	public final class ViewHolder {
		public ImageView imageView;
		public TextView textView;
		public CheckBox mCheckBox;
	}

}
