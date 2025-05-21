package com.rutvik.locofit.exerciseActivities;

import com.rutvik.locofit.R;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class ExerciseVideoActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_video);

        // Initialize the VideoView
        videoView = findViewById(R.id.videoView);

        // Get the URL of the video from the intent (if passed)
        Intent intent = getIntent();
        String videoUrl = intent.getStringExtra("VIDEO_URL");

        // If video URL is missing, show an error or use default video from raw folder
        if (videoUrl == null || videoUrl.isEmpty()) {
            Toast.makeText(this, "Video URL is missing. Playing default video.", Toast.LENGTH_SHORT).show();

            // Access the video from the raw folder if URL is not provided
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.walking);
            videoView.setVideoURI(videoUri);
        } else {
            // If valid URL, use it
            videoView.setVideoPath(videoUrl);
        }

        // Set up MediaController for play, pause buttons
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Start the video
        videoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null) {
            videoView.pause(); // Pause the video when the activity is paused
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null && !videoView.isPlaying()) {
            videoView.start(); // Resume video playback if it's not already playing
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback(); // Stop the video when the activity is destroyed
        }
    }
}
