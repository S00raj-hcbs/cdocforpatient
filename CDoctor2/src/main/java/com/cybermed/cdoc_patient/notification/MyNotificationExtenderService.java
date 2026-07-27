package com.cybermed.cdoc_patient.notification;

/*import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

*/

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
import static android.content.Context.NOTIFICATION_SERVICE;

import static com.cybermed.cdoc_patient.common.CDoctor2Application.application;
import static com.cybermed.cdoc_patient.notification.MyNotificationReceivedHandler.CDOC_CHANNEL_ID;
import static com.cybermed.cdoc_patient.notification.MyNotificationReceivedHandler.CDOC_CHANNEL_NAME;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.doctor.VideoCallActivity;
import com.cybermed.cdoc_patient.notification.NotificationService.CallNotificationActionReceiver;
import com.onesignal.OneSignal;
import com.onesignal.notifications.IActionButton;
import com.onesignal.notifications.IDisplayableMutableNotification;
import com.onesignal.notifications.INotificationReceivedEvent;
import com.onesignal.notifications.INotificationServiceExtension;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by qinwe on 2017/5/15.
 *//*
//old code
public class MyNotificationExtenderService extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {
        return true;
    }
}*/
 // Keep is required to prevent minification from renaming or removing your class
public class MyNotificationExtenderService implements INotificationServiceExtension {
    public static final String LOG_TAG = "sdktest";
    private String CHANNEL_ID = CDoctor2Application.application.getString(R.string.app_name) + "CallChannel";
    private String CHANNEL_NAME = CDoctor2Application.application.getString(R.string.app_name) + "Call Channel";
    @Override
    public void onNotificationReceived(INotificationReceivedEvent event) {


        /*if (notification.getActionButtons() != null) {
            for (IActionButton button : notification.getActionButtons()) {
                // you can modify your action buttons here
            }
        }*/
        /*JSONObject data = event.getNotification().getAdditionalData();
        String data2 = event.getNotification().getRawPayload();
        MyNotificationReceivedHandler notificationReceivedHandler = new MyNotificationReceivedHandler(application);
        // this is an example of how to modify the notification by changing the background color to blue
       // notification.setExtender(builder -> builder.setColor(0xFF0000FF));
        Log.v(LOG_TAG, "fired" + "data with RawPayload: " + data);
        Log.v(LOG_TAG, "fired" + "data with RawPayload: " + data2);
        IDisplayableMutableNotification notification = event.getNotification();
        OneSignal.getNotifications().clearAllNotifications();
        try {
            // old code
          *//*  if (notification.payload.body.contains("HeartBeat Check")) {
                setDeviceStatus();
            }*//*
            if (Objects.requireNonNull(notification.getBody()).contains("HeartBeat Check")) {
                notificationReceivedHandler.setDeviceStatus();
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
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(application, CDOC_CHANNEL_ID)
                            .setSmallIcon(R.drawable.cdoc_icon)
                            .setContentTitle("Charge Notification")
                            .setContentText(notification.getBody())
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                            .setPriority(NotificationCompat.PRIORITY_MAX);

                    NotificationManager notificationManager = (NotificationManager) application.getSystemService(NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel mChannel = new NotificationChannel(CDOC_CHANNEL_ID, CDOC_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

                        notificationManager.createNotificationChannel(mChannel);
                    }

                    notificationManager.notify(notificationReceivedHandler.PAYMENT_NOTIFICATION, mBuilder.build());
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
                                notificationReceivedHandler.restartApp();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Normal push
                    try {
                        notificationReceivedHandler.roomnumber = data.getString("roomnumber");
                        notificationReceivedHandler.onlinestatus = data.getString("onlinestatus");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (notificationReceivedHandler.onlinestatus != null && notificationReceivedHandler.onlinestatus.equals("1")) {
                        // message = notification.payload.body;
                        notificationReceivedHandler.message = notification.getBody();
                        String[] messageString = notificationReceivedHandler.message.split(" ");
                        notificationReceivedHandler.callerType = messageString[0].toLowerCase().trim();
                        notificationReceivedHandler.callerName = messageString[1] + " " + messageString[2];

                        notificationReceivedHandler.startActivity();
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

        }*/
        Log.v(LOG_TAG, "IRemoteNotificationReceivedHandler fired" + " with INotificationReceivedEvent: " + event.toString());
        Log.v(LOG_TAG, "fired" + " with AdditionalData: " + event.getNotification().getAdditionalData());
        Log.v(LOG_TAG, "fired" + " with RawPayload: " + event.getNotification().getRawPayload());
        JSONObject data = event.getNotification().getAdditionalData();
        String data2 = event.getNotification().getRawPayload();
        MyNotificationReceivedHandler notificationReceivedHandler = new MyNotificationReceivedHandler(application);

        Log.v(LOG_TAG, "fired" + "data with RawPayload: " + data);
        Log.v(LOG_TAG, "fired" + "data with RawPayload: " + data2);
        IDisplayableMutableNotification notification = event.getNotification();
        OneSignal.getNotifications().clearAllNotifications();
        try {
            if (Objects.requireNonNull(notification.getBody()).contains("HeartBeat Check")) {
                notificationReceivedHandler.setDeviceStatus();
            } else if (notification.getBody().contains("Payment")) {

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(application, CDOC_CHANNEL_ID)
                        .setSmallIcon(R.drawable.cdoc_icon)
                        .setContentTitle("Charge Notification")
                        .setContentText(notification.getBody())
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                        .setPriority(NotificationCompat.PRIORITY_MAX);

                NotificationManager notificationManager = (NotificationManager) application.getSystemService(NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel mChannel = new NotificationChannel(CDOC_CHANNEL_ID, CDOC_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

                    notificationManager.createNotificationChannel(mChannel);
                }

                notificationManager.notify(notificationReceivedHandler.PAYMENT_NOTIFICATION, mBuilder.build());
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Clear OneSignal notifications
                        OneSignal.getNotifications().clearAllNotifications();
                    }
                }, 500);
            }
            else {
                if (data != null) {

                    //Normal push
                    try {
                        //Reboot push
                        //Reboot Message
                        if (data.has("admin_action")){
                            String admin_action = data.getString("admin_action");
                            if (admin_action != null && admin_action.equals("reboot_device")) {
                                notificationReceivedHandler.restartApp();
                            }
                        }
                        //Normal push
                        notificationReceivedHandler.roomnumber = data.getString("roomnumber");
                        notificationReceivedHandler.onlinestatus = data.getString("onlinestatus");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    OneSignal.getNotifications().clearAllNotifications();
                    if (notificationReceivedHandler.onlinestatus != null && notificationReceivedHandler.onlinestatus.equals("1")) {
                        OneSignal.getNotifications().clearAllNotifications();
                        // message = notification.payload.body;
                        notificationReceivedHandler.message = notification.getBody();
                        String[] messageString = notificationReceivedHandler.message.split(" ");
                        notificationReceivedHandler.callerType = messageString[0].toLowerCase().trim();
                        notificationReceivedHandler.callerName = messageString[1] + " " + messageString[2];
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !foregrounded()) {
                            OneSignal.getNotifications().clearAllNotifications();
                            int NOTIFICATION_ID = 120;
                            Uri soundUri = Uri.parse("android.resource://" + application.getPackageName() + "/" + R.raw.ring);
                            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                                    + "://" + application.getPackageName() + "/" + R.raw.ring);
                            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                            channel.setDescription("Call Notifications");
                            channel.setSound(alarmSound, null); // Set the sound for the channel
                            NotificationManager notificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.createNotificationChannel(channel);

                            Intent receiveCallAction = new Intent(CDoctor2Application.application, VideoCallActivity.class);
                            receiveCallAction.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            receiveCallAction.putExtra("ACTION_TYPE", "DIALOG_CALL");
                            receiveCallAction.putExtra("callFromPush", false);
                            receiveCallAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
                            receiveCallAction.putExtra("type", 0);
                            receiveCallAction.putExtra("docName", notificationReceivedHandler.callerName);
                            receiveCallAction.putExtra("callType", notificationReceivedHandler.callerType);
                            receiveCallAction.putExtra("roomNumber",  notificationReceivedHandler.roomnumber);

                            receiveCallAction.setAction("DIALOG_CALL");
                            /*receiveCallAction.putExtra("call_input_type", "RECEIVE_CALL");
                            receiveCallAction.putExtra("callFromPush", true);
                            receiveCallAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
                            receiveCallAction.putExtra("type", 0);

                            receiveCallAction.putExtra("docName", notificationReceivedHandler.callerName);
                            receiveCallAction.putExtra("callType", notificationReceivedHandler.callerType);
                            receiveCallAction.putExtra("roomNumber",  notificationReceivedHandler.roomnumber);

                            receiveCallAction.setAction("RECEIVE_CALL");*/

                            Intent cancelCallAction = new Intent(CDoctor2Application.application, VideoCallActivity.class);
                            cancelCallAction.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            cancelCallAction.putExtra("ConstantApp.CALL_RESPONSE_ACTION_KEY", "ConstantApp.CALL_CANCEL_ACTION");
                            cancelCallAction.putExtra("ACTION_TYPE", "CANCEL_CALL");
                            cancelCallAction.putExtra("callFromPush", false);
                            cancelCallAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
                            cancelCallAction.putExtra("type", 0);

                            cancelCallAction.putExtra("docName", notificationReceivedHandler.callerName);
                            cancelCallAction.putExtra("callType", notificationReceivedHandler.callerType);
                            cancelCallAction.putExtra("roomNumber",  notificationReceivedHandler.roomnumber);

                            cancelCallAction.setAction("CANCEL_CALL");

                            Intent callDialogAction = new Intent(CDoctor2Application.application, VideoCallActivity.class);
                            callDialogAction.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            callDialogAction.putExtra("ACTION_TYPE", "DIALOG_CALL");
                            callDialogAction.putExtra("callFromPush", false);
                            callDialogAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
                            callDialogAction.putExtra("type", 0);
                            callDialogAction.putExtra("docName", notificationReceivedHandler.callerName);
                            callDialogAction.putExtra("callType", notificationReceivedHandler.callerType);
                            callDialogAction.putExtra("roomNumber",  notificationReceivedHandler.roomnumber);

                            callDialogAction.setAction("DIALOG_CALL");


                            PendingIntent receiveCallPendingIntent = null;
                            PendingIntent cancelCallPendingIntent = null;
                            PendingIntent callDialogPendingIntent = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                                receiveCallPendingIntent = PendingIntent.getActivity(CDoctor2Application.application, 1200, receiveCallAction, PendingIntent.FLAG_IMMUTABLE);
                                cancelCallPendingIntent = PendingIntent.getActivity(CDoctor2Application.application, 1201, cancelCallAction, PendingIntent.FLAG_IMMUTABLE);
                                callDialogPendingIntent = PendingIntent.getActivity(CDoctor2Application.application, 1202, callDialogAction, PendingIntent.FLAG_IMMUTABLE);
                                notificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.cancelAll();
                            }else {
                                receiveCallPendingIntent = PendingIntent.getActivity(CDoctor2Application.application, 1200, receiveCallAction, PendingIntent.FLAG_UPDATE_CURRENT);
                                cancelCallPendingIntent = PendingIntent.getActivity(CDoctor2Application.application, 1201, cancelCallAction, PendingIntent.FLAG_UPDATE_CURRENT);
                                callDialogPendingIntent = PendingIntent.getActivity(CDoctor2Application.application, 1202, callDialogAction, PendingIntent.FLAG_UPDATE_CURRENT);
                                notificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.cancelAll();
                            }




                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(application, CHANNEL_ID)
                                    .setContentTitle(messageString[1] + " " + messageString[2])
                                    .setContentText("Incoming Video Call")
                                    .setSmallIcon(R.drawable.logo_small)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setCategory(NotificationCompat.CATEGORY_CALL)
                                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                    .setSound(alarmSound)
                                    .addAction(R.drawable.btn_hangup, "Reject Call", cancelCallPendingIntent)
                                    .addAction(R.drawable.btn_accept_call, "Accept Call", receiveCallPendingIntent)
                                    .setAutoCancel(true)
                                    .setFullScreenIntent(callDialogPendingIntent,true);
                            Notification incomingCallNotification = notificationBuilder.build();
                            notificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(NOTIFICATION_ID,incomingCallNotification);
                            /*Bundle data3=new Bundle() ;
                            data3.putString("inititator",  notificationReceivedHandler.callerName);
                            data3.putString("call_type","video");
                            data3.putInt("type", 0);
                            data3.putString("docName",  notificationReceivedHandler.callerName);
                            data3.putString("callType", notificationReceivedHandler.callerType);
                            data3.putString("roomNumber", notificationReceivedHandler.roomnumber);
                            incomingCallReceiver(data3);
                            startCancelCallTimer();*/
                            //startCancelCallTimer();
                             /*Intent serviceIntent = new Intent(application, CallNotificationService.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putString("inititator", notificationReceivedHandler.callerName);
                            mBundle.putString("call_type","video");
                            mBundle.putInt("type", 0);
                            mBundle.putString("docName", notificationReceivedHandler.callerName);
                            mBundle.putString("callType", notificationReceivedHandler.callerType);
                            mBundle.putString("roomNumber", notificationReceivedHandler.roomnumber);*/
                              //serviceIntent.putExtras(mBundle);
                            //  incomingCallReceiver(mBundle);
                           // ContextCompat.startForegroundService(application, serviceIntent);
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Clear OneSignal notifications
                                    OneSignal.getNotifications().clearAllNotifications();
                                }
                            }, 500);
                        }else {
                            notificationReceivedHandler.startActivity();
                        }

                        //notificationReceivedHandler.startActivity();
                    } else {
                        OneSignal.getNotifications().clearAllNotifications();
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
        if (notification.getActionButtons() != null) {
            for (IActionButton button : notification.getActionButtons()) {
                Log.v(LOG_TAG, "ActionButton: " + button.toString());
            }
        }
        notification.setExtender(builder -> builder.setColor(event.getContext().getResources().getColor(R.color.azure)));
        //If you need to perform an async action or stop the payload from being shown automatically,
        //use event.preventDefault(). Using event.notification.display() will show this message again.
    }

    public void startCancelCallTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent iclose = new Intent(CDoctor2Application.application, CallNotificationActionReceiver.class);
                iclose.putExtra("ConstantApp.CALL_RESPONSE_ACTION_KEY", "ConstantApp.CALL_NO_ANSWER");
                iclose.putExtra("ACTION_TYPE", "CALL_NO_ANSWER");
                iclose.setAction("CALL_NO_ANSWER");
                application.sendBroadcast(iclose);
            }
        }, 25000);
    }
    public void incomingCallReceiver(Bundle data) {
        Intent iclose = new Intent(CDoctor2Application.application, CallNotificationActionReceiver.class);
        iclose.putExtra("ConstantApp.CALL_RESPONSE_ACTION_KEY", "ConstantApp.INCOMING_CALL");
        iclose.putExtra("ACTION_TYPE", "INCOMING_CALL");
        if (data != null) {
            iclose.putExtra("docName", data.getString("docName"));
            iclose.putExtra("callType", data.getString("callType"));
            iclose.putExtra("roomNumber", data.getString("roomNumber"));
        }
        iclose.setAction("INCOMING_CALL");
        application.sendBroadcast(iclose);

    }
    public boolean foregrounded() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }
}

/*
@Keep // Keep is required to prevent minification from renaming or removing your class
public class MyNotificationExtenderService implements INotificationServiceExtension {

    @Override
    public void onNotificationReceived(INotificationReceivedEvent event) {
        IDisplayableMutableNotification notification = event.getNotification();
        if (notification.getActionButtons() != null) {
            for (IActionButton button : notification.getActionButtons()) {
                // you can modify your action buttons here
                Log.d("OneSignalDebug", button.getText());
                Log.d("OneSignalDebug", button.getIcon());
                Log.d("OneSignalDebug", button.getId());
            }
        }
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
        MyNotificationReceivedHandler notificationReceivedHandler = new MyNotificationReceivedHandler(application);
        try {
            // old code
          */
/*  if (notification.payload.body.contains("HeartBeat Check")) {
                setDeviceStatus();
            }*//*

            if (Objects.requireNonNull(notification.getBody()).contains("HeartBeat Check")) {
                notificationReceivedHandler.setDeviceStatus();
            } */
/*else if (notification.payload.body.contains("Payment")) {
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
            }*//*
else if (notification.getBody().contains("Payment")) {
                try {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(application, CDOC_CHANNEL_ID)
                            .setSmallIcon(R.drawable.cdoc_icon)
                            .setContentTitle("Charge Notification")
                            .setContentText(notification.getBody())
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                            .setPriority(NotificationCompat.PRIORITY_MAX);

                    NotificationManager notificationManager = (NotificationManager) application.getSystemService(NOTIFICATION_SERVICE);

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
                                notificationReceivedHandler.restartApp();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Normal push
                    try {
                        notificationReceivedHandler.roomnumber = data.getString("roomnumber");
                        notificationReceivedHandler.onlinestatus = data.getString("onlinestatus");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (notificationReceivedHandler.onlinestatus != null && notificationReceivedHandler.onlinestatus.equals("1")) {
                        // message = notification.payload.body;
                        notificationReceivedHandler.message = notification.getBody();
                        String[] messageString = notificationReceivedHandler.message.split(" ");
                        notificationReceivedHandler.callerType = messageString[0].toLowerCase().trim();
                        notificationReceivedHandler.callerName = messageString[1] + " " + messageString[2];

                        notificationReceivedHandler.startActivity();
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
       */
/* if (notification.getActionButtons() != null) {
            for (IActionButton button : notification.getActionButtons()) {
                // you can modify your action buttons here
            }
        }

        // this is an example of how to modify the notification by changing the background color to blue
        notification.setExtender(builder -> builder.setColor(0xFF0000FF));*//*


        //If you need to perform an async action or stop the payload from being shown automatically,
        //use event.preventDefault(). Using event.notification.display() will show this message again.
    }
}*/
