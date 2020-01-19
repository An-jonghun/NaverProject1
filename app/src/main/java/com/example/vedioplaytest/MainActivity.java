package com.example.vedioplaytest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import com.example.vedioplaytest.CameraSetting.CameraAction;
import com.example.vedioplaytest.VideoSetting.FindVideoPath;
import com.example.vedioplaytest.VideoSetting.Select_InternetView;
import com.example.vedioplaytest.VideoSetting.VideoSetPath;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SELECT_GALLERY = 1;
    private static final int SELECT_INTERNET = 2;
    public static int GET_VIEOTYPE = 0;

    private Button btnStart;
    private Button btnPause;
    private Button btnRestart;
    private Button btnGetVedioGallery;
    private Button btnGetVedioInternet;


     VideoView videoView;    //비교할 영상
     TextureView myActionView;       // 내동작

    MediaController mediaController;
    VideoSetPath videoSetPath;

    String videoPath;
    boolean videoReady = false; //  비디오영상이 준비됬는지 안됬는지 확인

    Intent intentGetVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);
        myActionView = findViewById(R.id.myAction);

        btnStart = findViewById(R.id.btnStart);
        btnPause = findViewById(R.id.btnPause);
        btnRestart = findViewById(R.id.btnRestart);

        btnGetVedioGallery = findViewById(R.id.getVedioGallery);
        btnGetVedioInternet = findViewById(R.id.getVedioInternet);

        CameraAction camera = new CameraAction();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.camera_area, camera);
        transaction.commit();

        btnStart.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnRestart.setOnClickListener(this);
        btnGetVedioGallery.setOnClickListener(this);
        btnGetVedioInternet.setOnClickListener(this);

        mediaController = new MediaController(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(GET_VIEOTYPE!=0) {
            if (GET_VIEOTYPE == SELECT_INTERNET) {
                Intent videoURL = getIntent();
                videoPath = videoURL.getExtras().getString("VIDEO_URL");
            }//인터넷에서 받아올 경우

            videoSetPath = new VideoSetPath(videoView, mediaController, GET_VIEOTYPE, videoPath);
            videoReady = videoSetPath.isVideoReady();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    public void onClick(View v) {
        if (videoReady) {
            switch (v.getId()) {
                case R.id.btnStart:
                    videoView.start();
                    break;
                case R.id.btnPause:
                    videoView.pause();
                    break;
                case R.id.btnRestart:
                    videoView.seekTo(0);
                    videoView.start();
                    break;
            }
        } else {
           if(v.getId()==R.id.btnStart || v.getId()==R.id.btnPause || v.getId()==R.id.btnRestart)
            Toast.makeText(this, "재생 준비가 되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }

        switch (v.getId()) {
            case R.id.getVedioGallery:
                GET_VIEOTYPE = SELECT_GALLERY;
                videoReady = false;
                intentGetVideo = new Intent(Intent.ACTION_GET_CONTENT);
                intentGetVideo.setType("video/*");
                startActivityForResult(intentGetVideo, SELECT_GALLERY);
                break;
            case R.id.getVedioInternet:
                GET_VIEOTYPE = SELECT_INTERNET;
                videoReady = false;
                intentGetVideo = new Intent(this, Select_InternetView.class);
                startActivity(intentGetVideo);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        FindVideoPath mfindVideoPath = new FindVideoPath(uri);
        videoPath = mfindVideoPath.getRealPathFromURI(getApplicationContext(),uri);     //경로받기
    }

}

