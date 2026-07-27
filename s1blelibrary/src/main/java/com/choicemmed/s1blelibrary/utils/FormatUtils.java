package com.choicemmed.s1blelibrary.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {
	public static final String template_DbDateTime = "yyyy-MM-dd HH:mm:ss";
	public static final String template_DispalyDateTime = "yyyy/MM/dd HH:mm";
	public static final String template_DownloadDateTime = "yyyy-MM-ddTHH:mm:ss";
	public static final String template_Date = "yyyy-MM-dd";
	public static final String template_Month = "yyyy-MM";

	public static Date parseDate(String dateString, String template)
	{
		SimpleDateFormat format = new SimpleDateFormat(template, Locale.getDefault());
		try {
			return format.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static String getDateTimeString(Date date, String template)
	{
		SimpleDateFormat format = new SimpleDateFormat(template, Locale.getDefault());
		return format.format(date);
	}

	public static String getDoubleString(double value, int decimalDigits)
	{
		if(decimalDigits<1)
		{
			decimalDigits = 1;
		}
		String pattern = "0.";
		for (int i = 0; i < decimalDigits; i++) {
			pattern = pattern + "0";
		}
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(value);
	}

	public static String getDateTimeString(Long timeInMillis, String template){
		Calendar c=Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);
		return getDateTimeString(c.getTime(),template_DbDateTime);
	}
}
