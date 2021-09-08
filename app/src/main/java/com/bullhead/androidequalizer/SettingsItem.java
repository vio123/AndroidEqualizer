package com.bullhead.androidequalizer;

public class SettingsItem {
    private int icon;
    private String title;
    private String subTitle;
    private boolean isChecked;
    public SettingsItem(int icon, String title, String subTitle,boolean isChecked) {
        this.icon = icon;
        this.title = title;
        this.subTitle = subTitle;
        this.isChecked=isChecked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
}
