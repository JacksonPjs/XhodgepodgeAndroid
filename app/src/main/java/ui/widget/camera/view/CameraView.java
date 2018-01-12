package ui.widget.camera.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.x.xhodgepodgeandroid.R;

import ui.widget.camera.view.utils.CameraRender;
import ui.widget.camera.view.utils.EncodeConfig;
import ui.widget.camera.view.utils.FilterEnum;
import ui.widget.camera.view.utils.GPUImageFilter;


/**
 * Created by panbin on 2017/11/30.
 */

public class CameraView extends FrameLayout {

    private MyGLSurfaceView glSurfaceView;

    public CameraView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context){
        View view= View.inflate(context, R.layout.layout_camera,this);
        glSurfaceView=(MyGLSurfaceView)view.findViewById(R.id.camera_suf);
    }


    public CameraRender getCameraRender(){
        return glSurfaceView.getCameraRender();
    }

    public void setFilter(GPUImageFilter filter){
        glSurfaceView.setFilter(filter);
    }

    public void setFilter(FilterEnum.FilterE type){
        glSurfaceView.setFilter(type);
    }
    public void startRecording(final EncodeConfig cfg){
        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                CameraRender render=glSurfaceView.getCameraRender();
                render.setEncodeConfig(cfg);
            }
        });
        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                glSurfaceView.getCameraRender().setRecordingEnabled(true);
            }
        });
    }

    public void stopRecording(){
        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                glSurfaceView.getCameraRender().setRecordingEnabled(false);
            }
        });
    }

}
