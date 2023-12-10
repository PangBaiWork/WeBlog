package com.pangbai.weblog.editor.text;
import android.content.*;
import android.os.*;
import android.webkit.*;
import java.io.*;
import android.widget.*;

public class FileThread  {

	private TextEditorView textEditor;
	private Context context;

	public FileThread(Context context,TextEditorView textEditor
					  , String path, boolean flag) {
						  
		this.context = context;
		this.textEditor = textEditor;
		if (flag == true) {
			new FileLoadThread().execute(path);
		} else {
			new FileSaveThread().execute(path);
		}
	}


	/* 读取文件线程 */
	class FileLoadThread extends AsyncTask<String,Integer,String> {

		@Override
		protected void onProgressUpdate(Integer[] values) {
			// TODO: Implement this method
			super.onProgressUpdate(values);
		}


		@Override
		protected String doInBackground(String...params) {
			// TODO: Implement this method
			//根据params[1]的值，判断是加载文件还是保存文件
			if (checkFile(new File(params[0])) == true) {
				return readFile(params[0]).toString();
			} else {
				return "";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO: Implement this method
			super.onPostExecute(result);
			textEditor.setText(result);

		}

		/* 读取文件 */
		public StringBuffer readFile(String path) {
			BufferedReader br = null;
			StringBuffer buf = new StringBuffer();
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
				char[] ch = new char[1024];
				int len = 0;
				while ((len = br.read(ch)) != -1) {
					buf.append(ch, 0, len);
				}
			} catch (Exception e) {

			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {}
				}
			}
			return buf;
		}


		/* 根据MIMEType判断文件类型 */
		public boolean checkFile(File f) {
			boolean flag = false;
			if (f.isDirectory()) {
				flag = false;
			} else {
				MimeTypeMap mime = MimeTypeMap.getSingleton();
				String suffix = f.getName();

				if (suffix.equals("") || suffix == null || suffix.indexOf(".") < 0) {
					flag = false;
				} else {
					int index = suffix.indexOf(".");
					String expand = suffix.substring(index + 1, suffix.length());

					if (!expand.equals("")) {
						if (mime.getMimeTypeFromExtension(expand) == null
							|| mime.getMimeTypeFromExtension(expand).matches("text/[\\s|\\S]*")) {
							flag = true;
						}
					}
				}
			}
			return flag;
		}
	}



	/* 写文件线程 */
	class FileSaveThread extends AsyncTask<String,Integer,String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO: Implement this method
			writeFile(params[0]);
			return params[0];
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO: Implement this method
			super.onPostExecute(result);
			Toast.makeText(context,"文件已保存："+result,Toast.LENGTH_LONG).show();
		}
		


		/* 写文件 */
		public void writeFile(String path) {
			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
				bw.write(textEditor.getText().toString());
			} catch (Exception e) {} finally {
				if (bw != null) {
					try {
						bw.close();
					} catch (IOException e) {}
				}
			}
		}
	}

}
