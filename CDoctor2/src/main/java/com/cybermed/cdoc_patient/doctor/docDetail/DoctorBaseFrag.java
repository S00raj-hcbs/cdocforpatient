package com.cybermed.cdoc_patient.doctor.docDetail;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.common.base.BaseMVVMFragment;
import com.cybermed.cdoc_patient.databinding.FragmentBaseBinding;
import com.cybermed.cdoc_patient.doctor.docDetail.model.BaseResponseModel;
import com.cybermed.cdoc_patient.doctor.searchDoctor.ResponseDocInfo;
import com.cybermed.cdoc_patient.main.HomeFragment;
import com.cybermed.cdoc_patient.util.AppConstant;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.manager.HomeApiManager;

import static com.cybermed.cdoc_patient.util.AppConstant.DOC_INFO;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_PAGE;
import static com.cybermed.cdoc_patient.util.AppConstant.PAGE_APPT;
import static com.cybermed.cdoc_patient.util.AppConstant.PAGE_DOC_LIST;
import static com.cybermed.cdoc_patient.util.AppConstant.PAGE_HOME;
import static com.cybermed.cdoc_patient.util.AppConstant.PAGE_SEARCH;

/**
 * doctor appointment booking module
 */
public class DoctorBaseFrag extends BaseMVVMFragment<DocBookingVm> {
    HomeFragment homeFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction ft;
    FragmentBaseBinding binding;
    public static int FROM_PROFILE = 1;
    public static int FROM_BOOKING = 2;
    public static int FROM_PAYMENT = 9;
    public static final int DOCTOR_FREE = 0;
    public static final int DOCTOR_PAYMENT = 1;
    public static final int Insurance = 2;
    boolean retryOnce = false;
    Context mContext;

    @Override
    protected DocBookingVm createViewModel() {
        return new ViewModelProvider(getActivity()).get(DocBookingVm.class);
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_base;
    }

    @Override
    public void onViewModelCreated(View view, DocBookingVm viewModel) {
        binding = (FragmentBaseBinding) getDataBinding();
        homeFragment = (HomeFragment) getParentFragment();
        fragmentManager = getChildFragmentManager();
        mContext = getActivity();
        docDetail();

    }

    /**
     * get arguements and decide to open which page
     */
    private void getArgument() {
        if (getArguments() != null) {
            boolean openProfilePage = getArguments().getBoolean(AppConstant.KEY_PAGE_TYPE, false);
            {
                if (openProfilePage) {
                    openDocProfile();
                } else {
                    openDocBookingFrag();
                }
            }
        }
    }

    /**
     * open doctor profile page
     */
    public void openDocProfile() {
        DoctorProfileFrag doctorProfileFrag = new DoctorProfileFrag();
        doctorProfileFrag.setArguments(getArguments());
        setFragment(doctorProfileFrag);
    }
    /**
     * open PaymentCreditCardFrag page
     */
   /* public void openPaymentCreditCardFrag() {
        // Step 1: Create a new instance of PaymentCreditCardFrag with arguments
        PaymentCreditCardFrag creditCardFrag = PaymentCreditCardFrag.newInstance(CDoctor2Application.getLoginInfo().getUserInfo());

// Step 2: Set the arguments (if there are additional arguments to set)
        Bundle args = getArguments();
        creditCardFrag.setArguments(args);

// Step 3: Replace or add the fragment using the fragment manager
        setFragment(creditCardFrag);
    }*/

    /**
     * doctor confirm appointment page
     */
    public void openConfirmAppointment() {
        ConfirmAppointmentFragment confirmAppointmentFragment = new ConfirmAppointmentFragment();
        confirmAppointmentFragment.setArguments(getArguments());
        setFragment(confirmAppointmentFragment);
    }

    /**
     * doctor slot booking page
     */
    public void openDocBookingFrag() {
        DocBookingFrag docBookingFrag = new DocBookingFrag();
        docBookingFrag.setArguments(getArguments());
        setFragment(docBookingFrag);
    }

    /**
     * doctor payment page
     */
    public void openPaymentFrag() {
        PaymentFrag paymentFrag = new PaymentFrag();
        setFragment(paymentFrag);
    }

    /**
     * open home page
     */
    public void openMainFragment() {
        homeFragment.openMainActivity();
    }

    /**
     * doctor listing
     *
     * @param fromSeach trus if open doctor search page
     */
    public void openDocList(boolean fromSeach) {
        homeFragment.openDoctorList(fromSeach, true);
    }

    /**
     * appointment page
     */
    public void openApptFrag() {
        homeFragment.openApptFragment("1", true);
    }

    public void setFragment(Fragment fragment) {
        ft = fragmentManager.beginTransaction();
        if (getCurrentFrag() != null && fragment != getCurrentFrag())
            ft.remove(getCurrentFrag());

        ft.replace(R.id.home_container, fragment, fragment.getClass().getSimpleName());
        ft.commit();

    }

    Fragment getCurrentFrag() {
        return fragmentManager.findFragmentById(R.id.home_container);
    }

    @Override
    public void refreshFragment(boolean isRefresh) {
        super.refreshFragment(isRefresh);
        retryOnce = false;
        docDetail();
    }

    /**
     * get doc info
     */
    public void docDetail() {
        showProgress();
        HomeApiManager apiManager = new HomeApiManager(new IResponseReceiver() {
            @Override
            public void onSuccess(Object data) {
                hideProgress();
                if (data != null && ((BaseResponseModel<ResponseDocInfo>) data).getObject() != null) {
                    ResponseDocInfo responseDocInfo = ((BaseResponseModel<ResponseDocInfo>) data).getObject();
                    viewModel.getDocInfo().setValue(responseDocInfo);
                    viewModel.getDocFavourite().setValue(responseDocInfo.getFavDoc());
                    viewModel.initPaidMethod();
                    viewModel.getDocNextAvailList(CDoctor2Application.getLoginInfo().getAccount(), viewModel.getDocInfo().getValue().getProviderCode());
                    viewModel.getDocInfo().getValue().setVideoAppoitnmentType(getArguments().getBoolean(AppConstant.KEY_APPT_TYPE, false));
                    boolean is_reschedule;
                    String apptid;
                    String is_note;
                    String is_reason;

                    if (getArguments() != null) {
                         is_reschedule = getArguments().getBoolean(AppConstant.KEY_IS_RESCHEDULE, false);
                         apptid = getArguments().getString(AppConstant.KEY_APPTID);
                        is_note = getArguments().getString(AppConstant.KEY_IS_CHIEF_NOTES);
                        is_reason = getArguments().getString(AppConstant.KEY_IS_CHIEF_COMPLAIN);
                    }else {
                        is_reschedule = false;
                        apptid = "";
                        is_note = "";
                        is_reason = "";
                    }
                    viewModel.getDocInfo().getValue().setApptId(apptid);
                    viewModel.getDocInfo().getValue().setIs_reschedule(is_reschedule);
                    viewModel.getDocInfo().getValue().setChiefComplaint(is_reason);
                    viewModel.getDocInfo().getValue().setChiefComplaintNote(is_note);
                    getArgument();

                } else {
                    if (!retryOnce) {
                        retryOnce = true;
                        docDetail();
                    } else
                        openMainFragment();
                }

            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                hideProgress();
                if (!retryOnce) {
                    retryOnce = true;
                    docDetail();
                } else
                    openMainFragment();
            }
        }, mContext);
        apiManager.getDoctorDetail(CDoctor2Application.getLoginInfo().getAccount(), getArguments().getString(DOC_INFO));

    }

    public void getPaymentMethod(){
        viewModel.initPaidMethod();
    }

    public void handleBack() {
        Bundle data = getArguments();
        if (data != null) {
            String pageType = data.getString(KEY_PAGE);
            if (!TextUtils.isEmpty(pageType)) {
                if (pageType.equals(PAGE_HOME)) {
                    openMainFragment();
                } else if (pageType.equals(PAGE_SEARCH)) {
                    openDocList(true);
                } else if (pageType.equals(PAGE_DOC_LIST)) {
                    openDocList(false);
                } else if (pageType.equals(PAGE_APPT)) {
                    openApptFrag();
                } else {
                    openMainFragment();
                }
            } else openMainFragment();
        }
    }


}