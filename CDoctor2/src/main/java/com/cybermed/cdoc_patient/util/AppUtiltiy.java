package com.cybermed.cdoc_patient.util;

import static com.cybermed.cdoc_patient.util.AppConstant.SERVER_DATE_FORMAT;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.me.securemessages.model.ReceivedMessagesItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class AppUtiltiy {
    static int minBpColor = 0;
    static int maxBpColor = 0;

    // Generic function to sort M
    // ap in Java by reverse ordering of its keys
    public static <K extends Comparable, V> Map<K, V> sortByKeys(Map<K, V> map) {
        Map<K, V> treeMap = new TreeMap<>(new Comparator<K>() {
            @Override
            public int compare(K a, K b) {
                return a.compareTo(b);
            }
        });

        treeMap.putAll(map);

        return treeMap;
    }
    public static <V> Map<String, V> sortByKeys2(Map<String, V> map) {
        // Adjust the date format to match your date strings
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM");

        // Use a TreeMap with a custom comparator to sort keys (date strings) in descending order
        Map<String, V> treeMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                try {
                    Date date1 = dateFormat.parse(a);
                    Date date2 = dateFormat.parse(b);
                    assert date2 != null;
                    return date2.compareTo(date1); // Reverse chronological order
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        treeMap.putAll(map);

        return treeMap;
    }

    public static <K extends Comparable, V> Map<K, V> sortByKeys3(Map<K, V> map) {
        Map<K, V> treeMap = new TreeMap<>(new Comparator<K>() {
            @Override
            public int compare(K a, K b) {
                return b.compareTo(a);
            }
        });

        treeMap.putAll(map);

        return treeMap;
    }

    /**
     *
     * @param minBpLevel dia value
     * @param context context
     * @return status of dia bp
     */
    public static String getBpDiaStatus(String minBpLevel, Context context) {
        String status = null;

        if (minBpLevel != null) {
            if (Integer.parseInt(minBpLevel) >= 40 && Integer.parseInt(minBpLevel) <= 60) {
                status = context.getString(R.string.low_bp);
                minBpColor = context.getColor(R.color.color_cb544c);
            } else if (Integer.parseInt(minBpLevel) >= 60 && Integer.parseInt(minBpLevel) <= 80) {
                status = context.getString(R.string.normal_bp);
                minBpColor = context.getColor(R.color.green);
            } else if (Integer.parseInt(minBpLevel) >= 81) {
                status = context.getString(R.string.high_bp);
                minBpColor = context.getColor(R.color.color_cb544c);
            }

        }

        return status;
    }

    /**
     *
     * @return dia status color
     */
    public static int getDiaColor() {
        return minBpColor;
    }

    /**
     *
     * @param maxBpLevel sys value
     * @param context context
     * @return sys status
     */
    public static String getBpSysStatus(String maxBpLevel, Context context) {
        String status = null;
        if (maxBpLevel != null) {
            if (Integer.parseInt(maxBpLevel) >= 70 && Integer.parseInt(maxBpLevel) <= 90) {
                status = context.getString(R.string.low_bp);
                maxBpColor = context.getColor(R.color.color_cb544c);
            } else if (Integer.parseInt(maxBpLevel) >= 91 && Integer.parseInt(maxBpLevel) <= 120) {
                status = context.getString(R.string.normal_bp);
                maxBpColor = context.getColor(R.color.green);
            } else if (Integer.parseInt(maxBpLevel) >= 121) {
                status = context.getString(R.string.high_bp);
                maxBpColor = context.getColor(R.color.color_cb544c);
            }

        }
        return status;
    }

    /**
     *
     * @return sys color
     */
    public static int getSysColor() {
        return maxBpColor;
    }

    public static boolean isDeviceTablet(Activity context){
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float yInches= metrics.heightPixels/metrics.ydpi;
        float xInches= metrics.widthPixels/metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches);
        if (diagonalInches>=6.5){
            // 6.5inch device or bigger
            return  true;
        }else{
            // smaller device
            return false;
        }
    }

    public static class MessageListingComprator implements Comparator<String> {
        private Map<String, List<ReceivedMessagesItem>> sourceMap;

        public MessageListingComprator(Map<String, List<ReceivedMessagesItem>> sourceMap) {
            this.sourceMap = sourceMap;
        }

        @Override
        public int compare(String key1, String key2) {
            // TODO: null checks
            List<ReceivedMessagesItem> student1 = sourceMap.get(key1);
            List<ReceivedMessagesItem> student2 = sourceMap.get(key2);
            SimpleDateFormat format = new SimpleDateFormat(SERVER_DATE_FORMAT,Locale.ENGLISH);
            Date o1CreationDate=null,o2CreationDate=null;
            try {
                assert student1 != null;
                Date date1 = format.parse(student1.get(student1.size()-1).getMsgSendDate());
                assert student2 != null;
                Date date2 = format.parse(student2.get(student2.size()-1).getMsgSendDate());
                o1CreationDate =date1;
                o2CreationDate = date2;

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return Objects.requireNonNull(o2CreationDate).compareTo(o1CreationDate);
        }
    }
}
