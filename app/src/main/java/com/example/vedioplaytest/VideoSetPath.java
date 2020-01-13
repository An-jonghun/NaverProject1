package com.example.vedioplaytest;

import android.net.Uri;
import android.os.Environment;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoSetPath {

    public final static int SELECT_GALLERY = 1;
    public final static int SELECT_INTERNET = 2;

    boolean videoReady = false;

    String VIDEO_Path;  //비디오 경로


    public VideoSetPath(VideoView videoView, MediaController mediaController) {

        VIDEO_Path = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_10mb.mp4"; //추후변경
         videoView.setVideoURI(Uri.parse(VIDEO_Path));

    /*    VIDEO_Path = "/storage/emulated/0/Download/big_buck_bunny_720p_10mb.mp4";
        videoView.setVideoPath(VIDEO_Path);*/
    //내부경로 사용

        videoView.setMediaController(mediaController);
        videoReady = true;  //동영상재생 준비완료
    }

    public VideoSetPath(VideoView videoView, MediaController mediaController, int GET_VIEOTYPE, String VIDEO_Path) {

        switch (GET_VIEOTYPE) {
            case SELECT_GALLERY:
                //동영상 경로가 내갤러리일 경우
                this.VIDEO_Path = VIDEO_Path;
                videoView.setVideoPath(this.VIDEO_Path);
                break;
            case SELECT_INTERNET:
                //동영상 경로가 인터넷일 경우
                VIDEO_Path = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_10mb.mp4"; //추후변경
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
