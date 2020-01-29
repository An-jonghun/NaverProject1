package com.example.vedioplaytest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.app.Activity;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

    private static final int SELECT_GET_VIDEO_FROM = 5000;

    private static final int SELECT_GALLERY = 1;
    private static final int SELECT_INTERNET = 2;
    public static int GET_VIEOTYPE = 0;

    private ImageButton btnStart;
    private ImageButton btnPause;
    private ImageButton btnRestart;

    private LinearLayout videoLayer;

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

        videoLayer = findViewById(R.id.video_layer);
        videoLayer.setVisibility(View.INVISIBLE);

        cameraAction = new CameraAction();

        if (checkCallingOrSelfPermission(CAMERA) != PackageManager.PERMISSION_DENIED) {
            initCamera();
        }

        btnStart.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnRestart.setOnClickListener(this);

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

        setActionBar();
    }

    private void setActionBar() {
        MainActionBar mActionBar = new MainActionBar(this, getSupportActionBar());
        mActionBar.setActionBar(R.layout.main_action_bar);
    }

    public void layoutClick(View view) {
        int layoutId = view.getId();

        if (layoutId == R.id.call_video_from_outsource) {
            Intent intent = new Intent(this, GetVideoFrom.class);
            startActivityForResult(intent, SELECT_GET_VIDEO_FROM);
        }
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
                    getVideo(5001);
                } else if (requestCode == PERMISSION_STORAGE_FOR_INTERNET) {
                    getVideo(5002);
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
// TODO: 인터넷에서 영상 받아오는 부분 구조 변경해서 startActivityForResult로 결과 받은 후에 영상 연결하기

//        if (GET_VIEOTYPE != 0) {
//            controllActivity = new ControllActivity(videoView, cameraAction, getApplicationContext());
//            if (GET_VIEOTYPE == SELECT_GALLERY) {
//                controllActivity.setVIDEO_URL(videoPath);
//            } else if (GET_VIEOTYPE == SELECT_INTERNET) {
//                Intent videoURL = getIntent();
//                videoPath = videoURL.getExtras().getString("VIDEO_URL");
//                controllActivity.setVIDEO_URL(videoURL.getExtras().getString("VIDEO_URL"));
//                controllActivity.setEXERCISE_NAME(videoURL.getExtras().getString("EXERCISE_NAME"));
//                controllActivity.setSTOP_SECONDS(videoURL.getExtras().getIntArray("STOP_SECONDS"));
//            } //인터넷에서 받아올 경우
//
//            videoSetPath = new VideoSetPath(videoView, mediaController, GET_VIEOTYPE, videoPath);
//        }
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
/*
        if (layoutId == R.id.getCameraImage) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("결과", "캡쳐 스레드 시작");
                    tempImage = cameraAction.takePicture();
                    Log.d("결과", "메인 엑티비티 " + tempImage);

                    if (!"Error".equals(tempImage) && !"null".equals(tempImage)) {
                        String resultJSON = poseEstimation.estimate(tempImage);

                        Log.d("결과", resultJSON);
                    }
                }
            }).start();
        }
*/
    }

    private void getVideo(int select) {
        if (select == 5001) {
            GET_VIEOTYPE = SELECT_GALLERY;
            videoReady = false;
            intentGetVideo = new Intent(Intent.ACTION_GET_CONTENT);
            intentGetVideo.setType("video/*");
            startActivityForResult(intentGetVideo, SELECT_GALLERY);
        } else if (select == 5002) {
            GET_VIEOTYPE = SELECT_INTERNET;
            videoReady = false;
            intentGetVideo = new Intent(this, Select_InternetView.class);
            startActivity(intentGetVideo);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            Log.d("결과",  "요청 취소");

            return;
        }
        if (requestCode == SELECT_GET_VIDEO_FROM) {
            int select = data.getIntExtra("button", 5000);
            Log.d("결과", "인텐트 버튼클릭 " + select);

            if (checkCallingOrSelfPermission(STORAGE) == PackageManager.PERMISSION_DENIED) {
                if (select == 5001) {
                    requirePermission(STORAGE, PERMISSION_STORAGE_FOR_GALLERY);
                } else if (select == 5002) {
                    requirePermission(STORAGE, PERMISSION_STORAGE_FOR_INTERNET);
                } else {
                    // 선택 취소
                }
            } else {
                getVideo(select);
            }
        }
        else {
            Log.d("결과", "취소 ? " + requestCode + " and " + resultCode);
            if (data != null) {
                Uri uri = data.getData();
                FindVideoPath mfindVideoPath = new FindVideoPath(uri);
                videoPath = mfindVideoPath.getRealPathFromURI(getApplicationContext(), uri);     //경로받기
                videoReady = true;
                videoLayer.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getApplicationContext(), "영상 받아오기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                GET_VIEOTYPE = 0;
            }
        }
    }
}