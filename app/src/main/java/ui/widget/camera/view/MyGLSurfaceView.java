package ui.widget.camera.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Scroller;



import java.util.List;

import ui.widget.camera.SnoppaCamera;
import ui.widget.camera.view.utils.CameraHandlerListner;
import ui.widget.camera.view.utils.CameraRender;
import ui.widget.camera.view.utils.FilterEnum;
import ui.widget.camera.view.utils.GPUImageFilter;


/**
 * Created by panbin on 2017/11/2.
 * 定制glsurfaceview 监听view左右滑动的事件做到动态切换滤镜
 */

public class MyGLSurfaceView extends GLSurfaceView implements SurfaceTexture.OnFrameAvailableListener,CameraHandlerListner{

    private static final String TAG="MGLSV";

    private int offset;
    //当前滑动方向 1=right -1=left 0=current
    private int derection;
    //是否停止滑动
    private boolean locked;

    private int downX;

    private int currentX;

    private Scroller scroller;

    private boolean needSwitch;



    //private int mOutputWidth;
    //private int mOutputHeight;


    private CameraHandler cameraHandler;
    private HandlerThread handlerThread;
    private CameraRender cameraRender;




    public MyGLSurfaceView(Context context) {
        super(context);
        if (scroller == null){
            scroller=new Scroller(context);
        }
        init();
    }

    public MyGLSurfaceView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        if (scroller == null){
            scroller=new Scroller(context);
        }
        init();

    }

    private void init(){
        setEGLContextClientVersion(2);
        handlerThread=new HandlerThread("GlSurfaceView");
        handlerThread.start();
        cameraHandler=new CameraHandler(handlerThread.getLooper(),this);
        cameraRender=new CameraRender(cameraHandler);
        setRenderer(cameraRender);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        int degress = SnoppaCamera.getInstance().getCameraDisplayRotation(Camera.CameraInfo.CAMERA_FACING_BACK);
        cameraRender.onRotationChanged(degress);
    }





    /**
     * 重写 屏幕触摸事件监听左右滑动屏幕的时候 实时的切换滤镜
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        Log.i(TAG,"on my gl surface view touch event.....");
        if (locked) {
            return true;
        }
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX=(int)event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (downX == -1) {
                    return true;
                }
                currentX=(int)event.getX();
                if (currentX > downX) {
                    derection=1;
                }else{
                    derection=-1;
                }
                offset= Math.abs(currentX - downX);
                break;
            case MotionEvent.ACTION_UP:
                if (downX == -1) {
                    return true;
                }
                if (offset == 0) {
                    return true;
                }
                locked = true;
                downX = -1;
                if (offset > this.getMeasuredWidth() / 3) {
                    scroller.startScroll(offset, 0, this.getMeasuredWidth() - offset, 0, 100 * (1 - offset / this.getMeasuredWidth()));
                    needSwitch = true;
                } else {
                    scroller.startScroll(offset, 0, -offset, 0, 100 * (offset / this.getMeasuredWidth()));
                    needSwitch = false;
                }
                break;
        }

        if (SnoppaCamera.getInstance().isPreview) {
            SnoppaCamera.getInstance().autoFocus();
        }
        Log.i(TAG,"on touch event: downX="+downX+" offset="+offset+" currentX="+currentX+" derection="+derection+" locked="+locked);
        return true;
    }

    public int getOffset() {
        return offset;
    }

    public int getDerection() {
        return derection;
    }

    public boolean isLocked() {
        return locked;
    }

    public int getDownX() {
        return downX;
    }

    public int getCurrentX() {
        return currentX;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setDerection(int derection) {
        this.derection = derection;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setDownX(int downX) {
        this.downX = downX;
    }

    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    public Scroller getScroller(){
        return scroller;
    }
    public void setScroller(Scroller scroller){
        this.scroller=scroller;
    }

    public boolean isNeedSwitch(){
        return needSwitch;
    }

    public void setNeedSwitch(boolean needSwitch){
        this.needSwitch=needSwitch;
    }




    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
//        Log.i(TAG,"on surfacetexture frame available");
        requestRender();
    }



    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG,"on rotation changed");
        cameraHandler.sendEmptyMessage(CameraHandler.ROTATION_CHANGED);
    }


    public CameraRender getCameraRender(){
        return cameraRender;
    }

    public void setFilter(GPUImageFilter filter){
        cameraRender.setFilter(filter);
    }

    public void setFilter(FilterEnum.FilterE type){
        cameraRender.setFilter(type);
    }
    public static class CameraHandler extends Handler {
        public static final int SETUP_CAMERA=1001;
        public static final int CONFIG_CAMERA=1002;
        public static final int START_PREIVEW=1003;
        public static final int ROTATION_CHANGED=1004;

        private CameraHandlerListner listner;
        public CameraHandler(Looper looper, CameraHandlerListner listner){
            super(looper);
            this.listner=listner;
        }
        @Override
        public void handleMessage(Message msg) {
            listner.handleMessage(msg);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case CameraHandler.SETUP_CAMERA: {
                final int width = msg.arg1;
                final int height = msg.arg2;
                final SurfaceTexture surfaceTexture = (SurfaceTexture) msg.obj;
                surfaceTexture.setOnFrameAvailableListener(this);
                cameraHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        SnoppaCamera.getInstance().onSetup(SnoppaCamera.getInstance().getCameraId(), surfaceTexture);
                        cameraHandler.sendMessage(cameraHandler.obtainMessage(CameraHandler.CONFIG_CAMERA, width, height));
                    }
                });
            }
            break;
            case CameraHandler.CONFIG_CAMERA:{
                final int width=msg.arg1;
                final int height=msg.arg2;
                List<Camera.Size> cameraSizes=SnoppaCamera.getInstance().getPreviewSize();
                List<Camera.Size> videoSize=SnoppaCamera.getInstance().getParameters().getSupportedVideoSizes();
                Camera.Size currentSize=SnoppaCamera.getInstance().getPreviewSize(SnoppaCamera.getInstance().getmCamera().new Size(width,height));
                Camera.Size preferSize=SnoppaCamera.getInstance().getParameters().getPreferredPreviewSizeForVideo();
                cameraRender.setSize(cameraSizes,currentSize,currentSize);
                SnoppaCamera.getInstance().setPreviewSize(preferSize);
                SnoppaCamera.getInstance().setParameters(SnoppaCamera.getInstance().getParameters());
                cameraHandler.sendEmptyMessage(CameraHandler.START_PREIVEW);
            }
            break;
            case CameraHandler.START_PREIVEW:{
                cameraHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        SnoppaCamera.getInstance().onStartPreview();
                    }
                });
            }
            break;
            case CameraHandler.ROTATION_CHANGED:{
                cameraHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        int degress = SnoppaCamera.getInstance().getCameraDisplayRotation(SnoppaCamera.getInstance().getCameraId());
                        cameraRender.onRotationChanged(degress);
                    }
                });
            }
            break;
            default:
                break;
        }
    }
}
