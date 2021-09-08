package com.bullhead.equalizer;

import java.io.Serializable;

/**
 * Created by Harjot on 09-Dec-16.
 */

public class EqualizerModel implements Serializable {
    private boolean isEqualizerEnabled;
    private int[]   seekbarpos = new int[5];
    private int     presetPos;
    private short   reverbPreset;
    private short   bassStrength;
    private short   loudnessStrength;
    private boolean reverbChecked;
    private boolean volumeChecked;
    public EqualizerModel() {
        isEqualizerEnabled = true;
        reverbPreset       = -1;
        bassStrength       = -1;
        loudnessStrength   = -1;
        reverbChecked=false;
        volumeChecked=false;
    }

    public boolean isVolumeChecked() {
        return volumeChecked;
    }

    public void setVolumeChecked(boolean volumeChecked) {
        this.volumeChecked = volumeChecked;
    }

    public boolean isReverbChecked() {
        return reverbChecked;
    }

    public void setReverbChecked(boolean reverbChecked) {
        this.reverbChecked = reverbChecked;
    }

    public short getLoudnessStrength() {
        return loudnessStrength;
    }

    public void setLoudnessStrength(short loudnessStrength) {
        this.loudnessStrength = loudnessStrength;
    }

    public boolean isEqualizerEnabled() {
        return isEqualizerEnabled;
    }

    public void setEqualizerEnabled(boolean equalizerEnabled) {
        isEqualizerEnabled = equalizerEnabled;
    }

    public int[] getSeekbarpos() {
        return seekbarpos;
    }

    public void setSeekbarpos(int[] seekbarpos) {
        this.seekbarpos = seekbarpos;
    }

    public int getPresetPos() {
        return presetPos;
    }

    public void setPresetPos(int presetPos) {
        this.presetPos = presetPos;
    }

    public short getReverbPreset() {
        return reverbPreset;
    }

    public void setReverbPreset(short reverbPreset) {
        this.reverbPreset = reverbPreset;
    }

    public short getBassStrength() {
        return bassStrength;
    }

    public void setBassStrength(short bassStrength) {
        this.bassStrength = bassStrength;
    }
}
