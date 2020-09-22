package com.example.miniapp.helper_classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.couchbase.lite.DatabaseConfiguration;
import com.example.miniapp.models.IUserDBManager;
import com.example.miniapp.models.Task;
import com.example.miniapp.models.UserDBManager;

import java.util.Calendar;
import java.util.Date;

public class OnAppKilledReceiver extends BroadcastReceiver implements ISubscriber<Task> {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("MY TAG", "app killed broadcast received");

        this.context = context;
        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(context);

        Log.v("MY TAG", "restart service for user " + sharedPrefUtils.getEmailFromSP());
        String email = sharedPrefUtils.getEmailFromSP();
        IUserDBManager dbManager = new UserDBManager(email, new DatabaseConfiguration(context));
        dbManager.addSub(this);
        dbManager.openDB();
        dbManager.listenForChanges();
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

            Intent alarmServiceIntent = new Intent(context.getApplicationContext(), TestService.class);
            alarmServiceIntent.putExtra("task", task);
            alarmServiceIntent.putExtra("unixTimestamp", unixTimestamp);
            alarmServiceIntent.putExtra("notificationID", notificationID);
            context.startService(alarmServiceIntent);
        }
    }

}
