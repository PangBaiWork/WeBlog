package com.pangbai.weblog.editor.file;


/*
 *获取 图片 视频 apk缩略图
 */
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.os.*;
import android.os.FileUtils;
import android.provider.*;
import java.util.*;
import com.editor.text.*;

public class ThumbnailUtil extends AsyncTask<String,Void,Object> {

    private static Context context;
    private String flagType;
    private Map<String,Object> m; 

    public ThumbnailUtil(Map<String,Object> m ,String flagType){
        this.m = m;
        this.flagType = flagType;
    }


    public ThumbnailUtil(Context context,Map<String,Object> m ,String flagType ){
        this(m,flagType);
        this.context = context;
    }



    @Override
    protected void onProgressUpdate(Void...values) {
        // TODO: Implement this method
        super.onProgressUpdate(values);
    } 


    @Override
    protected Object doInBackground(String... params) {
        // TODO: Implement this method
        Object obj = null;
        if(flagType.equals("apk")){
            obj = getUninstallAPKInfo(params[0]);
        }else if(flagType.equals("image")){
            obj = getImageThumbnail(params[0], 75, 75);

        }else if(flagType.equals("video")){
            getVideoThumbnail(params[0], 75, 75,
                              MediaStore.Video.Thumbnails.MICRO_KIND);
        } 
        return obj;
    }

    @Override
    protected void onPostExecute(Object result) {
        // TODO: Implement this method
        super.onPostExecute(result);
        if(flagType.equals("apk")){
            if (result != null) {
                m.put("imageView", (Bitmap)result);
            } else {
                m.put("imageView", android.os.FileUtils.setBitmap(R.drawable.format_apk));
            } 
        }else if(flagType.equals("image")){
            if (result != null) {
                m.put("imageView", (Bitmap)result);
            } else {
                m.put("imageView", android.os.FileUtils.setBitmap(R.drawable.format_picture));
            } 
        }else if(flagType.equals("video")){
            if(result!=null){
                m.put("imageView", (Bitmap)result);
            } else{
                m.put("imageView", FileUtils.setBitmap(R.drawable.format_media));
            } 
        }
    } 


    //获取未安装apk包的信息
    public static Bitmap getUninstallAPKInfo(String appPath) {
        //获取版本，程序名称，包名
        Bitmap bitmap = null;
        PackageManager manager = context.getPackageManager();
        PackageInfo pakinfo = manager.getPackageArchiveInfo(appPath, PackageManager.GET_ACTIVITIES);
        if (pakinfo != null) {
            ApplicationInfo appinfo=pakinfo.applicationInfo;
            //String versionName = pakinfo.versionName;
            //获得icon图标
            Drawable icon = manager.getApplicationIcon(appinfo);
            BitmapDrawable draw = (BitmapDrawable) icon;
            //获得bitmap对象
            bitmap = draw.getBitmap();
            //String appName = (String) manager.getApplicationLabel(appinfo);
            //String pakName = appinfo.packageName;

        }
        return bitmap;
    } 


    /**
     * 根据指定的图像路径和大小来获取缩略图
     * 此方法有两点好处：
     * 1.使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
     * 2.缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
     * 用这个工具生成的图像不会被拉伸。
     * @param imagePath 图像的路径
     * @param width 指定输出图像的宽度
     * @param height 指定输出图像的高度
     * @return 生成的缩略图
     */
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        int scaleX = imageWidth / width;
        int scaleY = imageHeight / height;
        int scale = 1;
        if (scaleX < scaleY) {
            scale = scaleX;
        } else {
            scale = scaleY;
        }
        if (scale <= 0) {
            scale = 1;
        }
        options.inSampleSize = scale;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    } 


    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param videoPath 视频的路径
     * @param width 指定输出视频缩略图的宽度
     * @param height 指定输出视频缩略图的高度度
     * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     * 其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                           int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                                                 ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    } 


}

