package com.pangbai.weblog.global;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.view.Window;
import android.view.WindowInsets;

import androidx.core.view.ViewCompat;

import com.google.android.material.internal.EdgeToEdgeUtils;

public class ThemeUtil {
   public static int getTheme(int color, Context context){
       int[] attribute = new int[] { color};
       TypedArray array = context.getTheme().obtainStyledAttributes(attribute);
       int mColor = array.getColor(0, Color.TRANSPARENT);
       array.recycle();
       return mColor;
   }
   @SuppressLint({"RestrictedApi", "WrongConstant"})
   public static void applyEdgeToEdge(Window window){
       if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
           return;
       EdgeToEdgeUtils.applyEdgeToEdge(window, true);
       ViewCompat.setOnApplyWindowInsetsListener(
               window.getDecorView(),   (v, insets) -> {
                   if (v.getResources().getConfiguration().orientation
                           != Configuration.ORIENTATION_LANDSCAPE) {
                       return insets;
                   }
                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                       v.setPadding(
                               insets.getInsets(WindowInsets.Type.systemBars()).left,
                               0,
                               insets.getInsets(WindowInsets.Type.systemBars()).right,
                               insets.getInsets(WindowInsets.Type.systemBars()).bottom);
                   } else {
                       v.setPadding(
                               insets.getStableInsetLeft(),
                               0,
                               insets.getStableInsetRight(),
                               insets.getStableInsetBottom());
                   }
                   return insets;
               });
   }

}
