package com.pangbai.weblog.global;

import android.app.Application;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.utilities.DynamicColor;

public class application extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
        
    }
}
    
