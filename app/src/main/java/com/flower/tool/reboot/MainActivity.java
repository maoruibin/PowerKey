package com.flower.tool.reboot;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener {
    private LinearLayout mLLContainer;
    private TextView mTvPowerOff;
    private TextView mTvReboot;
    private TextView mTvAirPlane;
    private TextView mTvSilent;
    private AnimManager mAnimManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        initView();

        addListener();

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
        mTvAirPlane = (TextView) findViewById(R.id.tv_air_plane);
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
//              nforceCallingOrSelfPermission(android.Manifest.permission.REBOOT, null);
                break;
            case R.id.tv_reboot:
                reboot();
                //mPowerManager.reboot("reboot");
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
}
