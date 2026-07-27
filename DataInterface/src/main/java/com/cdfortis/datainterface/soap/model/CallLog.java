package com.cdfortis.datainterface.soap.model;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.util.Hashtable;

/**
 * Created by joshu on 6/8/2017.
 */

public class CallLog implements KvmSerializable {

    public String time;
    public String call_type;
    public String Provider_Name;
    public String talk_min;
    public String charge_type;
    public String charge_amount;
    public String appt_id;
    public String provider_org_code;
    public String provider_provider_code;
    public String start_time;
    public String end_time;

    public CallLog(){}

    public CallLog(SoapObject soapObject)
    {
        if (soapObject == null)
            return;
        if (soapObject.hasProperty("time"))
        {
            Object obj = soapObject.getProperty("time");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                time = j.toString();
            }else if (obj!= null && obj instanceof String){
                time = (String) obj;
            }
        }
        if (soapObject.hasProperty("call_type"))
        {
            Object obj = soapObject.getProperty("call_type");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                call_type = j.toString();
            }else if (obj!= null && obj instanceof String){
                call_type = (String) obj;
            }
        }
        if (soapObject.hasProperty("Provider_Name"))
        {
            Object obj = soapObject.getProperty("Provider_Name");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                Provider_Name = j.toString();
            }else if (obj!= null && obj instanceof String){
                Provider_Name = (String) obj;
            }
        }
        if (soapObject.hasProperty("talk_min"))
        {
            Object obj = soapObject.getProperty("talk_min");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                talk_min = j.toString();
            }else if (obj!= null && obj instanceof String){
                talk_min = (String) obj;
            }
        }
        if (soapObject.hasProperty("charge_type"))
        {
            Object obj = soapObject.getProperty("charge_type");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                charge_type = j.toString();
            }else if (obj!= null && obj instanceof String){
                charge_type = (String) obj;
            }
        }
        if (soapObject.hasProperty("charge_amount"))
        {
            Object obj = soapObject.getProperty("charge_amount");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                charge_amount = j.toString();
            }else if (obj!= null && obj instanceof String){
                charge_amount = (String) obj;
            }
        }
        if (soapObject.hasProperty("appt_id"))
        {
            Object obj = soapObject.getProperty("appt_id");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                appt_id = j.toString();
            }else if (obj!= null && obj instanceof String){
                appt_id = (String) obj;
            }
        }
        if (soapObject.hasProperty("provider_org_code"))
        {
            Object obj = soapObject.getProperty("provider_org_code");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                provider_org_code = j.toString();
            }else if (obj!= null && obj instanceof String){
                provider_org_code = (String) obj;
            }
        }
        if (soapObject.hasProperty("provider_provider_code"))
        {
            Object obj = soapObject.getProperty("provider_provider_code");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                provider_provider_code = j.toString();
            }else if (obj!= null && obj instanceof String){
                provider_provider_code = (String) obj;
            }
        }
        if (soapObject.hasProperty("start_time"))
        {
            Object obj = soapObject.getProperty("start_time");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                start_time = j.toString();
            }else if (obj!= null && obj instanceof String){
                start_time = (String) obj;
            }
        }
        if (soapObject.hasProperty("end_time"))
        {
            Object obj = soapObject.getProperty("end_time");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                end_time = j.toString();
            }else if (obj!= null && obj instanceof String){
                end_time = (String) obj;
            }
        }
    }
    @Override
    public Object getProperty(int arg0) {
        switch(arg0){
            case 0:
                return time == null ? "" : time;
            case 1:
                return call_type == null ? "" : call_type;
            case 2:
                return Provider_Name == null ? "" : Provider_Name;
            case 3:
                return talk_min == null ? "" : talk_min;
            case 4:
                return charge_type == null ? "" : charge_type;
            case 5:
                return charge_amount == null ? "" : charge_amount;
            case 6:
                return appt_id == null ? "" : appt_id;
            case 7:
                return provider_org_code == null ? "" : provider_org_code;
            case 8:
                return provider_provider_code == null ? "" : provider_provider_code;
            case 9:
                return start_time == null ? "" : start_time;
            case 10:
                return end_time == null ? "" : end_time;
         }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 11;
    }

    @Override
    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "time";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "call_type";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Provider_Name";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "talk_min";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "charge_type";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "charge_amount";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "appt_id";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "provider_org_code";
                break;
            case 8:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "provider_provider_code";
                break;
            case 9:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "start_time";
                break;
            case 10:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "end_time";
                break;


        }
    }

//    @Override
//    public String getInnerText() {
//        return null;
//    }


//    @Override
//    public void setInnerText(String s) {
//    }


    @Override
    public void setProperty(int arg0, Object arg1) {
    }
}
