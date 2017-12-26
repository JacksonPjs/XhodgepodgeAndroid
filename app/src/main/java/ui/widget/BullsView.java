package ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yingc on 2017/12/18 0018.
 */

public class BullsView extends View {

    private Paint mPaint;

    private Point mCenter;
    private float mRadius;
    private float mArea;

    public BullsView(Context context) {
        this(context, null);
    }

    public BullsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BullsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 创建画笔（支持锯齿）
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        // 创建圆心
        mCenter = new Point();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width, height;
        // 确定内容的理想大小，无约束
        int cWidth = 100;
        int mHeight = 100;

        width = getHowToGetWH(widthMeasureSpec, cWidth);
        height = getHowToGetWH(heightMeasureSpec, mHeight);

        // 使用测量必须调用该方法
        setMeasuredDimension(width, height);
    }

    /**
     * 测量宽度和高度的方法
     */
    private int getHowToGetWH(int measureSpec, int mSize) {

        int specSize = MeasureSpec.getSize(measureSpec);

        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.AT_MOST:
                return Math.min(specSize, mSize);
            case MeasureSpec.UNSPECIFIED:
                return mSize;
            case MeasureSpec.EXACTLY:
                return specSize;
            default:
                return 0;
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        // 如果有变化，则复位参数
        if (w != oldw || h != oldh) {
            mCenter.x = w / 2;
            mCenter.y = h / 2;
            mArea = Math.min(mCenter.x, mCenter.y);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 绘制同心圆
        mPaint.setColor(Color.RED);
        canvas.drawCircle(mCenter.x, mCenter.y, mArea, mPaint);
        mPaint.setColor(Color.BLUE);
        canvas.drawCircle(mCenter.x, mCenter.y, mRadius, mPaint);
    }

    public void setRadius(int f) {
        mRadius =17.0f;
    }
}

