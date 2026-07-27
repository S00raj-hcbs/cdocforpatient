package com.cybermed.cdoc_patient.me.medication;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cdfortis.datainterface.soap.UserInfo;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.annotation.Factory;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.PreferenceUtil;
import com.cybermed.cdoc_patient.databinding.FragmentMedicationUiBinding;
import com.cybermed.cdoc_patient.doctor.docDetail.model.BaseResponseModel;
import com.cybermed.cdoc_patient.doctor.searchDoctor.CalendarHelper;
import com.cybermed.cdoc_patient.main.HomeFragment;
import com.cybermed.cdoc_patient.me.MeFragment;
import com.cybermed.cdoc_patient.me.manager.ProfileApiManager;
import com.cybermed.cdoc_patient.me.medication.adapter.MedicationRecyclerViewAdapter;
import com.cybermed.cdoc_patient.me.medication.model.MedicationData;
import com.cybermed.cdoc_patient.util.DateUtil;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import static com.cybermed.cdoc_patient.me.MeFragment.USERINFOKEY;
import static com.cybermed.cdoc_patient.util.AppConstant.DATE_TIME_FORMAT;
import static com.cybermed.cdoc_patient.util.AppConstant.IS_FROM_HEALTH_RECORD;
import static com.cybermed.cdoc_patient.util.AppConstant.MEDICATION_REMINDER;
import static com.cybermed.cdoc_patient.util.AppConstant.SERVER_DATE_FORMAT;


public class MedicationFragment extends BaseFragment implements IResponseReceiver<BaseResponseModel<List<MedicationData>>>,
        MeFragment.OnInnerFragmentStatusChange,HomeFragment.OnInnerFragmentStatusChange {


    MedicationRecyclerViewAdapter medicationRecyclerViewAdapter;
    Activity context;
    private Hashtable<Integer, String> calendarIdTable;
    FragmentMedicationUiBinding binding;

    @Factory
    public static MedicationFragment newInstance(UserInfo userInfo, boolean isFromHealthRecord) {
        MedicationFragment fragment = new MedicationFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putSerializable(USERINFOKEY, userInfo);
        args.putBoolean(IS_FROM_HEALTH_RECORD, isFromHealthRecord);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_medication_ui, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {

        //Set Year, Month, Day
        context = getActivity();
        CalendarHelper.requestCalendarReadWritePermission(context);
        setRecyclerView();
        callApi();
        clickListner();
    }

    private void clickListner() {
        binding.toolBar.txtTittle.setText(getString(R.string.medication));
        binding.toolBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getArguments() != null && getArguments().getBoolean(IS_FROM_HEALTH_RECORD)) {
                    if (getParentFragment() != null)
                        ((HomeFragment) getParentFragment()).openHealthRecordFragment();
                } else {
                    if (((MeFragment) getParentFragment() != null)) {
                        ((MeFragment) getParentFragment()).openUserActivityFragment();
                    }
                }
            }
        });
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callApi();
            }
        });
    }

    //***************api call and success failure**************************************
    private void callApi() {
        Bundle args = getArguments();
        if (args != null) {
            UserInfo userInfo = (UserInfo) args.getSerializable(USERINFOKEY);
            if (userInfo != null) {
                ProfileApiManager medicationManager = new ProfileApiManager(this, context);
                medicationManager.getMedicationList(userInfo.getService_code(), userInfo.getEmail());
                showProgress();
            }
        }
    }


    @Override
    public void onSuccess(BaseResponseModel<List<MedicationData>> data) {
        hideProgress();
        binding.swipeRefreshLayout.setRefreshing(false);
        List<MedicationData> dataList = ((BaseResponseModel<List<MedicationData>>) data).getObject();
        if (data != null && dataList.size() > 0) {
            binding.emptyLayout.setVisibility(View.GONE);
            for (MedicationData list : dataList) {
                String date = DateUtil.formatedDate(list.getEntryDate(),
                        SERVER_DATE_FORMAT, DATE_TIME_FORMAT);
                list.setEntryDate(date);
            }
            setList(dataList);
        } else {
            binding.emptyLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFailure(@NonNull String errorResponse) {
        hideProgress();
        binding.swipeRefreshLayout.setRefreshing(false);
        binding.emptyLayout.setVisibility(View.VISIBLE);
    }

    /**
     * set recycler view
     */
    void setRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        medicationRecyclerViewAdapter = new MedicationRecyclerViewAdapter(new ArrayList<>(), context);
        medicationRecyclerViewAdapter.setListner(new MedicationRecyclerViewAdapter.ItemClickListner() {
            @Override
            public void itemClick(MedicationData medicationData) {
                showDate(medicationData);
            }

        });
        binding.recyclerView.setAdapter(medicationRecyclerViewAdapter);
    }

    void setList(List<MedicationData> data) {
        if (medicationRecyclerViewAdapter != null)
            medicationRecyclerViewAdapter.setList(data);
    }


    //****************Set reminder*******************************************
    void showDate(MedicationData medicationData) {
        Calendar cal = Calendar.getInstance(Locale.US);
        android.app.DatePickerDialog datePicker = new android.app.DatePickerDialog(
                getActivity(), (view, year1, month, dayOfMonth1) -> {
            String tempMonth, tempDay;
            if ((month + 1) < 10)
                tempMonth = "0" + (month + 1);
            else
                tempMonth = (month + 1) + "";

            if (dayOfMonth1 < 10)
                tempDay = "0" + dayOfMonth1;
            else
                tempDay = dayOfMonth1 + "";

            openTimePicker(medicationData, tempMonth + "/" + tempDay + "/" + year1);

        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePicker.show();


    }

    private void openTimePicker(MedicationData medicationData, String date) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                try {
                    SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
                    SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
                    Date _24HourDt = _24HourSDF.parse(selectedHour + ":" + selectedMinute);
                    System.out.println(_24HourDt);
                    System.out.println(_12HourSDF.format(_24HourDt));
                    setCalender(medicationData, date, _12HourSDF.format(_24HourDt));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle(getString(R.string.select_time));
        mTimePicker.show();
    }

    private void setCalender(MedicationData medicationData, String date, String time) {
        if (CalendarHelper.haveCalendarReadWritePermissions(context)) {
            calendarIdTable = CalendarHelper.listCalendarId(context);
            CalendarHelper.updateCalendarIdSpinner(calendarIdTable);
            CalendarHelper.addNewEvent(calendarIdTable, context,
                    getString(R.string.take_mdeicine),
                    getString(R.string.please_take_medicine) + " " + medicationData.getDrugInfo(),
                    date + " " + time, System.currentTimeMillis(), new CalendarHelper.ICalenderSuccess() {
                        @Override
                        public void eventIdSuccess(int calenderid) {
                            Toast.makeText(context, getString(R.string.reminder_added),
                                    Toast.LENGTH_LONG).show();
                            PreferenceUtil.commitString(MEDICATION_REMINDER + medicationData.getEntryDate() + medicationData.getDrugInfo(), String.valueOf(calenderid));
                            medicationRecyclerViewAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void calenderFailure() {

                        }
                    });

        } else {
            CalendarHelper.requestCalendarReadWritePermission(context);
        }
    }

    @Override
    public void refreshFragment(boolean isRefresh) {
        super.refreshFragment(isRefresh);

    }

    @Override
    public void onMyResume() {
        callApi();
    }

    @Override
    public void onMyStop() {

    }
}
