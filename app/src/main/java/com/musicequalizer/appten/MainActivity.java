package com.musicequalizer.appten;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.bullhead.equalizer.EqualizerFragment;
import com.bullhead.equalizer.EqualizerModel;
import com.bullhead.equalizer.Settings;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    static int sessionId;
    AudioSessionReceiver receiver=new AudioSessionReceiver();
    private SharedPreferences sharedPreferences;
    public  EqualizerFragment equalizerFragment;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nr = getIntent().getIntExtra("nr", 0);
        sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);
        getSupportActionBar().setTitle("Equalizer");
        if (sharedPreferences.getBoolean("dark", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (sessionId == 0)
            sessionId = audioManager.generateAudioSessionId();
         equalizerFragment = EqualizerFragment.newBuilder()
                .setAccentColor(Color.parseColor("#4caf50"))
                .setAudioSessionId(sessionId)
                .build();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.eqFrame, equalizerFragment).commit();
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }
    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals("switch")) {
                Intent serviceIntent=new Intent(getApplicationContext(),ExampleService.class);
                if(Settings.isEqualizerEnabled)
                {
                    startService(serviceIntent);
                }else{
                    stopService(serviceIntent);
                }
                // Toast.makeText(getApplicationContext(),"da",Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public static void eq(Context context, int id) {
        sessionId = id;
    }

    @Override
    protected void onResume() {
        super.onResume();
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (sessionId == 0)
            sessionId = audioManager.generateAudioSessionId();
         equalizerFragment = EqualizerFragment.newBuilder()
                .setAccentColor(Color.parseColor("#4caf50"))
                .setAudioSessionId(sessionId)
                .build();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.eqFrame, equalizerFragment).commit();
        if (nr == 1) {
            loadEqualizerSettings();
            nr=0;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    int nr;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.loadPreset) {
            nr = 1;
            loadEqualizerSettings();
            return true;
        } else if (item.getItemId() == R.id.savePreset) {
            saveEqualizerSettings();
            return true;
        } else if (item.getItemId() == R.id.settings) {
            Intent i = new Intent(getApplicationContext(), Settings1Activity.class);
            i.putExtra("nr", nr);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveEqualizerSettings() {
        if (Settings.equalizerModel != null) {
            EqualizerSettings settings = new EqualizerSettings();
            settings.bassStrength = Settings.equalizerModel.getBassStrength();
            settings.presetPos = Settings.equalizerModel.getPresetPos();
            settings.reverbPreset = Settings.equalizerModel.getReverbPreset();
            settings.seekbarpos = Settings.equalizerModel.getSeekbarpos();
            settings.loudnessStrength = Settings.equalizerModel.getLoudnessStrength();
            settings.isReverbChecked = sharedPreferences.getBoolean("reverb", false);
            settings.isVolumeChecked = sharedPreferences.getBoolean("volume", false);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

            Gson gson = new Gson();
            preferences.edit()
                    .putString(PREF_KEY, gson.toJson(settings))
                    .apply();
        }
    }

    public void loadEqualizerSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Gson gson = new Gson();
        EqualizerSettings settings = gson.fromJson(preferences.getString(PREF_KEY, "{}"), EqualizerSettings.class);
        EqualizerModel model = new EqualizerModel();
        model.setBassStrength(settings.bassStrength);
        model.setPresetPos(settings.presetPos);
        model.setReverbPreset(settings.reverbPreset);
        model.setSeekbarpos(settings.seekbarpos);
        model.setLoudnessStrength(settings.loudnessStrength);
        Settings.isEqualizerEnabled = true;
        Settings.isEqualizerReloaded = true;
        if (!settings.isReverbChecked) {
            Settings.reverbChecked = sharedPreferences.getBoolean("reverb", false);
        } else {
            Settings.reverbChecked = settings.isReverbChecked;
        }
        if (!settings.isVolumeChecked) {
            Settings.volumeChecked = sharedPreferences.getBoolean("volume", false);
        } else {
            Settings.volumeChecked = settings.isVolumeChecked;
        }
        Settings.bassStrength = settings.bassStrength;
        Settings.presetPos = settings.presetPos;
        Settings.reverbPreset = settings.reverbPreset;
        Settings.seekbarpos = settings.seekbarpos;
        Settings.equalizerModel = model;
        Settings.loudnessStrength = settings.loudnessStrength;
        EqualizerFragment equalizerFragment = EqualizerFragment.newBuilder()
                .setAccentColor(Color.parseColor("#4caf50"))
                .setAudioSessionId(sessionId)
                .build();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.eqFrame, equalizerFragment)
                .commit();
    }

    public static final String PREF_KEY = "equalizer";
}
