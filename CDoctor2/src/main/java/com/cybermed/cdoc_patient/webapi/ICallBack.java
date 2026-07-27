package com.cybermed.cdoc_patient.webapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ICallBack {
    /**
     * show progress
     */
    void showProgress();

    /**
     * hide progress
     */
    void hideProgress();
    void unsubscribe();
    void connectDevice();
    void apiMapValues(HashMap<String, List<Map<String, String>>> hashMap);
}
