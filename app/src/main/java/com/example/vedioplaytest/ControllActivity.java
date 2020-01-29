package com.example.vedioplaytest;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.vedioplaytest.CameraSetting.CameraAction;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

public class ControllActivity implements Serializable {
    private String VIDEO_URL;
    private String EXERCISE_NAME;
    private int[] STOP_SECONDS = new int[10];

    VideoView videoView;
    CameraAction cameraAction;
    Context context;

    public ControllActivity(VideoView videoView, CameraAction cameraAction, Context context) {
        this.videoView = videoView;
        this.cameraAction = cameraAction;
        this.context = context;
    }

    public void setVIDEO_URL(String VIDEO_URL) {
        this.VIDEO_URL = VIDEO_URL;
    }

    public void setEXERCISE_NAME(String EXERCISE_NAME) {
        this.EXERCISE_NAME = EXERCISE_NAME;
    }

    public void setSTOP_SECONDS(int[] STOP_SECONDS) {
        for (int i = 0; i < STOP_SECONDS.length; i++) {
            this.STOP_SECONDS[i] = STOP_SECONDS[i];
        }
    }

    public void videoControll() {
        int second;
        for (int i = 0; i < this.STOP_SECONDS.length; i++) {
            second = STOP_SECONDS[i];
            if (second != 0) {
                vedioStop(second + (i * 4000));
            } else
                return;
        }
    }

    public void vedioStop(int second) {      //정해진 시간에 비디오 멈춤
        final TimerTask videoStop = new TimerTask() {
            public void run() {
                videoView.pause();
                videoResume();
                cameraCapture();
            }
        };
        Timer timer = new Timer();
        timer.schedule(videoStop, second);
    }

    public void videoResume() {       // 멈춘뒤 4초후 다시 재생
        TimerTask videoResume = new TimerTask() {
            public void run() {
                videoView.start();
            }
        };
        Timer timer1 = new Timer();
        timer1.schedule(videoResume, 4000);
    }

    public void cameraCapture() {            // 카메라 캡쳐
        cameraAction.takePicture();
    }
}
