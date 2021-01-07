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
    private String address;

    /**
     * 软件版本
     */
    private String softwareVersion;

    /**
     * 硬件版本
     */
    private String hardwareVersion;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
