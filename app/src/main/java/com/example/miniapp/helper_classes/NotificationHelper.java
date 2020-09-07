package com.example.miniapp.helper_classes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import com.example.miniapp.R;

public class NotificationHelper extends ContextWrapper {
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 0;

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

    private NotificationCompat.Builder getNotificationBuilder(){
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("You've been notified!")
                .setContentText("This is your notification text")
                .setSmallIcon(R.drawable.ic_alarm_clock);

        return notifyBuilder;
    }

    public void sendNotification() {
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }
}
