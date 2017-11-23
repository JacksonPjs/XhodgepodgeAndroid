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
 * Created by Administrator on 2017/3/3.
 */

public class MyRecycleItemAdapter  extends RecyclerView.Adapter<MyRecycleItemAdapter.MyViewHolder> {

    private List<String> mDatas;
    private Context mContext;
    private LayoutInflater inflater;
    private OnItemClickLitener mOnItemClickListener = null;
    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickListener = mOnItemClickLitener;
    }


    public MyRecycleItemAdapter(Context context, List<String> datas){
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
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.tv.setText( mDatas.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    //注意这里使用getTag方法获取数据
                    mOnItemClickListener.onItemClick(v,position);
                }
            }
        });
    }

    //重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.recycle_item,parent, false);
        MyViewHolder holder= new MyViewHolder(view);
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public MyViewHolder(View view) {
            super(view);
            tv=(TextView) view.findViewById(R.id. recycle_item_text);
        }

    }
    public  interface OnItemClickLitener {
        void onItemClick(View view , int position);
    }

}