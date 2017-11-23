package ui.activity;

import com.x.xhodgepodgeandroid.R;

import butterknife.Bind;
import ui.widget.ProgressView;

/**
 * Created by Administrator on 2017/4/28.
 */

public class ProgressActivity extends BaseActivity {
    @Bind(R.id.progressView)
    ProgressView progressView;
    @Override
    public int getContentViewId() {
        return R.layout.activity_progress;
    }

    @Override
    public void initViews() {
        progressView.setProgress(50);

    }
}
