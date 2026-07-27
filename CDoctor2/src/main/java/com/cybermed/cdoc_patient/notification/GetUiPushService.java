//package com.cybermed.cdoc.notification;
//
///**
// * Created by joshu on 3/21/2018.
// */
//
//
//        import android.app.Service;
//        import android.content.Intent;
//        import android.os.IBinder;
//        import android.util.Log;
//
//        import com.igexin.sdk.GTServiceManager;
//        import com.igexin.sdk.PushManager;
//
//public class GetUiPushService extends Service {
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        GTServiceManager.getInstance().onCreate(this);
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        super.onStartCommand(intent, flags, startId);
//        int startcom = GTServiceManager.getInstance().onStartCommand(this, intent, flags, startId);
//        Log.d("pushnotificationdebug","startcommand() " + startcom);
//        return startcom;
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return GTServiceManager.getInstance().onBind(intent);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.d("NOTIDEBUG","ONDESTROY");
//        GTServiceManager.getInstance().onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        Log.d("NOTIDEBUG","LOW MEM");
//        GTServiceManager.getInstance().onLowMemory();
//    }
//}
