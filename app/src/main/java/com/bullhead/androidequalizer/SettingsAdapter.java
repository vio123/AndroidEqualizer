package com.bullhead.androidequalizer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    public void onBindViewHolder(@NonNull SettingsAdapter.MyViewHolder holder, int position) {
        holder.logoImg.setImageResource(settingsItems.get(position).getIcon());
        holder.titleTxt.setText(settingsItems.get(position).getTitle());
        holder.subTitleTxt.setText(settingsItems.get(position).getSubTitle());
    }

    @Override
    public int getItemCount() {
        return settingsItems.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView logoImg;
        TextView titleTxt,subTitleTxt;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            logoImg=itemView.findViewById(R.id.logoImg);
            titleTxt=itemView.findViewById(R.id.titleTxt);
            subTitleTxt=itemView.findViewById(R.id.subTitleTxt);
        }
    }
}
