package com.musicequalizer.appten;

import static com.musicequalizer.appten.App.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class ExampleService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input=intent.getStringExtra("inputExtra");
        Intent notificationIntent=new Intent(getApplicationContext(),MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,notificationIntent,0);
        Notification notification=new NotificationCompat.Builder(getApplicationContext(), App.CHANNEL_ID)
                .setContentTitle("Equalizer")
                .setSmallIcon(R.drawable.ic_audio)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1,notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
