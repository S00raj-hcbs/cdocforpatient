package com.cybermed.cdoc_patient.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.cybermed.cdoc_patient.R;

import static android.provider.Settings.ACTION_APPLICATION_SETTINGS;
import static com.cybermed.cdoc_patient.main.FragmentMainActivity.MY_CAMERA_AUDIO_REQUEST_CODE;

public class PermissionUtil {

    public static void checkCameraAudioPermission(Activity activity, Runnable havePermission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT ) != PackageManager.PERMISSION_GRANTED /*||
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED */) {

                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.BLUETOOTH_CONNECT/*,
                                Manifest.permission.READ_MEDIA_IMAGES*/},
                        MY_CAMERA_AUDIO_REQUEST_CODE);
            } else {
                if (havePermission != null)
                    havePermission.run();
            }
        }else {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED /*||
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED*/) {

                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION/*,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE*/},
                        MY_CAMERA_AUDIO_REQUEST_CODE);
            } else {
                if (havePermission != null)
                    havePermission.run();
            }
        }

    }

    public static void notificationCheck(Context context) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.request_notification_permission_title)
                    .setCancelable(false)
                    .setMessage(R.string.request_notification_permission_message)
                    .setPositiveButton(R.string.btn_ok, (d,w)->{
                        d.dismiss();
                        context.startActivity(new Intent(ACTION_APPLICATION_SETTINGS));
                    }).show();
        }
    }
}
