package com.pangbai.weblog.global;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;

public class themeUtil {
   public static int getTheme(int color, Context context){
       int[] attribute = new int[] { color};
       TypedArray array = context.getTheme().obtainStyledAttributes(attribute);
       int mColor = array.getColor(0, Color.TRANSPARENT);
       array.recycle();
       return mColor;
   }
}
