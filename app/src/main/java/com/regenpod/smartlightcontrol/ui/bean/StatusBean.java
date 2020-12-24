package com.regenpod.smartlightcontrol.ui.bean;

/**
 * @auther liming
 * @date 2020-12-24
 * @desc
 */
public class StatusBean {
    //设备状态
    private int status;

    public StatusBean(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
