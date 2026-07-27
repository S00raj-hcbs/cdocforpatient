package com.cybermed.cdoc_patient.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Stream;
import com.cdfortis.datainterface.data.MostRecentMonitorData;
import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.WebServiceID;
import com.cdfortis.datainterface.soap.model.IoT_Device;
import com.cdfortis.datainterface.soap.model.Monitor_BO;
import com.cdfortis.datainterface.soap.model.Monitor_BP;
import com.cdfortis.datainterface.soap.model.Monitor_Glucose;
import com.cdfortis.datainterface.soap.model.Monitor_HR;
import com.cdfortis.datainterface.soap.model.Monitor_STEMO;
import com.cdfortis.datainterface.soap.model.Monitor_Weight;
import com.cdfortis.datainterface.soap.model.PatientRoutineDefaultMessage;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.GlucoseRoutineAlertDialog;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Blood_pressure.BPInfoFragment;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealth_Glucometer.GlucoseDailyRoutine;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.databinding.AdapterIotDeviceBinding;
import com.cybermed.cdoc_patient.databinding.GraphHistoryBinding;
import com.cybermed.cdoc_patient.util.AppUtiltiy;

import org.ksoap2.serialization.SoapObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.BP;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.BP_DEVICE_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.GLUCOMETER_DEVICE_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.IHEALTH_MAC_ADDR;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.OXIMETER;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.PO_DEVICE_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.SCALE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.SCALE_DEVICE_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.STEMOSCOPE_NAME;
import static com.cybermed.cdoc_patient.util.AppConstant.APPLE_HEALTH;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_MAX_BP;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_MIN_BP;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_MAC;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_WATCH;

class LastMeasurement {
    String measurement;
    String measurement_time;
    String device_type;


    LastMeasurement(String device_type, String measurement_time, String measurement) {
        this.measurement = measurement;
        this.measurement_time = measurement_time;
        this.device_type = device_type;
    }
}

public class IOTSettingAdapter extends RecyclerView.Adapter<IOTSettingAdapter.MyViewHolder> {


    private Vector<IoT_Device> ioT_deviceVector;
    private Context context;
    private LastMeasurement[] lastMeasurements = new LastMeasurement[5];
    String maxBpLevel;
    String minBpLevel;
    Monitor_Glucose glucose;
    Monitor_Weight weight;
    Monitor_BP bp;
    Monitor_BO bo;
    Monitor_HR hr;
    Monitor_STEMO stemo;
    AppCompatActivity activity;

    IClickListner iClickListner;
    private FragmentManager fragmentManager;
    Vector<Monitor_HR> monitor_hrVector;
    Vector<Monitor_BO> monitor_boVector;

    public void updateData(MostRecentMonitorData mrd) {
         monitor_hrVector = mrd.getMonitor_hrVector();
        Vector<Monitor_Weight> monitor_weightVector = mrd.getMonitor_weightVector();
         monitor_boVector = mrd.getMonitor_boVector();
        Vector<Monitor_BP> monitor_bpVector = mrd.getMonitor_bpVector();
        Vector<Monitor_Glucose> monitor_glucoseVector = mrd.getMonitor_glucoseVector();
        Vector<Monitor_STEMO> monitor_stemos = mrd.getMonitor_STEMOVector();

        if (monitor_glucoseVector.size() != 0) {
            glucose = monitor_glucoseVector.get(monitor_glucoseVector.size() - 1);
            lastMeasurements[GLUCOSE_INDEX] = new LastMeasurement(context.getResources().getString(R.string.iot_glucometer)
                    , dateFormatter(glucose.Glucose_timestamp), glucose.Glucose + " mg/dL");
        }

        if (monitor_weightVector.size() != 0) {
            weight = monitor_weightVector.get(monitor_weightVector.size() - 1);
            lastMeasurements[SCALE_INDEX] = new LastMeasurement(context.getResources().getString(R.string.iot_scale), dateFormatter(weight.weight_timestamp)
                    , weight.weight + " lb(s)");
        }

        if (monitor_bpVector.size() != 0) {
            bp = monitor_bpVector.get(monitor_bpVector.size() - 1);
            lastMeasurements[BP_INDEX] = new LastMeasurement(context.getResources().getString(R.string.iot_blood_pressure), dateFormatter(bp.BP_timestamp)
                    , String.format("%s / %s mmHg", bp.BPH, bp.BPL));
        }

        if (monitor_boVector.size() != 0) {
            bo = monitor_boVector.get(monitor_boVector.size() - 1);
            if (monitor_hrVector.size() != 0) {
                hr = monitor_hrVector.get(monitor_hrVector.size() - 1);
                lastMeasurements[OXIMETER_INDEX] = new LastMeasurement(context.getResources().getString(R.string.iot_pulse_oximeter), dateFormatter(bo.BO_timestamp)
                        , String.format("BO: %s%% \t HR: %s bpm", bo.BO, hr.HR));
            }else {
                lastMeasurements[OXIMETER_INDEX] = new LastMeasurement(context.getResources().getString(R.string.iot_pulse_oximeter), dateFormatter(bo.BO_timestamp)
                        , String.format("BO: %s%% \t HR: N/A", bo.BO));
            }

        }else {

            if (monitor_hrVector.size() != 0) {
                hr = monitor_hrVector.get(monitor_hrVector.size() - 1);
                lastMeasurements[OXIMETER_INDEX] = new LastMeasurement(context.getResources().getString(R.string.iot_pulse_oximeter), dateFormatter(hr.HR_timestamp)
                        , String.format("BO:N/A \t HR: %s bpm", "", hr.HR));
            }else {
                lastMeasurements[OXIMETER_INDEX] = new LastMeasurement(context.getResources().getString(R.string.iot_pulse_oximeter), dateFormatter(bo.BO_timestamp)
                        , String.format("BO:N/A \t HR: N/A", "", ""));
            }
        }

        if (monitor_stemos.size() != 0) {
            stemo = monitor_stemos.get(monitor_stemos.size() - 1);
            lastMeasurements[STEMO_INDEX] = new LastMeasurement(STEMOSCOPE_NAME, dateFormatter(stemo.Stemoscope_timestamp), "");
        }

        notifyDataSetChanged();
    }

    public IOTSettingAdapter(Context context, IClickListner iClickListner,FragmentManager fragmentManager) {
        ioT_deviceVector = new Vector<>();
        this.context = context;
        this.iClickListner = iClickListner;
        this.fragmentManager = fragmentManager;
    }


    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterIotDeviceBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.adapter_iot_device, parent, false);
        return new MyViewHolder(binding);
    }


    @Override
    public int getItemCount() {
        return ioT_deviceVector.size();
    }

    // stores and recycles views as they are scrolled off screen
    class MyViewHolder extends RecyclerView.ViewHolder {
        AdapterIotDeviceBinding binding;

        MyViewHolder(AdapterIotDeviceBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;

        }
    }


    private final int GLUCOSE_INDEX = 0;
    private final int SCALE_INDEX = 1;
    private final int BP_INDEX = 2;
    private final int OXIMETER_INDEX = 3;
    private final int STEMO_INDEX = 4;

    private int setAccordingToType(String device_type, AdapterIotDeviceBinding holder) {
        int id = 0;
        Drawable deviceIcon = null;
        String deviceType = "UNNAMED DEVICE";
        LastMeasurement lm = null;
        holder.txtMeausre.setText(ID_String(R.string.measure));
        holder.measurementStatus.setVisibility(View.GONE);
        holder.txtGlucoReading.setVisibility(View.GONE);
        if (device_type.contains(OXIMETER)) { //if is oximeter
            /**/
            deviceIcon = getIDDrawable(R.drawable.pulse_oxy_ic);
            deviceType = ID_String(R.string.iot_pulse_oximeter);
            id = R.id.action_IOT_MainPage_Fragment_to_iHealthPulseOxiFragment;
            /*RecentData Below*/
            lm = lastMeasurements[OXIMETER_INDEX];
        } else if (device_type.contains(BP)) { //if is Blood Pressure
            deviceType = ID_String(R.string.blood_pressure);
            deviceIcon = getIDDrawable(R.drawable.blood_pressure_ic);
            id = R.id.action_IOT_MainPage_Fragment_to_iHealthBp3LFragment;
            /*RecentData Below*/
            lm = lastMeasurements[BP_INDEX];
            if (lm != null)
                getBPStatus(lm.measurement, holder);
        } else if (device_type.contains("Glucose")) { //if is Glucose
            deviceType = ID_String(R.string.iot_glucometer);
            deviceIcon = getIDDrawable(R.drawable.glucometer_ic);
            id = R.id.action_IOT_MainPage_Fragment_to_iHealthBG5Fragment;
            /*RecentData Below*/
            lm = lastMeasurements[GLUCOSE_INDEX];
            holder.txtGlucoReading.setVisibility(View.VISIBLE);
        } else if (device_type.contains(SCALE)) { //if is Scale
            deviceType = ID_String(R.string.iot_scale);
            deviceIcon = getIDDrawable(R.drawable.scale_ic);
            id = R.id.action_IOT_MainPage_Fragment_to_iHealthScaleFragment;
            /*RecentData Below*/
            lm = lastMeasurements[SCALE_INDEX];
        } else if (device_type.contains(STEMOSCOPE_NAME)) { //if is Stemoscope
            deviceType = ID_String(R.string.iot_stemoscope);
            deviceIcon = getIDDrawable(R.drawable.stethoscope_ic);
            id = R.id.action_IOT_MainPage_Fragment_to_stemoscopeFragment;
            /*RecentData Below*/
            lm = lastMeasurements[STEMO_INDEX];
            holder.txtMeausre.setText(ID_String(R.string.record));
        } else if (device_type.contains(SMART_WATCH)) {
            deviceType = ID_String(R.string.smart_watch);
            deviceIcon = getIDDrawable(R.drawable.smart_watch_ic);
        }else if (device_type.contains(APPLE_HEALTH)) {
            deviceType = ID_String(R.string.apple_health);
            deviceIcon = getIDDrawable(R.drawable.ic_vital);
        }
        holder.txtMeausre.setEnabled(true);

        //if we got some icon then set it
        if (deviceIcon != null)
            holder.iotDeviceImg.setImageDrawable(deviceIcon);

        //set the deviceType
        holder.deviceType.setText(deviceType);


        //if last measurement has some data
        if (lm != null) {
            holder.txtMeausre.setEnabled(true);
            holder.measurement.setText(lm.measurement);
           // binding.txtDate.setText(DateUtil.formatedDate(clinicVitaldata.getVitalDate(), SERVER_DATE_FORMAT, DATE_FORMAT2));
            holder.measurementTime.setText(lm.measurement_time);
            holder.txtViewRecords.setEnabled(true);
            holder.txtViewRecords.setTextColor(context.getColor(R.color.azure));
        } else {

            //otherwise disable viewrecord button and set the text according to the device type
            // holder.txtMeausre.setBackgroundColor(context.getColor(R.color.disableBlueButton));
            //  holder.txtMeausre.setEnabled(false);
            if (device_type.equals(STEMOSCOPE_NAME)) {
                holder.measurement.setText(R.string.no_latest_record);
                File wavFile = new File(CDoctor2Application.application.getFilesDir().getAbsolutePath(), "stemoscope.wav");
                if (wavFile == null) {
                    holder.txtViewRecords.setEnabled(false);
                    holder.txtViewRecords.setTextColor(context.getColor(R.color.disableBlueButton));
                }
            }else if (device_type.equals(APPLE_HEALTH)) {
                /*holder.measurement.setText("");
                holder.measurementTime.setText("");
                holder.txtViewRecords.setEnabled(true);
                holder.txtViewRecords.setTextColor(context.getColor(R.color.azure));*/

                holder.txtMeausre.setVisibility(View.INVISIBLE);
                holder.txtViewRecords.setVisibility(View.INVISIBLE);
            } else if (!device_type.equals(SMART_WATCH)) {
                holder.measurement.setText(R.string.no_latest_measurement);
                holder.measurementTime.setText(R.string.not_measures);
                holder.txtViewRecords.setEnabled(false);
                holder.txtViewRecords.setTextColor(context.getColor(R.color.disableBlueButton));
            }
        }

        return id;
    }

    /*
        click listener attaching to the view
        it will attach click listener to the all types of devices binding's rootview and imgCheckVital
     */
    private void addClickListeners(AdapterIotDeviceBinding holder, String mac_address, int passID, String device_type, int position) {
        holder.txtViewRecords.setOnClickListener(v -> {
            activity = (AppCompatActivity) v.getContext();
            Log.e("device",""+device_type);
            if (PO_DEVICE_TYPE.equalsIgnoreCase(device_type)) {
                if (monitor_boVector.size()!=0){
                    if (monitor_hrVector.size() != 0) {
                        viewRecords(device_type, bo.BO_timestamp, bo.BO, hr.HR);
                    } else {
                        viewRecords(device_type, bo.BO_timestamp, bo.BO, "");
                    }
                }else {
                    if (monitor_hrVector.size() != 0) {
                        viewRecords(device_type, hr.HR_timestamp, "", hr.HR);
                    } else {
                        viewRecords(device_type, "", "", "");
                    }
                }

            } else if (BP_DEVICE_TYPE.equalsIgnoreCase(device_type)) {
                viewRecords(device_type, bp.BP_timestamp, bp.BPH, bp.BPL);
            } else if (GLUCOMETER_DEVICE_TYPE.equalsIgnoreCase(device_type)) {
                viewRecords(device_type, glucose.Glucose_timestamp, glucose.Glucose, "");
            } else if (SCALE_DEVICE_TYPE.equalsIgnoreCase(device_type)) {
                viewRecords(device_type, weight.weight_timestamp, weight.weight, "");
            } else if (STEMOSCOPE_NAME.equalsIgnoreCase(device_type)) {
                Bundle bundle = new Bundle();
                bundle.putString(IHEALTH_MAC_ADDR, mac_address);
                Navigation.findNavController(holder.rootView).navigate(R.id.action_IOT_MainPage_Fragment_to_stemoAudioFragment, bundle);
            } else if (SMART_WATCH.equalsIgnoreCase(device_type)) {
                viewRecords(device_type, "", ioT_deviceVector.get(position).getDevice_macAddress(), "");
            }else if (APPLE_HEALTH.equalsIgnoreCase(device_type)) {
                /*viewRecords(device_type, "", ioT_deviceVector.get(position).getDevice_macAddress(), "");*/
                /*Bundle data = new Bundle();
                Navigation.findNavController(holder.rootView).navigate(R.id.action_IOT_MainPage_Fragment_to_vitalMonitor, data);*/
                ;
            }
        });
        holder.txtMeausre.setOnClickListener(v -> {
            if (GLUCOMETER_DEVICE_TYPE.equalsIgnoreCase(device_type) || SCALE_DEVICE_TYPE.equalsIgnoreCase(device_type) || PO_DEVICE_TYPE.equalsIgnoreCase(device_type) || BP_DEVICE_TYPE.equalsIgnoreCase(device_type)) {
                moveToNext(passID, mac_address, holder);
            } else if (STEMOSCOPE_NAME.equalsIgnoreCase(device_type)) {
                Bundle bundle = new Bundle();
                bundle.putString(IHEALTH_MAC_ADDR, mac_address);
                Navigation.findNavController(holder.rootView).navigate(R.id.action_IOT_MainPage_Fragment_to_stemoscopeFragment, bundle);
            } else if (APPLE_HEALTH.equalsIgnoreCase(device_type)) {
                /*viewRecords(device_type, "", ioT_deviceVector.get(position).getDevice_macAddress(), "");*/
            }else if (SMART_WATCH.equalsIgnoreCase(device_type)) {
                Bundle data = new Bundle();
                data.putString(SMART_MAC, mac_address);
                Navigation.findNavController(holder.rootView).navigate(R.id.action_IOT_MainPage_Fragment_to_watchItemList, data);
            }
        });
        holder.txtGlucoReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRoutineDialog();
            }
        });

        holder.measurementStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString(KEY_MIN_BP, minBpLevel);
                args.putString(KEY_MAX_BP, maxBpLevel);
                BPInfoFragment bpInfoFragment = new BPInfoFragment();
                bpInfoFragment.setArguments(args);
                bpInfoFragment.show(fragmentManager, "BpInfo Fragment");
            }
        });

    }

    void viewRecords(String type, String timeStamp, String measurement1, String measurement2) {
        iClickListner.viewRecords(type, timeStamp, measurement1, measurement2);

    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        IoT_Device ioT_device = ioT_deviceVector.get(position);

        String mac_address = ioT_device.device_macAddress;
        String device_type = ioT_device.device_type;

        final int passID = setAccordingToType(device_type, holder.binding);
        addClickListeners(holder.binding, mac_address, passID, device_type, position);


    }

    private void showRoutineDialog() {
        final String userId = CDoctor2Application.getLoginInfo().getAccount();
        OnPostExecute ope = result -> {
            PatientRoutineDefaultMessage routine = new PatientRoutineDefaultMessage((SoapObject) result);
            GlucoseDailyRoutine.setRoutine(routine.wakeup_time, routine.breakfast_time, routine.lunch_time, routine.dinner_time, routine.bed_time);
            GlucoseRoutineAlertDialog dialog = GlucoseRoutineAlertDialog.newInstance(context);
            dialog.show();
        };
        WebService.webServiceAsyncTask(WebServiceID.retrieve_patient_routine_default_message, ope, userId);
    }

    private Drawable getIDDrawable(int id) {
        return ResourcesCompat.getDrawable(context.getResources(), id, context.getTheme());
    }

    private String ID_String(int id) {
        return context.getResources().getString(id);
    }


    public Vector<IoT_Device> getIoT_deviceVector() {
        return ioT_deviceVector;
    }

    public void setIoT_deviceVector(Vector<IoT_Device> ioT_deviceVector) {
        this.ioT_deviceVector.clear();
        this.ioT_deviceVector.addAll(
                Stream.of(ioT_deviceVector)
                        .filter(device -> !device.device_type.equalsIgnoreCase("Tablet") && !device.device_type.equalsIgnoreCase(APPLE_HEALTH)).sorted().toList()
        );
        notifyDataSetChanged();
    }

    public void getBPStatus(String bp, AdapterIotDeviceBinding holder) {
        maxBpLevel = bp.substring(0, bp.indexOf("/")).trim();
        minBpLevel = bp.substring(bp.indexOf("/") + 1).replace("mmHg", " ").trim();
        if (minBpLevel != null) {
            holder.measurementStatus.setVisibility(View.VISIBLE);
            holder.txtDia.setText(": " + AppUtiltiy.getBpDiaStatus(minBpLevel, context));
            holder.txtDia.setTextColor(AppUtiltiy.getDiaColor());

        } else {
            holder.txtDia.setVisibility(View.GONE);
            holder.labelDia.setVisibility(View.GONE);
        }
        if (maxBpLevel != null) {
            holder.measurementStatus.setVisibility(View.VISIBLE);
            holder.txtSys.setText(": " + AppUtiltiy.getBpSysStatus(maxBpLevel, context));
            holder.txtSys.setTextColor(AppUtiltiy.getSysColor());
        } else {
            holder.txtSys.setVisibility(View.GONE);
            holder.labelSys.setVisibility(View.GONE);
        }
    }


    public void moveToNext(int passID, String mac_address, AdapterIotDeviceBinding rootView) {
        Bundle bundle = new Bundle();
        bundle.putString(IHEALTH_MAC_ADDR, mac_address);
        if (passID != 0) {
            Navigation.findNavController(rootView.rootView).navigate(passID, bundle);
        }
    }

    public interface IClickListner {
        void viewRecords(String type, String timeStamp, String measurement1, String measurement2);
    }

    private static String dateFormatter(String timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy, hh:mm aa", Locale.getDefault());
        return sdf.format(new Date(Long.valueOf(timestamp) * 1000));
    }

}
