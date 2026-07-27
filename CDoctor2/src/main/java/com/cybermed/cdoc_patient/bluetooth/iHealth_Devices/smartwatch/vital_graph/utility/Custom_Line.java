package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility;

import com.github.mikephil.charting.data.LineData;

public class Custom_Line {
    public LineData data;
    public String[] labels;

    public Custom_Line(LineData data, String[] labels) {
        this.data = data;
        this.labels = labels;
    }
}
