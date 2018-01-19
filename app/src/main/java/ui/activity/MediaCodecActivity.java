package ui.activity;

import android.os.Handler;
import android.view.View;
import android.widget.GridView;

import com.x.xhodgepodgeandroid.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import DB.SnoppaMediaDBDao;
import bean.SnoppaMedia;
import butterknife.Bind;
import ui.adapter.GridViewAdapter;
import utils.FileUtil;
import utils.ImageUtils;

/**
 * Created by jackson on 2018/1/17 0017.
 */

public class MediaCodecActivity extends BaseActivity {
    private SnoppaMediaDBDao mDBDao;                //数据库
    @Bind(R.id.gridview)
    public GridView gridView;
    ArrayList<SnoppaMedia> snoppaMedias = new ArrayList<>();
    ArrayList<SnoppaMedia> normal_snoppaMedias = new ArrayList<>();
    ArrayList<SnoppaMedia> favorite_snoppaMedias = new ArrayList<>();
    public static MediaCodecActivity instance = null;
    private GridViewAdapter adapter;
    private Handler myHandler = new Handler() {

    };

    @Override
    public int getContentViewId() {
        return R.layout.activity_mediacodec;
    }

    @Override
    public void initViews() {
        instance = this;
        init();
        initData();

    }

    public void init() {
        //存到数据库
        mDBDao = new SnoppaMediaDBDao(this);
        mDBDao.openDataBase();//打开数据库
    }

    public void initData() {
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                //sd卡的MP4，JPG文件名字入库
                makeAdapterList();
                initGridView();
            }
        });
    }


    private void initGridView() {
        //查询数据库
        snoppaMedias = (ArrayList<SnoppaMedia>) mDBDao.queryAll();
        favorite_snoppaMedias.clear();
        normal_snoppaMedias.clear();
        if (snoppaMedias == null) {
            return;
        }
        for (SnoppaMedia media : snoppaMedias) {
            String playDetial = ImageUtils.getPlayDetial(mSnoppaDir.toString() + "/" + media.url, this);
            String[] split = playDetial.split("#");
            media.timeLong = split[0];
            media.dpi = split[1];
            media.date = ImageUtils.url2date(media.url);
            normal_snoppaMedias.add(media);
        }
        adapter = new GridViewAdapter(MediaCodecActivity.this, normal_snoppaMedias);

        gridView.setAdapter(adapter);
    }

    /**
     * 生成 所有的 文件名集合
     *
     * @return
     */
    private File mSnoppaDir;
    private List<String> mFileNames;

    private void makeAdapterList() {
        mSnoppaDir = FileUtil.getCameraFile();
        mFileNames = Arrays.asList(mSnoppaDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".mp4"))
                    return true;
                return false;
            }
        }));

        //将文件名存入数据库
        if (!mFileNames.isEmpty()) {
            if (mDBDao == null) {
                mDBDao = new SnoppaMediaDBDao(this);
                mDBDao.openDataBase();
            }
            for (String fileName : mFileNames) {
                //读取文件的时候，将读取到的数据添加到数据库中,如果已经存在的数据则不会存放到里面
                boolean isHave = mDBDao.queryDataIsHave(fileName);
                if (!isHave) {
                    long l = mDBDao.insertData(new SnoppaMedia(fileName));
                }
            }
        }
    }

    public long deleteFileByUrl(String url) {
        long ll = mDBDao.deleteDataByUrl(url);
        return ll;
    }
}
