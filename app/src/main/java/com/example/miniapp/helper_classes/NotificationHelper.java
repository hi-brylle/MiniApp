package com.example.miniapp.helper_classes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.miniapp.R;
import com.example.miniapp.views.MainActivity;

public class NotificationHelper extends ContextWrapper {
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager notificationManager;

    public NotificationHelper(Context base) {
        super(base);
        createNotificationChannel();
    }

    private void createNotificationChannel(){
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "Mascot Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.YELLOW);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder(String task){
        Intent openAppIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, openAppIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("To-do:")
                .setContentText(task)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_alarm_clock);

        return notifyBuilder;
    }


    // TODO: add pending intent to do something when notification is clicked
    public void sendNotification(int notificationID, String task) {
        if (notificationID == 0){
            Log.v("MY TAG", "Error in sending notification");
        }
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder(task);
        notificationManager.notify(notificationID, notifyBuilder.build());
    }
}
