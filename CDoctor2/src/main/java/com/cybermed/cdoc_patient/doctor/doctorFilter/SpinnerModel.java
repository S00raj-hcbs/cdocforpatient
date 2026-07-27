package com.cybermed.cdoc_patient.doctor.doctorFilter;

import com.cybermed.cdoc_patient.R;

public class SpinnerModel {
    private String filterText;
    private boolean selectedFilter;

    public SpinnerModel(String filterText, boolean selectedFilter) {
        this.filterText = filterText;
        this.selectedFilter = selectedFilter;
    }

    public boolean isSelectedFilter() {
        return selectedFilter;
    }

    public String getFilterText() {
        return filterText;
    }

    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }

    public void setSelectedFilter(boolean selectedFilter) {
        this.selectedFilter = selectedFilter;
    }

    public int getColor() {
        return (selectedFilter ? R.color.azure : R.color.color_4f4f4f);
    }

    public int getImage() {
        return (selectedFilter ? R.drawable.selected_icon : R.drawable.circle_white);
    }

}
