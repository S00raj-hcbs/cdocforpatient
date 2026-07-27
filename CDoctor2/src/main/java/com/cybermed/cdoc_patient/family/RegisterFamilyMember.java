package com.cybermed.cdoc_patient.family;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.UserInfo;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.WebServiceID;
import com.cdfortis.datainterface.soap.model.Patient_Demographic;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.databinding.DialogRegisterFamilyMemberBinding;
import com.cybermed.cdoc_patient.doctor.doctorFilter.SpinnerAdapter;
import com.cybermed.cdoc_patient.doctor.doctorFilter.SpinnerModel;
import com.cybermed.cdoc_patient.login.signup.ValidationUtils;
import com.cybermed.cdoc_patient.modal.GeneralConsentForm;
import com.cybermed.cdoc_patient.util.ConsentForm;
import com.cybermed.cdoc_patient.util.ErrorMessage;
import com.cybermed.cdoc_patient.util.LocalizationUtil;
import com.cybermed.cdoc_patient.util.SystemFunctionUtil;
import com.jpardogo.android.googleprogressbar.library.FoldingCirclesDrawable;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.cdfortis.datainterface.soap.WebServiceID.CreateNewUser_Android_v2;
import static com.cdfortis.datainterface.soap.WebServiceID.Upload_Patient_Consent;
import static com.cdfortis.datainterface.soap.WebServiceID.get_PatientDemographic_Android;

public class RegisterFamilyMember extends Dialog {

   public static interface OnRegisterSuccess {
        void onSuccess(String userId, String pwd);
    }

    private String firstName;
    private String lastName;
    private String gender;
    private String dob;
    private String zip_code;
    private String service_code;

    private static final int[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
    private boolean isPassRevealed=false;
    private List<SpinnerModel> stateList;
    private OnRegisterSuccess onRegisterSuccess;
    private Activity activity;
    DialogRegisterFamilyMemberBinding binding;

    private AlertDialog dialog;
    private ProgressDialog pd;
    private List<String> consentFormInB64 = new ArrayList<>();
    public RegisterFamilyMember(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static RegisterFamilyMember newInstance(Activity activity, OnRegisterSuccess onRegisterSuccess) {
        RegisterFamilyMember dialog = new RegisterFamilyMember(activity, R.style.Theme_AppCompat_DayNight);
        dialog.onRegisterSuccess = onRegisterSuccess;
        dialog.activity = activity;
        return dialog;
    }

    public void setBasicInfo(String firstName, String lastName, String gender, String dob, String zip_code) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dob = dob;
        this.zip_code = zip_code;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         binding = DialogRegisterFamilyMemberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getContext().getResources().getColor(R.color.color_1782ba));
        initViews();
        initDefaultValues();
    }

    private void initViews() {

        String[] stateArray = getContext().getResources().getStringArray(R.array.state2);
        stateList = new ArrayList<>();
        for (String state : stateArray)
            stateList.add(new SpinnerModel(state, false));


        binding.editState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStateOptionDialog();
            }
        });
        binding.editMail.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                String emailStr = binding.editMail.getText().toString().trim();
                if (!ValidationUtils.isEmailValid(emailStr)) {
                    binding.editMail.setError(getContext().getString(R.string.regist_error_email));
                } else {
                    OnPostExecute ope = result -> {
                        if (result.toString().equals("1")) {
                         //  binding.emailCheck.setVisibility(View.VISIBLE);
                        } else {
                            binding.editMail.setError(getContext().getString(R.string.regist_error_email));
                        }
                    };
                    WebService.webServiceAsyncTask(WebServiceID.Check_Duplicate_Email_Address, ope, emailStr);
                }
            } else {
              //  binding.emailCheck.setVisibility(View.GONE);
            }
        });
        binding.editPwd.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String pwdStr = binding.editPwd.getText().toString();
                if (TextUtils.isEmpty(pwdStr)) {
                    binding.editPwd.setError(getContext().getString(R.string.regist_error_password)/*, errorIcon()*/);
                    //binding.editPwd.requestFocus();
                    return;
                }

                if (pwdStr.length() < 5) {
                    binding.editPwd.setError(getContext().getString(R.string.regist_error_password_short));
                  //  binding.editPwd.requestFocus();
                    binding.editPwd.setText("");
                    return;
                }
               // binding.passCheck.setVisibility(View.VISIBLE);
            } else {
                //binding.passCheck.setVisibility(View.GONE);
            }
        });
        binding.editConfirmPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                String pwdStr =binding.editPwd.getText().toString();
                String confirmPwdStr = binding.editConfirmPassword.getText().toString();
                if (TextUtils.isEmpty(confirmPwdStr)) {
                    binding.editConfirmPassword.setError(getContext().getString(R.string.regist_error_confirm_password));
                    //binding.editConfirmPassword.requestFocus();
                    return;
                }

                if (!confirmPwdStr.equals(pwdStr)) {
                    binding.editConfirmPassword.setError(getContext().getString(R.string.regist_error_confirm_password_match));
                   // binding.editConfirmPassword.setError(getContext().getString(R.string.regist_error_confirm_password_match));
                    //binding.editConfirmPassword.requestFocus();
                    return;
                }

               // binding.confirmPasswordCheck.setVisibility(View.VISIBLE);
            } else {
               // binding.confirmPasswordCheck.setVisibility(View.GONE);
            }
        });
        binding.infoPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > before) {
                    if (s.length() == 3) {
                        binding.infoPhone.setText(s + "-");
                        binding.infoPhone.setSelection(binding.infoPhone.getText().length());
                    } else if (s.length() == 7) {
                        binding.infoPhone.setText(s + "-");
                        binding.infoPhone.setSelection(binding.infoPhone.getText().length());
                    }
                } else if (before > count) {
                    if (s.length() == 7) {
                        s = s.subSequence(0, 6);
                        binding.infoPhone.setText(s);
                        binding.infoPhone.setSelection(binding.infoPhone.getText().length());
                    } else if (s.length() == 3) {
                        s = s.subSequence(0, 2);
                        binding.infoPhone.setText(s);
                        binding.infoPhone.setSelection(binding.infoPhone.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.backBtn.setOnClickListener(v -> {
            onBackPressed();
        });
        binding.passwordReveal.setOnClickListener(v -> {
            if(isPassRevealed) {
                isPassRevealed = false;
                binding.editPwd.setTransformationMethod(new PasswordTransformationMethod());
                binding.editPwd.setSelection(binding.editPwd.length());

                binding.editConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                binding.editConfirmPassword.setSelection(binding.editConfirmPassword.length());
                binding.passwordReveal.setImageResource(R.drawable.pass_hide);
            } else {
                isPassRevealed = true;
                binding.editPwd.setTransformationMethod(null);
                binding.editPwd.setSelection(binding.editPwd.length());

                binding.editConfirmPassword.setTransformationMethod(null);
                binding.editConfirmPassword.setSelection(binding.editConfirmPassword.length());
                binding.passwordReveal.setImageResource(R.drawable.pass_show);
            }
        });
    }

    private Drawable errorIcon() {
        Drawable errorIcon = getContext().getResources().getDrawable(R.drawable.xmark_signup);
        errorIcon.setBounds(0, 0, 60, 60);
        return errorIcon;
    }

    private void initDefaultValues() {
        String originalUserId = CDoctor2Application.getLoginInfo().getOriginalAccount();
        OnPostExecute ope = result -> {
            Patient_Demographic patientInfo = new Patient_Demographic((SoapObject) result);

            UserInfo userInfo = new UserInfo();
            userInfo.deserialize(patientInfo);

            service_code = userInfo.getService_code();
            binding.infoPhone.setText(userInfo.getPhoneNum());
            binding.infoAddr1.setText(userInfo.getAddr1());
            binding.infoAddr2.setText(userInfo.getAddr2());
            binding.editCity.setText(userInfo.getCity());
            binding.editState.setText(userInfo.getState());
            binding.editZipCode.setText(userInfo.getZip());

            binding.registerFamily.setOnClickListener(v -> {
                if(checkInput()) {
                    showConsentForm();
                }
            });
        };

        WebService.webServiceAsyncTask(get_PatientDemographic_Android, ope, originalUserId);
    }

    private void register() {
        String emailStr = binding.editMail.getText().toString().trim().toLowerCase();
        String pwdStr = binding.editPwd.getText().toString();
        String phoneNum = binding.infoPhone.getText().toString();
        String addr1Str = binding.infoAddr1.getText().toString().trim();
        String addr2Str = binding.infoAddr2.getText().toString().trim();
        String cityStr = binding.editCity.getText().toString().trim();
        String stateStr = binding.editState.getText().toString().trim();
        String zipStr = binding.editZipCode.getText().toString().trim();
        String mobile_mode = LocalizationUtil.getLocalizedResources(getContext(), Locale.US).getString(R.string.TeleHealth_Mode);
        pd = new ProgressDialog(getContext());
        pd.setCancelable(false);
        pd.setIndeterminateDrawable(new FoldingCirclesDrawable(colors));
        pd.setTitle(getContext().getString(R.string.registering_title));
        OnPostExecute ope = result -> {
            String res = result.toString();
            if(res.equals("1")) {
                if (pd != null)
                    pd.cancel();
                if(onRegisterSuccess != null) {
                    onRegisterSuccess.onSuccess(emailStr, pwdStr);
                    Upload_Patient_Consent.setDisableNullRestriction(true);
                    for (String form : consentFormInB64)
                        WebService.webServiceAsyncTask(Upload_Patient_Consent, emailStr, "PDF", service_code, form);
                }
                dismiss();
            } else {
                if (pd != null)
                    pd.cancel();
                ErrorMessage.alertDialog(getContext(), getContext().getString(R.string.signup_fail_heading), getContext().getString(R.string.signup_fail_paragraph), null);
            }
        };
        pd.show();

        progressDialogTimeout();
        WebService.webServiceAsyncTask(CreateNewUser_Android_v2, ope, emailStr, pwdStr, firstName, "", lastName, gender, dob, addr1Str, addr2Str, cityStr
                , stateStr, zipStr, phoneNum, service_code, mobile_mode);

    }

    private void progressDialogTimeout() {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (pd != null)
                pd.cancel();
        }).start();
    }


    private boolean checkInput() {
        String emailStr = binding.editMail.getText().toString().trim().toLowerCase();
        String pwdStr = binding.editPwd.getText().toString();
        String confirmPwdStr = binding.editConfirmPassword.getText().toString();
        String phoneNum = binding.infoPhone.getText().toString();
        String addr1Str = binding.infoAddr1.getText().toString().trim();
        String cityStr = binding.editCity.getText().toString().trim();
        String stateStr = binding.editState.getText().toString().trim();
        String zipStr = binding.editZipCode.getText().toString().trim();

        if (!ValidationUtils.isEmailValid(emailStr)) {
            binding.editMail.setError(getContext().getString(R.string.regist_error_email));
            binding.editMail.requestFocus();
            return false;
        }
        if(pwdStr.length() < 5) {
            binding.editPwd.setError(getContext().getString(R.string.regist_error_password_short));
            binding.editPwd.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(confirmPwdStr)) {
            binding.editConfirmPassword.setError(getContext().getString(R.string.regist_error_confirm_password));
            binding.editConfirmPassword.requestFocus();
            return false;
        }
        if(!pwdStr.equals(confirmPwdStr)) {
            binding.editConfirmPassword.setError(getContext().getString(R.string.regist_error_confirm_password_match));
            binding.editConfirmPassword.requestFocus();
            return false;
        }
        if(phoneNum.length() != 12) {
            binding.infoPhone.setError(getContext().getString(R.string.regist_error_phone));
            binding.infoPhone.requestFocus();
            return false;
        }
        if(addr1Str.isEmpty()) {
            binding.infoAddr1.setError(getContext().getString(R.string.regist_error_addr1));
            binding.infoAddr1.requestFocus();
            return false;
        }
        if(cityStr.isEmpty()) {
            binding.editCity.setError(getContext().getString(R.string.regist_error_city));
            binding.editCity.requestFocus();
            return false;
        }
        if(stateStr.length() != 2) {
            binding.editState.setError(getContext().getString(R.string.regist_error_state));
            binding.editState.requestFocus();
            return false;
        }
        if(zipStr.length() != 5) {
            binding.editZipCode.setError(getContext().getString(R.string.regist_error_zip_verify));
            binding.editZipCode.requestFocus();
            return false;
        }

        return true;
    }

    private void showConsentForm() {
       /* AlertDialog.Builder adb = new AlertDialog.Builder(getContext())
                .setTitle(getContext().getString(R.string.consent_form_title))
                .setPositiveButton(getContext().getString(R.string.btn_accept), (dialog, which) -> {
                    dialog.cancel();
                 //   register();
                })
                .setNegativeButton(getContext().getString(R.string.btn_decline), (dialog, which) -> dialog.cancel());*/
/*
        String message = "CyberMed Health Inc, also known as CDoc (\"CDoc\"), are providing you, the patient, with an easy way to reach a provider in your medical group through the use of telemedicine. Telemedicine involves a voice and video experience with the physician.\n" +
                "\n" +
                "The purpose of this form is to obtain your approval to permit this type of audio/video call.\n" +
                "\n" +
                "When you use any CyberMed Health Inc (“CDOC”) Service, or send e‐mails, text messages, and other communications from your desktop or mobile device to us, you are communicating with us electronically. You consent to receive communications from us electronically. You agree that (a) all agreements and consents can be signed electronically and (b) all notices, disclosures, and other communications that we provide to you electronically satisfy any legal requirement that such notices and other communications be in writing.\n" +
                "\n" +
                "When you register for this program, we will need to obtain your email address and phone number. CDoc commits to keeping this information confidential and will not use it, or share it with anyone outside CyberMed Health Inc..\n";

        adb.setMessage(message).show();*/

        new ConsentForm(activity, new GeneralConsentForm("Telehealth Consent Form"), new ConsentForm.ConsentFormCallBack() {
            @Override
            public void successWithPDF(String b64_pdf) {
                consentFormInB64.add(b64_pdf);
                register();
            }

        }).ShowConsentForm();
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        View view = getCurrentFocus();
        if(SystemFunctionUtil.isHideInput(view, ev)) {
            SystemFunctionUtil.hideSoftKeyboard(activity, view.getWindowToken());
        }
        return super.dispatchTouchEvent(ev);
    }

    private void showStateOptionDialog() {


        LayoutInflater layoutInflaterAndroid = getLayoutInflater();
        View mView = layoutInflaterAndroid.inflate(R.layout.custom_spinner_drop_down_list_view, null);
        RecyclerView filters = mView.findViewById(R.id.listItems);
        TextView title = mView.findViewById(R.id.title);
        ImageView imgClose=mView.findViewById(R.id.closeBtn);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        title.setText(R.string.select_state_title);

        //in this case there is no okay button
        mView.findViewById(R.id.okayBtn).setVisibility(View.GONE);

        SpinnerAdapter adapter = new SpinnerAdapter(stateList, SpinnerAdapter.Source.Familysignup);
        adapter.setDialogue(this);
        filters.setAdapter(adapter);
        filters.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        filters.setHasFixedSize(true);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(mView);
        dialog = alertDialogBuilder.create();
        dialog.show();
    }
    public void setState(String state) {
        binding.editState.setText(state);
        dialog.dismiss();
    }
}
