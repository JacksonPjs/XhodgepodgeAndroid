package ui.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.x.xhodgepodgeandroid.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import ui.adapter.MyRecycleItemAdapter;
import ui.adapter.StaggerHorizntalRecycleAdapter;
import ui.widget.DividerGridItemDecoration;

/**
 * Created by Administrator on 2017/3/6.
 */

public class StaggeredGridRecycleActivity extends BaseActivity {
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Override
    public int getContentViewId() {
        return R.layout.activity_recycler;
    }

    @Override
    public void initViews() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 102; i++) {
            list.add("item" + i);
        }
        StaggerHorizntalRecycleAdapter adapter = new StaggerHorizntalRecycleAdapter(this, list);
        //设置布局管理器
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.HORIZONTAL));
        //添加分割线
        recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        //设置Adapter
        recyclerView.setAdapter(adapter);
    }
}
