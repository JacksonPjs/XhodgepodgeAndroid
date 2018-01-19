package ui.activity;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.x.xhodgepodgeandroid.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.App;
import butterknife.Bind;
import butterknife.OnClick;
import ui.widget.camera.SnoppaCamera;
import ui.widget.camera.view.CameraView;
import ui.widget.camera.view.utils.EncodeConfig;
import ui.widget.camera.view.utils.FilterEnum;
import ui.widget.camera.view.utils.GPUImageFilter;
import ui.widget.camera.view.utils.GrayScaleFilter;
import ui.widget.camera.view.utils.LoopTask;
import utils.AppUtils;
import utils.FileUtil;

/**
 * Created by jackson on 2017/12/29 0029.
 */

public class CameraActivity extends BaseActivity {
    private static final String TAG="CameraActivity";
    @Bind(R.id.btn_take_picture)
    ImageView take_picture;
    @Bind(R.id.camera_view)
    CameraView cameraView;
    @Bind(R.id.btn_camera)
    ImageView btn_camera;
    @Bind(R.id.btn_taking_picture)
    RelativeLayout taking_picture;
    @Bind(R.id.time)
    TextView timeTextView;


    private int btn_camera_click=0;
    private SimpleDateFormat sdf=null;
    private static final String BASE_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+"/xhodgepodge/camera/";

    @Override
    public int getContentViewId() {
        return R.layout.activity_camera;
    }

    @Override
    public void initViews() {
        App.setCurrentActivity(this);
        try {
            FileUtil.createFiles(new File(BASE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        SnoppaCamera.getInstance().setCaptureListner(captureListner);
        SnoppaCamera.getInstance().setRecordListner(recordListner);
        hideBottomUIMenu();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SnoppaCamera.getInstance().close();
//        cameraView.destroyDrawingCache();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SnoppaCamera.getInstance().stopPreview();
    }
    @OnClick({R.id.btn_take_picture,R.id.btn_camera,R.id.btn_taking_picture})
    public void onClick(View view) {
        int degress = SnoppaCamera.getInstance().getCameraDisplayRotation(Camera.CameraInfo.CAMERA_FACING_BACK);
        switch (view.getId()) {
            case R.id.btn_take_picture:
                Log.i(TAG,"on take picture button click");
                //SnoppaCamera.getInstance().onRecord();
                try {
                    File f=new File(generateFileName());
                    cameraView.startRecording(new EncodeConfig(f,2160,3840,2160*3840*10,30,2, MediaRecorder.AudioSource.MIC,44100));
                    take_picture.setVisibility(View.GONE);
                    taking_picture.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Log.e(TAG,"start record video exception:"+e.getMessage());
                    e.printStackTrace();
                }
                startRecordTime();
                break;
            case R.id.btn_camera:
                Log.i(TAG,"on camera button click");
                if (btn_camera_click % 2 == 0) {
                    SnoppaCamera.getInstance().onSwitchCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
                    GrayScaleFilter gfilter=new GrayScaleFilter();
                    gfilter.setRotation(degress);
                    cameraView.setFilter(FilterEnum.FilterE.GRAY);
                } else {
                    SnoppaCamera.getInstance().onSwitchCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    GPUImageFilter nfilter=new GPUImageFilter();
                    nfilter.setRotation(degress);
                    cameraView.setFilter(FilterEnum.FilterE.NOFILTER);
                }
                btn_camera_click++;
                break;
            case R.id.btn_taking_picture:
                Log.i(TAG,"taking picture button click");
                stopRecordTime();

                try {
                    cameraView.stopRecording();
                    take_picture.setVisibility(View.VISIBLE);
                    taking_picture.setVisibility(View.GONE);
                } catch (Exception e) {
                    Log.e(TAG,"stop record video exception:"+e.getMessage());
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

    }

    private SnoppaCamera.CaptureListner captureListner = new SnoppaCamera.CaptureListner() {
        @Override
        public void onCaptureComplete() {
            take_picture.setVisibility(View.VISIBLE);
            taking_picture.setVisibility(View.GONE);
        }

        @Override
        public void onCaptureStart() {
            take_picture.setVisibility(View.GONE);
            taking_picture.setVisibility(View.VISIBLE);
        }
    };
    private SnoppaCamera.RecordListner recordListner = new SnoppaCamera.RecordListner() {
        @Override
        public void onRecordStart() {
            take_picture.setVisibility(View.GONE);
            taking_picture.setVisibility(View.VISIBLE);
        }

        @Override
        public void onRecordStop() {
            take_picture.setVisibility(View.VISIBLE);
            taking_picture.setVisibility(View.GONE);
        }
    };

    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
    private String generateFileName(){
        if (sdf == null) {
            sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        }
        String s=sdf.format(new Date());
            return BASE_PATH+"snoppa-"+s+".mp4";
    }


    private final int MSG_UPDATA_TIME = 1;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATA_TIME:
                    String time = AppUtils.getModifyTime("HH:mm:ss", recordTime);
//                    Logger.i(TAG, "MSG_UPDATA_TIME=" + recordTime + ",time=" + time);
                    timeTextView.setText(time);
                    break;
            }
        }
    };


    private LoopTask recordTimeTask = null;
    private long recordTime = 0;
    public void startRecordTime() {
        recordTimeTask = new LoopTask(new LoopTask.Task() {
            long time;

            @Override
            public void onRun() {
                time = System.currentTimeMillis();
            }

            @Override
            public void onLoop() {
                recordTime = System.currentTimeMillis() - time;
                mHandler.sendEmptyMessage(MSG_UPDATA_TIME);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).onStart();
    }

    public void stopRecordTime() {
        if (recordTimeTask != null) {
            recordTimeTask.onStop();
        }
    }
}
