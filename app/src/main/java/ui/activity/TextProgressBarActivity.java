package ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.x.xhodgepodgeandroid.R;

import java.math.BigInteger;

import butterknife.Bind;
import butterknife.OnClick;
import ui.widget.GuideView;
import utils.DataTypeUtil;

/**
 * Created by Administrator on 2017/3/2.
 */

public class TextProgressBarActivity extends BaseActivity{
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.progress)
    TextView textView;
    @Bind(R.id.start)
    Button start;

    GuideView guideView;

    private Handler handler = new Handler();


    @Override
    public int getContentViewId() {
        return R.layout.activity_progressbar;
    }

    @Override
    public void initViews() {
        int carIndex=257;
        byte[] result = new byte[4];
//        result[0] = (byte) ((carIndex >> 24) & 0xFF);
//        result[1] = (byte) ((carIndex >> 16) & 0xFF);
//        result[2] = (byte) ((carIndex >> 8) & 0xFF);
//        result[3] = (byte) (carIndex & 0xFF);
        result[0] = (byte) (carIndex & 0xFF);
        result[1] = (byte) ((carIndex >> 8) & 0xFF);
        result[2] = (byte) ((carIndex >> 16) & 0xFF);
        result[3] = (byte) ((carIndex >> 24) & 0xFF);
//        result[0] = (byte)((carIndex >> 24) & 0xFF);
//        result[1] = (byte)((carIndex >> 16) & 0xFF);
//        result[2] = (byte)((carIndex >> 8) & 0xFF);
        result[0] = 00;
        result[1] = 00;
        result[2] = 00;
        result[3] = 00;
//        result[3] = (byte)(carIndex & 0xFF);
        progressBar.setProgress(0);
    Log.e("0位", result[0]+"");
    Log.e("1位", result[1]+"");
    Log.e("2位", result[2]+"");
    Log.e("3位", result[3]+"");
    Log.e("result", DataTypeUtil.byteArrayToHexStr(result)+"");
        Log.e("2进制",binary(result,2));
        Log.e("16进制",binary(result,16));
        Log.e("32进制",binary(result,32));
        Log.e("10进制",binary(result,10));
        // 使用图片
        final ImageView iv = new ImageView(this);
        iv.setImageResource(R.mipmap.delete);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        iv.setLayoutParams(params);
        // 使用文字
        TextView tv = new TextView(this);
        tv.setText("欢迎使用");
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setTextSize(30);
        tv.setGravity(Gravity.CENTER);

        guideView =new  GuideView.Builder(this)
                .setTargetView(start)//设置目标
                .setCustomGuideView(iv)
                .setDirction(GuideView.Direction.LEFT_BOTTOM)
//                .setShape(GuideView.MyShape.CIRCULAR)   // 设置圆形显示区域，
                .setBgColor(getResources().getColor(R.color.shadow))
                .setDrawRec()
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        guideView.hide();
//                        guideView2.show();
                    }
                })
                .build();

        guideView.show();
    }

    /**
     * 将byte[]转为各种进制的字符串
     * @param bytes byte[]
     * @param radix 基数可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    public static String binary(byte[] bytes, int radix){
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }

    int pro=0;
    Runnable runnable=new Runnable(){
        public void run() {
            progressBar.setVisibility(View.VISIBLE);
            pro=progressBar.getProgress()+10;
            progressBar.setProgress(pro);
            textView.setText(pro+"%");
            //如果进度小于100,，则延迟1000毫秒后重复执行runnable
            if(pro<100){
                handler.postDelayed(runnable, 1000);

            }else{
//                progressBar.setVisibility(View.GONE);
//                startActivity(new Intent(TextProgressBarActivity.this, TextDialogActivity.class));
                handler.removeCallbacks(runnable);
                progressBar.setProgress(0);
            }
        }
    };
    @OnClick({R.id.stop,R.id.start})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.start:
                handler.post(runnable); //开始执行
                break;
            case R.id.stop:
                handler.removeCallbacks(runnable);//停止执行
                progressBar.setProgress(0);
                break;
        }
    }
}
