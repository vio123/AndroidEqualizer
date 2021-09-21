package com.musicequalizer.appten;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.audiofx.Equalizer;
import android.util.Log;

import com.bullhead.equalizer.EqualizerFragment;
import com.bullhead.equalizer.Settings;

public class AudioSessionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
            int id=intent.getIntExtra(Equalizer.EXTRA_AUDIO_SESSION,-1);
            MainActivity.eq(context,id);
            Log.i("id123", "DA");
    }
}
