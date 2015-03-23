package com.flower.tool.reboot;

import android.animation.Animator;
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

    //存储管理器中的view
    private Map<ActionType,View> mViews = new HashMap<>();
    //每个view 的中心点坐标
    private Map<ActionType,float[]> mViewCenter = new HashMap<>();
    //管理器中每个view 对应的隐藏位置
    private Map<ActionType,float[]> mHidePoint = new HashMap<>();

    //屏幕中心的坐标点
    private float[] screenCenter = new float[]{0,0};
    //记录要移动的view 的 横向和纵向位移大小值
    private float[] historyOffset = new float[]{0,0};
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


    /**
     * 获得当前隐藏的view的个数
     * @return 如果隐藏的view数目大于零 则返回1 等于0 则返回0  ！ 嗯，这个方法的名字有点坑爹啊
     */
    public int getHideViewCountFlag(){
       int count = 0;
        for(ActionType type : mViews.keySet()){
            if(mViews.get(type).getVisibility() != View.VISIBLE){
                count += 1 ;
                break;
            }
        }
        return count;
    }

    public boolean canExecuteAction(){
        if(getHideViewCountFlag()>0){
            return true;
        }
        return false;
    }


    public void addView(ActionType type,View view){
        mViews.put(type,view);
        mViewCenter.put(type, getViewCenterLocation(view));
        mHidePoint.put(type,getHidePosition(view,type));
    }

    /**
     * 获取view对应的隐藏坐标
     * @param view
     * @return
     */
    private float[] getHidePosition(View view,ActionType type) {
        float[]position = new float[2];
        float[]centerPoint = mViewCenter.get(type);
        switch (type){
            case POWER_OFF:
                position[0] = -view.getWidth()/2;
                position[1] = centerPoint[1]-view.getHeight()/2;
                break;
            case REBOOT:
//                position[0] = -view.getWidth()/2;
//                position[1] = centerPoint[1]-view.getHeight()/2;
                break;
            case AIR_PLANE:
                position[0] = -view.getWidth()/2;
                position[1] = centerPoint[1] + view.getHeight();
                break;
            case SILENT:
                position[0] = -view.getWidth()/2;
                position[1] = centerPoint[1]-view.getHeight()/2;
                break;
        }
        return position;
    }

    /**
     * 还原回原来的样子
     */
    public void revertView(){
        for(ActionType type : mViews.keySet()){
            View view = mViews.get(type);
            if(view.getVisibility() == View.VISIBLE){
                moveViewToOrigin(view);
            }else{
                showView(view);
            }
        }
    }
    public void confirmExecute(View view){
        for(ActionType type : mViews.keySet()){
            if(mViews.get(type).getId() == view.getId()){
                moveViewToCenter(view);
            }else{
                removeView(view, type);
            }
        }
    }


    private void removeView(View view,ActionType type){
        float[]hideLocation = mHidePoint.get(type);
        float[]viewCenterLocation = mViewCenter.get(type);

        moveAnim(view,viewCenterLocation,hideLocation);

//
//
//
//        AnimatorSet animatorSet = new AnimatorSet();
//        ObjectAnimator moveAnimX = ObjectAnimator.ofFloat(view,"translationX",0f,tempOffSet[0]);
//        ObjectAnimator moveAnimY = ObjectAnimator.ofFloat(view,"translationY",0f,tempOffSet[1]);
//
//        animatorSet.setDuration(500);
//        animatorSet.setInterpolator(new DecelerateInterpolator());
//        animatorSet.playTogether(moveAnimX, moveAnimY);
//
//        animatorSet.start();

    }

    private void moveViewToCenter(View view){
        float[]viewLocation = getViewCenterLocation(view);

        historyOffset[0] = screenCenter[0] - viewLocation[0];
        historyOffset[1] = screenCenter[1] - viewLocation[1];

        moveAnim(view,viewLocation,screenCenter);

    }

    private void moveAnim(View view,float start[],float end[]){
        float[]offset = new float[2];
        offset[0] = end[0] - start[0];
        offset[1] = end[1] - start[1];

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator moveAnimX = ObjectAnimator.ofFloat(view,"translationX",0f,offset[0]);
        ObjectAnimator moveAnimY = ObjectAnimator.ofFloat(view,"translationY",0f,offset[1]);

        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.playTogether(moveAnimX, moveAnimY);

        animatorSet.start();
    }

    /**
     * 让view回到原始位置
     * @param view 目标view
     */
    private void moveViewToOrigin(View view){
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator moveAnimX = ObjectAnimator.ofFloat(view,"translationX",historyOffset[0],0f);
        ObjectAnimator moveAnimY = ObjectAnimator.ofFloat(view,"translationY",historyOffset[1],0f);

        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.playTogether(moveAnimX, moveAnimY);
        animatorSet.start();
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

    private void showView(final View view){
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view,"alpha",0f,1f);
        alphaAnim.setDuration(500);
        alphaAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        alphaAnim.start();
    }
}
