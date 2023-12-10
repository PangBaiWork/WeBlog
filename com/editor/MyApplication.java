package com.editor;

import android.app.*;
import android.view.*;
import java.lang.reflect.*;


/*
 *ActionBar溢出菜单在任意手机上显示
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        // TODO: Implement this method
        super.onCreate();
        menuKey();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        
    }

    public void menuKey() {
        try {
            ViewConfiguration mconfig = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(mconfig, false);
            }
        } catch (Exception e) {
        }
    }
    
}
