package com.flower.tool.reboot;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener {
    private LinearLayout mLLContainer;
    private TextView mTvPowerOff;
    private TextView mTvReboot;
    private TextView mTvAirPlane;
    private TextView mTvSlient;
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
        mTvSlient.setOnClickListener(this);
    }

    private void initView() {
        mLLContainer = (LinearLayout) findViewById(R.id.ll_container);
        mTvPowerOff = (TextView) findViewById(R.id.tv_power_off);
        mTvReboot = (TextView) findViewById(R.id.tv_reboot);
        mTvAirPlane = (TextView) findViewById(R.id.tv_air_plane);
        mTvSlient = (TextView) findViewById(R.id.tv_silent);

        mAnimManager = new AnimManager(this);

        mLLContainer.post(new Runnable() {
            @Override
            public void run() {
                mAnimManager.addView(ActionType.POWER_OFF, mTvPowerOff);
                mAnimManager.addView(ActionType.REBOOT, mTvReboot);
                mAnimManager.addView(ActionType.AIR_PLANE, mTvAirPlane);
                mAnimManager.addView(ActionType.SILENT, mTvSlient);

                mAnimManager.initEnter();
            }
        });


    }

    @Override
    public void onClick(View view) {
        if(mAnimManager.canExecuteAction()){
            //Toast.makeText(this,"执行 " + ((TextView)view).getText().toString(),Toast.LENGTH_SHORT).show();
            //mAnimManager.moveViewToOrigin(view);
        }else{
            mAnimManager.confirmExecute(view);
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //只有 界面 上有隐藏的view的情况下 才可对onTouch方法进行处理
        if(mAnimManager.canExecuteAction()){
            mAnimManager.resetVisibity();
            mAnimManager.revertView(AnimManager.DURATION_LONG);

        }
        return false;
    }
}
