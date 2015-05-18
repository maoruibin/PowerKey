package com.flower.tool.reboot;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flower.tool.reboot.manager.AnimManager;
import com.flower.tool.reboot.receiver.AdminManageReceiver;
import com.flower.tool.reboot.service.FloatWindowService;
import com.flower.tool.reboot.util.Util;

import java.io.IOException;


public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener {
    private LinearLayout mLLContainer;
    private TextView mTvPowerOff;
    private TextView mTvReboot;
    private TextView mTvAirPlane;
    private TextView mTvSilent;
    private AnimManager mAnimManager;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);

        setContentView(R.layout.activity_main);

        initView();

        addListener();

        //启动悬浮球
        startService(new Intent(this, FloatWindowService.class));

        activityDevice();

        //启动截屏操作

        shot();


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void shot() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        Bitmap bm = ((BitmapDrawable) wallpaperDrawable).getBitmap();
        Bitmap dealBm = Util.fastblur(bm,90);
        Drawable drawable = new BitmapDrawable(dealBm);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN){
            mLLContainer.setBackground(drawable);
        }
    }



    private void activityDevice(){
        mAdminName = new ComponentName(this, AdminManageReceiver.class);
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (!mDPM.isAdminActive(mAdminName)) {
            showAdminManagement(mAdminName);
        }
    }

    private void addListener() {
        mLLContainer.setOnTouchListener(this);
        mTvPowerOff.setOnClickListener(this);
        mTvReboot.setOnClickListener(this);
        mTvAirPlane.setOnClickListener(this);
        mTvSilent.setOnClickListener(this);
    }

    private void initView() {
        mLLContainer = (LinearLayout) findViewById(R.id.ll_container);
        mTvPowerOff = (TextView) findViewById(R.id.tv_power_off);
        mTvReboot = (TextView) findViewById(R.id.tv_reboot);
        mTvAirPlane = (TextView) findViewById(R.id.tv_lock_screen);
        mTvSilent = (TextView) findViewById(R.id.tv_silent);

        mAnimManager = new AnimManager(this);

        mLLContainer.post(new Runnable() {
            @Override
            public void run() {
                mAnimManager.addView(ActionType.POWER_OFF, mTvPowerOff);
                mAnimManager.addView(ActionType.REBOOT, mTvReboot);
                mAnimManager.addView(ActionType.AIR_PLANE, mTvAirPlane);
                mAnimManager.addView(ActionType.SILENT, mTvSilent);
                //进入app 动画
                mAnimManager.initEnter();
            }
        });


    }

    @Override
    public void onClick(final View view) {
        if(mAnimManager.canExecuteAction()){
            mAnimManager.scaleAnim(view,new AnimManager.IAnimationEnd() {
                @Override
                public void onAnimationEnd() {
                    executeAction(view);
                }
            });
        }else{
            mAnimManager.confirmExecute(view);
        }
    }

    private void executeAction(View view){
        switch (view.getId()){
            case R.id.tv_power_off:
                powerOff();
                break;
            case R.id.tv_reboot:
                reboot();
                break;
            case R.id.tv_lock_screen:
                if (mDPM.isAdminActive(mAdminName)) {
                    mDPM.lockNow();
                }
                break;
            case R.id.tv_silent:
                break;
            default:
                TextView textView = (TextView) view;
                Toast.makeText(this,textView.getText().toString(),Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void powerOff() {
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "poweroff -f"});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reboot() {
        try {
            Runtime.getRuntime().exec(new String[]{ "su", "-c", "reboot" });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(mAnimManager.canExecuteAction()){
            mAnimManager.resetVisibity();
            mAnimManager.revertView(AnimManager.DURATION_NORMAL);
        }
        return false;
    }

    private void showAdminManagement(ComponentName mAdminName) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, R.string.desc_enable_admin);
        startActivityForResult(intent,1);
    }
}
