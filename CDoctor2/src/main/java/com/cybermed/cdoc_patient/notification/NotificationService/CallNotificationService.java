package com.cybermed.cdoc_patient.notification.NotificationService;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.onesignal.OneSignal;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CallNotificationService extends Service implements MediaPlayer.OnPreparedListener {
    private String CHANNEL_ID = CDoctor2Application.application.getString(R.string.app_name) + "CallChannel";
    private String CHANNEL_NAME = CDoctor2Application.application.getString(R.string.app_name) + "Call Channel";
    MediaPlayer mediaPlayer;
    Vibrator mvibrator;
    AudioManager audioManager;
    AudioAttributes playbackAttributes;
    private Handler handler;
    AudioManager.OnAudioFocusChangeListener afChangeListener;
    private boolean status = false;
    private boolean vstatus = false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle data = null;
        String name = "", callType = "";
        int NOTIFICATION_ID = 120;
        try {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            if (audioManager != null) {
                switch (audioManager.getRingerMode()) {
                    case AudioManager.RINGER_MODE_NORMAL:
                        status = true;
                        break;
                    case AudioManager.RINGER_MODE_SILENT:
                        status = false;
                        break;
                    case AudioManager.RINGER_MODE_VIBRATE:
                        status = false;
                        vstatus = true;
                        Log.e("Service!!", "vibrate mode");
                        break;
                }
            }

            if (status) {
                Runnable delayedStopRunnable = new Runnable() {
                    @Override
                    public void run() {
                        releaseMediaPlayer();
                    }
                };

                afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                    public void onAudioFocusChange(int focusChange) {
                        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                            // Permanent loss of audio focus
                            // Pause playback immediately
                            //mediaController.getTransportControls().pause();
                            if (mediaPlayer != null) {
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.pause();
                                }
                            }
                            // Wait 30 seconds before stopping playback
                            handler.postDelayed(delayedStopRunnable,
                                    TimeUnit.SECONDS.toMillis(30));
                        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                            // Pause playback
                        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                            // Lower the volume, keep playing
                        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                            // Your app has been granted audio focus again
                            // Raise volume to normal, restart playback if necessary
                        }
                    }
                };
                KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);


                mediaPlayer = MediaPlayer.create(this, R.raw.ring);
                mediaPlayer.setLooping(true);
                //mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    handler = new Handler();


                    playbackAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build();

                    AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                            .setAudioAttributes(playbackAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener(afChangeListener, handler)
                            .build();
                    int res = audioManager.requestAudioFocus(focusRequest);
                    if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        if (!keyguardManager.isDeviceLocked()) {

                            mediaPlayer.start();
                        }

                    }
                } else {

                    // Request audio focus for playback
                    int result = audioManager.requestAudioFocus(afChangeListener,
                            // Use the music stream.
                            AudioManager.STREAM_MUSIC,
                            // Request permanent focus.
                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        if (!keyguardManager.isDeviceLocked()) {
                            // Start playback
                            mediaPlayer.start();
                        }
                    }

                }

            } else if (vstatus) {
                mvibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Start without a delay
                // Each element then alternates between vibrate, sleep, vibrate, sleep...
                long[] pattern = {0, 250, 200, 250, 150, 150, 75, 150, 75, 150};

                // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
                mvibrator.vibrate(pattern, 0);
                Log.e("Service!!", "vibrate mode start");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (intent != null && intent.getExtras() != null) {

            data = intent.getExtras();
            name = data.getString("inititator");
            callType = "Video";


        }
        try {
            Intent receiveCallAction = new Intent(CDoctor2Application.application, CallNotificationActionReceiver.class);

                receiveCallAction.putExtra("ConstantApp.CALL_RESPONSE_ACTION_KEY", "ConstantApp.CALL_RECEIVE_ACTION");
            receiveCallAction.putExtra("ACTION_TYPE", "RECEIVE_CALL");
            receiveCallAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
            receiveCallAction.putExtra("type", 0);
            if (data != null) {
                receiveCallAction.putExtra("docName", data.getString("docName"));
                receiveCallAction.putExtra("callType", data.getString("callType"));
                receiveCallAction.putExtra("roomNumber", data.getString("roomNumber"));
            }
            receiveCallAction.setAction("RECEIVE_CALL");

            Intent cancelCallAction = new Intent(CDoctor2Application.application, CallNotificationActionReceiver.class);
            cancelCallAction.putExtra("ConstantApp.CALL_RESPONSE_ACTION_KEY", "ConstantApp.CALL_CANCEL_ACTION");
            cancelCallAction.putExtra("ACTION_TYPE", "CANCEL_CALL");
            cancelCallAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
            cancelCallAction.putExtra("type", 0);
            if (data != null) {
                cancelCallAction.putExtra("docName", data.getString("docName"));
                cancelCallAction.putExtra("callType", data.getString("callType"));
                cancelCallAction.putExtra("roomNumber", data.getString("roomNumber"));
            }
            cancelCallAction.setAction("CANCEL_CALL");

            Intent callDialogAction = new Intent(CDoctor2Application.application, CallNotificationActionReceiver.class);
            callDialogAction.putExtra("ACTION_TYPE", "DIALOG_CALL");
            callDialogAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
            callDialogAction.putExtra("type", 0);
            if (data != null) {
                callDialogAction.putExtra("docName", data.getString("docName"));
                callDialogAction.putExtra("callType", data.getString("callType"));
                callDialogAction.putExtra("roomNumber", data.getString("roomNumber"));
            }
            callDialogAction.setAction("DIALOG_CALL");

            PendingIntent receiveCallPendingIntent = null;
            PendingIntent cancelCallPendingIntent = null;
            PendingIntent callDialogPendingIntent = null;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                receiveCallPendingIntent = PendingIntent.getActivity(CDoctor2Application.application, 1200, receiveCallAction, PendingIntent.FLAG_IMMUTABLE);
                cancelCallPendingIntent = PendingIntent.getBroadcast(CDoctor2Application.application,1201, cancelCallAction, PendingIntent.FLAG_IMMUTABLE);
                callDialogPendingIntent = PendingIntent.getActivity(CDoctor2Application.application, 1202, callDialogAction, PendingIntent.FLAG_IMMUTABLE);
            }else {
                receiveCallPendingIntent = PendingIntent.getActivity(CDoctor2Application.application, 1200, receiveCallAction, PendingIntent.FLAG_UPDATE_CURRENT);
                cancelCallPendingIntent = PendingIntent.getBroadcast(CDoctor2Application.application,1201, cancelCallAction, PendingIntent.FLAG_UPDATE_CURRENT);
                callDialogPendingIntent = PendingIntent.getActivity(CDoctor2Application.application,1202, callDialogAction, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            createChannel();
            NotificationCompat.Builder notificationBuilder = null;
            OneSignal.getNotifications().clearAllNotifications();

            if (data != null) {
                // Uri ringUri= Settings.System.DEFAULT_RINGTONE_URI;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    OneSignal.getNotifications().clearAllNotifications();
                    if (data != null) {
                        // Uri ringUri= Settings.System.DEFAULT_RINGTONE_URI;
                        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                                .setContentTitle(name)
                                .setContentText("Incoming " + callType + " Call")
                                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setCategory(NotificationCompat.CATEGORY_CALL)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .addAction(R.drawable.btn_hangup, "Reject Call", cancelCallPendingIntent)
                                .addAction(R.drawable.btn_accept_call, "Accept Call", receiveCallPendingIntent)
                                .setAutoCancel(true)
                                .setFullScreenIntent(callDialogPendingIntent, true);
                    }
                    Notification incomingCallNotification = notificationBuilder.build();
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID,incomingCallNotification);
                    startForeground(NOTIFICATION_ID, incomingCallNotification);
                }else {
                    if (data != null) {
                        // Uri ringUri= Settings.System.DEFAULT_RINGTONE_URI;
                        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                                .setContentTitle(name)
                                .setContentText("Incoming " + callType + " Call")
                                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setCategory(NotificationCompat.CATEGORY_CALL)
                                .addAction(R.drawable.btn_hangup, "Reject Call", cancelCallPendingIntent)
                                .addAction(R.drawable.btn_accept_call, "Accept Call", receiveCallPendingIntent)
                                .setAutoCancel(true)
                                //.setSound(ringUri)
                                .setFullScreenIntent(callDialogPendingIntent, true);

                    }
                    Notification incomingCallNotification = notificationBuilder.build();
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID,incomingCallNotification);
                    startForeground(NOTIFICATION_ID, incomingCallNotification);
                }

            }

            /*Notification incomingCallNotification = null;
            if (notificationBuilder != null) {
                incomingCallNotification = notificationBuilder.build();
            }
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID,incomingCallNotification);
            startForeground(NOTIFICATION_ID, incomingCallNotification);*/
            incomingCallReceiver(data);
            startCancelCallTimer();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    /**
     * stop service if nobody accept/reject the call
     */
    public void startCancelCallTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                releaseMediaPlayer();
                releaseVibration();
                Intent iclose = new Intent(CDoctor2Application.application, CallNotificationActionReceiver.class);
                iclose.putExtra("ConstantApp.CALL_RESPONSE_ACTION_KEY", "ConstantApp.CALL_NO_ANSWER");
                iclose.putExtra("ACTION_TYPE", "CALL_NO_ANSWER");
                iclose.setAction("CALL_NO_ANSWER");
                sendBroadcast(iclose);
            }
        }, 30000);
    }

    /**
     * Incoming call
     * @param data
     */
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
        sendBroadcast(iclose);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();// release your media player here audioManager.abandonAudioFocus(afChangeListener);
        releaseMediaPlayer();
        releaseVibration();
    }

    public void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Uri ringUri = Settings.System.DEFAULT_RINGTONE_URI;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Call Notifications");
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
           /* channel.setSound(ringUri,
                    new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setLegacyStreamType(AudioManager.STREAM_RING)
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION).build());*/
                Objects.requireNonNull(CDoctor2Application.application.getSystemService(NotificationManager.class)).createNotificationChannel(channel);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void releaseVibration() {
        try {
            if (mvibrator != null) {
                if (mvibrator.hasVibrator()) {
                    mvibrator.cancel();
                }
                mvibrator = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseMediaPlayer() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }
                mediaPlayer = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }

}
