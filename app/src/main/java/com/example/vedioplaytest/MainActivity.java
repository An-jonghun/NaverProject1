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
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.vedioplaytest.CameraSetting.CameraAction;
import com.example.vedioplaytest.VideoSetting.FindVideoPath;
import com.example.vedioplaytest.VideoSetting.Select_InternetView;
import com.example.vedioplaytest.VideoSetting.VideoSetPath;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String CAMERA = Manifest.permission.CAMERA;
    private static final String STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int PERMISSION_CAMERA = 0;
    private static final int PERMISSION_STORAGE_FOR_GALLERY = 2;
    private static final int PERMISSION_STORAGE_FOR_INTERNET = 3;

    private static final int SELECT_GALLERY = 1;
    private static final int SELECT_INTERNET = 2;
    public static int GET_VIEOTYPE = 0;

    private Button btnStart;
    private Button btnPause;
    private Button btnRestart;
    private Button btnGetVedioGallery;
    private Button btnGetVedioInternet;
    private Button btnGetCameraImage;


    VideoView videoView;    //비교할 영상
    TextureView myActionView;       // 내동작

    MediaController mediaController;
    VideoSetPath videoSetPath;
    CameraAction cameraAction;
    ControllActivity controllActivity;

    String videoPath;
    boolean videoReady = false; //  비디오영상이 준비됬는지 안됬는지 확인

    Intent intentGetVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 화면 꺼짐 방지
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        videoView = findViewById(R.id.videoView);
        myActionView = findViewById(R.id.myAction);

        btnStart = findViewById(R.id.btnStart);
        btnPause = findViewById(R.id.btnPause);
        btnRestart = findViewById(R.id.btnRestart);

        btnGetVedioGallery = findViewById(R.id.getVedioGallery);
        btnGetVedioInternet = findViewById(R.id.getVedioInternet);
        btnGetCameraImage = findViewById(R.id.getCameraImage);

        cameraAction = new CameraAction();

        if (checkCallingOrSelfPermission(CAMERA) != PackageManager.PERMISSION_DENIED) {
            initCamera();
        }

        btnStart.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnRestart.setOnClickListener(this);
        btnGetVedioGallery.setOnClickListener(this);
        btnGetVedioInternet.setOnClickListener(this);
        btnGetCameraImage.setOnClickListener(this);

        mediaController = new MediaController(this);

        videoView.requestFocus();
        // 동영상이 재생준비가 완료되엇을떄를 알수있는 리스너
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub
                if (GET_VIEOTYPE != 0) {
                    Toast.makeText(getApplicationContext(),
                            "동영상 준비완료. '시작'버튼을 누르세요.", Toast.LENGTH_LONG).show();
                    videoReady = videoSetPath.isVideoReady();
                }
            }
        });
        initPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void initPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        String[] permission_list = {STORAGE, CAMERA};

        for (String permission : permission_list) {
            //권한 허용 여부를 확인한다.
            int chk = checkCallingOrSelfPermission(permission);

            if (chk == PackageManager.PERMISSION_DENIED) {
                //권한 허용을여부를 확인하는 창을 띄운다
                requestPermissions(permission_list, 0);
            }
        }
    }

    private void initCamera() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.camera_area, cameraAction);
        transaction.commit();
    }

    public void requirePermission(String permission, int requestCode) {
        String[] perm = {permission};
        //권한 허용 여부를 확인한다.
        requestPermissions(perm, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            Log.d("테스트", permissions[i] + " : " + grantResults[i] + " = " + requestCode);
            //허용됬다면
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                if (requestCode == PERMISSION_CAMERA && CAMERA.equals(permissions[i])) {
                    initCamera();
                } else if (requestCode == PERMISSION_STORAGE_FOR_GALLERY) {
                    getVideo(R.id.getVedioGallery);
                } else if (requestCode == PERMISSION_STORAGE_FOR_INTERNET) {
                    getVideo(R.id.getVedioInternet);
                }
            } else {
                if (requestCode == 0 && CAMERA.equals(permissions[i])) {
                    Toast.makeText(getApplicationContext(), "카메라 실행을 위한 권한 설정이 필요합니다! ", Toast.LENGTH_LONG).show();
                    finish();
                } else if (requestCode == PERMISSION_STORAGE_FOR_GALLERY || requestCode == PERMISSION_STORAGE_FOR_INTERNET) {
                    Toast.makeText(this, "저장공간 권한 설정이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (GET_VIEOTYPE != 0) {
            controllActivity = new ControllActivity(videoView, cameraAction, getApplicationContext());
            if (GET_VIEOTYPE == SELECT_GALLERY) {
                controllActivity.setVIDEO_URL(videoPath);
            } else if (GET_VIEOTYPE == SELECT_INTERNET) {
                Intent videoURL = getIntent();
                videoPath = videoURL.getExtras().getString("VIDEO_URL");
                controllActivity.setVIDEO_URL(videoURL.getExtras().getString("VIDEO_URL"));
                controllActivity.setEXERCISE_NAME(videoURL.getExtras().getString("EXERCISE_NAME"));
                controllActivity.setSTOP_SECONDS(videoURL.getExtras().getIntArray("STOP_SECONDS"));
            } //인터넷에서 받아올 경우

            videoSetPath = new VideoSetPath(videoView, mediaController, GET_VIEOTYPE, videoPath);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        removeCache();
    }

    private void removeCache() {
        File cachPath = new File(this.getCacheDir() + "");
        Log.d("결과", "캐시 삭제 시작");

        if (cachPath.isDirectory()) {
            String[] children = cachPath.list();
            for (int i = 0; i < children.length; i++) {
                File target = new File(cachPath, children[i]);
                Log.d("결과", target.getPath() + "");
                target.delete();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int layoutId = v.getId();
        if (videoReady) {
            switch (layoutId) {
                case R.id.btnStart:
                    videoView.start();
                    controllActivity.videoControll();
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
            if (layoutId == R.id.btnStart || layoutId == R.id.btnPause || layoutId == R.id.btnRestart)
                Toast.makeText(this, "재생 준비가 되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }

        if (layoutId == R.id.getVedioGallery || layoutId == R.id.getVedioInternet) {
            if (checkCallingOrSelfPermission(STORAGE) == PackageManager.PERMISSION_DENIED) {
                if (layoutId == R.id.getVedioGallery) {
                    requirePermission(STORAGE, PERMISSION_STORAGE_FOR_GALLERY);
                } else {
                    requirePermission(STORAGE, PERMISSION_STORAGE_FOR_INTERNET);
                }
            } else {
                getVideo(layoutId);
            }
        }

        if (layoutId == R.id.getCameraImage) {
            String getResult = cameraAction.takePicture();
            Log.d("결과", "메인 엑티비티 " + getResult);
        }
    }

    private void getVideo(int layoutId) {
        if (layoutId == R.id.getVedioGallery) {
            GET_VIEOTYPE = SELECT_GALLERY;
            videoReady = false;
            intentGetVideo = new Intent(Intent.ACTION_GET_CONTENT);
            intentGetVideo.setType("video/*");
            startActivityForResult(intentGetVideo, SELECT_GALLERY);
        } else if (layoutId == R.id.getVedioInternet) {
            GET_VIEOTYPE = SELECT_INTERNET;
            videoReady = false;
            intentGetVideo = new Intent(this, Select_InternetView.class);
            startActivity(intentGetVideo);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri uri = data.getData();
            FindVideoPath mfindVideoPath = new FindVideoPath(uri);
            videoPath = mfindVideoPath.getRealPathFromURI(getApplicationContext(), uri);     //경로받기
            videoReady = true;
        } else {
            Toast.makeText(getApplicationContext(), "영상 받아오기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
            GET_VIEOTYPE = 0;
        }
    }
}