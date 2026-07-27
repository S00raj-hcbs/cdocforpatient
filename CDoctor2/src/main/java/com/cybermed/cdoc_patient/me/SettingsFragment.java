package com.cybermed.cdoc_patient.me;

import static com.cybermed.cdoc_patient.common.videoui.ConstantApp.KEY_ENABLE;
import static com.cybermed.cdoc_patient.login.LoginActivity.TABLETMOODE;
import static com.cybermed.cdoc_patient.util.AppConstant.IS_FROM_HEALTH_RECORD;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.annotation.Factory;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.databinding.FragmentSettingBinding;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;
import com.cybermed.cdoc_patient.main.HomeFragment;
import com.cybermed.cdoc_patient.me.manager.ProfileApiManager;
import com.cybermed.cdoc_patient.view.MedicalDisclaimerDialog;
import com.cybermed.cdoc_patient.view.MyAlertDialog;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.manager.ResponseGetEnableLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;


public class SettingsFragment extends BaseFragment implements View.OnClickListener, MeFragment.OnInnerFragmentStatusChange, HomeFragment.OnInnerFragmentStatusChange {



    private MeFragment meFragment;
    private View view;
    private FragmentMainActivity fragMain;

    //New setting Elements
    private CardView linear_enable_log;
    private RelativeLayout linear_language_box;
    private TextView mVersionNumber;

    private CountDownTimer tabletCountDownTimer;
    private int tabletCountDownInt = 0;
    private boolean mTabletMode;
    String[] langArray;
    SwitchCompat toggleEnableLog;
    private ImageView arrow_up, arrow_down;
    private TextView showState;
    String mDefaultLang;
    Context context;
    TextView txtViewLang;
    FragmentSettingBinding binding;


    @Factory
    public static SettingsFragment newInstance( boolean isFromHealthRecord) {
        SettingsFragment fragment = new SettingsFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putBoolean(IS_FROM_HEALTH_RECORD, isFromHealthRecord);
        fragment.setArguments(args);

        return fragment;
    }
    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_setting,container,false);
        view=binding.getRoot();
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        fragMain = (FragmentMainActivity) getActivity();
        context = getActivity();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mTabletMode = preferences.getBoolean(TABLETMOODE, false);

        binding.toolbar.txtTittle.setText(getString(R.string.settings_heading));
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getArguments() != null && getArguments().getBoolean(IS_FROM_HEALTH_RECORD)) {
                    if (getParentFragment() != null)
                        ((HomeFragment) getParentFragment()).openMainActivity();
                } else {
                    if (((MeFragment) getParentFragment() != null)) {
                        ((MeFragment) getParentFragment()).openMeActivity(false);
                    }
                }
            }
        });

        initView();
        initVersionName();
        setRecyclerView();
    }

    private void initView() {
        CardView aboutLayout = view.findViewById(R.id.about_us_layout);
        CardView contactLayout = view.findViewById(R.id.contact_us_layout);
        RelativeLayout changeStateLayout = view.findViewById(R.id.change_state_layout);
        showState = view.findViewById(R.id.show_state);
        txtViewLang = view.findViewById(R.id.show_currentlang);
        mVersionNumber = view.findViewById(R.id.system_version);
        RelativeLayout mSystemLayout = view.findViewById(R.id.layout_system);
        toggleEnableLog = view.findViewById(R.id.toggle_enable_log);
        linear_enable_log = view.findViewById(R.id.linear_enable_log);
        arrow_up = view.findViewById(R.id.up_arrow);
        arrow_down = view.findViewById(R.id.down_arrow);
        txtViewLang.setOnClickListener(this);
        linear_language_box = view.findViewById(R.id.ll_language_opt);


        String state = fragMain.getLoginInfo2().getUserInfo().getDefault_state();
        if (PreferenceManager.getDefaultSharedPreferences(
                getActivity()).getBoolean(KEY_ENABLE, false)) {
            toggleEnableLog.setChecked(true);
        }
        if (TextUtils.isEmpty(state)) {
            showState.setText("All");
        } else
            showState.setText(state);



        langArray = getResources().getStringArray(R.array.lang_select);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mDefaultLang = preferences.getString("LANG", "");
        if (mDefaultLang.isEmpty()) {
            mDefaultLang = Locale.getDefault().getLanguage();
        }

        aboutLayout.setOnClickListener(this);
        contactLayout.setOnClickListener(this);
        changeStateLayout.setOnClickListener(this);
        mSystemLayout.setOnClickListener(this);
        toggleEnableLog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!toggleEnableLog.isChecked()) {
                    PreferenceManager.getDefaultSharedPreferences(
                            getActivity()).edit().putBoolean(KEY_ENABLE, false).commit();
                    Toast.makeText(getActivity(), "Logs Disabled", Toast.LENGTH_SHORT).show();
                } else
                    showEnableLogDialog();
            }
        });
        arrow_up.setOnClickListener(this);
        arrow_down.setOnClickListener(this);
        showState.setOnClickListener(this);
        binding.disclaimerUsLayout.setOnClickListener(this);
        getEnableLog();

    }


    private void initVersionName() {
        try {
            String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            mVersionNumber.setText(versionName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_us_layout:
                Intent aboutIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.cybermedcorp.com/Abouts/about_us.html"));
                startActivity(aboutIntent);
                break;
            case R.id.contact_us_layout:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.cybermedcorp.com/index.html#contact"));
                startActivity(browserIntent);
                break;
            case R.id.change_state_layout:
                showChangeStateDialog();
                break;
            case R.id.disclaimer_us_layout:
                String title="Medical Disclaimer";
                String description= "This application is intended for health monitoring and informational purposes only.\n" +
                        "\n" +
                        "The app does not provide medical diagnosis, treatment, emergency medical services, or medical recommendations.\n" +
                        "\n" +
                        "Health information displayed in the app is collected from compatible connected devices and should not be relied upon as a substitute for professional medical advice, diagnosis, or treatment.\n" +
                        "\n" +
                        "Always consult a licensed healthcare professional regarding any medical condition or healthcare decision.";
                /*  String description= "The app does not provide medical diagnosis, treatment, or emergency medical services.\n" +
                        "\n" +
                        "Users are reminded to consult a licensed healthcare professional for medical advice, diagnosis, or treatment."*/
                        /*\n" +
                        "\n" +
                        "Health and vital information displayed in CDoc may be provided by external devices that users voluntarily connect. CDoc does not automatically collect data from such devices."*/;


                MedicalDisclaimerDialog.show(context,title,description,"I Understand",false,()->{
                    // AFTER medical → show hardware
                    MedicalDisclaimerDialog.show(
                            context,
                            "Device Information",
                            "CDOC requires compatible external medical, wellness, or wearable devices for certain features and health measurements, including blood pressure monitors, glucometers, pulse oximeters, weight scales, and supported wearable devices.\n" +
                                    "\n" +
                                    "Health data displayed in the application is obtained from connected compatible devices. These device-dependent features do not function independently without the appropriate external hardware.\n" +
                                    "\n" +
                                    "Please ensure that a supported device is properly connected before using these features."
                            /*"Certain CDOC features require compatible external medical or wellness devices, such as Bluetooth blood pressure monitors, glucometers, pulse oximeters, weight scales, or supported wearable devices.\n" +
                                    "\n" +
                                    "These device-connected features do not function independently without the appropriate external hardware."*/
                            /*\n" +
                                    "\n" +
                                    "CDoc does not automatically collect health data from external devices. Health data is imported only after you choose to connect a compatible device and grant permission."*/,
                            "Continue",
                            false,
                            () -> {

                            },
                            ()->{}
                    );
                },()->{});
                break;
            case R.id.layout_system:
            case 0:
                tabletCountDownInt++;
                if (tabletCountDownInt == 10) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("tabletmode", true);
                    editor.commit();
                    switchTabletMode(mTabletMode);
                    recreate();
                } else {
                    if (tabletCountDownInt > 5) {
                        if (mTabletMode) {
                            Toast.makeText(getActivity(), "Press " + String.valueOf(10 - tabletCountDownInt) + " more times to enter Normal Mode", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Press " + String.valueOf(10 - tabletCountDownInt) + " more times to enter Tablet Mode", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (tabletCountDownTimer != null) {
                        tabletCountDownTimer.cancel();
                    }
                }

                tabletCountDownTimer = new CountDownTimer(5000, 1000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        tabletCountDownInt = 0;
                    }
                }.start();
                break;

            case R.id.up_arrow:
                arrow_down.setVisibility(View.VISIBLE);
                arrow_up.setVisibility(View.GONE);
                linear_language_box.setVisibility(View.GONE);
                txtViewLang.setVisibility(View.VISIBLE);
                break;
            case R.id.show_currentlang:
            case R.id.down_arrow:
                arrow_down.setVisibility(View.GONE);
                arrow_up.setVisibility(View.VISIBLE);
                linear_language_box.setVisibility(View.VISIBLE);
                txtViewLang.setVisibility(View.GONE);
                break;
            case R.id.show_state:
                showChangeStateDialog();
                break;
        }
    }

    public void setRecyclerView() {
        RecyclerView rv_language = view.findViewById(R.id.rv_language);
        ArrayList<String> langList = new ArrayList<>();
        Collections.addAll(langList, langArray);
        String setDefaultLang;

        if (mDefaultLang.equals("en")) {
            setDefaultLang = langArray[0];

        } else if (mDefaultLang.equals("zh")){
            setDefaultLang = langArray[1];
        }else {
            setDefaultLang = langArray[2];
        }
        txtViewLang.setText(setDefaultLang);
        SettingsLanguageAdapter settingsLanguageAdapter = new SettingsLanguageAdapter(langList, setDefaultLang, getContext(),
                language -> {
                    String lang = "";
                    if (!language.isEmpty()) {
                        if (language.equals("English")) {
                            lang = "en";
                        } else if (language.equals("简体中文")){
                            lang = "zh";
                        }else {
                            lang = "es";
                        }
                    }
                    setLangPref(lang);
                    txtViewLang.setText(language);
                });
        rv_language.setAdapter(settingsLanguageAdapter);
        rv_language.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        rv_language.setHasFixedSize(true);
    }

    private void setLangPref(String lang) {
        if (!lang.equals(mDefaultLang)) {
            SharedPreferences.Editor editor1 = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
            editor1.putString("LANG", lang).apply();
            setLangRecreate(lang);
        }
    }

    private void switchTabletMode(boolean tabletMode) {

        PackageManager pm = getActivity().getApplicationContext().getPackageManager();
        ComponentName compName = new ComponentName(getActivity().getPackageName(), getActivity().getPackageName() + ".login.WelcomeActivity");
        ComponentName compName2 = new ComponentName(getActivity().getPackageName(), getActivity().getPackageName() + ".login.WelcomeActivityTablet");
        if (!tabletMode) {
            pm.setComponentEnabledSetting(compName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            pm.setComponentEnabledSetting(compName2, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        } else {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("tabletmode", false);
            editor.commit();
            pm.setComponentEnabledSetting(compName2, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            pm.setComponentEnabledSetting(compName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
    }

    public void showChangeStateDialog() {
        AlertDialog dialogBuilder = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_state, null);
        dialogBuilder.setView(dialogView);
        ImageView close = dialogView.findViewById(R.id.closeBtn);
        RecyclerView rv_state = dialogView.findViewById(R.id.listItems);
        ArrayList<String> stateList = new ArrayList<>();
        String[] stateArray = getResources().getStringArray(R.array.state);
        //stateList.add(getString(R.string.service_code_all));
        Collections.addAll(stateList, stateArray);

        SettingsStateAdapter adapter = new SettingsStateAdapter(stateList, getContext(), new SettingsStateAdapter.IStateCallBack() {
            @Override
            public void selectedState(String state) {
                String setState = state;
                showState.setText(setState);
                fragMain.getLoginInfo2().getUserInfo().setDefault_state(setState); //update default
                String userid = fragMain.getLoginInfo2().getAccount();
                //update
                fragMain.updateUserDefaultState(userid, setState); // update  online
                fragMain.reloadDoctorList();
                showFinishStateDialog();
                dialogBuilder.dismiss();
            }
        });
        rv_state.setAdapter(adapter);
        rv_state.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        rv_state.setHasFixedSize(true);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.show();

    }

    private void showFinishStateDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getString(R.string.state_changed_title));
        alertDialog.setMessage(getString(R.string.state_changed_msg));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.btn_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    public void setLangRecreate(String langval) {
        Configuration config = getResources().getConfiguration();
        Locale locale = new Locale(langval);
        Locale.setDefault(locale);
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        recreate();
    }

    private void recreate() {
        final FragmentMainActivity fragMain = (FragmentMainActivity) getActivity();
        fragMain.logOut();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    private void showEnableLogDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_enable_log);

        EditText text = (EditText) dialog.findViewById(R.id.edt_password);

        Button ok = (Button) dialog.findViewById(R.id.btn_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (text.getText().toString().equals("qwzx@as!#")) {
                    PreferenceManager.getDefaultSharedPreferences(
                            getActivity()).edit().putBoolean(KEY_ENABLE, true).commit();
                    Toast.makeText(getActivity(), "Logs enabled", Toast.LENGTH_SHORT).show();
                } else {
                    toggleEnableLog.setChecked(false);
                    MyAlertDialog dialog = new MyAlertDialog(getActivity());
                    dialog.show();
                    dialog.setDialogContent(getString(R.string.auth_fail));
                    dialog.setRightClickListener(getString(R.string.btn_confirm), new MyAlertDialog.RightClickListener() {
                        @Override
                        public void onRightClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }


            }
        });
        Button dialogButton = (Button) dialog.findViewById(R.id.btn_cancel);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceManager.getDefaultSharedPreferences(
                        getActivity()).getBoolean(KEY_ENABLE, false)) {
                    toggleEnableLog.setChecked(true);
                } else
                    toggleEnableLog.setChecked(false);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * call enable log api to check whether to show enable log button or not
     */
    void getEnableLog() {
        ProfileApiManager apiManager = new ProfileApiManager(new IResponseReceiver() {
            @Override
            public void onSuccess(Object data) {
                if (((ResponseGetEnableLog) data).isEnableLog()) {
                    linear_enable_log.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {

            }
        }, context);
        apiManager.getEnableLog(fragMain.getLoginInfo2().getUserInfo().getEmail());
    }

    @Override
    public void onMyResume() {
        getEnableLog();
    }

    @Override
    public void onMyStop() {

    }
}
