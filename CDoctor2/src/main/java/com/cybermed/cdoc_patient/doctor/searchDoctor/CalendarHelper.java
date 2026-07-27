package com.cybermed.cdoc_patient.doctor.searchDoctor;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cybermed.cdoc_patient.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * calnder helper
 */
public class CalendarHelper {

    //Remember to initialize this activityObj first, by calling initActivityObj(this) from
//your activity
    private static final String TAG = "CalendarHelper";
    public static final int CALENDARHELPER_PERMISSION_REQUEST_CODE = 99;

    ICalenderSuccess iCalenderSuccess;

    /**
     *
     * @param context context
     * @param calendarId event id
     * @param iCalenderDel callback
     */
    public static void deleteCalenderEvent(Context context, long calendarId, ICalenderDel iCalenderDel) {
        Uri eventUri = Uri.parse("content://com.android.calendar/events");  // or "content://com.android.calendar/events"
        Uri uri = ContentUris.withAppendedId(eventUri, calendarId);
        context.getContentResolver().delete(uri, null, null);
        iCalenderDel.deleteEvent();
    }

    /**
     *
     * @param caller activity
     */
    public static void requestCalendarReadWritePermission(Activity caller) {
        List<String> permissionList = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(caller, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_CALENDAR);
        }

        if (ContextCompat.checkSelfPermission(caller, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_CALENDAR);
        }

        if (permissionList.size() > 0) {
            String[] permissionArray = new String[permissionList.size()];
            for (int i = 0; i < permissionList.size(); i++) {
                permissionArray[i] = permissionList.get(i);
            }

            ActivityCompat.requestPermissions(caller,
                    permissionArray,
                    CALENDARHELPER_PERMISSION_REQUEST_CODE);
        }

    }

    /**
     *
     * @param c context
     * @return  list of events
     */
    public static Hashtable listCalendarId(Context c) {
        if (haveCalendarReadWritePermissions((Activity) c)) {

            String projection[] = {"_id", "calendar_displayName"};
            Uri calendars;
            calendars = Uri.parse("content://com.android.calendar/calendars");

            ContentResolver contentResolver = c.getContentResolver();
            Cursor managedCursor = contentResolver.query(calendars, projection, null, null, null);

            if (managedCursor.moveToFirst()) {
                String calName;
                String calID;
                int cont = 0;
                int nameCol = managedCursor.getColumnIndex(projection[1]);
                int idCol = managedCursor.getColumnIndex(projection[0]);
                Hashtable<Integer, String> calendarIdTable = new Hashtable<>();

                do {
                    calName = managedCursor.getString(nameCol);
                    calID = managedCursor.getString(idCol);
                    Log.v(TAG, "CalendarName:" + calName + " ,id:" + calID);
                    calendarIdTable.put(Integer.parseInt(calID), calName);
                    cont++;
                } while (managedCursor.moveToNext());
                managedCursor.close();

                return calendarIdTable;
            }

        }

        return null;

    }

    /**
     *
     * @param caller context
     * @return check permission
     */
    public static boolean haveCalendarReadWritePermissions(Activity caller) {
        int permissionCheck = ContextCompat.checkSelfPermission(caller,
                Manifest.permission.READ_CALENDAR);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            permissionCheck = ContextCompat.checkSelfPermission(caller,
                    Manifest.permission.WRITE_CALENDAR);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param calendarIdTable update google calender list
     */
    public static void updateCalendarIdSpinner(Hashtable<Integer, String> calendarIdTable) {
        if (calendarIdTable == null) {
            return;
        }

        List<Integer> list = new ArrayList<Integer>();

        Enumeration e = calendarIdTable.keys();
        while (e.hasMoreElements()) {
            Integer key = (Integer) e.nextElement();
            list.add(key);
        }
    }

    /**
     *
     * @param calendarIdTable hashtable
     * @param context context
     * @param tittle tittle
     * @param desciption description
     * @param apptTime appt time
     * @param calendar calender
     * @param iCalenderEvent callback
     */
    public static void addNewEvent(Hashtable<Integer, String> calendarIdTable, Activity context,
                                   String tittle, String desciption, String apptTime, long calendar, ICalenderSuccess iCalenderEvent) {
        if (calendarIdTable == null) {
            Toast.makeText(context, (String) context.getString(R.string.reminder_alert),
                    Toast.LENGTH_LONG).show();
            //Load calendars
            calendarIdTable = CalendarHelper.listCalendarId(context);

            updateCalendarIdSpinner(calendarIdTable);
            iCalenderEvent.calenderFailure();
            return;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault());
        Date date = null;
        try {
            date = formatter.parse(apptTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final long oneHour = 1000 * 60 * 60;
        final long tenMinutes = 1000 * 60 * 10;

        long oneHourFromNow = date.getTime() + oneHour;
        long tenMinutesFromNow = date.getTime() - tenMinutes;
        int calendar_id = 0;
        for (Map.Entry<Integer, String> e : calendarIdTable.entrySet()) {
            if (e.getValue().contains("@")) {
                calendar_id = e.getKey();
                break;
            }
        }

        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, tenMinutesFromNow);
        values.put(CalendarContract.Events.DTEND, tenMinutesFromNow + oneHour);
        values.put(CalendarContract.Events.TITLE, tittle);
        values.put(CalendarContract.Events.DESCRIPTION, desciption);
        values.put(CalendarContract.Events.CALENDAR_ID, calendar_id);
        values.put(CalendarContract.Events.STATUS, CalendarContract.Events.STATUS_CONFIRMED);

        boolean allDay = false;
        if (allDay) {
            values.put(CalendarContract.Events.ALL_DAY, true);
        }
        boolean hasAlarm = true;
        if (hasAlarm) {
            values.put(CalendarContract.Events.HAS_ALARM, true);
        }

        //Get current timezone
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        Log.i(TAG, "Timezone retrieved=>" + TimeZone.getDefault().getID());
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        Log.i(TAG, "Uri returned=>" + uri.toString());
        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());

        if (hasAlarm) {
            ContentValues reminders = new ContentValues();
            reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
            reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            reminders.put(CalendarContract.Reminders.MINUTES, 3);

            Uri uri2 = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);
        }
        iCalenderEvent.eventIdSuccess((int)eventID);
        // return calendar_id;
    }

    /**
     * calender callback
     */
    public interface ICalenderSuccess {
        void eventIdSuccess(int calenderid);

        void calenderFailure();
    }

    public interface ICalenderDel {
        void deleteEvent();
    }

}
