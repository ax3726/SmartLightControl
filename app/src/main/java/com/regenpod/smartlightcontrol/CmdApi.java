package com.regenpod.smartlightcontrol;


import android.text.TextUtils;
import android.widget.Toast;

import com.clj.fastble.utils.HexUtil;
import com.regenpod.smartlightcontrol.app.LightApplication;
import com.regenpod.smartlightcontrol.ui.bean.ControlBean;
import com.regenpod.smartlightcontrol.ui.bean.DeviceInfoBean;
import com.regenpod.smartlightcontrol.ui.bean.StatusBean;
import com.regenpod.smartlightcontrol.ui.bean.SwitchDeviceBen;
import com.regenpod.smartlightcontrol.ui.bean.TimeBean;

import org.greenrobot.eventbus.EventBus;

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
     * 获取设备机型 (必需实现).
     */
    public static final int SYS_INFO_MODEL = 0;
    /**
     * 获取设备联机地址
     */
    public static final int SYS_INFO_ADDRESS = 1;
    /**
     * 获取设备软件、硬件版本号
     */
    public static final int SYS_INFO_VER = 2;

    /**
     * 获取设备状态
     */
    public static final int SYS_STATUS = 0X51;
    //************指令*************//
    /**
     * 获取设备状态---待机状态
     */
    public static final int SYS_STATUS_NORMAL = 0;

    /**
     * 获取设备状态---关机状态
     */
    public static final int SYS_STATUS_END = 1;

    /**
     * 获取设备状态---运行中状态
     */
    public static final int SYS_STATUS_RUNNING = 2;

    /**
     * 获取设备状态---设备故障
     */
    public static final int SYS_STATUS_ERROR = 3;
    //************指令*************//

    /**
     * 设备控制
     */
    public static final int SYS_CONTROL = 0X53;

    //********以下是data 指令********//

    /**
     * 设备控制---写入红灯占空比
     */
    public static final int SYS_CONTROL_R_PWM = 3;

    /**
     * 设备控制---写入红外灯占空比
     */
    public static final int SYS_CONTROL_RW_PWM = 4;

    /**
     * 设备控制---写入红灯频率
     */
    public static final int SYS_CONTROL_R_FER = 5;

    /**
     * 设备控制---写入红外灯频率
     */
    public static final int SYS_CONTROL_RW_FER = 6;


    /**
     * 设备控制---设备正式启动
     */
    public static final int SYS_CONTROL_START = 8;

    /**
     * 设备控制---设备停止
     */
    public static final int SYS_CONTROL_STOP = 0;

    /**
     * 设备控制---写入设定时间
     */
    public static final int SYS_CONTROL_TIME = 7;


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

    public static byte[] createMessage(int cmd, int data, int value) {
        return createMessage(cmd, data, value, false);
    }

    public static byte[] createMessage(int cmd, int data, int value, boolean isHighZero) {
        if (data == -1) {
            byte[] bytes = new byte[4];
            bytes[0] = (byte) MSG_HEAD;
            bytes[1] = (byte) cmd;
            bytes[2] = (byte) (cmd);
            bytes[3] = (byte) MSG_FOOTER;
            return bytes;
        }

        //没有 value值
        if (value == -1) {
            byte[] bytes = new byte[5];
            bytes[0] = (byte) MSG_HEAD;
            bytes[1] = (byte) cmd;
            bytes[2] = (byte) data;
            bytes[3] = (byte) (cmd ^ data);
            bytes[4] = (byte) MSG_FOOTER;
            return bytes;
        } else {
            String hexValue = Integer.toHexString(value);
            int check = 0;
            if (hexValue.length() == 4) {
                Integer height = Integer.parseInt(hexValue.substring(0, 2), 16);
                Integer low = Integer.parseInt(hexValue.substring(2, 4), 16);
                check = cmd ^ data ^ height ^ low;
                byte[] bytes = new byte[7];
                bytes[0] = (byte) MSG_HEAD;
                bytes[1] = (byte) cmd;
                bytes[2] = (byte) data;
                bytes[3] = height.byteValue();
                bytes[4] = low.byteValue();
                bytes[5] = (byte) check;
                bytes[6] = (byte) MSG_FOOTER;
                return bytes;
            } else if (hexValue.length() == 3) {
                Integer height = Integer.parseInt(hexValue.substring(0, 1), 16);
                Integer low = Integer.parseInt(hexValue.substring(1, 3), 16);
                check = cmd ^ data ^ height ^ low;
                byte[] bytes = new byte[7];
                bytes[0] = (byte) MSG_HEAD;
                bytes[1] = (byte) cmd;
                bytes[2] = (byte) data;
                bytes[3] = height.byteValue();
                bytes[4] = low.byteValue();
                bytes[5] = (byte) check;
                bytes[6] = (byte) MSG_FOOTER;
                return bytes;
            } else {
                check = cmd ^ data ^ value;
                if (isHighZero) {
                    byte[] bytes = new byte[7];
                    bytes[0] = (byte) MSG_HEAD;
                    bytes[1] = (byte) cmd;
                    bytes[2] = (byte) data;
                    bytes[3] = (byte) 0X00;
                    bytes[4] = (byte) value;
                    bytes[5] = (byte) check;
                    bytes[6] = (byte) MSG_FOOTER;
                    return bytes;
                } else {
                    byte[] bytes = new byte[6];
                    bytes[0] = (byte) MSG_HEAD;
                    bytes[1] = (byte) cmd;
                    bytes[2] = (byte) data;
                    bytes[3] = (byte) value;
                    bytes[4] = (byte) check;
                    bytes[5] = (byte) MSG_FOOTER;
                    return bytes;
                }


            }
        }
    }

    /**
     * 解析消息
     *
     * @param data
     */
    public static void analyzeInstruction(String data) {
        if (TextUtils.isEmpty(data)) {
            return;
        }

        //关机成功
        if (data.equals(HexUtil.formatHexString(createMessage(SYS_CONTROL, SYS_CONTROL_STOP, -1)))) {
            EventBus.getDefault().postSticky(new SwitchDeviceBen(false));
            return;
        }
        //开机成功
        if (data.equals(HexUtil.formatHexString(createMessage(SYS_CONTROL, SYS_CONTROL_START, -1)))) {
            EventBus.getDefault().postSticky(new SwitchDeviceBen(true));
            return;
        }

        //效验是否有头尾帧
        if (!data.contains(Integer.toHexString(MSG_HEAD))
                || !data.contains(Integer.toHexString(MSG_FOOTER))
        ) {
            return;
        }
        //效验头是否正确
        if (data.length() < 6 || !Integer.toHexString(MSG_HEAD).equals(data.substring(0, 2))) {
            return;
        }
        //有效数据   cmd +data+value
        String validData = data.substring(2, data.length() - 4);
        int command = Integer.parseInt(validData.substring(0, 2), 16);

        switch (command) {
            case SYS_INFO: //设备信息
                if (validData.length() > 4) {
                    //指令
                    int controlCommand = Integer.parseInt(validData.substring(2, 4), 16);
                    String substring = validData.substring(4);
                    DeviceInfoBean deviceInfoBean = new DeviceInfoBean();
                    deviceInfoBean.setStatus(controlCommand);
                    switch (controlCommand) {
                        case SYS_INFO_ADDRESS://获取设备联机地址
                            deviceInfoBean.setAddress(substring);
                            EventBus.getDefault().postSticky(deviceInfoBean);
                            break;
                        case SYS_INFO_VER://获取设备软件、硬件版本号
                            if (substring.length() == 4) {
                                deviceInfoBean.setSoftwareVersion(substring.substring(0, 2));
                                deviceInfoBean.setHardwareVersion(substring.substring(2, 4));
                                EventBus.getDefault().postSticky(deviceInfoBean);
                            }
                            break;
                        case SYS_INFO_MODEL://获取设备机型 (必需实现)
                            if (substring.length() == 8) {
                                deviceInfoBean.setFactory(substring.substring(0, 4));
                                deviceInfoBean.setModel(substring.substring(4, 8));
                                EventBus.getDefault().postSticky(deviceInfoBean);
                            }
                            break;
                    }

                }
                break;
            case SYS_STATUS: //设备状态
                //控制指令
                int controlCommand = Integer.parseInt(validData.substring(2, 4), 16);
                switch (controlCommand) {
                    case SYS_STATUS_RUNNING:
                        //发送设备状态值
                        EventBus.getDefault().postSticky(new StatusBean(SYS_STATUS_RUNNING));
                        if (validData.length() == 20) {
                            int time = Integer.parseInt(validData.substring(16, 20), 16);
                            EventBus.getDefault().postSticky(new TimeBean(time));

                            int rw_fer =  Integer.parseInt(validData.substring(12, 16), 16);
                            EventBus.getDefault().postSticky(new ControlBean(SYS_CONTROL_RW_FER, rw_fer));

                            int r_fer =  Integer.parseInt(validData.substring(8, 12), 16);
                            EventBus.getDefault().postSticky(new ControlBean(SYS_CONTROL_R_FER, r_fer));

                            int rw_pwm =  Integer.parseInt(validData.substring(6, 8), 16);
                            EventBus.getDefault().postSticky(new ControlBean(SYS_CONTROL_RW_PWM, rw_pwm));

                            int r_pwm =  Integer.parseInt(validData.substring(4,6), 16);
                            EventBus.getDefault().postSticky(new ControlBean(SYS_CONTROL_R_PWM, r_pwm));

                        }
                        break;
                    case SYS_STATUS_END:
                        //发送设备状态值
                        EventBus.getDefault().postSticky(new StatusBean(SYS_STATUS_END));
                        break;
                    case SYS_STATUS_NORMAL:
                        EventBus.getDefault().postSticky(new StatusBean(SYS_STATUS_NORMAL));
                        break;
                    case SYS_STATUS_ERROR:
                        EventBus.getDefault().postSticky(new StatusBean(SYS_STATUS_ERROR));
                        Toast.makeText(LightApplication.getInstance(), "设备故障! 代码：" + validData.substring(4), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        String substring = validData.substring(4);
                        try {
                            //值
                            int value = Integer.parseInt(substring, 16);
                            //发送值

                        } catch (Exception ex) {
                            Toast.makeText(LightApplication.getInstance(), "解析数据异常!", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                break;
            case SYS_CONTROL: //设备控制
               /* //控制指令
                int controlCommand = Integer.parseInt(validData.substring(2, 4), 16);
                //值
                int value = Integer.parseInt(validData.substring(4), 16);
                //发送值
                EventBus.getDefault().postSticky(new ControlBean(controlCommand, value));*/
                break;
        }

    }

    /**
     * 十六进制转换字符串
     *
     * @param hexStr str Byte字符串(Byte之间无分隔符 如:[616C6B])
     * @return String 对应的字符串
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }
}
