package com.bullhead.equalizer;

import androidx.appcompat.widget.SwitchCompat;

public class Settings {
    public static boolean        isEqualizerEnabled  = true;
    public static boolean        isEqualizerReloaded = true;
    public static int[]          seekbarpos          = new int[5];
    public static int            presetPos;
    public static short          reverbPreset        = -1;
    public static short          bassStrength        = -1;
    public static short         loudnessStrength  =-1;
    public static EqualizerModel equalizerModel;
    public static double         ratio               = 1.0;
    public static boolean        isEditing           = false;
    public static boolean   reverbChecked=false;
    public static boolean   volumeChecked=false;
}
