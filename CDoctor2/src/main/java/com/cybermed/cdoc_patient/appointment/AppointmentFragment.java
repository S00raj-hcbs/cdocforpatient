package com.cybermed.cdoc_patient.appointment;

import static com.cybermed.cdoc_patient.util.AppConstant.APPT_DATE_TIME_FORMAT;
import static com.cybermed.cdoc_patient.util.AppConstant.DATE_FORMAT;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;
import com.cybermed.cdoc_patient.util.DateUtil;

import java.util.Calendar;

/**
 * Created by ldf on 16/11/4.
 */

public class AppointmentFragment extends BaseFragment implements MyApptFragment.IApptCallBack,
        DatePickerDialog.OnDateSetListener {

    private View view;
    private FragmentMainActivity fragMain;
    private MyApptFragment apptFrag;
    private ImageView bookAppt;
    private int openApptCode;
    private boolean hasOpened = false;
    ImageView imgCalenderFilter;
    RelativeLayout relativeDateView;
    TextView txtFilterReset,txtDateHeader;
    private DatePickerDialog datePickerDialog;


    /**
     * Appointment View. Shows future and past appointments
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_appt, null);
        return view;
    }

    @Override
    protected void initLayout(View view) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        fragMain = (FragmentMainActivity) getActivity();
        hasOpened = true;
        initToolBar(openApptCode);
        setContainer();
    }

    private void setContainer() {
        apptFrag = newInstance(PASTAPPT);
        FragmentTransaction transaction = getParentFragment().getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.container, apptFrag).commit();
    }

    private MyApptFragment newInstance(String myApptTab) {
        MyApptFragment myApptFragment = new MyApptFragment();
        myApptFragment.setListner(this);
        Bundle args = new Bundle();
        args.putString("past_upcoming", myApptTab);
        myApptFragment.setArguments(args);
        return myApptFragment;
    }

    private void initToolBar(final int data) {
        bookAppt = view.findViewById(R.id.bookAppt);
        relativeDateView = view.findViewById(R.id.relativeDateView);
        ImageView back = view.findViewById(R.id.img_back);
        back.setOnClickListener(v -> {
            if (data == MAINFRAGMENTTYPE) {
                if (fragMain != null)
                    fragMain.setHomeNavigation();
            }
        });
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(requireContext(), this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        imgCalenderFilter = view.findViewById(R.id.datePick);
        txtDateHeader=view.findViewById(R.id.txtDateHeader);
        txtDateHeader.setText(DateUtil.formatedDate(getDate(),APPT_DATE_TIME_FORMAT,DATE_FORMAT));
        txtFilterReset = view.findViewById(R.id.txtFilter);
        txtDateHeader.setVisibility(View.GONE);
        relativeDateView.setVisibility(View.GONE);
        imgCalenderFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        txtFilterReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // txtDateHeader.setVisibility(View.GONE);
               // relativeDateView.setVisibility(View.GONE);
                txtDateHeader.setText(DateUtil.formatedDate(getDate(),APPT_DATE_TIME_FORMAT,DATE_FORMAT));
                //imgCalenderFilter.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_white_trans));
               //txtFilterReset.setVisibility(View.GONE);
                apptFrag.callApi();
            }
        });

        bookAppt.setOnClickListener(view -> fragMain.toDoctorList());

    }
    public void setFilterOptionVisibility(boolean isVisible) {
        if (relativeDateView != null) {
            relativeDateView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            txtFilterReset.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }
    @Override
    public void fragmentOpened(int data) {
        openApptCode = data;
        //When fragment opened, update toolbar
        if (hasOpened) {
            initToolBar(openApptCode);
            // imgCalenderFilter.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_white_trans));
            txtFilterReset.setVisibility(View.GONE);
            apptFrag.callApi();
        }
    }


    @Override
    public void showPlusIcon() {
        // bookAppt.setVisibility(View.GONE);
    }

    @Override
    public void bookNewAppt() {
        fragMain.toDoctorList();
    }

    @Override
    public void apiCallBack(boolean isShowDateHeader) {

        if(isShowDateHeader){
            txtDateHeader.setVisibility(View.VISIBLE);
        }else {
            txtDateHeader.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        //  imgCalenderFilter.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_blue_trans));
        String date=(month + 1) + "/" + dayOfMonth + "/" + year;
        txtDateHeader.setText(DateUtil.formatedDate(date,"MM/dd/yyyy",DATE_FORMAT));
        apptFrag.callFilterApi(date);
        //txtFilterReset.setVisibility(View.VISIBLE);
       // relativeDateView.setVisibility(View.VISIBLE);
    }

}

