package com.regenpod.smartlightcontrol.app;

import android.app.Application;

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
        CrashHandler.getInstance().init(this);
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
