package com.regenpod.smartlightcontrol.app;

import android.app.Application;

import com.clj.fastble.BleManager;

public class LightApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        BleManager.getInstance().init(this);
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }
}
