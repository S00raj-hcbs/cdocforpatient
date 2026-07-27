package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Glucometer.BG5.Measurement;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.WebServiceID;
import com.cdfortis.datainterface.soap.model.PatientRoutineDefaultMessage;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.ButterKnifeFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.OnBackPressedListener;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.ResultData;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IBackPressFrag;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Glucometer.GlucoseDailyRoutine;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.databinding.FragmentBg5ResultBinding;
import com.cybermed.cdoc_patient.util.ErrorMessage;

import org.jetbrains.annotations.NotNull;
import org.ksoap2.serialization.SoapObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.GLUCOMETER_DEVICE_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.CustomizedFragment.STATE.RESULT;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.EventBusMessage.BtUtils.defaultEventBus;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.MEASUREMENT1;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.MEASUREMENT2;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.TIMESTAMP;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph.VALUE;

public class iHealthBG5ResultFragment extends ButterKnifeFragment implements OnBackPressedListener {


    private String measureTime;
    FragmentBg5ResultBinding binding;
    String mData;
    IBackPressFrag iBackPressListner;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bg5_result, container, false);


        ResultData resultData = defaultEventBus().removeStickyEvent(ResultData.class);
        mData = resultData != null ? resultData.getData1() : RESULT.getValue();

        if (binding.txtGlucose != null /*&& mInstruction != null*/) {
            binding.txtGlucose.setText(mData);

            SimpleDateFormat parseFormat = new SimpleDateFormat("MMM dd, hh:mm a");
            Date date = new Date();
            String currDate = parseFormat.format(date);

            binding.glucoseDate.setText(currDate);
        }

        ArrayAdapter<CharSequence> measureTimeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.glucose_measure_time, R.layout.item_gulcometer_routinr);
        measureTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerRelationship.setAdapter(measureTimeAdapter);
//        spinner_relationship.setTe
        binding.spinnerRelationship.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                measureTime = getResources().getStringArray(R.array.glucose_measure_time)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // binding.spinnerRelationship.setOnSpinnerItemClickListener((position, itemAtPosition) -> measureTime = itemAtPosition);

        if (GlucoseDailyRoutine.isSet) {
            binding.spinnerRelationship.setSelection(GlucoseDailyRoutine.getDefaultTime());
        } else {
            final String userId = CDoctor2Application.getLoginInfo().getAccount();
            OnPostExecute ope = result -> {
                PatientRoutineDefaultMessage routine = new PatientRoutineDefaultMessage((SoapObject) result);
                GlucoseDailyRoutine.setRoutine(routine.wakeup_time, routine.breakfast_time, routine.lunch_time, routine.dinner_time, routine.bed_time);
                binding.spinnerRelationship.setSelection(GlucoseDailyRoutine.getDefaultTime());
            };
            WebService.webServiceAsyncTask(WebServiceID.retrieve_patient_routine_default_message, ope, userId);
        }
        binding.btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToMain();
            }
        });
        return binding.getRoot();
    }

    private void goBackToMain() {

        if (measureTime == null || measureTime.isEmpty()) {
            String warning = "Warning";
            String message = "Please Select Your Measure Time";
            ErrorMessage.alertDialog(getContext(), warning, message, null);
            return;
        } else {
            if (RESULT.getIndex().equals("-1") || RESULT.getIndex().isEmpty()) {
                String error = "Error";
                String message = "Internet error, Please Measure Again";
                ErrorMessage.alertDialog(getContext(), error, message, null);
            } else {
                WebService.webServiceAsyncTask(WebServiceID.send_patient_vital_data_message, RESULT.getIndex(), measureTime);
            }
        }
        //int ID = mainID(BluetoothBaseFragment.GLUCOSE_BG5);

        // Navigation.findNavController(getView()).navigate(ID);
        binding.btnFinish.setVisibility(View.GONE);
        binding.btnViewHistory.setVisibility(View.VISIBLE);
        binding.btnViewHistory.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString(VALUE, GLUCOMETER_DEVICE_TYPE);
            args.putString(TIMESTAMP, String.valueOf(System.currentTimeMillis()));
            args.putString(MEASUREMENT1, mData);
            args.putString(MEASUREMENT2, "");
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_iHealthBG5Fragment_to_iHealthIotGraph, args);
        });

    }


    @Override
    public void onBackPressed() {
        goBackToMain();
    }

    public void setBackPressListner(IBackPressFrag iBackPressListner) {
        this.iBackPressListner = iBackPressListner;
    }
}