package com.cybermed.cdoc_patient.util

import com.cybermed.cdoc_patient.appointment.ApptStatus
import org.threeten.bp.LocalDate


class FilterRequest(val apptStatus: ApptStatus, val startDate: LocalDate, val endDate: LocalDate)

class PatientHistoryRefreshCompleted