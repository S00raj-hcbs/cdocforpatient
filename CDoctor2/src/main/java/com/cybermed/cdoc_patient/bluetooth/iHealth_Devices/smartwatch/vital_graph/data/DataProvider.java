package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.data;

import android.graphics.Color;

import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Custom;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Custom_Line;

public class DataProvider {
    DataRepository repository;
   private final int[] colors = new int[]{Color.parseColor("#6870B5"),
            Color.parseColor("#8969AE"),
            Color.parseColor("#53BD8B"),
            Color.parseColor("#F2727A"),
            Color.parseColor("#F79452"),
            Color.parseColor("#DDA827")};

    public DataProvider(String json) {
        this.repository = new DataRepository();
        repository.getApiResponse(json);
    }

    public Custom getWeekSteps() {
        return repository.getWeekData("smart_daily", new String[]{"step"}, new int[]{colors[0]});
    }

    public Custom getMonthSteps() {
        return repository.getMonthData("smart_daily", new String[]{"step"}, new int[]{colors[0]});
    }

    public Custom getYearSteps() {
        return repository.getYearData("smart_daily", new String[]{"step"}, new int[]{colors[0]});
    }

    public Custom getWeekDistance() {
        return repository.getWeekData("smart_daily", new String[]{"distance"}, new int[]{colors[1]});
    }

    public Custom getMonthDistance() {
        return repository.getMonthData("smart_daily", new String[]{"distance"}, new int[]{colors[1]});
    }

    public Custom getYearDistance() {
        return repository.getYearData("smart_daily", new String[]{"distance"}, new int[]{colors[1]});
    }

    public Custom getWeekCalories() {
        return repository.getWeekData("smart_daily", new String[]{"calories"}, new int[]{colors[2]});
    }

    public Custom getMonthCalories() {
        return repository.getMonthData("smart_daily", new String[]{"calories"}, new int[]{colors[2]});
    }

    public Custom getYearCalories() {
        return repository.getYearData("smart_daily", new String[]{"calories"}, new int[]{colors[2]});
    }

    public Custom getWeekBloodOxygen() {
        return repository.getWeekData("smart_bp", new String[]{"spo2Data"}, new int[]{colors[3]});
    }

    public Custom getMonthBloodOxygen() {
        return repository.getMonthData("smart_bp", new String[]{"spo2Data"}, new int[]{colors[3]});
    }

    public Custom getYearBloodOxygen() {
        return repository.getYearData("smart_bp", new String[]{"spo2Data"}, new int[]{colors[3]});
    }

    public Custom_Line getWeekTemperature() {
        return repository.getWeekLineGraphData("smart_temp", "temperature", colors[4]);
    }

    public Custom_Line getMonthTemperature() {
        return repository.getMonthLineGraphData("smart_temp", "temperature", colors[4]);
    }

    public Custom_Line getYearTemperature() {
        return repository.getYearLineGraphData("smart_temp", "temperature", colors[4]);
    }

    public Custom getWeekBloodPressure() {
        return repository.getWeekData("smart_hrv", new String[]{"highBP", "lowBP"}, new int[]{colors[5], colors[0]});
    }

    public Custom getMonthBloodPressure() {
        return repository.getMonthData("smart_hrv", new String[]{"highBP", "lowBP"}, new int[]{colors[5], colors[0]});
    }

    public Custom getYearBloodPressure() {
        return repository.getYearData("smart_hrv", new String[]{"highBP", "lowBP"}, new int[]{colors[5], colors[0]});
    }

    public Custom_Line getWeekHearRate() {
        return repository.getWeekLineGraphData("smart_hrv", "heartRate", colors[0]);
    }

    public Custom_Line getMonthHearRate() {
        return repository.getMonthLineGraphData("smart_hrv", "heartRate", colors[0]);
    }

    public Custom_Line getYearHearRate() {
        return repository.getYearLineGraphData("smart_hrv", "heartRate", colors[0]);
    }
}

