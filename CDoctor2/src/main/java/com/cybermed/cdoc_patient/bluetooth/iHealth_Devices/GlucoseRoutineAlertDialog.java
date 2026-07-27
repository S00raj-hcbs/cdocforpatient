package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.WebServiceID;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.databinding.DialogGlucoseRoutineBinding;
import com.cybermed.cdoc_patient.util.DateUtil.MyTime;
import com.cybermed.cdoc_patient.util.ErrorMessage;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Glucometer.GlucoseDailyRoutine.bedTime;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Glucometer.GlucoseDailyRoutine.breakfastTime;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Glucometer.GlucoseDailyRoutine.dinnerTime;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Glucometer.GlucoseDailyRoutine.lunchTime;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Glucometer.GlucoseDailyRoutine.wakeUpTime;

public class GlucoseRoutineAlertDialog implements View.OnClickListener {

    private AlertDialog dialog;
    private Context context;
    private static int interval = 0 * 60;


    DialogGlucoseRoutineBinding binding;

    public static GlucoseRoutineAlertDialog newInstance(Context context) {
        GlucoseRoutineAlertDialog dialog = new GlucoseRoutineAlertDialog(context);
        dialog.binding.wakeUpTime.setText(wakeUpTime.toString());
        dialog.binding.breakfastTime.setText(breakfastTime.toString());
        dialog.binding.lunchTime.setText(lunchTime.toString());
        dialog.binding.dinnerTime.setText(dinnerTime.toString());
        dialog.binding.bedTime.setText(bedTime.toString());
        return dialog;
    }

    private GlucoseRoutineAlertDialog(Context context) {
        this.context = context;


        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        binding = DataBindingUtil.inflate(layoutInflaterAndroid,R.layout.dialog_glucose_routine,null,false);


        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
//        alertDialogBuilderUserInput.setView(mView);

//        alertDialogBuilderUserInput
//                .setCancelable(false)
//                .setPositiveButton(context.getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialogBox, int id) {
//                                send();
//                                dismiss();
//                            }
//                })
//
//                .setNegativeButton(context.getString(R.string.btn_cancel), (dialogBox, id) -> dismiss());
//        dialog = alertDialogBuilderUserInput.create();

        //set ratio of views


//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int adjustedHeight = (int) (displayMetrics.heightPixels * 0.7);
//        int adjustedWidth = (int) (displayMetrics.widthPixels * 0.95);
//        ViewGroup.LayoutParams lp = binding.outerLayout.getLayoutParams();
//        lp.width = adjustedWidth;
//        lp.height = adjustedHeight;
//        binding.outerLayout.setLayoutParams(lp);

        binding.wakeUpIv.setOnClickListener(this);
        binding.breakfastIv.setOnClickListener(this);
        binding.lunchIv.setOnClickListener(this);
        binding.dinnerIv.setOnClickListener(this);
        binding.bedIv.setOnClickListener(this);
        binding.cancelBtn.setOnClickListener(this);
        binding.okayBtn.setOnClickListener(this);

        dialog = alertDialogBuilderUserInput.setView(binding.getRoot()).create();

    }

    public void setSizeAccordingWindow(float widthRatio, float heightRatio) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;
        dialog.getWindow().setLayout((int) (displayWidth * widthRatio), (int) (displayHeight * heightRatio));
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        if (binding != null) {
            binding.unbind();
        }
        dialog.dismiss();
    }


    private void toast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    private void startTimePickerDialog(int hourOfDay, int minute, TimePickerDialog.OnTimeSetListener listener) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, listener, hourOfDay, minute, false);
        timePickerDialog.show();
    }

    private void changeButtonColor(Button button) {

    }


    private void send() {
        OnPostExecute ope = new OnPostExecute() {
            @Override
            public void onPostExecute(Object result) {
                String res = result.toString();
                if (res.equals("1")) {
                    toast("Routine Set");
                } else {
                    toast("Failed, please set again");
                }
            }
        };
        final String userId = CDoctor2Application.getLoginInfo().getAccount();
        String wut = wakeUpTime.toString();
        String bft = breakfastTime.toString();
        String lt = lunchTime.toString();
        String dt = dinnerTime.toString();
        String bt = bedTime.toString();

        WebService.webServiceAsyncTask(WebServiceID.save_patient_routine_default_message, ope, userId, wut, bft, lt, dt, bt);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wake_up_iv:
                startTimePickerDialog(wakeUpTime.hourOfDay, wakeUpTime.minute, (timePicker, i, i1) -> {
                    MyTime temp = new MyTime(i, i1);
                    if (temp.compareTo(breakfastTime) < interval) {
                        wakeUpTime.setTime(i, i1);
                        binding.wakeUpTime.setText(wakeUpTime.toString());
                    } else {
                        ErrorMessage.alertDialog(context, "Warning", "Please Select a Correct Time", null);
                    }
                });
                break;
            case R.id.breakfast_iv:
                startTimePickerDialog(breakfastTime.hourOfDay, breakfastTime.minute, (timePicker, i, i1) -> {
                    MyTime temp = new MyTime(i, i1);
                    if (temp.compareTo(lunchTime) < interval && temp.compareTo(wakeUpTime) > interval) {
                        breakfastTime.setTime(i, i1);
                        binding.breakfastTime.setText(breakfastTime.toString());
                    } else {
                        ErrorMessage.alertDialog(context, "Warning", "Please Select a Correct Time", null);
                    }
                });
                break;
            case R.id.lunch_iv:
                startTimePickerDialog(lunchTime.hourOfDay, lunchTime.minute, (timePicker, i, i1) -> {
                    MyTime temp = new MyTime(i, i1);
                    if (temp.compareTo(dinnerTime) < interval && temp.compareTo(breakfastTime) > interval) {
                        lunchTime.setTime(i, i1);
                        binding.lunchTime.setText(lunchTime.toString());
                    } else {
                        ErrorMessage.alertDialog(context, "Warning", "Please Select a Correct Time", null);
                    }
                });
                break;
            case R.id.dinner_iv:
                startTimePickerDialog(dinnerTime.hourOfDay, dinnerTime.minute, (timePicker, i, i1) -> {
                    MyTime temp = new MyTime(i, i1);
                    if ((bedTime.hourOfDay > 12 && temp.compareTo(bedTime) < interval && temp.compareTo(lunchTime) > interval)
                            || (bedTime.hourOfDay <= 12 && temp.compareTo(lunchTime) > interval)) {
                        dinnerTime.setTime(i, i1);
                        binding.dinnerTime.setText(dinnerTime.toString());
                    } else {
                        ErrorMessage.alertDialog(context, "Warning", "Please Select a Correct Time", null);
                    }
                });
                break;
            case R.id.bed_iv:
                startTimePickerDialog(bedTime.hourOfDay, bedTime.minute, (timePicker, i, i1) -> {
                    MyTime temp = new MyTime(i, i1);
                    if ((temp.hourOfDay >= 12 && temp.compareTo(dinnerTime) > interval) || temp.hourOfDay < 12 && temp.compareTo(bedTime) < interval) {
                        bedTime.setTime(i, i1);
                        binding.bedTime.setText(bedTime.toString());
                    } else {
                        ErrorMessage.alertDialog(context, "Warning", "Please Select a Correct Time", null);
                    }
                });
                break;
            case R.id.okayBtn:
                send();
            case R.id.cancel_btn:
                dismiss();
        }
    }
}
