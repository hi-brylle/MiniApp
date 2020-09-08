package com.example.miniapp.helper_classes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.example.miniapp.views.NewTask;

import java.util.Date;

public class AlarmService extends Service {
    AlarmManager alarmManager;

    @Override
    public void onCreate() {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, BroadcastHelper.class);
        notificationIntent.putExtra("task", intent.getStringExtra("task"));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, notificationIntent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, broadcastIntent);
        } else{
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, broadcastIntent);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
