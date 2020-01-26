package com.example.vedioplaytest.CameraSetting;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Camera2APIs {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray(4);

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 270);
        ORIENTATIONS.append(Surface.ROTATION_90, 180);
        ORIENTATIONS.append(Surface.ROTATION_180, 90);
        ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    interface Camera2Interface {
        void onCameraDeviceOpened(CameraDevice cameraDevice, Size cameraSize);
    }

    private Size mCameraSize;
    private Camera2Interface mInterface;
    private CameraCaptureSession mCaptureSession;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private Context mContext;
    private String mCameraId;
    private TextureView mTextureView;

    CameraManager cameraManager;

    public Camera2APIs(Camera2Interface impl) {
        mInterface = impl;
    }

    // 카메라 시스템 서비스 매니저 리턴.
    public CameraManager CameraManager_1(Activity activity, Context context, TextureView textureView) {
         cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        mContext = context;
        mTextureView = textureView;
        return cameraManager;
    }

    // 사용가능한 카메라 리스트를 가져와 후면 카메라(LENS_FACING_BACK) 사용하여 해당 cameraId 리턴.
    // StreamConfiguratonMap은 CaptureSession을 생성할때 surfaces를 설정하기 위한 출력 포맷 및 사이즈등의 정보를 가지는 클래스.
    // 사용가능한 출력 사이즈중 가장 큰 사이즈 선택.

    // 참고로, INFO_SUPPORTED_HARDWARE_LEVEL키값으로 카메라 디바이스의 레벨을 알 수 있는데
    // LEGACY < LIMITED < FULL < LEVEL_3 순으로 고성능이며 더 세밀한 카메라 설정이 가능하다.
    // LEGACY 디바이스의 경우 구형 안드로이드 단말 호환을 위해 Camera2 API는 기존 Camera API의 인터페이스에 불과하다.
    // 즉, 프레임 단위 컨트롤 등의 Camera2 기능은 사용할 수 없다.
    public String CameraCharacteristics_2(CameraManager cameraManager) {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    Size[] sizes = map.getOutputSizes(SurfaceTexture.class);
                    mCameraSize = sizes[0];
                    for (Size size : sizes) {
                        if (size.getWidth() > mCameraSize.getWidth()) {
                            mCameraSize = size;
                        }
                    }
                    mCameraId = cameraId;
                    return cameraId;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    // onOpened()에서 취득한 CameraDevice로 CaptureSession, CaptureRequest가 이뤄지는데
    // Camera2 APIs 처리과정을 MainActivity에서 일원화하여 표현하기 위해 인터페이스로 처리.
    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            mInterface.onCameraDeviceOpened(camera, mCameraSize);
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
        }
    };

    // 비동기 콜백 CameraDevice.StateCallback onOpened()로 취득.
    // null파라미터는 MainThread를 이용하고,
    // 작성한 Thread Handler를 넘겨주면 해당 Thread로 콜백이 떨어진다.
    // 비교적 딜레이가 큰(~500ms) 작업이라 Thread 권장.
    public void CameraDevice_3(CameraManager cameraManager, String cameraId, Handler handler) {
        try {
            cameraManager.openCamera(cameraId,mCameraDeviceStateCallback,null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // 생성된 세션에 카메라 프리뷰를 위한 CaptureRequest정보 설정.
    // 프리뷰 화면은 연속되는 이미지가 보여지기 때문에 CONTROL_AF_MODE_CONTINUOUS_PICTURE로 포커스를 지속적으로 맞추고,
    // setRepeatingRequest로 해당 세션에 설정된 CaptureRequest세팅으로 이미지를 지속적으로 요청.

    // setRepeatingRequest의 null파라미터는 MainThread를 사용하고,
    // 작성한 Thread Handler를 넘겨주면 해당 Thread로 콜백이 떨어진다.
    // 프리뷰 화면은 지속적으로 화면 캡쳐가 이뤄지기 때문에,
    // MainThread 사용 시 Frame drop이 발생할 수 있다.
    // Background Thread 사용 권장.

    // Google의 android-Camera2Basic샘플 코드에서는 CaptureRequest를 먼저 수행하는데,
    // 여기서는 Google 프레젠테이션 자료 및 모델 그림의 프로세스를 기준으로 작성하기 위해 CaptureSession을 먼저 수행.

    private CameraCaptureSession.StateCallback mCaptureSessionCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
            try {
                mCaptureSession = cameraCaptureSession;
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                cameraCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), mCaptureCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
        }
    };

    // 일단 세션이 생성(비동기)된 후에는 해당 CameraDevice에서 새로운 세션이 생성되거나 종료하기 이전에는 유효.
    public void CaptureSession_4(CameraDevice cameraDevice, Surface surface) {
        try {
            cameraDevice.createCaptureSession(Collections.singletonList(surface), mCaptureSessionCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // 카메라 프리뷰(CameraDevice.TEMPLATE_PREVIEW)를 위한 Builder 패턴의 CaptureRequest생성.
    // 예를 들어, 사진 촬영의 경우에는 CameraDevice.TEMPLATE_STILL_CAPTURE로 리퀘스트를 설정한다.
    // surface는 해당 세션에 사용된 surface를 타겟으로 설정.
    public void CaptureRequest_5(CameraDevice cameraDevice, Surface surface) {
        try {
            mPreviewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    protected void takePicture() {
        if (null == mCameraDevice) {
            return;
        }

        try {
            Size[] jpegSizes = null;
            CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            if (map != null) {
                jpegSizes = map.getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }

            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(mTextureView.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());

            captureBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);

            // Orientation
            int rotation = ((Activity) mContext).getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");

            final File file = new File(mContext.getCacheDir(), "pic_" + dateFormat.format(date) + ".jpg");

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);

                        Bitmap tempImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Matrix inversion = new Matrix();
                        inversion.setScale(-1, 1);
                        inversion.postRotate(90);
                        Bitmap reversed = Bitmap.createBitmap(tempImage, 0, 0, tempImage.getWidth(), tempImage.getHeight(), inversion, false);
                        save(reversed);

                    } finally {
                        if (image != null) {
                            image.close();
                            reader.close();
                        }
                    }
                }

                private void save(Bitmap bitmap) {
                    try {
                        file.createNewFile();  // 파일을 생성해주고
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);  // 넘거 받은 bitmap을 jpeg(손실압축)으로 저장해줌
                        out.close(); // 마무리로 닫아줍니다.
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            HandlerThread thread = new HandlerThread("CameraPicture");
            thread.start();
            final Handler backgroudHandler = new Handler(thread.getLooper());
            reader.setOnImageAvailableListener(readerListener, backgroudHandler);


            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session,
                                               CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(mContext, "사진촬영 성공", Toast.LENGTH_SHORT).show();
                    startPreview();
                }

            };

            mCameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, backgroudHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, backgroudHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startPreview() {
        CameraDevice_3(cameraManager,mCameraId,null);
    }

    // 캡쳐된 이미지 정보 및 Metadata가 넘어오는데, 프리뷰에서는 딱히 처리할 작업은 없다.
    // 사진 촬영의 경우라면, onCaptureCompleted()에서 촬영이 완료되고 이미지가 저장되었다는 메세지를 띄우는 시점.
    // 캡쳐 이미지와 Metadata 매칭은 Timestamp로 매칭가능하다.
    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }
    };

    public void closeCamera() {
        if (null != mCaptureSession) {
            mCaptureSession.close();
            mCaptureSession = null;
        }
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

}