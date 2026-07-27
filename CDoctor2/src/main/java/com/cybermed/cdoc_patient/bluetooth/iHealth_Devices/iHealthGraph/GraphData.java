package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.iHealthGraph;

import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TextView;

import com.cdfortis.datainterface.soap.model.Monitor_BO;
import com.cdfortis.datainterface.soap.model.Monitor_Glucose;
import com.cdfortis.datainterface.soap.model.Monitor_HR;
import com.cdfortis.datainterface.soap.model.Monitor_Weight;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Custom;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Custom_Line;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Custom_LineBP;
import com.cybermed.cdoc_patient.me.vitalcheck.model.VitalDataBP;
import com.cybermed.cdoc_patient.util.AppConstant;
import com.cybermed.cdoc_patient.util.DateUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

public class GraphData<T> {
    String[] timeStamp;
    String[] bp_timeStamp;

    Vector<Bp_data> bp_dataVector;
    Vector<Monitor_HR> hr_dataVector;

    private Vector<Monitor_Weight> weight_dataVector;
    private Vector<Monitor_BO> bo_dataVector;
    private Vector<Monitor_Glucose> glucose_dataVector;


    public Vector<Monitor_Glucose> removeGlucoseDuplicateValues(Vector<Monitor_Glucose> vector) {
        timeStamp = new String[vector.size()];
        HashMap<String, Monitor_Glucose> map = new HashMap<>();
        glucose_dataVector = new Vector<>();
        bp_timeStamp = new String[vector.size()];
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        SimpleDateFormat current = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
        Date inputDate = null;
        for (int i = 0; i < vector.size(); i++) {
            long time = Long.parseLong(vector.get(i).Glucose_timestamp);
            Date date = new Date(time * 1000);
            timeStamp[i] = String.valueOf(date);

            try {
                inputDate = format.parse(timeStamp[i]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (inputDate != null) {
                String date_value = current.format(inputDate);

                map.put(date_value, vector.get(i));
            }
        }
        for (Map.Entry<String, Monitor_Glucose> m : map.entrySet()) {
            glucose_dataVector.add(m.getValue());
        }
        return glucose_dataVector;
    }

    public Vector<Monitor_BO> removeBODuplicatevalue(Vector<Monitor_BO> vector) {
        timeStamp = new String[vector.size()];
        HashMap<String, Monitor_BO> map = new HashMap<>();
        bo_dataVector = new Vector<>();
        bp_timeStamp = new String[vector.size()];
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        SimpleDateFormat current = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
        Date inputDate = null;
        for (int i = 0; i < vector.size(); i++) {
            long time = Long.parseLong(vector.get(i).BO_timestamp);
            Date date = new Date(time * 1000);
            timeStamp[i] = String.valueOf(date);

            try {
                inputDate = format.parse(timeStamp[i]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (inputDate != null) {
                String date_value = current.format(inputDate);

                map.put(date_value, vector.get(i));
            }
        }
        for (Map.Entry<String, Monitor_BO> m : map.entrySet()) {
            bo_dataVector.add(m.getValue());
        }
        return bo_dataVector;
    }

    public Vector<Monitor_Weight> removeWeightDuplicateValues(Vector<Monitor_Weight> vector) {
        timeStamp = new String[vector.size()];
        HashMap<String, Monitor_Weight> map = new HashMap<>();
        weight_dataVector = new Vector<>();
        bp_timeStamp = new String[vector.size()];
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        SimpleDateFormat current = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
        Date inputDate = null;
        for (int i = 0; i < vector.size(); i++) {
            long time = Long.parseLong(vector.get(i).weight_timestamp);
            Date date = new Date(time * 1000);
            timeStamp[i] = String.valueOf(date);

            try {
                inputDate = format.parse(timeStamp[i]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (inputDate != null) {
                String date_value = current.format(inputDate);

                map.put(date_value, vector.get(i));
            }
        }
        for (Map.Entry<String, Monitor_Weight> m : map.entrySet()) {
            weight_dataVector.add(m.getValue());
        }
        return weight_dataVector;
    }

    public Vector<Monitor_HR> removeHrDuplicateValues(Vector<Monitor_HR> vector) {
        timeStamp = new String[vector.size()];
        HashMap<String, Monitor_HR> map = new HashMap<>();
        hr_dataVector = new Vector<>();
        bp_timeStamp = new String[vector.size()];
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        SimpleDateFormat current = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
        Date inputDate = null;
        for (int i = 0; i < vector.size(); i++) {
            long time = Long.parseLong(vector.get(i).HR_timestamp);
            Date date = new Date(time * 1000);
            timeStamp[i] = String.valueOf(date);

            try {
                inputDate = format.parse(timeStamp[i]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (inputDate != null) {
                String date_value = current.format(inputDate);

                map.put(date_value, vector.get(i));
            }
        }
        for (Map.Entry<String, Monitor_HR> m : map.entrySet()) {
            hr_dataVector.add(m.getValue());
        }
        return hr_dataVector;
    }

    public Vector<Bp_data> removeDuplicateValue(Vector<Bp_data> vector) {
        timeStamp = new String[vector.size()];
        HashMap<String, Bp_data> map = new HashMap<>();
        bp_dataVector = new Vector<>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        SimpleDateFormat current = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
        Date inputDate = null;
        for (int i = 0; i < vector.size(); i++) {
            long time = Long.parseLong(vector.get(i).BP_timestamp);
            Date date = new Date(time * 1000);
            timeStamp[i] = String.valueOf(date);

            try {
                inputDate = format.parse(timeStamp[i]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (inputDate != null) {
                String date_value = current.format(inputDate);

                map.put(date_value, vector.get(i));
            }
        }
        for (Map.Entry<String, Bp_data> m : map.entrySet()) {
            bp_dataVector.add(m.getValue());
        }
        return bp_dataVector;
    }

    public Custom_Line getDailyLineData(String[] datapoints, int size, int[] thisColors, String[] timeStamp_value,LineChart lineChart) {
        ArrayList<Entry> lineEntries = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        SimpleDateFormat format2 = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        String formattedDate = format2.format(calendar.getTime());

        Date currentDate2 = new Date();

        Double max=0.0;
        Double min= Double.parseDouble(datapoints[0]);
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            try {
                String days2=format2.format(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                if(formattedDate.equalsIgnoreCase(days2)){
                    Double value= Double.parseDouble(datapoints[i]);
                    if (value>max){
                        max=value;
                    }
                    if (value<min){
                        min=value;
                    }
                    lineEntries.add(new Entry(i,
                            Float.parseFloat(datapoints[i])));
                    labels.add(format2.format(format.parse(timeStamp_value[i])));
                }

                /*if (days1.equalsIgnoreCase(days2)){
                    SimpleDateFormat newFormat = new SimpleDateFormat("EE dd");
                    lineEntries.add(new Entry(i,
                            Float.parseFloat(datapoints[i])));
                    labels.add(newFormat.format(days.get(j)));
                }*/
             /*   if (format.parse(timeStamp_value[i]) != null) {
                    Date  arrayDate = format.parse(timeStamp_value[i]);*/
                    /*calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                    int inputMonth = calendar.get(Calendar.MONTH);
                    int inputYear = calendar.get(Calendar.YEAR);*/

                    /*
                            if difference between current date and the date is less than or equal to 7
                     */
                    /*if (isSameDay(currentDate2, arrayDate)) {
                        lineEntries.add(new Entry(i,
                                Float.parseFloat(datapoints[i])));
                        labels.add(formattedDate);
                    }*/
               // }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        fixLineIndices(lineEntries);

        String[] label_values = new String[labels.size()];
        label_values = labels.toArray(label_values);
        LineDataSet dataSet = new LineDataSet(lineEntries, "");
        dataSet.setColors(thisColors[0]);
        dataSet.setCircleColor(thisColors[0]);
        dataSet.enableDashedLine(10f, 5f, 0f);
        dataSet.setColor(Color.rgb(244, 117, 117));
       // dataSet.setLineWidth(2f);
        dataSet.setLineWidth(1f); // Adjust line width as needed
        dataSet.setCircleRadius(3f); // Adjust circle size as needed
        dataSet.setDrawCircleHole(false);
        dataSet.setFillAlpha(255);
        dataSet.setDrawFilled(true);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        //dataSet.setFillDrawable(ContextCompat.getDrawable(lineChart.getContext(),R.drawable.gradient_bg2));

        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
       // dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                // change the return value here to better understand the effect
                // return 600;
                return lineChart.getAxisLeft().getAxisMinimum();
            }
        });
        int dataCount = lineEntries.size();
        if (dataCount > 1) {
            lineChart.getXAxis().setAxisMinimum(lineEntries.get(0).getX()); // Minimum x-value
            lineChart.getXAxis().setAxisMaximum(lineEntries.get(dataCount - 1).getX()); // Maximum x-value
        } else if (dataCount == 1) {
            // Handle case with a single data point if needed
            // For example, set a reasonable range around the single point
            float singleXValue = lineEntries.get(0).getX();
            lineChart.getXAxis().setAxisMinimum(singleXValue - 2f);
            lineChart.getXAxis().setAxisMaximum(singleXValue + 0.5f);
        }
        lineChart.getAxisLeft().setTextColor(Color.parseColor("#000000")); // Change left y-axis label color
        lineChart.getAxisRight().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color
        lineChart.getXAxis().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color
        lineChart.getAxisLeft().setAxisMaximum((float)(max+20));
        lineChart.getAxisLeft().setAxisMinimum((float)(min-20));
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));

        return new Custom_Line(new LineData(dataSet), label_values);
    }

    public Custom_Line getDailyLineData(String[] datapoints, int size, int[] thisColors, String[] timeStamp_value,LineChart lineChart, TextView textView,String type) {
        ArrayList<Entry> lineEntries = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        SimpleDateFormat format2 = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
        int l=0;
        Double sum=0.0;
        Calendar calendar = Calendar.getInstance();
        String formattedDate = format2.format(calendar.getTime());

        Date currentDate2 = new Date();

        Double max=0.0;
        Double min= Double.parseDouble(datapoints[0]);
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            try {
                if (timeStamp_value[i] != null && !timeStamp_value[i].trim().isEmpty()) {
                    String days2=format2.format(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    if (formattedDate.equalsIgnoreCase(days2)) {
                        Double value = Double.parseDouble(datapoints[i]);
                        if (value > max) {
                            max = value;
                        }
                        if (value < min) {
                            min = value;
                        }
                        l = l + 1;
                        sum = sum + Double.parseDouble(datapoints[i]);
                        lineEntries.add(new Entry(i,
                                Float.parseFloat(datapoints[i])));
                        labels.add(format2.format(format.parse(timeStamp_value[i])));
                    }
                }


                /*if (days1.equalsIgnoreCase(days2)){
                    SimpleDateFormat newFormat = new SimpleDateFormat("EE dd");
                    lineEntries.add(new Entry(i,
                            Float.parseFloat(datapoints[i])));
                    labels.add(newFormat.format(days.get(j)));
                }*/
             /*   if (format.parse(timeStamp_value[i]) != null) {
                    Date  arrayDate = format.parse(timeStamp_value[i]);*/
                    /*calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                    int inputMonth = calendar.get(Calendar.MONTH);
                    int inputYear = calendar.get(Calendar.YEAR);*/

                    /*
                            if difference between current date and the date is less than or equal to 7
                     */
                    /*if (isSameDay(currentDate2, arrayDate)) {
                        lineEntries.add(new Entry(i,
                                Float.parseFloat(datapoints[i])));
                        labels.add(formattedDate);
                    }*/
                // }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (type.equals("hr")){
            textView.setText(l>0? AppConstant.getdataHRtextColor(Double.parseDouble(String.valueOf(sum/l)),textView):"--");
            if (textView.getText().toString().equals("--")){
                textView.setTextColor(Color.GRAY);
            }
        }else if (type.equals("glucose")){
            textView.setText(l>0? AppConstant.getdataGlucosetextColor(Double.parseDouble(String.valueOf(sum/l)),textView):"--");
            if (textView.getText().toString().equals("--")){
                textView.setTextColor(Color.GRAY);
            }
        }else if (type.equals("pulse")){
            textView.setText(l>0? AppConstant.getdataOxitextColor(Double.parseDouble(String.valueOf(sum/l)),textView):"--");
            if (textView.getText().toString().equals("--")){
                textView.setTextColor(Color.GRAY);
            }
        }else if (type.equals("weight")){
            textView.setText(l>0? String.valueOf(sum/l):"--");
            if (textView.getText().toString().equals("--")){
                textView.setTextColor(Color.GRAY);
            }else {
                textView.setTextColor(Color.BLUE);
            }
        }
        fixLineIndices(lineEntries);

        String[] label_values = new String[labels.size()];
        label_values = labels.toArray(label_values);
        LineDataSet dataSet = new LineDataSet(lineEntries, "");
        dataSet.setColors(thisColors[0]);
        dataSet.setCircleColor(thisColors[0]);
        dataSet.enableDashedLine(10f, 5f, 0f);
        dataSet.setColor(Color.rgb(244, 117, 117));
        // dataSet.setLineWidth(2f);
        dataSet.setLineWidth(1f); // Adjust line width as needed
        dataSet.setCircleRadius(3f); // Adjust circle size as needed
        dataSet.setDrawCircleHole(false);
        dataSet.setFillAlpha(255);
        dataSet.setDrawFilled(true);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        //dataSet.setFillDrawable(ContextCompat.getDrawable(lineChart.getContext(),R.drawable.gradient_bg2));

        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        // dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                // change the return value here to better understand the effect
                // return 600;
                return lineChart.getAxisLeft().getAxisMinimum();
            }
        });
        int dataCount = lineEntries.size();
        if (dataCount > 1) {
            lineChart.getXAxis().setAxisMinimum(lineEntries.get(0).getX()); // Minimum x-value
            lineChart.getXAxis().setAxisMaximum(lineEntries.get(dataCount - 1).getX()); // Maximum x-value
        } else if (dataCount == 1) {
            // Handle case with a single data point if needed
            // For example, set a reasonable range around the single point
            float singleXValue = lineEntries.get(0).getX();
            lineChart.getXAxis().setAxisMinimum(singleXValue - 2f);
            lineChart.getXAxis().setAxisMaximum(singleXValue + 0.5f);
        }
        lineChart.getAxisLeft().setTextColor(Color.parseColor("#000000")); // Change left y-axis label color
        lineChart.getAxisRight().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color
        lineChart.getXAxis().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color
        lineChart.getAxisLeft().setAxisMaximum((float)(max+20));
        lineChart.getAxisLeft().setAxisMinimum((float)(min-20));
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));

        return new Custom_Line(new LineData(dataSet), label_values);
    }

    public Custom getWeekData(String[] datapoints, int size, int[] thisColors, String[] timeStamp_value) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        int currentDate = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        List<String> labels = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            try {
                if (format.parse(timeStamp_value[i]) != null) {
                    calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                    int inputMonth = calendar.get(Calendar.MONTH);
                    int inputYear = calendar.get(Calendar.YEAR);

                    /*
                            if difference between current date and the date is less than or equal to 7
                     */

                    if ((currentYear == inputYear) && (currentMonth == inputMonth) && (currentDate - inputDate) <= 7) {
                        barEntries.add(new BarEntry(i,
                                Float.parseFloat(datapoints[i])));
                        labels.add(String.format(Locale.ENGLISH, "%02d/%02d/%d", inputMonth + 1,inputDate,  inputYear));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        fixIndices(barEntries);

        String[] label_values = new String[labels.size()];
        label_values = labels.toArray(label_values);
        BarDataSet dataSet = new BarDataSet(barEntries, "");
        dataSet.setColors(thisColors);
        dataSet.setValueTextSize(9f);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        return new Custom(new BarData(dataSet), label_values);
    }

    public Custom getAllData(String[] datapoints, int size, int[] thisColors, String[] timeStamp_value) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();


        List<String> labels = new ArrayList<>();
        for (int i = 0; i < size; i++) {
                        barEntries.add(new BarEntry(i,
                                Float.parseFloat(datapoints[i])));
                        labels.add(DateUtil.formatedDate(timeStamp_value[i],"EEE MMM dd HH:mm:ss Z yyyy","M/d/yy"));
        }

        fixIndices(barEntries);

        String[] label_values = new String[labels.size()];
        label_values = labels.toArray(label_values);
        BarDataSet dataSet = new BarDataSet(barEntries, "");
        dataSet.setColors(thisColors);
        dataSet.setValueTextSize(9f);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        return new Custom(new BarData(dataSet), label_values);
    }

    public Custom_Line getAllDataLine(String[] datapoints, int size, int[] thisColors, String[] timeStamp_value) {
        ArrayList<Entry> lineEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (datapoints[i]!=null){
                lineEntries.add(new Entry(i, Float.parseFloat(datapoints[i])));
                labels.add(DateUtil.formatedDate(timeStamp_value[i],"EEE MMM dd HH:mm:ss Z yyyy","M/d/yy"));
            }
        }

        fixLineIndices(lineEntries);

        String[] label_values = new String[labels.size()];
        label_values = labels.toArray(label_values);
        LineDataSet dataSet = new LineDataSet(lineEntries, "");
        dataSet.setColors(thisColors);
        dataSet.setValueTextSize(9f);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        return new Custom_Line(new LineData(dataSet), label_values);
    }

    public Custom getAllBPData(String[] bph_dataPoints, String[] bpl_dataPoints, Vector<Bp_data> bpVector, String[] timeStamp_value) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<BarEntry> barEntries2 = new ArrayList<>();

        List<String> labels = new ArrayList<>();
        for (int i = 0; i < bpVector.size(); i++) {
                        barEntries.add(new BarEntry(i, Float.parseFloat(bph_dataPoints[i])));
                        barEntries2.add(new BarEntry(i, Float.parseFloat(bpl_dataPoints[i])));
                        labels.add(DateUtil.formatedDate(timeStamp_value[i],"EEE MMM dd HH:mm:ss Z yyyy","M/d/yy"));
        }

        fixIndices(barEntries);
        fixIndices(barEntries2);

        BarDataSet dataSet = new BarDataSet(barEntries, "Systolic");
        dataSet.setColors(Color.parseColor("#f2af22"));
        dataSet.setValueTextSize(9f);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        BarDataSet dataSet2 = new BarDataSet(barEntries2, "Diastolic");
        dataSet2.setColors(Color.parseColor("#ed3c1a"));
        dataSet2.setValueTextSize(9f);
        dataSet2.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        BarData data = new BarData(dataSet, dataSet2);
        return new Custom(data, labels.toArray(new String[0]));
    }


    public Custom getWeekBPData(String[] bph_dataPoints, String[] bpl_dataPoints, Vector<Bp_data> bpVector, String[] timeStamp_value) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<BarEntry> barEntries2 = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        int currentDate = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        List<String> labels = new ArrayList<>();
        for (int i = 0; i < bpVector.size(); i++) {
            try {
                if (format.parse(timeStamp_value[i]) != null) {
                    calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                    int inputMonth = calendar.get(Calendar.MONTH);
                    int inputYear = calendar.get(Calendar.YEAR);

                    /*
                            if difference between current date and the date is less than or equal to 7
                     */

                    if ((currentYear == inputYear) && (currentMonth == inputMonth) && (currentDate - inputDate) <= 7) {
                        barEntries.add(new BarEntry(i, Float.parseFloat(bph_dataPoints[i])));
                        barEntries2.add(new BarEntry(i, Float.parseFloat(bpl_dataPoints[i])));
                        SimpleDateFormat newFormat = new SimpleDateFormat("EE dd",Locale.ENGLISH);
                       // labels.add(String.format(Locale.ENGLISH, "%02d/%02d/%d", inputMonth + 1,inputDate, inputYear));
                        labels.add(newFormat.format(timeStamp_value[i]));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        fixIndices(barEntries);

        /*String[] label_values = new String[labels.size()];
        label_values = labels.toArray(label_values);
        BarDataSet dataSet = new BarDataSet(barEntries, "");
        dataSet.setColors(thisColors);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        return new Custom(new BarData(dataSet), label_values);*/
        fixIndices(barEntries2);

        BarDataSet dataSet = new BarDataSet(barEntries, "Systolic");
        dataSet.setColors(Color.parseColor("#f2af22"));
        dataSet.setValueTextSize(9f);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        BarDataSet dataSet2 = new BarDataSet(barEntries2, "Diastolic");
        dataSet2.setColors(Color.parseColor("#ed3c1a"));
        dataSet2.setValueTextSize(9f);
        dataSet2.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        BarData data = new BarData(dataSet, dataSet2);
        return new Custom(data, labels.toArray(new String[0]));
    }

    public Custom getMonthData(String[] datapoints, int size, int[] thisColors, String[] timeStamp_value) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            try {
                if (format.parse(timeStamp_value[i]) != null) {
                    calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputMonth = calendar.get(Calendar.MONTH);
                    int inputYear = calendar.get(Calendar.YEAR);

                    /*
                        if years is same and difference between month is 1 then we have to take it
                        otherwise the case is when current month is january so, previous month will be december of previous year
                     */

                    if ((currentYear == inputYear && currentMonth - inputMonth <= 1) || (currentYear - inputYear == 1 && inputMonth - currentMonth == 11)) {
                        barEntries.add(new BarEntry(i, Float.parseFloat(datapoints[i])));
                        labels.add( String.format(Locale.ENGLISH, "%02d/%02d/%d",  inputMonth + 1, calendar.get(Calendar.DAY_OF_MONTH),inputYear));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        fixIndices(barEntries);

        BarDataSet dataSet = new BarDataSet(barEntries, "");
        dataSet.setColors(thisColors);
        dataSet.setValueTextSize(9f);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        return new Custom(new BarData(dataSet), labels.toArray(new String[0]));
    }

    public Custom getMonthBPData(String[] bph_dataPoints, String[] bpl_dataPoints, Vector<Bp_data> bpVector, int[] thisColors, String[] timeStamp_value) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<BarEntry> barEntries2 = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        List<String> labels = new ArrayList<>();
        for (int i = 0; i < bpVector.size(); i++) {
            try {
                if (format.parse(timeStamp_value[i]) != null) {
                    calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputMonth = calendar.get(Calendar.MONTH);
                    int inputYear = calendar.get(Calendar.YEAR);

                    /*
                        if years is same and difference between month is 1 then we have to take it
                        otherwise the case is when current month is january so, previous month will be december of previous year
                     */

                    if ((currentYear == inputYear && currentMonth - inputMonth <= 1) || (currentYear - inputYear == 1 && inputMonth - currentMonth == 11)) {
                        barEntries.add(new BarEntry(i, Float.parseFloat(bph_dataPoints[i])));
                        barEntries2.add(new BarEntry(i, Float.parseFloat(bpl_dataPoints[i])));
                        labels.add(String.format(Locale.ENGLISH, "%02d/%02d/%d",  inputMonth + 1, calendar.get(Calendar.DAY_OF_MONTH),inputYear));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        fixIndices(barEntries);

        /*BarDataSet dataSet = new BarDataSet(barEntries, "");
        dataSet.setColors(thisColors);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value)

        );
        return new Custom(new BarData(dataSet), labels.toArray(new String[0]));*/
        fixIndices(barEntries2);

        BarDataSet dataSet = new BarDataSet(barEntries, "Systolic");
        dataSet.setColors(Color.parseColor("#f2af22"));
        dataSet.setValueTextSize(9f);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        BarDataSet dataSet2 = new BarDataSet(barEntries2, "Diastolic");
        dataSet2.setColors(Color.parseColor("#ed3c1a"));
        dataSet2.setValueTextSize(9f);
        dataSet2.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        BarData data = new BarData(dataSet, dataSet2);
        return new Custom(data, labels.toArray(new String[0]));
    }


    public Custom getYearData(String[] datapoints, int size, int[] thisColors, String[] timeStamp_value) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        List<String> labels = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            try {
                if (format.parse(timeStamp_value[i]) != null) {
                    calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputYear = calendar.get(Calendar.YEAR);

                    /*
                            if year is same or it is a previous year
                     */

                    if ((currentYear - inputYear) <= 1) {
                        barEntries.add(new BarEntry(i, Float.parseFloat(datapoints[i])));
                        labels.add(DateUtil.formatedDate(timeStamp_value[i],"EEE MMM dd HH:mm:ss Z yyyy","M/d/yy"));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        fixIndices(barEntries);

        BarDataSet dataSet = new BarDataSet(barEntries, "");
        dataSet.setColors(thisColors);
        dataSet.setValueTextSize(9f);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));

        return new Custom(new BarData(dataSet), labels.toArray(new String[0]));
    }

    public Custom getYearBPData(String[] bph_dataPoints, String[] bpl_dataPoints, Vector<Bp_data> bpVector, int[] thisColors, String[] timeStamp_value) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<BarEntry> barEntries2 = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);


        List<String> labels = new ArrayList<>();
        for (int i = 0; i < bpVector.size(); i++) {
            try {
                if (format.parse(timeStamp_value[i]) != null) {
                    calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputYear = calendar.get(Calendar.YEAR);

                    /*
                            if year is same or it is a previous year
                     */

                    if ((currentYear - inputYear) <= 1) {
                        barEntries.add(new BarEntry(i,
                                 Float.parseFloat(bph_dataPoints[i])));
                        barEntries2.add(new BarEntry(i,
                               Float.parseFloat(bpl_dataPoints[i])));
                        labels.add(DateUtil.formatedDate(timeStamp_value[i],"EEE MMM dd HH:mm:ss Z yyyy","M/d/yy"));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        fixIndices(barEntries);
        fixIndices(barEntries2);

        BarDataSet dataSet = new BarDataSet(barEntries, "Systolic");
        dataSet.setColors(Color.parseColor("#f2af22"));
        dataSet.setValueTextSize(9f);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        BarDataSet dataSet2 = new BarDataSet(barEntries2, "Diastolic");
        dataSet2.setColors(Color.parseColor("#ed3c1a"));
        dataSet2.setValueTextSize(9f);
        dataSet2.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        BarData data = new BarData(dataSet, dataSet2);
        return new Custom(data, labels.toArray(new String[0]));
    }

    public Custom_Line getWeekLineData(String[] datapoints, int size, int[] thisColors, String[] timeStamp_value) {
        ArrayList<Entry> lineEntries = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        SimpleDateFormat format2 = new SimpleDateFormat("EEE MMM dd yyyy", Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        int currentDate = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        List<Date> days=getAllDaysOfTheCurrentWeek();

        List<String> labels = new ArrayList<>();
        for (int j = 0; j < days.size(); j++) {
        for (int i=0;i<size;i++){

                try {
                    String days1=format2.format(days.get(j));
                    String days2=format2.format(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    if (days1.equalsIgnoreCase(days2)){
                        SimpleDateFormat newFormat = new SimpleDateFormat("EE dd");
                        lineEntries.add(new Entry(i,
                                Float.parseFloat(datapoints[i])));
                        labels.add(newFormat.format(days.get(j)));
                    }
                        /*if (format.parse(timeStamp_value[i]) != null) {
                            calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                            int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                            int inputMonth = calendar.get(Calendar.MONTH);
                            int inputYear = calendar.get(Calendar.YEAR);


                          //  if difference between current date and the date is less than or equal to 7


                            if ((currentYear == inputYear) && (currentMonth == inputMonth) && (currentDate - inputDate) < 7) {

                            }
                        }*/
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }

        fixLineIndices(lineEntries);

        String[] label_values = new String[labels.size()];
        label_values = labels.toArray(label_values);
        LineDataSet dataSet = new LineDataSet(lineEntries, "");
        dataSet.setColors(thisColors[0]);
        dataSet.setCircleColor(thisColors[0]);
        dataSet.setLineWidth(2f); // Adjust line width as needed
        dataSet.setCircleRadius(4f); // Adjust circle size as needed
        dataSet.setDrawCircleHole(false);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        return new Custom_Line(new LineData(dataSet), label_values);
    }

    public Custom_Line getWeekLineDataold(String[] datapoints, int size, int[] thisColors, String[] timeStamp_value) {
        ArrayList<Entry> lineEntries = new ArrayList<>();


        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        SimpleDateFormat newFormat = new SimpleDateFormat("EE dd",Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        int currentDate = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);


        List<String> labels = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            try {
                if (format.parse(timeStamp_value[i]) != null) {
                    calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                    int inputMonth = calendar.get(Calendar.MONTH);
                    int inputYear = calendar.get(Calendar.YEAR);

                    /*
                            if difference between current date and the date is less than or equal to 7
                     */

                    if ((currentYear == inputYear) && (currentMonth == inputMonth) && (currentDate - inputDate) <= 7) {
                        lineEntries.add(new Entry(i,
                                Float.parseFloat(datapoints[i])));
                      /*  labels.add(String.format(Locale.ENGLISH, "%02d/%02d/%d",  inputMonth + 1,inputDate, inputYear));*/
                        labels.add(newFormat.format(timeStamp_value[i]));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        fixLineIndices(lineEntries);

        String[] label_values = new String[labels.size()];
        label_values = labels.toArray(label_values);
        LineDataSet dataSet = new LineDataSet(lineEntries, "");
        dataSet.setColors(thisColors);
        dataSet.setValueTextSize(9f);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        return new Custom_Line(new LineData(dataSet), label_values);
    }
    public Custom_Line getWeekLineData2(String[] datapoints, int size, int[] thisColors, String[] timeStamp_value,LineChart lineChart) {
        ArrayList<Entry> lineEntries = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        SimpleDateFormat format2 = new SimpleDateFormat("EEE MMM dd yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        int currentDate = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        List<Date> days=getAllDaysOfTheCurrentWeek();
        Double max=0.0;
        Double min= Double.parseDouble(datapoints[0]);
        List<String> labels = new ArrayList<>();
        for (int j = 0; j < days.size(); j++) {
            for (int i=0;i<size;i++){

                try {
                    String days1=format2.format(days.get(j));
                    String days2=format2.format(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    if (days1.equalsIgnoreCase(days2)){
                        Double value= Double.parseDouble(datapoints[i]);
                        if (value>max){
                            max=value;
                        }
                        if (value<min){
                            min=value;
                        }
                        SimpleDateFormat newFormat = new SimpleDateFormat("EE dd");
                        lineEntries.add(new Entry(i,
                                Float.parseFloat(datapoints[i])));
                        labels.add(newFormat.format(days.get(j)));
                    }
                        /*if (format.parse(timeStamp_value[i]) != null) {
                            calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                            int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                            int inputMonth = calendar.get(Calendar.MONTH);
                            int inputYear = calendar.get(Calendar.YEAR);


                          //  if difference between current date and the date is less than or equal to 7


                            if ((currentYear == inputYear) && (currentMonth == inputMonth) && (currentDate - inputDate) < 7) {

                            }
                        }*/
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        fixLineIndices(lineEntries);
        String[] label_values = new String[labels.size()];
        label_values = labels.toArray(label_values);
        LineDataSet dataSet = new LineDataSet(lineEntries, "");
        dataSet.setColors(thisColors[0]);
        dataSet.setCircleColor(thisColors[0]);
        dataSet.setLineWidth(2f); // Adjust line width as needed
        dataSet.setCircleRadius(4f); // Adjust circle size as needed
        dataSet.setDrawCircleHole(false);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        int dataCount = lineEntries.size();
        if (dataCount > 1) {
            lineChart.getXAxis().setAxisMinimum(lineEntries.get(0).getX()); // Minimum x-value
            lineChart.getXAxis().setAxisMaximum(lineEntries.get(dataCount - 1).getX()); // Maximum x-value
        } else if (dataCount == 1) {
            // Handle case with a single data point if needed
            // For example, set a reasonable range around the single point
            float singleXValue = lineEntries.get(0).getX();
            lineChart.getXAxis().setAxisMinimum(singleXValue - 2f);
            lineChart.getXAxis().setAxisMaximum(singleXValue + 0.5f);
        }
        lineChart.getAxisLeft().setTextColor(Color.parseColor("#000000")); // Change left y-axis label color
        lineChart.getAxisRight().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color
        lineChart.getXAxis().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color
        lineChart.getAxisLeft().setAxisMaximum((float)(max+20));
        lineChart.getAxisLeft().setAxisMinimum((float)(min-20));
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        return new Custom_Line(new LineData(dataSet), label_values);
    }

    public Custom_Line getWeekLineData2(String[] datapoints, int size, int[] thisColors, String[] timeStamp_value,LineChart lineChart, TextView textView,String type) {
        ArrayList<Entry> lineEntries = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        SimpleDateFormat format2 = new SimpleDateFormat("EEE MMM dd yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        int currentDate = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        List<Date> days=getAllDaysOfTheCurrentWeek();
        Double max=0.0;
        int l=0;
        Double sum=0.0;
        Double min= Double.parseDouble(datapoints[0]);
        List<String> labels = new ArrayList<>();
        for (int j = 0; j < days.size(); j++) {
            for (int i=0;i<size;i++){

                try {
                    String days1=format2.format(days.get(j));
                    String days2=format2.format(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    if (days1.equalsIgnoreCase(days2)){
                        Double value= Double.parseDouble(datapoints[i]);
                        if (value>max){
                            max=value;
                        }
                        if (value<min){
                            min=value;
                        }
                        l=l+1;
                        sum=sum+Double.parseDouble(datapoints[i]);
                        SimpleDateFormat newFormat = new SimpleDateFormat("EE dd");
                        lineEntries.add(new Entry(i,
                                Float.parseFloat(datapoints[i])));
                        labels.add(newFormat.format(days.get(j)));
                    }
                        /*if (format.parse(timeStamp_value[i]) != null) {
                            calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                            int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                            int inputMonth = calendar.get(Calendar.MONTH);
                            int inputYear = calendar.get(Calendar.YEAR);


                          //  if difference between current date and the date is less than or equal to 7


                            if ((currentYear == inputYear) && (currentMonth == inputMonth) && (currentDate - inputDate) < 7) {

                            }
                        }*/
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if (type.equals("hr")){
            textView.setText(l>0? AppConstant.getdataHRtextColor(Double.parseDouble(String.valueOf(sum/l)),textView):"--");
            if (textView.getText().toString().equals("--")){
                textView.setTextColor(Color.GRAY);
            }
        }else if (type.equals("glucose")){
            textView.setText(l>0? AppConstant.getdataGlucosetextColor(Double.parseDouble(String.valueOf(sum/l)),textView):"--");
            if (textView.getText().toString().equals("--")){
                textView.setTextColor(Color.GRAY);
            }
        }else if (type.equals("pulse")){
            textView.setText(l>0? AppConstant.getdataOxitextColor(Double.parseDouble(String.valueOf(sum/l)),textView):"--");
            if (textView.getText().toString().equals("--")){
                textView.setTextColor(Color.GRAY);
            }
        }else if (type.equals("weight")){
            textView.setText(l>0? String.valueOf(sum/l):"--");
            if (textView.getText().toString().equals("--")){
                textView.setTextColor(Color.GRAY);
            }else {
                textView.setTextColor(Color.BLUE);
            }
        }
        fixLineIndices(lineEntries);
        String[] label_values = new String[labels.size()];
        label_values = labels.toArray(label_values);
        LineDataSet dataSet = new LineDataSet(lineEntries, "");
        dataSet.setColors(thisColors[0]);
        dataSet.setCircleColor(thisColors[0]);
        dataSet.setLineWidth(2f); // Adjust line width as needed
        dataSet.setCircleRadius(4f); // Adjust circle size as needed
        dataSet.setDrawCircleHole(false);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        int dataCount = lineEntries.size();
        if (dataCount > 1) {
            lineChart.getXAxis().setAxisMinimum(lineEntries.get(0).getX()); // Minimum x-value
            lineChart.getXAxis().setAxisMaximum(lineEntries.get(dataCount - 1).getX()); // Maximum x-value
        } else if (dataCount == 1) {
            // Handle case with a single data point if needed
            // For example, set a reasonable range around the single point
            float singleXValue = lineEntries.get(0).getX();
            lineChart.getXAxis().setAxisMinimum(singleXValue - 2f);
            lineChart.getXAxis().setAxisMaximum(singleXValue + 0.5f);
        }
        lineChart.getAxisLeft().setTextColor(Color.parseColor("#000000")); // Change left y-axis label color
        lineChart.getAxisRight().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color
        lineChart.getXAxis().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color
        lineChart.getAxisLeft().setAxisMaximum((float)(max+20));
        lineChart.getAxisLeft().setAxisMinimum((float)(min-20));
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        return new Custom_Line(new LineData(dataSet), label_values);
    }

    public Custom_LineBP getdefaultBPData(String[] bph_dataPoints, String[] bpl_dataPoints, ArrayList<VitalDataBP> bpVector, int[] thisColors, String[] timeStamp_value) {
        ArrayList<Entry> barEntries = new ArrayList<>();
        ArrayList<Entry> barEntries2 = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        List<String> labels = new ArrayList<>();
        for (int i = 0; i < bpVector.size(); i++) {
            barEntries.add(new BarEntry(i,
                    Float.parseFloat(bph_dataPoints[i])));
            barEntries2.add(new BarEntry(i,
                    Float.parseFloat(bpl_dataPoints[i])));
            labels.add(timeStamp_value[i]);
        }

        //fixLineIndices(barEntries);
       // fixLineIndices(barEntries2);
        String[] label_values = new String[labels.size()];
        label_values = labels.toArray(label_values);

        LineDataSet dataSet = new LineDataSet(barEntries, "High BP");
        dataSet.setColors(thisColors[0]);
        dataSet.setCircleColor(thisColors[0]);
        dataSet.setLineWidth(2f); // Adjust line width as needed
        dataSet.setCircleRadius(4f); // Adjust circle size as needed
        dataSet.setDrawCircleHole(false);
        dataSet.setMode(LineDataSet.Mode.LINEAR); // Use LINEAR mode for straight lines
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));

        LineDataSet dataSet2 = new LineDataSet(barEntries2, "Low BP");
        dataSet2.setColors(thisColors[1]);
        dataSet2.setCircleColor(thisColors[1]);
        dataSet2.setLineWidth(2f); // Adjust line width as needed
        dataSet2.setCircleRadius(4f); // Adjust circle size as needed
        dataSet2.setDrawCircleHole(false);
        dataSet2.setMode(LineDataSet.Mode.LINEAR); // Use LINEAR mode for straight lines
        dataSet2.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));

        LineData data = new LineData(dataSet, dataSet2);
        return new Custom_LineBP(data, label_values);
    }

    public Custom_LineBP getdefaultBPData2(String[] bph_dataPoints, String[] bpl_dataPoints, ArrayList<VitalDataBP> bpVector, int[] thisColors, String[] timeStamp_value, String  type, LineChart lineChart) {
        ArrayList<Entry> barEntries = new ArrayList<>();
        ArrayList<Entry> barEntries2 = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Double max=0.0;
        Double min= Double.parseDouble(bpVector.get(0).getBplow());
        List<String> labels = new ArrayList<>();

        if (type.equals("daily")){
            SimpleDateFormat format3 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
            SimpleDateFormat format2 = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);

            Calendar calendar = Calendar.getInstance();
            String formattedDate = format2.format(calendar.getTime());

            /*Date currentDate2 = new Date();
          //  labels = new ArrayList<>();
            for (int i = 0; i < bpVector.size(); i++) {
                try {
                    if (format3.parse(timeStamp_value[i]) != null) {
                        Date  arrayDate = format3.parse(timeStamp_value[i]);
                    *//*calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                    int inputMonth = calendar.get(Calendar.MONTH);
                    int inputYear = calendar.get(Calendar.YEAR);*//*

             *//*
                            if difference between current date and the date is less than or equal to 7
                     *//*
                        if (isSameDay(currentDate2, arrayDate)) {
                            barEntries.add(new BarEntry(i,
                                    Float.parseFloat(bph_dataPoints[i])));
                            barEntries2.add(new BarEntry(i,
                                    Float.parseFloat(bpl_dataPoints[i])));
                            labels.add(formattedDate);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }*/


            labels = new ArrayList<>();
            for (int i = 0; i < bpVector.size(); i++) {

                String days2=DateUtil.formatedDate(bpVector.get(i).getEntry_date(),"EEE MMM dd HH:mm:ss Z yyyy","dd MMM, yyyy");
                if(formattedDate.equalsIgnoreCase(days2)){
                    Double value= Double.parseDouble(bpVector.get(i).getBphigh());
                    Double value2= Double.parseDouble(bpVector.get(i).getBplow());
                    if (value>max){
                        max=value;
                    }
                    if (value2<min){
                        min=value2;
                    }
                    barEntries.add(new BarEntry(i,
                            Float.parseFloat(bph_dataPoints[i])));
                    barEntries2.add(new BarEntry(i,
                            Float.parseFloat(bpl_dataPoints[i])));
                    labels.add(days2);
                }

                /*if (days1.equalsIgnoreCase(days2)){
                    SimpleDateFormat newFormat = new SimpleDateFormat("EE dd");
                    lineEntries.add(new Entry(i,
                            Float.parseFloat(datapoints[i])));
                    labels.add(newFormat.format(days.get(j)));
                }*/
             /*   if (format.parse(timeStamp_value[i]) != null) {
                    Date  arrayDate = format.parse(timeStamp_value[i]);*/
                    /*calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                    int inputMonth = calendar.get(Calendar.MONTH);
                    int inputYear = calendar.get(Calendar.YEAR);*/

                    /*
                            if difference between current date and the date is less than or equal to 7
                     */
                    /*if (isSameDay(currentDate2, arrayDate)) {
                        lineEntries.add(new Entry(i,
                                Float.parseFloat(datapoints[i])));
                        labels.add(formattedDate);
                    }*/
                // }

            }


        }else if (type.equals("weekly")){
            Calendar calendar = Calendar.getInstance();
            int currentDate = calendar.get(Calendar.DAY_OF_MONTH);
            int currentMonth = calendar.get(Calendar.MONTH);
            int currentYear = calendar.get(Calendar.YEAR);

            List<Date> days=getAllDaysOfTheCurrentWeek();
            labels = new ArrayList<>();
            SimpleDateFormat format2 = new SimpleDateFormat("EEE MMM dd yyyy", Locale.ENGLISH);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Collections.sort(bpVector, new Comparator<VitalDataBP>() {
                @Override
                public int compare(VitalDataBP entry1, VitalDataBP entry2) {
                    try {
                        Date date1 = sdf.parse(entry1.getEntry_date());
                        Date date2 = sdf.parse(entry2.getEntry_date());
                        return date1.compareTo(date2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });

            for (int j = 0; j < days.size(); j++) {
                for (int i=0;i<bpVector.size();i++){

                    try {
                        String days1=format2.format(days.get(j));
                        String days2=format2.format(Objects.requireNonNull(format.parse(bpVector.get(i).getEntry_date())));
                        if (days1.equalsIgnoreCase(days2)){
                            Double value= Double.parseDouble(bpVector.get(i).getBphigh());
                            Double value2= Double.parseDouble(bpVector.get(i).getBplow());
                            if (value>max){
                                max=value;
                            }
                            if (value2<min){
                                min=value2;
                            }
                            SimpleDateFormat newFormat = new SimpleDateFormat("EE dd");
                            barEntries.add(new BarEntry(i,
                                    Float.parseFloat(bpVector.get(i).getBphigh())));
                            barEntries2.add(new BarEntry(i,
                                    Float.parseFloat(bpVector.get(i).getBplow())));

                            labels.add(newFormat.format(days.get(j)));
                        }
                        /*if (format.parse(timeStamp_value[i]) != null) {
                            calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                            int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                            int inputMonth = calendar.get(Calendar.MONTH);
                            int inputYear = calendar.get(Calendar.YEAR);


                          //  if difference between current date and the date is less than or equal to 7


                            if ((currentYear == inputYear) && (currentMonth == inputMonth) && (currentDate - inputDate) < 7) {

                            }
                        }*/
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
        else if (type.equals("monthly")){

            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(Calendar.MONTH);
            int currentYear = calendar.get(Calendar.YEAR);


            //labels = new ArrayList<>();
            List<Date> days=getAllDaysOfTheCurrentMonth();
            labels = new ArrayList<>();
            SimpleDateFormat format2 = new SimpleDateFormat("EEE MMM dd yyyy", Locale.ENGLISH);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);


            Collections.sort(bpVector, new Comparator<VitalDataBP>() {
                @Override
                public int compare(VitalDataBP entry1, VitalDataBP entry2) {
                    try {
                        Date date1 = sdf.parse(entry1.getEntry_date());
                        Date date2 = sdf.parse(entry2.getEntry_date());
                        return date1.compareTo(date2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });

            for (int i=0;i<bpVector.size();i++){
                Double value= Double.parseDouble(bpVector.get(i).getBphigh());
                Double value2= Double.parseDouble(bpVector.get(i).getBplow());
                if (value>max){
                    max=value;
                }
                if (value2<min){
                    min=value2;
                }
                barEntries.add(new BarEntry(i,
                        Float.parseFloat(bpVector.get(i).getBphigh())));
                barEntries2.add(new BarEntry(i,
                        Float.parseFloat(bpVector.get(i).getBplow())));
                labels.add(DateUtil.formatedDate(bpVector.get(i).getEntry_date(),"yyyy-MM-dd","MMM dd"));
            }
        }
        else if (type.equals("sixmonth")){
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);

            labels = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
            Collections.sort(bpVector, new Comparator<VitalDataBP>() {
                @Override
                public int compare(VitalDataBP entry1, VitalDataBP entry2) {
                    try {
                        Date date1 = sdf.parse(entry1.getEntry_date());
                        Date date2 = sdf.parse(entry2.getEntry_date());
                        return date1.compareTo(date2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });
            for (int i = 0; i < bpVector.size(); i++) {

                Double value= Double.parseDouble(bpVector.get(i).getBphigh());
                Double value2= Double.parseDouble(bpVector.get(i).getBplow());
                if (value>max){
                    max=value;
                }
                if (value2<min){
                    min=value2;
                }

                barEntries.add(new BarEntry(i,
                        Float.parseFloat(bpVector.get(i).getBphigh())));
                barEntries2.add(new BarEntry(i,
                        Float.parseFloat(bpVector.get(i).getBplow())));
                labels.add(DateUtil.formatedDate(bpVector.get(i).getEntry_date(),"MMM yyyy","MMMyyyy"));
            }
        }
        else if (type.equals("yearly")){
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);

            labels = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
            Collections.sort(bpVector, new Comparator<VitalDataBP>() {
                @Override
                public int compare(VitalDataBP entry1, VitalDataBP entry2) {
                    try {
                        Date date1 = sdf.parse(entry1.getEntry_date());
                        Date date2 = sdf.parse(entry2.getEntry_date());
                        return date1.compareTo(date2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });
            for (int i = 0; i < bpVector.size(); i++) {
                /*try {
                    if (format.parse(timeStamp_value[i]) != null) {
                        calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                        int inputYear = calendar.get(Calendar.YEAR);

                    *//*
                            if year is same or it is a previous year
                     *//*

                        if ((currentYear - inputYear) <= 1) {

                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
                Double value= Double.parseDouble(bpVector.get(i).getBphigh());
                Double value2= Double.parseDouble(bpVector.get(i).getBplow());
                if (value>max){
                    max=value;
                }
                if (value2<min){
                    min=value2;
                }

                barEntries.add(new BarEntry(i,
                        Float.parseFloat(bpVector.get(i).getBphigh())));
                barEntries2.add(new BarEntry(i,
                        Float.parseFloat(bpVector.get(i).getBplow())));
                labels.add(DateUtil.formatedDate(bpVector.get(i).getEntry_date(),"MMM yyyy","MMM"));
            }
        }

        /*Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);


        List<String> labels = new ArrayList<>();
        for (int i = 0; i < bpVector.size(); i++) {
            barEntries.add(new BarEntry(i,
                    Float.parseFloat(bph_dataPoints[i])));
            barEntries2.add(new BarEntry(i,
                    Float.parseFloat(bpl_dataPoints[i])));

            labels.add(timeStamp_value[i]);
        }*/

        fixLineIndices(barEntries);
        fixLineIndices(barEntries2);
        String[] label_values = new String[labels.size()];
        label_values = labels.toArray(label_values);

        LineDataSet dataSet = new LineDataSet(barEntries, "High BP");
        dataSet.setColors(thisColors[0]);
        dataSet.setCircleColor(thisColors[0]);
        dataSet.setLineWidth(2f); // Adjust line width as needed
        dataSet.setCircleRadius(4f); // Adjust circle size as needed
        dataSet.setDrawCircleHole(false);
        dataSet.setMode(LineDataSet.Mode.LINEAR); // Use LINEAR mode for straight lines
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        LineDataSet dataSet2 = new LineDataSet(barEntries2, "Low BP");
        dataSet2.setColors(thisColors[1]);
        dataSet2.setCircleColor(thisColors[1]);
        dataSet2.setLineWidth(2f); // Adjust line width as needed
        dataSet2.setCircleRadius(4f); // Adjust circle size as needed
        dataSet2.setDrawCircleHole(false);
        dataSet2.setMode(LineDataSet.Mode.LINEAR); // Use LINEAR mode for straight lines
        dataSet2.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        dataSet2.setHighLightColor(Color.rgb(244, 117, 117));
        int dataCount = barEntries.size();
        if (dataCount > 1) {
            lineChart.getXAxis().setAxisMinimum(barEntries.get(0).getX()); // Minimum x-value
            lineChart.getXAxis().setAxisMaximum(barEntries.get(dataCount - 1).getX()); // Maximum x-value
        } else if (dataCount == 1) {
            // Handle case with a single data point if needed
            // For example, set a reasonable range around the single point
            float singleXValue = barEntries.get(0).getX();
            lineChart.getXAxis().setAxisMinimum(singleXValue - 2f);
            lineChart.getXAxis().setAxisMaximum(singleXValue + 0.5f);
        }
        lineChart.getAxisLeft().setTextColor(Color.parseColor("#000000")); // Change left y-axis label color
        lineChart.getAxisRight().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color
        lineChart.getXAxis().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color

        lineChart.getAxisLeft().setAxisMaximum((float)(max+20));
        lineChart.getAxisLeft().setAxisMinimum((float)(min-20));
        LineData data = new LineData(dataSet, dataSet2);
        return new Custom_LineBP(data, label_values);
    }

    public Custom_LineBP getdefaultBPData2(String[] bph_dataPoints, String[] bpl_dataPoints, ArrayList<VitalDataBP> bpVector, int[] thisColors, String[] timeStamp_value, String  type, LineChart lineChart, TextView textView) {
        ArrayList<Entry> barEntries = new ArrayList<>();
        ArrayList<Entry> barEntries2 = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Double max=0.0;
        Double min= Double.parseDouble(bpVector.get(0).getBplow());
        List<String> labels = new ArrayList<>();
        int l=0;
        Double sumsys=0.0;
        Double sumdyn=0.0;
        /*for (int i=0;i<bp_dataVector.size();i++){

        }
        binding.textBpAvg.setText(l>0?getdataBPtextColor(Double.parseDouble(String.valueOf(sumsys/l)),Double.parseDouble(String.valueOf(sumdyn/l))):"--");
    }else {
        binding.textBpAvg.setTextColor(Color.GRAY);
        binding.textBpAvg.setText("--");
    }*/
        /* int l=0;
                        int sumsys=0;
                        int sumdyn=0;
                        for (int i=0;i<bp_dataVector.size();i++){
                            l=l+1;
                            sumsys=sumsys+Integer.parseInt(bph_dataPoints[i]);
                            sumdyn=sumdyn+Integer.parseInt(bpl_dataPoints[i]);
                        }
                        binding.textBpAvg.setText(l>0?getdataBPtextColor(Double.parseDouble(String.valueOf(sumsys/l)),Double.parseDouble(String.valueOf(sumdyn/l))):"--");
                    }else {
                        binding.textBpAvg.setTextColor(Color.GRAY);
                        binding.textBpAvg.setText("--");
                    }*/
        if (type.equals("daily")){
            SimpleDateFormat format3 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
            SimpleDateFormat format2 = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);

            Calendar calendar = Calendar.getInstance();
            String formattedDate = format2.format(calendar.getTime());

            /*Date currentDate2 = new Date();
          //  labels = new ArrayList<>();
            for (int i = 0; i < bpVector.size(); i++) {
                try {
                    if (format3.parse(timeStamp_value[i]) != null) {
                        Date  arrayDate = format3.parse(timeStamp_value[i]);
                    *//*calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                    int inputMonth = calendar.get(Calendar.MONTH);
                    int inputYear = calendar.get(Calendar.YEAR);*//*

             *//*
                            if difference between current date and the date is less than or equal to 7
                     *//*
                        if (isSameDay(currentDate2, arrayDate)) {
                            barEntries.add(new BarEntry(i,
                                    Float.parseFloat(bph_dataPoints[i])));
                            barEntries2.add(new BarEntry(i,
                                    Float.parseFloat(bpl_dataPoints[i])));
                            labels.add(formattedDate);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }*/


            labels = new ArrayList<>();
            for (int i = 0; i < bpVector.size(); i++) {

                String days2=DateUtil.formatedDate(bpVector.get(i).getEntry_date(),"EEE MMM dd HH:mm:ss Z yyyy","dd MMM, yyyy");
                if(formattedDate.equalsIgnoreCase(days2)){
                    Double value= Double.parseDouble(bpVector.get(i).getBphigh());
                    Double value2= Double.parseDouble(bpVector.get(i).getBplow());
                    if (value>max){
                        max=value;
                    }
                    if (value2<min){
                        min=value2;
                    }
                    l=l+1;
                    sumsys=sumsys+Double.parseDouble(bph_dataPoints[i]);
                    sumdyn=sumdyn+Double.parseDouble(bpl_dataPoints[i]);
                    barEntries.add(new BarEntry(i,
                            Float.parseFloat(bph_dataPoints[i])));
                    barEntries2.add(new BarEntry(i,
                            Float.parseFloat(bpl_dataPoints[i])));
                    labels.add(days2);
                }

                /*if (days1.equalsIgnoreCase(days2)){
                    SimpleDateFormat newFormat = new SimpleDateFormat("EE dd");
                    lineEntries.add(new Entry(i,
                            Float.parseFloat(datapoints[i])));
                    labels.add(newFormat.format(days.get(j)));
                }*/
             /*   if (format.parse(timeStamp_value[i]) != null) {
                    Date  arrayDate = format.parse(timeStamp_value[i]);*/
                    /*calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                    int inputMonth = calendar.get(Calendar.MONTH);
                    int inputYear = calendar.get(Calendar.YEAR);*/

                    /*
                            if difference between current date and the date is less than or equal to 7
                     */
                    /*if (isSameDay(currentDate2, arrayDate)) {
                        lineEntries.add(new Entry(i,
                                Float.parseFloat(datapoints[i])));
                        labels.add(formattedDate);
                    }*/
                // }

            }


        }else if (type.equals("weekly")){
            Calendar calendar = Calendar.getInstance();
            int currentDate = calendar.get(Calendar.DAY_OF_MONTH);
            int currentMonth = calendar.get(Calendar.MONTH);
            int currentYear = calendar.get(Calendar.YEAR);

            List<Date> days=getAllDaysOfTheCurrentWeek();
            labels = new ArrayList<>();
            SimpleDateFormat format2 = new SimpleDateFormat("EEE MMM dd yyyy", Locale.ENGLISH);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Collections.sort(bpVector, new Comparator<VitalDataBP>() {
                @Override
                public int compare(VitalDataBP entry1, VitalDataBP entry2) {
                    try {
                        Date date1 = sdf.parse(entry1.getEntry_date());
                        Date date2 = sdf.parse(entry2.getEntry_date());
                        return date1.compareTo(date2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });

            for (int j = 0; j < days.size(); j++) {
                for (int i=0;i<bpVector.size();i++){

                    try {
                        String days1=format2.format(days.get(j));
                        String days2=format2.format(Objects.requireNonNull(format.parse(bpVector.get(i).getEntry_date())));
                        if (days1.equalsIgnoreCase(days2)){
                            Double value= Double.parseDouble(bpVector.get(i).getBphigh());
                            Double value2= Double.parseDouble(bpVector.get(i).getBplow());
                            if (value>max){
                                max=value;
                            }
                            if (value2<min){
                                min=value2;
                            }
                            SimpleDateFormat newFormat = new SimpleDateFormat("EE dd");
                            l=l+1;
                            sumsys=sumsys+Double.parseDouble(bpVector.get(i).getBphigh());
                            sumdyn=sumdyn+Double.parseDouble(bpVector.get(i).getBplow());
                            barEntries.add(new BarEntry(i,
                                    Float.parseFloat(bpVector.get(i).getBphigh())));
                            barEntries2.add(new BarEntry(i,
                                    Float.parseFloat(bpVector.get(i).getBplow())));

                            labels.add(newFormat.format(days.get(j)));
                        }
                        /*if (format.parse(timeStamp_value[i]) != null) {
                            calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                            int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                            int inputMonth = calendar.get(Calendar.MONTH);
                            int inputYear = calendar.get(Calendar.YEAR);


                          //  if difference between current date and the date is less than or equal to 7


                            if ((currentYear == inputYear) && (currentMonth == inputMonth) && (currentDate - inputDate) < 7) {

                            }
                        }*/
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
        else if (type.equals("monthly")){

            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(Calendar.MONTH);
            int currentYear = calendar.get(Calendar.YEAR);


            //labels = new ArrayList<>();
            List<Date> days=getAllDaysOfTheCurrentMonth();
            labels = new ArrayList<>();
            SimpleDateFormat format2 = new SimpleDateFormat("EEE MMM dd yyyy", Locale.ENGLISH);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);


            Collections.sort(bpVector, new Comparator<VitalDataBP>() {
                @Override
                public int compare(VitalDataBP entry1, VitalDataBP entry2) {
                    try {
                        Date date1 = sdf.parse(entry1.getEntry_date());
                        Date date2 = sdf.parse(entry2.getEntry_date());
                        return date1.compareTo(date2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });

            for (int i=0;i<bpVector.size();i++){
                Double value= Double.parseDouble(bpVector.get(i).getBphigh());
                Double value2= Double.parseDouble(bpVector.get(i).getBplow());
                if (value>max){
                    max=value;
                }
                if (value2<min){
                    min=value2;
                }
                l=l+1;
                sumsys=sumsys+Double.parseDouble(bpVector.get(i).getBphigh());
                sumdyn=sumdyn+Double.parseDouble(bpVector.get(i).getBplow());
                barEntries.add(new BarEntry(i,
                        Float.parseFloat(bpVector.get(i).getBphigh())));
                barEntries2.add(new BarEntry(i,
                        Float.parseFloat(bpVector.get(i).getBplow())));
                labels.add(DateUtil.formatedDate(bpVector.get(i).getEntry_date(),"yyyy-MM-dd","MMM dd"));
            }
        }
        else if (type.equals("sixmonth")){
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);

            labels = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
            Collections.sort(bpVector, new Comparator<VitalDataBP>() {
                @Override
                public int compare(VitalDataBP entry1, VitalDataBP entry2) {
                    try {
                        Date date1 = sdf.parse(entry1.getEntry_date());
                        Date date2 = sdf.parse(entry2.getEntry_date());
                        return date1.compareTo(date2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });
            for (int i = 0; i < bpVector.size(); i++) {

                Double value= Double.parseDouble(bpVector.get(i).getBphigh());
                Double value2= Double.parseDouble(bpVector.get(i).getBplow());
                if (value>max){
                    max=value;
                }
                if (value2<min){
                    min=value2;
                }
                l=l+1;
                sumsys=sumsys+Double.parseDouble(bpVector.get(i).getBphigh());
                sumdyn=sumdyn+Double.parseDouble(bpVector.get(i).getBplow());
                barEntries.add(new BarEntry(i,
                        Float.parseFloat(bpVector.get(i).getBphigh())));
                barEntries2.add(new BarEntry(i,
                        Float.parseFloat(bpVector.get(i).getBplow())));
                labels.add(DateUtil.formatedDate(bpVector.get(i).getEntry_date(),"MMM yyyy","MMMyyyy"));
            }
        }
        else if (type.equals("yearly")){
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);

            labels = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
            Collections.sort(bpVector, new Comparator<VitalDataBP>() {
                @Override
                public int compare(VitalDataBP entry1, VitalDataBP entry2) {
                    try {
                        Date date1 = sdf.parse(entry1.getEntry_date());
                        Date date2 = sdf.parse(entry2.getEntry_date());
                        return date1.compareTo(date2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });
            for (int i = 0; i < bpVector.size(); i++) {
                /*try {
                    if (format.parse(timeStamp_value[i]) != null) {
                        calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                        int inputYear = calendar.get(Calendar.YEAR);

                    *//*
                            if year is same or it is a previous year
                     *//*

                        if ((currentYear - inputYear) <= 1) {

                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
                Double value= Double.parseDouble(bpVector.get(i).getBphigh());
                Double value2= Double.parseDouble(bpVector.get(i).getBplow());
                if (value>max){
                    max=value;
                }
                if (value2<min){
                    min=value2;
                }
                l=l+1;
                sumsys=sumsys+Double.parseDouble(bpVector.get(i).getBphigh());
                sumdyn=sumdyn+Double.parseDouble(bpVector.get(i).getBplow());
                barEntries.add(new BarEntry(i,
                        Float.parseFloat(bpVector.get(i).getBphigh())));
                barEntries2.add(new BarEntry(i,
                        Float.parseFloat(bpVector.get(i).getBplow())));
                labels.add(DateUtil.formatedDate(bpVector.get(i).getEntry_date(),"MMM yyyy","MMM"));
            }
        }

        /*Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);


        List<String> labels = new ArrayList<>();
        for (int i = 0; i < bpVector.size(); i++) {
            barEntries.add(new BarEntry(i,
                    Float.parseFloat(bph_dataPoints[i])));
            barEntries2.add(new BarEntry(i,
                    Float.parseFloat(bpl_dataPoints[i])));

            labels.add(timeStamp_value[i]);
        }*/
        textView.setText(l>0? AppConstant.getdataBPtextColor(Double.parseDouble(String.valueOf(sumsys/l)),Double.parseDouble(String.valueOf(sumdyn/l)),textView):"--");
        if (textView.getText().toString().equals("--")){
            textView.setTextColor(Color.GRAY);
        }
        fixLineIndices(barEntries);
        fixLineIndices(barEntries2);
        String[] label_values = new String[labels.size()];
        label_values = labels.toArray(label_values);

        LineDataSet dataSet = new LineDataSet(barEntries, "High BP");
        dataSet.setColors(thisColors[0]);
        dataSet.setCircleColor(thisColors[0]);
        dataSet.setLineWidth(2f); // Adjust line width as needed
        dataSet.setCircleRadius(4f); // Adjust circle size as needed
        dataSet.setDrawCircleHole(false);
        dataSet.setMode(LineDataSet.Mode.LINEAR); // Use LINEAR mode for straight lines
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        LineDataSet dataSet2 = new LineDataSet(barEntries2, "Low BP");
        dataSet2.setColors(thisColors[1]);
        dataSet2.setCircleColor(thisColors[1]);
        dataSet2.setLineWidth(2f); // Adjust line width as needed
        dataSet2.setCircleRadius(4f); // Adjust circle size as needed
        dataSet2.setDrawCircleHole(false);
        dataSet2.setMode(LineDataSet.Mode.LINEAR); // Use LINEAR mode for straight lines
        dataSet2.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        dataSet2.setHighLightColor(Color.rgb(244, 117, 117));
        int dataCount = barEntries.size();
        if (dataCount > 1) {
            lineChart.getXAxis().setAxisMinimum(barEntries.get(0).getX()); // Minimum x-value
            lineChart.getXAxis().setAxisMaximum(barEntries.get(dataCount - 1).getX()); // Maximum x-value
        } else if (dataCount == 1) {
            // Handle case with a single data point if needed
            // For example, set a reasonable range around the single point
            float singleXValue = barEntries.get(0).getX();
            lineChart.getXAxis().setAxisMinimum(singleXValue - 2f);
            lineChart.getXAxis().setAxisMaximum(singleXValue + 0.5f);
        }
        lineChart.getAxisLeft().setTextColor(Color.parseColor("#000000")); // Change left y-axis label color
        lineChart.getAxisRight().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color
        lineChart.getXAxis().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color

        lineChart.getAxisLeft().setAxisMaximum((float)(max+20));
        lineChart.getAxisLeft().setAxisMinimum((float)(min-20));
        LineData data = new LineData(dataSet, dataSet2);
        return new Custom_LineBP(data, label_values);
    }


    public Custom_Line getMonthLineData(String[] datapoints, int size, int[] thisColors, String[] timeStamp_value) {
        ArrayList<Entry> entries = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        SimpleDateFormat format2 = new SimpleDateFormat("EEE MMM dd yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        //List<String> labels = new ArrayList<>();
        List<Date> days=getAllDaysOfTheCurrentMonth();

        List<String> labels = new ArrayList<>();
        for (int j = 0; j < days.size(); j++) {
            for (int i=0;i<size;i++){

                try {
                    String days1=format2.format(days.get(j));
                    String days2=format2.format(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    if (days1.equalsIgnoreCase(days2)){
                        SimpleDateFormat newFormat = new SimpleDateFormat("MMM dd");
                        entries.add(new Entry(i,
                                Float.parseFloat(datapoints[i])));
                        labels.add(newFormat.format(days.get(j)));
                    }
                        /*if (format.parse(timeStamp_value[i]) != null) {
                            calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                            int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                            int inputMonth = calendar.get(Calendar.MONTH);
                            int inputYear = calendar.get(Calendar.YEAR);


                          //  if difference between current date and the date is less than or equal to 7


                            if ((currentYear == inputYear) && (currentMonth == inputMonth) && (currentDate - inputDate) < 7) {

                            }
                        }*/
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }

        /*for (int i = 0; i < size; i++) {
            try {
                if (format.parse(timeStamp_value[i]) != null) {
                    calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputMonth = calendar.get(Calendar.MONTH);
                    int inputYear = calendar.get(Calendar.YEAR);

                    *//*
                        if years is same and difference between month is 1 then we have to take it
                        otherwise the case is when current month is january so, previous month will be december of previous year
                     *//*

                    if ((currentYear == inputYear && currentMonth - inputMonth <= 1) || (currentYear - inputYear == 1 && inputMonth - currentMonth == 11)) {
                        entries.add(new Entry(i, Float.parseFloat(datapoints[i])));
                        labels.add(String.format(Locale.ENGLISH, "%02d/%02d/%d",  inputMonth + 1, calendar.get(Calendar.DAY_OF_MONTH),inputYear));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }*/

        fixLineIndices(entries);

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColors(thisColors);
        dataSet.setColors(thisColors[0]);
        dataSet.setCircleColor(thisColors[0]);
        dataSet.setLineWidth(2f); // Adjust line width as needed
        dataSet.setCircleRadius(4f); // Adjust circle size as needed
        dataSet.setDrawCircleHole(false);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        return new Custom_Line(new LineData(dataSet), labels.toArray(new String[0]));
    }

    public Custom_Line getMonthLineData2(String[] datapoints, int size, int[] thisColors, String[] timeStamp_value,LineChart lineChart) {
        ArrayList<Entry> entries = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        SimpleDateFormat format2 = new SimpleDateFormat("EEE MMM dd yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        //List<String> labels = new ArrayList<>();
        List<Date> days=getAllDaysOfTheCurrentMonth();
        Double max=0.0;
        Double min= Double.parseDouble(datapoints[0]);
        List<String> labels = new ArrayList<>();
        for (int j = 0; j < days.size(); j++) {
            for (int i=0;i<size;i++){

                try {
                    String days1=format2.format(days.get(j));
                    String days2=format2.format(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    if (days1.equalsIgnoreCase(days2)){
                        Double value= Double.parseDouble(datapoints[i]);
                        if (value>max){
                            max=value;
                        }
                        if (value<min){
                            min=value;
                        }
                        SimpleDateFormat newFormat = new SimpleDateFormat("MMM dd");
                        entries.add(new Entry(i,
                                Float.parseFloat(datapoints[i])));
                        labels.add(newFormat.format(days.get(j)));
                    }
                        /*if (format.parse(timeStamp_value[i]) != null) {
                            calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                            int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                            int inputMonth = calendar.get(Calendar.MONTH);
                            int inputYear = calendar.get(Calendar.YEAR);


                          //  if difference between current date and the date is less than or equal to 7


                            if ((currentYear == inputYear) && (currentMonth == inputMonth) && (currentDate - inputDate) < 7) {

                            }
                        }*/
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }

        /*for (int i = 0; i < size; i++) {
            try {
                if (format.parse(timeStamp_value[i]) != null) {
                    calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputMonth = calendar.get(Calendar.MONTH);
                    int inputYear = calendar.get(Calendar.YEAR);

                    *//*
                        if years is same and difference between month is 1 then we have to take it
                        otherwise the case is when current month is january so, previous month will be december of previous year
                     *//*

                    if ((currentYear == inputYear && currentMonth - inputMonth <= 1) || (currentYear - inputYear == 1 && inputMonth - currentMonth == 11)) {
                        entries.add(new Entry(i, Float.parseFloat(datapoints[i])));
                        labels.add(String.format(Locale.ENGLISH, "%02d/%02d/%d",  inputMonth + 1, calendar.get(Calendar.DAY_OF_MONTH),inputYear));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }*/

        fixLineIndices(entries);

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColors(thisColors);
        dataSet.setColors(thisColors[0]);
        dataSet.setCircleColor(thisColors[0]);
        dataSet.setLineWidth(2f); // Adjust line width as needed
        dataSet.setCircleRadius(4f); // Adjust circle size as needed
        dataSet.setDrawCircleHole(false);
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        int dataCount = entries.size();
        if (dataCount > 1) {
            lineChart.getXAxis().setAxisMinimum(entries.get(0).getX()); // Minimum x-value
            lineChart.getXAxis().setAxisMaximum(entries.get(dataCount - 1).getX()); // Maximum x-value
        } else if (dataCount == 1) {
            // Handle case with a single data point if needed
            // For example, set a reasonable range around the single point
            float singleXValue = entries.get(0).getX();
            lineChart.getXAxis().setAxisMinimum(singleXValue - 2f);
            lineChart.getXAxis().setAxisMaximum(singleXValue + 0.5f);
        }
        lineChart.getAxisLeft().setTextColor(Color.parseColor("#000000")); // Change left y-axis label color
        lineChart.getAxisRight().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color
        lineChart.getXAxis().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color
        lineChart.getAxisLeft().setAxisMaximum((float)(max+20));
        lineChart.getAxisLeft().setAxisMinimum((float)(min-20));
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        return new Custom_Line(new LineData(dataSet), labels.toArray(new String[0]));
    }
    public Custom_Line getMonthLineData2(String[] datapoints, int size, int[] thisColors, String[] timeStamp_value,LineChart lineChart, TextView textView,String type) {
        ArrayList<Entry> entries = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        SimpleDateFormat format2 = new SimpleDateFormat("EEE MMM dd yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        int l=0;
        Double sum=0.0;
        //List<String> labels = new ArrayList<>();
        List<Date> days=getAllDaysOfTheCurrentMonth();
        Double max=0.0;
        Double min= Double.parseDouble(datapoints[0]);
        List<String> labels = new ArrayList<>();
        for (int j = 0; j < days.size(); j++) {
            for (int i=0;i<size;i++){

                try {
                    String days1=format2.format(days.get(j));
                    String days2=format2.format(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    if (days1.equalsIgnoreCase(days2)){
                        Double value= Double.parseDouble(datapoints[i]);
                        if (value>max){
                            max=value;
                        }
                        if (value<min){
                            min=value;
                        }
                        l=l+1;
                        sum=sum+Double.parseDouble(datapoints[i]);
                        SimpleDateFormat newFormat = new SimpleDateFormat("MMM dd");
                        entries.add(new Entry(i,
                                Float.parseFloat(datapoints[i])));
                        labels.add(newFormat.format(days.get(j)));
                    }
                        /*if (format.parse(timeStamp_value[i]) != null) {
                            calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                            int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                            int inputMonth = calendar.get(Calendar.MONTH);
                            int inputYear = calendar.get(Calendar.YEAR);


                          //  if difference between current date and the date is less than or equal to 7


                            if ((currentYear == inputYear) && (currentMonth == inputMonth) && (currentDate - inputDate) < 7) {

                            }
                        }*/
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }

        /*for (int i = 0; i < size; i++) {
            try {
                if (format.parse(timeStamp_value[i]) != null) {
                    calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputMonth = calendar.get(Calendar.MONTH);
                    int inputYear = calendar.get(Calendar.YEAR);

                    *//*
                        if years is same and difference between month is 1 then we have to take it
                        otherwise the case is when current month is january so, previous month will be december of previous year
                     *//*

                    if ((currentYear == inputYear && currentMonth - inputMonth <= 1) || (currentYear - inputYear == 1 && inputMonth - currentMonth == 11)) {
                        entries.add(new Entry(i, Float.parseFloat(datapoints[i])));
                        labels.add(String.format(Locale.ENGLISH, "%02d/%02d/%d",  inputMonth + 1, calendar.get(Calendar.DAY_OF_MONTH),inputYear));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }*/
        if (type.equals("hr")){
            textView.setText(l>0? AppConstant.getdataHRtextColor(Double.parseDouble(String.valueOf(sum/l)),textView):"--");
            if (textView.getText().toString().equals("--")){
                textView.setTextColor(Color.GRAY);
            }
        }else if (type.equals("glucose")){
            textView.setText(l>0? AppConstant.getdataGlucosetextColor(Double.parseDouble(String.valueOf(sum/l)),textView):"--");
            if (textView.getText().toString().equals("--")){
                textView.setTextColor(Color.GRAY);
            }
        }else if (type.equals("pulse")){
            textView.setText(l>0? AppConstant.getdataOxitextColor(Double.parseDouble(String.valueOf(sum/l)),textView):"--");
            if (textView.getText().toString().equals("--")){
                textView.setTextColor(Color.GRAY);
            }
        }else if (type.equals("weight")){
            textView.setText(l>0? String.valueOf(sum/l):"--");
            if (textView.getText().toString().equals("--")){
                textView.setTextColor(Color.GRAY);
            }else {
                textView.setTextColor(Color.BLUE);
            }
        }
        fixLineIndices(entries);

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColors(thisColors);
        dataSet.setColors(thisColors[0]);
        dataSet.setCircleColor(thisColors[0]);
        dataSet.setLineWidth(2f); // Adjust line width as needed
        dataSet.setCircleRadius(4f); // Adjust circle size as needed
        dataSet.setDrawCircleHole(false);
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        int dataCount = entries.size();
        if (dataCount > 1) {
            lineChart.getXAxis().setAxisMinimum(entries.get(0).getX()); // Minimum x-value
            lineChart.getXAxis().setAxisMaximum(entries.get(dataCount - 1).getX()); // Maximum x-value
        } else if (dataCount == 1) {
            // Handle case with a single data point if needed
            // For example, set a reasonable range around the single point
            float singleXValue = entries.get(0).getX();
            lineChart.getXAxis().setAxisMinimum(singleXValue - 2f);
            lineChart.getXAxis().setAxisMaximum(singleXValue + 0.5f);
        }
        lineChart.getAxisLeft().setTextColor(Color.parseColor("#000000")); // Change left y-axis label color
        lineChart.getAxisRight().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color
        lineChart.getXAxis().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color
        lineChart.getAxisLeft().setAxisMaximum((float)(max+20));
        lineChart.getAxisLeft().setAxisMinimum((float)(min-20));
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        return new Custom_Line(new LineData(dataSet), labels.toArray(new String[0]));
    }

    public Custom_Line getMonthLineDataold(String[] datapoints, int size, int[] thisColors, String[] timeStamp_value) {
        ArrayList<Entry> entries = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);


        List<String> labels = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            try {
                if (format.parse(timeStamp_value[i]) != null) {
                    calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputMonth = calendar.get(Calendar.MONTH);
                    int inputYear = calendar.get(Calendar.YEAR);

                    /*
                        if years is same and difference between month is 1 then we have to take it
                        otherwise the case is when current month is january so, previous month will be december of previous year
                     */

                    if ((currentYear == inputYear && currentMonth - inputMonth <= 1) || (currentYear - inputYear == 1 && inputMonth - currentMonth == 11)) {
                        entries.add(new Entry(i, Float.parseFloat(datapoints[i])));
                        labels.add(String.format(Locale.ENGLISH, "%02d/%02d/%d",  inputMonth + 1, calendar.get(Calendar.DAY_OF_MONTH),inputYear));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        fixLineIndices(entries);

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColors(thisColors);
        dataSet.setValueTextSize(9f);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        return new Custom_Line(new LineData(dataSet), labels.toArray(new String[0]));
    }

    public Custom_Line getYearLineData(String[] datapoints, int size, int[] thisColors, String[] timeStamp_value, String  type) {
        ArrayList<Entry> entries = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        List<String> labels = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            /*try {
                if (format.parse(timeStamp_value[i]) != null) {
                    calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputYear = calendar.get(Calendar.YEAR);

                    *//*
                            if year is same or it is a previous year
                     *//*

                    if ((currentYear - inputYear) <= 1) {

                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }*/
            entries.add(new Entry(i+10.5f, Float.parseFloat(datapoints[i])));
            if (type.equals("sixmonth")){
                labels.add(DateUtil.formatedDate(timeStamp_value[i],"MMM yyyy","MMM yyyy"));
            }else {
                labels.add(DateUtil.formatedDate(timeStamp_value[i],"MMM yyyy","MMM yyyy"));
            }

        }

        fixLineIndices(entries);

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColors(thisColors[0]);
        dataSet.setCircleColor(thisColors[0]);
        dataSet.setLineWidth(2f); // Adjust line width as needed
        dataSet.setCircleRadius(4f); // Adjust circle size as needed
        dataSet.setDrawCircleHole(false);

        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));

        return new Custom_Line(new LineData(dataSet), labels.toArray(new String[0]));
    }

    public Custom_Line getYearLineDataold(String[] datapoints, int size, int[] thisColors, String[] timeStamp_value) {
        ArrayList<Entry> entries = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        List<String> labels = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            try {
                if (format.parse(timeStamp_value[i]) != null) {
                    calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputYear = calendar.get(Calendar.YEAR);

                    /*
                            if year is same or it is a previous year
                     */

                    if ((currentYear - inputYear) <= 1) {
                        entries.add(new Entry(i, Float.parseFloat(datapoints[i])));
                        labels.add(DateUtil.formatedDate(timeStamp_value[i],"EEE MMM dd HH:mm:ss Z yyyy","M/d/yy"));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        fixLineIndices(entries);

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColors(thisColors);
        dataSet.setValueTextSize(9f);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));

        return new Custom_Line(new LineData(dataSet), labels.toArray(new String[0]));
    }

    public Custom_Line getYearLineData2(String[] datapoints, int size, int[] thisColors, String[] timeStamp_value, String  type,LineChart lineChart) {
        ArrayList<Entry> entries = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        List<String> labels = new ArrayList<>();
        Double max=0.0;
        Double min= Double.parseDouble(datapoints[0]);
        for (int i = 0; i < size; i++) {
            /*try {
                if (format.parse(timeStamp_value[i]) != null) {
                    calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputYear = calendar.get(Calendar.YEAR);

                    *//*
                            if year is same or it is a previous year
                     *//*

                    if ((currentYear - inputYear) <= 1) {

                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }*/
            Double value= Double.parseDouble(datapoints[i]);
            if (value>max){
                max=value;
            }
            if (value<min){
                min=value;
            }
            entries.add(new Entry(i+10.5f, Float.parseFloat(datapoints[i])));
            if (type.equals("sixmonth")){
                labels.add(DateUtil.formatedDate(timeStamp_value[i],"MMM yyyy","MMMyyyy"));
            }else {
                labels.add(DateUtil.formatedDate(timeStamp_value[i],"MMM yyyy","MMM"));
            }

        }

        fixLineIndices(entries);

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColors(thisColors[0]);
        dataSet.setCircleColor(thisColors[0]);
        dataSet.setLineWidth(2f); // Adjust line width as needed
        dataSet.setCircleRadius(4f); // Adjust circle size as needed
        dataSet.setDrawCircleHole(false);
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        int dataCount = entries.size();
        if (dataCount > 1) {
            lineChart.getXAxis().setAxisMinimum(entries.get(0).getX()); // Minimum x-value
            lineChart.getXAxis().setAxisMaximum(entries.get(dataCount - 1).getX()); // Maximum x-value
        } else if (dataCount == 1) {
            // Handle case with a single data point if needed
            // For example, set a reasonable range around the single point
            float singleXValue = entries.get(0).getX();
            lineChart.getXAxis().setAxisMinimum(singleXValue - 2f);
            lineChart.getXAxis().setAxisMaximum(singleXValue + 0.5f);
        }
        lineChart.getAxisLeft().setTextColor(Color.parseColor("#000000")); // Change left y-axis label color
        lineChart.getAxisRight().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color
        lineChart.getXAxis().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color
        lineChart.getAxisLeft().setAxisMaximum((float)(max+20));
        lineChart.getAxisLeft().setAxisMinimum((float)(min-20));
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));

        return new Custom_Line(new LineData(dataSet), labels.toArray(new String[0]));
    }


    public Custom_Line getYearLineData2(String[] datapoints, int size, int[] thisColors, String[] timeStamp_value, String  type,LineChart lineChart, TextView textView,String type2) {
        ArrayList<Entry> entries = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        int l=0;
        Double sum=0.0;
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        List<String> labels = new ArrayList<>();
        Double max=0.0;
        Double min= Double.parseDouble(datapoints[0]);
        for (int i = 0; i < size; i++) {
            /*try {
                if (format.parse(timeStamp_value[i]) != null) {
                    calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputYear = calendar.get(Calendar.YEAR);

                    *//*
                            if year is same or it is a previous year
                     *//*

                    if ((currentYear - inputYear) <= 1) {

                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }*/
            Double value= Double.parseDouble(datapoints[i]);
            if (value>max){
                max=value;
            }
            if (value<min){
                min=value;
            }
            l=l+1;
            sum=sum+Double.parseDouble(datapoints[i]);
            entries.add(new Entry(i+10.5f, Float.parseFloat(datapoints[i])));
            if (type.equals("sixmonth")){
                labels.add(DateUtil.formatedDate(timeStamp_value[i],"MMM yyyy","MMMyyyy"));
            }else {
                labels.add(DateUtil.formatedDate(timeStamp_value[i],"MMM yyyy","MMM"));
            }

        }

        fixLineIndices(entries);
        if (type2.equals("hr")){
            textView.setText(l>0? AppConstant.getdataHRtextColor(Double.parseDouble(String.valueOf(sum/l)),textView):"--");
            if (textView.getText().toString().equals("--")){
                textView.setTextColor(Color.GRAY);
            }
        }else if (type2.equals("glucose")){
            textView.setText(l>0? AppConstant.getdataGlucosetextColor(Double.parseDouble(String.valueOf(sum/l)),textView):"--");
            if (textView.getText().toString().equals("--")){
                textView.setTextColor(Color.GRAY);
            }
        }else if (type2.equals("pulse")){
            textView.setText(l>0? AppConstant.getdataOxitextColor(Double.parseDouble(String.valueOf(sum/l)),textView):"--");
            if (textView.getText().toString().equals("--")){
                textView.setTextColor(Color.GRAY);
            }
        }else if (type2.equals("weight")){
            textView.setText(l>0? String.valueOf(sum/l):"--");
            if (textView.getText().toString().equals("--")){
                textView.setTextColor(Color.GRAY);
            }else {
                textView.setTextColor(Color.BLUE);
            }
        }
        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColors(thisColors[0]);
        dataSet.setCircleColor(thisColors[0]);
        dataSet.setLineWidth(2f); // Adjust line width as needed
        dataSet.setCircleRadius(4f); // Adjust circle size as needed
        dataSet.setDrawCircleHole(false);
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        int dataCount = entries.size();
        if (dataCount > 1) {
            lineChart.getXAxis().setAxisMinimum(entries.get(0).getX()); // Minimum x-value
            lineChart.getXAxis().setAxisMaximum(entries.get(dataCount - 1).getX()); // Maximum x-value
        } else if (dataCount == 1) {
            // Handle case with a single data point if needed
            // For example, set a reasonable range around the single point
            float singleXValue = entries.get(0).getX();
            lineChart.getXAxis().setAxisMinimum(singleXValue - 2f);
            lineChart.getXAxis().setAxisMaximum(singleXValue + 0.5f);
        }
        lineChart.getAxisLeft().setTextColor(Color.parseColor("#000000")); // Change left y-axis label color
        lineChart.getAxisRight().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color
        lineChart.getXAxis().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color
        lineChart.getAxisLeft().setAxisMaximum((float)(max+20));
        lineChart.getAxisLeft().setAxisMinimum((float)(min-20));
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));

        return new Custom_Line(new LineData(dataSet), labels.toArray(new String[0]));
    }

    public Custom_Line getYearLineData3(String[] datapoints, int size, int[] thisColors, String[] timeStamp_value, String  type,LineChart lineChart) {
        ArrayList<Entry> entries = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        List<String> labels = new ArrayList<>();
        Double max=0.0;
        Double min= Double.parseDouble(datapoints[0]);
        for (int i = 0; i < size; i++) {
            /*try {
                if (format.parse(timeStamp_value[i]) != null) {
                    calendar.setTime(Objects.requireNonNull(format.parse(timeStamp_value[i])));
                    int inputYear = calendar.get(Calendar.YEAR);

                    *//*
                            if year is same or it is a previous year
                     *//*

                    if ((currentYear - inputYear) <= 1) {

                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }*/
            Double value= Double.parseDouble(datapoints[i]);
            if (value>max){
                max=value;
            }
            if (value<min){
                min=value;
            }
            entries.add(new Entry(i+10.5f, Float.parseFloat(datapoints[i])));
            labels.add(DateUtil.formatedDate(timeStamp_value[i],"EEE MMM dd HH:mm:ss Z yyyy","MMM dd, yyyy"));

        }

        fixLineIndices(entries);

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColors(thisColors[0]);
        dataSet.setCircleColor(thisColors[0]);
        dataSet.setLineWidth(2f); // Adjust line width as needed
        dataSet.setCircleRadius(4f); // Adjust circle size as needed
        dataSet.setDrawCircleHole(false);
        Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
        dataSet.setValueTypeface(boldTypeface);
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        int dataCount = entries.size();
        if (dataCount > 1) {
            lineChart.getXAxis().setAxisMinimum(entries.get(0).getX()); // Minimum x-value
            lineChart.getXAxis().setAxisMaximum(entries.get(dataCount - 1).getX()); // Maximum x-value
        } else if (dataCount == 1) {
            // Handle case with a single data point if needed
            // For example, set a reasonable range around the single point
            float singleXValue = entries.get(0).getX();
            lineChart.getXAxis().setAxisMinimum(singleXValue - 2f);
            lineChart.getXAxis().setAxisMaximum(singleXValue + 0.5f);
        }
        lineChart.getAxisLeft().setTextColor(Color.parseColor("#000000")); // Change left y-axis label color
        lineChart.getAxisRight().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color
        lineChart.getXAxis().setTextColor(Color.parseColor("#000000")); // Change right y-axis label color
        lineChart.getAxisLeft().setAxisMaximum((float)(max+20));
        lineChart.getAxisLeft().setAxisMinimum((float)(min-20));
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));

        return new Custom_Line(new LineData(dataSet), labels.toArray(new String[0]));
    }

    private void fixIndices(ArrayList<BarEntry> barEntries) {
        int size = barEntries.size();
        int i = size;
        for (BarEntry barEntry : barEntries) {
            barEntry.setX(size - i);
            i--;
        }
    }


    private static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
    private void fixLineIndices(ArrayList<Entry> entries) {
        int size = entries.size();
        int i = size;
        for (Entry entry : entries) {
            entry.setX(size - i);
            i--;
        }
    }

    public static List<Date> getAllDaysOfTheCurrentWeek() {
        List<Date> datesWeek = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Set to the first day of the week

        // Generate a list of dates for the week
        for (int i = 0; i < 7; i++) {
            datesWeek.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_WEEK, 1); // Move to the next day
        }

        return datesWeek;
    }
    private static List<Date> getAllDaysOfTheCurrentMonth() {
        List<Date> datesMonth = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        // Set the calendar to the first day of the current month
        calendar.set(year, month, 1);

        // Generate a list of dates for the month
        while (calendar.get(Calendar.MONTH) == month) {
            datesMonth.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to the next day
        }

        return datesMonth;
    }
}
