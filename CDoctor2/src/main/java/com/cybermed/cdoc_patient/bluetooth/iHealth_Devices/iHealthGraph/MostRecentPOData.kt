package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph

import com.cdfortis.datainterface.soap.model.Monitor_BO
import com.cdfortis.datainterface.soap.model.Monitor_HR
import java.util.*

data class MostRecentPOData(val monitor_hrVector: Vector<Monitor_HR>?,val monitor_boVector: Vector<Monitor_BO>?)