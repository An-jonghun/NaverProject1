package com.example.vedioplaytest;

import android.os.Environment;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoSetPath {

    public final static int URL = 1;
    public final static int SDCARD = 2;

    boolean videoReady = false;

    String VIDEO_Path;  //비디오 경로


    public VideoSetPath(VideoView videoView, MediaController mediaController) {

                VIDEO_Path = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_10mb.mp4"; //추후변경
                videoView.setVideoPath(VIDEO_Path);

        videoView.setMediaController(mediaController);
        videoReady = true;  //동영상재생 준비완료
    }

    public VideoSetPath(VideoView videoView, MediaController mediaController, int type) {

        switch (type) {
            case URL:
                //동영상 경로가 URL일 경우
                VIDEO_Path = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_10mb.mp4"; //추후변경
                videoView.setVideoPath(VIDEO_Path);
                break;
            case SDCARD:
                //동영상 경로가 SDCARD일 경우
                //VIDEO_Path = Environment.getExternalStorageDirectory() + "/TestVideo.mp4";
                VIDEO_Path = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_10mb.mp4"; //추후변경
                videoView.setVideoPath(VIDEO_Path);
                break;
        }
        videoView.setMediaController(mediaController);

        videoReady = true;  //동영상재생 준비완료
    }

    public boolean isVideoReady() {
        return videoReady;
    }
}
