package com.cybermed.cdoc_patient.me.vitalcheck.model;


import android.graphics.drawable.Drawable;

public class VitalDataNew {
    private String value;
    private String name;
    private Drawable image;
    private String type;

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
