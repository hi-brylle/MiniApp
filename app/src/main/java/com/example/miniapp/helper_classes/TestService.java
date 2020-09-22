package com.example.miniapp.helper_classes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import com.couchbase.lite.DatabaseConfiguration;
import com.example.miniapp.models.IUserDBManager;
import com.example.miniapp.models.Task;
import com.example.miniapp.models.UserDBManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TestService extends Service implements ISubscriber<Task> {
    ArrayList<Pair<Integer, String>> activeTasks;
    public TestService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        cancelAll();
        Log.v("MY TAG", "SERVICE STOPPED");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("MY TAG", "SERVICE STARTED");
        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(this);

        if (sharedPrefUtils.isUserLoggedOut()){
            Log.v("MY TAG", "previous user logged out.");
        } else {
            Log.v("MY TAG", "start service for user " + sharedPrefUtils.getEmailFromSP());
            String email = sharedPrefUtils.getEmailFromSP();
            IUserDBManager dbManager = new UserDBManager(email, new DatabaseConfiguration(this));
            dbManager.addSub(this);
            dbManager.openDB();
            dbManager.listenForChanges();
        }

        return START_STICKY;
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.v("MY TAG", "APP KILLED FROM RECENT");
        Intent restartIntent = new Intent(getApplicationContext(), OnAppKilledReceiver.class);
        sendBroadcast(restartIntent);
    }

    @Override
    public void update(Task t) {
        Date now = Calendar.getInstance().getTime();
        if (t.getDateStart().after(now)){
            String task = t.getTask();
            Date dateStart = t.getDateStart();

            assert dateStart != null;
            long unixTimestamp = dateStart.getTime();
            // notification ID identifies the pending intent
            int notificationID = (int) (unixTimestamp / 1000);

            if(unixTimestamp == 0 || notificationID == 0){
                Log.v("MY TAG", "Warning: default values on service params");
            }

            recordActiveTask(task, notificationID);

            setAlarm(task, unixTimestamp, notificationID);
        }
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
        Log.v("MY TAG", "cancelled all alarms for user");
    }

}
