package com.example.collabuy;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SessionManager {

    public static String NO_SESSION = null;

    private static SessionManager instance = null;
    private SharedPreferences prefs;

    private SessionManager(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SessionManager getInstance(Context context){
        if (instance == null)
            instance = new SessionManager(context);
        return instance;
    }

    public void setSession(String user, String pass){
        prefs.edit()
                .putString("username", user)
                .putString("password", pass)
                .commit();
    }

    public String getUsername(){
        return prefs.getString("username", NO_SESSION);
    }

    public String getPassword(){
        return prefs.getString("passsword", NO_SESSION);
    }

    public void clearSession(){
        prefs.edit()
                .remove("username")
                .remove("password")
                .commit();
    }
}
