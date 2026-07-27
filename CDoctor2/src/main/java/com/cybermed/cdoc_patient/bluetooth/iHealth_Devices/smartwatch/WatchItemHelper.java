package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.swUtil.ReqSaveSWData;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Utility;
import com.cybermed.cdoc_patient.databinding.ActivityWatchItemBinding;
import com.cybermed.cdoc_patient.doctor.docDetail.model.BaseResponseModel;
import com.cybermed.cdoc_patient.util.DateUtil;
import com.cybermed.cdoc_patient.webapi.ICallBack;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.manager.HomeApiManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jstyle.blesdk1963.constant.DeviceKey;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.SmartWatchBaseFragment.getMacAddr;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_BO;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_DAILY;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_HRV;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_TEMP;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_WATCH;

public class WatchItemHelper {
    Context context;
    ActivityWatchItemBinding binding;
    static String swDataJson;
    ICallBack iCallBack;
    private HashMap<String, List<Map<String, String>>> hashMap = new HashMap<>();
    String mMac;

    public WatchItemHelper(Context context, ActivityWatchItemBinding binding, ICallBack iCallBack) {
        this.context = context;
        this.binding = binding;
        this.iCallBack = iCallBack;
    }

    /**
     * set data to ui xml
     *
     * @param hashMap data set
     */
    public void setDataToUI(HashMap<String, List<Map<String, String>>> hashMap) {
        if (hashMap != null) {
            String date = null;
            if (hashMap.get(SMART_BO) != null && hashMap.get(SMART_BO).size() > 0) {
                binding.layoutBloodpressure.parentBloodPressure.setVisibility(View.VISIBLE);
                binding.layoutBloodpressure.setMap(hashMap.get(SMART_BO).get(0));
                date = hashMap.get(SMART_BO).get(0).get(DeviceKey.Date);
                String formatedDate = DateUtil.formatedDate(date,
                        "yyyy.MM.dd HH:mm:ss", "dd MMM yyyy");
                binding.layoutBloodpressure.textBloodDate.setText(formatedDate);
            }
            if (hashMap.get(SMART_DAILY) != null && hashMap.get(SMART_DAILY).size() > 0) {
                binding.layoutDaily.parentDaily.setVisibility(View.VISIBLE);
                binding.topCircularBars.setVisibility(View.VISIBLE);
                binding.distanceCount.setText(hashMap.get(SMART_DAILY).get(0).get(DeviceKey.Distance));
                binding.footStepCount.setText(hashMap.get(SMART_DAILY).get(0).get(DeviceKey.Step));
                binding.calorieCount.setText(hashMap.get(SMART_DAILY).get(0).get(DeviceKey.Calories));
                binding.layoutDaily.setMap(hashMap.get(SMART_DAILY).get(0));

                if (date != null) {
                    compareDate(date, hashMap.get(SMART_DAILY).get(0).get(DeviceKey.Date)
                            , binding.layoutDaily.textTotalDate);
                } else {
                    binding.layoutDaily.textTotalDate.setVisibility(View.VISIBLE);
                    binding.layoutDaily.textTotalDate.setText(hashMap.get(SMART_DAILY).get(0).get(DeviceKey.Date));
                }
                date = hashMap.get(SMART_DAILY).get(0).get(DeviceKey.Date);
                binding.layoutDaily.textTotalDistance.setText(String.format("%.1f", Utility.convertKmsToMiles(Float.parseFloat(
                        hashMap.get(SMART_DAILY).get(0).get(DeviceKey.Distance)))) + " Mile");
            }
            if (hashMap.get(SMART_TEMP) != null && hashMap.get(SMART_TEMP).size() > 0) {
                binding.layoutTemp.parentTemp.setVisibility(View.VISIBLE);
                binding.layoutTemp.setMap(hashMap.get(SMART_TEMP).get(0));
                binding.layoutTemp.txtTemperature.setText(String.format("%.1f",
                        (double) Utility.convertCelsiusToFahrenheit(Float.parseFloat(
                                hashMap.get(SMART_TEMP).get(0).get(DeviceKey.temperature)))) + " °F");
                if (date != null) {
                    compareDate(date, hashMap.get(SMART_TEMP).get(0).get(DeviceKey.Date)
                            , binding.layoutTemp.textTempDate);
                } else {
                    binding.layoutTemp.textTempDate.setVisibility(View.VISIBLE);
                    binding.layoutTemp.textTempDate.setText(hashMap.get(SMART_TEMP).get(0).get(DeviceKey.Date));
                }
                date = hashMap.get(SMART_TEMP).get(0).get(DeviceKey.Date);
            }
            if (hashMap.get(SMART_HRV) != null && hashMap.get(SMART_HRV).size() > 0) {
                binding.layoutHrv.parentHrv.setVisibility(View.VISIBLE);
                binding.layoutHrv.setMap(hashMap.get(SMART_HRV).get(0));
                if (date != null) {
                    compareDate(date, hashMap.get(SMART_HRV).get(0).get(DeviceKey.Date)
                            , binding.layoutHrv.textHrvDate);
                } else {
                    binding.layoutHrv.textHrvDate.setVisibility(View.VISIBLE);
                    binding.layoutHrv.textHrvDate.setText(hashMap.get(SMART_HRV).get(0).get(DeviceKey.Date));
                }


            }


        }
    }

    public void compareDate(String olderDate, String newerDate, TextView textView) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
            Date date1 = sdf.parse(olderDate);
            Date currentDate = sdf.parse(newerDate);
            if (sdf.format(date1).compareTo(sdf.format(currentDate)) != 0) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(new SimpleDateFormat("dd MMM yyyy").format(currentDate));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //*************************** APi Call *********************************************

    /**
     * send smart watch data to server
     *
     * @param val json of smart watch data
     */
    public void sendData(String val) {
        swDataJson = val;
        iCallBack.showProgress();
        HomeApiManager apiManager = new HomeApiManager(new IResponseReceiver() {
            @Override
            public void onSuccess(Object data) {
                iCallBack.hideProgress();
                //Toast.makeText(context, "success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                iCallBack.hideProgress();
                // Toast.makeText(context, "failure", Toast.LENGTH_LONG).show();
            }
        }, context);
        ReqSaveSWData reqSaveSWData = new ReqSaveSWData();
        reqSaveSWData.setDeviceMacAddress(mMac);
        reqSaveSWData.setHubMacAddress(getMacAddr());
        reqSaveSWData.setTimestamp(String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000));
        reqSaveSWData.setType(SMART_WATCH);
        reqSaveSWData.setValue(val);
        apiManager.saveSWData(reqSaveSWData);
    }

    /**
     * get data from server AND show last recorded data
     *
     * @param smartMac smart watch mac address
     */
    public void getData(String smartMac) {
        this.mMac = smartMac;
        iCallBack.showProgress();
        HomeApiManager apiManager = new HomeApiManager(new IResponseReceiver() {
            @Override
            public void onSuccess(Object data) {
                iCallBack.hideProgress();
                if (data == null || (((BaseResponseModel<List<ReqSaveSWData>>) data).getObject() != null
                        && ((BaseResponseModel<List<ReqSaveSWData>>) data).getObject().size() == 0)) {
                    iCallBack.connectDevice();
                } else {
                    ArrayList<ReqSaveSWData> resSmartGetData = (ArrayList<ReqSaveSWData>)
                            ((BaseResponseModel<List<ReqSaveSWData>>) data).getObject();
                    swDataJson = resSmartGetData.get(0).getValue();
                    //Toast.makeText(context, "success", Toast.LENGTH_LONG).show();
                    hashMap.clear();
                    convertJsonToMap(swDataJson);
                }
            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                iCallBack.hideProgress();
                Toast.makeText(context, "failure", Toast.LENGTH_LONG).show();
            }
        }, context);

        apiManager.getSWData(SMART_WATCH, smartMac, "1");
    }

    public void convertJsonToMap(String val) {
        Type type = new TypeToken<HashMap<String, List<Map<String, String>>>>() {
        }.getType();
        hashMap = new Gson().fromJson(val, type);
        iCallBack.apiMapValues(hashMap);
        setDataToUI(hashMap);

    }

    /**
     * create data list from smart watch
     *
     * @param mapList   data list from smart watch
     * @param type      type of data
     * @param isDataEnd is more data avialable for same type
     */
    public void createList(List<Map<String, String>> mapList, String type, boolean isDataEnd) {
        List<Map<String, String>> list = new ArrayList<>();
        list.addAll(mapList);
        if (!hashMap.containsKey(type)) {
            hashMap.put(type, list);
        } else {
            List<Map<String, String>> keyList = hashMap.get(type);
            keyList.addAll(list);
            hashMap.put(type, keyList);
        }
        if (hashMap.size() == 4 && isDataEnd) {
            iCallBack.unsubscribe();
            sendData(new Gson().toJson(hashMap));
            setDataToUI(hashMap);
        }
    }

    public void clearData() {
        hashMap.clear();
    }

}
