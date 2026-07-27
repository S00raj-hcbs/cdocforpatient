package com.cybermed.cdoc_patient.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.cybermed.cdoc_patient.R
import com.cybermed.cdoc_patient.Tablet_Mode.WelcomeActivityTablet.getWIFIMacAddr
import com.cybermed.cdoc_patient.common.CDoctor2Application
import okio.JvmStatic
import java.io.PrintWriter
import java.io.StringWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object ErrorMessage {
    @JvmStatic
    fun alertDialog(
        context: Context,
        title: CharSequence?,
        message: CharSequence?,
        okBtnCallBack: OkBtnCallBack?
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_my_alert)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        val tvTitle = dialog.findViewById<View>(R.id.tv_title) as TextView
        val tvContent = dialog.findViewById<View>(R.id.tv_content) as TextView
        var leftBtn = dialog.findViewById<Button>(R.id.tv_left)
        val rightBtn = dialog.findViewById<Button>(R.id.tv_right)
        rightBtn.visibility = View.VISIBLE
        rightBtn.setText(context.getString(R.string.btn_ok))
        rightBtn.setOnClickListener {
            dialog.dismiss()
            if (okBtnCallBack != null) {
                okBtnCallBack.callback()
            }
        }
        // val dialog = AlertDialog.Builder(context).create()
        if (!TextUtils.isEmpty(title)) {
            tvTitle.visibility = View.VISIBLE
            tvTitle.setText(title)
        }
        if (!TextUtils.isEmpty(message))
            tvContent.setText(message)
        dialog.show()
    }

    interface OkBtnCallBack {
        fun callback()
    }

    @JvmStatic
    fun produceUnhandleExceptionError(e: Throwable, context: Context): Array<String> {
        val userID = CDoctor2Application.getLoginInfo().account
        val currentVersion =
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        val mac = getWIFIMacAddr()

        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)

        val df: DateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.US)
        val timestamp = df.format(Calendar.getInstance().time)

        return arrayOf(userID, "VN: $currentVersion Mac: $mac St: $sw", timestamp)
    }

}