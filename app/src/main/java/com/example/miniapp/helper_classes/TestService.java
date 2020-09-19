package com.example.miniapp.helper_classes;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class TestService extends Service {
    public TestService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "SERVICE STOPPED", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "SERVICE STARTED", Toast.LENGTH_SHORT).show();
        Log.v("MY TAG", "SERVICE STARTED");
        return START_STICKY;
    }
}
