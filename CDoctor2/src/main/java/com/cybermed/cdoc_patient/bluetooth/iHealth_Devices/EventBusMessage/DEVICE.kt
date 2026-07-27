package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage

val SCALE_DEVICE_NAME = "HS4S"
val GLUCOMETER_DEVICE_NAME = "BG5"
val BP_DEVICE_NAME = "BP3L"
val PO_DEVICE_NAME = "PO3"
val SCALE_DEVICE_TYPE = "IChoice_Scale"
val GLUCOMETER_DEVICE_TYPE = "IChoice_Glucose"
val BP_DEVICE_TYPE = "IChoice_BP"
val PO_DEVICE_TYPE = "IChoice_Oximeter"

enum class DEVICE(val device_name: String, val device_type: String) {
    GLUCOMETER(GLUCOMETER_DEVICE_NAME, GLUCOMETER_DEVICE_TYPE),
    PULSE_OXIMETER(PO_DEVICE_NAME, PO_DEVICE_TYPE),
    BLOOD_PRESSURE_MONITOR(BP_DEVICE_NAME, BP_DEVICE_TYPE),
    SCALE(SCALE_DEVICE_NAME, SCALE_DEVICE_TYPE),
    UNKNOWN("", "")
}