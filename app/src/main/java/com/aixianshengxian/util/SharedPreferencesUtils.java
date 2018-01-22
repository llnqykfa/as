package com.aixianshengxian.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class SharedPreferencesUtils {
	private static SharedPreferencesUtils mSPU=null;
	public Context context=null;
	private static HashMap<String,SharedPreferences> sharePrefrences =new HashMap<String,SharedPreferences>();

	public static HashMap<String, SharedPreferences> getSharePrefrences() {
		return sharePrefrences;
	}

	public static void setSharePrefrences(HashMap<String, SharedPreferences> sharePrefrences) {
		SharedPreferencesUtils.sharePrefrences = sharePrefrences;
	}

	public static SharedPreferencesUtils getInstance(Context context){
		if(mSPU == null){
			mSPU = new SharedPreferencesUtils();
		}
		mSPU.context=context;
		return mSPU;
	}
	private static SharedPreferences getSharedPreferencesInstance(String xmlName){
		if(!sharePrefrences.containsKey(xmlName)){
			sharePrefrences.put(xmlName,mSPU.context.getSharedPreferences(xmlName, 0));
		}
		return sharePrefrences.get(xmlName);
	}
	public void setSP(String xmlName,String key,Object obj){
		// 存入数据
		Editor editor = getSharedPreferencesInstance(xmlName).edit();
		if(obj.getClass() == String.class){
			editor.putString(key, String.valueOf(obj));
		}else if(obj.getClass() == int.class){
		}else if(obj.getClass() == boolean.class){
			
		}
		editor.commit();
	}
	public void removeConfig(String xmlName,String key){
		Editor editor = getSharedPreferencesInstance(xmlName).edit();
		editor.remove(key);
		editor.commit();
	}
	public void setIntSP(String xmlName,String key,int obj){
		// 存入数据
		Editor editor = getSharedPreferencesInstance(xmlName).edit();
		editor.putInt(key, obj);
		editor.commit();
	}
	
	public void setBooleanSP(String xmlName,String key,boolean obj){
		// 存入数据
		Editor editor = getSharedPreferencesInstance(xmlName).edit();
		editor.putBoolean(key, obj);
		editor.commit();
	}
	
	public int getSP(String xmlName,String key){
		return getSharedPreferencesInstance(xmlName).getInt(key, 0);
	}
	
	public String getStringSP(String xmlName,String key){
		return getSharedPreferencesInstance(xmlName).getString(key, "");
	}
	
	public boolean getBooleanSP(String xmlName,String key){
		return getSharedPreferencesInstance(xmlName).getBoolean(key,false);
	}

	public enum SharePreferenceKeyEnum {
		login_account,
		login_password
	}
}
