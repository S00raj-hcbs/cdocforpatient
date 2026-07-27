package com.cdfortis.datainterface.soap.model;

import com.cdfortis.datainterface.JsonSerializable;
import com.cdfortis.datainterface.annotation.DataField;

import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

/**
 * Created by qinwe on 2017/5/5.
 */

public class DocInfo extends SoapObjectData implements JsonSerializable {

    @DataField
    public String last_name;
    @DataField
    public String first_name;
    @DataField
    public String mi; //em
    @DataField
    public String name_prefix; //
    @DataField
    public String name_suffix;//
    @DataField
    public String provider_code;
    @DataField
    public String org_code;
    @DataField
    public String online_status;// check
    @DataField
    public String online_room;//
    @DataField
    public String picture;//
    @DataField
    public String specialties;//support
    @DataField
    public String languages;//english
    @DataField
    public String review_score; // 5
    @DataField
    public String addr1;
    @DataField
    public String addr2;
    @DataField
    public String city;
    @DataField
    public String state;
    @DataField
    public String zip;
    @DataField
    public String initial_min; //""
    @DataField
    public String initial_charge;//""
    @DataField
    public String incremental_min;
    @DataField
    public String incremental_charge;
    @DataField
    public String favorite; //""
   /* @DataField
    private String support_provider;*/

    public DocInfo(SoapObject soapObject) {
        super(soapObject);
    }

    public DocInfo() {
    }

    @Override
    public void deserialize(JSONObject jsonObject) {
        last_name = jsonObject.optString("last_name", "");
        first_name = jsonObject.optString("first_name", "");
        mi = jsonObject.optString("mi", "");
        name_prefix = jsonObject.optString("name_prefix", "");
        name_suffix = jsonObject.optString("name_suffix", "");
        provider_code = jsonObject.optString("provider_code", "");
        org_code = jsonObject.optString("org_code", "");
        online_status = jsonObject.optString("online_status", "");
        online_room = jsonObject.optString("online_room", "");
        picture = jsonObject.optString("picture", "");
        specialties = jsonObject.optString("specialties", "");
        languages = jsonObject.optString("languages", "");
        review_score = jsonObject.optString("review_score", "");
        addr1 = jsonObject.optString("addr1", "");
        addr2 = jsonObject.optString("addr2", "");
        city = jsonObject.optString("city", "");
        state = jsonObject.optString("state", "");
        zip = jsonObject.optString("zip", "");
       // support_provider=jsonObject.optString("support_provider","");
        initial_min = jsonObject.optString("initial_min", "");
        initial_charge = jsonObject.optString("initial_charge", "");
        incremental_min = jsonObject.optString("incremental_min", "");
        incremental_charge = jsonObject.optString("incremental_charge", "");
        incremental_charge = jsonObject.optString("favorite", "");
    }

    @Override
    public void serialize(JSONObject jsonObject) {

    }

    public String getLast_name() {
        return last_name == null ? "" : last_name.substring(0,1)+last_name.substring(1).toLowerCase();
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name == null ? "" : first_name.substring(0,1)+first_name.substring(1).toLowerCase();
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getMi() {

        return mi == null ? "" : mi.substring(0,1)+mi.substring(1).toLowerCase();
    }

    public void setMi(String mi) {
        this.mi = mi;
    }

    public String getName_prefix() {

        return name_prefix == null ? "" : name_prefix.substring(0,1)+name_prefix.substring(1).toLowerCase();
    }

    public void setName_prefix(String name_prefix) {
        this.name_prefix = name_prefix;
    }

    public String getName_suffix() {
        return name_suffix == null ? "" : name_suffix.substring(0,1)+name_suffix.substring(1).toLowerCase();
    }

    public void setName_suffix(String name_suffix) {
        this.name_suffix = name_suffix;
    }

    public String getProvider_code() {
        return provider_code == null? "": provider_code;
    }

    public void setProvider_code(String provider_code) {
        this.provider_code = provider_code;
    }

    public String getOrg_code() {
        return org_code == null? "": org_code;
    }

    public void setOrg_code(String org_code) {
        this.org_code = org_code;
    }

    public String getOnline_status() {
        return online_status == null ? "0" : online_status;
    }

    public void setOnline_status(String online_status) {
        this.online_status = online_status;
    }

    public String getOnline_room() {
        return online_room;
    }

    public void setOnline_room(String online_room) {
        this.online_room = online_room;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getSpecialties() {
        return specialties;
    }

    public void setSpecialties(String specialties) {
        this.specialties = specialties;
    }

    public String getLanguages() {
        return languages == null? "": languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getReview_score() {
//        if (review_score == null || review_score.equals("")) {
//            return "5";
//        }
        return review_score==null?"0.0":review_score;
    }

    public void setReview_score(String review_score) {
        this.review_score = review_score;
    }

    public String getAddr1() {
        return addr1 == null? "": addr1;
    }

    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }

    public String getAddr2() {
        return addr2 == null? "": addr2;
    }

    public void setAddr2(String addr2) {
        this.addr2 = addr2;
    }

    public String getCity() {
        return city == null? "": city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state == null? "": state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip == null? "": zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getInitial_min() {
        return initial_min;
    }

    public void setInitial_min(String initial_min) {
        this.initial_min = initial_min;
    }

    public String getInitial_charge() {
        return initial_charge;
    }

    public void setInitial_charge(String initial_charge) {
        this.initial_charge = initial_charge;
    }

    public String getIncremental_min() {
        return incremental_min;
    }

    public void setIncremental_min(String incremental_min) {
        this.incremental_min = incremental_min;
    }

    public String getIncremental_charge() {
        return incremental_charge;
    }

    public void setIncremental_charge(String incremental_charge) {
        this.incremental_charge = incremental_charge;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    /*public String getSupport_provider() {
        return support_provider==null?"":support_provider;
    }*/
   /* public String getSupport_provider() {
        return support_provider==null?"0":support_provider;
    }

    public void setSupport_provider(String support_provider) {
        this.support_provider = support_provider;
    }*/
}
