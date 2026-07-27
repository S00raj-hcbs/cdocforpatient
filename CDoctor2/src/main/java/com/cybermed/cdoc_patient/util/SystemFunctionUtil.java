package com.cybermed.cdoc_patient.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class SystemFunctionUtil {
    private static long lastClickTime = 0;
    private final static int clickInterval = 500;

    public static boolean checkDoubleClick() {
        if (SystemClock.elapsedRealtime() - lastClickTime < clickInterval) {
            return false;
        }
        lastClickTime = SystemClock.elapsedRealtime();
        return true;
    }

    public static void hideSoftKeyboard(Activity activity, IBinder token) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                token, 0);
    }

    public static boolean isHideInput(View v, MotionEvent ev) {
        if ((v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            return !(ev.getX() > left) || !(ev.getX() < right) || !(ev.getY() > top)
                    || !(ev.getY() < bottom);
        }
        return false;
    }

    private static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static Intent getStemoscopeAppIntent(Context context) {
        if(isStemoscopeAppExisted(context.getPackageManager())){
            Intent intent = new Intent();
            intent.setData(Uri.parse("stemoapp://app.stemoscope/path"));
            intent.putExtra("", "");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return intent;
        }else{
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.stemoscope.app&hl=en_US"));
        }
    }

    public static boolean isStemoscopeAppExisted(PackageManager packageManager) {
        return isPackageInstalled("com.stemoscope.app", packageManager);
    }
}
