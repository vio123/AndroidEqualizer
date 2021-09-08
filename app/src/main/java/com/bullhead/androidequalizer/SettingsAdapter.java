package com.bullhead.androidequalizer;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.MyViewHolder> {
    private ArrayList<SettingsItem> settingsItems;

    public SettingsAdapter(ArrayList<SettingsItem> settingsItems) {
        this.settingsItems = settingsItems;
    }

    @NonNull
    @Override
    public SettingsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.settingsitem,parent,false);
       return new MyViewHolder(view);
    }
    String subTitle;
    @Override
    public void onBindViewHolder(@NonNull final SettingsAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.logoImg.setImageResource(settingsItems.get(position).getIcon());
        holder.titleTxt.setText(settingsItems.get(position).getTitle());
        holder.subTitleTxt.setText(settingsItems.get(position).getSubTitle());
         subTitle=holder.subTitleTxt.getText().toString();
        if(position==0)
        {
            if(holder.check.isChecked())
              subTitle+="Enabled";
            else{
                subTitle+="Disabled";
            }
        }else if(position==2)
        {
            if(holder.check.isChecked())
                subTitle+="enabled";
            else{
                subTitle+="disabled";
            }
        }else if(position==3)
        {
            if(holder.check.isChecked())
                subTitle+="visible";
            else{
                subTitle+="hidden";
            }
        }
        holder.subTitleTxt.setText(subTitle);
        holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                subTitle=settingsItems.get(position).getSubTitle();
                if(position==0)
                {
                    if(isChecked)
                        subTitle+="Enabled";
                    else{
                        subTitle+="Disabled";
                    }
                }else if(position==2)
                {
                    if(isChecked)
                        subTitle+="enabled";
                    else{
                        subTitle+="disabled";
                    }
                }else if(position==3)
                {
                    if(isChecked)
                        subTitle+="visible";
                    else{
                        subTitle+="hidden";
                    }
                }
                holder.subTitleTxt.setText(subTitle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return settingsItems.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView logoImg;
        TextView titleTxt,subTitleTxt;
        CheckBox check;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            logoImg=itemView.findViewById(R.id.logoImg);
            titleTxt=itemView.findViewById(R.id.titleTxt);
            subTitleTxt=itemView.findViewById(R.id.subTitleTxt);
            check=itemView.findViewById(R.id.check);
        }
    }
}
