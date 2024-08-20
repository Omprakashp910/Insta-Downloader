package com.dizaraa.apps;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

import com.dizaraa.apps.R;


public class VideoPreviewActivity extends AppCompatActivity {
    VideoView displayVV;
    ImageView backIV;
    String videoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }

        backIV = findViewById(R.id.backIV);
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        videoPath = getIntent().getStringExtra("videoPath");
        displayVV = findViewById(R.id.displayVV);

        displayVV.setVideoPath(videoPath);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(displayVV);

        displayVV.setMediaController(mediaController);

        displayVV.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        displayVV.setVideoPath(videoPath);
        displayVV.start();
    }
}
