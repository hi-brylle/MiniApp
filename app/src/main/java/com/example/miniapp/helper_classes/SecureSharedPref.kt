package com.example.miniapp.helper_classes

import android.content.Context
import android.content.SharedPreferences

class SecureSharedPref(val context: Context){
    // store shared preferences as lambda lmao
    private val sharedPref = {context.getSharedPreferences("loggedInUser", Context.MODE_PRIVATE)}

    fun recordLogin(email: String, hash: String){
        val editor: SharedPreferences.Editor = sharedPref().edit()
        editor.putString("email", email)
        editor.putString("hash", hash)
        editor.apply()
    }

    fun clearLogin(){
        // empty the shared pref
        recordLogin("","")
    }

    fun isUserLoggedOut(): Boolean {
        val storedEmail = sharedPref().getString("email", "")
        val storedPassword = sharedPref().getString("hash", "")

        return if (storedEmail == null || storedPassword == null) true else (storedEmail == "" || storedPassword == "")
    }

    fun getLoggedEmail(): String? {
        return sharedPref().getString("email", "")
    }
}


