package com.example.fyptest;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();

//        createNotificationChannel();
    }

//    private void createNotificationChannel() {
//        if (Build.VERSION_CODES.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Foreground Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            Notification manager = getSystemService(Notification.class);
//        }
//    }
}
