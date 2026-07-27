package com.cybermed.cdoc_patient.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;

import com.cybermed.cdoc_patient.R;

import androidx.core.app.NotificationCompat;

import static com.cybermed.cdoc_patient.notification.MyNotificationReceivedHandler.CDOC_CHANNEL_ID;
import static com.cybermed.cdoc_patient.notification.MyNotificationReceivedHandler.CDOC_CHANNEL_NAME;

public class AppointmentNotificationService extends Service {
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    private static final int APPOINTMENT_NOTIFICATION = 1001;
    public static final String APPOINTMENT_ALARM_APPOINTMENT_DATE = "appointment_date";


    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            makeNotification();

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        HandlerThread thread = new HandlerThread("AppointmentNotificationService",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    void makeNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CDOC_CHANNEL_ID)
                .setSmallIcon(R.drawable.cdoc_icon)
                .setContentTitle("Appointment Notification")
                .setContentText("There's an appointment upcoming in 15 minutes")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CDOC_CHANNEL_ID, CDOC_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(APPOINTMENT_NOTIFICATION, mBuilder.build());
    }
}