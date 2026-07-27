package com.cybermed.cdoc_patient.util;

import android.content.Context;
import android.widget.Toast;

public class MyToast {

    public static void myShortToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
