package com.sanjay.openfire.chat.utilies;

import android.content.Context;
import android.content.SharedPreferences;

import com.sanjay.openfire.R;


/**
 * Created by RAJ ARYAN on 23/07/19.
 */
public class MySharedPref {
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public MySharedPref(Context context) {
        sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_name), Context.MODE_PRIVATE);
    }

    public void writeString(String key, String value) {
        editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String readString(String key, String defaultValue) {
        return sharedPref.getString(key, defaultValue);
    }

    public void writeInt(String key, int value) {
        editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int readInt(String key, int defaultValue) {
        return sharedPref.getInt(key, defaultValue);
    }

    public void writeBoolean(String key, boolean value) {
        editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean readBoolean(String key, boolean defaultValue) {
        return sharedPref.getBoolean(key, defaultValue);
    }


    public void deleteAllData() {
        editor = sharedPref.edit();
        editor.clear().apply();

    }
}
