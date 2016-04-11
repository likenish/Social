package com.example.nishtrack.social;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


public class StaticObject {

    public static void createSession(boolean isGoogle,boolean isFacebook,String photoUrl,String name, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isGoogleLogin", isGoogle);
        editor.putBoolean("isFacebookLogin",isFacebook );
        editor.putString("photoUrl",photoUrl);
        editor.putString("name", name);
        editor.commit();
        Log.d("session", "saved");
    }

    public static boolean getboolean(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, false);
    }

    public static String getString(String key,Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public static void update(boolean isGoogle,boolean isFacebook,String photoUrl,String name,Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isFacebookLogin",isFacebook);
        editor.putBoolean("isGoogleLogin", isGoogle);
        editor.putString("photoUrl",photoUrl);
        editor.putString("name", name);
        editor.commit();
    }

}
