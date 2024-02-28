package com.pangbai.weblog.preference;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Set;

public class PrefManager {
    public enum Keys {
        tags,
        category,
        current_project,
        current_file,
        first_launch,
        bl_interface_undo_button_display,
        bl_artical_title_to_md5
    }

    static SharedPreferences pref;




    public static void init(Context context) {
        pref =PreferenceManager.getDefaultSharedPreferences(context);
        Config.update();

    }

    public static boolean isFirstLaunch() {
        if (getBoolen(Keys.first_launch,true)) {
            putBoolen(Keys.first_launch,false);
            return true;
        }
        return false;
    }

    public static void putBoolen(Keys key, boolean value) {
        pref.edit().putBoolean(key.name(), value).apply();

    }
    public static boolean getBoolen(Keys key, boolean defaultValue) {
      return   pref.getBoolean(key.name(),defaultValue);

    }

    public static void putString(Keys key, String value) {
        pref.edit().putString(key.name(), value).apply();

    }

    public static String getString(Keys key, String defaultValue) {
        return pref.getString(key.name(), defaultValue);
    }

    /**
     * String[] to Arraylist  is a process of replication too
     * and may slow down program
     *
     * @param key
     * @param value
     */
    public static void putStringArray(Keys key, String[] value) {
        String tmp = "";
        for (String s : value) {
            tmp += s;
            tmp += "%%";
        }
        pref.edit().putString(key.name(), tmp).commit();
    }

    public static void putStringArray(Keys key, ArrayList<String> value) {
        String tmp = "";
        for (String s : value) {
            tmp += s;
            tmp += "%%";
        }
        pref.edit().putString(key.name(), tmp).apply();
    }

    public static void putStringArray(Keys key, Set<String> value) {
        pref.edit().putStringSet(key.name(), value).apply();
    }

    public static Set<String> getStringArray(Keys key, Set<String> value) {
        return pref.getStringSet(key.name(), value);
    }


    public static String[] getStringArray(Keys key, String[] defaultValue) {
        String tmp = "";
        for (String s : defaultValue) {
            tmp += s;
            tmp += "%%";
        }
        tmp = pref.getString(key.name(), tmp);
        if (tmp.isBlank())
            return null;
        String[] array = tmp.split("%%");
        return array;
    }


}
