package com.test.missedcall;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
//import android.support.annotation.Nullable;
//import android.support.v4.app.NotificationCompat;

public class ForegroundService extends Service {
    private static String input = "";
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    @Override
    public void onCreate() {
        super.onCreate();
    }
    private static Notification generateNotification(String text, String title){
        Context context = MainActivity.appContext;
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, notificationIntent, 0);
        int icon = R.drawable.ic_baseline_agriculture_24;
        int color = Color.rgb(255, 255, 31);
        if (title != null) {
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(icon)
                    .setColor(color)
                    .setContentIntent(pendingIntent)
                    .setNotificationSilent()
                    .build();
            return notification;
        } else {
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentText(text)
                    .setSmallIcon(icon)
                    .setColor(color)
                    .setContentIntent(pendingIntent)
                    .setNotificationSilent()
                    .build();
            return notification;
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Notification notification = generateNotification(input, null);
        startForeground(1, notification);
        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    //@Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Missed call monitor",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    public static void updateNotification(String updatedContent){
        Notification notification = generateNotification(input, updatedContent);
        NotificationManager manager = MainActivity.appContext.getSystemService(NotificationManager.class);
        manager.notify(1, notification);
    }
}