package com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.data;

import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Custom;
import com.cybermed.cdoc_patient.bluetooth.iHealth_Devices.smartwatch.vital_graph.utility.Custom_Line;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class DataRepository {

    private JSONObject details;
    String[] details_values;
    String[] timestamp_values;
    String[] detail_highBp;
    String[] detail_lowBp;

    public void getApiResponse(String json) {
        //code to hit api and update details
        try {
            details = new JSONObject(json);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public Custom getWeekData(String category, String[] data, int[] thisColors) {
        try {
            removeDuplicateValues(category, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();


        Calendar calendar = Calendar.getInstance();
        int currentDate = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
        if (data[0].equalsIgnoreCase("distance")) {
            int j = 0;
            for (int i = 0; i < details_values.length; i++) {
                try {
                    if (format.parse(timestamp_values[i]) != null) {
                        calendar.setTime(Objects.requireNonNull(format.parse(timestamp_values[i])));
                        int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                        int inputMonth = calendar.get(Calendar.MONTH);
                        int inputYear = calendar.get(Calendar.YEAR);

                    /*
                            if difference between current date and the date is less than or equal to 7
                     */

                        if ((currentYear == inputYear) && (currentMonth == inputMonth) && (currentDate - inputDate) <= 7) {
                            barEntries.add(new BarEntry(j,
                                    0.621f * Float.parseFloat(details_values[i])));
                            labels.add(0, String.format(Locale.ENGLISH, "%02d/%02d/%d", inputDate, inputMonth + 1, inputYear));
                            j++;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else if (data[0].equals("highBP") && data[1].equals("lowBP")) {
            int j = 0;
            for (int i = 0; i < detail_highBp.length; i++) {
                try {
                    if (format.parse(timestamp_values[i]) != null) {
                        calendar.setTime(Objects.requireNonNull(format.parse(timestamp_values[i])));
                        int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                        int inputMonth = calendar.get(Calendar.MONTH);
                        int inputYear = calendar.get(Calendar.YEAR);

                    /*
                            if difference between current date and the date is less than or equal to 7
                     */

                        if ((currentYear == inputYear) && (currentMonth == inputMonth) && (currentDate - inputDate) <= 7) {
                            barEntries.add(new BarEntry(j, new float[]{Float.parseFloat(detail_highBp[i]), Float.parseFloat(detail_lowBp[i])}));
                            labels.add(0, String.format(Locale.ENGLISH, "%02d/%02d/%d", inputDate, inputMonth + 1, inputYear));
                            j++;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            int j = 0;
            for (int i = 0; i < details_values.length; i++) {
                try {
                    if (format.parse(timestamp_values[i]) != null) {
                        calendar.setTime(Objects.requireNonNull(format.parse(timestamp_values[i])));
                        int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                        int inputMonth = calendar.get(Calendar.MONTH);
                        int inputYear = calendar.get(Calendar.YEAR);

                    /*
                            if difference between current date and the date is less than or equal to 7
                     */

                        if ((currentYear == inputYear) && (currentMonth == inputMonth) && (currentDate - inputDate) <= 7) {
                            barEntries.add(new BarEntry(j, Float.parseFloat(details_values[i])));
                            labels.add(0, String.format(Locale.ENGLISH, "%02d/%02d/%d", inputDate, inputMonth + 1, inputYear));
                            j++;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }


        fixIndices(barEntries);
        String[] label_values = new String[labels.size()];
        label_values = labels.toArray(label_values);
        String labelValue = getCategoryLabel(category, data[0]);
        BarDataSet dataSet = new BarDataSet(barEntries, labelValue);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        dataSet.setColors(thisColors);
        dataSet.setStackLabels(data);
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(1f);
        return new Custom(barData, label_values);
    }

    public void removeDuplicateValues(String category, String[] data) throws JSONException {
        JSONArray dailyDetails = null;
        HashMap<String, String> map = new HashMap<>();
        HashMap<String, String> m = new HashMap<>();

        try {
            dailyDetails = (JSONArray) details.get(category);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (data[0].equals("highBP") && data[1].equals("lowBP")) {
            for (int i = 0; i < dailyDetails.length(); i++) {
                JSONObject detail = null;
                try {
                    detail = (JSONObject) dailyDetails.get(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String[] highbp_value = new String[dailyDetails.length()];
                String[] lowbp_value = new String[dailyDetails.length()];
                String[] time_value = new String[dailyDetails.length()];
                for (int j = 0; j < data.length; j++) {
                    switch (j) {
                        case 0:
                            highbp_value[i] = detail.getString(data[j]);
                            break;
                        case 1:
                            lowbp_value[i] = detail.getString(data[j]);
                            break;
                    }

                    time_value[i] = detail.getString("date");
                    map.put(time_value[i], highbp_value[i]);
                    m.put(time_value[i], lowbp_value[i]);
                }
            }
            detail_highBp = map.values().toArray(new String[0]);
            timestamp_values = map.keySet().toArray(new String[0]);
            detail_lowBp = m.values().toArray(new String[0]);

        } else {
            for (int i = 0; i < dailyDetails.length(); i++) {
                JSONObject detail = null;
                try {
                    detail = (JSONObject) dailyDetails.get(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String[] values = new String[dailyDetails.length()];
                String[] time_value = new String[dailyDetails.length()];
                for (int j = 0; j < data.length; j++) {
                    values[i] = detail.getString(data[j]);
                    time_value[i] = detail.getString("date");
                    map.put(time_value[i], values[i]);
                }
            }
            details_values = map.values().toArray(new String[0]);
            timestamp_values = map.keySet().toArray(new String[0]);

        }
    }


    public void removeDuplicateValues(String category,String data) throws JSONException {
        JSONArray dailyDetails = null;
        HashMap<String, String> map = new HashMap<>();
        try {
            dailyDetails = (JSONArray) details.get(category);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < dailyDetails.length(); i++) {
            JSONObject detail = null;
            try {
                detail = (JSONObject) dailyDetails.get(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String[] values = new String[dailyDetails.length()];
            String[] time_value = new String[dailyDetails.length()];
            values[i] = detail.getString(data);
            time_value[i] = detail.getString("date");
            map.put(time_value[i], values[i]);
        }

        details_values = map.values().toArray(new String[0]);
        timestamp_values = map.keySet().toArray(new String[0]);

    }


    public Custom getMonthData(String category, String[] data, int[] thisColors) {
        try {
            removeDuplicateValues(category, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();


        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
        if (data[0].equalsIgnoreCase("distance")) {
            int j = 0;
            for (int i = 0; i < details_values.length; i++) {
                try {
                    if (format.parse(timestamp_values[i]) != null) {
                        calendar.setTime(Objects.requireNonNull(format.parse(timestamp_values[i])));
                        int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                        int inputMonth = calendar.get(Calendar.MONTH);
                        int inputYear = calendar.get(Calendar.YEAR);

                     /*
                        if years is same and difference between month is 1 then we have to take it
                        otherwise the case is when current month is january so, previous month will be december of previous year
                     */

                        if ((currentYear == inputYear && currentMonth - inputMonth <= 1) || (currentYear - inputYear == 1 && inputMonth - currentMonth == 11)) {
                            barEntries.add(new BarEntry(j,
                                    0.621f * Float.parseFloat(details_values[i])));
                            labels.add(0, String.format(Locale.ENGLISH, "%02d/%02d/%d", inputDate, inputMonth + 1, inputYear));
                            j++;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else if (data[0].equals("highBP") && data[1].equals("lowBP")) {
            int j = 0;
            for (int i = 0; i < detail_highBp.length; i++) {
                try {
                    if (format.parse(timestamp_values[i]) != null) {
                        calendar.setTime(Objects.requireNonNull(format.parse(timestamp_values[i])));
                        int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                        int inputMonth = calendar.get(Calendar.MONTH);
                        int inputYear = calendar.get(Calendar.YEAR);

                    /*
                        if years is same and difference between month is 1 then we have to take it
                        otherwise the case is when current month is january so, previous month will be december of previous year
                     */

                        if ((currentYear == inputYear && currentMonth - inputMonth <= 1) || (currentYear - inputYear == 1 && inputMonth - currentMonth == 11)) {
                            barEntries.add(new BarEntry(j, new float[]{Float.parseFloat(detail_highBp[i]), Float.parseFloat(detail_lowBp[i])}));
                            labels.add(0, String.format(Locale.ENGLISH, "%02d/%02d/%d", inputDate, inputMonth + 1, inputYear));
                            j++;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
          } else {

            for (int i = 0; i < details_values.length; i++) {
                int j = 0;
                try {
                    if (format.parse(timestamp_values[i]) != null) {
                        calendar.setTime(Objects.requireNonNull(format.parse(timestamp_values[i])));
                        int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                        int inputMonth = calendar.get(Calendar.MONTH);
                        int inputYear = calendar.get(Calendar.YEAR);

                    /*
                        if years is same and difference between month is 1 then we have to take it
                        otherwise the case is when current month is january so, previous month will be december of previous year
                     */

                        if ((currentYear == inputYear && currentMonth - inputMonth <= 1) || (currentYear - inputYear == 1 && inputMonth - currentMonth == 11)) {
                            barEntries.add(new BarEntry(j, Float.parseFloat(details_values[i])));
                            labels.add(0, String.format(Locale.ENGLISH, "%02d/%02d/%d", inputDate, inputMonth + 1, inputYear));
                            j++;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }


        fixIndices(barEntries);

        String[] label_values = new String[labels.size()];
        label_values = labels.toArray(label_values);
        String labelValue = getCategoryLabel(category, data[0]);
        BarDataSet dataSet = new BarDataSet(barEntries, labelValue);
        dataSet.setColors(thisColors);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        dataSet.setStackLabels(data);
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(1f);
        return new Custom(barData, label_values);
    }


    public Custom getYearData(String category, String[] data, int[] thisColors) {
        try {
            removeDuplicateValues(category, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        ArrayList<String> labels = new ArrayList<>();


        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
        if (data[0].equalsIgnoreCase("distance")) {
            int j =  0;
            for (int i = 0; i < details_values.length; i++) {
                try {
                    if (format.parse(timestamp_values[i]) != null) {
                        calendar.setTime(Objects.requireNonNull(format.parse(timestamp_values[i])));
                        int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                        int inputMonth = calendar.get(Calendar.MONTH);
                        int inputYear = calendar.get(Calendar.YEAR);

                   /*
                    if year is same or it is a previous year
                    */
                        if ((currentYear - inputYear) <= 1) {
                            barEntries.add(new BarEntry(j,
                                    0.621f * Float.parseFloat(details_values[i])));
                            labels.add(0, String.format(Locale.ENGLISH, "%02d/%02d/%d", inputDate, inputMonth + 1, inputYear));
                            j++;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else if (data[0].equals("highBP") && data[1].equals("lowBP")) {
            int j = 0;
            for (int i = 0; i < detail_highBp.length; i++) {
                try {
                    if (format.parse(timestamp_values[i]) != null) {
                        calendar.setTime(Objects.requireNonNull(format.parse(timestamp_values[i])));
                        int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                        int inputMonth = calendar.get(Calendar.MONTH);
                        int inputYear = calendar.get(Calendar.YEAR);

                   /*
                    if year is same or it is a previous year
                    */
                        if ((currentYear - inputYear) <= 1) {
                            barEntries.add(new BarEntry(j, new float[]{Float.parseFloat(detail_highBp[i]), Float.parseFloat(detail_lowBp[i])}));
                            labels.add(0, String.format(Locale.ENGLISH, "%02d/%02d/%d", inputDate, inputMonth + 1, inputYear));
                            j++;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            int j = 0;
            for (int i = 0; i < details_values.length; i++) {
                try {
                    if (format.parse(timestamp_values[i]) != null) {
                        calendar.setTime(Objects.requireNonNull(format.parse(timestamp_values[i])));
                        int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                        int inputMonth = calendar.get(Calendar.MONTH);
                        int inputYear = calendar.get(Calendar.YEAR);

                   /*
                    if year is same or it is a previous year
                    */
                        if ((currentYear - inputYear) <= 1) {
                            barEntries.add(new BarEntry(j, Float.parseFloat(details_values[i])));
                            labels.add(0, String.format(Locale.ENGLISH, "%02d/%02d/%d", inputDate, inputMonth + 1, inputYear));
                            j++;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        fixIndices(barEntries);
        String[] label_values = new String[labels.size()];
        label_values = labels.toArray(label_values);
        String labelValue = getCategoryLabel(category, data[0]);
        BarDataSet dataSet = new BarDataSet(barEntries, labelValue);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        dataSet.setStackLabels(data);
        dataSet.setColors(thisColors);
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(6f);
        return new Custom(barData, label_values);
    }

    public Custom_Line getWeekLineGraphData(String category, String data, int thisColor) {
        try {
            removeDuplicateValues(category, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<Entry> lineEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();


        Calendar calendar = Calendar.getInstance();
        int currentDate = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);


        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
        if (data.equalsIgnoreCase("temperature")) {
            int j = 0;
            for (int i = 0; i < details_values.length; i++) {
                try {
                    if (format.parse(timestamp_values[i]) != null) {
                        calendar.setTime(Objects.requireNonNull(format.parse(timestamp_values[i])));
                        int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                        int inputMonth = calendar.get(Calendar.MONTH);
                        int inputYear = calendar.get(Calendar.YEAR);

                    /*
                            if difference between current date and the date is less than or equal to 7
                     */

                        if ((currentYear == inputYear) && (currentMonth == inputMonth) && (currentDate - inputDate) <= 7) {
                            float celsius = Float.parseFloat(details_values[i]);
                            float value = ((celsius * 9) / 5) + 32;
                            lineEntries.add(new Entry(j, value));
                            labels.add(j, String.format(Locale.ENGLISH, "%02d/%02d/%d", inputDate, inputMonth + 1, inputYear));
                            j++;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            int j = 0;
            for (int i = 0; i < details_values.length; i++) {
                try {
                    if (format.parse(timestamp_values[i]) != null) {
                        calendar.setTime(Objects.requireNonNull(format.parse(timestamp_values[i])));
                        int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                        int inputMonth = calendar.get(Calendar.MONTH);
                        int inputYear = calendar.get(Calendar.YEAR);

                    /*
                            if difference between current date and the date is less than or equal to 7
                     */

                        if ((currentYear == inputYear) && (currentMonth == inputMonth) && (currentDate - inputDate) <= 7) {
                            lineEntries.add(new Entry(j, Float.parseFloat(details_values[i])));
                            labels.add(j, String.format(Locale.ENGLISH, "%02d/%02d/%d", inputDate, inputMonth + 1, inputYear));
                            j++;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }


        fixLineIndices(lineEntries);
        String[] label_values = new String[labels.size()];
        label_values = labels.toArray(label_values);
        String labelValue = getCategoryLabel(category, data);
        LineDataSet dataSet = new LineDataSet(lineEntries, labelValue);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        dataSet.setColor(thisColor);
        return new Custom_Line(new LineData(dataSet), label_values);
    }

    public Custom_Line getMonthLineGraphData(String category, String data, int thisColor) {
        try {
            removeDuplicateValues(category, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<Entry> lineEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();


        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);


        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
        if (data.equalsIgnoreCase("temperature")) {
            int j = 0;
            for (int i = 0; i < details_values.length; i++) {
                try {
                    if (format.parse(timestamp_values[i]) != null) {
                        calendar.setTime(Objects.requireNonNull(format.parse(timestamp_values[i])));
                        int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                        int inputMonth = calendar.get(Calendar.MONTH);
                        int inputYear = calendar.get(Calendar.YEAR);

                    /*
                        if years is same and difference between month is 1 then we have to take it
                        otherwise the case is when current month is january so, previous month will be december of previous year
                     */

                        if ((currentYear == inputYear && currentMonth - inputMonth <= 1) || (currentYear - inputYear == 1 && inputMonth - currentMonth == 11)) {
                            float celsius = Float.parseFloat(details_values[i]);
                            float value = ((celsius * 9) / 5) + 32;
                            lineEntries.add(new Entry(j, value));
                            labels.add(j, String.format(Locale.ENGLISH, "%02d/%02d/%d", inputDate, inputMonth + 1, inputYear));
                            j++;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            int j = 0;
            for (int i = 0; i < details_values.length; i++) {
                try {
                    if (format.parse(timestamp_values[i]) != null) {
                        calendar.setTime(Objects.requireNonNull(format.parse(timestamp_values[i])));
                        int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                        int inputMonth = calendar.get(Calendar.MONTH);
                        int inputYear = calendar.get(Calendar.YEAR);

                    /*
                        if years is same and difference between month is 1 then we have to take it
                        otherwise the case is when current month is january so, previous month will be december of previous year
                     */

                        if ((currentYear == inputYear && currentMonth - inputMonth <= 1) || (currentYear - inputYear == 1 && inputMonth - currentMonth == 11)) {
                            lineEntries.add(new Entry(j, Float.parseFloat(details_values[i])));
                            labels.add(j, String.format(Locale.ENGLISH, "%02d/%02d/%d", inputDate, inputMonth + 1, inputYear));
                            j++;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        fixLineIndices(lineEntries);
        String[] label_values = new String[labels.size()];
        label_values = labels.toArray(label_values);
        String labelValue = getCategoryLabel(category, data);
        LineDataSet dataSet = new LineDataSet(lineEntries, labelValue);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        dataSet.setColor(thisColor);
        return new Custom_Line(new LineData(dataSet), label_values);
    }

    public Custom_Line getYearLineGraphData(String category, String data, int thisColor) {
        try {
            removeDuplicateValues(category, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<Entry> lineEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();


        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);


        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
        if (data.equalsIgnoreCase("temperature")) {
            int j = 0;
            for (int i = 0; i < details_values.length; i++) {
                try {
                    if (format.parse(timestamp_values[i]) != null) {
                        calendar.setTime(Objects.requireNonNull(format.parse(timestamp_values[i])));
                        int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                        int inputMonth = calendar.get(Calendar.MONTH);
                        int inputYear = calendar.get(Calendar.YEAR);
                     /*
                        if year is same or it is a previous year
                    */
                        if ((currentYear - inputYear) <= 1) {
                            float celsius = Float.parseFloat(details_values[i]);
                            float value = ((celsius * 9) / 5) + 32;
                            lineEntries.add(new Entry(j, value));
                            labels.add(j, String.format(Locale.ENGLISH, "%02d/%02d/%d", inputDate, inputMonth + 1, inputYear));
                            j++;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            int j = 0;
            for (int i = 0; i < details_values.length; i++) {
                try {
                    if (format.parse(timestamp_values[i]) != null) {
                        calendar.setTime(Objects.requireNonNull(format.parse(timestamp_values[i])));
                        int inputDate = calendar.get(Calendar.DAY_OF_MONTH);
                        int inputMonth = calendar.get(Calendar.MONTH);
                        int inputYear = calendar.get(Calendar.YEAR);

                    /*
                        if year is same or it is a previous year
                    */
                        if ((currentYear - inputYear) <= 1) {
                            lineEntries.add(new Entry(j, Float.parseFloat(details_values[i])));
                            labels.add(j, String.format(Locale.ENGLISH, "%02d/%02d/%d", inputDate, inputMonth + 1, inputYear));
                            j++;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        fixLineIndices(lineEntries);
        String[] label_values = new String[labels.size()];
        label_values = labels.toArray(label_values);
        String labelValue = getCategoryLabel(category, data);
        LineDataSet dataSet = new LineDataSet(lineEntries, labelValue);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.format(Locale.ENGLISH, "%.1f", value));
        dataSet.setColor(thisColor);
        return new Custom_Line(new LineData(dataSet), label_values);
    }

    public String getCategoryLabel(String category, String data) {
        String label = "";
        if (category != null) {
            if (category.equalsIgnoreCase("smart_daily")) {
                if (data.equalsIgnoreCase("distance")) {
                    label = "Distance in Miles";
                } else if (data.equalsIgnoreCase("calories")) {
                    label = "Calories in Kcal";
                } else {
                    label = "Steps";
                }
            } else if (category.equalsIgnoreCase("smart_bp")) {
                label = "Blood Oxygen in %";

            } else if (category.equalsIgnoreCase("smart_temp")) {
                label = "Temperature in Farenheit";

            } else if (category.equalsIgnoreCase("smart_hrv")) {
                if (data.equalsIgnoreCase("highBP") || data.equalsIgnoreCase("lowBP")) {
                    label = "in mmhg";
                }
                if (data.equalsIgnoreCase("heartRate")) {
                    label = "Heart Rate in Bpm";
                }
            }
        }
        return label;
    }

    private void fixLineIndices(ArrayList<Entry> barEntries) {
        int size = barEntries.size();
        int i = size;
        for (Entry entry : barEntries) {
            entry.setX(size - i);
            i--;
        }
    }

    private void fixIndices(ArrayList<BarEntry> barEntries) {
        int size = barEntries.size();
        int i = 1;
        for (BarEntry barEntry : barEntries) {
            barEntry.setX(size - i);
            i++;
        }
    }
}
