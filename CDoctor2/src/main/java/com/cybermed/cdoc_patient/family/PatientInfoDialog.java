package com.cybermed.cdoc_patient.family;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.WebServiceID;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.util.ErrorMessage;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.util.Calendar;
import java.util.Date;

public class PatientInfoDialog {

    private Activity activity;
    private AlertDialog dialog;
    private ButtonCallBack callBack;

    private EditText firstName;
    private EditText lastName;

    private String gender = "M";
    private EditText mTxtBirth;
    private EditText zipCode;
    private String dob;
    private int selectMonth = 0, selectDay = 1, selectYear = 2000;
    private Button searchFamily;
    private RadioGroup radioGroup;
    View dialogView;


    public interface ButtonCallBack {
        void onPatientExist(String userId,String fullname);

        void onPatientNotExist(String firstName, String lastName, String gender, String dob, String zip_code);
    }

    public PatientInfoDialog(Activity activity) {
        this.activity = activity;

        dialog = new AlertDialog.Builder(activity).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_add_family_by_info, null);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        initViews(dialogView);

        dialog.setView(dialogView);
        dialog.show();
    }

    public void setCallBack(ButtonCallBack callBack) {
        this.callBack = callBack;
    }

    private void initViews(View dialogView) {
        firstName = dialogView.findViewById(R.id.editFirstName);
        lastName = dialogView.findViewById(R.id.editLastName);
        // tvMale = dialogView.findViewById(R.id.tvMale);
        //tvFemale = dialogView.findViewById(R.id.tvFemale);
        zipCode = dialogView.findViewById(R.id.editZipCode);
        mTxtBirth = dialogView.findViewById(R.id.txtBirth);
        searchFamily = dialogView.findViewById(R.id.btn_search_family);
        dialogView.findViewById(R.id.imgclose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        radioGroup = dialogView.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.tvMale:
                    gender = "M";
                    break;
                case R.id.tvFemale:
                    gender = "F";
                    break;
                case R.id.tvOther:
                    gender = "U";
            }
        });

        mTxtBirth.setOnClickListener(v -> {
            showDatePicker();
        });
        searchFamily.setOnClickListener(v -> {
            searchAccountByInfo();
        });


    }

    public void dismiss() {
        dialog.dismiss();
    }

    private void showDatePicker() {

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int curr_year = c.get(Calendar.YEAR);
        int curr_month = c.get(Calendar.MONTH);
        int curr_day = c.get(Calendar.DAY_OF_MONTH);

        new SpinnerDatePickerDialogBuilder()
                .context(activity)
                .callback((view, year, monthOfYear, dayOfMonth) -> {
                    String tempMonth, tempDay;
                    if ((monthOfYear + 1) < 10)
                        tempMonth = "0" + (monthOfYear + 1);
                    else
                        tempMonth = (monthOfYear + 1) + "";

                    if (dayOfMonth < 10)
                        tempDay = "0" + dayOfMonth;
                    else
                        tempDay = dayOfMonth + "";

                    selectDay = dayOfMonth;
                    selectMonth = monthOfYear;
                    selectYear = year;

                    dob = year + "-" + tempMonth + "-" + tempDay;

                    mTxtBirth.setText(tempMonth + "/" + tempDay + "/" + year);
                })
                .spinnerTheme(R.style.DatePickerSpinner)
                .defaultDate(selectYear, selectMonth, selectDay)
                .maxDate(curr_year, curr_month, curr_day)
                .build()
                .show();
    }

    private void searchAccountByInfo() {
        String first_name = firstName.getText().toString().trim();
        String last_name = lastName.getText().toString().trim();
        String zip_code = zipCode.getText().toString();
        if (checkInput()) {
            OnPostExecute ope = result -> {
                String res = result.toString();
                if (res.equals("-1")) {
                    callBack.onPatientNotExist(first_name, last_name, gender, dob, zip_code);
                } else {
                    dismiss();
                    callBack.onPatientExist(res,first_name+" "+last_name);
                }
            };
            WebService.webServiceAsyncTask(WebServiceID.get_user_id_by_patient_info, ope, first_name, last_name, dob, gender, zip_code);
        } else {
            ErrorMessage.alertDialog(activity, activity.getString(R.string.error_dialog_title), activity.getString(R.string.please_fill_forms), null);
        }
    }

    private boolean checkInput() {
        String first_name = firstName.getText().toString().trim();
        String last_name = lastName.getText().toString().trim();
        String zip_code = zipCode.getText().toString();
        if (first_name.isEmpty() || last_name.isEmpty()) {
            return false;
        }
        if (zip_code.length() != 5) {
            return false;
        }
        if (dob == null) {
            return false;
        }
        return true;
    }


}
