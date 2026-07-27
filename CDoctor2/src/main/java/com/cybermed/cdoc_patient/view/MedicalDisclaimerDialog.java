package com.cybermed.cdoc_patient.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cybermed.cdoc_patient.R;

public class MedicalDisclaimerDialog {

    public static void show(Context context,
                            String title,
                            String description,
                            String buttonText,
                            boolean isCancelShow,
                            Runnable onContinue,
                            Runnable onCancel) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_medical_disclaimer, null);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
        }

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        Button btnContinue = view.findViewById(R.id.btnContinue);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        tvTitle.setText(title);
        tvMessage.setText(description);
        btnContinue.setText(buttonText);
        if (isCancelShow){
            btnCancel.setVisibility(View.VISIBLE);
            btnCancel.setOnClickListener(v -> {
                dialog.dismiss();
                if (onCancel != null) onCancel.run();
            });
        }

        btnContinue.setOnClickListener(v -> {
            dialog.dismiss();
            if (onContinue != null) onContinue.run();
        });

        dialog.show();
    }
}