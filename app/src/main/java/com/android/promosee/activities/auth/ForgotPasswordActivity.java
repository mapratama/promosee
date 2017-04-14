package com.android.promosee.activities.auth;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.android.promosee.R;
import com.android.promosee.activities.BaseActivity;
import com.android.promosee.core.API;
import com.android.promosee.core.Alert;
import com.android.promosee.core.Session;
import com.android.promosee.core.Validators;
import com.android.promosee.dialogs.LoadingDialog;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class ForgotPasswordActivity extends BaseActivity {

    @BindView(R.id.email_edittext) EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        setupBackButtonBar();
    }

    @OnClick(R.id.submit_button)
    public void submitButtonOnClick() {
        if (!Validators.validateEmail(emailEditText))
            Alert.dialog(this, "Email not valid ");
        else {
            final String email = emailEditText.getText().toString();
            JSONObject params = API.getBaseJSONParams(this);
            try {
                params.put("email", email);
            } catch (JSONException e) {
                return;
            }

            final LoadingDialog loadingDialog = new LoadingDialog(this);
            loadingDialog.show();

            final Activity activity = this;
            API.post(this, API.BASE_URL + "auth/forget-password", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (!API.responseIsSuccess(activity, response)) return;
                    Toast.makeText(activity, "Silahkan cek inbox email " + email
                            + " untuk mendapatkan password baru akun PROMOSEE anda", Toast.LENGTH_LONG).show();
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
