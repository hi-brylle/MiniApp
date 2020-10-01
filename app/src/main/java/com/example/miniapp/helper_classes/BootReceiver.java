package com.example.miniapp.helper_classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Objects;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")){
            Log.v("MY TAG", "Boot broadcast received");
            Intent alarmServiceIntent = new Intent(context, AlarmService.class);
            context.startService(alarmServiceIntent);
        }
    }
}
