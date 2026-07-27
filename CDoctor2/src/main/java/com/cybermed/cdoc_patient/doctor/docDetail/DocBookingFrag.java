package com.cybermed.cdoc_patient.doctor.docDetail;

import static com.cdfortis.datainterface.soap.WebServiceID.Check_Timeslot_Availablity;
import static com.cybermed.cdoc_patient.util.AppConstant.FEE_SCHEDULE;
import static com.cybermed.cdoc_patient.util.AppConstant.FREE_PROVIDER;
import static com.cybermed.cdoc_patient.util.AppConstant.PAID_BY_VISIT;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.WebService;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.RecyclerViewAdapter;
import com.cybermed.cdoc_patient.common.base.BaseMVVMFragment;
import com.cybermed.cdoc_patient.common.videoui.Constant;
import com.cybermed.cdoc_patient.databinding.DialogCancelApptBinding;
import com.cybermed.cdoc_patient.databinding.DialogueForConfirmBinding;
import com.cybermed.cdoc_patient.databinding.DocBookingFragBinding;
import com.cybermed.cdoc_patient.doctor.docDetail.model.DayDateModel;
import com.cybermed.cdoc_patient.util.AppConstant;
import com.cybermed.cdoc_patient.view.MyAlertDialog;

import org.jsoup.helper.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * doc booking page
 */
public class DocBookingFrag extends BaseMVVMFragment<DocBookingVm> {

    DocBookingFragBinding binding;
    DayDateModel mDayDateModel;
    int apptDateIncrement = 0;
    private RecyclerViewAdapter recyclerViewHorizontalAdapter;
    private View ChildView;
    private int RecyclerViewItemPosition;
    private boolean hasApptTimeLoaded = false;


    @Override
    protected DocBookingVm createViewModel() {
        return new ViewModelProvider(getActivity()).get(DocBookingVm.class);
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.doc_booking_frag;
    }

    @Override
    public void onViewModelCreated(View view, DocBookingVm viewModel) {
        binding = (DocBookingFragBinding) getDataBinding();
        initBookingList();
        registerObserver();
        clickListner();
        //stop loader if time exceed
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (!hasApptTimeLoaded) {
                binding.apptProgressBar.setVisibility(View.GONE);
                binding.unavailableTxt.setVisibility(View.VISIBLE);
            }
        }, 10000);

        if (viewModel.getDocInfo().getValue().isVideoAppoitnmentType()) {
            binding.toolbar.txtTittle.setText(getString(R.string.book_virtual_visit));
        } else {
            binding.toolbar.txtTittle.setText(getString(R.string.inperson_appointment));
        }
    }

    /**
     * on click listner
     */
    private void clickListner() {
        binding.toolbar.backBtn.setOnClickListener(v -> getArgument());
        binding.imgCalender.setOnClickListener(v -> {
            android.app.DatePickerDialog datePicker = new android.app.DatePickerDialog(
                    getActivity(), (view1, year1, month, dayOfMonth1) -> {
                //find the difference between selected date and current date
                try {
                    String dtStart = (1 + month) + "/" + dayOfMonth1 + "/" + year1;
                    Date date = new SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(dtStart);
                    Calendar cal = Calendar.getInstance(Locale.US);
                    cal.setTime(date);
                    long msDiff = cal.getTimeInMillis() - Calendar.getInstance(Locale.US).getTimeInMillis();
                    if (msDiff < 0) {
                        viewModel.getApptDateIncrement().setValue(0);
                    } else if (msDiff >= 0) {
                        long daysDiff = TimeUnit.DAYS.convert(msDiff, TimeUnit.MILLISECONDS);
                        viewModel.getApptDateIncrement().setValue((int) daysDiff + 1);
                        Log.d("daysdiff", String.valueOf(daysDiff));
                    }
                    viewModel.initAppointmentTime(cal);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }, Integer.parseInt(mDayDateModel.getYear()), Integer.parseInt(mDayDateModel.getMonthNumber()) - 1,
                    Integer.parseInt(mDayDateModel.getDateOfWeek()));
            datePicker.getDatePicker().setMinDate(new Date().getTime());
            datePicker.show();

        });

        binding.btnConfirmSlot.setOnClickListener(v -> {
            moveToNext();
        });
    }

    /**
     * move to next screen based on selected payment method
     */
    private void moveToNext() {
        if (!TextUtils.isEmpty(viewModel.getDocInfo().getValue().getApptTime())) {
            viewModel.getPageFrom().setValue(DoctorBaseFrag.FROM_BOOKING);
            if (viewModel.getDocInfo().getValue().getPayingMode() == FREE_PROVIDER) {
              //  ((DoctorBaseFrag) getParentFragment()).openConfirmAppointment();
                showAlertDialog();
            } else if (viewModel.getDocInfo().getValue().getPayingMode() == PAID_BY_VISIT ||
                    viewModel.getDocInfo().getValue().getPayingMode() == FEE_SCHEDULE) {
                if ((viewModel.getDocInfo().getValue().getInitialCharge().equals("0")|| StringUtil.isBlank(viewModel.getDocInfo().getValue().getInitialCharge().trim())) && (viewModel.getDocInfo().getValue().getIncrementalCharge().equals("0")||StringUtil.isBlank(viewModel.getDocInfo().getValue().getIncrementalCharge().trim()))){
                    //((DoctorBaseFrag) getParentFragment()).openConfirmAppointment();
                    showAlertDialog();
                }else {
                    ((DoctorBaseFrag) getParentFragment()).openPaymentFrag();
                }

            } else {
                ((DoctorBaseFrag) getParentFragment()).getPaymentMethod();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.select_any_slot), Toast.LENGTH_LONG).show();
        }
    }
/**
 * Dialogue for quick detail or schedule now.
 */
private void showAlertDialog() {
    /*AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
    builder.setTitle("Quick Detail or Schedule Now?")
            .setMessage("Would you like to add appointment details (reason, notes, documents) before scheduling?")
            .setPositiveButton("Yes", (dialog, which) -> dialog.dismiss())
            .setNeutralButton("Confirm Appointment", (dialog, which) -> dialog.dismiss())
            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

    AlertDialog dialog = builder.create();
    dialog.show();*/

    final Dialog dialog = new Dialog(getActivity());
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    DialogueForConfirmBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
            R.layout.dialogue_for_confirm, null, false);
    dialog.setContentView(binding.getRoot());
    dialog.setCancelable(false);
    dialog.setCanceledOnTouchOutside(false);
    binding.labelAppt.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen._5sdp));
    if(viewModel.getDocInfo().getValue().isIs_reschedule()){
        binding.txtMessage.setText(R.string.add_details_reschedule_dialogue_message);
        binding.btnSchudle.setText(R.string.reschedule_appointment);
        binding.btnConfirm.setText("Modify Details");
    }else {
        binding.txtMessage.setText(R.string.add_details_dialogue_message);
    }



    binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
            // moveToHome();
            dialog.dismiss();
            ((DoctorBaseFrag) getParentFragment()).openConfirmAppointment();
        }
    });
    binding.btnSchudle.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
            Constant.isSchedule = true;
            ((DoctorBaseFrag) getParentFragment()).openConfirmAppointment();
            // moveToHome();
        }
    });
    binding.btnCancel.setOnClickListener(v -> {
                dialog.dismiss();
            }
    );
    dialog.show();
}


    /**
     * get arguements and decide to open which page
     */
    private void getArgument() {
        if (getArguments() != null) {
            boolean openProfilePage = getArguments().getBoolean(AppConstant.KEY_PAGE_TYPE, false);
            {
                if (openProfilePage) {
                    ((DoctorBaseFrag) getParentFragment()).openDocProfile();
                } else {
                    ((DoctorBaseFrag) getParentFragment()).openMainFragment();
                }
            }
        }
    }

    /**
     * observer
     */
    private void registerObserver() {
        viewModel.initAppointmentTime(Calendar.getInstance());
        viewModel.getCalenderDayDate().observe(getActivity(), dayDateModels -> {
            for (DayDateModel dayDateModel : dayDateModels.getCalenderDayDateList()) {
                if (dayDateModel.isSelected()) {
                    mDayDateModel = dayDateModel;
                    viewModel.displayProviderSchedule(mDayDateModel.getDate(), mDayDateModel);
                    break;
                }
            }
        });
        viewModel.getApptBookingList().observe(getActivity(), o -> {
            List[] lists = (List[]) o;
            if (lists[0].size() == 0) {
                binding.unavailableTxt.setVisibility(View.VISIBLE);
            } else {
                binding.unavailableTxt.setVisibility(View.GONE);
            }
            recyclerViewHorizontalAdapter.setMaxApptList(lists[1]);
            recyclerViewHorizontalAdapter.setApptAvailableList(lists[2]);
            recyclerViewHorizontalAdapter.refreshRecyclerView(lists[0]);
            hasApptTimeLoaded = true;
            binding.apptProgressBar.setVisibility(View.GONE);
        });
        viewModel.getPaymentMethod().observe(getActivity(), o -> {
            if (!TextUtils.isEmpty(viewModel.getDocInfo().getValue().getApptTime())) {
                //moveToNext();
            }
        });

    }


    @Override
    public void refreshFragment(boolean isRefresh) {
        super.refreshFragment(isRefresh);

    }

    /**
     * initliaze recycler view of booking list
     */
    private void initBookingList() {
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        binding.appointmentRCView.setLayoutManager(recyclerViewLayoutManager);
        // Adding items to RecyclerView.
        ArrayList<String> apptTime = new ArrayList<>();
        ArrayList<Integer> apptAvail = new ArrayList<>();
        ArrayList<Integer> apptMax = new ArrayList<>();
        recyclerViewHorizontalAdapter = new RecyclerViewAdapter(apptTime, apptAvail, apptMax, getActivity());
        //LinearLayoutManager horizontalLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4, GridLayoutManager.HORIZONTAL, false);
        binding.appointmentRCView.setLayoutManager(layoutManager);
        binding.appointmentRCView.setAdapter(recyclerViewHorizontalAdapter);
        // Adding on item click listener to RecyclerView.
        binding.appointmentRCView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {
                ChildView = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (ChildView != null && gestureDetector.onTouchEvent(motionEvent)) {
                    RecyclerViewItemPosition = Recyclerview.getChildAdapterPosition(ChildView);
                    if (apptAvail.get(RecyclerViewItemPosition) < apptMax.get(RecyclerViewItemPosition)) {
                        Calendar cal = Calendar.getInstance(Locale.US);
                        cal.add(Calendar.DAY_OF_MONTH, viewModel.getApptDateIncrement().getValue());
                        String time = apptTime.get(RecyclerViewItemPosition);
                        String dateTime = mDayDateModel.getDate() + " " + time;
                        checkTimeSlotAvailability(viewModel.getDocInfo().getValue().getOrgCode(),
                                viewModel.getDocInfo().getValue().getProviderCode(), mDayDateModel.getDate(), time, dateTime);
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });


    }

    /**
     * check selected time slot
     */
    private void checkTimeSlotAvailability(final String org_code, final String pro_code,
                                           final String apptdate, final String timeslot, final String dateTime) {
        showProgress();
        OnPostExecute ope = result -> {
            if (!result.toString().contains("-1")) {
                viewModel.getDocInfo().getValue().setWaitingRoom(2);
                viewModel.getDocInfo().getValue().setApptTime(dateTime);
                hideProgress();
            } else {
                hideProgress();
                MyAlertDialog dialog = new MyAlertDialog(getActivity());
                dialog.show();
                dialog.setTitle(getString(R.string.unavailable_date));
                dialog.setDialogContent(getString(R.string.unavailable_time_msg));
                dialog.setRightClickListener(getString(R.string.btn_ok), view -> dialog.dismiss());
            }
        };

        WebService.webServiceAsyncTask(Check_Timeslot_Availablity, ope, org_code, pro_code, "CDOC_ONLINE", apptdate, timeslot);
    }


}
