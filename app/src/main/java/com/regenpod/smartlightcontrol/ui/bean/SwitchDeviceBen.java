package com.regenpod.smartlightcontrol.ui.bean;

/**
 * @auther liming
 * @date 2020-12-24
 * @desc
 */
public class SwitchDeviceBen {
    /**
     * 开关机状态
     */
    private boolean isSwitch;

    public SwitchDeviceBen(boolean isSwitch) {
        this.isSwitch = isSwitch;
    }

    public boolean isSwitch() {
        return isSwitch;
    }

    public void setSwitch(boolean aSwitch) {
        isSwitch = aSwitch;
    }
}
