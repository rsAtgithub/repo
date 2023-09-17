package com.example.phonelocation_1;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.Timer;
import java.util.TimerTask;

public class foregroundWork extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    private static  boolean IS_ACTIVITY_RUNNING = false;

    private myActivity task;

    private Timer myTimer;

    @Override
    public void onCreate() {
        super.onCreate();

        IS_ACTIVITY_RUNNING = true;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        int icon = R.drawable.ic_launcher_foreground;
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification =
                new Notification.Builder(this, "ForegroundServiceChannel")
                        .setContentTitle("Test1")
                        .setContentText("Test2")
                        .setSmallIcon(icon)
                        .setContentIntent(pendingIntent)
                        .setTicker("Test3")
                        .build();
        startForeground(1, notification);
        //do heavy work on a background thread
        //stopSelf();

        task = new myActivity(getApplicationContext());

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethodFG();
            }
        }, 0, MainActivity.updateTimeInMilliSeconds);

        return START_STICKY;
    }

    private void TimerMethodFG() {
        if (task.sec.isConnectionEstablished()) {
            task.noOfReads++;
            Log.d("RVS_001", "Timer Event");
            task.locationReader();
        } else {
            Log.d("RVS_001", "Timer Event, but no active connection");
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        task.disconnect();

        IS_ACTIVITY_RUNNING = false;
    }

    public static boolean isRunning() {
        return IS_ACTIVITY_RUNNING;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
