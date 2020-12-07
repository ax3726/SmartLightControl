package com.regenpod.smartlightcontrol;

import android.util.Log;

public class CmdApi {

    /**
     * 帧头
     */
    public static final String MSG_HEAD = "FB";

    /**
     * 帧尾
     */
    public static final String MSG_FOOTER = "BF";

    //********以下是cmd 指令********
    /**
     * 获取设备信息.
     */
    public static final int SYS_INFO = 0x50;
    /**
     * 获取设备状态
     */
    public static final int SYS_STATUS = 0X51;
    /**
     * 设备控制
     */
    public static final int SYS_CONTROL = 0X53;

    //********以下是data 指令********

    /**
     * 设定时间
     */
    public static final String SET_TIMER = "07";


    public static void parseMessage(String instruction) {
        instruction = "FB500050BF";
        int command = Integer.parseInt(instruction.substring(2, 4), 16);
        switch (command) {
            case SYS_INFO:
                System.out.print("获取设备信息");
                break;
            case SYS_STATUS:
                System.out.print("获取设备状态");
                break;
        }

    }




}
