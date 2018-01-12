package ui.widget.camera.view.utils;

import android.media.MediaRecorder;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 *渲染编码线程
 * Created by panbin on 2017/12/10.
 */

public class TextureEncoder implements Runnable {
    private static final String TAG="TextureEncoder";
    private FilterEnum.FilterE fe;
    private GPUImageFilter mFilter;
    private int mWidth = 1920;//defualt
    private int mHeight = 1080;//defualt
    private volatile int rotation;
    private int mTextureId;
    //private EGLContext mContext;
    private EglCore mEglCore;
    private WindowSurface encodeSurface;
    private CameraEncoderCore encodeCore;
    private static final int MSG_START_RECORDING = 0;
    private static final int MSG_STOP_RECORDING = 1;
    //public static final int MSG_START_MUXER = 2;
    private static final int MSG_FRAME_AVAILABLE = 3;
    private static final int MSG_SET_TEXTURE_ID = 4;
    private static final int MSG_UPDATE_SHARED_CONTEXT = 6;
    private static final int MSG_UPDATE_FILTER = 7;
    private static final int MSG_QUIT = 8;
    private static final int MSG_UPDATE_ROTATION=9;
    public static final int MSG_VIDEO_TRACK_READY=10;
    public static final int MSG_AUDIO_TRACK_READY=11;
    private volatile EncoderHandler encodeHandler;
    private static volatile TextureEncoder instance;

    private final Object mReadyFence = new Object();
    private boolean mReady;
    private boolean mRunning;


    public static void initialize(){
        if (instance == null) {
            synchronized (TextureEncoder.class){
                if (instance == null) {
                    instance = new TextureEncoder();
                }
            }
        }
    }

    public static TextureEncoder getInstance(){
        return instance;
    }


    @Override
    public void run() {
        Looper.prepare();
        synchronized (mReadyFence) {
            encodeHandler = new EncoderHandler(this);
            mReady = true;
            mReadyFence.notify();
        }
        Looper.loop();
        Log.w(TAG, "Encoder thread exiting");
        synchronized (mReadyFence) {
            mReady = mRunning = false;
            encodeHandler = null;
        }
    }

    public void startRecording(EncodeConfig cfg){
        Log.d(TAG, "Encoder: startRecording()");
        synchronized (mReadyFence) {
            if (mRunning) {
                Log.w(TAG, "Encoder thread already running");
                return;
            }
            mRunning = true;
            new Thread(this, "TextureEncoder").start();
            while (!mReady) {
                try {
                    mReadyFence.wait();
                } catch (InterruptedException ie) {
                    // ignore
                }
            }
        }
        encodeHandler.sendMessage(encodeHandler.obtainMessage(MSG_START_RECORDING,cfg));
    }

    public boolean isRecording() {
        synchronized (mReadyFence) {
            return mRunning;
        }
    }

    public void stopRecording(){
        //encodeHandler.sendEmptyMessage(MSG_STOP_RECORDING);
        encodeHandler.sendMessage(encodeHandler.obtainMessage(MSG_STOP_RECORDING));
        encodeHandler.sendMessage(encodeHandler.obtainMessage(MSG_QUIT));
        //encodeHandler.sendEmptyMessage(MSG_QUIT);
    }

    public void setTextureId(int textureId){
        synchronized (mReadyFence) {
            if (!mReady) {
                return;
            }
        }
        encodeHandler.sendMessage(encodeHandler.obtainMessage(MSG_SET_TEXTURE_ID,textureId,0,null));
    }

    public void updateShareEglContext(EGLContext context){
        encodeHandler.sendMessage(encodeHandler.obtainMessage(MSG_UPDATE_SHARED_CONTEXT,context));
    }

    public void frameAvailable(long timestampUs){
        synchronized (mReadyFence) {
            if (!mReady) {
                return;
            }
        }
        if (timestampUs == 0) {
            Log.w(TAG, "HEY: got SurfaceTexture with timestamp of zero");
            return;
        }
        encodeHandler.sendMessage(encodeHandler.obtainMessage(MSG_FRAME_AVAILABLE,(int) (timestampUs >> 32),(int) timestampUs));
        //Log.w(TAG,"send available frame message reslut:"+flag);
    }

    public void initFilter(FilterEnum.FilterE type){
        this.fe=type;
    }

    public void updateFilter(FilterEnum.FilterE type){
        synchronized (mReadyFence) {
            if (!mReady) {
                return;
            }
        }
        encodeHandler.sendMessage(encodeHandler.obtainMessage(MSG_UPDATE_FILTER,type));
//        if (fe != type) {
//            encodeHandler.sendMessage(encodeHandler.obtainMessage(MSG_UPDATE_FILTER,type));
//        }
    }

    public void setRotation(int rotation){
        this.rotation=rotation;
//        if (isRecording()) {
//            encodeHandler.sendMessage(encodeHandler.obtainMessage(MSG_UPDATE_ROTATION,rotation));
//        }
    }

    private void prepareEncoder(EGLContext shareContext, int width, int height, int frameRate, int frameInterval, int biteRate, File outputFile){
        try {
            mWidth=width;
            mHeight=height;
            EncodeConfig cfg=new EncodeConfig(outputFile,mWidth,mHeight,biteRate,frameRate,frameInterval, MediaRecorder.AudioSource.MIC,44100);
            encodeCore=new CameraEncoderCore(cfg);
            //setup egl
            mEglCore=new EglCore(shareContext,EglCore.FLAG_RECORDABLE);
            encodeSurface=new WindowSurface(mEglCore,encodeCore.getEncodeSurface(),true);
            encodeSurface.makeCurrent();
        }catch (Exception e){
            throw new RuntimeException("prepare encoder failed");
        }

    }

    private void handleSetmTextureId(int textureId){
        this.mTextureId=textureId;
    }
    private void handleUpdateFilter(FilterEnum.FilterE type,Integer rotation){
        try {
            if (type == fe) {
                Log.w(TAG,"filter not chaned:"+type+" "+fe);
                return;
            }
            final GPUImageFilter nFilter=FilterEnum.getFilter(type);
            final GPUImageFilter oldFilter=mFilter;
            mFilter=nFilter;
            if (oldFilter != null) {
                oldFilter.destroy();
            }
            mFilter.init();
            GLES20.glUseProgram(mFilter.getProgram());
            mFilter.onOutputSizeChanged(mWidth,mHeight);

            if (rotation != null) {
                mFilter.onChangedRotation(rotation);
            } else {
                mFilter.onChangedRotation(this.rotation);
            }
            this.fe=type;
        }catch (Exception e){
            Log.e(TAG,"update filter exception:"+e.getMessage());
            e.printStackTrace();
        }
    }



    private void handleUpdateShareContext(EGLContext eglContext){
        try {
            encodeSurface.releaseEglSurface();
            mFilter.destroy();
            mFilter=null;
            mEglCore.release();
            mEglCore=new EglCore(eglContext,EglCore.FLAG_RECORDABLE);
            encodeSurface.recreate(mEglCore);
            encodeSurface.makeCurrent();
            handleUpdateFilter(fe,null);
        }catch (Exception e){
            Log.e(TAG,"update egl context exception:"+e.getMessage());
            e.printStackTrace();
        }
    }



    private void handleStartRecording(EncodeConfig cfg){
        prepareEncoder(cfg.mEglContext,cfg.mWidth,cfg.mHeight,cfg.mFrameRate,cfg.mFrameInterval,cfg.mBitRate,cfg.mOutputFile);
        encodeCore.startAudioRecord();
        if (fe != null && mFilter == null) {
            mFilter=FilterEnum.getFilter(fe);
            mFilter.setRotation(rotation);
            mFilter.init();
            GLES20.glUseProgram(mFilter.getProgram());
            mFilter.onOutputSizeChanged(mWidth,mHeight);
        }
    }

    private void handleUpdateRotation(int rotation){
        if (mFilter != null && mFilter.isInitialized()) {
            mFilter.onChangedRotation(rotation);
        }
    }

    private void handleFrameAvaliable(long timestampUs)throws Exception {
        //Log.i(TAG,"handle frame");
        mFilter.onDraw(mTextureId);
        encodeCore.drawFrame(false);
        encodeSurface.setPresentationTime(timestampUs);
        encodeSurface.swapBuffers();
    }



    private void handleStopRecording()throws Exception {
        encodeCore.drawFrame(true);
        encodeCore.stopAudioRecord();
    }



    public static class EncoderHandler extends Handler {
        private WeakReference<TextureEncoder> textureEncoder;
        public EncoderHandler(TextureEncoder encoder){
            textureEncoder=new WeakReference<TextureEncoder>(encoder);
        }

        @Override
        public void handleMessage(Message msg) {
            TextureEncoder encoder=textureEncoder.get();
            int what=msg.what;
            Object obj=msg.obj;
            Log.i(TAG,"recive msg:"+msg.toString());
            switch (what) {
                case MSG_FRAME_AVAILABLE:
                    long timestampUs=(((long) msg.arg1) << 32) | (((long) msg.arg2) & 0xffffffffL);
                    try {
                        encoder.handleFrameAvaliable(timestampUs);
                    } catch (Exception e) {
                        Log.e(TAG,"on frame availbale"+e.getMessage());
                        e.printStackTrace();
                    }

                    break;
                case MSG_START_RECORDING:
                    EncodeConfig cfg=(EncodeConfig)obj;
                    encoder.handleStartRecording(cfg);
                    break;
                case MSG_SET_TEXTURE_ID:
                    encoder.handleSetmTextureId(msg.arg1);
                    break;
                case MSG_UPDATE_FILTER:
                    FilterEnum.FilterE type=(FilterEnum.FilterE)obj;
                    encoder.handleUpdateFilter(type,null);
                    break;
                case MSG_UPDATE_SHARED_CONTEXT:
                    EGLContext context=(EGLContext)obj;
                    encoder.handleUpdateShareContext(context);
                    break;
                case MSG_STOP_RECORDING:
                    try {
                        encoder.handleStopRecording();
                    }catch (Exception e){
                        Log.e(TAG,"handle stop recording exception:"+e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case MSG_UPDATE_ROTATION:
                    int rotation=(int)obj;
                    encoder.handleUpdateRotation(rotation);
                    break;
                case MSG_QUIT:
                    Looper looper = Looper.myLooper();
                    if (looper != null) {
                        looper.quit();
                    }
                    break;
            }
        }
    }


}
