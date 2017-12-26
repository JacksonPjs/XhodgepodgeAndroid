package ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.pvj.xlibrary.utils.DensityUtils;
import com.x.xhodgepodgeandroid.R;

import utils.MathUtils;

/**
 * Created by yingc on 2017/11/20 0020.
 */

public class RockerView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    //默认半径
    private static int DEFAULT_AREA_RADIUS = 75;
    private static int DEFAULT_ROCKER_RADIUS = 25;
    //默认颜色
    private static int DEFAULT_AREA_COLOR = Color.CYAN;
    private static int DEFAULT_ROCKER_COLOR = Color.RED;

    private static int DEFAULT_REFRESH_CYCLE = 30;//界面刷新周期，默认30毫秒，每30ms就会刷新一次界面
    private static int DEFAULT_CALLBACK_CYCLE = 100;//数据回调周期，默认100毫秒，每100ms就会发送一次界面状态回调

    private SurfaceHolder mHolder;
    private static Thread mDrawThread;
    private static Thread mCallbackThread;
    private static boolean mDrawOk = true;
    private static boolean mCallbackOk = true;

    private Paint mPaint;//画笔

    private Point mAreaPosition;
    private Point mRockerPosition;

    //半径
    private int mAreaRadius = -1;
    private int mRockerRadius = -1;
    //颜色
    private int mAreaColor;
    private int mRockerColor;
    //图片
    private Bitmap mAreaBitmap;
    private Bitmap mRockerBitmap;


    private RockerListener mListener;
    public static final int EVENT_ACTION = 1;
    public static final int EVENT_CLOCK = 2;

    private int mRefreshCycle = DEFAULT_REFRESH_CYCLE;
    private int mCallbackCycle = DEFAULT_CALLBACK_CYCLE;


    /*Life Cycle***********************************************************************************/

    public RockerView(Context context) {
        this(context, null);
    }

    public RockerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RockerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // init attrs
        initAttrs(context, attrs);

        // set paint
        setPaint();

        if (isInEditMode()) {
            return;
        }


        configSurfaceView();


        configSurfaceHolder();
    }

    private void initAttrs(Context context, AttributeSet attrs) {

        DEFAULT_AREA_RADIUS = DensityUtils.dip2px(context, 75);
        DEFAULT_ROCKER_RADIUS = DensityUtils.dip2px(context, 25);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.viewsupport);

        mAreaRadius = ta.getDimensionPixelOffset(R.styleable.viewsupport_area_radius, DEFAULT_AREA_RADIUS);
        mRockerRadius = ta.getDimensionPixelOffset(R.styleable.viewsupport_rocker_radius, DEFAULT_ROCKER_RADIUS);

        mRefreshCycle = ta.getInteger(R.styleable.viewsupport_refresh_cycle, DEFAULT_REFRESH_CYCLE);
        mCallbackCycle = ta.getInteger(R.styleable.viewsupport_callback_cycle, DEFAULT_CALLBACK_CYCLE);

        Drawable area_bg = ta.getDrawable(R.styleable.viewsupport_area_background);
        Drawable rocker_bg = ta.getDrawable(R.styleable.viewsupport_rocker_background);

        if (area_bg instanceof BitmapDrawable) {
            mAreaBitmap = ((BitmapDrawable) area_bg).getBitmap();
        } else if (area_bg instanceof ColorDrawable) {
            mAreaBitmap = null;
            mAreaColor = ((ColorDrawable) area_bg).getColor();
        } else {
            mAreaBitmap = null;
            mAreaColor = DEFAULT_AREA_COLOR;
        }

        if (rocker_bg instanceof BitmapDrawable) {
            mRockerBitmap = ((BitmapDrawable) rocker_bg).getBitmap();
        } else if (rocker_bg instanceof ColorDrawable) {
            mRockerBitmap = null;
            mRockerColor = ((ColorDrawable) rocker_bg).getColor();
        } else {
            mRockerBitmap = null;
            mRockerColor = DEFAULT_ROCKER_COLOR;
        }
        ta.recycle();
    }

    private void setPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    private void configSurfaceView() {
        setKeepScreenOn(true);          // 实现屏幕常亮
        // 使能控件获得焦点，设置为true时，并不是说立刻获得焦点，要想立刻获得焦点，得用requestFocus；
        //  使能获得焦点，就是说具备获得焦点的机会、能力，当有焦点在控件之间移动时，控件就有这个机会、能力得到焦点。
        setFocusable(true);
        setFocusableInTouchMode(true);  // make sure this surfaceView can get focus from touch.
        setZOrderOnTop(true);           //置于显示窗口的最顶层
    }

    private void configSurfaceHolder() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setFormat(PixelFormat.TRANSPARENT); //设置背景透明
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = 0, measureHeight = 0;
        int defaultWidth = (mAreaRadius + mRockerRadius) * 2;
        int defalutHeight = defaultWidth;

        int widthsize = MeasureSpec.getSize(widthMeasureSpec);      //取出宽度的确切数值
        int widthmode = MeasureSpec.getMode(widthMeasureSpec);      //取出宽度的测量模式

        int heightsize = MeasureSpec.getSize(heightMeasureSpec);    //取出高度的确切数值
        int heightmode = MeasureSpec.getMode(heightMeasureSpec);    //取出高度的测量模式

        if (widthmode == MeasureSpec.AT_MOST || widthmode == MeasureSpec.UNSPECIFIED || widthsize < 0) {
            measureWidth = defaultWidth;
        } else {
            measureWidth = widthsize;
        }


        if (heightmode == MeasureSpec.AT_MOST || heightmode == MeasureSpec.UNSPECIFIED || heightsize < 0) {
            measureHeight = defalutHeight;
        } else {
            measureHeight = heightsize;
        }

        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mAreaPosition = new Point(w / 2, h / 2);
        mRockerPosition = new Point(mAreaPosition);

        // this need subtract the view padding
        int tempRadius = Math.min(w - getPaddingLeft() - getPaddingRight(), h - getPaddingTop() - getPaddingBottom());
        tempRadius /= 2;
        if (mAreaRadius == -1)
            mAreaRadius = (int) (tempRadius * 0.75);
        if (mRockerRadius == -1)
            mRockerRadius = (int) (tempRadius * 0.25);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mDrawThread = new Thread(this);
            mDrawThread.start();

            mCallbackThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (mCallbackOk) {

                        // listener callback
                        listenerCallback();

                        try {
                            Thread.sleep(mCallbackCycle);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            mCallbackThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mDrawOk = false;
        mCallbackOk = false;
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            mDrawOk = true;
            mCallbackOk = true;
        } else {
            mDrawOk = false;
            mCallbackOk = false;
        }
    }

    /*Event Response*******************************************************************************/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            int len = MathUtils.getDistance(mAreaPosition.x, mAreaPosition.y, event.getX(), event.getY());

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //如果屏幕接触点不在摇杆挥动范围内,则不处理
                if (len > mAreaRadius) {
                    return true;
                }
            }

            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (len <= mAreaRadius) {
                    //如果手指在摇杆活动范围内，则摇杆处于手指触摸位置
                    mRockerPosition.set((int) event.getX(), (int) event.getY());

                } else {
                    //设置摇杆位置，使其处于手指触摸方向的 摇杆活动范围边缘
                    mRockerPosition = MathUtils.getPointByCutLength(mAreaPosition,
                            new Point((int) event.getX(), (int) event.getY()), mAreaRadius);
                }

                    int movex=mRockerPosition.x-mAreaPosition.x;
                    int movey=mRockerPosition.y-mAreaPosition.y;
                    if (movex<0){
                        movex=mAreaPosition.x-mRockerPosition.x;
                    }
                    if (movey<0){
                        movey=mAreaPosition.y-mRockerPosition.y;
                    }

                Log.e("mar",mAreaRadius+"");
                float movexf=(float)movex/mAreaRadius;
                float moveyf=(float)movey/mAreaRadius;
                int l= (int) (movexf*100);
                int k= (int) (moveyf*100);
                if (mListener != null) {
                    float radian = MathUtils.getRadian(mAreaPosition, new Point((int) event.getX(), (int) event.getY()));
                    int angle = RockerView.this.getAngleConvert(radian);
                    float distance = MathUtils.getDistance(mAreaPosition.x, mAreaPosition.y, event.getX(), event.getY());
                    mListener.callback(EVENT_ACTION, angle, distance);
                }
            }
            //如果手指离开屏幕，则摇杆返回初始位置
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mRockerPosition = new Point(mAreaPosition);
                if (mListener != null) {
                    mListener.callback(EVENT_ACTION, -1, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    /*Thread - draw view***************************************************************************/

    @Override
    public void run() {
        if (isInEditMode()) {
            return;
        }

        Canvas canvas = null;

        while (mDrawOk) {
            try {
                canvas = mHolder.lockCanvas();
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                drawArea(canvas);
                drawRocker(canvas);

                Thread.sleep(mRefreshCycle);    // 休眠

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    mHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private void drawArea(Canvas canvas) {

        if (null != mAreaBitmap) {
            mPaint.setColor(Color.BLACK);
            Rect src = new Rect(0, 0, mAreaBitmap.getWidth(), mAreaBitmap.getHeight());
            Rect dst = new Rect(
                    mAreaPosition.x - mAreaRadius,
                    mAreaPosition.y - mAreaRadius,
                    mAreaPosition.x + mAreaRadius,
                    mAreaPosition.y + mAreaRadius);
            canvas.drawBitmap(mAreaBitmap, src, dst, mPaint);
        } else {
            mPaint.setColor(mAreaColor);
            canvas.drawCircle(mAreaPosition.x, mAreaPosition.y, mAreaRadius, mPaint);
        }
    }

    private void drawRocker(Canvas canvas) {
        if (null != mRockerBitmap) {
            mPaint.setColor(Color.BLACK);
            Rect src = new Rect(0, 0, mRockerBitmap.getWidth(), mRockerBitmap.getHeight());
            Rect dst = new Rect(
                    mRockerPosition.x - mRockerRadius,
                    mRockerPosition.y - mRockerRadius,
                    mRockerPosition.x + mRockerRadius,
                    mRockerPosition.y + mRockerRadius);
            canvas.drawBitmap(mRockerBitmap, src, dst, mPaint);
        } else {
            mPaint.setColor(mRockerColor);
            canvas.drawCircle(mRockerPosition.x, mRockerPosition.y, mRockerRadius, mPaint);
        }
    }

    private void listenerCallback() {
        if (mListener != null) {
            if (mRockerPosition.x == mAreaPosition.x && mRockerPosition.y == mAreaPosition.y) {
                mListener.callback(EVENT_CLOCK, -1, 0);
            } else {
                float radian = MathUtils.getRadian(mAreaPosition, new Point(mRockerPosition.x, mRockerPosition.y));
                int angle = RockerView.this.getAngleConvert(radian);
                float distance = MathUtils.getDistance(mAreaPosition.x, mAreaPosition.y, mRockerPosition.x, mRockerPosition.y);
                mListener.callback(EVENT_CLOCK, angle, distance);
            }
        }
    }

    //获取摇杆偏移角度 0-360°
    private int getAngleConvert(float radian) {
        int tmp = (int) Math.round(radian / Math.PI * 180);
        if (tmp < 0) {
            return -tmp;
        } else {
            return 180 + (180 - tmp);
        }
    }

    // for preview
    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            canvas.drawColor(Color.WHITE);
            drawArea(canvas);
            drawRocker(canvas);
        }
    }

    /*Getter Setter********************************************************************************/

    public int getAreaRadius() {
        return mAreaRadius;
    }

    public void setAreaRadius(int areaRadius) {
        mAreaRadius = areaRadius;
    }

    public int getRockerRadius() {
        return mRockerRadius;
    }

    public void setRockerRadius(int rockerRadius) {
        mRockerRadius = rockerRadius;
    }

    public Bitmap getAreaBitmap() {
        return mAreaBitmap;
    }

    public void setAreaBitmap(Bitmap areaBitmap) {
        mAreaBitmap = areaBitmap;
    }

    public Bitmap getRockerBitmap() {
        return mRockerBitmap;
    }

    public void setRockerBitmap(Bitmap rockerBitmap) {
        mRockerBitmap = rockerBitmap;
    }

    public int getRefreshCycle() {
        return mRefreshCycle;
    }

    public void setRefreshCycle(int refreshCycle) {
        mRefreshCycle = refreshCycle;
    }

    public int getCallbackCycle() {
        return mCallbackCycle;
    }

    public void setCallbackCycle(int callbackCycle) {
        mCallbackCycle = callbackCycle;
    }

    public int getAreaColor() {
        return mAreaColor;
    }

    public void setAreaColor(int areaColor) {
        mAreaColor = areaColor;
        mAreaBitmap = null;
    }

    public int getRockerColor() {
        return mRockerColor;
    }

    public void setRockerColor(int rockerColor) {
        mRockerColor = rockerColor;
        mRockerBitmap = null;
    }

    public void setListener(@NonNull RockerListener listener) {
        mListener = listener;
    }

    /*Rocker Listener******************************************************************************/

    /**
     * rocker listener
     */
    public interface RockerListener {

        /**
         * you can get some event from this method
         *
         * @param eventType       The event type, EVENT_ACTION or EVENT_CLOCK
         * @param currentAngle    The current angle
         * @param currentDistance The current distance (px)
         */
        void callback(int eventType, int currentAngle, float currentDistance);
    }

}