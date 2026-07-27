package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Custom;

import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Custom_Line;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Custom_LineBP;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Utility;
import com.cybermed.cdoc_patient.databinding.FragmentNewVitalCheckUiBinding;
import com.cybermed.cdoc_patient.databinding.FragmentVitalCheckUiBinding;
import com.cybermed.cdoc_patient.databinding.GraphAdapterLayoutBinding;
import com.cybermed.cdoc_patient.databinding.GraphHistoryBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GraphUI {
    private static final String BP_POSTFIX = "mm/hg";
    private static final String BO_POSTFIX = "%";
    private static final String HR_POSTFIX = "bpm";
    private static final String WEIGHT_POSTFIX = "lbs";
    private static final String GLUCOSE_POSTFIX = "mg/dl";
    public static final String BP_PREFIX = "BP: ";
    public static final String BO_PREFIX = "BO: ";
    public static final String HR_PREFIX = "HR: ";
    public static final String WEIGHT_PREFIX = "Weight: ";
    public static final String GLUCOSE_PREFIX = "Glucose: ";
    public static final String BP_DEVICE_TYPE = "IChoice_BP", PO_DEVICE_TYPE = "IChoice_Oximeter", GLUCOMETER_DEVICE_TYPE = "IChoice_Glucose", SCALE_DEVICE_TYPE = "IChoice_Scale";

    public static void setAppearance(Button selected, Button unselected1, Button unselected2) {
        selected.setTextAppearance(R.style.selectedButton);
        selected.setSelected(true);
        unselected1.setTextAppearance(R.style.unselectedButton);
        unselected1.setSelected(false);
        unselected2.setTextAppearance(R.style.unselectedButton);
        unselected2.setSelected(false);
    }
    public static void setAppearance(Button selected, Button unselected1, Button unselected2, Button unselected3) {
        selected.setTextAppearance(R.style.selectedButton);
        selected.setSelected(true);
        unselected1.setTextAppearance(R.style.unselectedButton);
        unselected1.setSelected(false);
        unselected2.setTextAppearance(R.style.unselectedButton);
        unselected2.setSelected(false);
        unselected3.setTextAppearance(R.style.unselectedButton);
        unselected3.setSelected(false);
    }
    public static void setAppearance2(Button selected, Button unselected1, Button unselected2, Button unselected3, Button unselected4) {
       // selected.setTextAppearance(R.style.selectedButton);
        selected.setSelected(true);
        //unselected1.setTextAppearance(R.style.unselectedButton);
        unselected1.setSelected(false);
       // unselected2.setTextAppearance(R.style.unselectedButton);
        unselected2.setSelected(false);
      //  unselected3.setTextAppearance(R.style.unselectedButton);
        unselected3.setSelected(false);
      //  unselected4.setTextAppearance(R.style.unselectedButton);
        unselected4.setSelected(false);
    }
    public static void updateWeekData(Custom custom_values, BarChart barchart) {
        BarData data = custom_values.data;
        if (data.getEntryCount() != 0) {
            Utility.setDataAvailableBarGraph(barchart);
            Utility.updateGraph(barchart, data);
            Utility.updateLabels(barchart, data.getEntryCount(), custom_values.labels);
        } else {
            Utility.setNoDataAvailableBarGraph(barchart);
            Utility.updateGraph(barchart, null);
        }

    }

    public static void updateWeekData2(Custom custom_values, BarChart barchart) {
        BarData data = custom_values.data;
        if (data.getEntryCount() != 0) {
            Utility.setDataAvailableBarGraph(barchart);
            Utility.updateGraph2(barchart, data);
            Utility.updateLabels(barchart, data.getEntryCount(), custom_values.labels);
        } else {
            Utility.setNoDataAvailableBarGraph(barchart);
            Utility.updateGraph2(barchart, null);
        }

    }


    public static void updateMonthData(BarChart barChart, Custom custom_values) {
        BarData bar_data = custom_values.data;
        if (bar_data.getEntryCount() != 0) {
            Utility.setDataAvailableBarGraph(barChart);
            Utility.updateLabels(barChart, bar_data.getEntryCount(), custom_values.labels);
            Utility.updateGraph(barChart, bar_data);
        } else {
            Utility.setNoDataAvailableBarGraph(barChart);
            Utility.updateGraph(barChart, null);
        }

    }public static void updateMonthData2(BarChart barChart, Custom custom_values) {
        BarData bar_data = custom_values.data;
        if (bar_data.getEntryCount() != 0) {
            Utility.setDataAvailableBarGraph(barChart);
            Utility.updateLabels(barChart, bar_data.getEntryCount(), custom_values.labels);
            Utility.updateGraph2(barChart, bar_data);
        } else {
            Utility.setNoDataAvailableBarGraph(barChart);
            Utility.updateGraph2(barChart, null);
        }

    }

    public static void updateYearData(Custom custom_values, BarChart barChart) {
        BarData data = custom_values.data;
        if (data.getEntryCount() != 0) {
            Utility.setDataAvailableBarGraph(barChart);
            Utility.updateGraph(barChart, data);
            Utility.updateLabels(barChart, data.getEntryCount(), custom_values.labels);
        } else {
            Utility.setNoDataAvailableBarGraph(barChart);
            Utility.updateGraph(barChart, null);
        }
    }
    public static void updateYearData2(Custom custom_values, BarChart barChart) {
        BarData data = custom_values.data;
        if (data.getEntryCount() != 0) {
            Utility.setDataAvailableBarGraph(barChart);
            Utility.updateGraph2(barChart, data);
            Utility.updateLabels(barChart, data.getEntryCount(), custom_values.labels);
        } else {
            Utility.setNoDataAvailableBarGraph(barChart);
            Utility.updateGraph2(barChart, null);
        }
    }


    public static void updateWeekData(Custom_Line custom_values, LineChart lineChart) {
        LineData data = custom_values.data;
        if (data.getEntryCount() != 0) {
            Utility.setDataAvailableLineGraph(lineChart);
            Utility.updateLineGraph(lineChart, data);
            Utility.updateLabelsLineGraph(lineChart, data.getEntryCount(), custom_values.labels);
        } else {
            Utility.setNoDataAvailableLineGraph(lineChart);
            Utility.updateLineGraph(lineChart, null);
        }

    }
    public static void updateWeekDataNew(Custom_Line custom_values, LineChart lineChart) {
        LineData data = custom_values.data;
        if (data.getEntryCount() != 0) {
            Utility.setDataAvailableLineGraph(lineChart);
            Utility.updateLineGraphNew(lineChart, data);
            Utility.updateLabelsLineGraph(lineChart, data.getEntryCount(), custom_values.labels);
        } else {
            Utility.setNoDataAvailableLineGraph(lineChart);
            Utility.updateLineGraphNew(lineChart, null);
        }

    }

    public static void updateDailyData(Custom_Line custom_values, LineChart lineChart) {
        LineData data = custom_values.data;
        if (data.getEntryCount() != 0) {
            Utility.setDataAvailableLineGraph(lineChart);
            Utility.updateLineGraph(lineChart, data);
            Utility.updateLabelsLineGraph(lineChart, data.getEntryCount(), custom_values.labels);
        } else {
            Utility.setNoDataAvailableLineGraph(lineChart);
            Utility.updateLineGraph(lineChart, null);
        }

    }

    public static void updateVitalData(Custom_Line custom_values, LineChart lineChart) {
        LineData data = custom_values.data;
        if (data.getEntryCount() != 0) {
            Utility.setDataAvailableLineGraph(lineChart);
            Utility.updateLineGraph2(lineChart, data);
            Utility.updateLabelsLineGraph(lineChart, data.getEntryCount(), custom_values.labels);
        } else {
            Utility.setNoDataAvailableLineGraph(lineChart);
            Utility.updateLineGraph2(lineChart, null);
        }

    }

    public static void updateWeekData2(Custom_LineBP custom_values, LineChart lineChart) {
        LineData data = custom_values.data;
        if (data.getEntryCount() != 0) {
            Utility.setDataAvailableLineGraph(lineChart);
            Utility.updateLineGraph2(lineChart, data);
            Utility.updateLabelsLineGraph(lineChart, data.getEntryCount(), custom_values.labels);
        } else {
            Utility.setNoDataAvailableLineGraph(lineChart);
            Utility.updateLineGraph2(lineChart, null);
        }

    }
    public static void updateMonthData(LineChart lineChart, Custom_Line custom_values) {
        LineData lineData = custom_values.data;
        if (lineData.getEntryCount() != 0) {
            Utility.setDataAvailableLineGraph(lineChart);
            Utility.updateLabelsLineGraph(lineChart, lineData.getEntryCount(), custom_values.labels);
            Utility.updateLineGraph(lineChart, lineData);
        } else {
            Utility.setNoDataAvailableLineGraph(lineChart);
            Utility.updateLineGraph(lineChart, null);
        }
    }
    public static void updateMonthDataNew(LineChart lineChart, Custom_Line custom_values) {
        LineData lineData = custom_values.data;
        if (lineData.getEntryCount() != 0) {
            Utility.setDataAvailableLineGraph(lineChart);
            Utility.updateLabelsLineGraph(lineChart, lineData.getEntryCount(), custom_values.labels);
            Utility.updateLineGraphNew(lineChart, lineData);
        } else {
            Utility.setNoDataAvailableLineGraph(lineChart);
            Utility.updateLineGraphNew(lineChart, null);
        }
    }

    public static void updateMonthData2(LineChart lineChart, Custom_Line custom_values) {
        LineData lineData = custom_values.data;
        if (lineData.getEntryCount() != 0) {
            Utility.setDataAvailableLineGraph(lineChart);
            Utility.updateLabelsLineGraph(lineChart, lineData.getEntryCount(), custom_values.labels);
            Utility.updateLineGraph(lineChart, lineData);
        } else {
            Utility.setNoDataAvailableLineGraph(lineChart);
            Utility.updateLineGraph(lineChart, null);
        }
    }

    public static void updateYearData(Custom_Line custom_values, LineChart lineChart) {
        LineData data = custom_values.data;
        if (data.getEntryCount() != 0) {
            Utility.setDataAvailableLineGraph(lineChart);
            Utility.updateLineGraph(lineChart, data);
            Utility.updateLabelsLineGraph(lineChart, data.getEntryCount(), custom_values.labels);
        } else {
            Utility.setNoDataAvailableLineGraph(lineChart);
            Utility.updateLineGraph(lineChart, null);
        }
    }
    public static void updateYearDataNew(Custom_Line custom_values, LineChart lineChart) {
        LineData data = custom_values.data;
        if (data.getEntryCount() != 0) {
            Utility.setDataAvailableLineGraph(lineChart);
            Utility.updateLineGraphNew(lineChart, data);
            Utility.updateLabelsLineGraph(lineChart, data.getEntryCount(), custom_values.labels);
        } else {
            Utility.setNoDataAvailableLineGraph(lineChart);
            Utility.updateLineGraphNew(lineChart, null);
        }
    }


    public static void updateUIData(String type, String latest_time, String input1, String input2, GraphHistoryBinding binding) {
        if (type.equalsIgnoreCase(PO_DEVICE_TYPE)) {
            init_latest_measurements(latest_time, binding);

            binding.graphInfo.llLeft.setVisibility(View.VISIBLE);
            binding.graphInfo.llRight.setVisibility(View.VISIBLE);
            binding.heartRateChart.setVisibility(View.GONE);
            binding.graphInfo.topLayout.setVisibility(View.VISIBLE);
            binding.tvBoUnit.setVisibility(View.VISIBLE);
            binding.tvBoUnit.setText("Blood Oxygen(%)");
            binding.tvHrUnit.setText("Heart Rate(bpm)");
            binding.tvHrUnit.setVisibility(View.VISIBLE);
            binding.bloodOxygenChart.setVisibility(View.VISIBLE);
            binding.bloodPressureChart.setVisibility(View.GONE);
            binding.WeightChart.setVisibility(View.GONE);
            binding.GlucoseChart.setVisibility(View.GONE);
            binding.heartRateChart.setVisibility(View.VISIBLE);
            if (!input1.isEmpty() ) {
                if (!input2.isEmpty()){
                    updateTypeData(input1, input2, BO_PREFIX, HR_PREFIX, BO_POSTFIX, HR_POSTFIX, binding);
                }else {
                    updateTypeData(input1, "N/A", BO_PREFIX,HR_PREFIX, BO_POSTFIX, "", binding);
                }
            }else {
                if (!input2.isEmpty()){
                    updateTypeData("N/A", input2, BO_PREFIX, HR_PREFIX, "", HR_POSTFIX, binding);
                }else {
                    updateTypeData("N/A", "N/A", BO_PREFIX,HR_PREFIX, "", "", binding);
                    binding.graphInfo.topLayout.setVisibility(View.GONE);

                }
            }
        } else if (type.equalsIgnoreCase(BP_DEVICE_TYPE)) {
            init_latest_measurements(latest_time, binding);
            updateTypeData(input1 + " / " + input2, BP_PREFIX, BP_POSTFIX, binding);
            binding.graphInfo.llLeft.setVisibility(View.VISIBLE);
            binding.graphInfo.llRight.setVisibility(View.GONE);
            binding.tvBpUnit.setVisibility(View.VISIBLE);
            binding.tvBpUnit.setText("Blood Pressure(Systolic/Diastolic)");
            binding.bloodPressureChart.setVisibility(View.VISIBLE);
            if (!input1.isEmpty() && !input2.isEmpty()) {
                binding.graphInfo.topLayout.setVisibility(View.VISIBLE);
                binding.heartRateChart.setVisibility(View.GONE);
                binding.bloodOxygenChart.setVisibility(View.GONE);
                binding.WeightChart.setVisibility(View.GONE);
                binding.GlucoseChart.setVisibility(View.GONE);
            }else {
                binding.graphInfo.topLayout.setVisibility(View.GONE);
            }
        } else if (type.equalsIgnoreCase(GLUCOMETER_DEVICE_TYPE)) {
            init_latest_measurements(latest_time, binding);
            updateTypeData(input1, GLUCOSE_PREFIX, GLUCOSE_POSTFIX, binding);
            binding.graphInfo.llLeft.setVisibility(View.VISIBLE);
            binding.graphInfo.llRight.setVisibility(View.GONE);
            binding.tvGlucoseUnit.setVisibility(View.VISIBLE);
            binding.tvGlucoseUnit.setText("Glucose(mg/dl)");
            if (!input1.isEmpty()) {
                binding.graphInfo.topLayout.setVisibility(View.VISIBLE);
                binding.heartRateChart.setVisibility(View.GONE);
                binding.bloodOxygenChart.setVisibility(View.GONE);
                binding.bloodPressureChart.setVisibility(View.GONE);
                binding.WeightChart.setVisibility(View.GONE);
            }else {
                binding.graphInfo.topLayout.setVisibility(View.GONE);
            }
            binding.GlucoseChart.setVisibility(View.VISIBLE);
        } else if (type.equalsIgnoreCase(SCALE_DEVICE_TYPE)) {
            init_latest_measurements(latest_time, binding);
            updateTypeData(input1, WEIGHT_PREFIX, WEIGHT_POSTFIX, binding);
            binding.tvWeightUnit.setVisibility(View.VISIBLE);
            binding.tvWeightUnit.setText("Weight(lbs)");
            binding.graphInfo.llLeft.setVisibility(View.VISIBLE);
            binding.graphInfo.llRight.setVisibility(View.GONE);
            binding.WeightChart.setVisibility(View.VISIBLE);
            if (!input1.isEmpty()) {
                binding.heartRateChart.setVisibility(View.GONE);
                binding.graphInfo.topLayout.setVisibility(View.VISIBLE);
                binding.bloodOxygenChart.setVisibility(View.GONE);
                binding.bloodPressureChart.setVisibility(View.GONE);
                binding.GlucoseChart.setVisibility(View.GONE);
            }else {
                binding.graphInfo.topLayout.setVisibility(View.GONE);
            }
        }
    }

    private static void init_latest_measurements(String date, GraphHistoryBinding binding) {
        SimpleDateFormat current = new SimpleDateFormat("dd MMM yyyy ',' h:mm a ", Locale.ENGLISH);
        if(TextUtils.isEmpty(date)) {
            String latestDate = new SimpleDateFormat("dd MMM yyyy ',' h:mm a ", Locale.US).format(new Date());
            binding.graphInfo.tvDateTime.setText(latestDate);
        }else binding.graphInfo.tvDateTime.setText(current.format(new Date(Long.parseLong(date) * 1000)));
    }


    public static void updateTypeData(String left_value, String right_value, String left_header, String right_header, String left_unit, String right_unit, GraphHistoryBinding binding) {
        binding.graphInfo.tvHeader.setText(left_header);
        binding.graphInfo.tvHeaderValue.setText(left_value);
        binding.graphInfo.tvHeaderUnit.setText(left_unit);
        binding.graphInfo.tvHeaderRight.setText(right_header);
        binding.graphInfo.tvHeaderValueRight.setText(right_value);
        binding.graphInfo.tvHeaderRightUnit.setText(right_unit);

    }

    public static void updateTypeData(String header_value, String header, String unit, GraphHistoryBinding binding) {
        binding.graphInfo.tvHeader.setText(header);
        binding.graphInfo.tvHeaderValue.setText(header_value);
        binding.graphInfo.tvHeaderUnit.setText(unit);
    }


    public static void decorateGraph(GraphHistoryBinding binding) {
        Utility.decorateGraph(binding.bloodPressureChart);
        Utility.decorateGraph(binding.bloodOxygenChart);
        Utility.decorateLineGraph(binding.heartRateChart);
        Utility.decorateGraph(binding.WeightChart);
        Utility.decorateLineGraph(binding.GlucoseChart);
    }
    public static void decorateGraph2(FragmentVitalCheckUiBinding binding) {
        Utility.decorateLineGraph2(binding.bloodPressureChart);
        Utility.decorateLineGraph2(binding.boChart);
        Utility.decorateLineGraph2(binding.heartRateChart);
        Utility.decorateLineGraph2(binding.weightChart);
        Utility.decorateLineGraph2(binding.glucoseChart);
    }
    public static void decorateGraph3(GraphAdapterLayoutBinding binding) {
        Utility.decorateLineGraph2(binding.categoryChart);
    }


    public static void decorateGraph3(FragmentNewVitalCheckUiBinding binding) {
        Utility.decorateLineGraph2(binding.Chart);
    }
}
