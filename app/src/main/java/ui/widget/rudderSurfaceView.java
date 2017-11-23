package ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import utils.MathUtils;

/**
 * Created by yingc on 2017/11/20 0020.
 */

public class rudderSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {


    private SurfaceHolder mHolder;
    private boolean isStop = false;
    private Thread mThread;
    private Paint mPaint;
    private Point mRockerPosition; //摇杆位置
    private Point mCtrlPoint = new Point(180, 280);//摇杆起始位置
    private int mRudderRadius = 60;//摇杆半径
    private int mWheelRadius = 150;//摇杆活动范围半径
    private RudderListener listener = null; //事件回调接口
    public static final int ACTION_RUDDER = 1, ACTION_ATTACK = 2; // 1：摇杆事件 2：按钮事件（未实现）


    public rudderSurfaceView(Context context) {
        super(context);
    }

    public rudderSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
// TODO Auto-generated constructor stub
        this.setKeepScreenOn(true);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mThread = new Thread(this);
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setAntiAlias(true);////抗锯齿
        mRockerPosition = new Point(mCtrlPoint);
        setFocusable(true);
        setFocusableInTouchMode(true);
        mHolder.setFormat(PixelFormat.TRANSPARENT);// 设置背景透明
    }


    //设置回调接口
    public void setRudderListener(RudderListener rudderListener) {
        listener = rudderListener;
    }

    @Override
    public void run() {
// TODO Auto-generated method stub
        Canvas canvas = null;
        while (!isStop) {
            try {
                canvas = mHolder.lockCanvas();
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//清除屏幕
                mPaint.setColor(Color.CYAN);
                canvas.drawCircle(mCtrlPoint.x, mCtrlPoint.y, mWheelRadius, mPaint);//绘制范围
                mPaint.setColor(Color.RED);
                canvas.drawCircle(mRockerPosition.x, mRockerPosition.y, mRudderRadius, mPaint);//绘制摇杆
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    mHolder.unlockCanvasAndPost(canvas);
                }
            }
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
// TODO Auto-generated method stub

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
// TODO Auto-generated method stub
        mThread.start();
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
// TODO Auto-generated method stub
        isStop = true;
// mThread.stop();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
// TODO Auto-generated method stub
        int len = MathUtils.getLength(mCtrlPoint.x, mCtrlPoint.y, event.getX(), event.getY());
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//如果屏幕接触点不在摇杆挥动范围内,则不处理
            if (len > mWheelRadius) {
                return true;
            }
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (len <= mWheelRadius) {
//如果手指在摇杆活动范围内，则摇杆处于手指触摸位置
                mRockerPosition.set((int) event.getX(), (int) event.getY());
            } else {
//设置摇杆位置，使其处于手指触摸方向的 摇杆活动范围边缘
                mRockerPosition = MathUtils.getBorderPoint(mCtrlPoint, new Point((int) event.getX(), (int) event.getY()), mWheelRadius);
            }
            if (listener != null) {
                float radian = MathUtils.getRadian(mCtrlPoint, new Point((int) event.getX(), (int) event.getY()));
                listener.onSteeringWheelChanged(ACTION_RUDDER, event.getX(),event.getY());
            }


        }
//如果手指离开屏幕，则摇杆返回初始位置
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mRockerPosition = new Point(mCtrlPoint);
        }

        return true;
    }


    //回调接口
    public interface RudderListener {
        public void onSteeringWheelChanged(int action, float x,float y);
    }


    //获取摇杆偏移角度 0-360°
    public int getAngleCouvert(float raian) {
        int tmp = (int) Math.round(raian / Math.PI * 180);
        if (tmp < 0) {
            return -tmp;
        } else {
            return 180 + (180 - tmp);
        }
    }
}
