package com.example.miniapp.helper_classes;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtils {
    private SharedPreferences userLoginTrackerSharedPref;
    private String emailFromSP;

    public SharedPrefUtils(Context context){
        String SHARED_PREF_KEY = "loggedInUser";
        userLoginTrackerSharedPref = context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
    }

    public boolean isUserLoggedOut(){
        emailFromSP = userLoginTrackerSharedPref.getString("email", "");
        String password = userLoginTrackerSharedPref.getString("password", "");

        assert emailFromSP != null;
        assert password != null;
        return emailFromSP.equals("") || password.equals("");
    }

    public String getEmailFromSP() {
        return emailFromSP;
    }

    public void recordUserLogin(String email, String password){
        SharedPreferences.Editor editor = userLoginTrackerSharedPref.edit();
        editor.putString("email", email);
        editor.putString("password", password); // IS THIS SECURE?? NO, IT'S NOT
        editor.apply();
    }

    public void clearLogin() {
        SharedPreferences.Editor editor = userLoginTrackerSharedPref.edit();
        editor.putString("email", "");
        editor.putString("password", "");
        editor.apply();
    }
}
