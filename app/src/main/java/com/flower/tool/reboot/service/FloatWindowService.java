package com.flower.tool.reboot.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.flower.tool.reboot.MainActivity;
import com.flower.tool.reboot.receiver.AdminManageReceiver;
import com.flower.tool.reboot.manager.MyWindowManager;
import com.flower.tool.reboot.R;
import com.flower.tool.reboot.view.FloatControlView;

/**
 * Created by Mao on 17:35 4/22/2015
 */
public class FloatWindowService extends Service implements FloatControlView.OnClickFloatView {
    ComponentName mAdminName;
    DevicePolicyManager mDPM;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAdminName = new ComponentName(this, AdminManageReceiver.class);
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyWindowManager.createFloatView(this, this);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onClick() {
        if (mDPM.isAdminActive(mAdminName)) {
            mDPM.lockNow();
        }
    }

    @Override
    public void onLongClick() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
