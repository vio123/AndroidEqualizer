package com.musicequalizer.appten;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.audiofx.Equalizer;
import android.util.Log;

import com.bullhead.equalizer.EqualizerFragment;

public class AudioSessionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(Equalizer.EXTRA_AUDIO_SESSION, -1);
        String packageName = intent.getStringExtra(Equalizer.EXTRA_PACKAGE_NAME);
        Log.i("id123", String.valueOf(id));
        MainActivity.eq(context,id);
    }
}
