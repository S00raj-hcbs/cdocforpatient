package com.cybermed.cdoc_patient.main


import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.viewpager.widget.ViewPager
import com.cybermed.cdoc_patient.R
import com.cybermed.cdoc_patient.common.CDoctor2Application
import okio.JvmStatic
import java.util.*

/**
 * doctor booking data binding adapter
 */
class HomePageBindingAdapter {
    companion object {
        /**
         * set view pager
         */
        @JvmStatic
        @BindingAdapter("viewPager")
        fun ViewPager.viewPager(vm: MainActVm?) {
            try {
                val padding = resources.getDimensionPixelOffset(R.dimen._30sdp)
                setPadding(10, 0, 10, 0)
                setClipToPadding(false)

                var viewPagerAdapter = HomePagerAdapter(context)
                setAdapter(viewPagerAdapter)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * set user name at toolbar
         */
        @JvmStatic
        @BindingAdapter("toolBarText")
        fun TextView.toolBarText(vm: MainActVm?) {
            try {
                var titleText: String = this.context.getString(R.string.main_page_title).toString() +
                        CDoctor2Application.getLoginInfo().userInfo.firstName + " " + CDoctor2Application.getLoginInfo().userInfo.lastname
                if (CDoctor2Application.getLoginInfo().isAuthRep) {
                    titleText += "(Rep)"
                }
                setText(titleText)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * set user name at toolbar
         */
        @JvmStatic
        @BindingAdapter("setProfile")
        fun ImageView.setProfile(vm: MainActVm?) {
            try {
                if (CDoctor2Application.getLoginInfo().userInfo.sex == "M") {
                    setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.ic_doc))
                } else {
                    setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.user_girl))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * set fragment container height at runtime
         */
        @JvmStatic
        @BindingAdapter("setHeight")
        fun FrameLayout.setHeight(list: Float) {
            try {
                setLayoutParams(RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        list.toInt()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}