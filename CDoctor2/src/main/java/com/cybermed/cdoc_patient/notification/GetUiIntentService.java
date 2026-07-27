//package com.cybermed.cdoc.notification;
//
//        import android.content.Context;
//        import android.content.Intent;
//        import android.content.SharedPreferences;
//        import android.os.AsyncTask;
//        import android.os.Message;
//        import android.preference.PreferenceManager;
//        import android.util.Log;
//        import android.widget.Toast;
//
//        import com.cdfortis.datainterface.soap.WebService;
//        import com.cybermed.cdoc.common.CDoctor2Application;
//        import com.cybermed.cdoc.doctor.VideoCallActivity;
//        import com.cybermed.cdoc.login.WelcomeActivity;
//        import com.cybermed.cdoc.Tablet_Mode.WelcomeActivityTablet;
//        import com.igexin.sdk.GTIntentService;
//        import com.igexin.sdk.PushConsts;
//        import com.igexin.sdk.PushManager;
//        import com.igexin.sdk.message.FeedbackCmdMessage;
//        import com.igexin.sdk.message.GTCmdMessage;
//        import com.igexin.sdk.message.GTNotificationMessage;
//        import com.igexin.sdk.message.GTTransmitMessage;
//        import com.igexin.sdk.message.SetTagCmdMessage;
//
//        import org.json.JSONException;
//        import org.json.JSONObject;
//
//        import java.text.DateFormat;
//        import java.text.SimpleDateFormat;
//        import java.util.Calendar;
//        import java.util.TimeZone;
//
///**
// * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
// * onReceiveMessageData 处理透传消息<br>
// * onReceiveClientId 接收 cid <br>
// * onReceiveOnlineState cid 离线上线通知 <br>
// * onReceiveCommandResult 各种事件处理回执 <br>
// */
//public class GetUiIntentService extends GTIntentService{
//    private String docName, orgCode, providerId, roomnumber, onlinestatus, userid;
//    private CDoctor2Application context;
//
//    public GetUiIntentService() {
//    }
//
////    @Override
////    public int onStartCommand(Intent intent, int flags, int startId) {
////        Log.d(TAG,"onStartCommand() - getui start");
////        return START_STICKY;
////    }
//
//    @Override
//    public void onReceiveServicePid(Context context, int pid) {
//    }
//
//    @Override
//    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
//        Log.d("TIMEANALYSIS", "4. Recieved GETUI " + Calendar.getInstance().getTime().toString());
//        developerDebugLog("GETUI Notification - Received");
//        this.context = (CDoctor2Application) getApplicationContext();
//        Log.d("CONTEXTDEBUG", getApplicationContext().toString());
//        try {
//            JSONObject data =new JSONObject(new String(msg.getPayload()));
//            Log.d("NOTIFICATIONDEBUG","RECEIVED1" + data);
//
//            JSONObject additionalData = data.getJSONObject("data");
//            roomnumber = additionalData.getString("roomnumber");
//            onlinestatus = additionalData.getString("onlinestatus");
//            JSONObject contentData = data.getJSONObject("contents");
//
//            docName = contentData.getString("en");
////            if (!this.context.isLogin()) {
////                Log.d("GETUIDebug", "not logged in");
////                developerDebugLog("GETUI Notification - Not Logged In");
//////                Toast.makeText(context,"NOT LOGGED IN", Toast.LENGTH_LONG);
////                return;
////            }
//
//            if (data != null) {
//                Log.e("FOREGROUNDDEBUG", "STATUS: " + String.valueOf(this.context.isForeground()));
//
////                if (this.context.isForeground()) {
//                    if (onlinestatus != null && onlinestatus.equals("1")) {
//                        developerDebugLog("GETUI Notification - Start Activity");
//                        startActivity();
//                    } else {
//                        developerDebugLog("GETUI Notification - Clear Activity");
//                        VideoCallActivity.getInstance().clearNotification();
//                        VideoCallActivity.getInstance().cancelIncomingCallTimer();
//                        VideoCallActivity.getInstance().finish();
//                    }
////                } else {
////                    developerDebugLog("GETUI Notification - Start Back Activity");
////                    startBackActivity();
////
////                }
//
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//            Log.d("NOTIFICATIONDEBUG","RECEIVED2" + msg);
//
//    }
//
//    @Override
//    public void onReceiveClientId(Context context, String clientid) {
//        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
//
//    }
//
//    @Override
//    public void onReceiveOnlineState(Context context, boolean online) {
//    }
//
//    @Override
//    public void onReceiveCommandResult(Context context, GTCmdMessage gtCmdMessage) {
//        Log.d("NOTIFICATIONDEBUG","RECEIVED" + gtCmdMessage.getAction());
//
//    }
//
//    @Override
//    public void onNotificationMessageArrived(Context context, GTNotificationMessage msg) {
//        Log.d("NOTIFICATIONDEBUG","RECEIVED" + msg.getContent());
//        Log.d("NOTIFICATIONDEBUG","RECEIVED3" + msg.getContent());
//    }
//
//    @Override
//    public void onNotificationMessageClicked(Context context, GTNotificationMessage msg) {
//    }
//
//    private void startBackActivity() {
//        Log.d("RESTARTDEBUG","startBackActivity");
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        boolean tabletMode = preferences.getBoolean("tabletmode", false);
//        Intent intent = new Intent(context, WelcomeActivity.class);
//        if(tabletMode) {
//            intent = new Intent(context, WelcomeActivityTablet.class);
//        }
//        intent.setAction(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//        context.startActivity(intent);
//        startActivity();
//    }
//
//    private void startActivity() {
//        Log.d("videowtfdebug", "startVideoConsult");
//        Log.d("RESTARTDEBUG","startActivity");
//        Intent intent = new Intent(context, VideoCallActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
////        Log.d(CONSULT,"test " + docName + roomnumber + orgCode + providerId);
//        intent.putExtra("type", 0);
//        intent.putExtra("docName", docName);
//        intent.putExtra("roomNumber", roomnumber);
////        intent.putExtra("org_code", orgCode);
////        intent.putExtra("providerId", providerId);
//        context.startActivity(intent);
//    }
//
//
//    private AsyncTask developerDebugLog(final String message) {
//        return new AsyncTask<Void, Void, Integer>() {
//            Exception e;
//            private String timestamp;
//
//            @Override
//            protected void onPreExecute() {
//                DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
//                timestamp = df.format(Calendar.getInstance().getTime());
//            }
//
//            @Override
//            protected Integer doInBackground(Void... params) {
//                try {
//                    return WebService.getInstance().DeveloperDebugLog(((CDoctor2Application) context).getLoginInfo().getAccount()
//                            ,message,timestamp);
//                } catch (Exception e) {
//                    this.e = e;
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Integer integer) {
//                super.onPostExecute(integer);
//                if (e == null) {
//                    if (integer == 1) {
//                    }
//                } else {
//                }
//
//            }
//        }.execute();
//    }
//}
//
