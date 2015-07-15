package com.flower.tool.reboot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.flower.tool.reboot.service.FloatWindowService;

/**
 * Created by mao on 7/15/15.
 */
public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent autoIntent = new Intent(context, FloatWindowService.class);
        context.startService(autoIntent);
    }
}
