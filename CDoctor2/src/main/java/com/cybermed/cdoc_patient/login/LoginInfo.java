package com.cybermed.cdoc_patient.login;

/**
 * Created by qinwe on 2017/4/28.
 */


import android.content.Context;
import android.content.SharedPreferences;

import com.cdfortis.datainterface.soap.UserInfo;
import com.cybermed.cdoc_patient.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户登录记录
 */
public class LoginInfo {
    private static final String SP_PATH = "class com.cdfortis.gophar.a.r";
    private String account;
    private String pwd;
    private String IMEIID;//设备号imei
    private String oneSignalUserId;
    private String getUiUserId;
    private boolean isRegistDebugTopic = false;
    private Context context;
    private UserInfo userInfo;
    private boolean isAuthRep;
    private String originalAccount;
    private String triageConfig;

    public LoginInfo(Context context) {
        this.context = context;
        load();
    }

    public boolean isRegistDebugTopic() {
        return isRegistDebugTopic;
    }

    public void setRegistDebugTopic(boolean registDebugTopic) {
        isRegistDebugTopic = registDebugTopic;
    }

    public String getIMEIID() {
        return IMEIID;
    }

    public void setIMEIID(String IMEIID) {
        this.IMEIID = IMEIID;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getOneSignalUserId() {
        return oneSignalUserId;
    }

    public void setOneSignalUserId(String oneSignalUserId) {
        this.oneSignalUserId = oneSignalUserId;
        //Log.d("ONESIGNAL",this.oneSignalUserId);
    }

    public String getGetUiUserId() {
        return getUiUserId;
    }

    public void setGetUiUserId(String getUiUserId) {
        this.getUiUserId = getUiUserId;
    }

    public boolean isAuthRep() {
        return isAuthRep;
    }

    public void setAuthRep(boolean authRep) {
        isAuthRep = authRep;
    }

    public String getOriginalAccount() {
        return originalAccount;
    }

    public void setOriginalAccount(String originalAccount) {
        this.originalAccount = originalAccount;
    }

    public void load(){
        SharedPreferences preferences = context.getSharedPreferences(SP_PATH,context.MODE_PRIVATE);
        account = preferences.getString("account",null);
        pwd = preferences.getString("pwd",null);
        IMEIID = preferences.getString("deviceId",null);
        oneSignalUserId = preferences.getString("oneSignalUserId",null);
        getUiUserId = preferences.getString("getUiUserId",null);
        isRegistDebugTopic = preferences.getBoolean("debugTopic", BuildConfig.DEBUG);
        isAuthRep = preferences.getBoolean("isAuthRep", false);
        originalAccount = preferences.getString("originalAccount", null);
        String temp = preferences.getString("userInfo","");
        if (temp != null){
            userInfo = new UserInfo();
            try {
                JSONObject jsonObject = new JSONObject(temp);
                userInfo.deserialize(jsonObject);
            } catch (JSONException e) {
            }
        }else {
            account = "";
        }
    }

    public void save(){
        SharedPreferences preferences = context.getSharedPreferences(SP_PATH,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("account",account);
        editor.putString("pwd",pwd);
        editor.putString("deviceId", IMEIID);
        editor.putString("oneSignalUserId",oneSignalUserId);
        editor.putString("getUiUserId",getUiUserId);
        editor.putBoolean("debugTopic",isRegistDebugTopic);
        if (userInfo != null){
            JSONObject object = new JSONObject();
            userInfo.serialize(object);
            editor.putString("userInfo",object.toString());
        }else {
            editor.remove("userInfo");
        }

        editor.commit();
    }

    public void saveAuthRep() {
        SharedPreferences preferences = context.getSharedPreferences(SP_PATH, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("originalAccount", originalAccount);
        editor.putBoolean("isAuthRep", isAuthRep);
        editor.commit();
    }

    public void clear(){
        account = null;
        IMEIID = null;
        oneSignalUserId = null;
        getUiUserId = null;
        isRegistDebugTopic = false;
        save();
    }

    public String getTriageConfig() {
        return triageConfig!=null?triageConfig:"";
    }

    public void setTriageConfig(String triageConfig) {
        this.triageConfig = triageConfig;
    }
}


