package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Glucometer;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.WebServiceID;
import com.cdfortis.datainterface.soap.model.PatientRoutineDefaultMessage;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.util.DateUtil;
import com.cybermed.cdoc_patient.util.DateUtil.MyTime;

import org.ksoap2.serialization.SoapObject;


public class GlucoseDailyRoutine {

    public static boolean isSet = false;
    public static MyTime wakeUpTime;
    public static MyTime breakfastTime;
    public static MyTime lunchTime;
    public static MyTime dinnerTime;
    public static MyTime bedTime;

    public static void fetchData() {
        final String userId = CDoctor2Application.getLoginInfo().getAccount();
        OnPostExecute ope = result -> {
            PatientRoutineDefaultMessage routine = new PatientRoutineDefaultMessage((SoapObject) result);
            setRoutine(routine.wakeup_time, routine.breakfast_time, routine.lunch_time, routine.dinner_time, routine.bed_time);
        };
        WebService.webServiceAsyncTask(WebServiceID.retrieve_patient_routine_default_message, ope, userId);
    }

    public static void setRoutine(String wakeupTimeStr, String breakfastTimeStr, String lunchTimeStr, String dinnerTimeStr, String bedTimeStr) {
        MyTime wut = MyTime.dateToMyTime(DateUtil.routineTimeToDate(wakeupTimeStr));
        wakeUpTime = wut == null ? new MyTime(7, 0) : wut;
        MyTime bft = MyTime.dateToMyTime(DateUtil.routineTimeToDate(breakfastTimeStr));
        breakfastTime = bft == null ? new MyTime(8, 0) : bft;
        MyTime lt = MyTime.dateToMyTime(DateUtil.routineTimeToDate(lunchTimeStr));
        lunchTime = lt == null ? new MyTime(12, 0) : lt;
        MyTime dt = MyTime.dateToMyTime(DateUtil.routineTimeToDate(dinnerTimeStr));
        dinnerTime = dt == null ? new MyTime(18, 0) : dt;
        MyTime bt = MyTime.dateToMyTime(DateUtil.routineTimeToDate(bedTimeStr));
        bedTime = bt == null ? new MyTime(22, 0) : bt;
        isSet = true;
    }

    public static int getDefaultTime() {
        MyTime current = MyTime.currentTime();
        int min = Integer.MAX_VALUE;
        int wutDiff = Math.abs(current.compareTo(wakeUpTime));
        min = Math.min(wutDiff, min);
        int bftDiff = Math.abs(current.compareTo(breakfastTime));
        min = Math.min(bftDiff, min);
        int ltDiff = Math.abs(current.compareTo(lunchTime));
        min = Math.min(min, ltDiff);
        int dtDiff = Math.abs(current.compareTo(dinnerTime));
        min = Math.min(min, dtDiff);
        int btDiff = Math.abs(current.compareTo(bedTime));
        min = Math.min(min, btDiff);


        if (min == wutDiff) {
            return 0;
        } else if (min == bftDiff) {
            if (current.compareTo(breakfastTime) < 0) {
                return 1;
            } else {
                return 2;
            }
        } else if (min == ltDiff) {
            if (current.compareTo(lunchTime) < 0) {
                return 3;
            } else {
                return 4;
            }
        } else if (min == dtDiff) {
            if (current.compareTo(dinnerTime) < 0) {
                return 5;
            } else {
                return 6;
            }
        } else {
            return 7;
        }
    }
}
