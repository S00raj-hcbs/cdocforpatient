package com.cybermed.cdoc_patient.doctor.docDetail.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.util.ArrayList;

/**
 * booking calender model
 */
public class DayDateModel extends BaseObservable {
    /**
     * day of week
     */
    private String dayOfWeek;
    /**
     * date of week
     */
    private String dateOfWeek;
    /**
     * calneder date selected
     */
    private boolean isSelected;
    /**
     * date in format MM/dd/yyyy
     */
    private String date;
    /**
     * month in number eg. 08
     */
    private String monthNumber;
    /**
     * year
     */
    private String year;
    /**
     * model type of list
     */
    private ArrayList<DayDateModel> calenderDayDateList;
    /**
     * month in string eg. March
     */
   private String monthString;

    public DayDateModel(String day, String date, boolean isSelected, String fullDate, String monthNumber, String year, String monthString) {
        this.dayOfWeek = day;
        this.dateOfWeek = date;
        this.isSelected = isSelected;
        this.date = fullDate;
        this.monthNumber=monthNumber;
        this.year=year;
        this.monthString=monthString;
    }

    @Bindable
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        notifyPropertyChanged(BR.selected);
    }

    @Bindable
    public String getDateOfWeek() {
        return dateOfWeek;
    }

    public void setDateOfWeek(String dateOfWeek) {
        this.dateOfWeek = dateOfWeek;
        notifyPropertyChanged(BR.dateOfWeek);
    }

    @Bindable
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        notifyPropertyChanged(BR.dayOfWeek);
    }

    @Bindable
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        notifyPropertyChanged(BR.date);
    }
    @Bindable
    public String getMonthNumber() {
        return monthNumber;
    }

    public void setMonthNumber(String monthNumber) {
        this.monthNumber = monthNumber;
        notifyPropertyChanged(BR.monthNumber);
    }
    @Bindable
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
        notifyPropertyChanged(BR.year);
    }
    @Bindable
    public ArrayList<DayDateModel> getCalenderDayDateList() {
        return calenderDayDateList;
    }

    public void setCalenderDayDateList(ArrayList<DayDateModel> calenderDayDateList) {
        this.calenderDayDateList = calenderDayDateList;
        notifyPropertyChanged(BR.calenderDayDateList);
    }

    public String getMonthString() {
        return monthString;
    }

    public void setMonthString(String monthString) {
        this.monthString = monthString;
    }
}
