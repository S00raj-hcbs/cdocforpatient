package com.cybermed.cdoc_patient.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    private static final String OUR_TIME_FORMAT = "M/d/yyyy hh:mm:ss aa"; //the format we received in PainentAppointment.appt_date
    private static final String LOCAL_TIME_FORMAT = "M/d/yyyy"; //the format we format to a localdate
    private static final String ROUTINE_TIME_FORMAT = "HH:mm:ss";
    public final static LocalDate SMALLEST_LOCALDATE = LocalDate.of(1970, 1, 1);

    public static Date stringToDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat(OUR_TIME_FORMAT, Locale.US);
        Date ddate = null;
        try {
            ddate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ddate;
    }

    public static boolean checkSameDay(String date) {
        Date apptDate = stringToDate(date);
        if(apptDate == null) {
            return false;
        } else {
            Calendar apptCalendar = Calendar.getInstance();
            apptCalendar.setTime(apptDate);
            return currentYear() == apptCalendar.get(Calendar.YEAR)
                    && currentMonth() == apptCalendar.get(Calendar.MONTH)
                    && currentDay() == apptCalendar.get(Calendar.DAY_OF_MONTH);
        }
    }

    public static int currentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int currentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    public static int currentDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static LocalDate stringToLocalDate(String date) {
        String[] sarr = date.split(" ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LOCAL_TIME_FORMAT);
        LocalDate localDate = LocalDate.parse(sarr[0], formatter);
        return localDate;
    }

    public static Date routineTimeToDate(String time) {
        if(time == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(ROUTINE_TIME_FORMAT);
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static class MyTime implements Comparable<MyTime> {
        public int hourOfDay;
        public int minute;

        public MyTime(int hourOfDay, int minute) {
            this.hourOfDay = hourOfDay;
            this.minute = minute;
        }

        public void setTime(int hour, int min) {
            hourOfDay = hour;
            minute = min;
        }

        @Override
        public int compareTo(MyTime another) {
            int thisVal = this.hourOfDay * 60 + this.minute;
            int otherVal = another.hourOfDay * 60 + another.minute;
            return thisVal - otherVal;
        }

        @NonNull
        @Override
        public String toString() {
            String h = hourOfDay + "";
            String min = minute > 9? minute + "": "0" + minute;
            return h + ":" + min;
        }

        public static MyTime dateToMyTime(Date d) {
            if(d == null) {
                return null;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            return new MyTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        }

        public static MyTime currentTime() {
            Calendar calendar = Calendar.getInstance();
            return new MyTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        }
    }

    public static String getCurrentTimestamp() {
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        return ts;
    }

    public static String formatedDate(String date,String originaldateFormat,String formattedDateFormat) {
        String convertDate = null;
        if (TextUtils.isEmpty(date)) {
            return "";
        } else {
            SimpleDateFormat mformat = new SimpleDateFormat(formattedDateFormat, Locale.getDefault());
            SimpleDateFormat serverFormat = new SimpleDateFormat(originaldateFormat, Locale.getDefault());
            Date mdate = null;
            try {
                mdate = serverFormat.parse(date);
                convertDate = mformat.format(mdate);
            } catch (Exception ex) {
            }
            return convertDate;
        }
    }
}
