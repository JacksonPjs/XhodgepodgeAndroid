package ui.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;

import com.x.xhodgepodgeandroid.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import ui.adapter.CustomAdapter;
import ui.adapter.RecycleItemTouchHelper;

/**
 * Created by Administrator on 2017/3/1.
 */

public class RecyclerViewActivity extends BaseActivity {
    @Bind(R.id.delete)
    Button delete;

    @Bind(R.id.gallery)
    Button gallery;
    @Override
    public int getContentViewId() {
        return R.layout.activity_recycle;
    }

    @Override
    public void initViews() {

    }

    @OnClick({R.id.delete,R.id.gallery,R.id.line,R.id.grid,R.id.stagger})
    public void onClick(View view){
        Intent intent;
        switch (view.getId()){

            case R.id.delete:
                intent=new Intent(this,DeleteRecycleActivity.class);
                startActivity(intent);
                break;
            case R.id.gallery:
                intent=new Intent(this,GalleryRecycleActivity.class);
                startActivity(intent);
                break; case R.id.line:
                intent=new Intent(this,LinearRecycleActivy.class);
                startActivity(intent);
                break; case R.id.grid:
                intent=new Intent(this,GridRecycleActivity.class);
                startActivity(intent);
                break; case R.id.stagger:
                intent=new Intent(this,StaggeredGridRecycleActivity.class);
                startActivity(intent);
                break;
        }
    }
}
