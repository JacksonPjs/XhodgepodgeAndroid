package ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.pvj.xlibrary.log.Logger;
import com.x.xhodgepodgeandroid.R;

import utils.PermissionsManager;

/**
 * Created by yingc on 2017/12/29 0029.
 */

public class WelcomeActivity extends BaseActivity{
    private final static String TAG = "WelcomeActivity";
    private final int REQUEST_VIDEO_PERMISSION = 1;
    private final int MSG_START_MAIN = 1;
    boolean isTrue=false;

    @Override
    public int getContentViewId() {
        return R.layout.activity_welcome;
    }

    @Override
    public void initViews() {
            String[] permissions = PermissionsManager.haveNoPermissions(this, PERMISSIONS);
            if (permissions == null || permissions.length < 1) {
                startMain();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null && permissions.length > 0) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_VIDEO_PERMISSION);
            }
    }

    private void startMain(){
        //startActivity(new Intent(WelcomeActivity.this,SnoppaActivity.class));
        mHandler.sendEmptyMessageDelayed(MSG_START_MAIN,1000);
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_START_MAIN:
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    finish();
                    break;
            }
        }
    };

    private final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.EXPAND_STATUS_BAR,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean inquiry = true;
        boolean isGrant = true;
        if (grantResults != null && grantResults.length > 0) {
            //有权限获取失败
            for (int i = 0; i < grantResults.length; i++) {
                //是否为设置了【不再询问】
                if (grantResults[i] == -1) {
                    isGrant = false;
                }
                inquiry = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                Log.i(TAG, "onRequestPermissionsResult:" + "permissions=" + permissions[i] + ",grantResults=" + grantResults[i] + ",show=" + inquiry);
            }

            if (!isGrant) {
                Logger.i(TAG, "权限获取异常");
//                showErrDialog(this);
//                return;
            }
        }
        Log.i(TAG, "权限获取正常");
            startMain();
    }
}
