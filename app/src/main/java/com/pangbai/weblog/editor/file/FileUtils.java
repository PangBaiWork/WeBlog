package com.pangbai.weblog.editor.file;

import android.content.*;
import android.graphics.*;
import android.os.*;
import java.io.*;
import java.util.*;
import com.select.*;
import com.editor.text.*;


/*
 *异步加载文件
 */
public class FileUtils extends AsyncTask <File,File,String> {
    //定义文件名
    private String fileName;
    //list集合保存Map
    private List<Map<String,Object>> list;  
    //Map集合通过文件名获取文件
    public static Map<String,File> map;             
    private static Context context;

    public FileUtils(Context context,List<Map<String,Object>> list) {
        this.context = context;
        this.map = new HashMap<String,File>();
        this.list = list;
    }


    @Override
    protected void onProgressUpdate(File... values) {               // 更新进度
        Map<String,Object> m = new HashMap<String,Object>();
        //判断是否为目录
        if (values[0].isDirectory()) {
            m.put("imageView", setBitmap(R.drawable.format_folder));
        } else {
            fileName = values[0].getName();
            this.setFileIcon(m, values[0]);
        }
        //设置文件名称
        m.put("textView", values[0].getName());
        //对应的文件名->对应的文件
        map.put(values[0].getName(), values[0]);
        list.add(m);
    }

    /*
     *处理后台任务，用于文件查找
     */
    @Override
    protected String doInBackground(File... params) {

        // 设置一个选项，可以回到上一级目录(不是根目录)
        if (!params[0].getPath().equals(java.io.File.separator)) {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("imageView", setBitmap(R.drawable.format_folder));                        // 定义图片
            //列出上一级目录的文件名称 key->tv
            m.put("textView", "/返回上一级...");
            //对应的文件名->对应的文件
            map.put("/返回上一级...", params[0].getParentFile());
            list.add(m);
        }

        //当前是一个目录
        if (params[0].isDirectory()) {
            //列出全部文件(得到是一个数组)
            File f[] = params[0].listFiles();
            if (f != null) {
                for (int x=0;x < f.length;x++) {
                    //过滤以.开头的文件
                    if (!(f[x].getName().startsWith("."))) {
                        this.publishProgress(f[x]);
                    }
                }
            }
        }

        return "success！";
    }


    //获取图标的位图
    public static Bitmap setBitmap(int resId){
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 1;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId); 
        return bitmap;
    }


    //不同的文件显示不同的图标
    public void setFileIcon(final Map<String,Object> m, final File file) {
        if (fileName.endsWith(".apk")) {
            //设置app图标
            m.put("imageView", setBitmap(R.drawable.app_icon));
            new ThumbnailUtil(context,m,"apk").execute(file.getAbsolutePath());
        } else if (fileName.endsWith(".app")) {
            m.put("imageView", setBitmap(R.drawable.format_app));
        } else if (fileName.endsWith(".chm")) {
            m.put("imageView", setBitmap(R.drawable.format_chm));
        } else if (fileName.endsWith(".ebook")) {
            m.put("imageView", setBitmap(R.drawable.format_ebook));
        } else if (fileName.endsWith(".excel")) {
            m.put("imageView", setBitmap(R.drawable.format_excel));
        } else if (fileName.endsWith(".flash")) {
            m.put("imageView", setBitmap(R.drawable.format_flash));
        } else if (fileName.endsWith(".html") || fileName.endsWith(".jsp")) {
            m.put("imageView", setBitmap(R.drawable.format_html));
        } else if (fileName.endsWith(".mp4") || fileName.endsWith(".avi")
                   || fileName.endsWith(".rmvb") || fileName.endsWith(".wmv")
                   || fileName.endsWith(".mkv")) {

            //获得视频缩略图               
            m.put("imageView", setBitmap(R.drawable.format_media)); 
            new ThumbnailUtil(m,"video").execute(file.getAbsolutePath());

        } else if (fileName.endsWith(".mp3")) {
            m.put("imageView", setBitmap(R.drawable.format_music));
        } else if (fileName.endsWith(".pdf")) {
            m.put("imageView", setBitmap(R.drawable.format_pdf));
        } else if (fileName.endsWith(".jpg") || fileName.endsWith("jpeg")
                   || fileName.endsWith(".png") || fileName.endsWith(".bmp")
                   || fileName.endsWith(".gif")) {
            //获得图片缩略图
            m.put("imageView", setBitmap(R.drawable.format_picture)); 
            new ThumbnailUtil(m,"image").execute(file.getAbsolutePath());

        } else if (fileName.endsWith(".ppt")) {
            m.put("imageView", setBitmap(R.drawable.format_ppt));
        } else if (fileName.endsWith(".zip") || fileName.endsWith(".rar")
                   || fileName.endsWith(".7z") || fileName.endsWith(".tar")
                   || fileName.endsWith(".gz")) {
            m.put("imageView", setBitmap(R.drawable.format_zip));
        } else if (fileName.endsWith(".txt") || fileName.endsWith(".java")
                   || fileName.endsWith(".java") || fileName.endsWith(".xml")
                   || fileName.endsWith(".c") || fileName.endsWith(".properties")
				 ||fileName.endsWith(".cpp")||fileName.endsWith(".h")) {
            m.put("imageView", setBitmap(R.drawable.format_text));
        } else {
            m.put("imageView", setBitmap(R.drawable.format_unknown));
        } 
    }

}


