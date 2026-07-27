package com.cybermed.cdoc_patient.util

import com.cybermed.cdoc_patient.modal.GeneralConsentForm
import com.cybermed.cdoc_patient.modal.RPMConsentForm
import com.cybermed.cdoc_patient.modal.RPMConsentForm_Maysam
import com.cybermed.cdoc_patient.modal.RPMConsentForm_ThirdParty
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Chunk
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph


fun Paragraph.toText(): String {
    val sb = StringBuilder()

    for (text in this.iterator()) {
        sb.append(text.toString())
    }

    return sb.toString()
}

private fun defaultChunk(text: String, font: Font.FontFamily = Font.FontFamily.TIMES_ROMAN,
                         size: Float = 20.0f, style: Int = Font.NORMAL, baseColor: BaseColor = BaseColor.BLACK): Chunk {
    return Chunk(text, Font(font, size, style, baseColor))
}

fun RPMConsentFormParagraph(rpmConsentForm: RPMConsentForm): Paragraph {

    val device_number = rpmConsentForm.device_number
    val doctor_name = rpmConsentForm.doctor_name

    val consent_form = """
        I understand that:
        
        •   I am the only person who should be using the remote monitoring equipment as instructed. I will not use the device for reasons other than my own personal health monitoring. I understand that I can only participate in this program with %RED%(One Medical Provider) at a time. 
        
        •   I will not tamper with the equipment. I understand that I am responsible for any fees associated with misuse of the equipment. 
        
        •   I understand the devices are only designed for the %RED%(RPM) program. 
        
        •   I acknowledge that I received monitoring device serial # : %RED%($device_number) 
        
        •   The device is meant to collect vital readings and transfer those readings to an online website. It is %RED%(NOT AN EMERGENCY RESPONSE UNIT AND IS NOT MONITORED 24/7). Call 911 for immediate medical emergencies. 
        
        •   I am aware my daily readings will be transmitted from the monitor to a website located at www.myhealthconnected.net in a safe and secure manner. I can withdraw my consent to participate in this program, and revoke service at any time by returning the monitoring devices. %RED%($doctor_name) will securely and confidentially store my collected data, and record and store my readings into my Electronic Medical Record monthly. 
        
        •   I will do my best to take my reading every day. I am aware that a Remote Patient Monitoring Qualified Health Professional will only view my readings %RED%(every 30 days), and that this program is %RED%(NOT) a 24/7 Monitoring Service. I will be contacted every 30 days, by phone, to review and discuss my results and progress.
    """.trimIndent()


    return ColoredPDFParagraph(consent_form)
}

fun RPMConsentFormText(rpmConsentForm: RPMConsentForm): String {
    return RPMConsentFormParagraph(rpmConsentForm).toText()
}

// signature 2
fun GeneralConsentFormParagraph(): Paragraph { // to paragraph = signature 2

    val consent_form = """
        CyberMed Health Inc, also known as CDoc, are providing you, the patient, with an easy way to reach a provider in your medical group through the use of telemedicine. Telemedicine involves a voice and video experience with the physician. 
        The purpose of this form is to obtain your approval to permit this type of audio/video call.
        When you use any CyberMed Health Inc (“CDOC”) Service, or send e‐mails, text messages, and other communications from your desktop or mobile device to us, you are communicating with us electronically. You consent to receive communications from us electronically. You agree that (a) all agreements and consents can be signed electronically and (b) all notices, disclosures, and other communications that we provide to you electronically satisfy any legal requirement that such notices and other communications be in writing. 
        
        When you register for this program, we will need to obtain your email address and phone number. CDoc commits to keeping this information confidential and will not use it, or share it with anyone outside CyberMed Health Inc.
        """.trimIndent()


    return ColoredPDFParagraph(consent_form)
}

fun GeneralConsentFormText(rpmConsentForm: GeneralConsentForm): String { // to text string = signature 2
    return GeneralConsentFormParagraph().toText() // TODO String -> Spannable (colored text-field)
}

fun RPMConsentFormMaysamParagraph(rpmConsentForm: RPMConsentForm_Maysam): Paragraph {

    val consent_form = """
        I, ${rpmConsentForm.patient_name} hereby consent, acknowledge and agree to designate %RED%(Variety Pharmacy LLC), to fill and deliver my medications to me along with my Remote Patient Monitoring (RPM) Devices and supplies.  In consideration of the foregoing Variety Pharmacy LLC, will not charge any shipping costs for providing these services. 

        Should you have any questions or concerns, feel free to call us at anytime at %RED%(516-342-1156)

    """.trimIndent()


    return ColoredPDFParagraph(consent_form)
}

fun RPMThirdPartyConsentFormParagraph(rpmConsentForm: RPMConsentForm_ThirdParty, consentForm: String): Paragraph {
    val addLineBreak = splitStringToLines(consentForm);
    val replacedForm = addLineBreak.replace("patient_name", rpmConsentForm.patient_name);
    return ColoredPDFParagraph(replacedForm)
}

fun splitStringToLines(string: String) : String {
//    val strArr = string.split("\\n\\n");
////    var concatStr = "";
////    for(str in strArr) {
////        concatStr += str + System.lineSeparator();
////    }
////    return concatStr;
    return string.replace("\\n", System.lineSeparator())
}

fun RPMConsentFormMaysamText(rpmConsentForm: RPMConsentForm_Maysam): String {
    return RPMConsentFormMaysamParagraph(rpmConsentForm).toText()
}

fun ParagraphToText(paragraph: Paragraph): String {
    return paragraph.toText()
}

private fun ColoredPDFParagraph(consent_form: String): Paragraph {

    val regex = "%(\\w+)%\\((.*?)\\)".toRegex()


    val matchResults = regex.findAll(consent_form).iterator()

    var startIdx = 0
    val chunks = mutableListOf<Chunk>()

    while (matchResults.hasNext()) {
        val result = matchResults.next()
        val range = result.range

        val blackString = consent_form.substring(startIdx, range.first)
        chunks.add(defaultChunk(blackString))
        startIdx = range.last + 1

        val color = color_map.getOrElse(result.groupValues[1]) { BaseColor.BLACK }
        val text = result.groupValues[2]

        chunks.add(defaultChunk(text = text, baseColor = color))
    }

    val blackString = consent_form.substring(startIdx)
    chunks.add(defaultChunk(blackString))

    val paragraph = Paragraph().apply {
        chunks.forEach {
            add(it)
        }
    }

    return paragraph
}

val color_map = mapOf<String, BaseColor>("RED" to BaseColor.RED)