package com.cybermed.cdoc_patient.doctor;

import static com.cybermed.cdoc_patient.common.videoui.Constant.ishomesnot;
import static com.cybermed.cdoc_patient.util.AppConstant.FROM_SEARCH;
import static com.cybermed.cdoc_patient.util.AppConstant.ONLINE_STATUS;
import static com.cybermed.cdoc_patient.util.AppConstant.PAGE_DOC_LIST;
import static com.cybermed.cdoc_patient.util.AppConstant.PAGE_SEARCH;
import static com.cybermed.cdoc_patient.util.AppConstant.PROVIDER_CODE;
import static com.cybermed.cdoc_patient.util.AppConstant.SIGNALR_ONLINE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.WebServiceID;
import com.cdfortis.datainterface.soap.model.DocInfo;
import com.cdfortis.datainterface.soap.model.SoapObjectVector;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.appointment.AppointmentFragment;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.common.StateAbbr;
import com.cybermed.cdoc_patient.doctor.doctorFilter.DoctorDialogFilter;
import com.cybermed.cdoc_patient.doctor.doctorFilter.FilterCommunicator;
import com.cybermed.cdoc_patient.doctor.searchDoctor.RequestDoctorInfo;
import com.cybermed.cdoc_patient.doctor.searchDoctor.ResponseDocInfo;
import com.cybermed.cdoc_patient.doctor.searchDoctor.SearchDocAdapter;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;
import com.cybermed.cdoc_patient.main.HomeFragment;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.manager.HomeApiManager;
import com.cybermed.cdoc_patient.webapi.model.response.TriageConfigResponse;

import org.ksoap2.serialization.SoapObject;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * Created by qinwe on 2017/5/3.
 */

public class DoctorListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener
        , AdapterView.OnItemClickListener, View.OnClickListener {
    public static final int DOC_LIST_APPT_RETURN = 1002;
    public static final int DOC_LIST_WAITING_RETURN = 1102;

    private static int rangeStart = 1;
    private static int rangeEnd = 10;
    private boolean hasMore = true;
    private boolean listLoadMore = false;
    private int stateSpinnerPositionStore;
    private int specialtySpinnerPositionStore;
    private int languageSpinnerPositionStore;
    private SwipeRefreshLayout refreshLayout;
    private DoctorAdapter adapter;
    private FragmentMainActivity fragMain;
    private HomeFragment homeFragment;
    private TextView title;
    private TextView mEmptyView;
    private View mEmptyFilterView;
    private View view;
    private boolean isFilter = false;
    private Spinner stateSpinner;
    private Spinner specialitiesSpinner;
    private Spinner languageSpinner;
    private String filterState;
    private String filterState2;
    private String filterSpecialty;
    private String filterLanguage;
    private String userId;
    private String currState;
    private ImageView filterBtn, imgSearch;
    private ListView listView;
    BroadcastReceiver receiver;
    Activity context;
    LinearLayout linearSearchView;
    TextView txtSearchTittle, txtSearchMessage, specialtyFilterText, languageFilterText, stateFilterText;
    ImageView imgSearchView, specialtyFilterBtn, languageFilterBtn, stateFilterBtn;
    AppCompatEditText searchInput;
    boolean isForSearch;
    TextWatcher searchTextWatcher;
    HomeApiManager apiManager;
    RelativeLayout specialtyTab, languageTab, stateTab;
    LinearLayout filtersTab;
    String isTriageSupport;
    ProgressBar progressBar;
    FilterCommunicator communicator;
    private AppointmentFragment ApptFragment;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        view = inflater.inflate(R.layout.fragment_doctor_list, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        WindowCompat.setDecorFitsSystemWindows(getActivity().getWindow(), true);

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            return WindowInsetsCompat.CONSUMED;
        });
        }
        return view;
    }

    @Override
    protected void initLayout(View view) {
        fragMain = (FragmentMainActivity) getActivity();
        userId = fragMain.getLoginInfo2().getAccount();
        getTriageSupport();
        initToolBar();
        initView();
        ApptFragment = new AppointmentFragment();
        context = getActivity();
        receiveOnlineStatus();
    }


    private void receiveOnlineStatus() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle data = intent.getExtras();
                if (data != null) {
                    String onlineStatus = data.getString(ONLINE_STATUS);
                    String providerCode = data.getString(PROVIDER_CODE);
                    if (adapter != null)
                        adapter.setOnlineStatus(onlineStatus, providerCode);
                }
            }
        };
    }

    private void initToolBar() {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        filterBtn = toolbar.findViewById(R.id.filterBtn);
        //get current state
        currState = getState();

        fragMain.setSupportActionBar(toolbar);
        fragMain.getSupportActionBar().setDisplayShowTitleEnabled(false);
        // toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icon_back_row));

        toolbar.setNavigationOnClickListener(v -> {
            if (searchInput != null) {
                if (apiManager != null)
                    apiManager.cancelSearchRequest();
                searchInput.removeTextChangedListener(searchTextWatcher);
                searchInput.setText("");
            }
            if (ishomesnot.equals("appointment")){
                homeFragment = (HomeFragment) getParentFragment();
             /*   homeFragment.openMainActivity();
                fragMain.setHomeNavigation();*/
                homeFragment.openApptFragment("1", true);
            }else {
                homeFragment = (HomeFragment) getParentFragment();
                homeFragment.openMainActivity();
                fragMain.setHomeNavigation();
            }

        });


        toolbar.setTitleTextColor(Color.WHITE);
//        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
//        mTitle.setText(getString(R.string.service_code_all) + " - " + getString(R.string.doclist_heading));

        title = view.findViewById(R.id.toolbar_title);
        filterBtn.setOnClickListener(v -> {
            DoctorDialogFilter filter = new DoctorDialogFilter();
            filter.show(getParentFragmentManager(), "Filter Fragment");
            applyFilters();
        });

        title.setOnClickListener(v -> {
            DoctorDialogFilter filter = new DoctorDialogFilter();
            filter.show(getParentFragmentManager(), "Filter Fragment");
            //  filterDialog();
            applyFilters();
        });

    }

    private void applyFilters() {
        getParentFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                super.onFragmentDestroyed(fm, f);
                filterState = communicator.getState();
                filterState2 = communicator.getState();
                filterLanguage = communicator.getLanguage();
                filterSpecialty = communicator.getSpecialty();

                Log.e("filterState",""+filterState);
                String[] filters = context.getResources().getStringArray(R.array.state_name);
                int j=0;
                for (int i=0;i<filters.length;i++){
                    if (filters[i].equals(filterState)){
                        j=i;
                        break;
                    }
                }
                String[] filters2 = context.getResources().getStringArray(R.array.state);

                filterState=filters2[j];
                Log.e("filterState",""+filterState);
                if (filterState.equals("All")){
                    filterState="";
                    filterState2="";
                }
                isFilter=true;
                updateFiltersBar();
                getProviderList(userId, filterState, filterSpecialty, filterLanguage, false);
                getParentFragmentManager().unregisterFragmentLifecycleCallbacks(this);
            }
        }, false);

    }

    private void setTitle() {
        if (!currState.equals("")) {
            title.setText(StateAbbr.valueOfAbbreviation(currState).toString());
        } else {
          //  title.setText(getString(R.string.service_code_all) + " " + getString(R.string.doclist_heading));
            title.setText(getString(R.string.browse_our_doctors));
        }
    }

    private void initView() {
        Bundle bundle = getArguments();
        isForSearch = bundle.getBoolean(FROM_SEARCH, false);
        listView = view.findViewById(R.id.listView);
        txtSearchMessage = view.findViewById(R.id.txtSearchMessage);
        txtSearchTittle = view.findViewById(R.id.txtSearchTitle);
        imgSearchView = view.findViewById(R.id.imgSearchImage);
        searchInput = view.findViewById(R.id.edtSearch);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        filtersTab = view.findViewById(R.id.filtersTab);
        specialtyTab = view.findViewById(R.id.filterSpecialityTab);
        languageTab = view.findViewById(R.id.filterLanguageTab);
        stateTab = view.findViewById(R.id.filterStateTab);
        specialtyFilterText = view.findViewById(R.id.filterSpecialtyText);
        languageFilterText = view.findViewById(R.id.filterLanguageText);
        stateFilterText = view.findViewById(R.id.filterStateText);
        linearSearchView = view.findViewById(R.id.linear_searchView);
        specialtyFilterBtn = view.findViewById(R.id.filterSpecialtyCloseBtn);
        languageFilterBtn = view.findViewById(R.id.filterLanguageCloseBtn);
        stateFilterBtn = view.findViewById(R.id.filterStateCloseBtn);
        mEmptyView = view.findViewById(R.id.emptyNote);
        mEmptyFilterView = view.findViewById(R.id.emptyFilter);
        imgSearch = view.findViewById(R.id.searchImg);
        progressBar = view.findViewById(R.id.progress);
        listView.setOnItemClickListener(this);

    }

    private void apiCall() {
        if (isForSearch) {
            searchInput.setVisibility(View.VISIBLE);
            searchInput.requestFocus();
            searchInput.requestFocusFromTouch();
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            title.setText(/*R.string.doc_search*/getString(R.string.browse_our_doctors));
            filterBtn.setVisibility(View.GONE);
            refreshLayout.setEnabled(false);
            refreshLayout.setRefreshing(false);
            linearSearchView.setVisibility(View.VISIBLE);
            SearchDocAdapter searchDocAdapter = new SearchDocAdapter(getActivity());
            listView.setAdapter(searchDocAdapter);

            apiManager = new HomeApiManager(new IResponseReceiver() {
                @Override
                public void onSuccess(Object data) {
                    if (isAdded() && isVisible()) {
                        progressBar.setVisibility(View.GONE);
                        List<ResponseDocInfo> docInfo = (List<ResponseDocInfo>) data;
                        if (docInfo != null) {
                            if (docInfo.size() == 0) {
                                searchDocAdapter.clearList();
                                noSearchFoundView();
                            } else {
                                listView.setVisibility(View.VISIBLE);
                                linearSearchView.setVisibility(View.GONE);
                                searchDocAdapter.clearList();
                                searchDocAdapter.appendList(docInfo);
                            }
                        } else {
                            searchDocAdapter.clearList();
                            noSearchFoundView();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull String errorResponse) {
                    progressBar.setVisibility(View.GONE);
                }
            }, context);

            searchTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                private Timer timer = new Timer();
                private final long DELAY = 1500; // Milliseconds

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().isEmpty()) {
                        searchDocAdapter.clearList();
                        emptySearchView();
                        progressBar.setVisibility(View.GONE);
                    } else {
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        apiManager.getSearchDoc(new RequestDoctorInfo(
                                                CDoctor2Application.getLoginInfo().getAccount(),
                                                Objects.requireNonNull(searchInput.getText()).toString(), isTriageSupport == null ? false : Boolean.parseBoolean(isTriageSupport)));
                                    }
                                },
                                DELAY
                        );
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
            };
            searchInput.addTextChangedListener(searchTextWatcher);
        } else {
            setTitle();
            filterBtn.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
            refreshLayout.setEnabled(true);
            refreshLayout.setRefreshing(true);
            getProviderList(userId, getState(), "", "", false);
            listLoadMore = true;
            adapter = new DoctorAdapter(getActivity(), true);
            listView.setAdapter(adapter);
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {

                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                        if (listLoadMore) {
                            listLoadMore = false;
                            if (isFilter) {
                                getProviderList(userId, filterState, filterSpecialty, filterLanguage, true);
                            } else {
                                getProviderList(userId, currState, "", "", true);
                            }
                        }
                    }
                }
            });
            refreshLayout.setOnRefreshListener(this);
        }

    }

    private void getTriageSupport() {
        showProgress();
        new HomeApiManager(new IResponseReceiver<TriageConfigResponse>() {
            @Override
            public void onSuccess(TriageConfigResponse data) {
                hideProgress();
                if (data.getTriageConfiguration() != null && data.getTriageConfiguration().equals("1")) {
                    isTriageSupport = "true";
                } else isTriageSupport = "false";
                CDoctor2Application.getLoginInfo().setTriageConfig(isTriageSupport);
                apiCall();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                hideProgress();
                apiCall();
            }
        }, getActivity()).getTriageConfig(CDoctor2Application.getLoginInfo().getUserInfo().getService_code());

    }


    private void updateFiltersBar() {
        if (filterState.equals("") && filterSpecialty.equals("") && filterLanguage.equals("")) {
            filtersTab.setVisibility(View.GONE);
            //title.setText(getString(R.string.doclist_heading));
            title.setText(getString(R.string.browse_our_doctors));
        } else {
            filtersTab.setVisibility(View.VISIBLE);
            if (filterState.equals("")) {
               // title.setText(getString(R.string.doclist_heading));
                title.setText(getString(R.string.browse_our_doctors));
                stateTab.setVisibility(View.GONE);
            } else {
                title.setText(filterState2);
                stateFilterText.setText(filterState2);
                stateTab.setVisibility(View.VISIBLE);
            }
            if (filterSpecialty.equals(""))
                specialtyTab.setVisibility(View.GONE);
            else {
                specialtyFilterText.setText(filterSpecialty);
                specialtyTab.setVisibility(View.VISIBLE);
            }
            if (filterLanguage.equals("")) {
                languageTab.setVisibility(View.GONE);
            } else {
                languageFilterText.setText(filterLanguage);
                languageTab.setVisibility(View.VISIBLE);
            }


            stateFilterBtn.setOnClickListener(v -> {
                title.setText(getString(R.string.doclist_heading));
                communicator.setState("");
                filterState = "";
                getProviderList(userId, filterState, filterSpecialty, filterLanguage, false);
                stateTab.setVisibility(View.GONE);
                if (filterState.equals("") && filterSpecialty.equals("") && filterLanguage.equals(""))
                    filtersTab.setVisibility(View.GONE);
            });

            specialtyFilterBtn.setOnClickListener(v -> {
                communicator.setSpecialty("");
                filterSpecialty = "";
                getProviderList(userId, filterState, filterSpecialty, filterLanguage, false);
                specialtyTab.setVisibility(View.GONE);
                if (filterState.equals("") && filterSpecialty.equals("") && filterLanguage.equals(""))
                    filtersTab.setVisibility(View.GONE);
            });


            languageFilterBtn.setOnClickListener(v -> {
                communicator.setLanguage("");
                filterLanguage = "";
                getProviderList(userId, filterState, filterSpecialty, filterLanguage, false);
                languageTab.setVisibility(View.GONE);
                if (filterState.equals("") && filterSpecialty.equals("") && filterLanguage.equals(""))
                    filtersTab.setVisibility(View.GONE);
            });
        }

    }

    void emptySearchView() {
        if (isAdded() && isVisible()) {
            listView.setVisibility(View.GONE);
            linearSearchView.setVisibility(View.VISIBLE);
            txtSearchTittle.setText(getResources().getString(R.string.find_your_doctor));
            txtSearchMessage.setText(getResources().getString(R.string.search_doctors_by_name_for_consultation));
            imgSearchView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.search_inprogress));
        }
    }

    void noSearchFoundView() {
        if (isAdded() && isVisible()) {
            listView.setVisibility(View.GONE);
            linearSearchView.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(searchInput.getText().toString())) {
                emptySearchView();
            } else {
                txtSearchTittle.setText(getResources().getString(R.string.no_match_found));
                txtSearchMessage.setText(getResources().getString(R.string.try_another_string));
                imgSearchView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.search_oops));
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            int mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        communicator = FilterCommunicator.getInstance();
    }

    public void reloadWithNewState() {
        currState = getState();
        if (!isForSearch) {
            setTitle();
            getProviderList(userId, currState, "", "", false);
        }

    }

    private String getState() {
        //Filtered state is only removed after logout
        //filtered state takes precedence over default state unless default state is changed after filtered state
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String defaultState = prefs.getString("filtered_state", "");

        if (defaultState.equals("") || !isFilter) {
            filterBtn.setSelected(false);
            isFilter = false;
            defaultState = fragMain.getLoginInfo2().getUserInfo().getDefault_state();
            if (defaultState.equals("All")) {
                defaultState = "";
            }
        } else if (defaultState.equals("All")) {
            filterBtn.setSelected(true);
            isFilter = true;
            defaultState = "";
        } else {
            filterBtn.setSelected(true);
            isFilter = true;

        }
        return defaultState;
    }


    private void getProviderList(final String user_id, final String state,
                                 final String specialty, final String language, final boolean fromScroll) {
        //not scroll down at the bottom
        if (!fromScroll)
            hasMore = true;

        //scroll down but doesnt have anymore
        if (fromScroll && !hasMore) {
            return;
        } else {
            refreshLayout.setRefreshing(true);
        }

        //list more change range, otherwise reset to 1 and 10
        if (fromScroll) {
            rangeStart = rangeEnd + 1;
            rangeEnd = rangeEnd + 10;
        } else {
            rangeStart = 1;
            rangeEnd = 10;
        }


        OnPostExecute ope = result -> {
            listLoadMore = true;
            Vector<DocInfo> doctorInfos = new SoapObjectVector<>(DocInfo.class, (SoapObject) result);
//            doctorInfos = filterDocList(doctorInfos);
            refreshLayout.setRefreshing(false);

            //swipe for more images
            if (fromScroll) {
                //doesnt have anymore image to load
                if (doctorInfos.size() == 0) {
                    hasMore = false;
                }
                adapter.appendList(doctorInfos);
            } else if (doctorInfos.size() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
                String[] filters = context.getResources().getStringArray(R.array.state);
                int j=0;
                for (int i=0;i<filters.length;i++){
                    if (filters[i].equals(state)){
                        j=i;
                        break;
                    }
                }
                String[] filters2 = context.getResources().getStringArray(R.array.state_name);

                if (filters2[j].equals("All")){
                    mEmptyView.setText(getString(R.string.no_doctors_are_available_in_this_area));
                }else {
                    String Fname=filters2[j];
                    if (isAdded()) {
                        getActivity().runOnUiThread(() -> {
                            mEmptyView.setText(getString(R.string.no_doctors_are_available_in_this_area)+"\n("+Fname+")");
                        });
                    }

                }

                mEmptyFilterView.setVisibility(View.GONE);
                adapter.refreshData(doctorInfos);
            } else {
                mEmptyView.setVisibility(View.GONE);
                mEmptyFilterView.setVisibility(View.GONE);
                adapter.refreshData(doctorInfos);
            }
        };
        if (!TextUtils.isEmpty(CDoctor2Application.getLoginInfo().getTriageConfig())) {
            WebService.webServiceAsyncTask(WebServiceID.getProviderList_V4, ope,
                    String.valueOf(rangeStart), String.valueOf(rangeEnd), state, language, specialty, user_id,
                    "2", (isTriageSupport.equals("1") || isTriageSupport.equals("true")) ? "true" : "false");

        } else WebService.webServiceAsyncTask(WebServiceID.getProviderList_V4, ope,
                String.valueOf(rangeStart), String.valueOf(rangeEnd), state, language,
                specialty, user_id, "2", "false");


    }



    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, new IntentFilter(SIGNALR_ONLINE), Context.RECEIVER_EXPORTED);
        }else {
            context.registerReceiver(receiver, new IntentFilter(SIGNALR_ONLINE));
        }


        //Just in case tablet mode crashes and needs a change in status
        // getPatientOnlineStatus();

    }

    @Override
    public void onRefresh() {

        if (isFilter) {
            getProviderList(userId, filterState, filterSpecialty, filterLanguage, false);
        } else {
            getProviderList(userId, currState, "", "", false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 888) {
            getProviderList(userId, currState, "", "", false);
        } else if (resultCode == DOC_LIST_APPT_RETURN) {

            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle(getString(R.string.doc_profile_complete_appt));
            alertDialog.setMessage(getString(R.string.doc_profile_complete_appt_message));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.doc_profile_complete_appt_ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            fragMain.homefragment.openApptFragment(FUTUREAPPT, false);
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else if (resultCode == DOC_LIST_WAITING_RETURN) {

            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle(getString(R.string.doc_profile_waiting_confirmation));
            alertDialog.setMessage(getString(R.string.doc_profile_waiting_confirmation_message));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.doc_profile_complete_appt_ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            fragMain.homefragment.openApptFragment(FUTUREAPPT, false);
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        hideKeyBoard();
        if (isForSearch) {
            fragMain.homefragment.openDocDetail(((ResponseDocInfo) parent.getAdapter().getItem(position)).getProviderCode(),
                    PAGE_SEARCH, false, true);
        } else {
            DocInfo docInfo = (DocInfo) parent.getAdapter().getItem(position);
            fragMain.homefragment.openDocDetail(docInfo.provider_code, PAGE_DOC_LIST,
                    false, true);
        }
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onPause() {
        super.onPause();
        hideKeyBoard();
    }

    private void hideKeyBoard() {
        if (view.getWindowToken() != null) {
            InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }
}
