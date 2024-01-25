package com.pangbai.weblog.tool;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

public class util {
    public static Handler handler = new Handler(Looper.getMainLooper());


    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *

     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static float dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dipValue * scale + 0.5f;
    }

    public static void startActivity(Context ct, Class activity, boolean anim) {
        Intent it = new Intent(ct.getApplicationContext(), activity);
        if (anim) {
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        if (ct instanceof Activity)
            ct.startActivity(it, ActivityOptions.makeSceneTransitionAnimation((Activity) ct).toBundle());
        else
            ct.startActivity(it);
    }

    public static void runOnUiThread(Runnable run){
            handler.post(run);
    }
    public static String[] getByEnv(String env){

        return env.split("=",2);
    }


}
