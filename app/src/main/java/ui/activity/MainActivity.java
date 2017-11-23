package ui.activity;



import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.x.xhodgepodgeandroid.R;

import butterknife.Bind;
import ui.fragment.FragmentPic;
import utils.NavigationViewUtil;


public class MainActivity extends BaseActivity {
   @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar)
    AppBarLayout appbar;
    @Bind(R.id.navigation_view)
    NavigationView navigation_view;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;


    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("APP");
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerToggle.syncState();
        NavigationViewUtil.setDrawerLeftEdgeSize(this,mDrawerLayout,0.5f);
//        setDrawerRightEdgeSize(this,mDrawerLayout,0.5f);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        navigation_view.setItemIconTintList(null);
        navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        selectNavigation(0);
                        break;
                    case R.id.navigation_cal:
                        selectNavigation(1);
                        break;
                    case R.id.navigation_music:
                        selectNavigation(2);
                        break;
                    case R.id.navigation_setting:
                        selectNavigation(3);
                        break;
                    case R.id.navigation_greendao:
                        selectNavigation(4);
                        break;
                    case R.id.navigation_recycleview:
                        selectNavigation(5);
                        break;
                    case R.id.navigation_about:
                        selectNavigation(6);
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        selectNavigation(0);
    }

    public void selectNavigation(int position){
        Fragment fragment=null;
        switch (position){
            case 0:
                fragment=new FragmentPic();
//                fragment=new UpLoadFileFragment();
                break;
            case 1:
//                fragment=new TestRxJavaFragment();
                break;
            case 2:
//                fragment=new RetrofitFragment();
                break;
            case 3:
//                fragment=new BaiduLocationFragment();
                break;
            case 4:
//                fragment=new GreenDaoFragment();
                break;
            case 5:
//                fragment=new RecycleViewFragment();
                break;
            case 6:
                Intent intent=new Intent(this,AboutActivity.class);
                startActivity(intent);
                break;


        }
        if (fragment!=null){
            FragmentManager fragmentManager=getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_content,fragment).commit();
            setTitle("图片");
        }
    }

//    @OnClick(R.id.btn)
//    public void onClick(View view){
//        switch (view.getId()) {
//            case R.id.btn:
//                Toast.makeText(this,"onclick",Toast.LENGTH_LONG).show();
//            break;
//        }
//    }
}
