package com.musicequalizer.appten;

import static com.musicequalizer.appten.MainActivity.sessionId;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.media.audiofx.Equalizer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.bullhead.equalizer.EqualizerFragment;
import com.bullhead.equalizer.Settings;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ExampleService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent=new Intent(getApplicationContext(),MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,notificationIntent,0);
        Notification notification=new NotificationCompat.Builder(getApplicationContext(), App.CHANNEL_ID)
                .setContentTitle("Equalizer")
                .setSmallIcon(R.drawable.ic_audio)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1,notification);
        return START_STICKY;
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
