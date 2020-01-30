package com.example.vedioplaytest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

public class GetVideoFrom extends Activity implements View.OnClickListener {

    private LinearLayout gallery;
    private LinearLayout internet;
    private static final int SELECT_GET_VIDEO_FROM = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_video_from);

        gallery = findViewById(R.id.from_gallery);
        internet = findViewById(R.id.from_internet);

        gallery.setOnClickListener(this);
        internet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int layoutId = v.getId();

        Log.d("결과", "버튼클릭! " + layoutId);
        Intent intent = new Intent();

        switch (layoutId) {
            case R.id.from_gallery:
                intent.putExtra("button", 5001);
                break;
            case R.id.from_internet:
                intent.putExtra("button", 5002);
                break;
        }

        setResult(SELECT_GET_VIDEO_FROM, intent);
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("button", 5003);
        setResult(SELECT_GET_VIDEO_FROM, intent);
        finish();
    }
}