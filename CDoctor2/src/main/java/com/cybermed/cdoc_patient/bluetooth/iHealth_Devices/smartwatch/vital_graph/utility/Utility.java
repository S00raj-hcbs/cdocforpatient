package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility;

import static com.cybermed.cdoc_patient.util.AppConstant.BLOOD_OXYGEN;
import static com.cybermed.cdoc_patient.util.AppConstant.BLOOD_PRESSURE;
import static com.cybermed.cdoc_patient.util.AppConstant.CALORIES;
import static com.cybermed.cdoc_patient.util.AppConstant.DISTANCE;
import static com.cybermed.cdoc_patient.util.AppConstant.HEART_RATE;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_BO;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_DAILY;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_HRV;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_TEMP;
import static com.cybermed.cdoc_patient.util.AppConstant.SMART_WATCH;
import static com.cybermed.cdoc_patient.util.AppConstant.STEPS;
import static com.cybermed.cdoc_patient.util.AppConstant.TEMP;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph.IOTGraph;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.data.DataProvider;
import com.cybermed.cdoc_patient.databinding.SmartWatchGraphFragBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.jstyle.blesdk1963.constant.DeviceKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Utility {
    static DataProvider provider;

    public static void initializeData(String json) {
        provider = new DataProvider(json);
    }

    public static void updateGraph(SmartWatchGraphFragBinding binding, String type, String graphType) {
        hideGraphsAccToType(graphType, binding);
        switch (type) {
            case TYPE.WEEKLY: {
                setAppearance(binding.graphDivider.weekBtn, binding.graphDivider.monthBtn, binding.graphDivider.yearBtn);
                if (graphType.equalsIgnoreCase(SMART_WATCH)) {
                    Custom custom = provider.getWeekSteps();
                    BarData data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.stepsChart);
                        updateGraph(binding.stepsChart, data);
                        updateLabels(binding.stepsChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.stepsChart);
                        updateGraph(binding.stepsChart, null);

                    }


                    custom = provider.getWeekDistance();
                    data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.distanceChart);
                        updateGraph(binding.distanceChart, data);
                        updateLabels(binding.distanceChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.distanceChart);
                        updateGraph(binding.distanceChart, null);
                    }


                    custom = provider.getWeekCalories();
                    data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.calorieChart);
                        updateGraph(binding.calorieChart, data);
                        updateLabels(binding.calorieChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.calorieChart);
                        updateGraph(binding.calorieChart, null);
                    }


                    custom = provider.getWeekBloodPressure();
                    data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.bloodPressureChart);
                        updateGraph(binding.bloodPressureChart, data);
                        updateLabels(binding.bloodPressureChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.bloodPressureChart);
                        updateGraph(binding.bloodPressureChart, null);
                    }


                    custom = provider.getWeekBloodOxygen();
                    data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.bloodOxygenChart);
                        updateGraph(binding.bloodOxygenChart, data);
                        updateLabels(binding.bloodOxygenChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.bloodOxygenChart);
                        updateGraph(binding.bloodOxygenChart, null);
                    }


                    Custom_Line custom_line = provider.getWeekTemperature();
                    LineData lineData = custom_line.data;
                    if (lineData.getEntryCount() != 0) {
                        setDataAvailableLineGraph(binding.tempCHart);
                        updateLineGraph(binding.tempCHart, lineData);
                        updateLabelsLineGraph(binding.tempCHart, lineData.getEntryCount(), custom_line.labels);
                    } else {
                        setNoDataAvailableLineGraph(binding.tempCHart);
                        updateLineGraph(binding.tempCHart, null);
                    }


                    custom_line = provider.getWeekHearRate();
                    lineData = custom_line.data;
                    if (lineData.getEntryCount() != 0) {
                        setDataAvailableLineGraph(binding.heartRateChart);
                        updateLineGraph(binding.heartRateChart, lineData);
                        updateLabelsLineGraph(binding.heartRateChart, lineData.getEntryCount(), custom_line.labels);
                    } else {
                        setNoDataAvailableLineGraph(binding.heartRateChart);
                        updateLineGraph(binding.heartRateChart, null);
                    }


                } else if (graphType.equals(STEPS)) {
                    Custom custom = provider.getWeekSteps();
                    BarData data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.stepsChart);
                        updateGraph(binding.stepsChart, data);
                        updateLabels(binding.stepsChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.stepsChart);
                        updateGraph(binding.stepsChart, null);

                    }

                } else if (graphType.equals(DISTANCE)) {
                    Custom custom = provider.getWeekDistance();
                    BarData data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.distanceChart);
                        updateGraph(binding.distanceChart, data);
                        updateLabels(binding.distanceChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.distanceChart);
                        updateGraph(binding.distanceChart, null);
                    }

                } else if (graphType.equals(CALORIES)) {
                    Custom custom = provider.getWeekCalories();
                    BarData data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.calorieChart);
                        updateGraph(binding.calorieChart, data);
                        updateLabels(binding.calorieChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.calorieChart);
                        updateGraph(binding.calorieChart, null);
                    }

                } else if (graphType.equals(BLOOD_PRESSURE)) {
                    Custom custom = provider.getWeekBloodPressure();
                    BarData data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.bloodPressureChart);
                        updateGraph(binding.bloodPressureChart, data);
                        updateLabels(binding.bloodPressureChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.bloodPressureChart);
                        updateGraph(binding.bloodPressureChart, null);
                    }

                } else if (graphType.equals(BLOOD_OXYGEN)) {
                    Custom custom = provider.getWeekBloodOxygen();
                    BarData data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.bloodOxygenChart);
                        updateGraph(binding.bloodOxygenChart, data);
                        updateLabels(binding.bloodOxygenChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.bloodOxygenChart);
                        updateGraph(binding.bloodOxygenChart, null);
                    }

                } else if (graphType.equals(TEMP)) {
                    Custom_Line custom_line = provider.getWeekTemperature();
                    LineData lineData = custom_line.data;
                    if (lineData.getEntryCount() != 0) {
                        setDataAvailableLineGraph(binding.tempCHart);
                        updateLineGraph(binding.tempCHart, lineData);
                        updateLabelsLineGraph(binding.tempCHart, lineData.getEntryCount(), custom_line.labels);
                    } else {
                        setNoDataAvailableLineGraph(binding.tempCHart);
                        updateLineGraph(binding.tempCHart, null);
                    }

                } else if (graphType.equalsIgnoreCase(HEART_RATE)) {
                    Custom_Line custom_line = provider.getWeekHearRate();
                    LineData lineData = custom_line.data;
                    if (lineData.getEntryCount() != 0) {
                        setDataAvailableLineGraph(binding.heartRateChart);
                        updateLineGraph(binding.heartRateChart, lineData);
                        updateLabelsLineGraph(binding.heartRateChart, lineData.getEntryCount(), custom_line.labels);
                    } else {
                        setNoDataAvailableLineGraph(binding.heartRateChart);
                        updateLineGraph(binding.heartRateChart, null);
                    }

                }
                break;

            }
            case TYPE.MONTHLY: {
                setAppearance(binding.graphDivider.monthBtn, binding.graphDivider.weekBtn, binding.graphDivider.yearBtn);
                if (graphType.equalsIgnoreCase(SMART_WATCH)) {
                    Custom custom = provider.getMonthSteps();
                    BarData data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.stepsChart);
                        updateGraph(binding.stepsChart, data);
                        updateLabels(binding.stepsChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.stepsChart);
                        updateGraph(binding.stepsChart, null);
                    }

                    custom = provider.getMonthDistance();
                    data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.distanceChart);
                        updateGraph(binding.distanceChart, data);
                        updateLabels(binding.distanceChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.distanceChart);
                        updateGraph(binding.distanceChart, null);
                    }


                    custom = provider.getMonthCalories();
                    data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.calorieChart);
                        updateGraph(binding.calorieChart, data);
                        updateLabels(binding.calorieChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.calorieChart);
                        updateGraph(binding.calorieChart, null);
                    }

                    custom = provider.getMonthBloodPressure();
                    data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.bloodPressureChart);
                        updateGraph(binding.bloodPressureChart, data);
                        updateLabels(binding.bloodPressureChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.bloodPressureChart);
                        updateGraph(binding.bloodPressureChart, null);
                    }

                    custom = provider.getMonthBloodOxygen();
                    data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.bloodOxygenChart);
                        updateGraph(binding.bloodOxygenChart, data);
                        updateLabels(binding.bloodOxygenChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.bloodOxygenChart);
                        updateGraph(binding.bloodOxygenChart, null);
                    }

                    Custom_Line custom_line = provider.getMonthTemperature();
                    LineData lineData = custom_line.data;
                    if (lineData.getEntryCount() != 0) {
                        setDataAvailableLineGraph(binding.tempCHart);
                        updateLineGraph(binding.tempCHart, lineData);
                        updateLabelsLineGraph(binding.tempCHart, lineData.getEntryCount(), custom_line.labels);
                    } else {
                        setNoDataAvailableLineGraph(binding.tempCHart);
                        updateLineGraph(binding.tempCHart, null);
                    }


                    custom_line = provider.getMonthHearRate();
                    lineData = custom_line.data;
                    if (lineData.getEntryCount() != 0) {
                        setDataAvailableLineGraph(binding.heartRateChart);
                        updateLineGraph(binding.heartRateChart, lineData);
                        updateLabelsLineGraph(binding.heartRateChart, lineData.getEntryCount(), custom_line.labels);
                    } else {
                        setNoDataAvailableLineGraph(binding.heartRateChart);
                        updateLineGraph(binding.heartRateChart, null);
                    }
                } else if (graphType.equals(STEPS)) {
                    Custom custom = provider.getMonthSteps();
                    BarData data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.stepsChart);
                        updateGraph(binding.stepsChart, data);
                        updateLabels(binding.stepsChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.stepsChart);
                        updateGraph(binding.stepsChart, null);
                    }
                } else if (graphType.equals(DISTANCE)) {
                    Custom custom = provider.getMonthDistance();
                    BarData data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.distanceChart);
                        updateGraph(binding.distanceChart, data);
                        updateLabels(binding.distanceChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.distanceChart);
                        updateGraph(binding.distanceChart, null);
                    }
                } else if (graphType.equals(CALORIES)) {
                    Custom custom = provider.getMonthCalories();
                    BarData data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.calorieChart);
                        updateGraph(binding.calorieChart, data);
                        updateLabels(binding.calorieChart, data.getEntryCount(), custom.labels);
                    } else {

                        setNoDataAvailableBarGraph(binding.calorieChart);
                        updateGraph(binding.calorieChart, null);
                    }
                } else if (graphType.equals(BLOOD_PRESSURE)) {
                    Custom custom = provider.getMonthBloodPressure();
                    BarData data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.bloodPressureChart);
                        updateGraph(binding.bloodPressureChart, data);
                        updateLabels(binding.bloodPressureChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.bloodPressureChart);
                        updateGraph(binding.bloodPressureChart, null);
                    }
                } else if (graphType.equals(BLOOD_OXYGEN)) {
                    Custom custom = provider.getMonthBloodOxygen();
                    BarData data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.bloodOxygenChart);
                        updateGraph(binding.bloodOxygenChart, data);
                        updateLabels(binding.bloodOxygenChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.bloodOxygenChart);
                        updateGraph(binding.bloodOxygenChart, null);
                    }
                } else if (graphType.equals(TEMP)) {
                    Custom_Line custom_line = provider.getMonthTemperature();
                    LineData lineData = custom_line.data;
                    if (lineData.getEntryCount() != 0) {
                        setDataAvailableLineGraph(binding.tempCHart);
                        updateLineGraph(binding.tempCHart, lineData);
                        updateLabelsLineGraph(binding.tempCHart, lineData.getEntryCount(), custom_line.labels);
                    } else {
                        setNoDataAvailableLineGraph(binding.tempCHart);
                        updateLineGraph(binding.tempCHart, null);
                    }
                } else if (graphType.equals(HEART_RATE)) {
                    Custom_Line custom_line = provider.getMonthHearRate();
                    LineData lineData = custom_line.data;
                    if (lineData.getEntryCount() != 0) {
                        setDataAvailableLineGraph(binding.heartRateChart);
                        updateLineGraph(binding.heartRateChart, lineData);
                        updateLabelsLineGraph(binding.heartRateChart, lineData.getEntryCount(), custom_line.labels);
                    } else {
                        setNoDataAvailableLineGraph(binding.heartRateChart);
                        updateLineGraph(binding.heartRateChart, null);
                    }
                }

                break;
            }
            case TYPE.YEARLY: {
                setAppearance(binding.graphDivider.yearBtn, binding.graphDivider.monthBtn, binding.graphDivider.weekBtn);
                if (graphType.equalsIgnoreCase(SMART_WATCH)) {
                    Custom custom = provider.getYearSteps();
                    BarData data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.stepsChart);
                        updateGraph(binding.stepsChart, data);
                        updateLabels(binding.stepsChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.stepsChart);
                        updateGraph(binding.stepsChart, null);
                    }


                    custom = provider.getYearDistance();
                    data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.distanceChart);
                        updateGraph(binding.distanceChart, data);
                        updateLabels(binding.distanceChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.distanceChart);
                        updateGraph(binding.distanceChart, null);
                    }


                    custom = provider.getYearCalories();
                    data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.calorieChart);
                        updateGraph(binding.calorieChart, data);
                        updateLabels(binding.calorieChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.calorieChart);
                        updateGraph(binding.calorieChart, null);
                    }


                    custom = provider.getYearBloodPressure();
                    data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.bloodPressureChart);
                        updateGraph(binding.bloodPressureChart, data);
                        updateLabels(binding.bloodPressureChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.bloodPressureChart);
                        updateGraph(binding.bloodPressureChart, null);
                    }


                    custom = provider.getYearBloodOxygen();
                    data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.bloodOxygenChart);
                        updateGraph(binding.bloodOxygenChart, data);
                        updateLabels(binding.bloodOxygenChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.bloodOxygenChart);
                        updateGraph(binding.bloodOxygenChart, null);
                    }

                    Custom_Line custom_line = provider.getYearTemperature();
                    LineData lineData = custom_line.data;
                    if (lineData.getEntryCount() != 0) {
                        setDataAvailableLineGraph(binding.tempCHart);
                        updateLineGraph(binding.tempCHart, lineData);
                        updateLabelsLineGraph(binding.tempCHart, lineData.getEntryCount(), custom_line.labels);
                    } else {
                        setNoDataAvailableLineGraph(binding.tempCHart);
                        updateLineGraph(binding.tempCHart, null);
                    }


                        custom_line = provider.getYearHearRate();
                        lineData = custom_line.data;
                        if (lineData.getEntryCount() != 0) {
                            setDataAvailableLineGraph(binding.heartRateChart);
                            updateLineGraph(binding.heartRateChart, lineData);
                            updateLabelsLineGraph(binding.heartRateChart, lineData.getEntryCount(), custom_line.labels);
                        } else {
                            setNoDataAvailableLineGraph(binding.heartRateChart);
                            updateLineGraph(binding.heartRateChart, null);
                        }

                } else if (graphType.equals(STEPS)) {
                    Custom custom = provider.getYearSteps();
                    BarData data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.stepsChart);
                        updateGraph(binding.stepsChart, data);
                        updateLabels(binding.stepsChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.stepsChart);
                        updateGraph(binding.stepsChart, null);
                    }
                } else if (graphType.equals(DISTANCE)) {
                    Custom custom = provider.getYearDistance();
                    BarData data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.distanceChart);
                        updateGraph(binding.distanceChart, data);
                        updateLabels(binding.distanceChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.distanceChart);
                        updateGraph(binding.distanceChart, null);
                    }
                } else if (graphType.equals(CALORIES)) {
                    Custom custom = provider.getYearCalories();
                    BarData data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.calorieChart);
                        updateGraph(binding.calorieChart, data);
                        updateLabels(binding.calorieChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.calorieChart);
                        updateGraph(binding.calorieChart, null);
                    }
                } else if (graphType.equals(BLOOD_PRESSURE)) {
                    Custom custom = provider.getYearBloodPressure();
                    BarData data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.bloodPressureChart);
                        updateGraph(binding.bloodPressureChart, data);
                        updateLabels(binding.bloodPressureChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.bloodPressureChart);
                        updateGraph(binding.bloodPressureChart, null);
                    }
                } else if (graphType.equals(BLOOD_OXYGEN)) {
                    Custom custom = provider.getYearBloodOxygen();
                    BarData data = custom.data;
                    if (data.getEntryCount() != 0) {
                        setDataAvailableBarGraph(binding.bloodOxygenChart);
                        updateGraph(binding.bloodOxygenChart, data);
                        updateLabels(binding.bloodOxygenChart, data.getEntryCount(), custom.labels);
                    } else {
                        setNoDataAvailableBarGraph(binding.bloodOxygenChart);
                        updateGraph(binding.bloodOxygenChart, null);
                    }
                } else if (graphType.equals(TEMP)) {
                    Custom_Line custom_line = provider.getYearTemperature();
                    LineData lineData = custom_line.data;
                    if (lineData.getEntryCount() != 0) {
                        setDataAvailableLineGraph(binding.tempCHart);
                        updateLineGraph(binding.tempCHart, lineData);
                        updateLabelsLineGraph(binding.tempCHart, lineData.getEntryCount(), custom_line.labels);
                    } else {

                        setNoDataAvailableLineGraph(binding.tempCHart);
                        updateLineGraph(binding.tempCHart, null);
                    }
                } else if (graphType.equals(HEART_RATE)) {

                        Custom_Line custom_line = provider.getYearHearRate();
                        LineData lineData = custom_line.data;
                        if (lineData.getEntryCount() != 0) {
                            setDataAvailableLineGraph(binding.heartRateChart);
                            updateLineGraph(binding.heartRateChart, lineData);
                            updateLabelsLineGraph(binding.heartRateChart, lineData.getEntryCount(), custom_line.labels);
                        } else {

                            setNoDataAvailableLineGraph(binding.heartRateChart);
                            updateLineGraph(binding.heartRateChart, null);
                        }

                }

                break;
            }
        }
    }

    public static void updateGraph(BarChart barChart, BarData data) {
        barChart.setData(data);
        barChart.invalidate();
        if (data != null) {
            data.setHighlightEnabled(false);
            data.setBarWidth(0.9f);
            barChart.setVisibleXRangeMaximum(7);
            barChart.setVisibleXRangeMinimum(7);
            barChart.moveViewToX(data.getEntryCount()-7);
        }

    }

    public static void updateGraph2(BarChart barChart, BarData data) {
        barChart.setData(data);
        barChart.invalidate();
        if (data != null) {
            data.setHighlightEnabled(false);
            data.setBarWidth(0.8f);
            barChart.setVisibleXRangeMaximum(5);
            barChart.setVisibleXRangeMinimum(5);
            barChart.moveViewToX(data.getEntryCount()-5);
            /*barChart.moveViewToAnimated(-1, barChart.getYChartMax(), YAxis.AxisDependency.LEFT, 1000);
            barChart.getAxisLeft().setAxisMinimum(0f);
            barChart.getXAxis().setAxisMinimum(-0.455f);*/
        }

    }


    public static void updateLineGraph(LineChart lineChart, LineData data) {
        lineChart.setData(data);
        lineChart.invalidate();
        if (data != null) {
            data.setHighlightEnabled(false);
            lineChart.setVisibleXRangeMaximum(10);
            lineChart.setVisibleXRangeMinimum(10);
            lineChart.moveViewToX(data.getEntryCount()-10);
        }
    }

    public static void updateLineGraphNew(LineChart lineChart, LineData data) {
        lineChart.setData(data);
        lineChart.invalidate();
        if (data != null) {
            data.setHighlightEnabled(false);
            lineChart.setVisibleXRangeMaximum(5);
            lineChart.setVisibleXRangeMinimum(5);
            lineChart.moveViewToX(data.getEntryCount()-5);
            /*lineChart.moveViewToX(-1);
            lineChart.moveViewToAnimated(-1, lineChart.getYChartMax(), YAxis.AxisDependency.LEFT, 1000);*/
            //lineChart.getAxisLeft().setAxisMinimum(0f);
        }
    }

    public static void updateLineGraph2(LineChart lineChart, LineData data) {
        lineChart.setData(data);
        lineChart.invalidate();
        if (data != null) {
            data.setHighlightEnabled(false);
            lineChart.setVisibleXRangeMaximum(5);
            lineChart.setVisibleXRangeMinimum(5);
            lineChart.moveViewToX(data.getEntryCount()-5);
        }
    }


    public static void decorateGraph(SmartWatchGraphFragBinding binding) {
        decorateGraph(binding.stepsChart);
        decorateGraph(binding.distanceChart);
        decorateGraph(binding.calorieChart);
        decorateGraph(binding.bloodPressureChart);
        decorateGraph(binding.bloodOxygenChart);
        decorateLineGraph(binding.tempCHart);
        decorateLineGraph(binding.heartRateChart);
    }

    public static void decorateGraph(BarChart barChart) {
        barChart.zoomOut();
        barChart.getXAxis().setTextColor(R.color.dark_slate_blue);
        if(IOTGraph.isDeviceTablet){
            barChart.getXAxis().setTextSize(12f);
        }else
            barChart.getXAxis().setTextSize(7f);
        barChart.getXAxis().setDrawAxisLine(true);
        barChart.getXAxis().setEnabled(true);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false); // Hide left Y-axis grid lines
        barChart.getAxisRight().setDrawGridLines(false); // Hide right Y-axis grid lines
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.setDrawValueAboveBar(false);
        barChart.getAxisLeft().setEnabled(true);
        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getLegend().setEnabled(true);
        barChart.getXAxis().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color

        //barChart.setVisibleXRangeMaximum(2f);
        barChart.setTouchEnabled(true);
        barChart.getXAxis().setSpaceMax(0.4f);

    }

    public static void decorateLineGraph(LineChart lineChart) {
        lineChart.zoomOut();
        lineChart.getXAxis().setTextColor(R.color.dark_slate_blue);
        if(IOTGraph.isDeviceTablet){
            lineChart.getXAxis().setTextSize(12f);
        }else
            lineChart.getXAxis().setTextSize(7f);
        lineChart.getXAxis().setDrawAxisLine(true);
        lineChart.getXAxis().setEnabled(true);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false); // Hide left Y-axis grid lines
        lineChart.getAxisRight().setDrawGridLines(false); // Hide right Y-axis grid lines
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisLeft().setEnabled(true);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.getXAxis().setGranularity(1f);
        lineChart.getXAxis().setGranularityEnabled(true);
        lineChart.getLegend().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.getXAxis().setTextColor(Color.parseColor("#000000"));
        lineChart.getXAxis().setSpaceMax(0.4f);

    }

    public static void decorateLineGraph2(LineChart lineChart) {
        lineChart.zoomOut();
        lineChart.getXAxis().setTextColor(R.color.dark_slate_blue);
        if(IOTGraph.isDeviceTablet){
            lineChart.getXAxis().setTextSize(12f);
        }else
            lineChart.getXAxis().setTextSize(9f);
        lineChart.getXAxis().setEnabled(true);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setLabelRotationAngle(-25f);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisLeft().setEnabled(true);
        lineChart.getAxisLeft().setDrawGridLines(false); // Hide left Y-axis grid lines
        lineChart.getAxisRight().setDrawGridLines(false); // Hide right Y-axis grid lines
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.getXAxis().setDrawAxisLine(true);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.getXAxis().setGranularity(1f);
        lineChart.getXAxis().setGranularityEnabled(true);
        lineChart.getLegend().setEnabled(false);
        lineChart.setTouchEnabled(true);
        //lineChart.getXAxis().setSpaceMin(0.4f);
        lineChart.getXAxis().setSpaceMax(0.4f);
        lineChart.setExtraOffsets(10f, 10f, 10f, 20f);
        Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
        lineChart.getXAxis().setTypeface(boldTypeface);
        lineChart.getAxisLeft().setTypeface(boldTypeface);
    }

    public static float convertCelsiusToFahrenheit(float celsius) {
        return ((celsius * 9) / 5) + 32;
    }

    public static double convertKmsToMiles(float kms) {
        return 0.621371 * kms;
    }

    public static void updateLabels(BarChart chart, int count, String[] labels) {
        if (count <= 0)
            return;
        chart.getXAxis().setLabelCount(count);
        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter();
        formatter.setValues(labels);
        chart.getXAxis().setValueFormatter(formatter);
    }

    public static void updateLabelsLineGraph(LineChart chart, int count, String[] labels) {
        if (count <= 0)
            return;
        chart.getXAxis().setLabelCount(count);
        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter();
        formatter.setValues(labels);
        chart.getXAxis().setValueFormatter(formatter);
    }

    public static void setNoDataAvailableBarGraph(BarChart barChart) {
        barChart.setVisibility(View.VISIBLE);
        barChart.setNoDataText(/*"No information is available to show the history"*/ContextCompat.getString(barChart.getContext(),R.string.no_information_is_available_to_show_the_history));
    }

    public static void setDataAvailableBarGraph(BarChart barChart) {
        barChart.setVisibility(View.VISIBLE);
    }

    public static void setNoDataAvailableLineGraph(LineChart chart) {
        chart.setVisibility(View.VISIBLE);
        chart.setNoDataText(/*"No information is available to show the history"*/ContextCompat.getString(chart.getContext(),R.string.no_information_is_available_to_show_the_history));
    }


    public static void setDataAvailableLineGraph(LineChart chart) {
        chart.setVisibility(View.VISIBLE);
    }

    public static void hideGraphsAccToType(String graph_type, SmartWatchGraphFragBinding binding) {
        switch (graph_type) {
            case SMART_WATCH -> {
                binding.tvStepUnit.setVisibility(View.VISIBLE);
                binding.stepsChart.setVisibility(View.VISIBLE);
                binding.tvDistanceUnit.setVisibility(View.VISIBLE);
                binding.distanceChart.setVisibility(View.VISIBLE);
                binding.tvCalorieUnit.setVisibility(View.VISIBLE);
                binding.calorieChart.setVisibility(View.VISIBLE);
                binding.tvBpUnit.setVisibility(View.VISIBLE);
                binding.bloodPressureChart.setVisibility(View.VISIBLE);
                binding.tvBoUnit.setVisibility(View.VISIBLE);
                binding.bloodOxygenChart.setVisibility(View.VISIBLE);
                binding.tvTempUnit.setVisibility(View.VISIBLE);
                binding.tempCHart.setVisibility(View.VISIBLE);
                binding.tvHrUnit.setVisibility(View.VISIBLE);
                binding.heartRateChart.setVisibility(View.VISIBLE);
                binding.cardGraphDetails.setVisibility(View.GONE);
            }
            case STEPS -> {
                binding.tvStepUnit.setVisibility(View.VISIBLE);
                binding.cardGraphDetails.setVisibility(View.VISIBLE);
                binding.stepsChart.setVisibility(View.VISIBLE);
                binding.tvDistanceUnit.setVisibility(View.GONE);
                binding.distanceChart.setVisibility(View.GONE);
                binding.tvCalorieUnit.setVisibility(View.GONE);
                binding.calorieChart.setVisibility(View.GONE);
                binding.tvBpUnit.setVisibility(View.GONE);
                binding.bloodPressureChart.setVisibility(View.GONE);
                binding.tvBoUnit.setVisibility(View.GONE);
                binding.bloodOxygenChart.setVisibility(View.GONE);
                binding.tvTempUnit.setVisibility(View.GONE);
                binding.tempCHart.setVisibility(View.GONE);
                binding.tvHrUnit.setVisibility(View.GONE);
                binding.heartRateChart.setVisibility(View.GONE);
            }
            case DISTANCE -> {
                binding.cardGraphDetails.setVisibility(View.VISIBLE);
                binding.tvStepUnit.setVisibility(View.GONE);
                binding.stepsChart.setVisibility(View.GONE);
                binding.tvDistanceUnit.setVisibility(View.VISIBLE);
                binding.distanceChart.setVisibility(View.VISIBLE);
                binding.tvCalorieUnit.setVisibility(View.GONE);
                binding.calorieChart.setVisibility(View.GONE);
                binding.tvBpUnit.setVisibility(View.GONE);
                binding.bloodPressureChart.setVisibility(View.GONE);
                binding.tvBoUnit.setVisibility(View.GONE);
                binding.bloodOxygenChart.setVisibility(View.GONE);
                binding.tvTempUnit.setVisibility(View.GONE);
                binding.tempCHart.setVisibility(View.GONE);
                binding.tvHrUnit.setVisibility(View.GONE);
                binding.heartRateChart.setVisibility(View.GONE);
            }
            case CALORIES -> {
                binding.cardGraphDetails.setVisibility(View.VISIBLE);
                binding.tvStepUnit.setVisibility(View.GONE);
                binding.stepsChart.setVisibility(View.GONE);
                binding.tvDistanceUnit.setVisibility(View.GONE);
                binding.distanceChart.setVisibility(View.GONE);
                binding.tvCalorieUnit.setVisibility(View.VISIBLE);
                binding.calorieChart.setVisibility(View.VISIBLE);
                binding.tvBpUnit.setVisibility(View.GONE);
                binding.bloodPressureChart.setVisibility(View.GONE);
                binding.tvBoUnit.setVisibility(View.GONE);
                binding.bloodOxygenChart.setVisibility(View.GONE);
                binding.tvTempUnit.setVisibility(View.GONE);
                binding.tempCHart.setVisibility(View.GONE);
                binding.tvHrUnit.setVisibility(View.GONE);
                binding.heartRateChart.setVisibility(View.GONE);
            }
            case BLOOD_PRESSURE -> {
                binding.cardGraphDetails.setVisibility(View.VISIBLE);
                binding.tvStepUnit.setVisibility(View.GONE);
                binding.stepsChart.setVisibility(View.GONE);
                binding.tvDistanceUnit.setVisibility(View.GONE);
                binding.distanceChart.setVisibility(View.GONE);
                binding.tvCalorieUnit.setVisibility(View.GONE);
                binding.calorieChart.setVisibility(View.GONE);
                binding.tvBpUnit.setVisibility(View.VISIBLE);
                binding.bloodPressureChart.setVisibility(View.VISIBLE);
                binding.tvBoUnit.setVisibility(View.GONE);
                binding.bloodOxygenChart.setVisibility(View.GONE);
                binding.tvTempUnit.setVisibility(View.GONE);
                binding.tempCHart.setVisibility(View.GONE);
                binding.tvHrUnit.setVisibility(View.GONE);
                binding.heartRateChart.setVisibility(View.GONE);
            }
            case BLOOD_OXYGEN -> {
                binding.cardGraphDetails.setVisibility(View.VISIBLE);
                binding.tvStepUnit.setVisibility(View.GONE);
                binding.stepsChart.setVisibility(View.GONE);
                binding.tvDistanceUnit.setVisibility(View.GONE);
                binding.distanceChart.setVisibility(View.GONE);
                binding.tvCalorieUnit.setVisibility(View.GONE);
                binding.calorieChart.setVisibility(View.GONE);
                binding.tvBpUnit.setVisibility(View.GONE);
                binding.bloodPressureChart.setVisibility(View.GONE);
                binding.tvBoUnit.setVisibility(View.VISIBLE);
                binding.bloodOxygenChart.setVisibility(View.VISIBLE);
                binding.tvTempUnit.setVisibility(View.GONE);
                binding.tempCHart.setVisibility(View.GONE);
                binding.tvHrUnit.setVisibility(View.GONE);
                binding.heartRateChart.setVisibility(View.GONE);
            }
            case TEMP -> {
                binding.cardGraphDetails.setVisibility(View.VISIBLE);
                binding.tvStepUnit.setVisibility(View.GONE);
                binding.stepsChart.setVisibility(View.GONE);
                binding.tvDistanceUnit.setVisibility(View.GONE);
                binding.distanceChart.setVisibility(View.GONE);
                binding.tvCalorieUnit.setVisibility(View.GONE);
                binding.calorieChart.setVisibility(View.GONE);
                binding.tvBpUnit.setVisibility(View.GONE);
                binding.bloodPressureChart.setVisibility(View.GONE);
                binding.tvBoUnit.setVisibility(View.GONE);
                binding.bloodOxygenChart.setVisibility(View.GONE);
                binding.tvTempUnit.setVisibility(View.VISIBLE);
                binding.tempCHart.setVisibility(View.VISIBLE);
                binding.tvHrUnit.setVisibility(View.GONE);
                binding.heartRateChart.setVisibility(View.GONE);
            }
            case HEART_RATE -> {
                binding.cardGraphDetails.setVisibility(View.VISIBLE);
                binding.tvStepUnit.setVisibility(View.GONE);
                binding.stepsChart.setVisibility(View.GONE);
                binding.tvDistanceUnit.setVisibility(View.GONE);
                binding.distanceChart.setVisibility(View.GONE);
                binding.tvCalorieUnit.setVisibility(View.GONE);
                binding.calorieChart.setVisibility(View.GONE);
                binding.tvBpUnit.setVisibility(View.GONE);
                binding.bloodPressureChart.setVisibility(View.GONE);
                binding.tvBoUnit.setVisibility(View.GONE);
                binding.bloodOxygenChart.setVisibility(View.GONE);
                binding.tvTempUnit.setVisibility(View.GONE);
                binding.tempCHart.setVisibility(View.GONE);
                binding.tvHrUnit.setVisibility(View.VISIBLE);
                binding.heartRateChart.setVisibility(View.VISIBLE);
            }
        }
    }

    public static void setAppearance(Button selected, Button unselected1, Button unselected2) {
        selected.setTextAppearance(R.style.selectedButton);
        selected.setSelected(true);
        unselected1.setTextAppearance(R.style.unselectedButton);
        unselected1.setSelected(false);
        unselected2.setTextAppearance(R.style.unselectedButton);
        unselected2.setSelected(false);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public static void setDataToUi(HashMap<String, List<Map<String, String>>> hashMap, String graph_type, SmartWatchGraphFragBinding binding) {
        if (hashMap != null) {
            switch (graph_type) {
                case STEPS, DISTANCE, CALORIES -> {
                    if (hashMap.get(SMART_DAILY) != null && !Objects.requireNonNull(hashMap.get(SMART_DAILY)).isEmpty()) {
                        binding.cardGraphDetails.setVisibility(View.VISIBLE);
                        binding.mainHeader.setVisibility(View.VISIBLE);
                        binding.mainHeaderUnit.setVisibility(View.VISIBLE);
                        binding.leftHeader.setVisibility(View.VISIBLE);
                        binding.rightHeader.setVisibility(View.VISIBLE);
                        binding.mainHeader.setText(Objects.requireNonNull(hashMap.get(SMART_DAILY)).get(0).get(DeviceKey.Step));
                        binding.mainHeaderUnit.setText("Step Count");
                        binding.leftHeader.setText(Objects.requireNonNull(hashMap.get(SMART_DAILY)).get(0).get(DeviceKey.Calories) + " Kcal");
                        binding.rightHeader.setText(String.format("%.1f", Utility.convertKmsToMiles(Float.parseFloat(
                                Objects.requireNonNull(hashMap.get(SMART_DAILY)).get(0).get(DeviceKey.Distance)))) + " Mile");
                        binding.mainHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.sw_footsteps), null, null, null);
                        binding.leftHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.sw_calories), null, null, null);
                        binding.rightHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.sw_distance), null, null, null);
                    }
                }
                case BLOOD_PRESSURE, HEART_RATE -> {
                    if (hashMap.get(SMART_HRV) != null && !Objects.requireNonNull(hashMap.get(SMART_HRV)).isEmpty()) {
                        binding.cardGraphDetails.setVisibility(View.VISIBLE);
                        binding.mainHeader.setVisibility(View.VISIBLE);
                        binding.mainHeaderUnit.setVisibility(View.VISIBLE);
                        binding.leftHeader.setVisibility(View.VISIBLE);
                        binding.mainHeader.setText(Objects.requireNonNull(hashMap.get(SMART_HRV)).get(0).get(DeviceKey.highBP) + "/" + Objects.requireNonNull(hashMap.get(SMART_HRV)).get(0).get(DeviceKey.lowBP));
                        binding.mainHeaderUnit.setText("mmHg");
                        binding.leftHeader.setText(Objects.requireNonNull(hashMap.get(SMART_HRV)).get(0).get(DeviceKey.HRV) + "bpm");
                        if (graph_type.equals(BLOOD_PRESSURE)) {
                            binding.mainHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                    ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.blood_pressure_ic), null, null, null);
                        } else {
                            binding.mainHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                    ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.sw_pulse), null, null, null);
                        }
                    }
                }
                case BLOOD_OXYGEN -> {
                    if (hashMap.get(SMART_BO) != null && !Objects.requireNonNull(hashMap.get(SMART_BO)).isEmpty()) {
                        binding.cardGraphDetails.setVisibility(View.VISIBLE);
                        binding.mainHeader.setVisibility(View.VISIBLE);
                        binding.mainHeaderUnit.setVisibility(View.VISIBLE);
                        binding.mainHeader.setText(Objects.requireNonNull(hashMap.get(SMART_BO)).get(0).get(DeviceKey.Blood_oxygen));
                        binding.mainHeaderUnit.setText("%");
                        binding.mainHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.sw_bloodoxy), null, null, null);
                    }
                }
                case TEMP -> {
                    if (hashMap.get(SMART_TEMP) != null && !Objects.requireNonNull(hashMap.get(SMART_TEMP)).isEmpty()) {
                        binding.cardGraphDetails.setVisibility(View.VISIBLE);
                        binding.mainHeader.setVisibility(View.VISIBLE);
                        binding.mainHeaderUnit.setVisibility(View.VISIBLE);
                        binding.mainHeader.setText(String.format("%.1f",
                                (double) Utility.convertCelsiusToFahrenheit(Float.parseFloat(
                                        Objects.requireNonNull(hashMap.get(SMART_TEMP)).get(0).get(DeviceKey.temperature)))));
                        binding.mainHeaderUnit.setText("°F");
                        binding.mainHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.sw_thermometer), null, null, null);
                    }
                }
            }

        }
    }
}
