package com.example.vedioplaytest;

import android.os.Handler;
import android.widget.VideoView;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

public class VedioData implements Serializable {
  private String VIDEO_URL;
  private  String EXERCISE_NAME;
  private int[]STOP_SECONDS = new int[10];

  VideoView videoView;

    public VedioData(VideoView videoView) {
        this.videoView = videoView;
    }

    public String getVIDEO_URL() {
        return VIDEO_URL;
    }

    public void setVIDEO_URL(String VIDEO_URL) {
        this.VIDEO_URL = VIDEO_URL;
    }

    public void setEXERCISE_NAME(String EXERCISE_NAME) {
        this.EXERCISE_NAME = EXERCISE_NAME;
    }

    public void setSTOP_SECONDS(int[] STOP_SECONDS) {
        for (int i = 0; i <STOP_SECONDS.length ; i++) {
            this.STOP_SECONDS[i] = STOP_SECONDS[i];
        }
    }

    public void vedioStop(int second){      //정해진 시간에 비디오 멈춤
        TimerTask videoStop = new TimerTask() {
            public void run() {
                videoView.pause();
                videoResume();
            }
        };
        Timer timer = new Timer();
        timer.schedule(videoStop, second);
        }

        public void videoResume(){
            TimerTask videoResume = new TimerTask() {
                public void run() {
                    videoView.start();
                }
            };
            Timer timer1 = new Timer();
            timer1.schedule(videoResume,4000);   // 멈춘뒤 4초후 다시 재생
        }

        public void videoControll(){
            int second;
            for (int i = 0; i <this.STOP_SECONDS.length ; i++) {
                second = STOP_SECONDS[i];
                if(second!=0){
                    vedioStop(second+(i*4000));
                }
                else
                    return;
            }
        }

}
