package com.regenpod.smartlightcontrol.ui.bean;

/**
 * @auther liming
 * @date 2020-12-24
 * @desc
 */
public class ControlBean {
    //指令
    private int command = 0;
    //值
    private int value = 0;

    public ControlBean(int command, int value) {
        this.command = command;
        this.value = value;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
