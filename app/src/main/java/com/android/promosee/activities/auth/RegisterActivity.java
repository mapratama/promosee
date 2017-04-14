package com.android.promosee.activities.auth;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.promosee.R;
import com.android.promosee.activities.BaseActivity;
import com.android.promosee.core.API;
import com.android.promosee.core.Alert;
import com.android.promosee.dialogs.LoadingDialog;
import com.android.promosee.core.Preferences;
import com.android.promosee.core.Session;
import com.android.promosee.core.Validators;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.first_name_edittext) EditText firstNameEditText;
    @BindView(R.id.last_name_edittext) EditText lastNameEditText;
    @BindView(R.id.email_edittext) EditText emailEditText;
    @BindView(R.id.phone_edittext) EditText phoneEditText;
    @BindView(R.id.password_edittext) EditText passwordEditText;
    @BindView(R.id.confirm_password_edittext) EditText confirmPasswordEditText;
    @BindView(R.id.city_edittext) EditText cityEditText;
    @BindView(R.id.referral_code_edittext) EditText referralCodeEditText;
    @BindView(R.id.title_text) TextView titleTextView;
    @BindView(R.id.register_button) Button registerButton;

    public static final int ADD = 1, EDIT = 2, VIEW = 3;
    private int action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        setupBackButtonBar();

        action = getIntent().getIntExtra("action", 0);

        if (action != ADD) {
            Preferences preferences = new Preferences(this);
            firstNameEditText.setText(preferences.getString("name"));
            emailEditText.setText(preferences.getString("email"));
            phoneEditText.setText(preferences.getString("phone"));
            passwordEditText.setText("0123456789");
            confirmPasswordEditText.setText("0123456789");
            cityEditText.setText(preferences.getString("city"));

            if (action == VIEW) {
                firstNameEditText.setEnabled(false);
                lastNameEditText.setEnabled(false);
                emailEditText.setEnabled(false);
                phoneEditText.setEnabled(false);
                passwordEditText.setEnabled(false);
                confirmPasswordEditText.setEnabled(false);
                cityEditText.setEnabled(false);

                lastNameEditText.setText("-");
                registerButton.setVisibility(View.GONE);
                titleTextView.setText("PROFILE");
            }
            else {
                titleTextView.setText("EDIT PROFILE");
                registerButton.setText("EDIT PROFILE");
            }
        }
        else titleTextView.setText("REGISTER");
    }

    @OnClick(R.id.register_button)
    public void registerButtonOnClick() {
        if (!Validators.validateLength(firstNameEditText, 3))
            Alert.dialog(this, "First name is required");
        else if (!Validators.validateEmail(emailEditText))
            Alert.dialog(this, "Email not valid ");
        else if (!Validators.validateLength(phoneEditText, 8))
            Alert.dialog(this, "Phone is required");
        else if (!Validators.validateLength(passwordEditText, 4))
            Alert.dialog(this, "Password minimum 4 character");
        else if (!Validators.validateLength(confirmPasswordEditText, 4))
            Alert.dialog(this, "Confirm password is required");
        else if (!Validators.validateLength(cityEditText, 1))
            Alert.dialog(this, "City is required");
        else if (!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString()))
            Alert.dialog(this, "Password is mismatch");
        else {
            JSONObject params = API.getBaseJSONParams(this);
            try {
                params.put("name", firstNameEditText.getText().toString() + " " +
                        lastNameEditText.getText().toString());

                params.put("email", emailEditText.getText().toString());
                params.put("phone", phoneEditText.getText().toString());
                params.put("password", passwordEditText.getText().toString());
                params.put("city", cityEditText.getText().toString());
                params.put("referral_code", referralCodeEditText.getText().toString());
                params.put("fb_token", "");
                params.put("gcm_token", new Preferences(this).getString("fcmToken"));
            } catch (JSONException e) {
                return;
            }

            if (action == ADD) {
                Session.register(this, params, null);
                return;
            }

            final LoadingDialog loadingDialog = new LoadingDialog(this);
            loadingDialog.show();

            final Activity activity = this;
            API.post(this, API.BASE_URL + "auth/edit_profile", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (!API.responseIsSuccess(activity, response)) return;

                    Session.saveUserData(activity, response);
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      JSONObject errorResponse) {
                    API.handleFailure(activity, statusCode, errorResponse);
                }

                @Override
                public void onFinish() {
                    loadingDialog.dismiss();
                }
            });
        }
    }
}
