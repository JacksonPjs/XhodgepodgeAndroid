package ui.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.SeekBar;

import com.x.xhodgepodgeandroid.R;

import butterknife.Bind;
import ui.widget.BullsView;

/**
 * Created by yingc on 2017/12/18 0018.
 */

public class RoundActivity extends BaseActivity {
    @Bind(R.id.round)
    BullsView round;
    @Bind(R.id.seek)
    SeekBar seekBar;
//    @Bind(R.id.tipseekbar)
//    SeekBar tipseekbar;

    @Override
    public int getContentViewId() {
        return R.layout.activity_round;
    }

    @Override
    public void initViews() {
        round.setRadius(20);
        BullsView bullsView = new BullsView(this);
        Drawable drawable = new BitmapDrawable(convertViewToBitmap(bullsView));
        seekBar.setThumb(drawable);
//        tipseekbar.set
    }

    public static Bitmap convertViewToBitmap(View view) {
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }
}
