package com.cdfortis.datainterface.soap;

import android.os.AsyncTask;

public enum WebServiceID {

    //addWebservice IN ALPHABETICAL ORDER!

    Hello(),
    hello_edward(),
    call_all_online_providers("user_id", "room_number", "platform"),
    count_online_provider("user_id"),
    get_Pat_Vitals_v2("user_id"),
    get_patient_health_records("record_type", "org_code", "user_id"),
    get_patient_health_records_v2("record_type", "org_code", "user_id"),
    get_patient_visit_record("user_id"),
    get_patient_medical_records("record_type", "org_code", "account"),
    get_provider_apptlist("org_code", "provider_code", "date_to_search", "location"),
    Get_Provider_Charge_Mode("org_code", "provider_code"),
    Get_Provider_Payment_Options("org_code", "provider_code"),
    get_provider_review_v2("org_code", "provider_code","startIndex","endIndex"),
    get_provider_schedule_hour_From_EMR("org_code", "provider_code"),
    get_provider_schedule_hour_From_EMR_v2("org_code", "provider_code", "selected_date"),
    getProviderList_V4("range_start", "range_end", "state_filter_value"
            , "language_filter_value", "specialty_filter_value", "user_id", "platform","is_support"),
    get_all_online_support_providers(),
    generate_OnlineRoomNumber(),
    getProvidersProfileInfo_Android("org_code", "provider_code"),
    Mark_Provider_as_favorite("user_id", "org_code", "provider_code", "Set_as_favorite"),
    random_choose_online_provider("user_id"),
    ResetUserPasswordInPortal("user_id", "new_password"),
    save_pat_vitals("user_id", "entry_user_id", "org_code", "account", "chiefCompliant", "medicalHistory",
            "socialHistory", "allergies", "temperature", "heart_rate",
            "weight", "height", "BPH", "BPL", "spo2"),
    update_patient_default_state("user_id", "default_state"),
    UpdateUserProfile("user_id", "email", "password", "first_name", "mi", "last_name", "sex", "DOB", "addr1", "addr2", "city", "state", "zip", "phone_number"),
    Upload_Patient_Consent("user_id", "type", "service_code", "base64"),
    //addWebserviceEnd

    /*Newly Added Webservice*/
    Add_Update_family_member("user_id", "family_member_user_id", "relationship"),
    cancel_appointment("org_code", "provider_code", "appt_id"),
    CancelCall2Provider_Android("org_code", "provider_code", "room_number"),
    Check_Duplicate_Email_Address("email"),
    check_patient_service_code("service_code"),
    Check_Timeslot_Availablity("org_code", "pro_code", "officeloc", "apptdate", "timeslot"),
    CheckReceiverStatus("roomName"),
    create_appointment_on_EMR_Android_v2("room_number", "user_id", "org_code", "provider_code", "apptDate"),
    create_Call_Log_Rooms_Android("room_number", "receiver_id", "caller_id", "org_code"),
    create_Call_Log_Rooms_v2("room_number", "appt_id", "receiver_id", "caller_id", "org_code"),
    CreateNewUser_Android_v2("email", "password", "first_name", "mi", "last_name", "sex", "DOB", "addr1", "addr2", "city", "state", "zip", "phone_number", "AccessCode", "mobile_mode"),
    Delete_CCInfo("cc_idx"),
    developer_debug_log("user_id", "debug_message", "entry_date"),
    get_active_guests_count_noByref("room_number", "my_room_guest_id"),
    get_CCInfo("user_id"),
    get_cybermed_code_from_mac_address("mac_address"),
    Get_Full_PayorList("pat_state"),
    get_pat_apptHistory_v2("user_id", "date_to_search", "futureOrpast"),
    get_pat_apptHistory_v3("platform", "user_id", "date_to_search", "futureOrpast"),
    get_patient_family_member_list("user_id"),
    Get_Patient_Favorite_Pharmacy_List("user_id"),
    Get_Patient_Insurances("user_id"),
    //get_patient_IoT_device_list("user_id"),
    get_patient_IoT_device_list_V2("user_id"),
    get_patient_IoT_mode("user_id"),
    get_patient_onesignal_indicator("user_id", "room_number"),
    Get_Patient_PayPal_AccountList("user_id"),
    get_patient_user_id_by_mac_address("mac_address"),
    get_PatientDemographic_Android("user_id"),
    get_provider_onesignal_indicator("org_code", "provider_code", "room_number"),
    GetOnlineProviderName("user_id"),
    GetOnlineProviderName_v2("user_id"),
    GetOnlineRoomNumber_Patient("user_id"),
    getPatientCallLog_Android_v3("range_start", "range_end", "user_id", "date_to_search"),
    getPatientOnlineStatus_V2("user_id"),
    getProviderInfo("org_code", "provider_code"),
    getProviderList_V2("range_start", "range_end", "user_id", "state_filter_value", "language_filter_value", "specialty_filter_value", "platform"),
    getProviderOnlineStatus("provider_code", "org_code"),
    getProviderWaitingRoomPatNumber_From_EMR("platform", "appt_id", "org_code", "provider_code"),
    HangupCall_v2("room_number", "room_guest_id"),
    HelloWorld(),
    leaving_Room_as_Guest_v2("room_number", "room_guest_id", "device_id"),
    MakeCall2Patient_v3("org_code", "provider_code", "user_id", "room_number", "platform"),
    MakeCall2Provider_v2("org_code", "provider_code", "user_id", "room_number", "platform"),
    Mark_appointment_status("org_code", "room_number", "appt_status"),
    Mark_Appt_Payment_Method("org_code", "appt_id", "Payment_Method"),
    Notify_Patient("recipient_email", "msg_to_notify"),
    notify_patient_app_devices_v2("user_id", "msg_to_send", "room_number", "sound_filename", "platform"),
    Notify_Provider_v2("org_code", "provider_code", "msg_to_notify", "msg_to_notify_app"),
    RecordConsentAccepted_Andriod("user_id"),
    RecoverUserPassword("user_id"),
    refresh_accesstoken("user_id"),
    register_as_Room_Guest_v3("room_number", "guest_type", "guest_org_code", "guest_id", "latitude", "longitude", "device_id"),
    register_patient_IoT_device("user_id", "device_type", "device_mac_address"),
    register_patient_IoT_device_V2("user_id", "device_type", "device_mac_address", "to_delete", "device_model"),
    remove_family_member("user_id", "family_member_user_id"),
    remove_patient_IoT_device("user_id", "device_type", "device_mac_address"),
    retrieve_patient_routine_default_message("user_id"),
    save_patient_insurance_v2("user_id", "pat_insurance_sequence", "company_code", "insurance_id", "pat_relation_to_insured", "insured_first_name", "insured_last_name", "insured_dob", "insurance_image"),
    save_patient_routine_default_message("user_id", "wakeup_time", "breakfast_time", "lunch_time", "dinner_time", "bed_time"),
    Send_Patient_Activation_Email("user_id"),
    send_patient_vital_data("type", "value", "timestamp", "device_mac_address", "hub_mac_address", "measureTimeFormatted", "measureTime", "bpm"),
    send_patient_vital_data_message("idx", "msg"),
    set_appt_vital_intake_v4("org_code", "appt_id", "chief_complaint", "temperature", "pulse", "BPH", "BPL", "height", "weight", "smoke_status_code", "MedHx", "SocialHx", "Allergies", "LDN_Initial", "LDN_Refill", "reachback_phone_number"),
    set_device_app_version("user_id", "device_user_name", "device_type", "device_mac_address", "app_version"),
    set_device_battery_level("user_id", "device_user_name", "device_type", "device_mac_address", "device_status", "battery_level"),
    set_device_location("user_id", "device_user_name", "device_type", "device_mac_address", "location"),
    Set_Patient_Favorite_Pharmacy("user_id", "pharmacy_name", "longitude", "latitude", "address1", "address2", "city", "state", "country", "zip_code", "type", "Set_as_favorite"),
    set_patient_IoT_device_log("user_id", "device_type", "device_mac_address", "device_id"),
    set_patient_onesignal_indicator("user_id", "onesignal_indicator", "delivery_date"),
    set_provider_review("org_code", "provider_code", "user_id", "rating", "comment"),
    set_room_charge_creditcard("room_number", "appt_id", "org_code", "cc_idx", "cvv_code"),
    setPatientDeviceStatus_Android("user_id", "online_status", "device_id"),
    setPatientOnlineRoom("user_id", "online_status", "online_room"),
    setPatientOnlineStatus("user_id", "online_status", "vendor_device_id_string"),
    setPatientOnlineStatus_V5("user_id", "online_status", "vendor_device_id_string", "os_platform", "ws_version", "os_version", "is_camera_enabled", "is_microphone_enabled", "is_notification_enabled", "device_model", "app_version", "time_zone"),
    Update_CCInfo("user_id", "card_no", "name_on_card", "card_exp_date", "cc_idx"),
    update_patient_service_code("user_id", "new_service_code"),
    update_PayPal_Client_Metadata_Id("user_id", "PayPal_Client_Metadata_Id"),
    verifyPatientLogin_Android("user_id", "pwd", "player_id"),
    Verify_CC("cc_idx", "cvv_code"),
    verify_patient_insurance("org_code", "pat_last_name", "pat_first_name", "pat_gender", "pat_dob", "payor_name", "payor_id", "insurance_id", "subscriber_is_patient", "insured_last_name", "insured_first_name", "insured_gender", "insured_dob", "dos", "copay", "deductible"),
    verify_paypal_user("authorization_code", "user_id", "paypal_id", "PayPal_Client_Metadata_Id"),
    save_patient_imagelist_V1("userID", "appt_id", "org_code", "files"),
    get_appt_id_by_room_number("room_number"),
    get_promotion_code_by_service_code("service_code"),
    get_consent_form_by_service_code("service_code"),
    book_appointment("room_number", "user_id", "org_code", "provider_code", "apptDate"),
    create_auth_link("user_id", "auth_rep_user_id", "auth_rep_pwd"),
    get_rep_patients("user_id"),
    delete_auth_link("user_id", "auth_rep_user_id"),
    get_user_id_by_patient_info("first_name","last_name", "dob", "gender", "zip_code"),
    GetProvidersNextAvailabilityByUserID("user_id"),
    get_patient_lab_report("service_code","accession_id"),
    get_patient_referral("service_code","referral_id"),
    //view document
    get_patient_imagelist_V1("userID", "appt_id", "start_date", "end_date");


    private AsyncTask asyncTask;
    private String[] properties;
    private DoInBackground background;
    private OnPostExecute postExecute;
    private boolean disableNullRestriction = false;


    WebServiceID() {
    }

    WebServiceID(String... properties) {
        this.properties = properties;
    }

    public void setDisableNullRestriction(boolean disableNullRestriction) {
        this.disableNullRestriction = disableNullRestriction;
    }

    public boolean getDisableNullRestriction() {
        return disableNullRestriction;
    }

    public void setBackground(DoInBackground background) {
        this.background = background;
    }

    public void setPostExecute(OnPostExecute postExecute) {
        this.postExecute = postExecute;
    }

    public void setAsyncTask(AsyncTask asyncTask) {
        this.asyncTask = asyncTask;
    }

    public DoInBackground getBackground() {
        return this.background;
    }

    public OnPostExecute getPostExecute() {
        return this.postExecute;
    }

    public AsyncTask getAsyncTask() {
        return this.asyncTask;
    }

    public String[] getProperties() {
        return this.properties;
    }
}

