package com.cybermed.cdoc_patient.doctor.docDetail.model

import androidx.annotation.FontRes
import com.cybermed.cdoc_patient.R

/**
 * enum for fonttype  databinding
 */
enum class FontTypes (@FontRes val fontRes: Int) {
   BOLD(R.font.roboto_bold),
    MEDIUM(R.font.roboto_medium)
}