package com.cybermed.cdoc_patient.doctor.docDetail;

import static android.content.Context.ALARM_SERVICE;
import static com.cdfortis.datainterface.soap.WebService.WSInstance;
import static com.cdfortis.datainterface.soap.WebServiceID.Mark_appointment_status;
import static com.cdfortis.datainterface.soap.WebServiceID.Notify_Patient;
import static com.cdfortis.datainterface.soap.WebServiceID.Notify_Provider_v2;
import static com.cdfortis.datainterface.soap.WebServiceID.create_Call_Log_Rooms_v2;
import static com.cdfortis.datainterface.soap.WebServiceID.create_appointment_on_EMR_Android_v2;
import static com.cdfortis.datainterface.soap.WebServiceID.generate_OnlineRoomNumber;
import static com.cdfortis.datainterface.soap.WebServiceID.getProviderWaitingRoomPatNumber_From_EMR;
import static com.cdfortis.datainterface.soap.WebServiceID.get_Pat_Vitals_v2;
import static com.cdfortis.datainterface.soap.WebServiceID.get_patient_imagelist_V1;
import static com.cdfortis.datainterface.soap.WebServiceID.set_appt_vital_intake_v4;
import static com.cybermed.cdoc_patient.camera.ImageUtils.checkStoragePermission;
import static com.cybermed.cdoc_patient.camera.ImageUtils.imageSelectionPopUp;
import static com.cybermed.cdoc_patient.common.BaseActivity.DOCTOR_FREE;
import static com.cybermed.cdoc_patient.common.BaseActivity.DOCTOR_INSURANCE;
import static com.cybermed.cdoc_patient.common.BaseActivity.DOCTOR_PAYMENT;
import static com.cybermed.cdoc_patient.common.BaseActivity.DOCTOR_PAYPAL;
import static com.cybermed.cdoc_patient.common.BaseActivity.PERMISSION_CAMERA_MIC;
import static com.cybermed.cdoc_patient.main.FragmentMainActivity.MY_CAMERA_AUDIO_REQUEST_CODE;
import static com.cybermed.cdoc_patient.util.AppConstant.APPT_DATE_FORMAT;
import static com.cybermed.cdoc_patient.util.AppConstant.REQUEST_IMAGE_SELECTION;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cdfortis.datainterface.annotation.DataField;
import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.model.Appointment;
import com.cdfortis.datainterface.soap.model.PatientImage;
import com.cdfortis.datainterface.soap.model.SoapObjectVector;
import com.cdfortis.datainterface.soap.model.VitalInfo;
import com.cybermed.cdoc_patient.BuildConfig;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.camera.ImageUtils;
import com.cybermed.cdoc_patient.camera.PhotoRecyclerViewAdapter;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.common.base.BaseMVVMFragment;
import com.cybermed.cdoc_patient.common.videoui.Constant;
import com.cybermed.cdoc_patient.databinding.DialogCancelApptBinding;
import com.cybermed.cdoc_patient.databinding.FragmentConfirmAppointmentBinding;
import com.cybermed.cdoc_patient.doctor.VideoCallActivity;
import com.cybermed.cdoc_patient.doctor.docDetail.model.BaseResponseModel;
import com.cybermed.cdoc_patient.doctor.docDetail.model.ResMakeAppt;
import com.cybermed.cdoc_patient.doctor.doctorFilter.SpinnerAdapter;
import com.cybermed.cdoc_patient.doctor.doctorFilter.SpinnerModel;
import com.cybermed.cdoc_patient.doctor.searchDoctor.CalendarHelper;
import com.cybermed.cdoc_patient.login.LoginInfo;
import com.cybermed.cdoc_patient.login.viewmodel.BaseResponse;
import com.cybermed.cdoc_patient.service.AppointmentNotificationService;
import com.cybermed.cdoc_patient.util.AppConstant;
import com.cybermed.cdoc_patient.util.DateUtil;
import com.cybermed.cdoc_patient.util.ErrorMessage;
import com.cybermed.cdoc_patient.util.LocalizationUtil;
import com.cybermed.cdoc_patient.util.PermissionUtil;
import com.cybermed.cdoc_patient.webapi.APIs.PaymentApi;
import com.cybermed.cdoc_patient.webapi.AuthManager;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.RestApiCall;
import com.cybermed.cdoc_patient.webapi.manager.HomeApiManager;
import com.cybermed.cdoc_patient.webapi.model.request.ApptPayment;
import com.cybermed.cdoc_patient.webapi.model.response.ErrorResponse;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.kobjects.base64.Base64;
import org.ksoap2.serialization.SoapObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * confirm appointment page
 */
public class ConfirmAppointmentFragment extends BaseMVVMFragment<DocBookingVm> {
    FragmentConfirmAppointmentBinding binding;
    VitalInfo PV;
    String roomNumber = "";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(APPT_DATE_FORMAT, Locale.US);
    private final static int WAITING_ROOM = 1;
    private final static int APPOINTMENT = 2;
    private static final int JOIN_WAITING_ROOM = 1;
    private static final String ALARM_TO_REQUEST_CODE = "alarm_to_request_call";
    final LoginInfo loginInfo = CDoctor2Application.getLoginInfo();
    final String userId = loginInfo.getAccount();
    private String org_code;
    private String provider_id;
    private String docName;
    private String apptTime;
    private String userInputBusyProvider;
    private ProgressDialog pd;
    private String card_id;
    private int paymentType;
    private int isWaitingRoom;
    private Hashtable calendarIdTable;
    private Dialog reasonSpinner;
    private SpinnerAdapter adapter;
    String appid, date, time, apptStatusMessage;
    PhotoRecyclerViewAdapter photoRecyclerViewAdapter;
    List<Uri> photoList;
    Context mContext;
    private ProgressDialog progressDialog;
    @Override
    protected DocBookingVm createViewModel() {
        return new ViewModelProvider(requireActivity()).get(DocBookingVm.class);
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_confirm_appointment;
    }

    @Override
    public void onViewModelCreated(View view, DocBookingVm viewModel) {
        binding = (FragmentConfirmAppointmentBinding) getDataBinding();
        mContext = getActivity();
        initSpinners();
        initliazeValues();

        binding.reasonSpinner.setOnClickListener(v -> {
            reasonSpinner.show();
        });
        setUpUpload();
        onClickListeners();
        CalendarHelper.requestCalendarReadWritePermission(requireActivity());
        /*if (viewModel.getDocInfo().getValue().isIs_reschedule()){
            binding.uploadBtn.setVisibility(View.GONE);
        }*/
        if (viewModel.getDocInfo().getValue().isIs_reschedule()){
            if (viewModel.getDocInfo().getValue().isVideoAppoitnmentType()){
                ViewPhotoRequest();
            }else {
                if (Constant.isSchedule){
                    Constant.isSchedule=false;
                    sendInfo();
                }
            }
        }else {


            if (Constant.isSchedule){
                Constant.isSchedule=false;
                sendInfo();
            }
        }
        Log.e("waiting room",""+viewModel.getDocInfo().getValue().getWaitingRoom());

        if (!viewModel.getDocInfo().getValue().isVideoAppoitnmentType()){
            binding.uploadBtn.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * init view image values
     */
    private void ViewPhotoRequest() {
        showProgressDialog();
        OnPostExecute ope = result -> {
            hideProgress();
            if (result.toString().contains("-1")) {
                hideProgressDialog();
                if (Constant.isSchedule){
                    Constant.isSchedule=false;
                    sendInfo();
                }
            } else {
                if (viewModel.getDocInfo().getValue().isIs_reschedule()){
                    binding.uploadBtn.setVisibility(View.GONE);
                }
                Vector<PatientImage> pa = new SoapObjectVector<>(PatientImage.class, (SoapObject) result);

                List<Uri> photos = new ArrayList<>();
                for (PatientImage patientImage : pa) {
                    String encoded = patientImage.image_body;
                    if (encoded != null) {
                        Bitmap bitmap = decodeBase64(encoded);
                        photos.add(bitmapToUri(bitmap, mContext));
                    }
                }
                setPhotoUploadRecycler(photos);
                //hideProgress();
                hideProgressDialog();
                if (Constant.isSchedule){
                    Constant.isSchedule=false;
                    sendInfo();
                }
            }


        };

        WebService.webServiceAsyncTask("Provider",get_patient_imagelist_V1, ope, userId, viewModel.getDocInfo().getValue().getApptId(), "", "");

    }

    /**
     * init model values
     */
    private void initliazeValues() {
        if (viewModel.getDocInfo().getValue() != null) {
            org_code = viewModel.getDocInfo().getValue().getOrgCode();
            provider_id = viewModel.getDocInfo().getValue().getProviderCode();
            docName = viewModel.getDocInfo().getValue().getFirstName() + " " + viewModel.getDocInfo().getValue().getLastName();
            paymentType = viewModel.getDocInfo().getValue().getPaymentType();

            isWaitingRoom = viewModel.getDocInfo().getValue().getWaitingRoom();
            card_id = viewModel.getDocInfo().getValue().getCardId();
            apptTime = viewModel.getDocInfo().getValue().getApptTime();
            if (viewModel.getDocInfo().getValue().isIs_reschedule()){
               // binding.description.setText(TextUtils.isEmpty(viewModel.getDocInfo().getValue().getChiefComplaintNote())?"":viewModel.getDocInfo().getValue().getChiefComplaintNote());
                getChiefComplaintNotes(TextUtils.isEmpty(viewModel.getDocInfo().getValue().getChiefComplaint())?"":viewModel.getDocInfo().getValue().getChiefComplaint());
             //   binding.reasonSpinner.setText(getChiefComplaint());
                binding.description.setText(TextUtils.isEmpty(viewModel.getDocInfo().getValue().getChiefComplaintNote())?binding.description.getText():binding.description.getText()+", "+viewModel.getDocInfo().getValue().getChiefComplaintNote());
            }

            if (!TextUtils.isEmpty(apptTime)) {
                date = DateUtil.formatedDate(apptTime, APPT_DATE_FORMAT, AppConstant.DATE_FORMAT);
                time = DateUtil.formatedDate(apptTime, APPT_DATE_FORMAT, "hh:mm a");
                binding.appointmentDate.setText(date);
                binding.apptTime.setText(time);
            } else {
                date = new SimpleDateFormat(AppConstant.DATE_FORMAT, Locale.US).format(Calendar.getInstance().getTime());
                time = new SimpleDateFormat("hh:mm a", Locale.US).format(Calendar.getInstance().getTime());
                binding.appointmentDate.setText(date);
                binding.apptTime.setText(time);
            }
        }
    }

    /**
     * click Listeners
     */
    private void onClickListeners() {
        binding.toolbar.txtTittle.setText(getString(R.string.previsit_information));
        binding.btnWaiting.setOnClickListener(view -> {
            showProviderBusyDialog();
        });
        binding.btnAppointment.setOnClickListener(view -> {
            sendInfo();
        });
        binding.btnConsult.setOnClickListener(view -> {
            PermissionUtil.checkCameraAudioPermission(getActivity(), this::sendInfo);
        });
        binding.toolbar.backBtn.setOnClickListener(v -> {
            viewModel.resetValues();
            if (viewModel.getPageFrom().getValue() == DoctorBaseFrag.FROM_PROFILE) {
                ((DoctorBaseFrag) getParentFragment()).openDocProfile();
            } else if (viewModel.getPageFrom().getValue() == DoctorBaseFrag.FROM_PAYMENT) {
                ((DoctorBaseFrag) getParentFragment()).openPaymentFrag();
            } else {
                ((DoctorBaseFrag) getParentFragment()).openDocBookingFrag();
            }
        });
    }

    /**
     * set photo upload
     */
    private void setUpUpload() {
        setPhotoAdapter();
        if (viewModel.getDocInfo().getValue().isIs_reschedule()){
            binding.uploadBtn.setVisibility(View.GONE);
        }else {
            binding.uploadBtn.setVisibility(View.VISIBLE);
        }

        if (!viewModel.getDocInfo().getValue().isVideoAppoitnmentType()){
            binding.uploadBtn.setVisibility(View.INVISIBLE);
        }
        binding.uploadBtn.setOnClickListener(v -> {
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()){
                    imageSelectionPopUp(getActivity(), null, REQUEST_IMAGE_SELECTION, null);
                }else{
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }else {

            }*/
            Runnable success = () -> imageSelectionPopUp(getActivity(), null, REQUEST_IMAGE_SELECTION, null);
            Runnable failure = () -> {
            };
            checkStoragePermission(getActivity(), success, failure);
        });
    }


    private void initSpinners() {
        // Chief Complaints drop down elements
        List<SpinnerModel> complaints = new ArrayList<>();
        String[] chiefComplaintArray = getResources().getStringArray(R.array.chiefComplaints);
        for (String complaint : chiefComplaintArray)
            complaints.add(new SpinnerModel(complaint, false));
        // complaints.remove(0);
        reasonSpinner = getDialog(complaints);
    }

    /**
     * @param ListModel reason of appt. model
     * @return dialog
     */
    private Dialog getDialog(List<SpinnerModel> ListModel) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(requireContext());
        View mView = layoutInflaterAndroid.inflate(R.layout.custom_spinner_drop_down_list_view, null);
        RecyclerView reasons = mView.findViewById(R.id.listItems);
        TextView title = mView.findViewById(R.id.title);
        title.setText(getString(R.string.select_reason));

        mView.findViewById(R.id.closeBtn).setOnClickListener(v -> reasonSpinner.dismiss());

        mView.findViewById(R.id.okayBtn).setOnClickListener(v -> {
            binding.reasonSpinner.setText(getChiefComplaint());
            reasonSpinner.dismiss();
        });

        adapter = new SpinnerAdapter(ListModel, SpinnerAdapter.Source.Appointment);
        reasons.setAdapter(adapter);
        reasons.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        reasons.setHasFixedSize(true);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        alertDialogBuilder.setView(mView);
        return alertDialogBuilder.create();
    }


    private void book_appointment() {
        create_appointment(true);
    }

    /**
     * @param fromAppt is from appt.
     */
    private void create_appointment(boolean fromAppt) {
        if (fromAppt) {
            HomeApiManager apiManager = new HomeApiManager(new IResponseReceiver() {
                @Override
                public void onSuccess(Object data) {
                    if (data != null && ((BaseResponseModel<ResMakeAppt>) data).getObject() != null) {
                        ResMakeAppt resMakeAppt = ((BaseResponseModel<ResMakeAppt>) data).getObject();
                        appid = resMakeAppt.getApptId();
                        bookAppt(fromAppt);
                    } else {
                        if (pd != null) {
                            pd.cancel();
                        }
                        apptStatusMessage = "Your appointment has been scheduled for clinic visit.";
                        showApptSuccessDialog();
                        //TODO move to success appt dialog
                    }

                }

                @Override
                public void onFailure(@NonNull String errorResponse) {
                    if (pd != null) {
                        pd.cancel();
                    }

                    try {
                        JSONObject jsonObject = new JSONObject(errorResponse);
                        String error = jsonObject.get("error").toString();
                        if (!TextUtils.isEmpty(error)) {
                            ErrorMessage.alertDialog(requireContext(), null,
                                    error, null);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, mContext);
            ResMakeAppt resMakeAppt = new ResMakeAppt();
            resMakeAppt.setApptDate(apptTime);
            resMakeAppt.setApptId(viewModel.getDocInfo().getValue().getApptId());
            resMakeAppt.setIs_reschedule(viewModel.getDocInfo().getValue().isIs_reschedule());
            resMakeAppt.setProviderCode(provider_id);
            resMakeAppt.setReason(getChiefComplaint());
            if (viewModel.getDocInfo().getValue().isVideoAppoitnmentType()) {
                apiManager.makeVideoCallAppointment(CDoctor2Application.getLoginInfo().getAccount(), resMakeAppt);
            } else {
                apiManager.makeClinicAppointment(CDoctor2Application.getLoginInfo().getAccount(), resMakeAppt);
            }
        } else {
            bookAppt(fromAppt);
        }

    }

    /**
     * book appt.
     *
     * @param fromAppt is from appt.
     */
    private void bookAppt(boolean fromAppt) {
        Object o = Single.fromCallable(() -> {
            roomNumber = WSInstance().RxCallingWebservice(generate_OnlineRoomNumber).toString();
            if (fromAppt) {
                if (appid == null) {
                    throw new Exception("book_appointment");
                } else {
                   // sendPhotos(appid);
                    String result = WSInstance().RxCallingWebservice(create_Call_Log_Rooms_v2, roomNumber, appid, provider_id, userId, org_code).toString();
                    if (!result.equals("1")) {
                        throw new Exception("create_Call_Log_Rooms_v2 failed");
                    }
                    return result;
                }

            } else {
                Object so = WSInstance().RxCallingWebservice(create_appointment_on_EMR_Android_v2, roomNumber,
                        userId, org_code, provider_id, apptTime);
                appid = new Appointment((SoapObject) so).appt_id;
                if (this.appid == null) {
                    throw new Exception("book_appointment");
                } else {
                  //  sendPhotos(appid);
                    String result = WSInstance().RxCallingWebservice(create_Call_Log_Rooms_v2, roomNumber, this.appid, provider_id, userId, org_code).toString();
                    if (!result.equals("1")) {
                        throw new Exception("create_Call_Log_Rooms_v2 failed");
                    }
                    return result;
                }
            }

        })
                .subscribeOn(Schedulers.io())
                .flatMapObservable(result -> {
                    List<Observable<Boolean>> observables = new ArrayList<>();

                    if (!isVisitReasonSkipped()) {
                        /*The reason why it is not (!) contains -1 is because the return value is inconsistent,
                         * some 1 and some anyType{1}*/
                        Observable<Boolean> observable = Observable.fromCallable(() ->
                                !WSInstance().RxCallingWebservice(set_appt_vital_intake_v4, org_code, this.appid, getChiefComplaint(), PV.temperature, PV.pulse
                                        , PV.bph, PV.bpl, PV.height, PV.weight, "", PV.MedHx, PV.SocialHx, PV.allergies, "", "", "").toString()
                                        .contains("-1"))
                                .subscribeOn(Schedulers.io());
                        observables.add(observable);
                    }

                    if (paymentType == 1) {
                        Observable<Boolean> observable = Observable.fromCallable(this::setApptPaymentCard)
                                .subscribeOn(Schedulers.io());
                        observables.add(observable);
                    }

                    String push_msg = loginInfo.getUserInfo().getFirstName() + " "
                            + loginInfo.getUserInfo().getLastname()
                            + " (id: " + userId + ")"
                            + ((isWaitingRoom == JOIN_WAITING_ROOM) ? " was not able to reach you and entered your CDOC waiting room.\n\n"
                            : " has scheduled an appointment with you at " + apptTime + "\n\n")
                            + (isVisitReasonSkipped() ? "" : "Reason for Appointment:\n" + getChiefComplaint() + "\n\n")
                            + ((userInputBusyProvider == null || (userInputBusyProvider.equals(""))) ? "" : "Patient Message: " + userInputBusyProvider);

                    String message_provider = "This message is from CDOC. " + push_msg;

                    observables.add(Observable.fromCallable(() ->
                            !WSInstance().RxCallingWebservice(Notify_Provider_v2, org_code, provider_id, message_provider, push_msg).toString()
                                    .contains("-1")).subscribeOn(Schedulers.io()));


                    String message_patient = "Hello " + loginInfo.getUserInfo().getFirstName() + ",\n\n" + "Please review your appointment details here.\n\n"
                            + "Provider: " + docName + "\n" + "Date and Time: " + apptTime + ".\n\n"
                            + "Please login with the CDoc app before the specified date and time to speak with the provider.\n\n"
                            + "If you have any questions, please feel free to contact us at 732-800-0020.";

                    observables.add(Observable.fromCallable(() ->
                            !WSInstance().RxCallingWebservice(Notify_Patient, userId, message_patient).toString().contains("-1"))
                            .subscribeOn(Schedulers.io()));

                    observables.add(Observable.fromCallable(() ->
                            !WSInstance().RxCallingWebservice(Mark_appointment_status, org_code, roomNumber, (isWaitingRoom == JOIN_WAITING_ROOM) ? "6" : "0").toString().contains("-1"))
                            .subscribeOn(Schedulers.io()));

                    return Observable.merge(observables);

                })
                .contains(Boolean.FALSE)
                .map(fail -> {
                    if (fail) {
                        throw new Exception("Failed to make an appointment");
                    } else { //SUCCESS
                        String waitingRoomCount = WSInstance().RxCallingWebservice(getProviderWaitingRoomPatNumber_From_EMR, "1", this.appid, org_code, provider_id).toString();
                        String waitingRoomMsg = getString(R.string.video_in_waiting_room);

                        if (!waitingRoomCount.equals("-1")) {
                            waitingRoomMsg = getString(R.string.video_in_waiting_room_with_other_pat, Integer.valueOf(waitingRoomCount));
                        }
                        return waitingRoomMsg;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                            //success;
                            if (pd != null) {
                                pd.cancel();
                            }
                            apptStatusMessage = message;
                            sendPhotos(appid);
                            //Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                            showApptSuccessDialog();
                            setCalender();
                        },
                        error -> {
                            if (pd != null) {
                                pd.cancel();
                            }
                            if (error.getMessage().contains("book_appointment")) {
                                ErrorMessage.alertDialog(requireContext(), requireContext().getString(R.string.duplicate_appointment_title), requireContext().getString(R.string.duplicate_appointment_content),
                                        () -> {
                                            //moveToHome();
                                            //requireActivity().setResult(APPT_DUP_RETURN);
                                        });
                            } else {
                                // moveToHome();
                                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
    }


    private void showProviderBusyDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(requireContext());
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_provider_busy, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(requireContext());
        alertDialogBuilderUserInput.setView(mView);

        final EditText userInputDialog = mView.findViewById(R.id.userInputDialog);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setNegativeButton(getString(R.string.btn_no),
                        (dialogBox, id) -> dialogBox.cancel())
                .setPositiveButton(getString(R.string.btn_yes), (dialogBox, id) -> {
                    isWaitingRoom = WAITING_ROOM;
                    userInputBusyProvider = userInputDialog.getText().toString();
                    sendInfo();
                });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }


    private boolean isVisitReasonSkipped() {
        String chief_complaint = getChiefComplaint();
        if (!TextUtils.isEmpty(getChiefComplaint()) || !chief_complaint.equals("")) {
            return false;
        }

        return true;
    }

    public void sendInfo() {
        pd = new ProgressDialog(requireContext());
        pd.setMessage(getString(R.string.doclist_loading));
        pd.show();

        String chief_complaint = getChiefComplaint();

        //Due to a webservice (create_appointment_on_EMR_v2) bug, empty string will not populate the record table
        if (apptTime.equals("")) {
            SimpleDateFormat sdf = new SimpleDateFormat(APPT_DATE_FORMAT, Locale.US);
            apptTime = sdf.format(new Date());
        }

        Object o = Completable.fromCallable(() -> {
            Object so2 = WSInstance().RxCallingWebservice(get_Pat_Vitals_v2, userId);
            PV = new VitalInfo((SoapObject) so2);
            if (PV.height != null && PV.height.equalsIgnoreCase("0")) {
                PV.height = "";
            }
            eliminateNullFromPV(PV);
            return "";
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    if (paymentType == DOCTOR_FREE) {
                        if (isWaitingRoom == WAITING_ROOM) {
                            join_waitingRoom();
                            //requireActivity().setResult(APPT_VITAL_INTAKE_WAITING_RETURN);
                        } else if (isWaitingRoom == APPOINTMENT) {
                            book_appointment();
                            setAlarmForAppointment();
                            //requireActivity().setResult(APPT_VITAL_INTAKE_APPT_RETURN);
                        } else {
                            getProviderOnlineStatus();
                        }

                    } else if (paymentType == DOCTOR_PAYMENT) {
                        if (isWaitingRoom == WAITING_ROOM) {
                            join_waitingRoom();
                            //requireActivity().setResult(APPT_VITAL_INTAKE_WAITING_RETURN);
                        } else if (isWaitingRoom == APPOINTMENT) {
                            book_appointment();
                            setAlarmForAppointment();
                            //requireActivity().setResult(APPT_VITAL_INTAKE_APPT_RETURN);
                        } else {
                            getProviderOnlineStatus();
                        }
                    } else if (paymentType == DOCTOR_PAYPAL || paymentType == DOCTOR_INSURANCE) {
                        if (isWaitingRoom == WAITING_ROOM) {
                            //requireActivity().setResult(APPT_VITAL_INTAKE_WAITING_RETURN);
                        } else if (isWaitingRoom == APPOINTMENT) {
                            book_appointment();
                            setAlarmForAppointment();
                            //requireActivity().setResult(APPT_VITAL_INTAKE_APPT_RETURN);
                        } else {
                            getProviderOnlineStatus();
                        }
                    }
                });
    }


    private void join_waitingRoom() {
        create_appointment(false);
    }

    /**
     * get provider online status
     */
    private void getProviderOnlineStatus() {
        viewModel.getApiResponse().observe(this, liveAction -> {
            switch (liveAction.getLiveActionEvent()) {
                case PROVIDER_ONLINE_STATUS:
                    BaseResponse response = (BaseResponse) liveAction.getLiveActionValue();
                    if (response.getIntegerVal() == 0 || response.getIntegerVal() == 2) {
                        if (isWaitingRoom != WAITING_ROOM) {
                            if (isVisitReasonSkipped()) {
                                if (pd != null) {
                                    pd.cancel();
                                }
                                showProviderBusyDialog();
                            }
                        }
                    } else if (response.getIntegerVal() == 1) {
                        if (pd != null) {
                            pd.cancel();
                        }
                        startVideoConsult();
                    }
                    break;
            }
        });
        viewModel.getProviderOnlineStatus();
    }


    private void startVideoConsult() {
        String weight, totalHeight, bPH, bPL, temperature, pulse, chief_complaint, allergies, smokeStatus, socialHx, medHx, phone_num, userInputBusyProviderTemp;
        weight = PV.weight;
        totalHeight = PV.height;
        bPH = PV.bph;
        bPL = PV.bpl;
        pulse = PV.pulse;
        temperature = PV.temperature;
        chief_complaint = getChiefComplaint();
        allergies = PV.allergies;
        smokeStatus = PV.smoke_status_code;
        medHx = PV.MedHx;
        socialHx = PV.SocialHx;
        phone_num = CDoctor2Application.getLoginInfo().getUserInfo().getPhoneNum();
        userInputBusyProviderTemp = userInputBusyProvider;

        Intent intent = new Intent(requireContext(), VideoCallActivity.class);
        intent.putExtra("orgCode", org_code);
        intent.putExtra("providerId", provider_id);
        intent.putExtra("docName", docName);

        intent.putExtra("isskipped", isVisitReasonSkipped());
        intent.putExtra("weight", weight);
        intent.putExtra("total_height", totalHeight);
        intent.putExtra("bPH", bPH);
        intent.putExtra("bPL", bPL);
        intent.putExtra("pulse", pulse);
        intent.putExtra("temperature", temperature);
        intent.putExtra("chief_complaint", chief_complaint);
        intent.putExtra("allergies", allergies);
        intent.putExtra("smokestatus", smokeStatus);
        intent.putExtra("medHx", medHx);
        intent.putExtra("socialHx", socialHx);
        intent.putExtra("phone_num", phone_num);
        intent.putExtra("providerBusyMessage", userInputBusyProviderTemp);
        intent.putExtra("type", 1);
//        intent.putExtra("cc_idx", cc_idx);
//        intent.putExtra("cvv_code", cvv_code);
        intent.putExtra("card_id", card_id);
        intent.putExtra("apptTime", apptTime);

        if (paymentType == DOCTOR_FREE) {
            intent.putExtra("paymentType", DOCTOR_FREE);
            startActivityForResult(intent, 1112);

        } else if (paymentType == DOCTOR_PAYMENT) {
            intent.putExtra("paymentType", DOCTOR_PAYMENT);
            intent.putExtra("card_id", card_id);
            intent.putExtra("waitroom", isWaitingRoom);
            startActivityForResult(intent, 170);
        }
        moveToHome();
    }


    private void eliminateNullFromPV(VitalInfo PV) {
        try {
            /////////////
            for (Field field : PV.getClass().getDeclaredFields()) {
                if (field.getAnnotation(DataField.class) != null && field.get(PV) != null && field.get(PV).toString().equals("NULL")) {
                    field.set(PV, "");
                }
            }
            ////////////
        } catch (Exception e) {

        }
    }

    /**
     * @return reason of appt.
     */
    private String getChiefComplaint() {
        String chief_complaint = "";
        List<Integer> spinnerPos = adapter.getSelected();
        String[] chiefComplaintArray = LocalizationUtil.getLocalizedResources(requireContext(), Locale.US).getStringArray(R.array.chiefComplaints);

        String chiefComplaintReason = "";
        for (Integer pos : spinnerPos) {
            chiefComplaintReason += chiefComplaintArray[pos];
            chiefComplaintReason += ", ";
        }
        if (!chiefComplaintReason.isEmpty()) {
            chiefComplaintReason = chiefComplaintReason.substring(0, chiefComplaintReason.length() - 2);
        }
        String chiefComplainOthers = binding.description.getText().toString().trim();

        //Combining chief complaint free text and drop down list
        if (!TextUtils.isEmpty(chiefComplaintReason) && !TextUtils.isEmpty(chiefComplainOthers)) {
            chief_complaint = chiefComplaintReason + " , others: " + chiefComplainOthers;
        } else if (!TextUtils.isEmpty(chiefComplaintReason)) {
            chief_complaint = chiefComplaintReason;
        } else if (!TextUtils.isEmpty(chiefComplainOthers)) {
            chief_complaint = "others: " + chiefComplainOthers;
        }

        return chief_complaint;
    }/**
     * @return reason of appt.
     */
    private void getChiefComplaintNotes(String chiefComplaint) {
        if (chiefComplaint == null || chiefComplaint.isEmpty()) {
            return;
        }

        List<Integer> selectedPositions = new ArrayList<>();
        String[] chiefComplaintArray = LocalizationUtil.getLocalizedResources(requireContext(), Locale.US).getStringArray(R.array.chiefComplaints);
        String chiefComplaintReason="";
        String chief_complaints = "";
        String chief_complaints_reason = "";
        String chiefComplainOthers = "";
        if (chiefComplaint.contains("others:")){
            String[] complaintParts = chiefComplaint.split(" , others: ");
             chiefComplaintReason = complaintParts[0];
             chiefComplainOthers = complaintParts.length > 1 ? complaintParts[1] : "";
        }else {
            chiefComplaintReason=chiefComplaint;
            chiefComplainOthers="";
        }


        // Split the chief complaint reasons by comma and trim spaces
        String[] reasons = chiefComplaintReason.split(", ");
        for (String reason : reasons) {
            boolean found = false;
            for (int i = 0; i < chiefComplaintArray.length; i++) {
                if (reason.trim().equals(chiefComplaintArray[i])) {
                    selectedPositions.add(i);
                    if (chief_complaints_reason.isEmpty()) {
                        chief_complaints_reason = reason.trim();
                    } else {
                        chief_complaints_reason += ", " + reason.trim();
                    }
                      found = true;
                    break;
                }
            }
            if (!found) {
                if (chief_complaints.isEmpty()) {
                    chief_complaints = reason.trim();
                } else {
                    chief_complaints += ", " + reason.trim();
                }
            }
        }
        // Update spinner selections
        if (adapter != null) {
            adapter.setSelected(selectedPositions);
        } else {
            System.out.println("Adapter is null!");
        }


        // Set the "others" text
        if (!chief_complaints.isEmpty() && !chiefComplainOthers.isEmpty()){
            binding.description.setText(chiefComplainOthers);
            binding.description.setText(chief_complaints + ", " + chiefComplainOthers);
        }
        else if (chief_complaints.isEmpty() && !chiefComplainOthers.isEmpty()){
            binding.description.setText(chiefComplainOthers);
        }
        else if (!chief_complaints.isEmpty() && chiefComplainOthers.isEmpty()){
            binding.description.setText(chief_complaints);
        }else {
            binding.description.setText("");
        }
        binding.reasonSpinner.setText(chief_complaints_reason);
    }


    private boolean setApptPaymentCard() {
        if (AuthManager.getOrCheckTokenSync()) {
            PaymentApi paymentApi = RestApiCall.getApiService(PaymentApi.class);

            ApptPayment payment = new ApptPayment(org_code, appid, card_id);
            Call<Void> setApptPaymentCardCall = paymentApi.setApptPaymentCard(payment);

            try {
                Response<Void> response = setApptPaymentCardCall.execute();
                if (response.isSuccessful()) {
                    return true;
                } else {
                    if (response.code() == 500) {
                        ErrorMessage.alertDialog(requireContext(), "Server Error", "Error happened on server side", null);
                        return false;
                    }
                    String errorBody = response.errorBody().string();
                    ErrorResponse error = new Gson().fromJson(errorBody, ErrorResponse.class);
                    ErrorMessage.alertDialog(requireContext(), "Error", error.getError(), null);
                    return false;
                }
            } catch (IOException e) {
                ErrorMessage.alertDialog(requireContext(), "Server Error", "Cannot connect to the server", null);
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_CAMERA_MIC || requestCode == MY_CAMERA_AUDIO_REQUEST_CODE) {
            if (grantResults.length > 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //toastShortInfo("Permission enabled, please call again");
                ErrorMessage.alertDialog(requireContext(), null, getString(R.string.permission_enabled), null);
            } else {
                ErrorMessage.alertDialog(requireContext(), null, getString(R.string.please_enable_permission), null);
                //Toast.makeText(getContext(), "Please enable the permissions", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * move to redirected page after success
     */
    private void moveToHome() {
        ((DoctorBaseFrag) getParentFragment()).handleBack();
    }

    /**
     * appt dialog success
     */
    void showApptSuccessDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DialogCancelApptBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_cancel_appt, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        binding.labelAppt.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                ContextCompat.getDrawable(getActivity(), R.drawable.ic_success_icon), null, null);
        binding.labelAppt.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen._5sdp));
        if (viewModel.getDocInfo().getValue().isVideoAppoitnmentType()) {
            binding.labelAppt.setText(getString(R.string.appt_sucees2));
            binding.txtMessage.setText(getString(R.string.cancel_appt_booked));
            binding.txtStatus.setText("\n");
        } else {
            binding.labelAppt.setText(getString(R.string.clinic_appt2));
            binding.txtMessage.setText(getString(R.string.cancel_appt_booked));
            binding.txtStatus.setText("\n");
        }
        binding.txtdate.setText(date + "," + time);
        binding.txtDoctName.setText(docName);
        binding.txtStatus.setVisibility(View.VISIBLE);


        binding.btnConfirm.setText(getString(R.string.done));
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ((DoctorBaseFrag) getParentFragment()).openApptFrag();
               // moveToHome();
            }
        });
        binding.imgCancel.setOnClickListener(v -> {
                    dialog.dismiss();
                    moveToHome();
                }
        );
        dialog.show();
    }

    //**************************************** add calender event && alarm *************************************************

    /**
     * set calender
     */
    private void setCalender() {
        if (CalendarHelper.haveCalendarReadWritePermissions(requireActivity())) {
            calendarIdTable = CalendarHelper.listCalendarId(requireActivity());
            CalendarHelper.updateCalendarIdSpinner(calendarIdTable);
            addNewEvent();
        }
    }

    /**
     * add appt. to google calender
     */
    private void addNewEvent() {
        String description = getString(R.string.have_an_appt) + docName + getString(R.string.open_app);
        CalendarHelper.addNewEvent(calendarIdTable, requireActivity(),
                viewModel.getDocInfo().getValue().isVideoAppoitnmentType()?getString(R.string.upcoming_video_reminder):getString(R.string.upcoming_clinic_reminder), description, apptTime, System.currentTimeMillis(),
                new CalendarHelper.ICalenderSuccess() {
                    @Override
                    public void eventIdSuccess(int calenderid) {

                    }
                    @Override
                    public void calenderFailure() {

                    }
                });
    }

    /**
     * set alarm for appointment
     */
    private void setAlarmForAppointment() {
        int alarm_requestCode = PreferenceManager.getDefaultSharedPreferences(requireContext()).getInt(ALARM_TO_REQUEST_CODE, 0);
        PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().putInt(ALARM_TO_REQUEST_CODE, alarm_requestCode + 1).apply();
        Intent myIntent = new Intent(requireContext(), AppointmentNotificationService.class);
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(requireContext(), alarm_requestCode, myIntent, PendingIntent.FLAG_IMMUTABLE);

        try {
            Calendar calendar = Calendar.getInstance();
            Date date = sdf.parse(apptTime);
            calendar.setTime(date);
            calendar.set(Calendar.MINUTE, -15);
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //*****************************************Document upload*******************************************************************
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_SELECTION) {
            if (data != null) {
                List<Uri> photoUri = data.getExtras().getParcelableArrayList("key_upload_list");
                setPhotoUploadRecycler(photoUri);
            }
        }

    }

    /**
     * set photo recycler view
     *
     * @param photos photo list
     */
    void setPhotoUploadRecycler(List<Uri> photos) {
        if (photos != null && photos.size() > 0) {
            binding.recyclerViewUpload.setVisibility(View.VISIBLE);
            binding.uploadBtn.setVisibility(View.GONE);
        } else {
            binding.recyclerViewUpload.setVisibility(View.GONE);
            binding.uploadBtn.setVisibility(View.VISIBLE);
        }
        if (viewModel.getDocInfo().getValue().isIs_reschedule()){
            binding.uploadBtn.setVisibility(View.GONE);
            if (photos != null && photos.size() > 0) {
                photoRecyclerViewAdapter.setLastItemHidden(true);
            }
        }
        Log.e("photos",photos.toString());
        photoRecyclerViewAdapter.setPhoto(photos);
        photoList = photos;
        photoRecyclerViewAdapter.setListner(() -> imageSelectionPopUp(getActivity(), null, REQUEST_IMAGE_SELECTION, photoList));
    }

    /**
     * set photo adapter
     */
    void setPhotoAdapter() {
        binding.recyclerViewUpload.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        photoRecyclerViewAdapter = new PhotoRecyclerViewAdapter(getActivity(), (List<Uri> photos) -> {
            photoList = photos;
        }, null);
        binding.recyclerViewUpload.setAdapter(photoRecyclerViewAdapter);
        photoRecyclerViewAdapter.setPhotoAdapterCallback(() -> {
            binding.recyclerViewUpload.setVisibility(View.GONE);
            binding.uploadBtn.setVisibility(View.VISIBLE);
        });
    }

    /**
     * send photos
     *
     * @param appId app id
     */
    void sendPhotos(String appId) {
        List<Uri> photos = photoRecyclerViewAdapter.getPhotos();
        if (photos != null && photos.size() > 0) {
            ImageUtils.sendImageRxMultipleSoap(
                    getActivity(), photos, appId);
        }

    }
    /**
     * bitmap to uri convert photos
     *
     *
     */

    public Uri bitmapToUri(Bitmap bitmap, Context context) {
        try {
            // Step 1: Save the bitmap to a file
            File file = saveBitmapToFile(bitmap, context);

            // Step 2: Get a Uri for the file
            return getUriForFile(file, context);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * download image to uri convert photos
     */
    public File saveBitmapToFile(Bitmap bitmap, Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save the bitmap to the file
        FileOutputStream out = new FileOutputStream(image);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();

        return image;
    }
    private Bitmap decodeBase64(String encoded) {
        byte[] decodedString = Base64.decode(encoded);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return bitmap;
    }

    public Uri getUriForFile(File file, Context context) {
        return FileProvider.getUriForFile(context,  BuildConfig.APPLICATION_ID +".fileprovider", file);
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Downloading images...");
            progressDialog.setCancelable(false); // Set to true if you want to allow user to cancel
        }
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}