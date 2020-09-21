package com.example.miniapp.helper_classes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

public class TestService extends Service {
    ArrayList<Pair<Integer, String>> activeTasks;
    public TestService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.v("MY TAG", "SERVICE STOPPED");
        cancelAll();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String task = intent.getStringExtra("task");
        long unixTimestamp = intent.getLongExtra("unixTimestamp", 0);
        int notificationID = intent.getIntExtra("notificationID", 0);

        if(unixTimestamp == 0 || notificationID == 0){
            Log.v("MY TAG", "Warning: default values on service params");
        }

        // record active so they can be cancelled later should user log out
        recordActiveTask(task, notificationID);

        Log.v("MY TAG", "SERVICE STARTED");
        setAlarm(task, unixTimestamp, notificationID);

        return START_STICKY;
    }

    @Override
    public boolean stopService(Intent name) {
        Log.v("MY TAG", "user logged out. stopping alarm service");
        return super.stopService(name);
    }

    private void recordActiveTask(String task, int notificationID) {
        if (activeTasks == null) {
            activeTasks = new ArrayList<>();
        }
        activeTasks.add(new Pair<>(notificationID, task));
    }

    private void setAlarm(String task, long unixTimestamp, int notificationID){
        Intent intent = new Intent(getApplicationContext(), CustomBroadcastReceiver.class);
        intent.putExtra("task", task);
        intent.putExtra("notificationID", notificationID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notificationID, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, unixTimestamp, pendingIntent);
        Log.v("MY TAG", "alarm set for " + task + " at " + unixTimestamp);
    }

    private void cancelAlarm(int notificationID, String task){
        Intent intent = new Intent(getApplicationContext(), CustomBroadcastReceiver.class);
        intent.putExtra("task", task);
        intent.putExtra("notificationID", notificationID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notificationID, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            Log.v("MY TAG", "cancelled alarm for " + task + " with ID " + notificationID);
        }
    }

    private void cancelAll(){
        for(Pair<Integer, String> pair : activeTasks){
            cancelAlarm(pair.first, pair.second);
        }
    }
}
