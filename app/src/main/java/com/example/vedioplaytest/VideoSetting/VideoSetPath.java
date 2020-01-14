package com.example.vedioplaytest.VideoSetting;

import android.net.Uri;
import android.os.Environment;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoSetPath {

    public final static int SELECT_GALLERY = 1;
    public final static int SELECT_INTERNET = 2;

    boolean videoReady = false;

    String VIDEO_Path;  //비디오 경로
    String Video_name;

    public VideoSetPath(VideoView videoView, MediaController mediaController, int GET_VIEOTYPE, String VIDEO_Path) {

        switch (GET_VIEOTYPE) {
            case SELECT_GALLERY:
                //동영상 경로가 내갤러리일 경우
                this.VIDEO_Path = VIDEO_Path;
                videoView.setVideoPath(this.VIDEO_Path);
                break;
            case SELECT_INTERNET:
                //동영상 경로가 인터넷일 경우
                this.VIDEO_Path = VIDEO_Path;
            videoView.setVideoURI(Uri.parse(VIDEO_Path));
                break;
        }
        videoView.setMediaController(mediaController);

        videoReady = true;  //동영상재생 준비완료
    }

    public boolean isVideoReady() {
        return videoReady;
    }
}
