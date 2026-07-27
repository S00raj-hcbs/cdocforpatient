package com.cybermed.cdoc_patient.notification;

/*
import android.content.Intent;
import android.util.Log;

import com.cybermed.cdoc.common.CDoctor2Application;
import com.cybermed.cdoc.doctor.VideoCallActivity;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

*/
/**
 * Created by qinwe on 2017/5/10.
 *//*


*/
/**
 * 当通知点击时，这将被调用
 *//*

public class MyNotificationOpenHandler implements OneSignal.NotificationOpenedHandler {
    private CDoctor2Application context;
    private String docName,orgCode,providerId;

    public MyNotificationOpenHandler(CDoctor2Application context) {
        this.context = context;
    }

    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        if (!context.isLogin()){
            return;
        }
        if (actionType == OSNotificationAction.ActionType.Opened){
            OneSignal.clearOneSignalNotifications();
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);
            if (data != null) {
                try {
                    docName = data.getString("docName");
                    orgCode = data.getString("orgCode");
                    providerId = data.getString("providerId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(context,VideoCallActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                intent.putExtra("type",0);
                intent.putExtra("docName",docName);
                intent.putExtra("orgCode",orgCode);
                intent.putExtra("providerId",providerId);
                context.startActivity(intent);
            }

        }
    }
}
*/
