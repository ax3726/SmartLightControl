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

        if (System.currentTimeMillis() >= 1610956788000L) {//大于当前时间退出APP
            android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
            System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
        }

        BleManager.getInstance().init(this);
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setSplitWriteNum(512)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }

}
