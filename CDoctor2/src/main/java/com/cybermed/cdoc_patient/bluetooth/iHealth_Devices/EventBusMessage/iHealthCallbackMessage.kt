package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage

data class ConnectDevice(val userName: String?, val mac: String?, val deviceName: String?)

data class ScanDevice(val mac: String?, val deviceName: String?, val rssi: Int)

data class DeviceConnectionStateChange(val mac: String?, val deviceType: String?, val status: Int, val errorID: Int)

data class DeviceNotify(val mac: String?, val deviceType: String?, val action: String?, val message: String?)

data class DisconnectDevice( val mac: String?, val deviceName: String?)

class StopDiscovery
class StopMeasuring
class StartMeasuring
class ToResultFragment


data class ResultData(val data1: String, val data2: String, val data3: String)

data class MeasuringData(val data1: String, val data2: String)

data class StartDiscovery(val device: DEVICE)

class ToTimeoutPage