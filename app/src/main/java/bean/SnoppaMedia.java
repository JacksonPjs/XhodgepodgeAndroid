package bean;



/**
 * author: jk
 * created on: 2016/11/8 16:29
 * description:snoppa 媒体 类
 */
public class SnoppaMedia extends BaseModel {
    public SnoppaMedia() {
    }

    public SnoppaMedia(String url) {
        this.isFavorite = 0;
        this.url = url;
    }

    public SnoppaMedia(String url, boolean isMedia) {
        this.isMedia = isMedia;
        this.isFavorite = 0;
        this.url = url;
    }


    public SnoppaMedia(int isFavorite, String url) {
        this.isFavorite = isFavorite;
        this.url = url;
    }

    public int isFavorite; //是否为精选 1为精选，0为没有精选,默认为0；
    public int isExist;//是否存在 1为存在，0为不存在，默认为1；
    public String url;//媒体地址

    public float width = 720;
    public float height = 1280;
    public boolean selected = false;   //收藏选择时使用
    public boolean isMedia = false;//是否是Media
    public String frameRate;            //分辨率
    public String dpi;                  //帧率
    public String timeLong = "60hz";             //时长
    public String date;                 //日期

    @Override
    public String toString() {
        return "SnoppaMedia{" +
                "isFavorite=" + isFavorite +
                ", isExist=" + isExist +
                ", url='" + url + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", selected=" + selected +
                ", isMedia=" + isMedia +
                ", frameRate='" + frameRate + '\'' +
                ", dpi='" + dpi + '\'' +
                ", timeLong='" + timeLong + '\'' +
                ", date='" + date + '\'' +
                "} " + super.toString();
    }
}
