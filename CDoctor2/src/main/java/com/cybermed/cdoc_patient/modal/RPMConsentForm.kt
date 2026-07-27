package com.cybermed.cdoc_patient.modal

data class RPMConsentForm(val title: String, val device_number: String, val doctor_name: String)

data class RPMConsentForm_Maysam(val title: String, val patient_name: String)

data class RPMConsentForm_ThirdParty(val title: String, val patient_name: String)

// parametrize the consent form for any need
data class GeneralConsentForm(val title: String) // only title needed, nothing else need to parameterize (passing data to change displayed string)