package com.cybermed.cdoc_patient.me;




import static com.cybermed.cdoc_patient.util.AppConstant.FROM_USER;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.cybermed.cdoc_patient.R;

import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.family.FamilyFragment;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;
import com.cybermed.cdoc_patient.me.allergies.AllergiesFragment;
import com.cybermed.cdoc_patient.me.calllog.CallLogFragment;
import com.cybermed.cdoc_patient.me.immunizations.ImmunizationFragment;
import com.cybermed.cdoc_patient.me.labereport.LabFragment;
import com.cybermed.cdoc_patient.me.medication.MedicationFragment;
import com.cybermed.cdoc_patient.me.referral.ReferalFragment;
import com.cybermed.cdoc_patient.me.securemessages.view.SecureMessageParent;
import com.cybermed.cdoc_patient.payment.PaymentCreditCardFrag;

public class UserFragment extends BaseFragment implements View.OnClickListener, MeFragment.OnInnerFragmentStatusChange {
    private FragmentMainActivity mFragMain;
    private MeFragment mMeFragment;
    private View view;
    private TextView mLogoutBtn, name, email;
    ImageView profileImage;


    private CardView mAccountBtn, mFamilyBtn, mCallHistBtn, mMedicationBtn,mAllergiesBtn, mLabBtn, mImmunizationBtn, mPaymentBtn, mVaccBtn, mMonBtn;
    private boolean mTabletMode;
    Context context;


    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_my_account, null);
        context = getActivity();
        return view;
    }

    @Override
    protected void initLayout(View view) {
        mFragMain = (FragmentMainActivity) getActivity();
        mMeFragment = (MeFragment) getParentFragment();
        view.findViewById(R.id.imgBack).setOnClickListener(v -> {
            mMeFragment.openHomeFrag();
            // mFragMain.setHomeNavigation();
        });
        name = view.findViewById(R.id.doc_name);
        email = view.findViewById(R.id.email);
        profileImage = view.findViewById(R.id.dr_image);
        setProfile();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mTabletMode = preferences.getBoolean("tabletmode", false);


        view.findViewById(R.id.setting).setOnClickListener(
                v -> {
                    SettingsFragment settingsActivity = new SettingsFragment();
                    mMeFragment.replaceFragment(settingsActivity);
                }
        );

        initView();
    }

    private void setProfile() {
        String titleText = CDoctor2Application.getLoginInfo().getUserInfo().getFirstName() + " " + CDoctor2Application.getLoginInfo().getUserInfo().getLastname();
        if (name != null && titleText != null)
            name.setText(titleText);
        email.setText(CDoctor2Application.getLoginInfo().getAccount());
        if (CDoctor2Application.getLoginInfo().getUserInfo().getSex().equals("M")) {
            profileImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_doc));
        } else {
            profileImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.user_girl));
        }
    }

    private void initView() {
        mAccountBtn = view.findViewById(R.id.card_my_profile);
        // mVitalsBtn = view.findViewById(R.id.btn_vitals);
        mFamilyBtn = view.findViewById(R.id.card_family);
        mCallHistBtn = view.findViewById(R.id.card_call_history);
        mMedicationBtn = view.findViewById(R.id.card_medication);
        mLabBtn = view.findViewById(R.id.card_lab_report);
        mImmunizationBtn = view.findViewById(R.id.card_Immunization);
        mAllergiesBtn = view.findViewById(R.id.card_Allergies);
        mPaymentBtn = view.findViewById(R.id.card_payment);
        mMonBtn = view.findViewById(R.id.card_monitor);
        mVaccBtn = view.findViewById(R.id.card_vaciination);
        mLogoutBtn = view.findViewById(R.id.logout_btn);

        mAccountBtn.setOnClickListener(this);
//        mVitalsBtn.setOnClickListener(this);
        mFamilyBtn.setOnClickListener(this);
        mCallHistBtn.setOnClickListener(this);
        mMedicationBtn.setOnClickListener(this);
        mLabBtn.setOnClickListener(this);
        mAllergiesBtn.setOnClickListener(this);
        mImmunizationBtn.setOnClickListener(this);
        mPaymentBtn.setOnClickListener(this);
        mVaccBtn.setOnClickListener(this);
        mMonBtn.setOnClickListener(this);
        mLogoutBtn.setOnClickListener(this);

        view.findViewById(R.id.card_refferal).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {


        Bundle bundle = new Bundle();
        ComingSoonFragment comingSoonFragment = new ComingSoonFragment();
        switch (v.getId()) {
            case R.id.card_my_profile:
                AccountFragment accountFragment = new AccountFragment();
                mMeFragment.replaceFragment(accountFragment);
                break;
//            case R.id.btn_vitals:
//                MyVitalsFragment myVitalsFragment = new MyVitalsFragment();
//                Bundle bundle2 = new Bundle();
//                MyVitals patientVitals;
//
//                try {
//                    patientVitals = (MyVitals) GetPatVitals(mFragMain.getLoginInfo2().getAccount()).get();
//                } catch (Exception e) {
//                    patientVitals = null;
//                }
//
//                if (patientVitals != null) {
//                    bundle2.putSerializable("myvitals", patientVitals);
//                    myVitalsFragment.setArguments(bundle2);
//                    mMeFragment.replaceFragment(myVitalsFragment);
//                }
//
//                break;
            case R.id.card_family:
                FamilyFragment familyFragment = new FamilyFragment();
                bundle.putBoolean("TYPE", true);
                familyFragment.setArguments(bundle);
                mMeFragment.replaceFragment(familyFragment);
                break;

            case R.id.card_call_history:
                CallLogFragment callLogFragment = new CallLogFragment();
                mMeFragment.replaceFragment(callLogFragment);
                break;
            case R.id.card_medication:
                MedicationFragment medicationFragment = MedicationFragment.newInstance(mFragMain.getLoginInfo2().getUserInfo(), false);
                mMeFragment.replaceFragment(medicationFragment);

                break;
            case R.id.card_lab_report:
                LabFragment labFragment = LabFragment.newInstance(mFragMain.getLoginInfo2().getUserInfo(), false);
                mMeFragment.replaceFragment(labFragment);
                break;
            case R.id.card_Immunization:
                ImmunizationFragment immunizationFragment = ImmunizationFragment.newInstance(mFragMain.getLoginInfo2().getUserInfo(), false);
                mMeFragment.replaceFragment(immunizationFragment);
                break;
            case R.id.card_Allergies:
                AllergiesFragment allergiesFragment = AllergiesFragment.newInstance(mFragMain.getLoginInfo2().getUserInfo(), false);
                mMeFragment.replaceFragment(allergiesFragment);
                break;
            case R.id.card_payment:
                PaymentCreditCardFrag creditCardFrag = PaymentCreditCardFrag.newInstance(mFragMain.getLoginInfo2().getUserInfo());
                mMeFragment.replaceFragment(creditCardFrag);
               /* Intent intent1 = new Intent(getActivity(), PaymentCreditCardFrag.class);
                intent1.putExtra("cctype", 0);
                startActivity(intent1);*/
                break;
            case R.id.card_vaciination:
                SecureMessageParent secureMessageFragment = SecureMessageParent.newInstance(mFragMain.getLoginInfo2().getUserInfo());
                mMeFragment.replaceFragment(secureMessageFragment);

                break;
            case R.id.card_monitor:
                IOTGraph iotGraph = IOTGraph.newInstance("", Long.toString(System.currentTimeMillis() / 1000), "", "");
                Bundle data=iotGraph.getArguments();
                if(data!=null) {
                    data.putBoolean(FROM_USER, true);
                }else {
                    data=new Bundle();
                    data.putBoolean(FROM_USER, true);
                }
                iotGraph.setArguments(data);
                mMeFragment.replaceFragment(iotGraph);
               /* VitalCheckFragment vitalCheckFragment = VitalCheckFragment.newInstance(mFragMain.getLoginInfo2().getUserInfo(), false,false);
                mMeFragment.replaceFragment(vitalCheckFragment);*/
                break;
            case R.id.logout_btn:
                mFragMain.showLogOutDialog();
                break;
            case R.id.card_refferal:
                ReferalFragment referalFragment = ReferalFragment.newInstance(mFragMain.getLoginInfo2().getUserInfo(), false);
                mMeFragment.replaceFragment(referalFragment);
                break;
        }
    }


    @Override
    public void onMyResume() {
        setProfile();
    }

    @Override
    public void onMyStop() {

    }
}
