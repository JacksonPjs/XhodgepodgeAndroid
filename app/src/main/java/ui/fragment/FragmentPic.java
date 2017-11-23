package ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.x.xhodgepodgeandroid.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ui.activity.DetailsActivity;
import ui.activity.DiscountActivity;
import ui.activity.GalleryRecycleActivity;
import ui.activity.ProgressActivity;
import ui.activity.RecyclerViewActivity;
import ui.activity.SurfaceViewActivity;
import ui.activity.TextDialogActivity;
import ui.activity.TextProgressBarActivity;
import ui.activity.TextSocketClient;
import ui.activity.ViewPagerActivity;
import ui.widget.GalleryRecyclerView;

/**
 * Created by Administrator on 2017/3/2.
 */

public class FragmentPic extends Fragment {
    @Bind(R.id.progressBar)
    Button progressBar;
    @Bind(R.id.dialog)
    Button dialog;
    @Bind(R.id.recyclerView)
    Button recyclerView;
    @Bind(R.id.socket)
    Button socket;
    @Bind(R.id.surface)
    Button surface;

    Context context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
        Log.e("TAG'", "onAttach: ");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pic,
                null);
        ButterKnife.bind(this, rootView);


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        progressBar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(context, TextProgressBarActivity.class);
//                startActivity(intent);
//            }
//        });
//        dialog.setOnClickListener(this);

    }

    @OnClick({R.id.progressBar, R.id.dialog, R.id.recyclerView, R.id.discount, R.id.details, R.id.socket,
    R.id.surface})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.progressBar:
//                Toast.makeText(context,"t",Toast.LENGTH_SHORT).show();
                intent = new Intent(context, TextProgressBarActivity.class);
                startActivity(intent);
                break;
            case R.id.dialog:
                intent = new Intent(context, TextDialogActivity.class);
                startActivity(intent);
                break;
            case R.id.recyclerView:
                intent = new Intent(context, RecyclerViewActivity.class);
                startActivity(intent);
                break;
            case R.id.discount:
                intent = new Intent(context, DiscountActivity.class);
                startActivity(intent);
                break;
            case R.id.details:
                intent = new Intent(context, DetailsActivity.class);
                startActivity(intent);
                break;
            case R.id.socket:
                intent = new Intent(context, ViewPagerActivity.class);
                startActivity(intent);
                break;
            case R.id.progress:
                intent = new Intent(context, ProgressActivity.class);
                startActivity(intent);
                break;
                case R.id.surface:
                intent = new Intent(context, SurfaceViewActivity.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
