package ui.model;

import android.graphics.Point;

/**
 * Created by yingc on 2017/11/30 0030.
 */

public class Rocker {
   public  float x;
   public float y;
    public Rocker() {}

    public Rocker(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Rocker(Rocker src) {
        this.x = src.x;
        this.y = src.y;
    }

    /**
     * Set the point's x and y coordinates
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }


}
