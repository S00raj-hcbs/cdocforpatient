package com.cybermed.cdoc_patient.signalr;

public class QueryStringObj {
    private String user_group;

    private String user_name;

    private String my_org_code;

    private String device_id;

    public QueryStringObj(String user_group, String user_name, String my_org_code, String device_id) {
        this.user_group = user_group;
        this.user_name = user_name;
        this.my_org_code = my_org_code;
        this.device_id = device_id;
    }

    public String getUser_group() {
        return user_group;
    }

    public void setUser_group(String user_group) {
        this.user_group = user_group;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMy_org_code() {
        return my_org_code;
    }

    public void setMy_org_code(String my_org_code) {
        this.my_org_code = my_org_code;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String toString() {
        return "user_group=" + user_group + "&user_name=" + user_name + "&my_org_code=" + my_org_code + "&device_id=" + device_id;
    }
}
