package com.example.miniapp.helper_classes

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class TestWifiService : Service() {
    private var wifiOnReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
            if(wifiState == WifiManager.WIFI_STATE_ENABLED){
                CoroutineScope(IO).launch {
                    val t1 = Date().time
                    pingGoogle()
                    val t2 = Date().time
                    log("time spent: ${t2 - t1} ms")
                }
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        log("NET TEST SERVICE STARTED")
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        registerReceiver(wifiOnReceiver, intentFilter)
        return START_STICKY
    }

    override fun stopService(name: Intent): Boolean {
        unregisterReceiver(wifiOnReceiver)
        return super.stopService(name)
    }

     fun pingGoogle(){
         try {
             log("pinging google...")
             val url = URL("https://www.google.com/")
             val connection = url.openConnection() as HttpURLConnection
             connection.connectTimeout = 2000
             connection.connect()
             if (connection.responseCode == 200) {
                 log("Connection OK")
                 // TODO: Connection OK, do the things (replicator service)
             }
         } catch (e: IOException) {
             e.printStackTrace()
         }
    }
}