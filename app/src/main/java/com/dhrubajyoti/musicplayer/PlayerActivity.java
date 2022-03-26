package com.dhrubajyoti.musicplayer;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.dhrubajyoti.musicplayer.databinding.ActivityPlayerBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayerActivity extends AppCompatActivity {
    private ActivityPlayerBinding binding;
    private ArrayList<AudioModel> mySongs = new ArrayList<>();
    public static int position = -2;
    private static int pos = -1;
    public static MediaPlayer mediaPlayer = new MediaPlayer();
//    private Thread updateSeekBar;
    Intent intent;
//    int x = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        intent = getIntent();
//        ERROR_CODE = intent.getIntExtra("error",-2);
//        if(ERROR_CODE == -1){
//            SongAdapter.setSelected(position,true);
//            onBackPressed();
//            return;
//        }
        pos = intent.getIntExtra("position", 0);
        mySongs = new Gson().fromJson(intent.getStringExtra("mySongs"),new TypeToken<ArrayList<AudioModel>>(){}.getType());

        binding.songTitleTV.setSelected(true);
//        updateSeekBar = new Thread(){
//            @Override
//            public void run() {
//                int totalDuration = mediaPlayer.getDuration();
//                int currentPosition = 0;
//                while(currentPosition<totalDuration){
//                    try{
//                        sleep(500);
//                        currentPosition = mediaPlayer.getCurrentPosition();
//                        binding.seekBar.setProgress(currentPosition);
//                        binding.startSongTV.setText(getTime(mediaPlayer.getCurrentPosition()));
//                    }catch (InterruptedException|IllegalStateException e){
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                int totalDuration = mediaPlayer.getDuration();
//                int currentPosition = 0;
//                while(currentPosition<totalDuration){
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        binding.seekBar.setProgress(currentPosition);
                        binding.currentTimeTV.setText(getTime(currentPosition));
                    if(mediaPlayer.isPlaying()){
                        binding.btnPlay.setBackgroundResource(R.drawable.ic_pause);
//                        binding.imgSong.setRotation(x++);
                    }else {
                        binding.btnPlay.setBackgroundResource(R.drawable.ic_play);
//                        binding.imgSong.setRotation(0);
                    }
                new Handler().postDelayed(this,100);
            }
        });

        if(position != pos) {
            position = pos;
            playMusic();
        }else {
//            int currentPosition = mediaPlayer.getCurrentPosition();
//            boolean isPause = !mediaPlayer.isPlaying();
//            mediaPlayer.stop();
//            mediaPlayer.release();
//            mediaPlayer = MediaPlayer.create(this, Uri.parse(mySongs.get(position).getPath()));
//            mediaPlayer.start();
//            mediaPlayer.seekTo(currentPosition);
            binding.songTitleTV.setText(mySongs.get(position).getTitle().replace(".mp3","").replace(".wav",""));
            binding.seekBar.setMax(mediaPlayer.getDuration());
            binding.endTimeTV.setText(getTime(mediaPlayer.getDuration()));
//            if(mediaPlayer.isPlaying())
//                startAnimation();
            int audioSessionId = mediaPlayer.getAudioSessionId();
            if(audioSessionId != -1)
                binding.barVisualizer.setAudioSessionId(audioSessionId);
        }

//        updateSeekBar.start();
        binding.seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        binding.seekBar.getThumb().setColorFilter(getResources().getColor(R.color.white),PorterDuff.Mode.SRC_IN);
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                binding.startSongTV.setText(getTime(mediaPlayer.getCurrentPosition()));
//                handler.postDelayed(this,500);
//            }
//        },500);

        binding.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
//                    binding.btnPlay.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }else{
//                    binding.btnPlay.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                    startAnimation();
                }
            }
        });
        // next listener
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                binding.btnSkipNext.callOnClick();
            }
        });
        binding.btnSkipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SongAdapter.setSelected(position,false);
                position = ++position==mySongs.size()?0:position;
                playMusic();
            }
        });
        binding.btnSkipPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SongAdapter.setSelected(position,false);
                if(mediaPlayer.getCurrentPosition() < 30000) {
                    position = --position < 0 ? mySongs.size() - 1 : position;
                    playMusic();
                }else{
                    mediaPlayer.seekTo(0);
                }
            }
        });
        binding.btnFastForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
            }
        });
        binding.btnFastRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
            }
        });


    }

    public void startAnimation(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.imgSong,"rotation", 0f,360f)
                .setDuration(1200);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    public void playMusic(){
//        if(mediaPlayer != null){
//            mediaPlayer.stop();
//            mediaPlayer.release();
//        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(mySongs.get(position).getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            startAnimation();
            binding.songTitleTV.setText(mySongs.get(position).getTitle().replace(".mp3","").replace(".wav",""));
            binding.btnPlay.setBackgroundResource(R.drawable.ic_pause);
//            binding.seekBar.setProgress(0);
            binding.seekBar.setMax(mediaPlayer.getDuration());
            binding.endTimeTV.setText(getTime(mediaPlayer.getDuration()));
            int audioSessionId = mediaPlayer.getAudioSessionId();
            if(audioSessionId != -1)
                binding.barVisualizer.setAudioSessionId(audioSessionId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTime(int duration){
        String time = "";
        int min = (int) (duration/1000/60);
        int sec = (int) (duration/1000) % 60;
        if(min<10)
            time += "0";
        time += min+":";
        if(sec<10)
            time += "0";
        time += sec;
        return time;
    }

    @Override
    protected void onDestroy() {
        if(binding.barVisualizer!=null)
            binding.barVisualizer.release();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}