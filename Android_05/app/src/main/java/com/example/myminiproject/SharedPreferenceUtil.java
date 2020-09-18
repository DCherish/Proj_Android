package com.example.myminiproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceUtil
{
    public static void putSharedPreference(Context context, String key, int value)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(key, value);

        editor.commit();
    }

    // SharedPreference에 값을 저장

    public static int getSharedPreference(Context context, String key)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getInt(key, 0);
    }

    // SharedPreference에서 값을 불러옴
}