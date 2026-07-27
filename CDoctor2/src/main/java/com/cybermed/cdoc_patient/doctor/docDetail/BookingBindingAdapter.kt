package com.cybermed.cdoc_patient.doctor.docDetail


import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.text.TextUtils
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.cybermed.cdoc_patient.R
import com.cybermed.cdoc_patient.doctor.docDetail.model.DayDateModel
import com.cybermed.cdoc_patient.doctor.docDetail.model.FontTypes
import com.cybermed.cdoc_patient.doctor.searchDoctor.ResponseDocInfo
import okio.JvmStatic
import java.util.*

/**
 * doctor booking data binding adapter
 */
class BookingBindingAdapter {
    companion object {
        /**
         * set font
         */
        @JvmStatic
        @BindingAdapter("font")
        fun TextView.font(type: FontTypes) {
            try {
                typeface = ResourcesCompat.getFont(context, type.fontRes)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * set month on booking page
         */
        @JvmStatic
        @BindingAdapter("monthText")
        fun TextView.monthText(list: ArrayList<DayDateModel>) {
            try {
                if (list != null)
                    for (item: DayDateModel in list) {
                        if (item.isSelected) {
                            setText(item.monthString+", "+item.year)
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * set doctor online status
         */
        @JvmStatic
        @BindingAdapter("onlineStatus")
        fun ImageView.onlineStatus(status: String?) {
            try {
                if (!TextUtils.isEmpty(status))
                    when (status) {
                        "1" -> {
                            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_green_online))
                        }
                        "0" -> {
                            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_grey_busy))
                        }
                        "2" -> setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_red_offline))
                        else -> {
                            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_green_online))
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
         /**
         * Set doctor online status with tint color change
         */
        @JvmStatic
        @BindingAdapter("onlineStatusWithTint")
        fun ImageView.setOnlineStatusWithTint(status: String?) {
            try {
                if (!TextUtils.isEmpty(status)) {
                    val tintColor = when (status) {
                        "1" -> ContextCompat.getColor(context, R.color.green_1_1)
                        "0" -> ContextCompat.getColor(context, R.color.color_8f8f8f)
                        "2" -> ContextCompat.getColor(context, R.color.color_cb544c)
                        else -> ContextCompat.getColor(context, R.color.green_1_1)
                    }
                    val originalDrawable = ContextCompat.getDrawable(context, R.drawable.bg_blue_circle)
                    originalDrawable?.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
                    setImageDrawable(originalDrawable)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        /**
         * set doctor profile image
         */
        @JvmStatic
        @BindingAdapter("profileImage")
        fun ImageView.profileImage(base64: String?) {
            try {
                if (!TextUtils.isEmpty(base64)) {
                    val decodedString = Base64.decode(base64, Base64.DEFAULT)
                    val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    if (decodedByte != null) {
                        setImageBitmap(decodedByte)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * set doctor paid charges
         */
        @JvmStatic
        @BindingAdapter("charges")
        fun TextView.charges(model: ResponseDocInfo) {
            try {
                if (TextUtils.isEmpty(model.incrementalCharge) && TextUtils.isEmpty(model.initialCharge))
                    return
                var charges: String = ""
                if (!TextUtils.isEmpty(model.initialCharge)) {
                    charges = "$ " + model.initialCharge + " for initial " + model.initialMin + " mins"
                }
                if (!TextUtils.isEmpty(model.initialCharge)) {
                    if (TextUtils.isEmpty(charges)) {
                        charges = "$ " + model.incrementalCharge + "/" + model.initialMin + " min"
                    } else {
                        charges = charges + ", $ " + model.incrementalCharge + "/" + model.initialMin + " min"
                    }
                }
                setText(charges)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        /**
         * set doctor rating
         */

        @JvmStatic
        @BindingAdapter("ratingValue")
        fun AppCompatRatingBar.setRatingValue(reviewScore: String?) {
            try {
                rating = if (!reviewScore.isNullOrEmpty()) {
                    reviewScore.toFloatOrNull() ?: 0.0f
                } else {
                    0.0f
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                rating = 0.0f
            }
        }
        /**
         * set doctor address
         */
        @JvmStatic
        @BindingAdapter("address")
        fun TextView.address(model: ResponseDocInfo?) {
            try {
                //vm.docInfo.addr1 + " " + vm.docInfo.addr2 + "\n" + vm.docInfo.city + ", " + vm.docInfo.state + " " + vm.docInfo.zip
                if (model != null) {
                    var address: String = "";
                    if (!TextUtils.isEmpty(model.addr1)) {
                        address = model.addr1
                    }
                    if (!TextUtils.isEmpty(model.addr2)) {
                        if (!TextUtils.isEmpty(address)) {
                            address = address + " " + model.addr2
                        } else address = model.addr2
                    }
                    if (!TextUtils.isEmpty(model.city)) {
                        if (!TextUtils.isEmpty(address)) {
                            address = address + model.city
                        } else address = model.city
                    }
                    if (!TextUtils.isEmpty(model.state)) {
                        if (!TextUtils.isEmpty(address)) {
                            address = address + ", " + model.state
                        } else address = model.state

                    }
                    if (!TextUtils.isEmpty(model.zip)) {
                        if (!TextUtils.isEmpty(address)) {
                            address = address + " " + model.zip
                        } else address = model.zip
                    }

                    setText(address)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}