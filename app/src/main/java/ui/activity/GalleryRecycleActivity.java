package ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.x.xhodgepodgeandroid.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import ui.adapter.GalleryAdapter;
import ui.widget.GalleryRecyclerView;

/**
 * Created by Administrator on 2017/3/6.
 */

public class GalleryRecycleActivity extends BaseActivity {
    @Bind(R.id.id_content)
    ImageView imageView;

    @Bind(R.id.id_recyclerview_horizontal)
    GalleryRecyclerView recyclerView;


    private GalleryAdapter mAdapter;

    private List<Integer> mDatas;
    @Override
    public int getContentViewId() {
        return R.layout.activity_galleryrecycle;
    }

    @Override
    public void initViews() {
        mDatas = new ArrayList<Integer>(Arrays.asList(R.mipmap.a,
                R.mipmap.b, R.mipmap.c, R.mipmap.d, R.mipmap.e,
                R.mipmap.f, R.mipmap.g, R.mipmap.h, R.mipmap.l));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new GalleryAdapter(this, mDatas);
        recyclerView.setAdapter(mAdapter);

        recyclerView.setOnItemScrollChangeListener(new GalleryRecyclerView.OnItemScrollChangeListener()
        {
            @Override
            public void onChange(View view, int position)
            {
                imageView.setImageResource(mDatas.get(position));
            };
        });

        mAdapter.setOnItemClickLitener(new GalleryAdapter.OnItemClickLitener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
//				Toast.makeText(getApplicationContext(), position + "", Toast.LENGTH_SHORT)
//						.show();
                imageView.setImageResource(mDatas.get(position));
            }
        });

    }

}
