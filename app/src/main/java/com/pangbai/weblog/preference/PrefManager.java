package com.pangbai.weblog.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefManager {
   static SharedPreferences pref;
   static String first_launch="first_launch";

    public  static void init(Context context){
        pref= context.getSharedPreferences("weblog_preference", Context.MODE_PRIVATE);

    }
    public static boolean isFirstLaunch(){
          if (pref.getBoolean(first_launch,true)) {
              pref.edit().putBoolean(first_launch, false).commit();
              return true;
          }
          return false;
    }


}
