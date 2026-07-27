package com.cybermed.cdoc_patient.notification;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.cdfortis.datainterface.soap.WebService;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.Tablet_Mode.WelcomeActivityTablet;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.doctor.VideoCallActivity;
import com.cybermed.cdoc_patient.login.WelcomeActivity;
import com.cybermed.cdoc_patient.notification.NotificationService.CallNotificationService;
import com.cybermed.cdoc_patient.ws.WS;
//import com.onesignal.OSNotification;
//import com.onesignal.OSNotificationAction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


/**
 * Created by qinwe on 2017/5/10.
 */

public class MyNotificationReceivedHandler /*implements INotificationServiceExtension*//*OneSignal.NotificationReceivedHandler*/ {
    public final CDoctor2Application context;
    public final SharedPreferences sharedPreferences;
    public final SharedPreferences.Editor editor;

    public String message, roomnumber, onlinestatus, callerName, callerType;
    public final int PAYMENT_NOTIFICATION = 1000;
    public static final String CDOC_CHANNEL_ID = "com.cybermed.cdocpatient";
    public static final String CDOC_CHANNEL_NAME = "cdoc";
    public static final int SENT_INTERVAL = 35;
    public final Random random;


    public MyNotificationReceivedHandler(Context context) {
        this.context = (CDoctor2Application) context;
        sharedPreferences = context.getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        random = new Random();
    }



/*
    @Override
    public void onNotificationReceived(INotificationReceivedEvent event) {
        IDisplayableMutableNotification notification = event.getNotification();
        notification.setExtender(builder -> builder.setColor(0xFF0000FF));
        Log.d("OneSignalDebug", "received notification");
        Log.d("TIMEANALYSIS", "4. Recieved Onesignal " + Calendar.getInstance().getTime().toString());
        Log.d("TIMEANALYSIS", "4. Recieved Onesignal " + notification.getRawPayload());
        Log.d("TIMEANALYSIS", "4. Recieved Onesignal " + notification);
        //old code
        //JSONObject data = notification.payload.additionalData;
        JSONObject data = notification.getAdditionalData();
       // OneSignal.clearOneSignalNotifications();
        OneSignal.getNotifications().clearAllNotifications();
        try {
            // old code
          *//*  if (notification.payload.body.contains("HeartBeat Check")) {
                setDeviceStatus();
            }*//*
            if (Objects.requireNonNull(notification.getBody()).contains("HeartBeat Check")) {
                setDeviceStatus();
            } *//*else if (notification.payload.body.contains("Payment")) {
                try {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CDOC_CHANNEL_ID)
                            .setSmallIcon(R.drawable.cdoc_icon)
                            .setContentTitle("Charge Notification")
                            .setContentText(notification.payload.body)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                            .setPriority(NotificationCompat.PRIORITY_MAX);

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel mChannel = new NotificationChannel(CDOC_CHANNEL_ID, CDOC_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

                        notificationManager.createNotificationChannel(mChannel);
                    }

                    notificationManager.notify(PAYMENT_NOTIFICATION, mBuilder.build());
                } catch (Exception e) {
                }
            }*//*else if (notification.getBody().contains("Payment")) {
                try {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CDOC_CHANNEL_ID)
                            .setSmallIcon(R.drawable.cdoc_icon)
                            .setContentTitle("Charge Notification")
                            .setContentText(notification.getBody())
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                            .setPriority(NotificationCompat.PRIORITY_MAX);

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel mChannel = new NotificationChannel(CDOC_CHANNEL_ID, CDOC_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

                        notificationManager.createNotificationChannel(mChannel);
                    }

                    notificationManager.notify(1000, mBuilder.build());
                } catch (Exception e) {
                }
            }
//            else if (data != null && data.getString("subject") != null &&
//                    data.getString("subject").equals("Logout_All")) {
//                Intent intent = new Intent(SIGNALR_LOGOUT);
//                //LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//            }
            else {
                if (data != null) {
                    //Reboot push
                    try {
                        //Reboot Message
                        if (data.has("admin_action")){
                            String admin_action = data.getString("admin_action");
                            if (admin_action != null && admin_action.equals("reboot_device")) {
                                restartApp();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Normal push
                    try {
                        roomnumber = data.getString("roomnumber");
                        onlinestatus = data.getString("onlinestatus");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (onlinestatus != null && onlinestatus.equals("1")) {
                       // message = notification.payload.body;
                        message = notification.getBody();
                        String[] messageString = message.split(" ");
                        callerType = messageString[0].toLowerCase().trim();
                        callerName = messageString[1] + " " + messageString[2];

                        startActivity();
                    } else {
                        if (VideoCallActivity.getInstance() != null) {
                            VideoCallActivity.getInstance().clearNotification();
                            VideoCallActivity.getInstance().cancelIncomingCallTimer();
                            VideoCallActivity.getInstance().finish();
                        }
                    }
                }
            }
        } catch (Exception jsonException) {

        }

    }*/

    public void setDeviceStatus() {

        //https://stackoverflow.com/questions/7971946/in-java-return-value-within-synchronized-block-seems-like-bad-style-does-it-re
        //Return in synchronized is fine
        synchronized (this) {
            String key = "lastStatusSentTime";
            long lastSentTimeSec = sharedPreferences.getLong(key, 0);
            long currentTimeSec = new Date().getTime() / 1000;

            if (currentTimeSec - lastSentTimeSec > SENT_INTERVAL) {
                Log.d("TIMEANALYSISTimes", lastSentTimeSec + " " + currentTimeSec);
                //update
                editor.putLong(key, currentTimeSec);
                editor.commit();
            } else {
                return;
            }
        }
        //performing update online status
        new Thread(() -> {
            Log.d("TIMEANALYSISTimes", "" + new Date().getSeconds());

            try {
                Thread.sleep((5 + random.nextInt(30)) * 1000);
            } catch (Exception e) {
            }
            Log.d("TIMEANALYSISTimes", "" + new Date().getSeconds());

            int currentDeviceStatus;
            if (VideoCallActivity.getInstance() != null) {
                currentDeviceStatus = 2;
            } else {
                currentDeviceStatus = 1;
            }

            WS.setPatientDeviceStatus(currentDeviceStatus);
        }).start();
    }


    public void restartApp() {
        Intent intent = new Intent(context, WelcomeActivity.class);
        if (context.getTabletMode()) {
            intent = new Intent(context, WelcomeActivityTablet.class);
        }
        int mPendingIntentId = 666999;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }


    public void startActivity() {
        Intent intent = new Intent(context, VideoCallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("type", 0);
        intent.putExtra("docName", callerName);
        intent.putExtra("callType", callerType);
        intent.putExtra("roomNumber", roomnumber);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !foregrounded()) {
            Intent serviceIntent = new Intent(context, CallNotificationService.class);
            Bundle mBundle = new Bundle();
            mBundle.putString("inititator", callerName);
            mBundle.putString("call_type","video");
            mBundle.putInt("type", 0);
            mBundle.putString("docName", callerName);
            mBundle.putString("callType", callerType);
            mBundle.putString("roomNumber", roomnumber);
            serviceIntent.putExtras(mBundle);
            ContextCompat.startForegroundService(context, serviceIntent);
        } else {
            context.startActivity(intent);
        }
    }


    public boolean foregrounded() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }
    private AsyncTask developerDebugLog(final String message) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;
            private String timestamp;

            @Override
            protected void onPreExecute() {
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
                timestamp = df.format(Calendar.getInstance().getTime());
            }

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().DeveloperDebugLog(((CDoctor2Application) context).getLoginInfo().getAccount()
                            , message, timestamp);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (e == null) {
                    if (integer == 1) {
                    }
                } else {
                }

            }
        }.execute();
    }

/*
    @Override
    public void onWillDisplay(@NonNull INotificationWillDisplayEvent notification) {
        //IDisplayableMutableNotification notification = iNotificationWillDisplayEvent.getNotification();

        Log.d("OneSignalDebug", "received notification");
        Log.d("TIMEANALYSIS", "4. Recieved Onesignal " + Calendar.getInstance().getTime().toString());
        Log.d("TIMEANALYSIS", "4. Recieved Onesignal " + notification.getNotification().getRawPayload());
        Log.d("TIMEANALYSIS", "4. Recieved Onesignal " + notification);
        //old code
        //JSONObject data = notification.payload.additionalData;
        JSONObject data = notification.getNotification().getAdditionalData();
        // OneSignal.clearOneSignalNotifications();
        OneSignal.getNotifications().clearAllNotifications();
        try {
            // old code
           *//* if (notification.payload.body.contains("HeartBeat Check")) {
                setDeviceStatus();
            }*//*
            if (Objects.requireNonNull(notification.getNotification().getBody()).contains("HeartBeat Check")) {
                setDeviceStatus();
            } *//*else if (notification.payload.body.contains("Payment")) {
                try {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CDOC_CHANNEL_ID)
                            .setSmallIcon(R.drawable.cdoc_icon)
                            .setContentTitle("Charge Notification")
                            .setContentText(notification.payload.body)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                            .setPriority(NotificationCompat.PRIORITY_MAX);

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel mChannel = new NotificationChannel(CDOC_CHANNEL_ID, CDOC_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

                        notificationManager.createNotificationChannel(mChannel);
                    }

                    notificationManager.notify(PAYMENT_NOTIFICATION, mBuilder.build());
                } catch (Exception e) {
                }
            }*//*else if (notification.getNotification().getBody().contains("Payment")) {
                try {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CDOC_CHANNEL_ID)
                            .setSmallIcon(R.drawable.cdoc_icon)
                            .setContentTitle("Charge Notification")
                            .setContentText(notification.getNotification().getBody())
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                            .setPriority(NotificationCompat.PRIORITY_MAX);

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel mChannel = new NotificationChannel(CDOC_CHANNEL_ID, CDOC_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

                        notificationManager.createNotificationChannel(mChannel);
                    }

                    notificationManager.notify(1000, mBuilder.build());
                } catch (Exception e) {
                }
            }
//            else if (data != null && data.getString("subject") != null &&
//                    data.getString("subject").equals("Logout_All")) {
//                Intent intent = new Intent(SIGNALR_LOGOUT);
//                //LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//            }
            else {
                if (data != null) {
                    //Reboot push
                    try {
                        //Reboot Message
                        if (data.has("admin_action")){
                            String admin_action = data.getString("admin_action");
                            if (admin_action != null && admin_action.equals("reboot_device")) {
                                restartApp();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Normal push
                    try {
                        roomnumber = data.getString("roomnumber");
                        onlinestatus = data.getString("onlinestatus");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (onlinestatus != null && onlinestatus.equals("1")) {
                        // message = notification.payload.body;
                        message = notification.getNotification().getBody();
                        String[] messageString = message.split(" ");
                        callerType = messageString[0].toLowerCase().trim();
                        callerName = messageString[1] + " " + messageString[2];

                        startActivity();
                    } else {
                        if (VideoCallActivity.getInstance() != null) {
                            VideoCallActivity.getInstance().clearNotification();
                            VideoCallActivity.getInstance().cancelIncomingCallTimer();
                            VideoCallActivity.getInstance().finish();
                        }
                    }
                }
            }
        } catch (Exception jsonException) {

        }
    }*/
}
