package com.example.miniapp.helper_classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CustomBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.sendNotification(intent.getIntExtra("notificationID", 0), intent.getStringExtra("task"));
    }
}
