package ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.pvj.xlibrary.utils.Toast;
import com.x.xhodgepodgeandroid.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import ui.adapter.MyRecycleItemAdapter;
import ui.widget.DividerGridItemDecoration;
import ui.widget.DividerItemDecoration;

/**
 * Created by Administrator on 2017/3/6.
 */

public class LinearRecycleActivy extends BaseActivity {
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //添加分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        //设置Adapter
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickLitener(new MyRecycleItemAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(LinearRecycleActivy.this, "请在该区域之外点击"+position, 0).show();
//                if (itemClickListener!=null){
//                    itemClickListener.onItemclicck("点击"+position);
//                    dismiss();
//                }
            }
        });
    }
}
