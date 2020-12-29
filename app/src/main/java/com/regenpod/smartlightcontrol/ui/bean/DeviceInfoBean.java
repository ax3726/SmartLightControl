package com.regenpod.smartlightcontrol.ui.bean;

/**
 * @auther liming
 * @date 2020-12-29
 * @desc
 */
public class DeviceInfoBean {

    private int status;

    /**
     * 设备地址
     */
    private int address;

    /**
     * 软件版本
     */
    private int softwareVersion;

    /**
     * 硬件版本
     */
    private int hardwareVersion;

    /**
     * 厂家
     */
    private String factory;

    /**
     * 型号
     */
    private String model;

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(int softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public int getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(int hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }
}
