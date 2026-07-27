package com.cybermed.cdoc_patient.util;

import android.graphics.Color;
import android.widget.TextView;

import java.util.Locale;

public interface AppConstant {
    //blood pressure iot device list
    String KEY_MAX_BP = "max_bp";
    String KEY_MIN_BP = "min_bp";
    String KEY_HEIGHT = "height";
    String KEY_WEIGHT = "weight";
    String KEY_BMI = "bmi";

    String ONLINE_STATUS = "online_status";
    String PROVIDER_CODE = "provider_code";

    //signalr codes
    String SIGNALR_ONLINE = "signal_r_online";
    String SIGNALR_LOGOUT = "signalr_logout";
    String RECEIVE_LOGOUT = "receive_logout";

    //medication page
    String MEDICATION_TAKE = "_take";
    String MEDICATION_REMINDER = "reminder";
    String NOTIFY_LOGOUT = "notify_logout";

    //smart watch keys
    String WATCH_TYPE = "watch_type";
    String DEVICE_LIST_DATA = "device_list_data";
    String SMART_WATCH = "smart_watch";
    String APPLE_HEALTH = "apple_health";
    String SMART_TEMP = "smart_temp";
    String SMART_BO = "smart_bp";
    String SMART_HRV = "smart_hrv";
    String SMART_DAILY = "smart_daily";
    String KEY_SMARTW = "key_smart";
    String VAL_GET = "val_get";
    String VAL_SET = "val_set";
    String SMART_MAC = "smart_mac";
    int REQUEST_CODE_ADD_DEVICE = 1;
    int REQUEST_CODE_SMART_DATA = 1;
    String STEPS = "steps";
    String DISTANCE = "distance";
    String CALORIES = "calories";
    String BLOOD_PRESSURE = "blood_pressure";
    String BLOOD_OXYGEN = "blood_oxygen";
    String TEMP = "temp";
    String HEART_RATE = "heart_rate";
    //check app first time launch
    String isFirstLaunch = "isfirstlaunch";

    //home page
    String FROM_SEARCH = "from_search";
    String SELECTED_TAB = "selected_tab";
    int REQUEST_HOME_VIDEOCALL = 1212;
    int REQUEST_APPT_VIDEOCALL = 1313;

    //doctor booking flow keys
    String APPT_LIST = "appt_list";
    String DOC_INFO = "doc_object";
    String KEY_SEARCH = "key_search";
    String KEY_HOME = "key_home";
    String KEY_APPT_TYPE = "appt_type";
    String KEY_PAGE_TYPE = "page_type";
    String KEY_IS_RESCHEDULE = "is_reschedule";
    String KEY_IS_CHIEF_COMPLAIN = "is_reson";
    String KEY_IS_CHIEF_NOTES = "is_notes";
    String KEY_APPTID = "appt_id";
    String PAGE_SEARCH = "search_page";
    String PAGE_DOC_LIST = "doc_list";
    String PAGE_HOME = "home_page";
    String PAGE_APPT = "appt_page";
    String KEY_PAGE = "key_page";
    String APPT_DATE_FORMAT = "MM/dd/yyyy hh:mm a";
    String APPT_DATE_TIME_FORMAT = "MM/dd/yyyy hh:mm:ss a";
    String DATE_FORMAT = "MMM dd, yyy";

    String DATE_FORMAT2 = "MMM dd, yyyy";
    String DATE_TIME_FORMAT = "MMM dd yyy, hh:mm a";
    String DATE_TIME_FORMAT2 = "MM/dd hh:mm a";
    String DATE_TIME_FORMAT3 = "EEE MMM dd HH:mm:ss Z yyyy";
    String DATE_TIME_FORMAT5 = "EEE MMM dd yyyy";
    String DATE_TIME_FORMAT4 = "yyyy-MM-dd";
    String TIME_FORMAT = "hh:mm a";

    int REQUEST_IMAGE_SELECTION = 12121;
    String KEY_IMAGE_LIST = "key_upload_list";
    //doctor paid status
    int FEE_SCHEDULE = 3;
    int PAID_BY_VISIT = 4;
    int FREE_PROVIDER = 5;

    //profile part
    String DATE_FORMAT_PROFILE = "MM/dd/yyy";
    String SERVER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    String IS_FROM_HEALTH_RECORD = "is_from_health_record";
    String IShome = "is_home";
    String DEVICE_TYPE="iot_device_type";
    String IOT_GRAPH_IOT="iotgraph_to_gtaph";
    String FROM_USER="from_user";


    static String getdataBPtextColor(Double systolic, Double diastolic, TextView textView) {
        if (systolic < 90 || diastolic < 60) {
            textView.setTextColor(Color.BLUE);
            return String.format(Locale.ENGLISH, "%.1f", systolic)+"/"+String.format(Locale.ENGLISH, "%.1f", diastolic);
        } else if (systolic <= 120 && diastolic <= 80) {
            textView.setTextColor(Color.parseColor("#34c85a"));
            return String.format(Locale.ENGLISH, "%.1f", systolic)+"/"+String.format(Locale.ENGLISH, "%.1f", diastolic);
        } else if (systolic <= 129 && diastolic < 80) {
            textView.setTextColor(Color.parseColor("#ff9501"));
            return String.format(Locale.ENGLISH, "%.1f", systolic)+"/"+String.format(Locale.ENGLISH, "%.1f", diastolic);
        } else if ((systolic <= 139 && diastolic <= 89) || (systolic >= 130 && diastolic >= 80)) {
            textView.setTextColor(Color.RED);
            return String.format(Locale.ENGLISH, "%.1f", systolic)+"/"+String.format(Locale.ENGLISH, "%.1f", diastolic);
        } else if (systolic <= 180 && diastolic <= 120) {
            textView.setTextColor(Color.RED);
            return String.format(Locale.ENGLISH, "%.1f", systolic)+"/"+String.format(Locale.ENGLISH, "%.1f", diastolic);
        } else if (systolic > 180 || diastolic > 120) {
            textView.setTextColor(Color.MAGENTA);
            return String.format(Locale.ENGLISH, "%.1f", systolic)+"/"+String.format(Locale.ENGLISH, "%.1f", diastolic);
        }else {
            textView.setTextColor(Color.GRAY);
            return "--";
        }
    }

    static String getdataHRtextColor(Double heartRate, TextView textView) {
        if (heartRate < 60) {
            textView.setTextColor(Color.BLUE);
            return String.format(Locale.ENGLISH, "%.1f", heartRate);
        } else if (heartRate >= 60 && heartRate <= 100) {
            textView.setTextColor(Color.parseColor("#34c85a"));
            return String.format(Locale.ENGLISH, "%.1f", heartRate);
        } else if (heartRate > 100) {
            textView.setTextColor(Color.RED);
            return String.format(Locale.ENGLISH, "%.1f", heartRate);
        }else {
            textView.setTextColor(Color.GRAY);
            return "--";
        }
    }

    static String getdataGlucosetextColor(Double glucose, TextView textView) {
        if (glucose < 70) {
            textView.setTextColor(Color.BLUE);
            return String.format(Locale.ENGLISH, "%.1f", glucose);
        } else if (glucose <= 140) {
            textView.setTextColor(Color.parseColor("#34c85a"));
            return String.format(Locale.ENGLISH, "%.1f", glucose);
        } else if (glucose <= 199) {
            textView.setTextColor(Color.parseColor("#FFC107"));
            return String.format(Locale.ENGLISH, "%.1f", glucose);
        }else if (glucose > 199) {
            textView.setTextColor(Color.RED);
            return String.format(Locale.ENGLISH, "%.1f", glucose);
        }else {
            textView.setTextColor(Color.GRAY);
            return "--";
        }
    }
    static String getdataOxitextColor(Double spo2Level, TextView textView) {
        if (spo2Level < 90) {
            textView.setTextColor(Color.RED);
            return String.format(Locale.ENGLISH, "%.1f", spo2Level);
        } else if (spo2Level <= 94) {
            textView.setTextColor(Color.parseColor("#FFC107")); // Orange
            return String.format(Locale.ENGLISH, "%.1f", spo2Level);
        } else if (spo2Level <= 100) {
            textView.setTextColor(Color.parseColor("#34c85a"));
            return String.format(Locale.ENGLISH, "%.1f", spo2Level);
        } else {
            textView.setTextColor(Color.GRAY);
            return "--";
        }
    }


    static String getTemperatureColorInFahrenheit(Double temperatureF,TextView textView) {
        if (temperatureF < 96.8) {
            textView.setTextColor(Color.RED);
            return String.format(Locale.ENGLISH, "%.1f", temperatureF);
        } else if (temperatureF >= 96.8 && temperatureF <= 99.5) {
            textView.setTextColor(Color.parseColor("#34c85a"));
            return String.format(Locale.ENGLISH, "%.1f", temperatureF);
        } else if (temperatureF > 99.5 && temperatureF <= 100.4) {
            textView.setTextColor(Color.parseColor("#ff9501"));
            return String.format(Locale.ENGLISH, "%.1f", temperatureF);
        } else if (temperatureF > 100.4 && temperatureF <= 102.2) {
            textView.setTextColor(Color.MAGENTA);
            return String.format(Locale.ENGLISH, "%.1f", temperatureF);
        } else if (temperatureF > 102.2 && temperatureF <= 104.0) {
            textView.setTextColor(Color.RED);
            return String.format(Locale.ENGLISH, "%.1f", temperatureF);
        } else if (temperatureF > 104.0) {
            textView.setTextColor(Color.RED);
            return String.format(Locale.ENGLISH, "%.1f", temperatureF);
        }else {
              textView.setTextColor(Color.GRAY);
            return "--";
        }
    }


    static String getHeadCircumferenceColor(Double headCircumference,TextView textView) {
        if (headCircumference < 46.0) {
            textView.setTextColor(Color.BLUE);
            return String.format(Locale.ENGLISH, "%.1f", headCircumference);
        } else if (headCircumference >= 46.0 && headCircumference <= 60.0) {
            textView.setTextColor(Color.parseColor("#34c85a"));
            return String.format(Locale.ENGLISH, "%.1f", headCircumference);
        } else if (headCircumference > 60.0) {
            textView.setTextColor(Color.RED);
            return String.format(Locale.ENGLISH, "%.1f", headCircumference);
        }else {
            textView.setTextColor(Color.GRAY);
            return "--";
        }
    }

}
