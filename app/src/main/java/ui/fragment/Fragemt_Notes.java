package ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.pvj.xlibrary.loadinglayout.LoadingLayout;
import com.pvj.xlibrary.loadinglayout.Utils;
import com.pvj.xlibrary.loadingrecyclerview.LoadMoreRecyclerLoadingLayout;
import com.pvj.xlibrary.log.Logger;
import com.pvj.xlibrary.recyclerview.LoadMoreRecycleView;
import com.x.xhodgepodgeandroid.R;

import java.util.ArrayList;
import java.util.List;

import app.Contacts;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import ui.adapter.MyStringAdapter;
import ui.widget.MyScrollView;

/**
 * Created by Administrator on 2017/3/14.
 */
//投资记录
public class Fragemt_Notes extends Fragment implements LoadingLayout.OnReloadListener, LoadMoreRecyclerLoadingLayout.OnRefreshAndLoadMoreListener, MyScrollView.ScrollListener, LoadMoreRecycleView.RecScrollListener {
//    List<InvestmentBean.DataBean> biaoBeenList;
//    InvestmentAdapter adapter;
//    MyStringAdapter stringAdapter;


    int page = 1;
    int pagesize = 10;

    @Bind(R.id.public_listview)
    LoadMoreRecyclerLoadingLayout publicLv;
    @Bind(R.id.myscrollview)
    MyScrollView scrollView;
    String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        id = bundle.getString("id");

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragemt_notes, null);
        ButterKnife.bind(this, rootView);
        initView();
//        net(0, 0);
        return rootView;
    }

    /**
     * @param stype     是刷新 还是加载  0是刷新  1是加载
     * @param inrefresh 第几次刷新下的加载
     */
    private void net(final int stype, final int inrefresh) {
//        NetWorks.borrowInvestList(id, page + "", pagesize + "", new Subscriber<InvestmentBean>() {
//            @Override
//            public void onCompleted() {
//                publicLv.setRefreshing(false);
//
//                if (stype == 0) {
//                    publicLv.setRefreshing(false);
//                } else {
//                    publicLv.loadMoreComplete();
//                }
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                if (stype == 0) {
//                    publicLv.setRefreshing(false);
//                } else {
//                    publicLv.loadMoreComplete();
//                }
//                if (page == 1) {
//                    publicLv.setStatus(LoadingLayout.Error);
//                } else {
//                    publicLv.setTextEnd();
//                }
//
//                Logger.e(e.toString());
//            }
//
//            @Override
//            public void onNext(InvestmentBean biaoBean) {
//
//                if (stype == 0) {
//                    if (biaoBean.getState().getStatus() == 0) {
//                        biaoBeenList.clear();
//                        biaoBeenList.addAll(biaoBean.getData());
//                        publicLv.setStatus(LoadingLayout.Success);
//                    } else {
//                        publicLv.setStatus(LoadingLayout.Empty);
//                    }
//
//                } else if (stype == 1) {
//                    if (publicLv.getRefreshCount() == inrefresh) {
//
//                        if (biaoBean.getState().getStatus() == 0) {
//                            biaoBeenList.addAll(biaoBean.getData());
//                        } else {
//                            publicLv.setTextEnd();
//                        }
//
//                    }
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//        });

    }

    private void initView() {
        scrollView.setScrollListener(this);
//        adapter = new InvestmentAdapter(biaoBeenList, getActivity());
        List<String> strings=new ArrayList<>();

        MyStringAdapter stringAdapter=new MyStringAdapter(strings,getActivity());

        for (int i=0;i<100;i++){
            publicLv.setStatus(LoadingLayout.Success);
            strings.add("item"+i);
        }


        publicLv.verticalLayoutManager(getContext())
                .setAdapter(stringAdapter)
                .setOnReloadListener(this)
                .setRecycleViewBackgroundColor(Utils.getColor(getActivity(), R.color.font_color1))
                .setOnRefreshAndLoadMoreListener(this)
                .setSrcoll(this);


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onReload(View v) {
//        page = 1;
////        publicLv.setStatus(LoadingLayout.Loading);
////        net(0, 0);
    }

    @Override
    public void onRefresh() {
//        page = 1;
////        net(0, 0);
//        publicLv.setTextStart();
    }

    @Override
    public void onLoadMore(int inrefresh) {
//        page++;
//        net(1, inrefresh);
    }

    @Override
    public void onScrollToBottom() {

    }

    @Override
    public void onScrollToTop() {

    }

    @Override
    public void onScroll(int scrollY) {
        Contacts.APP.isTop = false;
//判断时候滑动到了顶部
        if (scrollY == 0) {
           if (publicLv.getScrollY()==0){
               Contacts.APP.isTop = true;
           }
        }

    }

    @Override
    public void notBottom() {

    }

    @Override
    public void onRecScrollToTop() {
        Contacts.APP.isTop = true;
    }

    @Override
    public void onRecScrollNotTop() {
        Contacts.APP.isTop = false;
    }
}
