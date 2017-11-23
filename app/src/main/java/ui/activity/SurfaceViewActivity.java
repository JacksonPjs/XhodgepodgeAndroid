package ui.activity;

import android.util.Log;
import android.widget.TextView;

import com.x.xhodgepodgeandroid.R;

import butterknife.Bind;
import ui.widget.RockerView;
import ui.widget.rudderSurfaceView;

/**
 * Created by yingc on 2017/11/20 0020.
 */

public class SurfaceViewActivity extends BaseActivity {
    @Bind(R.id.rudder)
    RockerView rudd;
    @Bind(R.id.showtv)
    TextView showtv;

    @Override
    public int getContentViewId() {
        return R.layout.activity_surfaceview;
    }

    @Override
    public void initViews() {
       rudd.setListener(new RockerView.RockerListener() {
           @Override
           public void callback(int eventType, int currentAngle, float currentDistance) {
               switch (eventType) {
                   case RockerView.EVENT_ACTION:
                       showtv.setText(currentAngle+"distance=="+currentDistance);
                       // 触摸事件回调
                       Log.e("EVENT_ACTION-------->", "angle="+currentAngle+" - distance"+currentDistance);
                       break;
//                            case RockerView.EVENT_CLOCK:
//                                // 定时回调
//                                Log.e("EVENT_CLOCK", "angle="+currentAngle+" - distance"+currentDistance);
//                                break;
               }
           }
       });


    }
}
