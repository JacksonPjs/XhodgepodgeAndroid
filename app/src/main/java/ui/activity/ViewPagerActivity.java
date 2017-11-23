package ui.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.pvj.xlibrary.banner.Banner;
import com.x.xhodgepodgeandroid.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by yingc on 2017/11/21 0021.
 */

public class ViewPagerActivity extends BaseActivity{
    @Bind(R.id.viewpager)
    Banner banner;

    @Override
    public int getContentViewId() {
        return R.layout.activity_viewpager;
    }

    @Override
    public void initViews() {
//        banner.addContent(getImageView(R.mipmap.a));
//        banner.addContent(getImageView(R.mipmap.b));
////        banner.startScroll();
//        banner.stopScroll();
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        ViewGroup.LayoutParams params = banner.getLayoutParams();
        params.width=width;
        params.height=width*7/10;
        banner.setLayoutParams(params);
        List imageViewList=new ArrayList<>();
        imageViewList.add(this.getResources().getDrawable(R.mipmap.a));
        imageViewList.add(this.getResources().getDrawable(R.mipmap.b));
        imageViewList.add(this.getResources().getDrawable(R.mipmap.c));
        banner.setBannerDataInit(new Banner.BannerDataInit() {
            @Override
            public ImageView initImageView() {
                return (ImageView) LayoutInflater.from(ViewPagerActivity.this).inflate(R.layout.imageview, null);
            }

            @Override
            public void initImgData(ImageView imageView, Object imgPath) {
                imageView.setImageDrawable((Drawable) imgPath);
            }
        });
        banner.setDataSource(imageViewList);
    }

    private ImageView getImageView(int resId) {
        ImageView image = new ImageView(this);
        image.setImageResource(resId);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(100, 100);
        image.setLayoutParams(params);
        return image;
    }
}
