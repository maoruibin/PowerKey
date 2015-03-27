package com.flower.tool.reboot;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mao on 14:51 2015/3/23
 */
public class AnimManager {
    public static final int DURATION_NORMAL = 500;
    public static final int DURATION_ZERO = 0;
    public static final int DURATION_LONG = 800;
    //存储管理器中的view
    private Map<ActionType,View> mViews = new HashMap<>();
    //每个view 的中心点坐标
    private Map<ActionType,float[]> mViewCenter = new HashMap<>();
    //管理器中每个view 对应的隐藏位置
    private Map<ActionType,float[]> mHidePoint = new HashMap<>();

    private Map<ActionType,Integer> mVisiblity = new HashMap<>();

    //屏幕中心的坐标点
    private float[] screenCenter = new float[]{0,0};
    private Context mContext;

    public AnimManager(Context mContext) {
        this.mContext = mContext;
        screenCenter = getScreenCenter();
    }

    private float[] getScreenCenter(){
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        return new float[]{screenWidth/2,screenHeight/2};
    }

    public void addView(ActionType type,View view){
        mViews.put(type,view);
        mViewCenter.put(type, getViewCenterLocation(view));
        mHidePoint.put(type,getHidePosition(view,type));
        mVisiblity.put(type,View.VISIBLE);
    }

    /**
     * 获取view对应的隐藏坐标
     * @param view 目标移动view
     * @return float[0] X轴上的偏移位置 float[1] Y轴上的偏移位置
     */
    private float[] getHidePosition(View view,ActionType type) {
        //translation[0] [1] 分别记录 view的水平移动距离和纵向移动距离
        float[] translation = new float[2];
        switch (type){
            case POWER_OFF:
                translation[0] = -view.getWidth() * 3/2;
                translation[1] = -view.getHeight();
                break;
            case REBOOT:
                translation[0] = view.getWidth() * 3/2;
                translation[1] = -view.getHeight();
                break;
            case AIR_PLANE:
                translation[0] = -view.getWidth() * 3/2;
                translation[1] = view.getHeight();
                break;
            case SILENT:
                translation[0] = view.getWidth() * 3/2;
                translation[1] = view.getHeight();
                break;
        }
        return translation;
    }

    /**
     * 还原回原来的样子
     */
    public void revertView(int duration){
        for(ActionType type : mViews.keySet()){
            translationAnim(mViews.get(type), 0, 0,duration);
        }
    }

    /**
     * 按钮是否可以执行真真的动作相应
     * @return true 可以执行相应的动作
     */
    public boolean canExecuteAction(){
        int visibleCount = 0;
        for(ActionType type : mVisiblity.keySet()){
            if(mVisiblity.get(type) == View.VISIBLE){
                visibleCount ++ ;
            }
        }
        //只有当显示的数目为1 才会去执行 动作
        return visibleCount == 1;
    }

    public void resetVisibity(){
        for(ActionType type : mVisiblity.keySet()){
            mVisiblity.put(type,View.VISIBLE);
        }
    }

    public void confirmExecute(final View view){
        moveViewToCenter(view);
        for(ActionType type : mViews.keySet()){
            View tempView = mViews.get(type);
            if(view.getId() != tempView.getId()){
                float[]viewLocation = getHidePosition(view,type);
                translationAnim(tempView, viewLocation[0], viewLocation[1],DURATION_NORMAL);
                mVisiblity.put(type, View.INVISIBLE);
            }else{
                mVisiblity.put(type,View.VISIBLE);
            }
        }
    }


    private void moveViewToCenter(View view){
        float[]viewLocation = getViewCenterLocation(view);

        float[]offset = new float[2];
        offset[0] = screenCenter[0] - viewLocation[0];
        offset[1] = screenCenter[1] - viewLocation[1];

        translationAnim(view, offset[0], offset[1],DURATION_NORMAL);

    }

    private void translationAnim(View view,float offSetX,float offSetY,int duration){
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator moveAnimX = ObjectAnimator.ofFloat(view,"translationX",offSetX);
        ObjectAnimator moveAnimY = ObjectAnimator.ofFloat(view,"translationY",offSetY);

        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.playTogether(moveAnimX, moveAnimY);

        animatorSet.start();
    }


    /**
     * 初始进入程序时的动画控制
     */
    public void initEnter(){
        for(ActionType type : mViews.keySet()){
            View tempView = mViews.get(type);
            float[]viewLocation = getHidePosition(tempView,type);
            translationAnim(tempView, viewLocation[0], viewLocation[1],DURATION_ZERO);
        }
//        for(ActionType type : mViews.keySet()){
//            translationAnim(mViews.get(type), 0, 0,DURATION_LONG);
//        }

        revertView(DURATION_NORMAL);
    }
    /**
     * 获取view的中心点坐标
     * @param view 目标view
     * @return view的中心点
     */
    private float[] getViewCenterLocation(View view){
        int[]viewLocation = new int[2];
        view.getLocationInWindow(viewLocation);
        return new float[]{viewLocation[0]+view.getWidth()/2,viewLocation[1]+view.getHeight()/2};
    }


}
