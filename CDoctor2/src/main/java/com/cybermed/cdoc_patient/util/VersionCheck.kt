package com.cybermed.cdoc_patient.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.cybermed.cdoc_patient.R
import org.jsoup.Jsoup
import kotlin.concurrent.thread


class VersionCheck(val activity: Activity, val runnable: Runnable) {

    fun check() {
        thread {
            try {
                val appStoreVersion = getAppStoreVersion(activity)
                val currentVersion = activity.packageManager.getPackageInfo(activity.packageName, 0).versionName

                activity.runOnUiThread {
                    if (updateNeeded(appStoreVersion, currentVersion!!)) {
                        AlertDialog.Builder(activity)
                                .setTitle(activity.getString(R.string.force_update_title))
                                .setMessage(activity.getString(R.string.force_update_msg))
                                .setPositiveButton(activity.getString(R.string.btn_ok)) { dialog, _ ->
                                    dialog.dismiss()
                                    activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${activity.packageName}")))
                                }.show()

                    } else { // No update needed
                        runnable.run()
                    }
                }
            } catch (e: Exception) {
                Log.d("wee",""+e)
                // Webservice call failed
                // Please Check Wifi connection is already prompted
            }
        }
    }

    private fun getAppStoreVersion(context: Context): String {
        val packageName = context.packageName
        return Jsoup.connect("https://play.google.com/store/apps/details?id=$packageName&hl=en")
                .timeout(30000)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com")
                .get()
                .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                .first()
                .ownText()
    }


    private fun updateNeeded(appVersion: String, currentVersion: String): Boolean {
        try {
//            val appVersionCompare = appVersion.run { substring(0, lastIndexOf(".")) }
//            val currentVersionCompare = currentVersion.run { substring(0, lastIndexOf(".")) }

            return appVersion > currentVersion
        } catch (e: Exception) {
            return false
        }
    }
}