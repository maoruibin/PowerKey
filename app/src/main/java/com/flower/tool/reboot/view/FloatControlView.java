package com.flower.tool.reboot.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.flower.tool.reboot.R;

import java.lang.reflect.Field;


/**
 * Created by Mao on 16:46 4/22/2015
 */
public class FloatControlView extends LinearLayout {
    private WindowManager windowManager;
    public static int viewWidth;
    public static int viewHeight;

    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;
    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;

    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float xInView;

    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float yInView;

    /**
     * 记录系统状态栏的高度
     */
    private static int statusBarHeight;

    private WindowManager.LayoutParams mParams;

    private OnClickFloatView mListener;

    /**
     * 上次点击的时间
     */
    private long waitTime = 300;

    // 已经连续 touch 的次数
    private long touchTime = 0;


    public FloatControlView(Context context) {
        this(context, null);
    }

    public FloatControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_view,this);
        View view = findViewById(R.id.tv_float);
        //view.setOnLongClickListener(this);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY()-getStatusBarHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY()-getStatusBarHeight();
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen){
                    if(mListener != null){
                        mListener.onClick();
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }
    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        windowManager.updateViewLayout(this, mParams);
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params
     *            小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }
    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    public void setClickListener(OnClickFloatView mListener) {
        this.mListener = mListener;
    }


    public interface  OnClickFloatView{
        public void onClick();
        public void onLongClick();
    }
}
