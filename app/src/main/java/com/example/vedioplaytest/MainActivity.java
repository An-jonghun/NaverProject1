package com.example.vedioplaytest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

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

        btnStart.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnRestart.setOnClickListener(this);
        btnGetVedioGallery.setOnClickListener(this);
        btnGetVedioInternet.setOnClickListener(this);

        mediaController = new MediaController(this);

       /* VideoSetPath videoSetPath = new VideoSetPath(videoView, mediaController);
        videoReady = videoSetPath.isVideoReady();
        //테스트값*/

        if (GET_VIEOTYPE != 0) {    //갤러리나 인터넷중에 선택했을경우
            VideoSetPath videoSetPath = new VideoSetPath(videoView, mediaController, GET_VIEOTYPE,videoPath);

            Log.e("경로값",videoPath);

            if (GET_VIEOTYPE == GET_VIEOTYPE) {     //갤러리에서 선택한 비디오영상 재생
                videoReady = videoSetPath.isVideoReady();
            } else if (GET_VIEOTYPE == SELECT_INTERNET) {
                videoReady = videoSetPath.isVideoReady();

            } else {        //아무것도 선택안했을경우
                Toast.makeText(this, "갤러리나 인터넷에서 영상을 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
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
            Toast.makeText(this, "재생에 문제가 있습니다.", Toast.LENGTH_SHORT).show();
        }

        switch (v.getId()) {
            case R.id.getVedioGallery:
                GET_VIEOTYPE = SELECT_GALLERY;
                intentGetVideo = new Intent(Intent.ACTION_GET_CONTENT);
                intentGetVideo.setType("video/*");
                startActivityForResult(intentGetVideo, SELECT_GALLERY);
                break;
            case R.id.getVedioInternet:
                GET_VIEOTYPE = SELECT_INTERNET;
                intentGetVideo = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.naver.com"));
                startActivity(intentGetVideo);
                //  @TODO: 인터넷에서 동영상 String path값 받기;
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        videoPath = getRealPathFromURI(uri);
    }

    private String getRealPathFromURI(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(uri, proj, null, null, null);
        int index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        c.moveToFirst();
        String path = c.getString(index);

        return path;
    }
}

