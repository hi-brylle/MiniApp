package com.example.miniapp.helper_classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent =  new Intent(context, TestService.class);
        context.startService(serviceIntent);
        Log.v("MY TAG", "Boot broadcast received");
    }
}
