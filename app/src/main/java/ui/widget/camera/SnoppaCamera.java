package ui.widget.camera;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;


import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import app.App;
import ui.widget.camera.view.utils.EncodeUtil;

public class SnoppaCamera {
    private static final String TAG = "SPACMA";

    private Camera mCamera;

    private Camera.Parameters parameters;

    private static SnoppaCamera snoppaCamera;

    private List<Camera.Size> cameraSizes;

    private Camera.Size currentSize;

    private int defalutWidth = 1920;
    private int defalutHeight = 1080;

    private CaptureListner captureListner;

    private RecordListner recordListner;

    private EncodeUtil encodUtil;


    public void setCaptureListner(CaptureListner captureListner) {
        this.captureListner = captureListner;
    }

    public void setRecordListner(RecordListner recordListner) {
        this.recordListner = recordListner;
    }


    public static SnoppaCamera getInstance() {
        if (snoppaCamera == null) {
            snoppaCamera = new SnoppaCamera();
        }
        return snoppaCamera;
    }

    public boolean isPreview = false;

    public boolean isOpen;

    private static final String PICTURE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/";

    public void open(int camerId) {
        if (mCamera != null) {
            close();
        }
        int num = Camera.getNumberOfCameras();
        if (num < 1) {
            Log.w(TAG, "open camera failed,camera num:" + num);
            return;
        }
        mCamera = Camera.open(camerId);
        if (mCamera == null) {
            Log.w(TAG, "open camera failed.");
            return;
        }
        parameters = mCamera.getParameters();
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        cameraSizes = parameters.getSupportedPreviewSizes();
        Collections.sort(cameraSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size o1, Camera.Size o2) {
                return o2.width * o2.height - o1.width * o1.height;
            }
        });
        setParameters(parameters);
        isOpen = true;
    }

    public void onSwitchCamera(int cameraId) {
        setCameraId(cameraId);
        onSetup(cameraId,surfaceTexture);
        onStartPreview(cameraId);

    }
    int cameraId;

    public int getCameraId() {
        return cameraId;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }

    SurfaceTexture surfaceTexture;
    public void onSetup(int cameraId, SurfaceTexture surfaceTexture) {
        this.surfaceTexture=surfaceTexture;
        try {
            if (mCamera != null) {
                close();
            }
            int num = Camera.getNumberOfCameras();
            if (num < 1) {
                Log.w(TAG, "open camera failed,camera num:" + num);
                return;
            }
            mCamera = Camera.open(cameraId);
            if (mCamera == null) {
                Log.w(TAG, "open camera failed.");
                return;
            }
            parameters = mCamera.getParameters();
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            cameraSizes = parameters.getSupportedPreviewSizes();
            Collections.sort(cameraSizes, new Comparator<Camera.Size>() {
                @Override
                public int compare(Camera.Size o1, Camera.Size o2) {
                    return o2.width * o2.height - o1.width * o1.height;
                }
            });
            parameters.setPreviewFormat(ImageFormat.NV21);
            //此SurfaceTexture作为相机预览输出
            mCamera.setPreviewTexture(surfaceTexture);
            setParameters(parameters);
            isOpen = true;
        } catch (Exception e) {
            Log.e(TAG, "set camera exception:" + e.getMessage());
            e.printStackTrace();
        }


    }

    public void setParameters(Camera.Parameters parameters) {
        if (mCamera != null) {
            mCamera.setParameters(parameters);
        }
    }

    public void onStartPreview() {
        if (isPreview) {
            return;
        }
        if (!isOpen) {
            open(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        mCamera.startPreview();
        isPreview = true;
        autoFocus();
    }

    public void onStartPreview(int cameraId) {
        if (isPreview) {
            return;
        }
        if (!isOpen) {
            open(cameraId);
        }
        mCamera.startPreview();
        isPreview = true;
        autoFocus();
    }

    public void onStartPreview(SurfaceHolder holder, SurfaceTexture texture, Camera.PreviewCallback callback) {
        try {
            if (isPreview) {
                return;
            }
            if (!isOpen) {
                open(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
            parameters.setPreviewFormat(ImageFormat.NV21);
            mCamera.setPreviewCallback(callback);
            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewTexture(texture);
            mCamera.startPreview();
            isPreview = true;
            autoFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onStartPreview(SurfaceHolder holder, SurfaceTexture texture) {
        try {
            if (isPreview) {
                return;
            }
            if (!isOpen) {
                open(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
            parameters.setPreviewFormat(ImageFormat.NV21);
            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewTexture(texture);
            mCamera.startPreview();
            isPreview = true;
            //mCamera.getParameters().getSupportedVideoSizes();
            //mCamera.getParameters().getPreferredPreviewSizeForVideo();
            autoFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onStartPreview(SurfaceTexture texture) {
        try {
            if (isPreview) {
                return;
            }
            if (!isOpen) {
                open(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
            parameters.setPreviewFormat(ImageFormat.NV21);
            mCamera.setPreviewTexture(texture);
            mCamera.startPreview();
            isPreview = true;
            autoFocus();

            //mCamera.getParameters().setRecordingHint(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCameraDisplayRotation(int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = App.getCurrentActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
//            result = (info.orientation + degrees) % 360;
//            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;

        }
        Log.i(TAG, "rotate degress:" + result);
        return result;
    }

    public void setPreviewSize(Camera.Size size) {
        if (parameters != null) {
            parameters.setPreviewSize(size.width, size.height);
            //setParameters(parameters);
            currentSize = size;
        }
    }

    public Camera.Parameters getParameters() {
        if (parameters != null) {
            return parameters;
        }
        return null;
    }

    public List<Camera.Size> getPreviewSize() {
        return cameraSizes;
    }

    public boolean isSupportedPreviewSize(Camera.Size size) {
        if (cameraSizes == null || cameraSizes.size() < 1) {
            return false;
        }
        for (Camera.Size s : cameraSizes) {
            if (s.equals(size)) {
                return true;
            }
        }
        return false;
    }

    public Camera.Size getPreviewSize(Camera.Size preview) {
        if (!isSupportedPreviewSize(preview)) {
            Camera.Size defalut = mCamera.getParameters().getPreferredPreviewSizeForVideo();
            return defalut;
        }
        Camera.Size size = null;
        for (Camera.Size s : cameraSizes) {
            if (s.width * defalutHeight == s.height * defalutWidth && s.width <= defalutWidth) {
                if (size == null) {
                    size = s;
                } else if (size.width < s.width) {
                    size = s;
                }
                if (s.width == preview.width && s.height == preview.height) {
                    return preview;
                }
            }
        }
        return size;
    }

    public void close() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            isPreview = false;
            isOpen = false;
        }
    }

    public Camera getmCamera() {
        return mCamera;
    }

    public int setCameraDisplayOrientation(int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = App.getCurrentActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            //前置摄像头需要镜像,转化后进行设置
            mCamera.setDisplayOrientation((360 - result) % 360);
        } else {
            result = (info.orientation - degrees + 360) % 360;
            //后置摄像头直接进行显示
            mCamera.setDisplayOrientation(result);
        }
        Log.i(TAG, "rotate degress:" + result);
        autoFocus();
        return result;
    }

    private boolean isFocusing = false;

    public void autoFocus() {
        try {
            if (mCamera != null && isOpen && isPreview && !isFocusing) {
                mCamera.cancelAutoFocus();
                isFocusing = true;
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        isFocusing = false;
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "snoppa camera autofocus exceptiom:" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopPreview() {
        if (mCamera != null && isOpen && isPreview) {
            mCamera.stopPreview();
            isPreview = false;
        }
    }

    public void onCapture() {
        captureListner.onCaptureStart();
        mCamera.setPreviewCallback(onCaptureListener);
    }

    private boolean isCapturing = false;

    private Camera.PreviewCallback onCaptureListener = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            takePicture(data, currentSize.width, currentSize.height, null);
        }
    };

    private boolean isRecording = false;

    private Camera.PreviewCallback onRecordListner = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Log.i(TAG, "on video record listner");
        }
    };

    public void onRecord() {
        try {
            //mCamera.unlock();
            recordListner.onRecordStart();
            encodUtil = new EncodeUtil();
            String videoName = PICTURE_PATH + generateVideoName();
            encodUtil.init(currentSize.width, currentSize.height, 30, videoName, null);
            //mCamera.setPreviewCallback(onRecordListner);
            encodUtil.onEncodeStart();
            isRecording = true;
        } catch (Exception e) {
            Log.e(TAG, "on video record exception:" + e.getMessage());
            e.printStackTrace();
        }
    }


    public Surface getEncodeSurface() {
        if (encodUtil != null && encodUtil.initStatus) {
            return encodUtil.getEncodeSurface();
        }
        return null;
    }

    public void stopRecord() {
        //mCamera.lock();
        recordListner.onRecordStop();
        encodUtil.onEncodeStop();
        //mCamera.setPreviewCallback(null);
        encodUtil.destroy();
        encodUtil = null;
        isRecording = false;
    }


    public void takePicture(byte[] data, int width, int height, int[] strides) {
        if (isCapturing) {
            return;
        }
        isCapturing = true;
        String path = PICTURE_PATH + generatePictureName();
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(path));
            YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, strides);
            yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, outputStream);
        } catch (Exception e) {
            Log.e(TAG, "take picture exception:" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }

            } catch (Exception e) {
                Log.e(TAG, "IO Exception:" + e.getMessage());
                e.printStackTrace();
            }
            isCapturing = false;
            stopCapture();
        }
    }

    public void stopCapture() {
        if (mCamera != null && !isCapturing) {
            mCamera.setPreviewCallback(null);
        }
        captureListner.onCaptureComplete();
    }


    private SimpleDateFormat sdf = null;

    private String generatePictureName() {
        if (sdf == null) {
            sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        }
        String name = "snoppa_" + sdf.format(new Date()) + ".jpg";
        return name;
    }

    private String generateVideoName() {
        if (sdf == null) {
            sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        }
        String name = "snoppa_" + sdf.format(new Date()) + ".mp4";
        return name;
    }


    public abstract static class CaptureListner {
        public abstract void onCaptureStart();

        public abstract void onCaptureComplete();
    }

    public abstract static class RecordListner {
        public abstract void onRecordStart();

        public abstract void onRecordStop();
    }
}
