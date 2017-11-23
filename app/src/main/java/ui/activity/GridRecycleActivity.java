package ui.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.x.xhodgepodgeandroid.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import ui.adapter.MyRecycleItemAdapter;
import ui.widget.DividerGridItemDecoration;

/**
 * Created by Administrator on 2017/3/6.
 */

public class GridRecycleActivity extends BaseActivity {
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Override
    public int getContentViewId() {
        return R.layout.activity_recycler;
    }

    @Override
    public void initViews() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 40; i++) {
            list.add("item" + i);
        }
        MyRecycleItemAdapter adapter = new MyRecycleItemAdapter(this, list);
        //设置布局管理器
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.HORIZONTAL));
//        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        //添加分割线
        recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        //设置Adapter
        recyclerView.setAdapter(adapter);
    }
}
