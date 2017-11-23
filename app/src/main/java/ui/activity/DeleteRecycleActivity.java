package ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.x.xhodgepodgeandroid.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import ui.adapter.CustomAdapter;
import ui.adapter.RecycleItemTouchHelper;

/**
 * Created by Administrator on 2017/3/6.
 */

public class DeleteRecycleActivity extends BaseActivity {
    @Bind(R.id.recyclerView)
    RecyclerView recycleview;

    private CustomAdapter adapter;

    @Override
    public int getContentViewId() {
        return R.layout.activity_recycler;
    }

    @Override
    public void initViews() {
        recycleview.setLayoutManager(new LinearLayoutManager(this));
        List<String> strings=new ArrayList<>();
        for (int i=0;i<10;i++){
            strings.add("测试"+i);
        }
        adapter=new CustomAdapter(this,strings);
        recycleview.setAdapter(adapter);
        ItemTouchHelper.Callback callback=new RecycleItemTouchHelper(adapter);
        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recycleview);


    }
}
