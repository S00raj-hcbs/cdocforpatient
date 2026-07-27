package com.choicemmed.s1blelibrary.cmd.listener;

import com.choicemmed.s1blelibrary.Device.S1Device;

/**
 * Created by lazy_xia on 17/1/19.
 */

public interface S1Listener {

    /**
     * 绑定 成功
     **/
    void onBindDeviceSuccess(S1Device s1Device);

    /**
     * 绑定 失败
     **/
    void onBindDeviceFail(String failMessage);

    /**
     * 数据 返回
     **/
    void onDataResponse(double d, String macaddress);

    /**
     * 错误
     **/
    void onError(String message);

    /**
     * 连接 成功
     **/
    void onConnectedDeviceSuccess();

    /**
     * 连接 失败
     **/
    void onConnectedDeviceFail(String failMessage);

    /**
     * 断开连接
     **/
    void onDisconnected();

    /**
     * 状态改变
     **/
    void onStateChanged(int bleState, int state);

    /**
     * 设置单位 成功
     **/
    void setUnitSuccess();

    /**
     * 设置单位 失败
     **/
    void setUnitError(String msg);
    void onScanTimeout(String msg);
}
