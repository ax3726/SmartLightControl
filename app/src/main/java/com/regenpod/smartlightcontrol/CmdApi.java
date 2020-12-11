package com.regenpod.smartlightcontrol;

import com.clj.fastble.utils.HexUtil;

public class CmdApi {

    /**
     * 帧头
     */
    public static final int MSG_HEAD = 0xFB;

    /**
     * 帧尾
     */
    public static final int MSG_FOOTER = 0xBF;

    //********以下是cmd 指令********
    /**
     * 获取设备信息.
     */
    public static final int SYS_INFO = 0X50;
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

    private static String toHex(int value) {
        return Integer.toHexString(value);
    }

    public static byte[] createMessage(int cmd, int data, Integer value) {
        int datas = Integer.valueOf(value == null ? data + "" : data + value + "");
        int check = cmd ^ datas;
        byte[] bytes = new byte[5];
        bytes[0] = (byte) MSG_HEAD;
        bytes[1] = (byte) cmd;
        bytes[2] = (byte) datas;
        bytes[3] = (byte) check;
        bytes[4] = (byte) MSG_FOOTER;
        return bytes;
    }


}
