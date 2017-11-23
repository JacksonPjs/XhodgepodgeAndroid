package ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.x.xhodgepodgeandroid.R;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/2/27.
 */

public class AboutActivity extends BaseActivity {
    @Bind(R.id.about)
    Button about;
    @Override
    public int getContentViewId() {
        return R.layout.activity_about;
    }

    @Override
    public void initViews() {

    }
    @OnClick(R.id.about)
    public void onClick(View view){
        switch (view.getId()){
            case R.id.about:
                Intent intent=new Intent(this,TextDialogActivity.class);
                startActivity(intent);
                break;
        }
    }
}
