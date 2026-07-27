@file:JvmName("BtUtils")

package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage

import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.BlueToothService.Companion.getDiscoveryTypeEnum
import com.ihealth.communication.manager.DiscoveryTypeEnum
import com.ihealth.communication.manager.iHealthDevicesCallback
import com.ihealth.communication.manager.iHealthDevicesManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.util.ArrayList

fun defaultEventBus() = EventBus.getDefault()
fun compareMac(mac1: String, mac2: String): Boolean {
    var m1 = mac1
    var m2 = mac2
    if (mac1.contains(':'))
        m1 = mac1.replace(":", "")
    if (mac2.contains(':'))
        m2 = mac2.replace(":", "")
    return m1 == m2
}

class BlueToothService(val context: Context ,val application: Application) {

    /**for iHealth Starts*/
    companion object {
        val deviceStructList = ArrayList<DeviceStruct>()

        class DeviceStruct {
            var name: String? = null
            var type: Long = 0
            var isSelected: Boolean = false
        }

        fun getDiscoveryTypeEnum(deviceName: String): DiscoveryTypeEnum? {
            for (type in DiscoveryTypeEnum.values()) {
                if (deviceName == type.name) {
                    return type
                }
            }
            return null
        }

        init {
            val fields = iHealthDevicesManager::class.java.fields
            for (field in fields) {
                val fieldName = field.name
                if (fieldName.contains("DISCOVERY_")) {
                    val struct = DeviceStruct()
                    struct.name = fieldName.substring(10)
                    try {
                        struct.type = field.getLong(null)
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    }

                    deviceStructList.add(struct)
                }
            }
        }
    }


    init {
        iHealthDevicesManager.getInstance().init(application, Log.VERBOSE, Log.ASSERT)
        btInit()
    }

    private fun btInit() {
        /*iHealth Code Don't Touch*/
        //authenticate certificate
        val mySharedPreferences = context.getSharedPreferences("preference", Context.MODE_PRIVATE)
        val discoveryType = mySharedPreferences.getLong("discoveryType", 0)
        for (struct in deviceStructList) {
            struct.isSelected = !((discoveryType and struct.type).equals(0))
        }

        try {
            val `is` = context.getAssets().open("com_cybermed_cdoc_patient_android.pem")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            val isPass = iHealthDevicesManager.getInstance().sdkAuthWithLicense(buffer)
            Log.i("info", "isPass:    $isPass")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        /*iHealth Code Don't Touch*/
    }

    /**for iHealth Ends*/

    private val miHealthDevicesCallback = object : iHealthDevicesCallback() {
        override fun onScanDevice(mac: String?, deviceType: String?, rssi: Int) {
            defaultEventBus().post(ScanDevice(mac, deviceType, rssi))
        }

        override fun onDeviceConnectionStateChange(mac: String?, deviceType: String?, status: Int, errorID: Int) {
            defaultEventBus().post(DeviceConnectionStateChange(mac, deviceType, status, errorID))
        }

        override fun onDeviceNotify(mac: String?, deviceType: String?, action: String?, message: String?) {
            defaultEventBus().post(DeviceNotify(mac, deviceType, action, message))
        }
    }

    private var callBackId: Int = 0

    /*Called By Containing Activity*/
    fun registerCallBack() {
        callBackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback)
    }

    fun unregisterCallBack() {
        iHealthDevicesManager.getInstance().unRegisterClientCallback(callBackId)
        iHealthDevicesManager.getInstance().destroy()
    }
    /*Called By Containing Activity*/


    private lateinit var timer: DiscoveryCountDownTimer
    private lateinit var device: DEVICE

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun startDiscovery(discoverMessage: StartDiscovery) {
        device = discoverMessage.device
        timer = DiscoveryCountDownTimer(device.device_name).startTimer()
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun stopDiscovery(stopDiscoverMessage: StopDiscovery) {
        iHealthDevicesManager.getInstance().stopDiscovery()
        timer.cancelTimer()
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun connectDevice(connectDeviceMessage: ConnectDevice) {

        iHealthDevicesManager.getInstance().connectDevice(
                connectDeviceMessage.userName, connectDeviceMessage.mac, connectDeviceMessage.deviceName)

    }
    @Subscribe(threadMode = ThreadMode.POSTING)
    fun disconnectDevice(connectDeviceMessage: DisconnectDevice) {

        iHealthDevicesManager.getInstance().disconnectDevice(connectDeviceMessage.mac, connectDeviceMessage.deviceName)

    }
}

private class DiscoveryCountDownTimer(val device_name: String) {

    private val countDownTimer = object : CountDownTimer(39000, 13000) {
        override fun onTick(millisUntilFinished: Long) {
            iHealthDevicesManager.getInstance().startDiscovery(getDiscoveryTypeEnum(device_name))
        }

        override fun onFinish() {
            defaultEventBus().post(ToTimeoutPage())
        }
    }

    fun startTimer(): DiscoveryCountDownTimer {
        countDownTimer.start()
        return this
    }

    fun cancelTimer() {
        countDownTimer.cancel()
    }
}