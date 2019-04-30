package com.tc.bubblelayout;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Iterator;
import java.util.Map;

/**
 * 使用SharedPreferences通用数据存储接口
 * <p/>
 * Created by hzj on 2015/10/7.
 */
public class SharedTool {

    private static SharedPreferences sp;
    private static SharedTool sharedTool;

    private SharedTool(Context context) {
        Context mContext = context.getApplicationContext();
        if(mContext == null){
            mContext = context;
        }
        sp = mContext.getSharedPreferences("com.tc.bubblelayout", Activity.MODE_PRIVATE);
    }

    public static SharedTool getInstance(Context context) {
        if (sharedTool == null) {
            synchronized (SharedTool.class) {
                if (sharedTool == null) {
                    sharedTool = new SharedTool(context);
                }
            }
        }
        return sharedTool;
    }

    private SharedPreferences.Editor getEditor() {
        return sp.edit();
    }

    public boolean saveInt(String key, int value) {
        return getEditor().putInt(key, value).commit();
    }

    public int getInt(String key) {
        return sp.getInt(key, 0);
    }

    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public boolean saveLong(String key, long value) {
        return getEditor().putLong(key, value).commit();
    }

    public long getLong(String key) {
        return sp.getLong(key, 0);
    }

    public long getLong(String key, long defautValue) {
        return sp.getLong(key, defautValue);
    }

    public boolean saveString(String key, String value) {
        return getEditor().putString(key, value).commit();
    }

    public String getString(String key) {
        return sp.getString(key, "");
    }

    public boolean saveBoolean(String key, boolean value) {
        return getEditor().putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean def) {
        return sp.getBoolean(key, def);
    }

    public boolean remove(String key) {
        return sp.edit().remove(key).commit();
    }

    public boolean contains(String key) {
        return sp.contains(key);
    }
    
    public void saveStringList(Map<String,String> stringMap){
        if (stringMap == null || stringMap.size() == 0){
            return;
        }
        Iterator<Map.Entry<String,String>> entries = stringMap.entrySet().iterator();
        while (entries.hasNext()){
            Map.Entry<String,String> entry = entries.next();
            sp.edit().putString(entry.getKey(),entry.getValue());
        }
        sp.edit().commit();
    }
}