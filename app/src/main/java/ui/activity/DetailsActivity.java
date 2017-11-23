package ui.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;


import com.pvj.xlibrary.loadinglayout.Utils;
import com.pvj.xlibrary.log.Logger;
import com.x.xhodgepodgeandroid.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import ui.adapter.ViewPagerFramentAdapter;
import ui.fragment.Fragemt_Notes;
import ui.fragment.FragmentPic;
import ui.widget.MyScrollView;

/**
 * Created by Administrator on 2017/3/10.
 */

public class DetailsActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.day_text)
    TextView dayText;
    @Bind(R.id.month_text)
    TextView monthText;
    @Bind(R.id.season_text)
    TextView seasonText;
    @Bind(R.id.tv_expand)
    TextView tv_expand;
    @Bind(R.id.icon_expan)
    ImageView icon;


    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.progress)
    TextView progress;





//    ;


    String id;
    boolean flag;

    List<Fragment> fragmentList;
    ViewPagerFramentAdapter viewPagerFramentAdapter;
    Handler handler = new Handler();



    @Override
    public int getContentViewId() {
        return R.layout.activity_details;
    }

    @Override
    public void initViews() {
        init();
    }

    private void init() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        fragmentList = new ArrayList<>();

        viewPagerFramentAdapter = new ViewPagerFramentAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(viewPagerFramentAdapter);
        viewPager.setOnPageChangeListener(this);

        FragmentPic fragment_day1 = new FragmentPic();
//        Bundle bundle1 = new Bundle();
//                    bundle1.putSerializable("data", (Serializable) oneBean);
//        bundle1.putString("id", id);
//        fragment_day1.setArguments(bundle1);
//        fragmentList.add(fragment_day1);

//        Fragment_Data fragment_day2 = new Fragment_Data();
//        Bundle bundle2 = new Bundle();
//        bundle2.putString("id", id);
//        fragment_day2.setArguments(bundle2);
//        fragmentList.add(fragment_day2);
//
        Fragemt_Notes fragment_day3 = new Fragemt_Notes();
        Bundle bundle3 = new Bundle();
        bundle3.putString("id", id);
        fragment_day3.setArguments(bundle3);
        fragmentList.add(fragment_day3);

        viewPagerFramentAdapter.notifyDataSetChanged();


        progressBar.setProgress(20);
        progress.setText("20%");
//        if (!(Boolean) SharedPreferencesUtils.getParam(this, "islogin", false)) {
//            topay.setText(getResources().getString(R.string.logintopay));
//            topay.setBackground(null);
//            topay.setTextColor(getResources().getColor(R.color.mouth_btn_bg));
//        } else {
//            topay.setText(getResources().getString(R.string.topay));
//            topay.setBackground(getResources().getDrawable(R.drawable.button_border));
//        }

        net();


    }

    @OnClick({R.id.buy, R.id.gotopay, R.id.icon_expan, R.id.day_text, R.id.season_text,
            R.id.month_text})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {





//            case R.id.icon_expan:
//                if (!flag) {
//                    flag = true;
//                    tv_expand.setEllipsize(null);  //展开
//                    tv_expand.setSingleLine(false);
//                } else {
//                    flag = false;
//                    tv_expand.setEllipsize(TextUtils.TruncateAt.END);  //收缩
//                    tv_expand.setLines(3);
//                }
//                handler.post(new Runnable() {
//                                 @Override
//                                 public void run() {
//                                     scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//                                 }
//                             }
//
//                );
//                break;
            case R.id.day_text:
                viewPager.setCurrentItem(0);
                break;
            case R.id.month_text:
                viewPager.setCurrentItem(1);
                break;
            case R.id.season_text:
                viewPager.setCurrentItem(2);
                break;

        }
    }

    private void net() {
    }

    private void setData(String b) {
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            dayText.setTextColor(Utils.getColor(this, R.color.colorPrimary));
            monthText.setTextColor(Utils.getColor(this, R.color.font_color1));
            seasonText.setTextColor(Utils.getColor(this, R.color.font_color1));


            ObjectAnimator animator = ObjectAnimator.ofFloat(dayText, "rotation", 0, 360, 360);
            animator.setDuration(1000);
            animator.start();
        } else if (position == 1) {
            monthText.setTextColor(Utils.getColor(this, R.color.mouth));
            dayText.setTextColor(Utils.getColor(this, R.color.font_color1));
            seasonText.setTextColor(Utils.getColor(this, R.color.font_color1));

            ObjectAnimator animator = ObjectAnimator.ofFloat(monthText, "rotation", 0, 360, 360);
            animator.setDuration(1000);
            animator.start();
        } else if (position == 2) {
            seasonText.setTextColor(Utils.getColor(this, R.color.season));
            monthText.setTextColor(Utils.getColor(this, R.color.font_color1));
            dayText.setTextColor(Utils.getColor(this, R.color.font_color1));

            ObjectAnimator animator = ObjectAnimator.ofFloat(seasonText, "rotation", 0, 360, 360);
            animator.setDuration(1000);
            animator.start();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }



}
