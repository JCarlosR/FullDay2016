package com.youtube.sorcjc.fullday2016;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Global {

    public static byte[] getDataFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        double height = bitmap.getHeight();
        double width = bitmap.getWidth();
        double divisor = 1;

        while (height/divisor > 1280 || width/divisor > 1280) {
            divisor += 0.5;
        }

        Bitmap thumbnailBitmap = Bitmap.createScaledBitmap(bitmap, (int) (width/divisor), (int) (height/divisor), false);
        thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
        return stream.toByteArray();
    }

    public static byte[] getThumbnailFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        double height = bitmap.getHeight();
        double width = bitmap.getWidth();
        double divisor = 2;

        while (height/divisor >= 400 || width/divisor >= 400) {
            divisor += 0.5;
        }

        Bitmap thumbnailBitmap = Bitmap.createScaledBitmap(bitmap, (int) (width/divisor), (int) (height/divisor), false);
        thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        return stream.toByteArray();
    }

    public static Bitmap decodeFromBase64(String imageBase64) throws IOException {
        System.gc();

        byte[] decodedByteArray = android.util.Base64.decode(imageBase64, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }
    public static Bitmap decodeAndReduceFromBase64(String imageBase64) throws IOException {
        System.gc();

        byte[] decodedByteArray = android.util.Base64.decode(imageBase64, Base64.NO_WRAP);
        Bitmap image = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
        return Bitmap.createScaledBitmap(image, image.getHeight()/3, image.getHeight()/3, false);
    }

    public static void clearSharedPreferences(Activity activity) {
        saveInSharedPreferences(activity, "token", "");
        saveInSharedPreferences(activity, "user_id", 0);
        saveInSharedPreferences(activity, "name", "");
    }

    public static void saveInSharedPreferences(Activity activity, String key, String value) {
        SharedPreferences sharedPref = activity.getSharedPreferences("global_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static void saveInSharedPreferences(Activity activity, String key, long value) {
        SharedPreferences sharedPref = activity.getSharedPreferences("global_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key, value);
        editor.apply();
    }
    public static void saveInSharedPreferences(Activity activity, String key, int value) {
        SharedPreferences sharedPref = activity.getSharedPreferences("global_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static String getFromSharedPreferences(Activity activity, String key) {
        SharedPreferences sharedPref = activity.getSharedPreferences("global_preferences", Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }
    public static int getIntFromSharedPreferences(Activity activity, String key) {
        SharedPreferences sharedPref = activity.getSharedPreferences("global_preferences", Context.MODE_PRIVATE);
        return sharedPref.getInt(key, 0);
    }
    public static long getLongFromSharedPreferences(Activity activity, String key) {
        SharedPreferences sharedPref = activity.getSharedPreferences("global_preferences", Context.MODE_PRIVATE);
        return sharedPref.getLong(key, 0);
    }

    public static String getFullPathImage(String imageFile) {
        return "http://fulldayunt.com/assets/images/" + imageFile;
    }

    public static void showMessageDialog(Context context, String title, String message) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle(title);
        adb.setMessage(message);
        adb.setPositiveButton("Ok", null);
        adb.show();
    }

    public static void hideKeyBoard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
