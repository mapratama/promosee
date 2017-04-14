package com.android.promosee.core;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by imam on 26/09/16.
 */
public class Preferences {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public Preferences(Context context) {
        preferences = context.getSharedPreferences("preferences", context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void set(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void set(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void set(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public void set(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void set(String key, JSONArray data) {
        String stringData = data.toString().replace("\"", "");
        set(key, stringData.substring(1, stringData.length() - 1));
    }

    public String getString(String key) {
        return preferences.getString(key, "");
    }

    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }

    public long getLong(String key) {
        return preferences.getLong(key, 0);
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public void delete(String key) {
        preferences.edit().remove(key).commit();
    }

    public Date getDate(String key) {
        return new Date(getLong(key));
    }

    public ArrayList<Integer> getIntegerArray(String key) {
        ArrayList<Integer> result = new ArrayList();
        for (String item : getString(key).split(","))
            result.add(Integer.parseInt(item));

        return result;
    }

    public void clear() {
        editor.clear().commit();
    }
}
