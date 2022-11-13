package com.rigadev.nutricapps.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.rigadev.nutricapps.BuildConfig;


public class SessionManager {
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }


    public void login(String idUser, String username, String email) {
        editor.putString("idUser", idUser);
        editor.putString("username", username);
        editor.putString("email", email);
        editor.putBoolean("logged", true);
        editor.commit();
    }

    public void logOut() {
        editor.putString("idUser", null);
        editor.putString("username", null);
        editor.putString("email", null);
        editor.putBoolean("logged", false);
        editor.commit();
    }

    public boolean isLoggedin() {
        return preferences.getBoolean("logged", false);
    }

    public String getIdUser() {
        return preferences.getString("idUser", "");
    }


    /*


    public void logOut() {
        editor.putString("idUser", null);
        editor.putString("username", null);
        editor.putString("fullname", null);
        editor.putString("position", null);
        editor.putString("role", null);
        editor.putBoolean("logged", false);
        editor.commit();
    }

    public void setIdLink(String idLink) {
        editor.putString("idLink", idLink);
        editor.commit();
    }

    public boolean isLoggedin() {
        return preferences.getBoolean("logged", false);
    }
    public String getUsername() {
        return preferences.getString("username", "");
    }
    public String getFullname() {
        return preferences.getString("fullname", "");
    }


    public String getidLink() {
        return preferences.getString("idLink", "");
    }*/

}
