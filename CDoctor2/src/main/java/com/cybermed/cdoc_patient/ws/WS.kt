package com.cybermed.cdoc_patient.ws

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.cdfortis.datainterface.soap.OnPostExecute
import com.cdfortis.datainterface.soap.WebService
import com.cdfortis.datainterface.soap.WebServiceID
import com.cybermed.cdoc_patient.Constant.Platform
import com.cybermed.cdoc_patient.common.CDoctor2Application
import okio.JvmOverloads
import okio.JvmStatic
import java.util.*

object WS {

    private val application get() = CDoctor2Application.application
    private val userId get() = CDoctor2Application.getLoginInfo()?.account
    private val oneSignalUserId get() = CDoctor2Application.getLoginInfo()?.oneSignalUserId
    private const val wsVersion = "2"
    private val currentVersion get() = application.packageManager.getPackageInfo(application.packageName, 0).versionName


    private fun String.truncate(numberOfCharacters: Int): String {
        return if (this.length <= numberOfCharacters) this else this.subSequence(0, numberOfCharacters) as String
    }

    @JvmStatic
    @JvmOverloads
    fun setPatientDeviceStatus(status: Int,
                               opeParam: OnPostExecute = OnPostExecute { },
                               webServiceID: WebServiceID = WebServiceID.setPatientOnlineStatus_V5) {

        if (status < 0 || status > 2) throw RuntimeException("Invalid status")

        val is_camera_enabled = ActivityCompat.checkSelfPermission(application, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val is_microphone_enabled = ActivityCompat.checkSelfPermission(application, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        val is_notification_enabled = NotificationManagerCompat.from(application).areNotificationsEnabled()

        // getOffset to observe daylight saving time
        val timeZoneDiff = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 60 / 60 / 1000

        WebService.webServiceAsyncTask(webServiceID, opeParam, userId, status.toString(), "OS^$oneSignalUserId", Platform.ANDROID, wsVersion, "${Build.VERSION.RELEASE} SDK:${Build.VERSION.SDK_INT}",
                is_camera_enabled.toString(), is_microphone_enabled.toString(), is_notification_enabled.toString(), "${Build.BRAND} ${Build.MODEL}".truncate(30), currentVersion, "GMT${timeZoneDiff.toString()}")
    }


    @JvmStatic
    @JvmOverloads
    fun registerBluetoothDevice(deviceType: String,
                                deviceMacAddress: String,
                                deviceModel: String,
                                opeParam: OnPostExecute = OnPostExecute { },
                                webServiceID: WebServiceID = WebServiceID.register_patient_IoT_device_V2) {

        WebService.webServiceAsyncTask(webServiceID, opeParam, userId,
                deviceType, deviceMacAddress, true.toString(), deviceModel)
    }



}