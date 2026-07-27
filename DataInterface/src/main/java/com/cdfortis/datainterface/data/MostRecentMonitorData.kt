package com.cdfortis.datainterface.data

import com.cdfortis.datainterface.soap.model.*
import java.util.*

data class MostRecentMonitorData(val monitor_hrVector: Vector<Monitor_HR>,
                                 val monitor_weightVector: Vector<Monitor_Weight>,
                                 val monitor_boVector: Vector<Monitor_BO>,
                                 val monitor_bpVector: Vector<Monitor_BP>,
                                 val monitor_glucoseVector: Vector<Monitor_Glucose>,
                                 val monitor_STEMOVector: Vector<Monitor_STEMO>)