package com.rigadev.nutricapps.util;

import android.content.Context;
import android.content.SharedPreferences;




public class SessionManager {
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    /*public SessionManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void login(String userID, String username, String fullname, String position ,String role) {
        editor.putString("idUser", userID);
        editor.putString("username", username);
        editor.putString("fullname", fullname);
        editor.putString("position", position);
        editor.putString("role", role);
        editor.putBoolean("logged", true);
        editor.commit();
    }

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
    public String getIdUser() {
        return preferences.getString("idUser", "");
    }

    public String getidLink() {
        return preferences.getString("idLink", "");
    }*/

}
