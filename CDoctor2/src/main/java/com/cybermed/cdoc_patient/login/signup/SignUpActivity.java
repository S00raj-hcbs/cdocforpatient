package com.cybermed.cdoc_patient.login.signup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.WebService;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.CommonAsyncTaskActivity;
import com.cybermed.cdoc_patient.databinding.ActivitySignUpBinding;
import com.cybermed.cdoc_patient.login.LoginActivity;
import com.cybermed.cdoc_patient.login.ProceedLogin;
import com.cybermed.cdoc_patient.login.viewmodel.SignUpVM;
import com.cybermed.cdoc_patient.modal.GeneralConsentForm;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;
import com.cybermed.cdoc_patient.util.ConsentForm;
import com.cybermed.cdoc_patient.util.LocalizationUtil;
import com.cybermed.cdoc_patient.view.MyAlertDialog;
import com.cybermed.cdoc_patient.view.RegistNoteDialog;
import com.jpardogo.android.googleprogressbar.library.FoldingCirclesDrawable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.cdfortis.datainterface.soap.WebServiceID.Check_Duplicate_Email_Address;
import static com.cdfortis.datainterface.soap.WebServiceID.CreateNewUser_Android_v2;
import static com.cdfortis.datainterface.soap.WebServiceID.Notify_Patient;
import static com.cdfortis.datainterface.soap.WebServiceID.RecordConsentAccepted_Andriod;
import static com.cdfortis.datainterface.soap.WebServiceID.Upload_Patient_Consent;
import static com.cdfortis.datainterface.soap.WebServiceID.check_patient_service_code;

/**
 * Created by joshu on 9/11/2017.
 */

public class SignUpActivity extends CommonAsyncTaskActivity {

    private SignUp0Fragment signup0Fragment;
    private SignUp1Fragment signup1Fragment;
    private SignUp2Fragment signup2Fragment;
    private SignUp3Fragment signup3Fragment;

    private Drawable errorMsgIcon;
    private boolean newOrgCode;
    private List<String> consentFormInB64 = new ArrayList<>();


    SignUpVM signUpVM;
    ActivitySignUpBinding binding;
    RegistNoteDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        /*TODO: This has to be removed when production*/
        //WebService.getInstance().switchToQaSite();
        /*if (SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()){

            }else {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                    startActivityForResult(intent, PERMISSION_Storage);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, PERMISSION_Storage);
                }
            }
        }*/
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        signUpVM = new ViewModelProvider(this).get(SignUpVM.class);
        checkDataConnectionAndVersion(0);

        signup0Fragment = new SignUp0Fragment();
        signup1Fragment = new SignUp1Fragment();
        signup2Fragment = new SignUp2Fragment();
        signup3Fragment = new SignUp3Fragment();

        replaceFragmentWithAnimation(signup0Fragment, "0");

        initNextBtn();
        registerObeserver();
        binding.txtsignUp.setOnClickListener(v -> backButton());
        binding.txtSignin.setOnClickListener(v -> finish());
        binding.imgClose.setOnClickListener(v -> finish());

    }

    private void registerObeserver() {
        signUpVM.getApiResponse().observe(this, liveAction -> {
            switch (liveAction.getLiveActionEvent()) {
                case EVENT_CHECK_EMAIL_DUPLICATE:


                    break;

            }
        });
        signUpVM.getMoveBack().observe(this, aBoolean -> backButton());
        signUpVM.getMoveNext().observe(this, aBoolean -> initNextBtn());
        signUpVM.getComplete().observe(this, aBoolean -> initCompleteBtn());
        signUpVM.getCliniCodePopUp().observe(this, aBoolean -> serviceCodeEmptyErrorAlert());

    }

    private void initNextBtn() {
        switch (getSupportFragmentManager().getBackStackEntryCount()) {
            case 1:
                //binding.titleLabel.setText("Select Mode");
                if (!signUpVM.getClinicCode().getValue().equals("")) {
                    checkServiceCode(signUpVM.getClinicCode().getValue().toLowerCase().trim());
                } else {
                    serviceCodeEmptyErrorAlert();
                }
                break;
            case 2:
                //binding.titleLabel.setText("Basic Information");
                if (signUpVM.validationBasicInfoCheck()) {
                    checkEmailDuplication(signUpVM.getEmail().getValue());
                }
                break;
            case 3:
                //binding.titleLabel.setText("Account Information");
                if (signUpVM.validationAccountInfoCheck()) {
                    trasitionToPage(3);
                }
                break;
        }
    }

    void addProgress() {
        int progress = binding.progressIndicator.getProgress();
        binding.progressIndicator.setProgress(progress + 1);
    }

    void backProgress() {
        int progress = binding.progressIndicator.getProgress();
        binding.progressIndicator.setProgress(progress - 1);
    }

    private void initCompleteBtn() {
        if (signUpVM.validationContactInfoCheck())
            consentFormDialog();
    }

    private void registerUser() {
        String email, password, firstName, miName, lastName, phone, gender, dob,
                infoaddr1, infoaddr2, infocity, infostate, infozipcode, serviceCode, mobile_mode;

        email = signUpVM.getEmail().getValue();
        password = signUpVM.getPassword().getValue();
        serviceCode = signUpVM.getClinicCode().getValue().toLowerCase().trim();
        firstName = signUpVM.getFirstName().getValue();
        lastName = signUpVM.getLastName().getValue();
        miName = "";
        gender = signUpVM.getGender().getValue();
        dob = signUpVM.getDob().getValue();
        phone = signUpVM.getMobile().getValue();
        infoaddr1 = signUpVM.getAddressLine1().getValue();
        infoaddr2 = signUpVM.getAddressLine2().getValue();
        infocity = signUpVM.getCity().getValue();
        infostate = signUpVM.getState().getValue();
        infozipcode = signUpVM.getZipCode().getValue();
        mobile_mode = signUpVM.getModeSelected().getValue();

        userRegist(email, password, firstName, miName, lastName, gender, dob, infoaddr1, infoaddr2, infocity, infostate, infozipcode, phone, serviceCode, mobile_mode);

        //Default state for riverside patients
        if (serviceCode.equals("riverside")) {
            newOrgCode = true;
            /*Ready to change*/
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("state_key", "NJ");
            editor.commit();
            /*Ready to change*/
        } else {
            newOrgCode = false;
            //Default state for other patients
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("state_key", infostate);
            editor.commit();
        }
    }


    private ProgressDialog pd;
    private static final int[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};

    private void userRegist(String email, String password,
                            String first_name, String mi, String last_name, String sex,
                            String dOB, String addr1, String addr2, String city, String state,
                            String zip, String phone_number, String serviceCode, String mobile_mode) {

        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setIndeterminateDrawable(new FoldingCirclesDrawable(colors));
        pd.setTitle(getString(R.string.registering_title));

        OnPostExecute ope = result -> {

            int integer = Integer.valueOf(result.toString());

            if (integer == 1) {

                /*TODO: This is for email confirmation*/
                //WebService.webServiceAsyncTask(Send_Patient_Activation_Email, email);

                RecordConsentAccepted(email);
            } else {
                if (pd != null)
                    pd.cancel();

                AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
                alertDialog.setTitle(getString(R.string.signup_fail_heading));
                alertDialog.setMessage(getString(R.string.signup_fail_paragraph));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.btn_ok),
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            }
        };

        pd.show();

        progressDialogTimeout();

        WebService.webServiceAsyncTask(CreateNewUser_Android_v2, ope, email, password, first_name, mi, last_name, sex, dOB, addr1, addr2, city
                , state, zip, phone_number, serviceCode, mobile_mode);
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


    private void checkServiceCode(final String serviceCode) {
        WebService.getInstance().CheckServiceCode(serviceCode);

        OnPostExecute ope = result -> {

            int resultInt = Integer.valueOf(result.toString());

            if (resultInt == 1) {
                binding.layoutLogo.setVisibility(View.GONE);
                binding.txtSignin.setVisibility(View.GONE);
                trasitionToPage(1);
            } else {
                serviceCodeErrorAlert();
            }
        };
        WebService.webServiceAsyncTask(check_patient_service_code, ope, serviceCode);
    }


    private void serviceCodeEmptyErrorAlert() {
        MyAlertDialog dialog = new MyAlertDialog(this);
        dialog.show();
        dialog.setDialogContent(getString(R.string.regist_rm_empty_service_code));
        dialog.setLeftClickListener(getString(R.string.btn_no), new MyAlertDialog.LeftClickListener() {
            @Override
            public void onLeftClick(View view) {
                signUpVM.getClinicCode().setValue("cdoc");
                checkServiceCode(signUpVM.getClinicCode().getValue().toLowerCase().trim());
            }
        });
        dialog.setRightClickListener(getString(R.string.btn_yes), new MyAlertDialog.RightClickListener() {
            @Override
            public void onRightClick(View view) {
                serviceCodeErrorAlert();

            }
        });


    }

    private void serviceCodeErrorAlert() {
        MyAlertDialog dialog = new MyAlertDialog(this);
        dialog.show();
        dialog.setDialogContent(getString(R.string.signup_fail_service_code));

        dialog.setRightClickListener(getString(R.string.btn_ok), new MyAlertDialog.RightClickListener() {
            @Override
            public void onRightClick(View view) {
                dialog.dismiss();

            }
        });

    }

    private void checkEmailDuplication(final String email) {
        OnPostExecute ope = result -> {
            int resultInt = Integer.valueOf(result.toString());

            if (resultInt == 1) {
                trasitionToPage(2);
            } else {
                MyAlertDialog dialog = new MyAlertDialog(this);
                dialog.show();
                dialog.setDialogContent(getString(R.string.signup_fail_paragraph));

                dialog.setRightClickListener(getString(R.string.btn_ok), new MyAlertDialog.RightClickListener() {
                    @Override
                    public void onRightClick(View view) {
                        dialog.dismiss();

                    }
                });

            }
        };

        signUpVM.checkEmailDuplication(Check_Duplicate_Email_Address, ope, email);

    }


    private void trasitionToPage(int page) {
        binding.imgClose.setVisibility(View.VISIBLE);
        addProgress();
        switch (page) {
            case 1:
                binding.titleLabel.setText(getString(R.string.regist_basic_label));
                openSignUp1Fragment();
                break;
            case 2:
                binding.titleLabel.setText(getString(R.string.account_information_heading));
                openSignUp2Fragment();
                break;
            case 3:
                binding.titleLabel.setText(getString(R.string.regist_contact_label));
                openSignUp3Fragment();
                break;
        }
    }

    private void consentFormDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.consent_form_title))
                .setPositiveButton(getString(R.string.btn_accept), (dialog, which) -> registerUser())
                .setNegativeButton(getString(R.string.btn_decline), (dialog, which) -> dialog.cancel());

        String mobile_mode = signUpVM.getModeSelected().getValue();
        String device_number = "";//signup0Fragment.getDevice_number();
        String service_code = signUpVM.getClinicCode().getValue().toLowerCase().trim();
        String patient_name = signUpVM.getFirstName() + " " + signUpVM.getLastName();
        String message = getString(R.string.terms_message);

        final String teleHealth = LocalizationUtil.getLocalizedResources(this, Locale.US).getString(R.string.TeleHealth_Mode);
        final String remoteMonitor = LocalizationUtil.getLocalizedResources(this, Locale.US).getString(R.string.Remote_Monitoring_Mode);

        if (mobile_mode.equalsIgnoreCase(teleHealth)) { // TODO signature function needed
            //adb.setMessage(message).show();
            new ConsentForm(this, new GeneralConsentForm("Telehealth Consent Form"), new ConsentForm.ConsentFormCallBack() {
                @Override
                public void successWithPDF(String b64_pdf) {
                    consentFormInB64.add(b64_pdf);
                    registerUser();
                }

            }).ShowConsentForm();
//            new ConsentForm(this, new GeneralConsentForm("TeleHealth Title on PDF"), pdf -> {
//                consentFormInB64.add(pdf);
//                //registerUser();
//            }).ShowConsentForm();

        } else if (mobile_mode.equalsIgnoreCase(remoteMonitor)) {
//            OnPostExecute ope = result -> {
//                String consent_form = result.toString();
//
//                if (!hasThirdPartyConsentForm(consent_form)) {
//                    new ConsentForm(this,
//                            new RPMConsentForm("Remote Monitoring Consent Form", device_number, service_code), pdf -> {
//                        consentFormInB64.add(pdf);
//                        registerUser();
//                    }).ShowConsentForm();
//                } else { //Maysam OrgCode
//
//                    RPMConsentForm normal = new RPMConsentForm("Remote Monitoring Consent Form", device_number, service_code);
//                    //RPMConsentForm_Maysam maysam = new RPMConsentForm_Maysam("Remote Monitoring Consent Form", patient_name);
//                    RPMConsentForm_ThirdParty additonConsentForm = new RPMConsentForm_ThirdParty("Remote Monitoring Consent Form", patient_name);
//                    new ConsentForm(this, normal
//                            , pdf -> {
//                        consentFormInB64.clear();
//                        consentFormInB64.add(pdf);
//                        new ConsentForm(this, additonConsentForm, consent_form, maysam_pdf -> {
//                            consentFormInB64.add(maysam_pdf);
//                            registerUser();
//                        }).ShowConsentForm();
//                    }).ShowConsentForm();
//                }
//            };
//            //normal rpm consent form
//            WebService.webServiceAsyncTask(get_consent_form_by_service_code, ope, service_code);
        }
    }

    private boolean hasThirdPartyConsentForm(String consent_form) {
        return !consent_form.equalsIgnoreCase("anyType{}");
    }


    private void RecordConsentAccepted(final String email) {

        WebService.webServiceAsyncTask(RecordConsentAccepted_Andriod, result -> {
            int resultInt = Integer.valueOf(result.toString());

            if (resultInt == 1) {
                String message = "Hello " + signUpVM.getFirstName() + ", thank you for registering with CDoc."
                        + " If you have any questions, please feel free to contact us at 732-800-0020.";
                NotifyPatient(email, message);
            }
        }, email);
    }

    private void NotifyPatient(final String email, final String message) {

        WebService.webServiceAsyncTask(Notify_Patient, result -> {
            int resultInt = Integer.valueOf(result.toString());

            if (resultInt == 1) {

                //send consent form to ehr
                String service_code = signUpVM.getClinicCode().getValue().toLowerCase().trim();

                Upload_Patient_Consent.setDisableNullRestriction(true);
                for (String form : consentFormInB64)
                    WebService.webServiceAsyncTask(Upload_Patient_Consent, email, "PDF", service_code, form);

                if (pd != null)
                    pd.cancel();
                //RegistNoteDialog dialog = null;
                dialog = new RegistNoteDialog(SignUpActivity.this, v -> {
                    dialog.dismiss();
                    Intent intent = new Intent();
                    intent.putExtra("email", email);
                    intent.putExtra("newOrgCode", newOrgCode);
                    setResult(RESULT_OK, intent);
                    //finish();
                    String userId = signUpVM.getEmail().getValue();
                    String password = signUpVM.getPassword().getValue();
                    ProceedLogin.verifyCredential(this, userId, password, () -> {

                        ProceedLogin.loginAsRep(SignUpActivity.this, userId, userId, password, () -> {

                            Intent fragMainIntent = new Intent(SignUpActivity.this, FragmentMainActivity.class);
                            startActivity(fragMainIntent);
                            finish();

                        }, () -> {

                            Toast.makeText(SignUpActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                            Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(loginIntent);
                            finish();

                        });
                    }, () -> {

                        Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                        Intent loginIntent = new Intent(this, LoginActivity.class);
                        startActivity(loginIntent);
                        finish();

                    });
                }, false);
                dialog.show();
                dialog.setText(getString(R.string.regist_successful),
                        getString(R.string.regist_successful_message), getString(R.string.btn_ok));
            }
        }, email, message);
    }

    public void openSignUp1Fragment() {
        replaceFragmentWithAnimation(signup1Fragment, "1");
    }

    public void openSignUp2Fragment() {
        replaceFragmentWithAnimation(signup2Fragment, "2");
    }

    public void openSignUp3Fragment() {
        replaceFragmentWithAnimation(signup3Fragment, "3");
    }

    public void replaceFragmentWithAnimation(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            transaction.setCustomAnimations(R.animator.slide_from_right, R.animator.slide_to_left, R.animator.slide_from_left, R.animator.slide_to_right);
        }
        transaction.replace(R.id.content, fragment, "1");
        transaction.addToBackStack(tag);
        transaction.commit();

    }

    @Override
    public void onBackPressed() {
        switch (getSupportFragmentManager().getBackStackEntryCount()) {
            case 1:
                finish();
                break;
            default:
                changeSignUp0();
                super.onBackPressed();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isHideInput(view, ev)) {
                HideSoftInput(view.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    public void backButton() {
        onBackPressed();
    }

    void changeSignUp0() {
        backProgress();
        if (getSupportFragmentManager().getBackStackEntryCount() == 2) {
            binding.layoutLogo.setVisibility(View.VISIBLE);
            binding.txtSignin.setVisibility(View.VISIBLE);
            binding.imgClose.setVisibility(View.GONE);
            binding.titleLabel.setText(getString(R.string.select_mode));
            binding.progressIndicator.setProgress(0);

        }

    }

}
