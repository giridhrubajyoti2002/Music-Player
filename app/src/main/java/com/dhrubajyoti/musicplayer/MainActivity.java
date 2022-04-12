package com.dhrubajyoti.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.OnItemActivatedListener;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static ArrayList<AudioModel> mySongs = new ArrayList<>();
   public static RecyclerView songsRV;
   public static SongAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        songsRV = findViewById(R.id.songsRV);

        runtimePermission();

    }


    public void runtimePermission(){
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displaySongs();
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

//    public ArrayList<File> findSong(File file){
//        ArrayList<File> arrayList = new ArrayList<>();
//        File[] files = file.listFiles();
//        if(files != null) {
//            for (File singleFile : files) {
//                if (singleFile.isDirectory() && !singleFile.isHidden()) {
//                    System.out.println(singleFile.getPath());
//                    arrayList.addAll(findSong(singleFile));
//                } else if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {
//                    arrayList.add(singleFile);
//                }
//            }
//        }
//        return arrayList;
//    }

    public void displaySongs(){
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null, MediaStore.Audio.Media.TITLE);
        while(cursor.moveToNext()){
            AudioModel song = new AudioModel(cursor.getString(1),cursor.getString(0),cursor.getString(2));
            if(new File(song.getPath()).exists())
                mySongs.add(song);
        }
//         Set<File> songs = new HashSet<>();
//         if(Build.VERSION.SDK_INT > 29) {
//             songs.addAll(findSong(Environment.getDataDirectory()));
//             Toast.makeText(this, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(), Toast.LENGTH_SHORT).show();
//             mySongs = (findSong(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
//             songs.addAll(findSong(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)));

//         }else{
//             songs.addAll(findSong(Environment.getExternalStorageDirectory()));
//             Toast.makeText(this, String.valueOf(Build.VERSION.SDK_INT), Toast.LENGTH_SHORT).show();
//         }
         //mySongs = findSong(Environment.getExternalStorageDirectory());
         Collections.sort(mySongs, new Comparator<AudioModel>() {
             @Override
             public int compare(AudioModel t1, AudioModel t2) {
                 if (Character.isDigit(t1.getTitle().charAt(0)))
                     return 1;
                 if (Character.isDigit(t2.getTitle().charAt(0)))
                     return -1;
                 return t1.getTitle().compareToIgnoreCase(t2.getTitle());
             }
         });
        if(mySongs.size()==0){
            new AlertDialog.Builder(this).setMessage("No Songs Found").setCancelable(false).create().show();
        }

        songsRV.setLayoutManager(new LinearLayoutManager(this));
        songsRV.setAdapter(new SongAdapter(this,mySongs));  // context = getApplicationContext();


    }


    @Override
    protected void onResume() {
        super.onResume();
        if(songsRV!=null){
            songsRV.setAdapter(new SongAdapter(this, mySongs));
            songsRV.scrollToPosition(PlayerActivity.position>6?PlayerActivity.position-6:0);
        }
    }



}