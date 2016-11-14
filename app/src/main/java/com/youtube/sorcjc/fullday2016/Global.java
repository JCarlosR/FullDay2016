package com.youtube.sorcjc.fullday2016;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Global {

    public static void saveInSharedPreferences(Activity activity, String key, String value) {
        SharedPreferences sharedPref = activity.getSharedPreferences("global_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getFromSharedPreferences(Activity activity, String key) {
        SharedPreferences sharedPref = activity.getSharedPreferences("global_preferences", Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

    public static String getFullPathImage(String imageFile) {
        return "http://fulldayunt.com/assets/images/" + imageFile;
    }

}
