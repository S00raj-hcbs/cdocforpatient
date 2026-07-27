package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility;

import com.github.mikephil.charting.data.BarData;

public class Custom {
    public BarData data;
    public String[] labels;

    public Custom(BarData data, String[] labels) {
        this.data = data;
        this.labels = labels;
    }
}
