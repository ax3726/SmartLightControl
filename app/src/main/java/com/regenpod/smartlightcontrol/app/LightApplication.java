package com.regenpod.smartlightcontrol.app;

import android.app.Application;

import com.clj.fastble.BleManager;

public class LightApplication extends Application {

    public static LightApplication mInstance;

    public static LightApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        BleManager.getInstance().init(this);
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setSplitWriteNum(512)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }

}
