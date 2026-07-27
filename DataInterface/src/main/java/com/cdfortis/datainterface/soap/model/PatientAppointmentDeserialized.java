package com.cdfortis.datainterface.soap.model;

import com.cdfortis.datainterface.JsonSerializable;

import org.json.JSONObject;

/**
 * Created by qinwe on 2017/5/5.
 */

public class PatientAppointmentDeserialized implements JsonSerializable {

    public String appt_id;
    public String appt_date;
    public String account;
    public String patient_name;
    public String appt_status;
    public String chief_complaint;
    public String room_number;
    public String reachback_phone_number;
    public String provider_code;
    public String start_time;
    public String end_time;
    public String org_code;
    public String provider_first_name;
    public String provider_last_name;
    public String provider_specialties;
    public String provider_languages;
    @Override
    public void deserialize(JSONObject jsonObject) {
        appt_id = jsonObject.optString("appt_id","");
        appt_date = jsonObject.optString("appt_date","");
        account = jsonObject.optString("account","");
        patient_name = jsonObject.optString("patient_name","");
        appt_status = jsonObject.optString("appt_status","");
        chief_complaint = jsonObject.optString("chief_complaint","");
        room_number = jsonObject.optString("room_number","");
        reachback_phone_number = jsonObject.optString("reachback_phone_number","");
        provider_code = jsonObject.optString("provider_code","");
        start_time = jsonObject.optString("start_time","");
        end_time = jsonObject.optString("end_time","");
        org_code = jsonObject.optString("org_code","");
        provider_first_name = jsonObject.optString("provider_first_name","");
        provider_last_name = jsonObject.optString("provider_last_name","");
        provider_specialties = jsonObject.optString("provider_specialties","");
        provider_languages = jsonObject.optString("provider_languages","");

    }

    @Override
    public void serialize(JSONObject jsonObject) {

    }

    public void deserialize(ProviderAvaliability providerAvaliability){
        appt_id = providerAvaliability.getProperty(0).toString();
        appt_date = providerAvaliability.getProperty(1).toString();
        //account = providerAvaliability.getProperty(2).toString();
        patient_name = providerAvaliability.getProperty(3).toString();
        //appt_status = providerAvaliability.getProperty(4).toString();
        //chief_complaint = providerAvaliability.getProperty(5).toString();
        //room_number = providerAvaliability.getProperty(6).toString();
        //reachback_phone_number = providerAvaliability.getProperty(7).toString();
        provider_code = providerAvaliability.getProperty(8).toString();
        //start_time = providerAvaliability.getProperty(9).toString();
        //end_time = providerAvaliability.getProperty(10).toString();
        room_number = providerAvaliability.getProperty(11).toString();
        reachback_phone_number = providerAvaliability.getProperty(12).toString();
        provider_code = providerAvaliability.getProperty(13).toString();
        start_time = providerAvaliability.getProperty(14).toString();
        end_time = providerAvaliability.getProperty(15).toString();

    }


    public String getAppt_id() {
        return appt_id;
    }

    public void setAppt_id(String timeslot) {
        this.appt_id = appt_id;
    }

    public String getAppt_date() {
        return appt_date;
    }

    public void setAppt_date(String appt_date) {
        this.appt_date = appt_date;
    }

    public String getMax_appts() {
        return account;
    }

    public void setMax_appts(String account) {
        this.account = account;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public String getAppt_status() {
        return appt_status;
    }

    public void setAppt_status(String appt_status) {
        this.appt_status = appt_status;
    }

    public String getChief_complaint() {
        return chief_complaint;
    }

    public void setChief_complaint(String chief_complaint) {this.chief_complaint = chief_complaint;}

    public String getReachback_phone_number(){ return reachback_phone_number;}

    public void setReachback_phone_number(String reachback_phone_number){this.reachback_phone_number = reachback_phone_number;}

    public String getProvider_code(){ return provider_code;}

    public void setProvider_code(String provider_code){this.provider_code = provider_code;}

    public String getStart_time(){ return start_time;}

    public void setStart_time(String start_time){this.start_time = start_time;}

    public String getEnd_time(){ return end_time;}

    public void setEnd_time(String end_time){this.end_time = end_time;}

    public String getOrg_code(){ return org_code;}

    public void setOrg_code(String org_code){this.org_code = org_code;}

    public String getProvider_first_name(){ return provider_first_name;}

    public void setProvider_first_name(String provider_first_name){this.provider_first_name = provider_first_name;}

    public String getProvider_last_name(){ return provider_last_name;}

    public void setProvider_last_name(String provider_last_name){this.provider_last_name = provider_last_name;}

    public String getProvider_specialties(){ return provider_specialties;}

    public void setProvider_specialties(String provider_specialties){this.provider_specialties = provider_specialties;}

    public String getProvider_languages(){ return provider_languages;}

    public void setProvider_languages(String provider_languages){this.provider_languages = provider_languages;}

}
