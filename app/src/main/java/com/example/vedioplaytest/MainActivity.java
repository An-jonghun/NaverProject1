package com.example.vedioplaytest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
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

    String[] permission_list = {
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

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

    VedioData vedioData;

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

        checkPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();

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

        videoView.requestFocus();
        // 동영상이 재생준비가 완료되엇을떄를 알수있는 리스너
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(),
                        "동영상 준비완료. '시작'버튼을 누르세요.", Toast.LENGTH_LONG).show();

                videoReady = videoSetPath.isVideoReady();
            }
        });

    }

    public void checkPermission(){
        for(String permission : permission_list){
            //권한 허용 여부를 확인한다.
            int chk = checkCallingOrSelfPermission(permission);

            if(chk == PackageManager.PERMISSION_DENIED){
                //권한 허용을여부를 확인하는 창을 띄운다
                requestPermissions(permission_list,0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0)
        {
            for(int i=0; i<grantResults.length; i++)
            {
                //허용됬다면
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                }
                else {
                    Toast.makeText(getApplicationContext(),"앱권한설정하세요",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(GET_VIEOTYPE!=0) {
            vedioData =new VedioData(videoView);
            if(GET_VIEOTYPE == SELECT_GALLERY){
                vedioData.setVIDEO_URL(videoPath);
            }
           else if (GET_VIEOTYPE == SELECT_INTERNET) {
                Intent videoURL = getIntent();
                videoPath = videoURL.getExtras().getString("VIDEO_URL");
                vedioData.setVIDEO_URL(videoURL.getExtras().getString("VIDEO_URL"));
                vedioData.setEXERCISE_NAME(videoURL.getExtras().getString("EXERCISE_NAME"));
                vedioData.setSTOP_SECONDS(videoURL.getExtras().getIntArray("STOP_SECONDS"));
            }//인터넷에서 받아올 경우
            videoSetPath = new VideoSetPath(videoView, mediaController, GET_VIEOTYPE, videoPath);
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
                    vedioData.videoControll();
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

