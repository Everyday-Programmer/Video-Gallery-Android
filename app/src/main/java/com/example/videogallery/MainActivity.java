package com.example.videogallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<VideoModel> arrayList;
    VideoAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        arrayList = new ArrayList<>();

        if (!permission()){
            requestPermission();
        } else {
            getVideos();
        }
    }

    public void getVideos(){
        arrayList.clear();

        int data;
        String path;
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, MediaStore.Video.Media.DATE_TAKEN + " DESC");
        data = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        while (cursor.moveToNext()){
            path = cursor.getString(data);
            File file = new File(path);
            VideoModel videoModel = new VideoModel();
            videoModel.setPath(file.getPath());
            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(path));
            try {
                videoModel.setDuration(timeConversion(mediaPlayer.getDuration()));
            } catch (Exception e) {
                e.printStackTrace();
                videoModel.setDuration("00 min, 00 sec");
            }
            arrayList.add(videoModel);
        }
        cursor.close();

        adapter = new VideoAdapter(getApplicationContext(), arrayList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new VideoAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int pos, View v) {
                Intent intent = new Intent(getApplicationContext(), VideoPlayActivity.class);
                intent.putExtra("path", arrayList.get(pos).getPath());
                startActivity(intent);
            }
        });
    }

    @SuppressLint("DefaultLocale")
    public String timeConversion(long value){
        String videoDuration;

        int dur = (int) value;
        int hrs = (dur / 3600000);
        int min = (dur / 60000) % 6000;
        int sec = (dur % 60000 / 1000);

        if (hrs > 0){
            videoDuration = String.format("%02d hrs, %02d min, %02d sec", hrs, min, sec);
        } else {
            videoDuration = String.format("%02d min, %02d sec");
        }
        return videoDuration;
    }

    public boolean permission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int result = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermission(){
        try {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 12);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 12){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else {
                Toast.makeText(this, "No permission to read storage", Toast.LENGTH_SHORT).show();
            }
        }
    }
}