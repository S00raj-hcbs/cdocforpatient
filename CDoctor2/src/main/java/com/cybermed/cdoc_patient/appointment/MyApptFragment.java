package com.cybermed.cdoc_patient.appointment;

import static com.cybermed.cdoc_patient.common.BaseActivity.CALL_TYPE_OUT_GOING;
import static com.cybermed.cdoc_patient.common.BaseActivity.DOCTOR_FREE;
import static com.cybermed.cdoc_patient.util.AppConstant.APPT_DATE_TIME_FORMAT;
import static com.cybermed.cdoc_patient.util.AppConstant.APPT_LIST;
import static com.cybermed.cdoc_patient.util.AppConstant.DATE_FORMAT;
import static com.cybermed.cdoc_patient.util.AppConstant.PAGE_APPT;
import static com.cybermed.cdoc_patient.util.AppConstant.PAGE_HOME;
import static com.cybermed.cdoc_patient.util.AppConstant.REQUEST_APPT_VIDEOCALL;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cdfortis.datainterface.soap.WebService;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.common.CommonAsyncTaskActivity;
import com.cybermed.cdoc_patient.databinding.FragmentApptListBinding;
import com.cybermed.cdoc_patient.doctor.VideoCallActivity;
import com.cybermed.cdoc_patient.doctor.docDetail.model.BaseResponseModel;
import com.cybermed.cdoc_patient.doctor.docDetail.model.ReqApptDateList;
import com.cybermed.cdoc_patient.doctor.docDetail.model.ResApptList;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;
import com.cybermed.cdoc_patient.main.HomeApptAdapter;
import com.cybermed.cdoc_patient.main.HomeFragment;
import com.cybermed.cdoc_patient.main.MainFragment;
import com.cybermed.cdoc_patient.main.chat.ChatActivity;
import com.cybermed.cdoc_patient.util.AppUtiltiy;
import com.cybermed.cdoc_patient.util.DateUtil;
import com.cybermed.cdoc_patient.util.ErrorMessage;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.manager.HomeApiManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyApptFragment extends BaseFragment
        implements MyApptRecyclerViewAdapter.ItemClickListener, HomeApptAdapter.ItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private MyApptRecyclerViewAdapter adapter;
    private HomeApptAdapter homeApptAdapter;
    private FragmentMainActivity fragMain;
    private SwipeRefreshLayout swipeContainer;
    private RelativeLayout relativeEmptyView;
    private String myApptTab;
    private boolean isFromHome, loading;
    private IApptCallBack iApptCallBack;
    FragmentApptListBinding binding;
    int pageNum = 0;
    int PAGE_SIZE = 20;
    boolean isLoading, isFilterApplied;
    Activity context;
    String date;
    HashMap<String, ArrayList<ResApptList>> mapList;
    AppointmentFragment secondFragment;
    public MyApptFragment() {
        // Required empty public constructor
    }

    public static MyApptFragment newInstance(String myApptTab, List<ResApptList> patientHistoryList) {
        MyApptFragment myApptFragment = new MyApptFragment();
        Bundle args = new Bundle();
        args.putString("past_upcoming", myApptTab);
        args.putParcelableArrayList(APPT_LIST, (ArrayList<? extends Parcelable>) patientHistoryList);
        args.putBoolean("FromHome", true);
        myApptFragment.setArguments(args);
        return myApptFragment;
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_appt_list, container, false);
        fragMain = (FragmentMainActivity) getActivity();
        if (getArguments() != null) {
            myApptTab = getArguments().getString("past_upcoming", FUTUREAPPT);
            isFromHome = getArguments().getBoolean("FromHome", false);
        }
        context = getActivity();
        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {
        relativeEmptyView = binding.relativeEmptyView;
        TextView txtMessage = binding.txtMsgAppt;
        Button btnScheduled = binding.btnScheduled;
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        btnScheduled.setOnClickListener(v -> {
            iApptCallBack.bookNewAppt();
        });
        secondFragment = (AppointmentFragment) getParentFragmentManager().findFragmentById(R.id.home_container);
        mapList = new HashMap<>();
        date = getDate();
        binding.txtAppt.setText(DateUtil.formatedDate(date, "MM/dd/yyyy", DATE_FORMAT));
        txtMessage.setText(R.string.do_not_have_past);
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, 0));
        if (!isFromHome) {
            adapter = new MyApptRecyclerViewAdapter(context);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
            showProgress();
            callApi();
            binding.relBtnScheduled.setVisibility(View.VISIBLE);
        } else {
            initiliazeHomeAdapter();
        }
    }

    private void initiliazeHomeAdapter() {
        List<ResApptList> patientHistoryList = getArguments().getParcelableArrayList(APPT_LIST);
        homeApptAdapter = new HomeApptAdapter(context);
        homeApptAdapter.setClickListener(this);
        homeApptAdapter.appendList(patientHistoryList, myApptTab);
        binding.relBtnScheduled.setVisibility(View.GONE);
        binding.recyclerView.setAdapter(homeApptAdapter);
        binding.recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void refreshFragment(boolean isRefresh) {
        if (getArguments() != null) {
            myApptTab = getArguments().getString("past_upcoming", FUTUREAPPT);
            isFromHome = getArguments().getBoolean("FromHome", false);
        }
        if (isRefresh) {
            isLoading = false;
            pageNum = 0;
            showProgress();
            callApi();
        }
    }


    /**
     * api call for patient
     */
    public void callApi() {
        date = getDate();
        if (pageNum == 0)
            showProgress();
        getPatientPastAppt("0","0");
    }

    /**
     * api call for patient
     */
    public void callFilterApi(String date) {
        isFilterApplied = true;
        this.date = date;
        pageNum = 0;
        showProgress();
        getPatientPastAppt(date,"1");
    }

    @Override
    public void onItemClick(ResApptList patientAppointment, boolean isDelete) {
        if (isDelete) {
            if (!patientAppointment.getApptStatus().equals("0") && !patientAppointment.getApptStatus().equals("6")) {
                showStatusDialog(getString(R.string.session_complete_toast), null);
            } else
                showDeleteDialog(patientAppointment);
        }
    }

    @Override
    public void onItemCancel(ResApptList patientAppointment, boolean isDelete) {
        if (isDelete) {
            if (!patientAppointment.getApptStatus().equals("0") && !patientAppointment.getApptStatus().equals("6")) {
                showStatusDialog(getString(R.string.session_complete_toast), null);
            } else
                showDeleteDialog(patientAppointment);
        }
    }

    @Override
    public void onBtnScheduled(ResApptList patientAppointment) {
        //getDocDetail(patientAppointment.getProviderCode(), false, true, PAGE_APPT);
        if (patientAppointment.getApptStatus() != null && !patientAppointment.getApptStatus().equals(" ") && !patientAppointment.getApptStatus().equals("")){
            if (Integer.parseInt(patientAppointment.getApptStatus())==10||Integer.parseInt(patientAppointment.getApptStatus())==11){
                getDocScheduale(patientAppointment.getProviderCode(), false, false, PAGE_APPT,true,patientAppointment.getApptId(),patientAppointment.getChiefComplaint(),patientAppointment.getChiefComplaintNotes());
            }  else {
                getDocScheduale(patientAppointment.getProviderCode(), true, false, PAGE_APPT,true,patientAppointment.getApptId(),patientAppointment.getChiefComplaint(),patientAppointment.getChiefComplaintNotes());
            }
        }

    }

    @Override
    public void videoAppt(ResApptList patientAppointment) {
        /*if ((CDoctor2Application.getLoginInfo().getTriageConfig().equalsIgnoreCase("true") &&
                patientAppointment.getIsSupport().equalsIgnoreCase("True") ||
                CDoctor2Application.getLoginInfo().getTriageConfig().equalsIgnoreCase("false"))) {
            String providerName = patientAppointment.getProviderFirstName() + " " + patientAppointment.getProviderLastName();
            getProviderOnlineStatus(patientAppointment.getProviderCode(), patientAppointment.getOrgCode(),
                    patientAppointment, providerName);
            //dialogVideoCall(patientAppointment);
        } else {
            showStatusDialog(getString(R.string.contact_support), null);
        }*/
        String providerName = patientAppointment.getProviderFirstName() + " " + patientAppointment.getProviderLastName();
        getProviderOnlineStatus(patientAppointment.getProviderCode(), patientAppointment.getOrgCode(),
                patientAppointment, providerName);
    }

    @Override
    public void chatAppt(ResApptList patientAppointment) {
        String providerName = patientAppointment.getProviderFirstName() + " " + patientAppointment.getProviderLastName();
        /*Log.e("providerName",""+providerName);
        Log.e("ProviderID",""+patientAppointment.getProvider_id());
        Log.e("getProviderCode",""+patientAppointment.getProviderCode());
        Log.e("org_code",""+patientAppointment.getOrgCode());
        Log.e("appt_id",""+patientAppointment.getApptId());*/
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("appt_id", patientAppointment.getApptId());
        intent.putExtra("ProviderID", patientAppointment.getProvider_id());
        intent.putExtra("provider_code", patientAppointment.getProviderCode());
        intent.putExtra("org_code", patientAppointment.getOrgCode());
        intent.putExtra("name", providerName);
        startActivity(intent);
    }

    @Override
    public void filterReset() {
        callApi();
    }

    /**
     * show video call dialog
     *
     * @param patientAppointment patient appt info
     */
    private void dialogVideoCall(ResApptList patientAppointment) {
        String providerName = patientAppointment.getProviderFirstName() + " " + patientAppointment.getProviderLastName();
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_cdoc_support);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        Button btn_call = dialog.findViewById(R.id.btn_call);
        TextView txtTittle = dialog.findViewById(R.id.txt_title);
        btn_cancel.setText(R.string.cancel);
        TextView txtMessage = dialog.findViewById(R.id.txt_message);
        txtTittle.setVisibility(View.GONE);
        txtMessage.setText(getString(R.string.start_a_video_call_with) + providerName + "?");
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                /*getProviderOnlineStatus(patientAppointment.getProviderCode(), patientAppointment.getOrgCode(),
                        patientAppointment, providerName);*/
                Intent intent = new Intent(context, VideoCallActivity.class);
                intent.putExtra("type", CALL_TYPE_OUT_GOING);
                intent.putExtra("appt_id", patientAppointment.getApptId());
                intent.putExtra("orgCode", patientAppointment.getOrgCode());
                intent.putExtra("providerId", patientAppointment.getProviderCode());
                intent.putExtra("docName", providerName);
                intent.putExtra("apptStatus", patientAppointment.getApptStatus());
                intent.putExtra("paymentType", DOCTOR_FREE);
                intent.putExtra("isskipped", true);
                startActivityForResult(intent, REQUEST_APPT_VIDEOCALL);

                /*Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("appt_id", patientAppointment.getApptId());
                intent.putExtra("ProviderID", patientAppointment.getProvider_id());
                intent.putExtra("name", providerName);
                startActivity(intent);*/
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showStatusDialog(String message, String title) {
        ErrorMessage.alertDialog(context, title,
                message, null);
    }

    void showDeleteDialog(ResApptList patientAppointment) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.dialog_cancel_appt);
        Button btn_ok = dialog.findViewById(R.id.btnConfirm);
        TextView docName = dialog.findViewById(R.id.txtDoctName);
        TextView date = dialog.findViewById(R.id.txtdate);
        date.setText(patientAppointment.getApptDate());
        docName.setText(patientAppointment.getProviderFirstName() + " " + patientAppointment.getProviderLastName());
        ImageView imgCancel = dialog.findViewById(R.id.imgCancel);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                boolean cancelSuccess = false;
                try {
                    int result = (int) cancelAppointment(patientAppointment.getOrgCode(),
                            patientAppointment.getProviderCode(), patientAppointment.getApptId()).get();
                    if (result == 1) {
                        cancelSuccess = true;
                        patientAppointment.setApptStatus("1");
                        if (isFromHome) {
                            homeApptAdapter.notifyDataSetChanged();
                        } else
                            adapter.notifyDataSetChanged();
                        Toast.makeText(context, getString(R.string.cancel_succeed), Toast.LENGTH_LONG).show();
                        String msg_to_notify = getString(R.string.patient) + fragMain.getLoginInfo2().getUserInfo().getFirstName() + " " + fragMain.getLoginInfo2().getUserInfo().getLastname()
                                + getString(R.string.has_cancelled) + patientAppointment.getApptDate() + getString(R.string.appointment_with_you);
                        Notify_Provider(patientAppointment.getOrgCode(), patientAppointment.getApptId(), msg_to_notify).get();
                    } else {
                        Toast.makeText(context, getString(R.string.cancel_failed), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    if (!cancelSuccess) {
                        Toast.makeText(context, getString(R.string.cancel_failed), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        imgCancel.setOnClickListener(v -> {
                    dialog.dismiss();
                }
        );
        dialog.show();
    }

    private void getProviderOnlineStatus(String provider_id, String org_code,
                                         final ResApptList patientAppointment, final String docName) {
        fragMain.GetProviderOnlineStatus(provider_id, org_code, new CommonAsyncTaskActivity.GetProviderOnlineStatus() {
            @Override
            public void GetProviderOnlineStatusResult(Integer integer) {
                if (integer == 0 || integer == 2) {
                    ErrorMessage.alertDialog(context, getString(R.string.provider_unavailable_title),
                            getString(R.string.provider_unavailable_msg), null);

                } else if (integer == 1) {
                    dialogVideoCall(patientAppointment);

                   /* Intent intent = new Intent(context, VideoCallActivity.class);
                    intent.putExtra("type", CALL_TYPE_OUT_GOING);
                    intent.putExtra("appt_id", patientAppointment.getApptId());
                    intent.putExtra("orgCode", patientAppointment.getOrgCode());
                    intent.putExtra("providerId", patientAppointment.getProviderCode());
                    intent.putExtra("docName", docName);
                    intent.putExtra("apptStatus", patientAppointment.getApptStatus());
                    intent.putExtra("paymentType", DOCTOR_FREE);
                    intent.putExtra("isskipped", true);
                    startActivityForResult(intent, REQUEST_APPT_VIDEOCALL);*/
                }
            }
        });
    }

    private void getPatientPastAppt(String date,String is_filter) {
        ReqApptDateList reqApptList = new ReqApptDateList();
        reqApptList.setCountPerPage(20);
        reqApptList.setDateToSearch(date);
        reqApptList.setUserId(CDoctor2Application.getLoginInfo().getAccount());
        reqApptList.setOrgCode(CDoctor2Application.getLoginInfo().getUserInfo().getService_code());
        reqApptList.setPageNumber(pageNum);
        HomeApiManager apiManager = new HomeApiManager(new IResponseReceiver() {
            @Override
            public void onSuccess(Object data) {
                List<ResApptList> patientAppointments = ((BaseResponseModel<List<ResApptList>>) data).getObject();
                int totalRecords = ((BaseResponseModel<List<ResApptList>>) data).getTotalRecords();

                if (is_filter.equals("1")){
                    if (secondFragment != null) {
                        secondFragment.setFilterOptionVisibility(true);
                    }
                }else {
                    if (secondFragment != null) {
                        secondFragment.setFilterOptionVisibility(false);
                    }
                }
                setPatientList(patientAppointments,is_filter);
                loading = false;
                binding.swipeRefreshLayout.setRefreshing(false);
                binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                        if (!loading) {
                            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                                    && firstVisibleItemPosition >= 0
                                    && totalItemCount >= PAGE_SIZE
                                    && totalItemCount < totalRecords) {
                                binding.swipeRefreshLayout.setRefreshing(true);
                                pageNum++;
                                callApi();
                                loading = true;
                            }
                        }
                    }
                });
                hideProgress();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                binding.swipeRefreshLayout.setRefreshing(false);
                isFilterApplied = false;
                hideProgress();
            }
        }, context);
        apiManager.getAppointmentList(reqApptList);
    }



    void setPatientList(List<ResApptList> patientAppointments, String is_filter) {

        if (patientAppointments != null && patientAppointments.size() > 0) {
            if (adapter != null) {
                if (pageNum == 0) {
                    adapter.clearList();
                }
            }
            mapList = new HashMap<>();
            // List<ResApptList> patientHistoryList = new ArrayList<>();
            relativeEmptyView.setVisibility(View.GONE);
           /* for (ResApptList patHist : patientAppointments) {
                if (patHist.getApptDate() != null) {
                    patHist.setApptType(getTypeOfAppt(patHist));
                    patientHistoryList.add(patHist);
                }
            }
            if (adapter != null) {
                if (pageNum == 0) {
                    adapter.clearList();
                }
                adapter.appendList(patientHistoryList, myApptTab);
            }
            binding.swipeRefreshLayout.setRefreshing(false);
            Log.d("patientHistory", "get_provider_avail success");
            if (isFilterApplied) {
                isFilterApplied = false;
                if (iApptCallBack != null) {
                    iApptCallBack.apiCallBack(true);
                }
            }*/
            for (ResApptList resCallLog : patientAppointments) {
                if (mapList.containsKey(DateUtil.formatedDate(resCallLog.getApptDate(), "MM/dd/yyyy hh:mm:ss aa", DATE_FORMAT))) {
                    ArrayList<ResApptList> list = mapList.get(DateUtil.formatedDate(resCallLog.getApptDate(), "MM/dd/yyyy hh:mm:ss aa", DATE_FORMAT));
                    if (resCallLog.getApptDate() != null) {
                        resCallLog.setApptType(getTypeOfAppt(resCallLog));
                    }
                    list.add(resCallLog);
                    mapList.put(DateUtil.formatedDate(resCallLog.getApptDate(), "MM/dd/yyyy hh:mm:ss aa", DATE_FORMAT), list);
                } else {
                    ArrayList<ResApptList> list = new ArrayList<>();
                    if (resCallLog.getApptDate() != null) {
                        resCallLog.setApptType(getTypeOfAppt(resCallLog));
                    }
                    list.add(resCallLog);
                    mapList.put(DateUtil.formatedDate(resCallLog.getApptDate(), "MM/dd/yyyy hh:mm:ss aa", DATE_FORMAT), list);
                }
            }
            ArrayList<ResApptList> resCallLogList = new ArrayList<>();
            Map<String, ArrayList<ResApptList>> hashMap = AppUtiltiy.sortByKeys3(mapList);
            for (Map.Entry<String, ArrayList<ResApptList>> entry : hashMap.entrySet()) {
                ResApptList item = new ResApptList();
                item.setViewType(1);
                item.setStartTime((entry.getKey()));
                resCallLogList.add(item);
                resCallLogList.addAll(entry.getValue());
            }
            if (adapter != null) {
                if (pageNum == 0) {
                    adapter.clearList();
                }
                adapter.appendList(resCallLogList, myApptTab,is_filter);
            }
          //  binding.swipeRefreshLayout.setEnabled(true);
            binding.swipeRefreshLayout.setRefreshing(false);
            Log.d("patientHistory", "get_provider_avail success");
            if (isFilterApplied) {
                isFilterApplied = false;
                if (iApptCallBack != null) {
                    iApptCallBack.apiCallBack(true);
                }
            }
        } else {
            isFilterApplied = false;
            binding.swipeRefreshLayout.setRefreshing(false);
          /*  if (is_filter.equals("0")){
                fragMain.toDoctorList();
            }*/
            if (pageNum == 0) {
                if (adapter != null) {
                    adapter.clearList();
                    adapter.notifyDataSetChanged();
                }
                relativeEmptyView.setVisibility(View.GONE);
                binding.txtAppt.setText(DateUtil.formatedDate(date, "MM/dd/yyyy", DATE_FORMAT));
            }

            if (adapter != null && adapter.getDisplayedList().size() == 0) {
                relativeEmptyView.setVisibility(View.VISIBLE);
                if (is_filter.equals("1")){
                    binding.txtAppt.setText(DateUtil.formatedDate(date, "MM/dd/yyyy", DATE_FORMAT));
                }

            }
           /* if (binding.txtAppt.getText().toString().equals(DateUtil.formatedDate(getDate(), "MM/dd/yyyy", DATE_FORMAT))){
                binding.swipeRefreshLayout.setEnabled(true);
            }else {
                binding.swipeRefreshLayout.setEnabled(false);
            }*/
            if (iApptCallBack != null) {
                // iApptCallBack.showPlusIcon();
                iApptCallBack.apiCallBack(false);
            }
            Log.d("patientHistory", "get_provider_avail failed");
        }
    }

    public static AsyncTask cancelAppointment(final String org_code,
                                              final String provider_code, final String appt_id) {
        return new AsyncTask<Object, Object, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Object... params) {
                try {
                    return WebService.getInstance().cancel_appointment(org_code, provider_code, appt_id);
                } catch (Exception e) {
                    this.e = e;
                }
                return -1;
            }
        }.execute();
    }

    public static AsyncTask Notify_Provider(final String org_code, final String provider_code,
                                            final String message) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().Notify_Provider(org_code, provider_code, message, message);
                } catch (Exception e) {
                }
                return null;
            }
        }.execute();
    }

    public void setListner(IApptCallBack iApptCallBack) {
        this.iApptCallBack = iApptCallBack;
    }

    @Override
    public void makeAppt(ResApptList patientAppointment, boolean isVideoClinicAppt) {
        getDocDetail(patientAppointment.getProviderCode(), isVideoClinicAppt, false, PAGE_HOME);
    }

    @Override
    public void waitingAppt(ResApptList patientAppointment) {
        if (!patientAppointment.getApptStatus().equals("11")) {
            showStatusDialog(getString(R.string.waiting_room_msg), null);
        }
    }


    @Override
    public void ReSchedualAppt(ResApptList patientAppointment) {
        /*if (!patientAppointment.getApptStatus().equals("11")) {
            showStatusDialog(getString(R.string.waiting_room_msg), null);
        }*/
        if (patientAppointment.getApptStatus() != null && !patientAppointment.getApptStatus().equals(" ") && !patientAppointment.getApptStatus().equals("")){
            if (Integer.parseInt(patientAppointment.getApptStatus())==10||Integer.parseInt(patientAppointment.getApptStatus())==11){
                getDocScheduale(patientAppointment.getProviderCode(), false, false, PAGE_HOME,true,patientAppointment.getApptId(),patientAppointment.getChiefComplaint(),patientAppointment.getChiefComplaintNotes());
            }  else {
                getDocScheduale(patientAppointment.getProviderCode(), true, false, PAGE_HOME,true,patientAppointment.getApptId(),patientAppointment.getChiefComplaint(),patientAppointment.getChiefComplaintNotes());
            }
        }
    }

    /**
     * pull to refresh
     */
    @Override
    public void onRefresh() {
        if (secondFragment != null) {
            secondFragment.setFilterOptionVisibility(false);
        }
        pageNum = 0;
        callApi();
        new Handler().postDelayed(() -> {
            if (secondFragment != null) {
                secondFragment.setFilterOptionVisibility(false);
            }
        }, 2000);
    }


    public interface IApptCallBack {
        void showPlusIcon();
        void bookNewAppt();
        void apiCallBack(boolean isShowDateHeader);
    }

    /**
     * open doctor profile
     *
     * @param providerCode      provider code
     * @param isVideoClinicAppt true: book appointment for video appt. / false clinic appt
     * @param isOpenProfile     true: open doctor profile page/ false open doctor booking page
     * @param pageType          decide page is from appt list or home page appt list
     */
    private void getDocDetail(String providerCode, boolean isVideoClinicAppt, boolean isOpenProfile, String pageType) {
        if (getParentFragment() instanceof MainFragment) {
            if ((HomeFragment) ((MainFragment) getParentFragment()).getParentFragment() != null)
                ((HomeFragment) ((MainFragment) getParentFragment()).getParentFragment())
                        .openDocDetail(providerCode, pageType, isVideoClinicAppt, isOpenProfile);
        } else {
            if ((HomeFragment) getParentFragment() != null)
                ((HomeFragment) getParentFragment())
                        .openDocDetail(providerCode, pageType, isVideoClinicAppt, isOpenProfile);
        }

    }


    /**
     * open doctor Schedual
     *
     * @param providerCode      provider code
     * @param isVideoClinicAppt true: book appointment for video appt. / false clinic appt
     * @param isOpenProfile     true: open doctor profile page/ false open doctor booking page
     * @param pageType          decide page is from appt list or home page appt list
     */
    private void getDocScheduale(String providerCode, boolean isVideoClinicAppt, boolean isOpenProfile, String pageType, boolean is_reschedule,String apptId,String Reason,String notes) {
        if (getParentFragment() instanceof MainFragment) {
            if ((HomeFragment) ((MainFragment) getParentFragment()).getParentFragment() != null)
              /*  ((HomeFragment) ((MainFragment) getParentFragment()).getParentFragment())
                        .openDocDetail(providerCode, pageType, isVideoClinicAppt, isOpenProfile);*/
                ((HomeFragment) ((MainFragment) getParentFragment()).getParentFragment()).openDocBook(providerCode, pageType, isVideoClinicAppt, isOpenProfile,is_reschedule,apptId,Reason,notes);


        } else {
            if ((HomeFragment) getParentFragment() != null)
               /* ((HomeFragment) getParentFragment())
                        .openDocDetail(providerCode, pageType, isVideoClinicAppt, isOpenProfile);*/
                ((HomeFragment) getParentFragment()).openDocBook(providerCode, pageType, isVideoClinicAppt, isOpenProfile,is_reschedule,apptId,Reason,notes);

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        refreshFragment(true);
    }

    String getTypeOfAppt(ResApptList appt) {
        SimpleDateFormat sdf = new SimpleDateFormat(APPT_DATE_TIME_FORMAT);
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
        Date strDate = null, currentDate = null;
        try {
            strDate = sdf.parse(appt.getApptDate());
            strDate = sdf2.parse(sdf2.format(strDate));
            String currDate = sdf2.format(new Date());
            currentDate = sdf2.parse(currDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (currentDate.compareTo(strDate) < 0 || currentDate.compareTo(strDate) == 0) {
            return appt.getApptStatus().equals("2") || appt.getApptStatus().equals("3") ? PASTAPPT : FUTUREAPPT;
        } else return PASTAPPT;
    }

}



