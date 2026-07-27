package com.cybermed.cdoc_patient.notification.NotificationService;

import static com.cybermed.cdoc_patient.common.BaseActivity.STATUS_ON_LINE;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.model.DocInfo;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.doctor.VideoCallActivity;
import com.cybermed.cdoc_patient.ws.WS;
import com.onesignal.OneSignal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CallNotificationActionReceiver extends BroadcastReceiver {


    Context mContext;
    private AsyncTask setPatientOnesignalIndicatorTask;
    private AsyncTask setPatientOnlineRoomTask;
    private AsyncTask getOnlineProviderNameV2Task;
    private static Timer incomingHangupTimer;
    private AsyncTask GetOnlineRoomNumberTask;
    private boolean hasEnteredInCall = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        if (intent != null && intent.getExtras() != null) {

            String action = "";
            action = intent.getStringExtra("ACTION_TYPE");

            if (action != null && !action.equalsIgnoreCase("")) {
                performClickAction(context, action, intent);
            }

            // Close the notification after the click action is performed.
//            Intent iclose = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//            context.sendBroadcast(iclose);
//            context.stopService(new Intent(context, CallNotificationService.class));
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                try {
                    OneSignal.getNotifications().clearAllNotifications();
                    Log.i("CallReceiver", "All OneSignal notifications removed");
                } catch (Exception e) {
                    Log.e("CallReceiver", "Error clearing OneSignal notifications: " + e.getMessage());
                    // Fallback to NotificationManager
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (notificationManager != null) {
                        notificationManager.cancelAll();
                        Log.i("CallReceiver", "All notifications removed using NotificationManager");
                    }
                }
            }
        }


    }

    private void performClickAction(Context context, String action, Intent bundleData) {
        Intent intentCallReceive = new Intent(mContext, VideoCallActivity.class);
        intentCallReceive.putExtra("type", 0);
        if (bundleData.getExtras() != null) {
            intentCallReceive.putExtra("docName", bundleData.getStringExtra("docName"));
            intentCallReceive.putExtra("callType", bundleData.getStringExtra("callType"));
            intentCallReceive.putExtra("roomNumber", bundleData.getStringExtra("roomNumber"));
        }
        intentCallReceive.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);


        if (action.equalsIgnoreCase("RECEIVE_CALL")) {
            hasEnteredInCall = true;
            if (checkAppPermissions()) {
                intentCallReceive.putExtra("call_input_type", "RECEIVE_CALL");
                intentCallReceive.putExtra("callFromPush", true);
                mContext.startActivity(intentCallReceive);
            } else {
                intentCallReceive.putExtra("call_input_type", "RECEIVE_CALL");
                intentCallReceive.putExtra("callFromPush", true);
                mContext.startActivity(intentCallReceive);
            }
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
            stopCallingService();
        } else if (action.equalsIgnoreCase("DIALOG_CALL")) {
            mContext.startActivity(intentCallReceive);
            stopCallingService();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                }
            }, 30000);
        } else if (action.equalsIgnoreCase("CANCEL_CALL")) {
            hangUpProcess(bundleData.getStringExtra("roomNumber"));
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        } else if (action.equalsIgnoreCase("CALL_NO_ANSWER")) {
            SetPatientOnesignalIndicator(false);
            stopCallingService();
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        } else if (action.equalsIgnoreCase("INCOMING_CALL")) {
            getOnlineProviderNameV2();
            SetPatientOnesignalIndicator(true);
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        } else {
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
            context.stopService(new Intent(context, CallNotificationService.class));
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            try{
                context.sendBroadcast(it);
            }catch (Exception e){

            }
        }

    }


    private void hangUpProcess(String roomNumber) {
        hangUpFunction(roomNumber);
        postFinish();
    }

    private void postFinish() {
        //Reset push notification indicator to false
        SetPatientOnesignalIndicator(false);
        stopCallingService();
    }

    private void hangUpFunction(String roomNumber) {
        OnPostExecute ope = result -> {
            if (result.toString().equals("1")) {
                setPatientOnlineRoom(CDoctor2Application.getLoginInfo().getAccount(), STATUS_ON_LINE, roomNumber);
            }
        };

        WS.setPatientDeviceStatus(STATUS_ON_LINE, ope);

    }

    private void setPatientOnlineRoom(String userId, int status, String onLineRoom) {
        getSetPatientOnlineRoomResult(userId, status, onLineRoom);
    }

    private void SetPatientOnesignalIndicator(boolean set) {
        SimpleDateFormat sdfCompareNow = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.US);
        String dateTimeCompNow = sdfCompareNow.format(new Date());
        if (setPatientOnesignalIndicatorTask == null) {
            setPatientOnesignalIndicatorTask = SetPatientOnesignalIndicatorAsyncTask(set, dateTimeCompNow);
        }
    }

    private AsyncTask SetPatientOnesignalIndicatorAsyncTask(final boolean set, final String delivery_date) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().SetPatientOnesignalIndicator(CDoctor2Application.getLoginInfo().getAccount(), set, delivery_date);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                setPatientOnesignalIndicatorTask = null;
                if (e == null) {
                    if (integer != -1) {
                        if (set == true) {
                            incomingHangupTimerCheck();
                        }
                        Log.d("TIMEANALYSIS", "5. Set GETUI Indicator " + Calendar.getInstance().getTime().toString());
                        Log.e("Consult", "setProviderOnesignalIndicator success");
                        //binding.linearAudiotrouble.setVisibility(View.VISIBLE);
                    } else {
                        Log.e("consult", "setProviderOnesignalIndicator failed");
                    }
                }

            }
        }.execute();
    }

    public void getSetPatientOnlineRoomResult(String userId, int onLineStatus,
                                              String onLineRoom) {

        if (setPatientOnlineRoomTask == null) {
            setPatientOnlineRoomTask = setPatientOnlineRoomAsyncTask(userId, onLineStatus, onLineRoom);
        }
    }

    //设置用户在线房间
    private AsyncTask setPatientOnlineRoomAsyncTask(final String uerId, final int onlineStatus,
                                                    final String onlineRoom) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().setPatientOnlineRoom(uerId, onlineStatus, onlineRoom);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                setPatientOnlineRoomTask = null;

            }
        }.execute();
    }

    /**
     * step 1 of incoming call
     */
    private void getOnlineProviderNameV2() {
        if (getOnlineProviderNameV2Task == null) {
            getOnlineProviderNameV2Task = getOnlineProviderNameV2AsyncTask();
        }
    }

    private AsyncTask getOnlineProviderNameV2AsyncTask() {
        return new AsyncTask<Void, Void, DocInfo>() {
            Exception e;

            @Override
            protected DocInfo doInBackground(Void... voids) {
                try {
                    return WebService.getInstance().getOnlineProviderNameV2(CDoctor2Application.getLoginInfo().getAccount());
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(DocInfo doctor) {
                getOnlineProviderNameV2Task = null;
                if (doctor != null) {
                    if (TextUtils.isEmpty(doctor.last_name)) {
                        cancelIncomingCallTimer();
                        postFinish();
                    }
                }
            }
        }.execute();
    }

    private void incomingHangupTimerCheck() {
        incomingHangupTimer = new Timer();
        incomingHangupTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!hasEnteredInCall) {
                    getOnlineProviderNameV2();
                }

                if (GetOnlineRoomNumberTask == null)
                    GetOnlineRoomNumberTask = GetOnlineRoomNumberAsyncTask(CDoctor2Application.getLoginInfo().getAccount());

            }
        }, 0, 3000);
    }

    private AsyncTask GetOnlineRoomNumberAsyncTask(final String user_id) {
        return new AsyncTask<Void, Void, String>() {
            Exception e;

            @Override
            protected String doInBackground(Void... params) {
                try {
                    return WebService.getInstance().GetOnlineRoomNumber_Patient(user_id);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                GetOnlineRoomNumberTask = null;
                if (s.isEmpty()) {
                    cancelIncomingCallTimer();
                    hangUpProcess(s);
                }
            }
        }.execute();
    }

    public void cancelIncomingCallTimer() {
        if (incomingHangupTimer != null) {
            incomingHangupTimer.cancel();
            incomingHangupTimer = null;
        }
    }


    /**
     * stop service if nobody accept/reject the call
     */
    public void stopCallingService() {
        Intent iclose = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        try{
            mContext.sendBroadcast(iclose);
        }catch (Exception e){

        }

        mContext.stopService(new Intent(mContext, CallNotificationService.class));
    }

    private Boolean checkAppPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return /*hasReadPermissions2() &&*/ hasCameraPermissions() && hasAudioPermissions();
        }else {
            return /*hasReadPermissions() && hasWritePermissions() &&*/ hasCameraPermissions() && hasAudioPermissions();
        }

    }

    private boolean hasAudioPermissions() {
        return (ContextCompat.checkSelfPermission(CDoctor2Application.application, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
    }

   /* private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(CDoctor2Application.application, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }*/

    /*@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private boolean hasReadPermissions2() {
        return (ContextCompat.checkSelfPermission(CDoctor2Application.application, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED);
    }*/
    /*private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(CDoctor2Application.application, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }*/

    private boolean hasCameraPermissions() {
        return (ContextCompat.checkSelfPermission(CDoctor2Application.application, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

}
