package com.bullhead.androidequalizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.bullhead.equalizer.Settings;

import java.util.ArrayList;

public class Settings1Activity extends AppCompatActivity {
    RecyclerView recycler;
    SettingsAdapter adapter;
    ArrayList<SettingsItem> settingsItems;
    private SharedPreferences sharedPreferences;
    int nr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings1);
        sharedPreferences=getSharedPreferences("myPref",MODE_PRIVATE);
        recycler=findViewById(R.id.recycler);
        settingsItems=new ArrayList<>();
        nr=getIntent().getIntExtra("nr",0);
        setSettings();
        setAdapter();
    }
    @Override
    public void onBackPressed()
    {
        if(nr==1)
        {
            Intent i=new Intent(getApplicationContext(),MainActivity.class);
            i.putExtra("nr",nr);
            startActivity(i);
        }
        super.onBackPressed();
    }

    private void setAdapter() {
        adapter=new SettingsAdapter(settingsItems,sharedPreferences);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(layoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
    }

    private void setSettings() {
        settingsItems.add(new SettingsItem(R.drawable.ic_dark,"Dark Theme","Dark Theme ",sharedPreferences.getBoolean("dark",false)));
        settingsItems.add(new SettingsItem(R.drawable.ic_audio,"Always attach to Global Mix","Enable this only if you are facing issues with me the atachment of the effects", false));
        settingsItems.add(new SettingsItem(R.drawable.ic_audio,"Reverb Effects","Reverb effects are ",sharedPreferences.getBoolean("reverb",false)));
        settingsItems.add(new SettingsItem(R.drawable.ic_audio,"Volume Slider","Volume slider is ",sharedPreferences.getBoolean("volume",false)));
        settingsItems.add(new SettingsItem(R.drawable.ic_notification,"Hide/Show Notifications","Open app's notification settings",false));
        settingsItems.add(new SettingsItem(R.drawable.ic_battery,"Disable Baterry Optimization","Disable battery optimization for equalizer for better functioning",false));
    }
}