package com.bullhead.androidequalizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class Settings1Activity extends AppCompatActivity {
    RecyclerView recycler;
    SettingsAdapter adapter;
    ArrayList<SettingsItem> settingsItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings1);
        recycler=findViewById(R.id.recycler);
        settingsItems=new ArrayList<>();
        setSettings();
        setAdapter();
    }

    private void setAdapter() {
        adapter=new SettingsAdapter(settingsItems);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(layoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
    }

    private void setSettings() {
        settingsItems.add(new SettingsItem(R.drawable.ic_dark,"Dark Theme","Dark Theme "));
        settingsItems.add(new SettingsItem(R.drawable.ic_audio,"Always attach to Global Mix","Enable this only if you are facing issues with me the atachment of the effects"));
        settingsItems.add(new SettingsItem(R.drawable.ic_audio,"Reverb Effects","Reverb effects are "));
        settingsItems.add(new SettingsItem(R.drawable.ic_audio,"Volume Slider","Volume slider is "));
    }
}