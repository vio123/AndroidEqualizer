package com.bullhead.equalizer;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.LoudnessEnhancer;
import android.media.audiofx.PresetReverb;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class EqualizerFragment extends Fragment {

    public static final String ARG_AUDIO_SESSIOIN_ID = "audio_session_id";

    static int       themeColor = Color.parseColor("#B24242");
    static int theme;
    public Equalizer mEqualizer;
    public SwitchCompat equalizerSwitch;
    public BassBoost bassBoost;
    LineChartView chart;
    public PresetReverb presetReverb;
    public LoudnessEnhancer loudnessEnhancer;
    public LinearLayout reverbLL,volumeLL;
    SeekBar seekVolume;
    BubbleSeekBar reverbSeek;
    RelativeLayout rl, mainLayout, presetsMainLayout;
    Button bassBtn,loudBtn,controllerBtn,eqBtn;
    int y = 0,z=0;
    SwitchCompat reverbSwitch,switchBass,switchLoudness,switchController3D,volumeCheck;
    ImageView    spinnerDropDownIcon;
    TextView     fragTitle, roomValueFromBubble;
    LinearLayout mLinearLayout,eq;

    SeekBar[] seekBarFinal = new SeekBar[5];

    AnalogController bassController, reverbController,loudnessController;

    Spinner presetSpinner;

    FrameLayout equalizerBlocker;


    Context ctx;

    public EqualizerFragment() {
        // Required empty public constructor
    }

    LineSet dataset;
    Paint   paint;
    float[] points;
    short   numberOfFrequencyBands;
    private int     audioSesionId;
    static  boolean showBackButton = true;

    public static EqualizerFragment newInstance(int audioSessionId) {

        Bundle args = new Bundle();
        args.putInt(ARG_AUDIO_SESSIOIN_ID, audioSessionId);

        EqualizerFragment fragment = new EqualizerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Settings.isEditing = true;

        if (getArguments() != null && getArguments().containsKey(ARG_AUDIO_SESSIOIN_ID)) {
            audioSesionId = getArguments().getInt(ARG_AUDIO_SESSIOIN_ID);
        }

        if (Settings.equalizerModel == null) {
            Settings.equalizerModel = new EqualizerModel();
            Settings.equalizerModel.setReverbPreset(PresetReverb.PRESET_NONE);
            Settings.equalizerModel.setBassStrength((short) (1000 / 19));
        }

        mEqualizer = new Equalizer(0, audioSesionId);
        bassBoost = new BassBoost(0, audioSesionId);
        bassBoost.setEnabled(Settings.isEqualizerEnabled);
        BassBoost.Settings bassBoostSettingTemp = bassBoost.getProperties();
        BassBoost.Settings bassBoostSetting     = new BassBoost.Settings(bassBoostSettingTemp.toString());
        bassBoostSetting.strength = Settings.equalizerModel.getBassStrength();
        bassBoost.setProperties(bassBoostSetting);
        try {
            presetReverb = new PresetReverb(0, audioSesionId);
            presetReverb.setPreset(Settings.equalizerModel.getReverbPreset());
            presetReverb.setEnabled(Settings.isEqualizerEnabled);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        loudnessEnhancer=new LoudnessEnhancer(audioSesionId);
        loudnessEnhancer.setTargetGain(Settings.equalizerModel.getLoudnessStrength());
        loudnessEnhancer.setEnabled(Settings.isEqualizerEnabled);
        mEqualizer.setEnabled(Settings.isEqualizerEnabled);

        if (Settings.presetPos == 0) {
            for (short bandIdx = 0; bandIdx < mEqualizer.getNumberOfBands(); bandIdx++) {
                mEqualizer.setBandLevel(bandIdx, (short) Settings.seekbarpos[bandIdx]);
            }
        } else {
            mEqualizer.usePreset((short) Settings.presetPos);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_equalizer, container, false);
    }
    SharedPreferences sharedPreferences;
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLinearLayout = view.findViewById(R.id.equalizerContainer);
        presetSpinner = view.findViewById(R.id.equalizer_preset_spinner);
        eqBtn=view.findViewById(R.id.eqBtn);
        controllerBtn=view.findViewById(R.id.controllerBtn);
        roomValueFromBubble = view.findViewById(R.id.roomValue);
        bassBtn=view.findViewById(R.id.bassBtn);
        loudBtn=view.findViewById(R.id.loudBtn);
        spinnerDropDownIcon = view.findViewById(R.id.spinner_dropdown_icon);
        eq=view.findViewById(R.id.eq);
        reverbSeek=view.findViewById(R.id.reverbSeek);
        seekVolume=view.findViewById(R.id.seekVolume);
        volumeCheck=view.findViewById(R.id.volumeCheck);
        switchController3D=view.findViewById(R.id.switchController3D);
        switchBass=view.findViewById(R.id.switchBass);
        switchLoudness=view.findViewById(R.id.switchLoudness);
        reverbSwitch=view.findViewById(R.id.reverbSwitch);
        sharedPreferences= ctx.getSharedPreferences("myPref",Context.MODE_PRIVATE);
        reverbLL=view.findViewById(R.id.reverbLL);
        volumeLL=view.findViewById(R.id.volumeLL);
        fragTitle = view.findViewById(R.id.equalizer_fragment_title);
        equalizerSwitch = view.findViewById(R.id.equalizer_switch);
        equalizerSwitch.setChecked(Settings.isEqualizerEnabled);
        chart   = view.findViewById(R.id.lineChart);
        mainLayout = view.findViewById(R.id.equalizer_action_container);
        presetsMainLayout = view.findViewById(R.id.presetsmainLayout);
        equalizerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEqualizer.setEnabled(isChecked);
                loudnessController.setEnabled(isChecked);
                bassBoost.setEnabled(isChecked);
                presetReverb.setEnabled(isChecked);
                presetSpinner.setEnabled(isChecked);
                spinnerDropDownIcon.setEnabled(isChecked);
              /*  if(!switchBass.isChecked())
                {
                    switchBass.setEnabled(isChecked);
                }
                if(!switchLoudness.isChecked())
                {
                    switchLoudness.setEnabled(isChecked);
                }
                if(!switchController3D.isChecked())
                {
                    switchController3D.setEnabled(isChecked);
                }
                if(!reverbSwitch.isChecked())
                {
                    reverbSwitch.setEnabled(isChecked);
                }
                if(!volumeCheck.isChecked())
                {
                    volumeCheck.setEnabled(isChecked);
                }*/
                Settings.isEqualizerEnabled = isChecked;
                for(int i=0;i<5;++i)
                {
                    seekBarFinal[i].setEnabled(isChecked);
                }
                if(isChecked)
                {
                    //eqBtn.setVisibility(View.INVISIBLE);
                    chart.setAlpha(1);
                    mLinearLayout.setAlpha(1);
                }else{
                    //eqBtn.setVisibility(View.VISIBLE);
                    chart.setAlpha(0.5f);
                    mLinearLayout.setAlpha(0.5f);
                }
            }
        });

        if(volumeCheck.isChecked())
        {
            for ( int i = 0; i < volumeLL.getChildCount();  i++ ){
                View view1 = volumeLL.getChildAt(i);
                view1.setEnabled(true);
                view1.setAlpha(1);// Or whatever you want to do with the view.
            }
        }else{
            seekVolume.setEnabled(false);
            for ( int i = 0; i < volumeLL.getChildCount();  i++ ){
                View view1 = volumeLL.getChildAt(i);
                view1.setEnabled(false);
                view1.setAlpha(0.5f);// Or whatever you want to do with the view.
            }
        }
        volumeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for ( int i = 0; i < volumeLL.getChildCount();  i++ ){
                    View view1 = volumeLL.getChildAt(i);
                    view1.setEnabled(isChecked);
                    if(isChecked)
                    {
                        view1.setAlpha(1);
                    }else{
                        view1.setAlpha(0.5f);
                    }
                }
            }
        });
        if(sharedPreferences.getBoolean("reverb",false))
        {
            reverbLL.setVisibility(View.VISIBLE);
        }else{
            reverbLL.setVisibility(View.GONE);
        }
        if(sharedPreferences.getBoolean("volume",false))
        {
            volumeLL.setVisibility(View.VISIBLE);
        }else{
            volumeLL.setVisibility(View.GONE);
        }
        if(reverbSwitch.isChecked())
        {
            for ( int i = 0; i < reverbLL.getChildCount();  i++ ){
                View view1 = reverbLL.getChildAt(i);
                view1.setEnabled(true);
                view1.setAlpha(1);// Or whatever you want to do with the view.
            }
        }else{
            for ( int i = 0; i < reverbLL.getChildCount();  i++ ){
                View view1 = reverbLL.getChildAt(i);
                view1.setEnabled(false);
                view1.setAlpha(0.5f);// Or whatever you want to do with the view.
            }
        }
        reverbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for ( int i = 0; i < reverbLL.getChildCount();  i++ ){
                    View view1 = reverbLL.getChildAt(i);
                    view1.setEnabled(isChecked);
                    if(isChecked)
                    {
                        view1.setAlpha(1);
                    }else{
                        view1.setAlpha(0.5f);
                    }
                }
            }
        });
        rl=view.findViewById(R.id.rl);
        int nightModeFlags =
                getContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags)
        {
            case Configuration.UI_MODE_NIGHT_YES:
                //   eq.setBackgroundResource(R.drawable.bgcardview);
                //  chart.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                //  presetsMainLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                //  mLinearLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                //  fragTitle.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                //  rl.setBackgroundColor(getResources().getColor(android.R.color.black));
                themeColor=Color.parseColor("#1ECD5D");
                theme=getResources().getColor(android.R.color.black);
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                //   eq.setBackgroundResource(R.drawable.bgcardview_white);
                //   fragTitle.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                //rl.setBackgroundColor(getResources().getColor(R.color.purple_700));
                themeColor=Color.parseColor("#E81515");
                theme=getResources().getColor(android.R.color.white);
                break;
        }
        reverbSeek.setBubbleColor(themeColor);
        reverbSeek.setThumbColor(themeColor);

        reverbSeek.setSecondTrackColor(themeColor);
        reverbSeek.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                if (progress == 0)
                {
                    roomValueFromBubble.setText("None");
                }
                else if (progress == 20)
                {
                    roomValueFromBubble.setText("Small room");
                }
                else if (progress == 40)
                {
                    roomValueFromBubble.setText("Medium room");
                }
                else if (progress == 60)
                {
                    roomValueFromBubble.setText("Large room");
                }
                else if (progress == 80)
                {
                    roomValueFromBubble.setText("Medium hall");
                }
                else if (progress == 100)
                {
                    roomValueFromBubble.setText("Large hall");
                }
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                if (progress == 0)
                {
                    roomValueFromBubble.setText("None");
                }
                else if (progress == 20)
                {
                    roomValueFromBubble.setText("Small room");
                }
                else if (progress == 40)
                {
                    roomValueFromBubble.setText("Medium room");
                }
                else if (progress == 60)
                {
                    roomValueFromBubble.setText("Large room");
                }
                else if (progress == 80)
                {
                    roomValueFromBubble.setText("Medium hall");
                }
                else if (progress == 100)
                {
                    roomValueFromBubble.setText("Large hall");
                }

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

                if (progress == 0)
                {
                    roomValueFromBubble.setText("None");
                }
                else if (progress == 20)
                {
                    roomValueFromBubble.setText("Small room");
                }
                else if (progress == 40)
                {
                    roomValueFromBubble.setText("Medium room");
                }
                else if (progress == 60)
                {
                    roomValueFromBubble.setText("Large room");
                }
                else if (progress == 80)
                {
                    roomValueFromBubble.setText("Medium hall");
                }
                else if (progress == 100)
                {
                    roomValueFromBubble.setText("Large hall");
                }
            }
        });
        spinnerDropDownIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presetSpinner.performClick();
            }
        });

        equalizerBlocker = view.findViewById(R.id.equalizerBlocker);


        paint   = new Paint();
        dataset = new LineSet();


        bassController   = view.findViewById(R.id.controllerBass);
        bassController.setOnTouchListener(new SeekBar.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int action = event.getAction();
                switch (action)
                {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                v.onTouchEvent(event);
                return true;
            }
        });
        reverbController = view.findViewById(R.id.controller3D);
        reverbController.setOnTouchListener(new SeekBar.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int action = event.getAction();
                switch (action)
                {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                v.onTouchEvent(event);
                return true;
            }
        });
        loudnessController=view.findViewById(R.id.controllerLoudness);
        loudnessController.setOnTouchListener(new SeekBar.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int action = event.getAction();
                switch (action)
                {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                v.onTouchEvent(event);
                return true;
            }
        });
        bassController.setLabel("");
        reverbController.setLabel("");
        loudnessController.setLabel("");
        bassController.circlePaint2.setColor(themeColor);
        bassController.linePaint.setColor(themeColor);
        bassController.textPaint.setColor(themeColor);
        bassController.invalidate();
        reverbController.circlePaint2.setColor(themeColor);
        reverbController.linePaint.setColor(themeColor);
        reverbController.textPaint.setColor(themeColor);
        reverbController.circlePaint.setColor(Color.RED);
        //reverbController.setBackgroundColor(theme);
        reverbController.invalidate();
        loudnessController.circlePaint2.setColor(themeColor);
        loudnessController.linePaint.setColor(themeColor);
        loudnessController.textPaint.setColor(themeColor);
        loudnessController.invalidate();
        if(switchBass.isChecked())
        {
            bassBtn.setVisibility(View.INVISIBLE);
            bassBoost.setEnabled(true);
            bassController.setAlpha(1);
        }else{
            bassBtn.setVisibility(View.VISIBLE);
            bassBoost.setEnabled(false);
            bassController.setAlpha(0.6f);
        }
        if(switchLoudness.isChecked())
        {
            loudBtn.setVisibility(View.INVISIBLE);
            loudnessEnhancer.setEnabled(true);
            loudnessController.setAlpha(1);
        }else{
            loudnessEnhancer.setEnabled(false);
            loudnessController.setAlpha(0.6f);
            loudBtn.setVisibility(View.VISIBLE);
        }
        controllerBtn.setAlpha(0);
        if(switchController3D.isChecked())
        {
            controllerBtn.setVisibility(View.INVISIBLE);
            presetReverb.setEnabled(true);
            reverbController.setAlpha(1);
        }else{
            presetReverb.setEnabled(false);
            reverbController.setAlpha(0.6f);
            controllerBtn.setVisibility(View.VISIBLE);
        }
        switchBass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bassBoost.setEnabled(isChecked);
                if(isChecked)
                {
                    bassBtn.setVisibility(View.INVISIBLE);
                    bassController.setAlpha(1);
                }else{
                    bassController.setAlpha(0.6f);
                    bassBtn.setVisibility(View.VISIBLE);
                }
            }
        });
        switchLoudness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                loudnessEnhancer.setEnabled(isChecked);
                if(isChecked)
                {
                    loudnessController.setAlpha(1);
                    loudBtn.setVisibility(View.INVISIBLE);
                }else{
                    loudnessController.setAlpha(0.6f);
                    loudBtn.setVisibility(View.VISIBLE);
                }
            }
        });
        switchController3D.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                presetReverb.setEnabled(isChecked);
                if(isChecked)
                {
                    reverbController.setAlpha(1);
                    controllerBtn.setVisibility(View.INVISIBLE);
                }else{
                    reverbController.setAlpha(0.6f);
                    controllerBtn.setVisibility(View.VISIBLE);
                }
            }
        });
        if (!Settings.isEqualizerReloaded) {
            int x = 0;
            if (bassBoost != null) {
                try {
                    x = ((bassBoost.getRoundedStrength() * 19) / 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(loudnessEnhancer!=null)
            {
                try{
                    z= (int) ((loudnessEnhancer.getTargetGain()*19)/16);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            if (presetReverb != null) {
                try {
                    y = (presetReverb.getPreset() * 19) / 6;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (x == 0) {
                bassController.setProgress(1);
            } else {
                bassController.setProgress(x);
            }
            if(switchController3D.isChecked()) {
                if (y == 0) {
                    reverbController.setProgress(1);
                } else {
                    reverbController.setProgress(y);
                }
            }
            if(z==0)
            {
                loudnessController.setProgress(1);
            }else{
                loudnessController.setProgress(z);
            }
        } else {
            int x = ((Settings.bassStrength * 19) / 1000);
            y = (Settings.reverbPreset * 19) / 6;
            z=(Settings.loudnessStrength*19)/6;
            if (x == 0) {
                bassController.setProgress(1);
            } else {
                bassController.setProgress(x);
            }

            if (y == 0) {
                reverbController.setProgress(1);
            } else {
                reverbController.setProgress(y);
            }
            if(z==0)
            {
                loudnessController.setProgress(1);
            }else{
                loudnessController.setProgress(z);
            }
        }
        bassController.setOnProgressChangedListener(new AnalogController.onProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {
                Settings.bassStrength = (short) (((float) 1000 / 19) * (progress));
                try {

                    bassBoost.setStrength(Settings.bassStrength);
                    Settings.equalizerModel.setBassStrength(Settings.bassStrength);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        loudnessController.setOnProgressChangedListener(new AnalogController.onProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {
                Settings.loudnessStrength = (short) ((progress * 6) / 19);
                Settings.equalizerModel.setLoudnessStrength(Settings.loudnessStrength);
                try {
                    loudnessEnhancer.setTargetGain(Settings.loudnessStrength);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        reverbController.setOnProgressChangedListener(new AnalogController.onProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {
                Settings.reverbPreset = (short) ((progress * 6) / 19);
                Settings.equalizerModel.setReverbPreset(Settings.reverbPreset);
                try {
                    presetReverb.setPreset(Settings.reverbPreset);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                y = progress;
            }
        });


        TextView equalizerHeading = new TextView(getContext());
        equalizerHeading.setText(R.string.eq);
        equalizerHeading.setTextSize(20);
        equalizerHeading.setGravity(Gravity.CENTER_HORIZONTAL);

        numberOfFrequencyBands = 5;

        points = new float[numberOfFrequencyBands];

        final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];
        final short upperEqualizerBandLevel = mEqualizer.getBandLevelRange()[1];

        for (short i = 0; i < numberOfFrequencyBands; i++) {
            final short    equalizerBandIndex      = i;
            final TextView frequencyHeaderTextView = new TextView(getContext());
            frequencyHeaderTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            frequencyHeaderTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            frequencyHeaderTextView.setTextColor(Color.parseColor("#FFFFFF"));
            frequencyHeaderTextView.setText((mEqualizer.getCenterFreq(equalizerBandIndex) / 1000) + "Hz");

            LinearLayout seekBarRowLayout = new LinearLayout(getContext());
            seekBarRowLayout.setOrientation(LinearLayout.VERTICAL);

            TextView lowerEqualizerBandLevelTextView = new TextView(getContext());
            lowerEqualizerBandLevelTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            lowerEqualizerBandLevelTextView.setTextColor(Color.parseColor("#FFFFFF"));
            lowerEqualizerBandLevelTextView.setText((lowerEqualizerBandLevel / 100) + "dB");

            TextView upperEqualizerBandLevelTextView = new TextView(getContext());
            lowerEqualizerBandLevelTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            upperEqualizerBandLevelTextView.setTextColor(Color.parseColor("#FFFFFF"));
            upperEqualizerBandLevelTextView.setText((upperEqualizerBandLevel / 100) + "dB");

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.weight = 1;

            SeekBar  seekBar  = new SeekBar(getContext());
            TextView textView = new TextView(getContext());
            switch (i) {
                case 0:
                    seekBar = view.findViewById(R.id.seekBar1);
                    textView = view.findViewById(R.id.textView1);
                    break;
                case 1:
                    seekBar = view.findViewById(R.id.seekBar2);
                    textView = view.findViewById(R.id.textView2);
                    break;
                case 2:
                    seekBar = view.findViewById(R.id.seekBar3);
                    textView = view.findViewById(R.id.textView3);
                    break;
                case 3:
                    seekBar = view.findViewById(R.id.seekBar4);
                    textView = view.findViewById(R.id.textView4);
                    break;
                case 4:
                    seekBar = view.findViewById(R.id.seekBar5);
                    textView = view.findViewById(R.id.textView5);
                    break;
            }
            seekBarFinal[i] = seekBar;
            //seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(colo, PorterDuff.Mode.SRC_IN));
            seekBar.getThumb().setColorFilter(new PorterDuffColorFilter(themeColor, PorterDuff.Mode.SRC_IN));
            seekBar.setId(i);
//            seekBar.setLayoutParams(layoutParams);
            seekBar.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);

            textView.setText(frequencyHeaderTextView.getText());
            textView.setTextColor(Color.WHITE);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            if (Settings.isEqualizerReloaded) {
                points[i] = Settings.seekbarpos[i] - lowerEqualizerBandLevel;
                dataset.addPoint(frequencyHeaderTextView.getText().toString(), points[i]);
                seekBar.setProgress(Settings.seekbarpos[i] - lowerEqualizerBandLevel);
            } else {
                points[i] = mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel;
                dataset.addPoint(frequencyHeaderTextView.getText().toString(), points[i]);
                seekBar.setProgress(mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel);
                Settings.seekbarpos[i]       = mEqualizer.getBandLevel(equalizerBandIndex);
                Settings.isEqualizerReloaded = true;
            }

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mEqualizer.setBandLevel(equalizerBandIndex, (short) (progress + lowerEqualizerBandLevel));
                    points[seekBar.getId()]                                  = mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel;
                    Settings.seekbarpos[seekBar.getId()]                     = (progress + lowerEqualizerBandLevel);
                    Settings.equalizerModel.getSeekbarpos()[seekBar.getId()] = (progress + lowerEqualizerBandLevel);
                    dataset.updateValues(points);
                    chart.notifyDataUpdate();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    presetSpinner.setSelection(0);
                    Settings.presetPos = 0;
                    Settings.equalizerModel.setPresetPos(0);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        equalizeSound();

        //paint.setColor(Color.parseColor("#555555"));
        paint.setStrokeWidth((float) (1.10 * Settings.ratio));

        dataset.setColor(themeColor);
        dataset.setSmooth(true);
        dataset.setThickness(5);

        chart.setXAxis(false);
        chart.setYAxis(false);

        chart.setYLabels(AxisController.LabelPosition.NONE);
        chart.setXLabels(AxisController.LabelPosition.NONE);
        chart.setGrid(ChartView.GridType.NONE, 7, 10, paint);

        chart.setAxisBorderValues(-300, 3300);

        chart.addData(dataset);
        chart.show();

        Button mEndButton = new Button(getContext());
        mEndButton.setBackgroundColor(themeColor);
        mEndButton.setTextColor(Color.WHITE);
        eqBtn.setVisibility(View.INVISIBLE);
        if(equalizerSwitch.isChecked())
        {
            for(int i=0;i<5;++i)
            {
                seekBarFinal[i].setEnabled(true);
            }
            spinnerDropDownIcon.setEnabled(true);
            //eqBtn.setVisibility(View.INVISIBLE);
            presetSpinner.setEnabled(true);
            chart.setAlpha(1);
        }else{
            //eqBtn.setVisibility(View.VISIBLE);
            spinnerDropDownIcon.setEnabled(false);
            for(int i=0;i<5;++i)
            {
                seekBarFinal[i].setEnabled(false);
            }
            presetSpinner.setEnabled(false);
            chart.setAlpha(0.5f);
        }
    }

    public void equalizeSound() {
        ArrayList<String> equalizerPresetNames = new ArrayList<>();
        ArrayAdapter<String> equalizerPresetSpinnerAdapter = new ArrayAdapter<>(ctx,
                R.layout.spinner_item,
                equalizerPresetNames);
        equalizerPresetSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        equalizerPresetNames.add("Custom");

        for (short i = 0; i < mEqualizer.getNumberOfPresets(); i++) {
            equalizerPresetNames.add(mEqualizer.getPresetName(i));
        }

        presetSpinner.setAdapter(equalizerPresetSpinnerAdapter);
        //presetSpinner.setDropDownWidth((Settings.screen_width * 3) / 4);
        if (Settings.isEqualizerReloaded && Settings.presetPos != 0) {
//            correctPosition = false;
            presetSpinner.setSelection(Settings.presetPos);
        }

        presetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (position != 0) {
                        mEqualizer.usePreset((short) (position - 1));
                        Settings.presetPos = position;
                        short numberOfFreqBands = 5;

                        final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];

                        for (short i = 0; i < numberOfFreqBands; i++) {
                            seekBarFinal[i].setProgress(mEqualizer.getBandLevel(i) - lowerEqualizerBandLevel);
                            points[i]                                  = mEqualizer.getBandLevel(i) - lowerEqualizerBandLevel;
                            Settings.seekbarpos[i]                     = mEqualizer.getBandLevel(i);
                            Settings.equalizerModel.getSeekbarpos()[i] = mEqualizer.getBandLevel(i);
                        }
                        dataset.updateValues(points);
                        chart.notifyDataUpdate();
                    }
                } catch (Exception e) {
                    Toast.makeText(ctx, "Error while updating Equalizer", Toast.LENGTH_SHORT).show();
                }
                Settings.equalizerModel.setPresetPos(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mEqualizer != null) {
            mEqualizer.release();
        }

        if (bassBoost != null) {
            bassBoost.release();
        }

        if (presetReverb != null) {
            presetReverb.release();
        }

        Settings.isEditing = false;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private int id = -1;

        public Builder setAudioSessionId(int id) {
            this.id = id;
            return this;
        }

        public Builder setAccentColor(int color) {
            themeColor = color;
            return this;
        }

        public Builder setShowBackButton(boolean show) {
            showBackButton = show;
            return this;
        }

        public EqualizerFragment build() {
            return EqualizerFragment.newInstance(id);
        }
    }


}