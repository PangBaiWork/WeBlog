package com.pangbai.weblog.view;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

public class DrawerAnim implements DrawerLayout.DrawerListener {
    View drawer;
    View child;
    int width=0;
    public DrawerAnim(View view){
        drawer=view;
       child= ( (DrawerLayout)drawer.getParent()).getChildAt(0);

    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        if (drawerView==drawer) {
            if (width==0) width=drawer.getMeasuredWidth();

            // 侧边栏
            // slideOffset 值默认是0~1
            child.setTranslationX(width * slideOffset);
        }
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
