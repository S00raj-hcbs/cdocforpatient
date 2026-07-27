package com.cybermed.cdoc_patient.me.calllog;

import static com.cybermed.cdoc_patient.util.AppConstant.DATE_FORMAT;
import static com.cybermed.cdoc_patient.util.AppConstant.DATE_FORMAT_PROFILE;
import static com.cybermed.cdoc_patient.util.AppConstant.PAGE_HOME;
import static com.cybermed.cdoc_patient.util.AppConstant.SERVER_DATE_FORMAT;
import static com.cybermed.cdoc_patient.util.AppConstant.TIME_FORMAT;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.doctor.docDetail.model.BaseResponseModel;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;
import com.cybermed.cdoc_patient.main.HomeFragment;
import com.cybermed.cdoc_patient.me.MeFragment;
import com.cybermed.cdoc_patient.me.calllog.adapter.CallLogRecyclerViewAdapter;
import com.cybermed.cdoc_patient.me.calllog.model.ReqCallLog;
import com.cybermed.cdoc_patient.me.calllog.model.ResCallLog;
import com.cybermed.cdoc_patient.me.manager.ProfileApiManager;
import com.cybermed.cdoc_patient.util.AppConstant;
import com.cybermed.cdoc_patient.util.AppUtiltiy;
import com.cybermed.cdoc_patient.util.DateUtil;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CallLogFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener,
        CallLogRecyclerViewAdapter.ItemClickListener, SwipeRefreshLayout.OnRefreshListener, MeFragment.OnInnerFragmentStatusChange {

    private static final String DATEPICKER_TAG = "datepicker";
    private CallLogRecyclerViewAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private DatePickerDialog datePickerDialog;
    private String user_id;
    private LinearLayout emptyNote;
    private View view;
    private FragmentMainActivity fragMain;
    private boolean loading;
    RecyclerView recyclerView;
    int pageNum = 0;
    int PAGE_SIZE = 20;
    ImageView imgCalenderFilter;
    TextView emptyTittleMessage;
    TextView emptyTittleDesc, txtFilter;
    String datePicked;
    Button btnConsult;
    Context context;

    HashMap<Date, ArrayList<ResCallLog>> mapList;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_call_log, null);
        return view;
    }

    @Override
    protected void initLayout(View view) {
        fragMain = (FragmentMainActivity) getActivity();
        user_id = fragMain.getLoginInfo2().getAccount();
        context = getActivity();

        emptyTittleMessage = view.findViewById(R.id.txtEmptyTittle);
        emptyTittleDesc = view.findViewById(R.id.txtMessage);
        emptyNote = view.findViewById(R.id.emptyNote);
        btnConsult = view.findViewById(R.id.btnConsult);
        swipeContainer = view.findViewById(R.id.swipeRefreshLayout);
        txtFilter = view.findViewById(R.id.txtFilter);
        swipeContainer.setOnRefreshListener(this);
        mapList = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        setRecyclerView();
        initToolBar();
        getPatientCallLog(null, false);
    }

    private void setRecyclerView() {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CallLogRecyclerViewAdapter(getActivity());
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }


    private void initToolBar() {
        ImageView backBtn = view.findViewById(R.id.back_btn);
        imgCalenderFilter = view.findViewById(R.id.datePick);
        imgCalenderFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        backBtn.setOnClickListener((View.OnClickListener) v -> {
            if (((MeFragment) getParentFragment() != null)) {
                ((MeFragment) getParentFragment()).openUserActivityFragment();
            }
        });

    }


    private void getPatientCallLog(String date, boolean isFilter) {
        showProgress();
        ProfileApiManager profileApiManager = new ProfileApiManager(new IResponseReceiver() {
            @Override
            public void onSuccess(Object data) {
                hideProgress();
                swipeContainer.setRefreshing(false);
                ArrayList<ResCallLog> responseModel = ((BaseResponseModel<ArrayList<ResCallLog>>) data).getObject();
                int totalRecords = ((BaseResponseModel<List<ResCallLog>>) data).getTotalRecords();
                setList(responseModel, isFilter);
                loading = false;
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                        if (!loading) {
                            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                                    && firstVisibleItemPosition >= 0
                                    && totalItemCount >= PAGE_SIZE
                                    && totalItemCount < totalRecords) {
                                swipeContainer.setRefreshing(true);
                                pageNum++;
                                getPatientCallLog(date, false);
                                loading = true;
                            }
                        }
                    }
                });


            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                hideProgress();
                emptyView(isFilter, true);

            }
        }, context);
        ReqCallLog reqCallLog = new ReqCallLog();
        reqCallLog.setPageNumber(pageNum);
        reqCallLog.setUserId(user_id);
        reqCallLog.setDateToSearch(date);
        reqCallLog.setCountPerPage(PAGE_SIZE);
        profileApiManager.getCallLog(reqCallLog);
    }

    private void setList(ArrayList<ResCallLog> responseModel, boolean isFilter) {
        if (responseModel != null) {
            if (responseModel.size() > 0) {
                emptyView(isFilter, false);
                SimpleDateFormat formatter = new SimpleDateFormat(AppConstant.DATE_FORMAT, Locale.getDefault());
                Date date = null;
                for (ResCallLog resCallLog : responseModel) {
                    try {
                        date = formatter.parse(DateUtil.formatedDate(resCallLog.getStartTime(), "yyyy-MM-dd'T'hh:mm:ss", AppConstant.DATE_FORMAT));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (mapList.containsKey(date)) {
                        ArrayList<ResCallLog> list = mapList.get(date);
                        list.add(resCallLog);
                        mapList.put(date, list);
                    } else {
                        ArrayList<ResCallLog> list = new ArrayList<>();
                        list.add(resCallLog);
                        mapList.put(date, list);
                    }
                }
                ArrayList<ResCallLog> resCallLogList = new ArrayList<>();
                Map<Date, ArrayList<ResCallLog>> hashMap = AppUtiltiy.sortByKeys3(mapList);
                for (Map.Entry<Date, ArrayList<ResCallLog>> entry : hashMap.entrySet()) {
                    ResCallLog item = new ResCallLog();
                    item.setViewType(1);
                    item.setStartTime(formatter.format(entry.getKey()));
                    resCallLogList.add(item);
                    resCallLogList.addAll(entry.getValue());
                }
                adapter.appendList(resCallLogList, isFilter);
            } else {
                emptyView(isFilter, true);
            }

        }

    }

    /**
     * @param isFilter    true :filter applied
     * @param isEmptyView true: show empty view
     */
    private void emptyView(boolean isFilter, boolean isEmptyView) {
        if (isEmptyView) {
            swipeContainer.setRefreshing(false);
            swipeContainer.setVisibility(View.GONE);
            emptyNote.setVisibility(View.VISIBLE);
        } else {
            emptyNote.setVisibility(View.GONE);
            swipeContainer.setVisibility(View.VISIBLE);
        }
        if (isFilter) {
            txtFilter.setVisibility(View.VISIBLE);
            emptyTittleDesc.setText(getString(R.string.sorr_no_calllog));
            emptyTittleMessage.setText(DateUtil.formatedDate(datePicked, DATE_FORMAT_PROFILE, DATE_FORMAT));
        } else {
            txtFilter.setVisibility(View.GONE);
            emptyTittleMessage.setText(getString(R.string.call_logs_empty));
            emptyTittleDesc.setText(getString(R.string.call_log_consultation));
        }
        txtFilter.setOnClickListener(v -> filterReset());
        btnConsult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeFragment) ((MeFragment) getParentFragment()).getParentFragment())
                        .openDoctorList(false, true);
            }
        });
    }


    @Override
    public void onRefresh() {
        pageNum = 0;
        mapList.clear();
        getPatientCallLog(null, false);
    }

    @Override
    public void onItemClick(View view, ResCallLog callLog) {
        if (!TextUtils.isEmpty(callLog.getProviderCode())) {
            /*if ((CDoctor2Application.getLoginInfo().getTriageConfig().equalsIgnoreCase("true") &&
                    callLog.getIsSupport().equalsIgnoreCase("True") ||
                    CDoctor2Application.getLoginInfo().getTriageConfig().equalsIgnoreCase("false"))) {
                ((HomeFragment) ((MeFragment) getParentFragment()).getParentFragment())
                        .openDocDetail(callLog.getProviderCode(), PAGE_HOME, false, true);
            }else{
                ErrorMessage.alertDialog(context, null,
                        getString(R.string.contact_support), null);
            }*/
            final Dialog dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.profile_dialogue_layout);
            TextView doc_name = dialog.findViewById(R.id.doc_name);
            TextView textAns = dialog.findViewById(R.id.textAns);
            TextView textCharge = dialog.findViewById(R.id.textCharge);
            TextView timeText = dialog.findViewById(R.id.timeText);
            TextView callType = dialog.findViewById(R.id.callType);
            ImageView call_img = dialog.findViewById(R.id.call_img);
            /*txt_title.setText(getString(R.string.view_provider_profile, callLog.getProviderName()));*/
            Button btn_ok = dialog.findViewById(R.id.btn_ok);
            doc_name.setText(callLog.getProviderName());
      //      textAns.setText(callLog.getProviderName());

            timeText.setText(DateUtil.formatedDate(callLog.getStartTime(), SERVER_DATE_FORMAT, TIME_FORMAT));
            textAns.setText(callLog.getStartTime());
            callType.setText(callLog.getCallType());
            if (callLog.getCallType().contains("Outgoing")) {
                call_img.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.out_call));
            } else {
                call_img.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_incoming));
            }
            String  amountString = (TextUtils.isEmpty(callLog.getChargeAmount()) || callLog.getChargeAmount().equals("0")) ?getString(R.string.charge)+ context.getString(R.string.no_charges) : (context.getString(R.string.call_logs_charge_amount) +
                    callLog.getChargeAmount().substring(0, callLog.getChargeAmount().indexOf(".") + 3));
            textCharge.setText(amountString);

//            if (!TextUtils.isEmpty(callLog.getTalkMin())){
//                try {
//                    int min = Integer.parseInt(callLog.getTalkMin());
//                    if (min > 60)
//                        textAns.setText(context.getString(R.string.call_logs_duration_time_hrs, min / 60, min % 60));
//                    else if (min != 0 && min < 60)
//                        textAns.setText(context.getString(R.string.call_logs_duration_time, min));
//                    else
//                        textAns.setText("Call Not Answered");
//                } catch (NumberFormatException e) {
//                    textAns.setText("Call Not Answered");
//                }
//            }else {
//                textAns.setText("Call Not Answered");
//            }

            if (!TextUtils.isEmpty(callLog.getTalkMin())){
                try {
                    int min = Integer.parseInt(callLog.getTalkMin());
                    if (min > 60)
                        textAns.setText(context.getString(R.string.call_logs_duration_time_hrs, min / 60, min % 60));
                    else if (min >1 && min < 60)
                        textAns.setText(context.getString(R.string.call_logs_duration_time, min));
                    else if (min == 1)
                        textAns.setText(context.getString(R.string.call_logs_duration_time2, min));
                    else
                        textAns.setText(context.getString(R.string.not_answered));
                } catch (NumberFormatException e) {
                    textAns.setText(context.getString(R.string.not_answered));
                }
            }else {
                textAns.setText(context.getString(R.string.not_answered));
            }

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    ((HomeFragment) ((MeFragment) getParentFragment()).getParentFragment())
                            .openDocDetail(callLog.getProviderCode(), PAGE_HOME, false, true);
                }
            });
            dialog.show();


        }

    }

    @Override
    public void filterReset() {
        imgCalenderFilter.setBackground(null);
        pageNum = 0;
        mapList.clear();
        getPatientCallLog(null, false);
    }

    @Override
    public void onMyResume() {
        pageNum = 0;
        mapList.clear();
        showProgress();
        getPatientCallLog(null, false);
    }

    @Override
    public void onMyStop() {

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        pageNum = 0;
        mapList.clear();
        imgCalenderFilter.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_blue_trans));
        datePicked = (month + 1) + "/" + dayOfMonth + "/" + year;
        getPatientCallLog(datePicked, true);

    }
}
