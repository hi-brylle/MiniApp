package com.example.miniapp.helper_classes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            log("Boot broadcast received")

            val secureSharedPref = SecureSharedPref(context)

            if(secureSharedPref.isUserLoggedOut()){
                log("previous user logged out.")
            } else {
                secureSharedPref.getLoggedEmail()?.let { userEmail ->
                    val dbListenerServiceIntent = Intent(context, UserDBListenerService::class.java)
                    dbListenerServiceIntent.putExtra("email", userEmail)
                    dbListenerServiceIntent.action = "INTENT_ACTION_DB_CHANGED"
                    context.startService(dbListenerServiceIntent)

                    val alarmServiceIntent = Intent(context, AlarmService::class.java)
                    alarmServiceIntent.putExtra("email", userEmail)
                    context.startService(alarmServiceIntent)
                }
            }


        }
    }
}