package ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.pvj.xlibrary.log.Logger;
import com.x.xhodgepodgeandroid.R;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import bean.ImageBDInfo;
import bean.SnoppaMedia;
import ui.activity.MediaCodecActivity;
import utils.FileUtil;
import utils.ImageUtils;


/**
 * Created by xuxiaopeng  on 17/4/18.
 */
public class GridViewAdapter extends BaseAdapter {

    private String TAG = "GridViewAdapter";
    private Context context;
    private String mDirPath;//图片所在的文件夹的路径
    private ArrayList<SnoppaMedia> data;
    public int index = -1;
    public boolean ones = false;
    private ImageBDInfo bdInfo;

    public GridViewAdapter(Context context, ArrayList<SnoppaMedia> data) {
        this.context = context;
        this.mDirPath = FileUtil.getCameraFile().toString();
        this.data = data;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }


    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.grid_view, null);
            holder.gridimage = (ImageView) convertView.findViewById(R.id.gridimage);
            holder.item_select = (CheckBox) convertView.findViewById(R.id.item_select);
            holder.time_tv = (TextView) convertView.findViewById(R.id.time_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.gridimage.setOnClickListener(new ImageOnclick(position, holder.gridimage));
        holder.gridimage.setOnLongClickListener(new ImageViewLongClickListenter(position, holder.item_select));
        holder.item_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    data.get(position).selected = true;
                } else {
                    data.get(position).selected = false;
                }
            }
        });

        if (selecting) {
            holder.item_select.setVisibility(View.VISIBLE);
        } else {
            holder.item_select.setVisibility(View.GONE);
        }
        if (index == position && ones) {
            ScaleAnimation animation = new ScaleAnimation(0.8f,1.0f, 0.8f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);//变大
            animation.setDuration(350);//设置动画持续时间
            animation.setRepeatCount(0);//设置重复次数
            animation.setFillAfter(false);//动画执行完后是否停留在执行完的状态
            animation.setStartOffset(100);//执行前的等待时间
            convertView.setAnimation(animation);
            animation.start();
            final View finalConvertView = convertView;
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ScaleAnimation animation1 = new ScaleAnimation(1.0f,0.8f,1.0f,0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);//变小
                    animation1.setDuration(350);//设置动画持续时间
                    animation1.setRepeatCount(0);//设置重复次数
                    animation1.setFillAfter(false);//动画执行完后是否停留在执行完的状态
                    animation1.setStartOffset(0);//执行前的等待时间
                    finalConvertView.setAnimation(animation1);
                    animation1.start();
                    animation1.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            ScaleAnimation animation2 = new ScaleAnimation(0.8f,1.0f, 0.8f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);//变大
                            animation2.setDuration(350);//设置动画持续时间
                            animation2.setRepeatCount(0);//设置重复次数
                            animation2.setFillAfter(false);//动画执行完后是否停留在执行完的状态
                            animation2.setStartOffset(0);//执行前的等待时间
                            finalConvertView.setAnimation(animation2);
                            animation2.start();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            ones = false;
        }
        holder.gridimage.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_launcher));
        final File file = new File(mDirPath + "/" + data.get(position).url);
        if (data.get(position).url.endsWith(".mp4")) {
            holder.time_tv.setVisibility(View.VISIBLE);
        } else {
            holder.time_tv.setVisibility(View.GONE);
        }
        Logger.d(TAG, "file.exists() = " + file.exists() + "  =    " + file.toString());
        if (!file.exists()) {
            //如果图片不存在，则不显示，并将数据库数据删除
            long l = MediaCodecActivity.instance.deleteFileByUrl(data.get(position).url);
            notifyDataSetInvalidated();
            Logger.d(TAG, "删除 不存在 的文件 = " + l);
        } else {
            Logger.d(TAG, "mediaDataList.get(position).url = " + data.get(position).url);
            ImageUtils.setImage(mDirPath + "/" + data.get(position).url, holder.gridimage, context);
            holder.time_tv.setText(data.get(position).timeLong);
        }

        if (data.get(position).selected) {
            holder.item_select.setChecked(true);
        } else {
            holder.item_select.setChecked(false);
        }
        return convertView;
    }

    public class ViewHolder {
        ImageView gridimage;
        CheckBox item_select;
        TextView time_tv;
    }

    private class ImageOnclick implements View.OnClickListener {

        private int index;
        private ImageView imageView;

        public ImageOnclick(int index, ImageView imageView) {
            this.index = index;
            this.imageView = imageView;
        }

        @Override
        public void onClick(View v) {
            if (selecting) {
//                clickFromAdapter.itemClick(index);
            } else {
                MediaCodecActivity activity = (MediaCodecActivity) context;
                View c = activity.gridView.getChildAt(0);
                int top = c.getTop();
                int firstVisiblePosition = activity.gridView.getFirstVisiblePosition() / 3;

                int a, b;
                a = index / 3;
                b = index % 3;
                Log.e("1", "高==" + top + "=" + firstVisiblePosition + "b=" + b);
//                bdInfo.width = (activity.Width - 3 * dip2px(2)) / 3;
//                bdInfo.height = bdInfo.width;
//                bdInfo.x = dip2px(1) + b * bdInfo.width + b * dip2px(2);
//                bdInfo.y = dip2px(1) + bdInfo.height * (a - firstVisiblePosition) + top + (a - firstVisiblePosition) * dip2px(2) + activity.gridView.getTop() - dip2px(1);
//                Intent intent = new Intent(context, PreviewsActivity.class);
//                intent.putExtra("data", (Serializable) data);
//                intent.putExtra("bdinfo", bdInfo);
//                intent.putExtra("index", index);
//                intent.putExtra("type", 2);
//                ((MediaCodecActivity) context).startActivityForResult(intent,2);
            }
        }
    }

    public ArrayList<SnoppaMedia> favoirteList;    //保存选中的数据
    /**
     * 决定GridVIew是否可以多选状态
     */
    public boolean selecting = false;

    private class ImageViewLongClickListenter implements View.OnLongClickListener {
        private int index;
        private CheckBox checkbox;

        public ImageViewLongClickListenter(int index, CheckBox checkbox) {
            this.index = index;
            this.checkbox = checkbox;
        }

        @Override
        public boolean onLongClick(View v) {
            favoirteList = new ArrayList<>();
//            if (fromAdapter == null) {
//                return true;
//            }
//            fromAdapter.itemLongClick();
            selecting = true;
            notifyDataSetChanged();
            favoirteList.add(data.get(index));
            checkbox.setChecked(true);
            data.get(index).selected = true;
            return true;
        }
    }

//    private FileListActivity.ClickFromAdapter clickFromAdapter;
//    private FileListActivity.LongClickFromAdapter fromAdapter;
//
//    public void setLongClickFromAdapter(FileListActivity.LongClickFromAdapter fromAdapter) {
//        this.fromAdapter = fromAdapter;
//    }
//
//    public void setClickFromAdapter(FileListActivity.ClickFromAdapter clickFromAdapter) {
//        this.clickFromAdapter = clickFromAdapter;
//    }

    public int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
