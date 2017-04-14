package com.android.promosee.core;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by angga on 06/10/16.
 */

public class Validators {

    public static boolean validateLength(View view, int length) {
        if (view instanceof EditText)
            return ((EditText) view).getText().toString().trim().length() >= length;
        else
            return ((TextView) view).getText().toString().trim().length() >= length;
    }

    public static boolean validateEmail(EditText editText) {
        return editText.getText().toString().trim().contains("@");
    }
}
