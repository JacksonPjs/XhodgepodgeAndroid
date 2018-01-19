package bean;

import java.io.Serializable;

/**
 * Created by xuxiaopeng  on 17/4/18.
 */
public class ImageBDInfo implements Serializable {

    public float x;
    public float y;
    public float width;
    public float height;

    @Override
    public String toString() {
        return "ImageBDInfo{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
