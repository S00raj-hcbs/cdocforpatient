package com.cybermed.cdoc_patient.login.signup;



import android.text.TextUtils;

import com.cybermed.cdoc_patient.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {
    public static boolean isEmailAddress(String email) {
        if (email == null || TextUtils.isEmpty(email))
            return false;

        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isPhoneNum(String phone) {
        if (TextUtils.isEmpty(phone))
            return false;
        if (phone.matches("[123456789]{1}\\d{2}-\\d{3}-\\d{4}")) {
            return true;
        } else
            return false;
    }

    public static boolean compareDates(String d1, String d2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);

            if (date1.after(date2)) {
                return true;
            }
            if (date1.before(date2)) {
                return false;
            }

            if (date1.equals(date2)) {
                return true;
            }

        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return false;
    }
}
