package com.cybermed.cdoc_patient.view;

import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.cybermed.cdoc_patient.R;

public class CdocProgressBar extends AlertDialog {

    public CdocProgressBar(@NonNull Context context) {
        super(context);
    }


    @Override
    public void show() {
        super.show();
        setContentView(R.layout.view_text_loader);
    }
}
