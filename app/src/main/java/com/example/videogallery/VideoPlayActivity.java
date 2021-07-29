package com.example.videogallery;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

public class VideoPlayActivity extends AppCompatActivity {
    VideoView videoView;
    SeekBar seekBar;
    File file;
    boolean ready = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        file = new File(getIntent().getStringExtra("path"));
        videoView = findViewById(R.id.video);
        seekBar = findViewById(R.id.seek);
        videoView.setVideoPath(file.getPath());
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
                seekBar.setMax(mp.getDuration());
                ready = true;
            }
        });

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ready){
                    if (videoView.isPlaying()){
                        videoView.pause();
                        Toast.makeText(VideoPlayActivity.this, "Paused", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(VideoPlayActivity.this, "Resumed", Toast.LENGTH_SHORT).show();
                        videoView.start();
                    }
                }
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    seekBar.setProgress(videoView.getCurrentPosition(), true);
                } else {
                    seekBar.setProgress(videoView.getCurrentPosition());
                }
                handler.postDelayed(this, 500);
            }
        }, 500);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}