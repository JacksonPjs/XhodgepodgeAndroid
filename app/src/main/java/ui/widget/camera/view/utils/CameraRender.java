package ui.widget.camera.view.utils;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;


import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ui.widget.camera.view.MyGLSurfaceView;

/**
 * camera view render
 * Created by panbin on 2017/12/11.
 */

public class CameraRender implements GLSurfaceView.Renderer{
    private TextureEncoder encoder;
    private GPUImageFilter mFilter;
    private FilterEnum.FilterE fe;
    private final Queue<Runnable> mRunOnDraw;
    private int textureId;
    private SurfaceTexture surfaceTexture;
    private int mOutputWidth;
    private int mOutputHeight;
    private Camera.Size currentSize;
    private List<Camera.Size> cameraSizes;
    private Camera.Size defalutSize;
    private MyGLSurfaceView.CameraHandler cameraHandler;
    private boolean recordingEnabled;
    private EncodeConfig encodeConfig;
    private static final int RECORD_OFF=0;
    private static final int RECORD_ON=1;
    private static final int RECORD_RESUME=2;
    private int encodingStatus;
    private int rotation;


    public CameraRender(MyGLSurfaceView.CameraHandler handler){
        this.cameraHandler=handler;
        fe= FilterEnum.FilterE.NOFILTER;
        TextureEncoder.initialize();
        encoder=TextureEncoder.getInstance();
        mRunOnDraw = new LinkedList<Runnable>();
    }

    public void setSize(List<Camera.Size> cameraSizes, Camera.Size currentSize, Camera.Size defaultSize){
        this.cameraSizes=cameraSizes;
        this.currentSize=currentSize;
        this.defalutSize=defaultSize;
    }

    public void setEncodeConfig(EncodeConfig cfg){
        this.encodeConfig=cfg;
    }

    public void setRecordingEnabled(boolean recordingEnabled){
        this.recordingEnabled=recordingEnabled;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //texture 创建时间需要在 surface create以后
        textureId=OpenGLUtils.createTextureObj();
        surfaceTexture=new SurfaceTexture(textureId);
        mFilter = new GPUImageFilter();
        mFilter.onInit();
        recordingEnabled=encoder.isRecording();
        if (recordingEnabled) {
            encodingStatus=RECORD_RESUME;
        } else {
            encodingStatus=RECORD_OFF;
            encoder.initFilter(fe);
        }
    }



    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (gl != null) {
            gl.glViewport(0, 0, width, height);
        }
        mOutputWidth=width;
        mOutputHeight=height;
        //encoder.setRotation(rotation);
        GLES20.glUseProgram(mFilter.getProgram());
        mFilter.onOutputSizeChanged(width,height);
        cameraHandler.sendMessage(cameraHandler.obtainMessage(MyGLSurfaceView.CameraHandler.SETUP_CAMERA,width,height,surfaceTexture));
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 在Surface上绘制的时候调用

        // 清除屏幕
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        surfaceTexture.updateTexImage();
        runAll(mRunOnDraw);
        mFilter.onDraw(textureId);
        onCameraDrawFrame(textureId,surfaceTexture.getTimestamp());
    }

    private void onCameraDrawFrame(int textureId,long timestampUs){
        if (recordingEnabled && encodeConfig != null) {
            switch (encodingStatus) {
                case RECORD_OFF:
                    encodeConfig.updateEglContext(EGL14.eglGetCurrentContext());
                    encoder.startRecording(encodeConfig);
                    encoder.setRotation(rotation);
                    encoder.setTextureId(textureId);
                    encodingStatus=RECORD_ON;
                    break;
                case RECORD_RESUME:
                    encoder.updateShareEglContext(EGL14.eglGetCurrentContext());
                    encoder.setTextureId(textureId);
                    encoder.setRotation(rotation);
                    encodingStatus=RECORD_ON;
                    break;
                case RECORD_ON:
                    break;
                default:
                    throw new RuntimeException("unknow status:"+encodingStatus);
            }
        } else {
            switch (encodingStatus) {
                case RECORD_RESUME:
                case RECORD_ON:
                    encoder.stopRecording();
                    encodingStatus=RECORD_OFF;
                    break;
                case RECORD_OFF:
                    break;
                default:
                    throw new RuntimeException("unknow status:"+encodingStatus);
            }
        }
        encoder.updateFilter(fe);
        encoder.frameAvailable(timestampUs);
    }


    protected void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.add(runnable);
        }
    }

    private void runAll(Queue<Runnable> queue) {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                queue.poll().run();
            }
        }
    }


    public void onRotationChanged(final int degress){
        rotation = degress;
        if (encoder != null ) {
            encoder.setRotation(rotation);
        }
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                mFilter.onChangedRotation(degress);
            }
        });
    }


    public void setFilter(final FilterEnum.FilterE type){
        this.fe=type;
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                final GPUImageFilter oldFilter = mFilter;
                mFilter = FilterEnum.getFilter(type);
                if (oldFilter != null) {
                    oldFilter.destroy();
                }
                mFilter.setRotation(rotation);
                mFilter.init();
                GLES20.glUseProgram(mFilter.getProgram());
                mFilter.onOutputSizeChanged(mOutputWidth, mOutputHeight);
                if (encoder != null) {
                    encoder.updateFilter(type);
                }
            }
        });
    }
    public void setFilter(final GPUImageFilter newfilter) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                final GPUImageFilter oldFilter = mFilter;
                mFilter = newfilter;
                if (oldFilter != null) {
                    oldFilter.destroy();
                }
                mFilter.init();
                GLES20.glUseProgram(mFilter.getProgram());
                mFilter.onOutputSizeChanged(mOutputWidth, mOutputHeight);
            }
        });
    }
}
