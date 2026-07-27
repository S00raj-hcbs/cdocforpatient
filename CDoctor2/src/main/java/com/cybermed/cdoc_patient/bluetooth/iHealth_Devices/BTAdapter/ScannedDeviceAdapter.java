package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BTAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cybermed.cdoc_patient.R;
import com.cdfortis.datainterface.soap.model.IoT_Device;
import com.cybermed.cdoc_patient.databinding.AdapterScannedDeviceBinding;
import com.cybermed.cdoc_patient.ws.WS;

import java.util.ArrayList;
import java.util.List;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.BP_DEVICE_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.GLUCOMETER_DEVICE_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.PO_DEVICE_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.BluetoothBaseFragment.SCALE_DEVICE_TYPE;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.IOTDeviceSetUpFragment.set_up_device_name;
import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthScannedDeviceFragment.UNRECOGNIZED;

public class  ScannedDeviceAdapter extends RecyclerView.Adapter<ScannedDeviceAdapter.MyViewHolder> {

    private List<IoT_Device> scannedDeviceList = new ArrayList<>();
    private Context context;


    class MyViewHolder extends RecyclerView.ViewHolder {


        AdapterScannedDeviceBinding binding = null;
        MyViewHolder(AdapterScannedDeviceBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        AdapterScannedDeviceBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.adapter_scanned_device, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        IoT_Device device = scannedDeviceList.get(position);
        holder.binding.deviceCybermedCode.setText(device.getCybermed_code());

        if(device.getCybermed_code().equals(UNRECOGNIZED)){
            holder.binding.deviceCybermedCode.setText(device.getDevice_macAddress());
        }

        appendDeviceTypeInDisplay(holder.binding.deviceType, holder.binding.deviceIcon, device.getDevice_type());

        holder.binding.rootView.setOnClickListener(v -> {
            RegisterDevice(device.getDevice_type() , device.getDevice_macAddress() , v);
        });
    }

    private void appendDeviceTypeInDisplay(TextView displayText, ImageView deviceIcon, String deviceType) {
        String device_text = "";

        switch (deviceType){
            case GLUCOMETER_DEVICE_TYPE:
                device_text = context.getString(R.string.iot_glucometer);
                deviceIcon.setImageResource(R.drawable.rpm_glucometer_icon);
                break;
            case SCALE_DEVICE_TYPE:
                device_text = context.getString(R.string.iot_scale);
                deviceIcon.setImageResource(R.drawable.rpm_scale_icon);
                break;
            case BP_DEVICE_TYPE:
                device_text = context.getString(R.string.iot_blood_pressure);
                deviceIcon.setImageResource(R.drawable.rpm_bp_icon);
                break;
            case PO_DEVICE_TYPE:
                device_text = context.getString(R.string.iot_pulse_oximeter);
                deviceIcon.setImageResource(R.drawable.rpm_oximeter_icon);
                break;
        }

        displayText.setText(device_text);
    }

    private void RegisterDevice(String device_type, String device_mac_address, View view) {

        OnPostExecute ope = result -> {
            if (result != null && result.toString().equals("1")) {
                Toast.makeText(context, "Register device successfully!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigate(R.id.action_iHealthScannedDeviceFragment_to_IOTMain);
            }else{
                Toast.makeText(context, "Failed registering device. It may already be registered",
                        Toast.LENGTH_SHORT).show();
            }
        };

        WS.registerBluetoothDevice(device_type, device_mac_address, set_up_device_name, ope);
    }

    public void addDevice(IoT_Device device) {
        scannedDeviceList.add(device);
        notifyDataSetChanged();
    }

    public boolean contains(IoT_Device device) {
        return scannedDeviceList.contains(device);
    }

    @Override
    public int getItemCount() {
        return scannedDeviceList.size();
    }


}
