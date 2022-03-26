package com.dhrubajyoti.musicplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.File;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;


class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{
    private static  Context mContext;
    private ArrayList<AudioModel> mySongs;

    public SongAdapter(Context mContext, ArrayList<AudioModel> mySongs) {
        this.mContext = mContext;
        this.mySongs = mySongs;
    }

    public SongAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.songNameTV.setText(mySongs.get(position).getTitle().replace(".mp3","").replace(".wav",""));
        if(PlayerActivity.position == position){
            holder.songNameTV.setSelected(true);
            holder.songNameTV.setTextColor(Color.YELLOW);
        }else{
            holder.songNameTV.setSelected(false);
            holder.songNameTV.setTextColor(Color.WHITE);
        }
        holder.parentRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try {
                    mContext.startActivity(new Intent(mContext, PlayerActivity.class)
                            .putExtra("mySongs", new Gson().toJson(mySongs))
                            .putExtra("position", holder.getAdapterPosition()));
//                }catch (Exception e){
//                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
//                    new AlertDialog.Builder(mContext).setTitle("Error").setMessage("Something wrong happened, please refresh")
//                            .setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            mContext.startActivity(new Intent(mContext, MainActivity.class));
//                        }
//                    }).create().show();
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mySongs.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView songNameTV;
        private RelativeLayout parentRL;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songNameTV = itemView.findViewById(R.id.songNameTV);
            parentRL = itemView.findViewById(R.id.parentRL);
        }

    }



//    public static void  setSelected(int position, boolean isSelected){
//        ViewHolder holder = (ViewHolder)MainActivity.songsRV.findViewHolderForAdapterPosition(position);
//        if(holder != null){
//            holder.songNameTV.setSelected(isSelected);
//            if(isSelected)
//                holder.songNameTV.setTextColor(Color.YELLOW);
//            else
//                holder.songNameTV.setTextColor(Color.WHITE);
//            Toast.makeText(mContext, String.valueOf(position), Toast.LENGTH_SHORT).show();
//        }else {
//            Toast.makeText(mContext, String.valueOf(position), Toast.LENGTH_SHORT).show();
//        }
//    }
}

