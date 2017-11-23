package ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.x.xhodgepodgeandroid.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.OnClick;
import ui.adapter.MyRecycleItemAdapter;
import ui.widget.DividerGridItemDecoration;
import ui.widget.DividerItemDecoration;
import ui.widget.MyDialog;

/**
 * Created by Administrator on 2017/3/2.
 */

public class TextDialogActivity extends BaseActivity {
    @Bind(R.id.btnPopup)
    Button btnPopup;
    @Bind(R.id.btnBack)
    Button btnBack;

    @Bind(R.id.btn)
    Button btn;

    @Bind(R.id.edit)
    EditText editText;
    @Bind(R.id.ll_popupLayout)
    LinearLayout ll_Popup;




    // 加载动画
    Animation animation1;
    Animation animation2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animation1 = AnimationUtils.loadAnimation(this,
                R.anim.score_business_query_enter);
        animation2 = AnimationUtils.loadAnimation(this,
                R.anim.score_business_query_exit);


    }


    @Override
    public int getContentViewId() {
        return R.layout.dialog_pay;
    }

    @Override
    public void initViews() {

    }

    @OnClick({R.id.btn, R.id.btnBack, R.id.btnPopup})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                AlertDialog alertDialog = new AlertDialog.Builder(TextDialogActivity.this).create();
                alertDialog.show();
                Window window = alertDialog.getWindow();
                window.setContentView(R.layout.dialog_main_info);
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < 10; i++) {
                    list.add("item" + i);
                }
                RecyclerView recyclerView = (RecyclerView) window.findViewById(R.id.recyclerView);
                MyRecycleItemAdapter adapter = new MyRecycleItemAdapter(TextDialogActivity.this, list);
                LinearLayoutManager layoutManager = new LinearLayoutManager(TextDialogActivity.this);
                //设置Adapter
                recyclerView.setAdapter(adapter);
                //设置布局管理器
                recyclerView.setLayoutManager(layoutManager);
                //设置为垂直布局，这也是默认的
                layoutManager.setOrientation(OrientationHelper.VERTICAL);

                break;
            case R.id.btnBack:
                ll_Popup.setVisibility(View.GONE);   // 取出布局
                ll_Popup.startAnimation(animation2); // 开始退出动画
                break;
            case R.id.btnPopup:
                MyDialog myDialog=new MyDialog(this);
                myDialog.show();

//                ll_Popup.setVisibility(View.VISIBLE);   // 显示布局
//                ll_Popup.startAnimation(animation1);    // 开始动画
//                editText.setFocusable(true);
//
//                editText.setFocusableInTouchMode(true);
//
//                editText.requestFocus();
//
//
//                InputMethodManager inputManager =
//
//                        (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//
//                inputManager.showSoftInput(editText, 0);
                break;
        }
    }
}
