package com.android.promosee.core;

/**
 * Created by angga on 20/03/17.
 */

public class ParseJSON {

    public static int getInt(String value) {
        if (value.equals("null") || value == null) return 0;
        return Integer.parseInt(value);
    }

    public static double getDouble(String value) {
        if (value.equals("null") || value == null) return 0;
        return Double.parseDouble(value);
    }
}
