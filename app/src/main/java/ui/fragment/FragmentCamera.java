package ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.x.xhodgepodgeandroid.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ui.activity.CameraActivity;
import ui.widget.camera.SnoppaCamera;

/**
 * Created by yingc on 2017/12/29 0029.
 */

public class FragmentCamera extends Fragment {
    @Bind(R.id.camera)
    Button camera;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_camera,
                null);
        ButterKnife.bind(this, rootView);
        initData();

        return rootView;
    }
    public void initData(){

    }
    @OnClick({R.id.camera})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.camera:
                startActivity(new Intent(getActivity(), CameraActivity.class));
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
