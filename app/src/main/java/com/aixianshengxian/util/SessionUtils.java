package com.aixianshengxian.util;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xmzynt.storm.basic.idname.IdName;
import com.xmzynt.storm.service.user.merchant.Merchant;


/**
 * Created by CWJ on 2016/12/19.
 */

public class SessionUtils {
    private static SessionUtils instance;
    private SharedPreferencesUtils sharedPreferencesUtils;
    private static final String xmlFileName="hechamall";
    public static SessionUtils getInstance(Context context) {
        if (instance == null) {
            instance = new SessionUtils();
        }
        if (instance.sharedPreferencesUtils == null) {
            instance.sharedPreferencesUtils = SharedPreferencesUtils.getInstance(context);
        }
        return instance;
    }

    public Merchant getLoginMerchan() {
        String userInfoStr = instance.sharedPreferencesUtils.getStringSP(xmlFileName, "login_user");
        if(userInfoStr !=null && !TextUtils.isEmpty(userInfoStr)){
            Gson gson = new Gson();
            return gson.fromJson(userInfoStr, Merchant.class);
        }
        return null;
    }

    public void saveLoginMerchant(Merchant merchant) {
        Gson gson = new Gson();
        if(merchant != null){
            instance.sharedPreferencesUtils.setSP(xmlFileName, "login_user", gson.toJson(merchant));
        }
    }

    public IdName getOrg() {
        String orgStr = instance.sharedPreferencesUtils.getStringSP(xmlFileName,"org");
        if(orgStr !=null && !TextUtils.isEmpty(orgStr)){
            Gson gson = new Gson();
            return gson.fromJson(orgStr,IdName.class);
        }
        return null;
    }

    public void saveOrg(IdName org) {
        Gson gson = new Gson();
        if (org != null) {
            instance.sharedPreferencesUtils.setSP(xmlFileName,"org",gson.toJson(org));
        }
    }
    public String getSessionId() {
        String sessionId = instance.sharedPreferencesUtils.getStringSP(xmlFileName,"sessionId");
        if(sessionId != null) {
            return sessionId;
        }
        return null;
    }

    public void saveSessionId(String sessionId) {
        if(sessionId != null) {
            instance.sharedPreferencesUtils.setSP(xmlFileName,"sessionId",sessionId);
        }
    }
    public String getCustomerUUID() {
        String customerUUID = instance.sharedPreferencesUtils.getStringSP(xmlFileName, "customerUUID");
        if(customerUUID !=null){

            return customerUUID;
        }
        return null;
    }

    public void saveCustomerUUID( String customerUUID ) {

        if(customerUUID != null){
            instance.sharedPreferencesUtils.setSP(xmlFileName, "customerUUID",customerUUID);
        }
    }

    public String getCustomerName() {
        String CustomerName = instance.sharedPreferencesUtils.getStringSP(xmlFileName, "CustomerName");
        if(CustomerName !=null ){

            return CustomerName;
        }
        return null;
    }

    public void saveCustomerName( String customerName ) {

        if(customerName != null){
            instance.sharedPreferencesUtils.setSP(xmlFileName, "CustomerName",customerName);
        }
    }
    public void saveLoginPhone(String phone){
        if(phone != null){
            instance.sharedPreferencesUtils.setSP(xmlFileName, "login_phone", phone);
        }

    }
    public String getLoginPhone(){
        String userPhone = instance.sharedPreferencesUtils.getStringSP(xmlFileName, "login_phone");

        if(userPhone !=null && !TextUtils.isEmpty(userPhone)){
            return userPhone ;
        }
        return null ;
    }
    public void saveLoginPwd(String pwd){
        if(pwd != null){
            instance.sharedPreferencesUtils.setSP(xmlFileName, "login_pwd", pwd);
        }

    }
    public String getLoginPwd(){
        String pwd = instance.sharedPreferencesUtils.getStringSP(xmlFileName, "login_pwd");

        if(pwd !=null && !TextUtils.isEmpty(pwd)){
            return pwd ;
        }
        return null ;
    }

    public void saveLoginState(boolean isLogin){
        instance.sharedPreferencesUtils.setBooleanSP(xmlFileName,"open",isLogin);

    }
    public boolean getLoginState(){
        boolean state = instance.sharedPreferencesUtils.getBooleanSP(xmlFileName,"open");
        return state;
    }

    public void saveKeyValue(String key,String value){
        instance.sharedPreferencesUtils.setSP(xmlFileName, key,value);
    }

    public String getValue(String key){
        return instance.sharedPreferencesUtils.getStringSP(xmlFileName, key);
    }
}
