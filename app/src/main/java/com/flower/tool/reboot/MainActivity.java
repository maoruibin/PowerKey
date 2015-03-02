package com.flower.tool.reboot;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
    }

    public void action_power_off(View view) throws Exception {
        Runtime.getRuntime().exec(new String[]{"su", "-c", "poweroff -f"});
    }

    public void action_reboot(View view) throws Exception {
        Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot"});
    }

    public void action_air_plane(View view)  {
    }
    public void action_silent(View view)  {
    }

}
