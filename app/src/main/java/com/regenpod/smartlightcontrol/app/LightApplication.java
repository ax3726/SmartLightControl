package com.regenpod.smartlightcontrol.app;

import android.app.Application;

import com.clj.fastble.BleManager;
import com.regenpod.smartlightcontrol.CrashHandler;
import com.regenpod.smartlightcontrol.utils.FileUtil;

public class LightApplication extends Application {

    public static LightApplication mInstance;

    public static LightApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        if (System.currentTimeMillis() >= 1611582840000L) {//大于当前时间退出APP
            android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
            System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
        }
        CrashHandler.getInstance().init(this);

        BleManager.getInstance().init(this);
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setSplitWriteNum(512)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }

    /**
     * 创建app需要用的文件夹
     */
    public static void makeAppDir() {
        /*创建目录*/
        String path = FileUtil.getAppSdMainPath();
        FileUtil.makeDir(path);

    }


}
