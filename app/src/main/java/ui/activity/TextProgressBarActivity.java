package ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.x.xhodgepodgeandroid.R;

import butterknife.Bind;
import butterknife.OnClick;

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

    private Handler handler = new Handler();


    @Override
    public int getContentViewId() {
        return R.layout.activity_progressbar;
    }

    @Override
    public void initViews() {
        progressBar.setProgress(0);
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
