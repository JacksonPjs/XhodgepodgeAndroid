package ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.x.xhodgepodgeandroid.R;

import java.util.List;

/**
 * Created by Administrator on 2017/3/6.
 */

public class StaggerHorizntalRecycleAdapter  extends RecyclerView.Adapter<StaggerHorizntalRecycleAdapter.StaggerViewHolder> {

    private List<String> mDatas;
    private Context mContext;
    private LayoutInflater inflater;

    public StaggerHorizntalRecycleAdapter(Context context, List<String> datas){
        this. mContext=context;
        this. mDatas=datas;
        inflater= LayoutInflater. from(mContext);
    }

    @Override
    public int getItemCount() {

        return mDatas.size();
    }

    //填充onCreateViewHolder方法返回的holder中的控件
    @Override
    public void onBindViewHolder(StaggerHorizntalRecycleAdapter.StaggerViewHolder holder, final int position) {

        holder.tv.setText( mDatas.get(position));
    }

    //重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    @Override
    public StaggerHorizntalRecycleAdapter.StaggerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.recycle_horizntal_item,parent, false);
        StaggerHorizntalRecycleAdapter.StaggerViewHolder holder= new StaggerHorizntalRecycleAdapter.StaggerViewHolder(view);
        return holder;
    }



    class StaggerViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public StaggerViewHolder(View view) {
            super(view);
            tv=(TextView) view.findViewById(R.id. recycle_item_text);
        }

    }
}
