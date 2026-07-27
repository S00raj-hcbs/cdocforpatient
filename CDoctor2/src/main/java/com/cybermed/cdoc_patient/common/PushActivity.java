package com.cybermed.cdoc_patient.common;

import android.app.Activity;
import android.os.Bundle;

import com.cybermed.cdoc_patient.R;

/**
 * Created by qinwe on 2017/5/9.
 */

public class PushActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
