package com.cybermed.cdoc_patient.common;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import android.util.Log;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.login.LoginInfo;
import com.cybermed.cdoc_patient.login.WelcomeActivity;
import com.cybermed.cdoc_patient.ws.WS;

/**
 * Created by joshu on 5/26/2017.
 */

public class LogoutService extends Service {
    private CountDownTimer timer;
    private CommonAsyncTaskActivity.SetStatusResult setStatusResult;
    private AsyncTask setPatientDeviceStatusTask;
    public static final int STATUS_OFF_LINE = 0;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        Log.d("timer", "logged out");
        String userId = getLoginInfo2().getAccount();

        OnPostExecute ope = result -> {
            logoutNotification();
            getCDocApplication().shutDown();
            getCDocApplication().setLogin(false);
        };

        WS.setPatientDeviceStatus(STATUS_OFF_LINE, ope);

        stopSelf();
        Log.d("timer", "Call Logout by Service");

    }

    //Self Notification for autoLogout
    private void logoutNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.icon_logo2);
        mBuilder.setContentTitle("Login Time Expired");
        mBuilder.setContentText("For safety concerns, you have been logged out of CDoc");
        Intent notificationIntent = new Intent(this, WelcomeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, mBuilder.build());

    }


    public CDoctor2Application getCDocApplication() {
        return (CDoctor2Application) getApplication();
    }

    public LoginInfo getLoginInfo2() {
        return getCDocApplication().getLoginInfo();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
