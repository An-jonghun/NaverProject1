package com.example.vedioplaytest;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CameraAction extends Fragment implements Camera2APIs.Camera2Interface, TextureView.SurfaceTextureListener {
    private Camera2APIs mCamera;
    private TextureView myActionView;       // 내동작

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_layout, container, false);
        myActionView = view.findViewById(R.id.myAction);
        myActionView.setSurfaceTextureListener(this);
        mCamera = new Camera2APIs(this);

        return view;
    }

    public Bitmap getImage() {
        return myActionView.getBitmap();
    }

    // openCamera()만 호출하면 5단계 과정이 전부 수행되며, 프리뷰가 이뤄진다.
    private void openCamera() {
        CameraManager cameraManager = mCamera.CameraManager_1(getActivity());
        String cameraId = mCamera.CameraCharacteristics_2(cameraManager);
        mCamera.CameraDevice_3(cameraManager, cameraId);
    }

    @Override
    public void onCameraDeviceOpened(CameraDevice cameraDevice, Size cameraSize) {
        SurfaceTexture texture = myActionView.getSurfaceTexture();
        texture.setDefaultBufferSize(cameraSize.getWidth(), cameraSize.getHeight());
        Surface surface = new Surface(texture);
        mCamera.CaptureSession_4(cameraDevice, surface);
        mCamera.CaptureRequest_5(cameraDevice, surface);
    }

    // Surface Texture가 준비 완료된 콜백을 받으면, 카메라 오픈.

    @Override
    public void onResume() {
        super.onResume();
        if (myActionView.isAvailable()) {
            openCamera();
        } else {
            myActionView.setSurfaceTextureListener(this);
        }
    } /* Surface Callbacks */

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        openCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    void closeCamera() {
        mCamera.closeCamera();
    }

    @Override
    public void onPause() {
        closeCamera();
        super.onPause();
    }
}
