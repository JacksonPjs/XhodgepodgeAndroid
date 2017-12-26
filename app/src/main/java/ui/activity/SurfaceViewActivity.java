package ui.activity;

import android.util.Log;
import android.widget.TextView;

import com.x.xhodgepodgeandroid.R;

import java.math.BigInteger;

import butterknife.Bind;
import ui.widget.RockerView;
import ui.widget.rudderSurfaceView;
import utils.DataTypeUtil;

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
                            case RockerView.EVENT_CLOCK:

                                 int i= (int) (1*2000);
                                byte[] bytes=DataTypeUtil.intToByteArray(i,4);
                                String s= DataTypeUtil.byteArrayToHexStr(bytes);

//                                int o= (int) Long.parseLong(""+(i&0xff),16);
                                int h1=i&0x00ff;

                                byte[] byteh=DataTypeUtil.intToByteArray(h1,4);
                                String d1= DataTypeUtil.byteArrayToHexStr(byteh);
                                int l=(i>>8)&0xff;
                                byte[] byteq=DataTypeUtil.intToByteArray(i,4);
                                String d= DataTypeUtil.byteArrayToHexStr(byteq);

                                String b1=numToHex8(i);
                                String b2=numToHex16(i);
                                String b3=numToHex32(i);

                                // 定时回调
                                Log.e("EVENT_CLOCK", "angle="+currentAngle+" - distance"+currentDistance);
                                break;
               }
           }
       });


    }

    //使用1字节就可以表示b
    public static String numToHex8(int b) {
        return String.format("%02x", b);//2表示需要两个16进行数
    }
    //需要使用2字节表示b
    public static String numToHex16(int b) {
        return String.format("%04x", b);
    }
    //需要使用4字节表示b
    public static String numToHex32(int b) {
        return String.format("%08x", b);
    }

   public int convertBytesToInt(byte[] b)
    {
        if(b == null || b.length != 4)
            return 0;

        int n0 = b[3];
        n0= (n0&0xff)<<24;
        int n1 = b[2];
        n1= (n1&0xff)<<16;
        int n2 = b[1];
        n2= (n2&0xff)<<8;
        int n3 = b[0];
        n3= (n3&0xff)<<24;

        return n0|n1|n2|n3;
    }
}
