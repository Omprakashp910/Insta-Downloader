package com.dizaraa.apps.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

    private static SharedPreferences mPreferences;
    public static final String NEW_PATH = "path";

    private static SharedPreferences getInstance(Context context) {
        if (mPreferences == null) {
            mPreferences = context.getApplicationContext()
                    .getSharedPreferences("all_saver_data", Context.MODE_PRIVATE);
        }
        return mPreferences;
    }

    public static String getPath(Context context) {
        return getInstance(context).getString(NEW_PATH, "");
    }

    public static void setPath(Context context, String path) {
        getInstance(context).edit().putString(NEW_PATH, path).apply();
    }

    public static void clearPrefs(Context context) {
        getInstance(context).edit().clear().apply();
    }


}
