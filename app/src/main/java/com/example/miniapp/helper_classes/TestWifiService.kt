package com.example.miniapp.helper_classes;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestWifiService extends Service {
    BroadcastReceiver wifiOnReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

            switch (wifiState) {
                case WifiManager.WIFI_STATE_ENABLED:
                    Logger.log("pinging google...");
                    new Thread(new PingURL()).start();
                    break;

                case WifiManager.WIFI_STATE_DISABLED:
                    // nothing
                    break;
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.log("NET TEST SERVICE STARTED");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiOnReceiver, intentFilter);

        return START_STICKY;
    }

    @Override
    public boolean stopService(Intent name) {
        unregisterReceiver(wifiOnReceiver);
        return super.stopService(name);
    }

    static class PingURL implements Runnable {
        @Override
        public void run() {
            try {
                URL url = new URL("https://www.google.com/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(2000);
                connection.connect();

                if(connection.getResponseCode() == 200){
                    Logger.log("Connection OK");
                    // TODO: Connection OK, do the things (replicator service)
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
