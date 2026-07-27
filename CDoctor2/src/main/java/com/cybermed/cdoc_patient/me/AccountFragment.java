package com.cybermed.cdoc_patient.me;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.UserInfo;
import com.cdfortis.datainterface.soap.WebService;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.FragmentAccountBinding;
import com.cybermed.cdoc_patient.login.signup.ValidationUtils;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;
import com.cybermed.cdoc_patient.util.MyToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import static android.app.DatePickerDialog.*;
import static com.cdfortis.datainterface.soap.WebServiceID.ResetUserPasswordInPortal;
import static com.cdfortis.datainterface.soap.WebServiceID.UpdateUserProfile;
import static com.cdfortis.datainterface.soap.WebServiceID.check_patient_service_code;
import static com.cdfortis.datainterface.soap.WebServiceID.update_patient_default_state;
import static com.cdfortis.datainterface.soap.WebServiceID.update_patient_service_code;

/**
 * Created by Ldj on 2016/4/28.
 */
public class AccountFragment extends BaseFragment implements DialogInterface.OnCancelListener, TextView.OnEditorActionListener, View.OnClickListener, MeFragment.OnInnerFragmentStatusChange, OnDateSetListener {
    public static final String DATEPICKER_TAG = "datepicker";


    private Button savePassword;
    private Button cancelButton;
    private ImageView closeButton;
    private ImageView infoButton;

    private EditText mEditPwd;
    private EditText mEditNewPwd;
    private EditText mEditComPwd;

    private EditText mEditSerCode;
    private EditText mEditNewSerCode;
    private EditText mEditComSerCode;


    private DatePickerDialog datePickerDialog;

    private String mBirthDay;
    private String mGender = "M";

    private AsyncTask mUserRegistAsyncTask;
    private ArrayList<String> stateList;

    private UserInfo userInfo;


    private FragmentMainActivity fragMain;


    private String user_id, serviceCode;

    FragmentAccountBinding binding;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.unbind();
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            WindowCompat.setDecorFitsSystemWindows(getActivity().getWindow(), true);

            ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
                return WindowInsetsCompat.CONSUMED;
            });
        }
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        initContent();

    }


    @Override
    public void onMyResume() {
        initContent();
    }

    @Override
    public void onMyStop() {

    }

    public void initContent() {


        if (getActivity() instanceof FragmentMainActivity) {
            fragMain = (FragmentMainActivity) getActivity();
            user_id = fragMain.getLoginInfo2().getAccount();
        }

        initToolBar();
        initView();
        initEvent();

        String[] stateArray;

        stateArray = getResources().getStringArray(R.array.state);

        stateList = new ArrayList<>();
        Collections.addAll(stateList, stateArray);
        binding.editState.setOnClickListener(this);
        binding.editState.setInputType(InputType.TYPE_NULL);

        datePickerDialog = new DatePickerDialog(getContext(), this, 1903, 0, 1);
        clickListener();
    }

    private void initView() {
        // only once init view
        serviceCode = fragMain.getLoginInfo2().getUserInfo().getService_code();
        binding.txtBirth.setInputType(InputType.TYPE_NULL);


        if (serviceCode.isEmpty()) {
            binding.changeServiceCode.setText(getString(R.string.service_code_all));
        } else {
            binding.changeServiceCode.setText(serviceCode.toUpperCase());
        }
        binding.tvMale.setSelected(true);
        binding.tvMale.setTextColor(getResources().getColor(R.color.white_0_2));
    }

    private void clickListener() {
        binding.accountContactInfoBtn.setOnClickListener(v -> showInfoDialog());
        binding.txtBirth.setOnClickListener(v -> {
//            datePickerDialog.show();
        });
    }


    private void showInfoDialog() {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.account_info_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);

        Button okayBtn = promptsView.findViewById(R.id.account_info_okay_btn);

        AlertDialog alertDialog = alertDialogBuilder.create();
        okayBtn.setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.show();
    }

    private void initToolBar() {
        binding.toolbar.txtTittle.setText( getString(R.string.account_information_heading));

        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backUserActivity();
            }
        });
    }


    private void initEvent() {
        binding.infoPhone.addTextChangedListener(new myPhoneTextWatcher());
        binding.txtBirth.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.txtBirth.setError(null);
                binding.txtBirth.clearFocus();
            }
        });

        binding.editFirstName.setOnEditorActionListener(this);
        fragMain.getLoginInfo2().load();
        userInfo = fragMain.getLoginInfo2().getUserInfo();
        setUserInfo(userInfo);
        binding.tvMale.setOnClickListener(this);
        binding.tvFemale.setOnClickListener(this);
        binding.tvChangePwd.setOnClickListener(this);
        binding.editState.setOnClickListener(this);
        binding.changeServiceCode.setOnClickListener(this);
    }

    private void setUserInfo(UserInfo userInfo) {
        binding.editMail.setText(userInfo.getEmail());
        binding.editFirstName.setText(userInfo.getFirstName());
        binding.editLastName.setText(userInfo.getLastname());
        // infoEmail.setText(userInfo.getEmail());
        binding.txtBirth.setText(userInfo.getDOB());
        StringTokenizer tokens = new StringTokenizer(userInfo.getDOB(), "/");
        String month = tokens.nextToken();
        String day = tokens.nextToken();
        String year = tokens.nextToken();
        mBirthDay = year + "-" + month + "-" + day;
        String sex = userInfo.getSex();
        if ("M".equals(sex)) {
            setSelected(binding.tvMale);
            setUnSelected(binding.tvFemale);
        } else {
            setSelected(binding.tvFemale);
            setUnSelected(binding.tvMale);
        }
        // infoEmail.setText(userInfo.getEmail());
        binding.infoPhone.setText(userInfo.getPhoneNum());
        binding.infoAddr1.setText(userInfo.getAddr1());
        binding.infoAddr2.setText(userInfo.getAddr2());
        binding.editCity.setText(userInfo.getCity());
        binding.editState.setText(userInfo.getState());
        binding.editZipCode.setText(userInfo.getZip());
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.editFirstName) {
            binding.editFirstName.clearFocus();
            binding.editLastName.requestFocus();
            return true;
        }
        return false;
    }

    private void backUserActivity() {


        if (((MeFragment) getParentFragment() != null)) {
            ((MeFragment) getParentFragment()).openUserActivityFragment();
        }

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String tempMonth, tempDay;
        if ((month + 1) < 10) {
            tempMonth = "0" + (month + 1);
        } else {
            tempMonth = (month + 1) + "";
        }

        if (day < 10) {
            tempDay = "0" + day;
        } else {
            tempDay = day + "";
        }

        mBirthDay = year + "-" + tempMonth + "-" + tempDay;
        binding.txtBirth.setText(tempMonth + "/" + tempDay + "/" + year);
    }


    public static boolean compareDates(String d1, String d2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);

            if (date1.after(date2)) {
                return true;
            }
            if (date1.before(date2)) {
                return false;
            }

            if (date1.equals(date2)) {
                return true;
            }

        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return false;
    }


    public void onSaveClick() {
        String password, comPassword, firtName, lastName, phone;
        String infoaddr1, infoaddr2, infocity, infostate, infozipcode;


        firtName = binding.editFirstName.getText().toString();
        lastName = binding.editLastName.getText().toString();
        phone = binding.infoPhone.getText().toString().trim();
        infoaddr1 = binding.infoAddr1.getText().toString().trim();
        infoaddr2 = binding.infoAddr2.getText().toString().trim();
        infocity = binding.editCity.getText().toString().trim();
        infostate = binding.editState.getText().toString().trim();
        infozipcode = binding.editZipCode.getText().toString().trim();

        if (TextUtils.isEmpty(firtName)) {
            binding.editFirstName.setError(getString(R.string.regist_error_firstname));
            binding.editFirstName.setText("");
            binding.editFirstName.requestFocus();
            return;
        }

        if (firtName.length() < 2 && !getResources().getConfiguration().locale.equals(Locale.CHINA)) {
            binding.editFirstName.setError(getString(R.string.regist_error_firstname));
            binding.editFirstName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            binding.editLastName.setError(getString(R.string.regist_error_lastname));
            binding.editLastName.setText("");
            binding.editLastName.requestFocus();
            return;
        }

        if (lastName.length() < 2 && !getResources().getConfiguration().locale.equals(Locale.CHINA)) {
            binding.editLastName.setError(getString(R.string.regist_error_lastname));
            binding.editLastName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(mBirthDay)) {
            binding.txtBirth.setError("Empty Date of Birth");
            binding.txtBirth.requestFocus();
            return;
        }

        binding.txtBirth.setError(null);
        binding.txtBirth.clearFocus();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = df.format(c.getTime());

        Log.d("compareDate", currentDate);
        Log.d("compareDate", mBirthDay);
        if (!compareDates(currentDate, mBirthDay)) {
            binding.txtBirth.setError("Invalid Date of Birth");
            binding.txtBirth.requestFocus();

            return;
        }


        if (!ValidationUtils.isPhoneNum(phone)) {
            binding.infoPhone.setError(getString(R.string.regist_error_phone));
            binding.infoPhone.requestFocus();
            return;
        }

        if (phone.length() != 12) {
            binding.infoPhone.setError(getString(R.string.regist_error_phone));
            binding.infoPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(infoaddr1)) {
            binding.infoAddr1.setError(getString(R.string.regist_error_addr1));
            binding.infoAddr1.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(infocity)) {
            binding.editCity.setError(getString(R.string.regist_error_city));
            binding.editCity.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(infostate)) {
            binding.editState.setError(getString(R.string.regist_error_state));
            binding.editState.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(infozipcode)) {
            binding.editZipCode.setError(getString(R.string.regist_error_zip));
            binding.editZipCode.requestFocus();
            return;
        }
        if (infozipcode.length() != 5) {
            binding.editZipCode.setError(getString(R.string.regist_error_zip_verify));
            binding.editZipCode.requestFocus();
            return;
        }

        //Save Default State
        // /*Ready to change*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        //Log.d("Account Default State", infostate);
        //editor.putString("state_key", infostate);
        editor.putString("filtered_state", "");
        editor.apply();
        /*Ready to change*/

        updateUserInfo(userInfo.getEmail(), ((FragmentMainActivity) getActivity()).getLoginInfo2().getPwd(), firtName, lastName, mGender, mBirthDay, infoaddr1, infoaddr2, infocity,
                infostate, infozipcode, phone);


    }


    private void changePassDialog() {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.dialog_change_password, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);

        mEditPwd = promptsView.findViewById(R.id.prevPassword);
        mEditPwd.setTypeface(Typeface.DEFAULT);
        mEditPwd.setTransformationMethod(new PasswordTransformationMethod());
        mEditNewPwd = promptsView.findViewById(R.id.newPassword);
        mEditNewPwd.setTypeface(Typeface.DEFAULT);
        mEditNewPwd.setTransformationMethod(new PasswordTransformationMethod());
        mEditComPwd = promptsView.findViewById(R.id.confirmPassword);
        mEditComPwd.setTypeface(Typeface.DEFAULT);
        mEditComPwd.setTransformationMethod(new PasswordTransformationMethod());
        cancelButton = promptsView.findViewById(R.id.cancel_btn);
        closeButton = promptsView.findViewById(R.id.closeBtn);
        savePassword = promptsView.findViewById(R.id.save_password_btn);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


        cancelButton.setOnClickListener(v -> alertDialog.dismiss());
        closeButton.setOnClickListener(v -> alertDialog.dismiss());

        savePassword.setOnClickListener(v -> {
            if (verifyPassword()) {
                alertDialog.dismiss();
            }
        });
    }

    private boolean verifyPassword() {

        String password = mEditPwd.getText().toString();
        String newPassword = mEditNewPwd.getText().toString();
        String comPassword = mEditComPwd.getText().toString();

        if (TextUtils.isEmpty(password)) {
            mEditPwd.setError(getString(R.string.regist_error_password));
            mEditPwd.requestFocus();
            return false;
        }

        if (!password.equals(fragMain.getLoginInfo2().getPwd())) {
            mEditPwd.setError(getString(R.string.regist_error_password));
            mEditPwd.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(newPassword)) {
            mEditNewPwd.setError(getString(R.string.account_information_please_enter_password));
            mEditNewPwd.requestFocus();
            return false;
        }

        if (newPassword.length() < 4) {
            mEditNewPwd.setError(getString(R.string.regist_error_password_short));
            mEditNewPwd.setText("");
            mEditNewPwd.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(comPassword)) {
            mEditComPwd.setError(getString(R.string.account_information_please_enter_password));
            mEditComPwd.requestFocus();
            return false;
        }

        if (!newPassword.equals(comPassword)) {
            mEditComPwd.setError(getString(R.string.account_information_password_notmatch));
            mEditComPwd.setText("");
            mEditComPwd.requestFocus();
            return false;
        }

        resetPassWord(fragMain.getLoginInfo2().getAccount(), newPassword);
        return true;
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        if (mUserRegistAsyncTask != null) {
            mUserRegistAsyncTask.cancel(true);
            mUserRegistAsyncTask = null;
        }
    }

    private void updateUserInfo(final String email, final String password,
                                final String firstName, final String lastName,
                                final String sex, final String dOB, final String addr1, final String addr2,
                                final String city, final String state, final String zip, final String phone) {

        WebService.webServiceAsyncTask(update_patient_default_state, email, state);

        OnPostExecute ope = result -> {
            int integer = Integer.parseInt(result.toString());

            if (integer == 1) {
                fragMain.toastShortInfo(getString(R.string.update_user_info_succeed));
                UserInfo userInfo = new UserInfo();
                userInfo.setEmail(email);
                userInfo.setFirstName(firstName);
                userInfo.setLastname(lastName);
                userInfo.setSex(sex);
                StringTokenizer tokens = new StringTokenizer(dOB, "-");
                String year = tokens.nextToken();
                String month = tokens.nextToken();
                String day = tokens.nextToken();
                userInfo.setDOB(month + "/" + day + "/" + year);
                userInfo.setAddr1(addr1);
                userInfo.setAddr2(addr2);
                userInfo.setCity(city);
                userInfo.setState(state); // account state
                userInfo.setZip(zip);
                userInfo.setPhoneNum(phone);
                userInfo.setDefault_state(state); // default state
                userInfo.setService_code(serviceCode);

                String userId = fragMain.getLoginInfo2().getAccount();
                fragMain.getCDocApplication().processUserLogin2(userId, password, userInfo);
                fragMain.setResult(fragMain.RESULT_OK);
                fragMain.reloadDoctorList();

                backUserActivity();
            } else {
                fragMain.toastShortInfo(getString(R.string.update_user_info_failed));
            }

        };
        WebService.webServiceAsyncTask(UpdateUserProfile, ope, email, email, password, firstName, lastName,
                sex, dOB, addr1, addr2, city, state, zip, phone);
    }


    private void resetPassWord(final String email, final String password) {

        OnPostExecute ope = result -> {
            int integer = Integer.parseInt(result.toString());
            if (integer == 1) {
                fragMain.getLoginInfo2().setPwd(password);
                fragMain.toastShortInfo(getString(R.string.password_change_succeed));

            } else {
                fragMain.toastShortInfo(getString(R.string.password_change_failed));
            }
        };

        WebService.webServiceAsyncTask(ResetUserPasswordInPortal, ope, email, password);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUserRegistAsyncTask != null) {
            mUserRegistAsyncTask.cancel(true);
            mUserRegistAsyncTask = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.tvMale:
//                setSelected(tvMale);
//                mGender = "M";
//                setSelected(tvMale);
//                setUnSelected(tvFemale);
//                break;
//            case R.id.tvFemale:
//                setSelected(tvFemale);
//                setUnSelected(tvMale);
//                mGender = "F";
//                break;
            case R.id.tv_change_pwd:
                changePassDialog();
                break;
//            case R.id.editState:
//                //showStateOptionDialog();
//                break;
            case R.id.change_service_code:
                changeServiceCodeDialog();
                break;
        }
    }

//    private void showStateOptionDialog() {
//
//        PickerViewUtil.alertBottomWheelOption(getActivity(), stateList, new PickerViewUtil.OnWheelViewClick() {
//            @Override
//            public void onClick(View view, int postion) {
//                binding.editState.setText(stateList.get(postion));
//            }
//        });
//    }

    private void setSelected(TextView tv) {
        tv.setSelected(true);
        tv.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_star_bg));
        tv.setTextColor(getResources().getColor(R.color.dark_slate_blue));
    }

    private void setUnSelected(TextView tv) {
        tv.setSelected(false);
        tv.setBackground(null);
        tv.setTextColor(getResources().getColor(R.color.color_8f8f8f));
    }


    private class myPhoneTextWatcher implements TextWatcher {
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

    }

    private void changeServiceCodeDialog() {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.dialog_change_service_code, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);

        //set current service getCode

        String serviceCode = fragMain.getLoginInfo2().getUserInfo().getService_code();
        TextView txtService = promptsView.findViewById(R.id.serviceCode);

        if (serviceCode.isEmpty()) {
            txtService.setText(getString(R.string.service_code_all));
        } else {
            txtService.setText(serviceCode.toUpperCase());
        }

        mEditNewSerCode = promptsView.findViewById(R.id.newServiceCode);
        mEditNewSerCode.setTypeface(Typeface.DEFAULT);

        alertDialogBuilder
                .setCancelable(false);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        alertDialog.findViewById(R.id.btn_Okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyServiceCode()) {
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        alertDialog.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        alertDialog.findViewById(R.id.tv_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetServiceCode("");
                alertDialog.cancel();
            }
        });
    }


    private boolean verifyServiceCode() {
        String newServiceCode = mEditNewSerCode.getText().toString().toLowerCase().trim();

        if (TextUtils.isEmpty(newServiceCode)) {
            mEditNewSerCode.setError(getString(R.string.empty_service_code_warning));
            mEditNewSerCode.requestFocus();
            return false;
        }

        resetServiceCode(newServiceCode);
        return true;
    }

    private void checkServiceCode() {
        OnPostExecute opeCheck = resultCheck -> {
            int serviceCodeExists = Integer.valueOf(resultCheck.toString());

            if (serviceCodeExists == -1) {
                toastShortInfo(getString(R.string.signup_fail_service_code));
            } else {
                updateServiceCode();
            }
        };
        WebService.webServiceAsyncTask(check_patient_service_code, opeCheck, serviceCode);
    }

    private void updateServiceCode() {
        OnPostExecute opeUpdate = resultUpdate -> {
            int integer = Integer.valueOf(resultUpdate.toString());

            if (integer != -1) {
                /*Save Locally*/
                fragMain.getLoginInfo2().getUserInfo().setService_code(serviceCode);
                fragMain.getLoginInfo2().save();

                if (serviceCode.isEmpty()) {
                    binding.changeServiceCode.setText(getString(R.string.service_code_all));
                } else {
                    binding.changeServiceCode.setText(serviceCode.toUpperCase());
                }
                toastShortInfo(getString(R.string.service_code_update_succeed));
                fragMain.reloadDoctorList();
            } else {
                toastShortInfo(getString(R.string.service_code_udpate_failed));
            }
        };

        WebService.webServiceAsyncTask(update_patient_service_code, opeUpdate, user_id, serviceCode);
    }

    private void resetServiceCode(final String serviceCode) {
        this.serviceCode = serviceCode;
        if (serviceCode.isEmpty()) {
            updateServiceCode();
        } else {
            checkServiceCode();
        }
    }

    public void toastShortInfo(String message) {
        MyToast.myShortToast(getActivity(), message);
    }
}