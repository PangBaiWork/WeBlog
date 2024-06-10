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
        interface_font,
        first_launch_mark,
        bl_interface_undo_button_display,
        bl_artical_title_to_md5,
        bl_editor_autosave,
        bl_script_init

    }

    static SharedPreferences pref;
    //1 for first installation , 2 for reinstalltation
    public   static int isFirstOrReinstall_flag=0;




    public static void init(Context context) {
        if (pref==null) pref =PreferenceManager.getDefaultSharedPreferences(context);
        Config.update();

    }

    public static boolean isFirstOrReinstall(Context context) {
        String mark= context.getApplicationInfo().nativeLibraryDir;
        String defaults="defaults";
        String marked=getString(Keys.first_launch_mark,defaults);
        if (!mark.equals(marked)) {
            putString(Keys.first_launch_mark,mark);
            isFirstOrReinstall_flag=marked.equals(defaults)?1:2;
            // 1 for first install,2 for reinstall
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
