package com.cdfortis.datainterface.soap;

import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cdfortis.datainterface.JsonSerializable;
import com.cdfortis.datainterface.soap.model.IoT_Device;
import com.cdfortis.datainterface.soap.model.Patient_Demographic;
import com.cdfortis.datainterface.soap.model.Patient_Info;
import com.cdfortis.datainterface.soap.model.SoapObjectVector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * Created by qinwe on 2017/5/5.
 */

public class UserInfo implements JsonSerializable {
    private String firstName = "";
    private String lastname = "";
    private String email = "";
    private String mi = "";
    private String sex = "";
    private String DOB = "";//生日
    private String addr1 = "";
    private String addr2 = "";
    private String city = "";
    private String state = "";
    private String zip = "";
    private String phoneNum = "";
    private String pwd = "";
    private String service_code = "";
    private String default_state = "";
    private String mobile_mode = "";
    transient private MutableLiveData<Vector<IoT_Device>> ioT_devices_obs = new MutableLiveData<>();
    transient private MutableLiveData<String> ioT_devices_error = new MutableLiveData<>();
    @Override
    public void deserialize(JSONObject jsonObject) {
        firstName = jsonObject.optString("firstName", "");
        lastname = jsonObject.optString("lastName", "");
        email = jsonObject.optString("email", "");
        mi = jsonObject.optString("mi", "");
        sex = jsonObject.optString("sex", "");
        DOB = jsonObject.optString("DOB", "");
        addr1 = jsonObject.optString("addr1", "");
        addr2 = jsonObject.optString("addr2", "");
        city = jsonObject.optString("city", "");
        state = jsonObject.optString("state", "");
        zip = jsonObject.optString("zip", "");
        phoneNum = jsonObject.optString("phoneNum", "");
        pwd = jsonObject.optString("pwd", "");
        service_code = jsonObject.optString("service_code", "");
        default_state = jsonObject.optString("default_state", "");
        mobile_mode = jsonObject.optString("mobile_mode", "");
    }

    @Override
    public void serialize(JSONObject jsonObject) {
        try {
            jsonObject.put("firstName", firstName);
            jsonObject.put("lastName", lastname);
            jsonObject.put("email", email);
            jsonObject.put("mi", mi);
            jsonObject.put("sex", sex);
            jsonObject.put("DOB", DOB);
            jsonObject.put("addr1", addr1);
            jsonObject.put("addr2", addr2);
            jsonObject.put("city", city);
            jsonObject.put("state", state);
            jsonObject.put("zip", zip);
            jsonObject.put("phoneNum", phoneNum);
            jsonObject.put("pwd", pwd);
            jsonObject.put("service_code", service_code);
            jsonObject.put("default_state", default_state);
            jsonObject.put("mobile_mode", mobile_mode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deserialize(Patient_Demographic patient_demographic_info) {
        firstName = patient_demographic_info.first_name;
        lastname = patient_demographic_info.last_name;
        email = patient_demographic_info.email;
        mi = patient_demographic_info.mi;
        sex = patient_demographic_info.sex;
        DOB = patient_demographic_info.DOB;
        addr1 = patient_demographic_info.addr1;
        addr2 = patient_demographic_info.addr2;
        city = patient_demographic_info.city;
        state = patient_demographic_info.state;
        zip = patient_demographic_info.zip;
        phoneNum = patient_demographic_info.phone_number;
        service_code = patient_demographic_info.service_code;
        default_state = patient_demographic_info.default_state;
        mobile_mode = patient_demographic_info.mobile_mode;
    }

    public void deserialize(Patient_Info providerInfo) {
        firstName = providerInfo.first_name;
        lastname = providerInfo.last_name;
        email = providerInfo.email;
        mi = providerInfo.mi;
        sex = providerInfo.sex;
        DOB = providerInfo.DOB;
        addr1 = providerInfo.addr1;
        addr2 = providerInfo.addr2;
        city = providerInfo.city;
        state = providerInfo.state;
        zip = providerInfo.zip;
        phoneNum = providerInfo.phone_number;
        service_code = providerInfo.service_code;
        default_state = providerInfo.default_state;
        mobile_mode = providerInfo.mobile_mode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMi() {
        return mi;
    }

    public void setMi(String mi) {
        this.mi = mi;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getAddr1() {
        return addr1;
    }

    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }

    public String getAddr2() {
        return addr2;
    }

    public void setAddr2(String addr2) {
        this.addr2 = addr2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getService_code() {

        if (service_code == null) {
            service_code = "";
        }

        return service_code;
    }

    public void setService_code(String service_code) {
        this.service_code = service_code;
    }

    public String getDefault_state() {
        if (default_state == null) {
            return "";
        }
        return default_state;
    }


    public String getMobile_mode() {
        if (mobile_mode == null) {
            return "";
        }

        return mobile_mode;
    }

    public void setDefault_state(String default_state) {
        this.default_state = default_state;
    }


    public LiveData<Vector<IoT_Device>> getIoT_devices_obs() {
        return ioT_devices_obs;
    }

    public void setDevices(LiveData<Vector<IoT_Device>>ioT_devices_obs){
        this.ioT_devices_obs = (MutableLiveData<Vector<IoT_Device>>) ioT_devices_obs;
    }


    public synchronized void setIoT_devices_obs(Vector<IoT_Device> ioT_devices) {
        Set<IoT_Device> set = new HashSet<>(ioT_devices);
        Vector<IoT_Device> newVector = new Vector<>(set);

        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.ioT_devices_obs.setValue(newVector);
        } else {  //thread other than main thread
            this.ioT_devices_obs.postValue(newVector);
        }
    }

    public void addIoT_device(IoT_Device device) {
        Vector<IoT_Device> device_vector = this.ioT_devices_obs.getValue();

        if (device_vector == null) {
            Vector<IoT_Device> temp = new SoapObjectVector<>();
            temp.add(device);
            setIoT_devices_obs(temp);
            return;
        }

        device_vector.add(device);
        setIoT_devices_obs(device_vector);
    }

    public MutableLiveData<String> getIoT_devices_error() {
        return ioT_devices_error;
    }

    public void setIoT_devices_error(MutableLiveData<String> ioT_devices_error) {
       // this.ioT_devices_error = ioT_devices_error;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            this.ioT_devices_error.setValue("");
        } else {  //thread other than main thread
            this.ioT_devices_error.postValue("");
        }
    }
}
