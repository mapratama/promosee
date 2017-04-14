package com.android.promosee.activities.auth;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.android.promosee.R;
import com.android.promosee.activities.MainActivity;
import com.android.promosee.core.API;
import com.android.promosee.core.Alert;
import com.android.promosee.core.Preferences;
import com.android.promosee.dialogs.LoadingDialog;
import com.android.promosee.core.Session;
import com.android.promosee.core.Validators;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.email_edittext) EditText emailEditText;
    @BindView(R.id.password_edittext) EditText passwordEditText;

    private FirebaseAuth firebaseAuth;
    private CallbackManager callbackManager;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        activity = this;
        firebaseAuth = FirebaseAuth.getInstance();

        if (Session.isAuthenticated(this)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                JSONObject params = API.getBaseJSONParams(activity);
                                try {
                                    params.put("name", object.optString("name"));
                                    params.put("email", object.optString("email"));
                                    params.put("phone", "");
                                    params.put("password", object.optString("id"));
                                    params.put("city", "");
                                    params.put("referral_code", "");
                                    params.put("fb_token", loginResult.getAccessToken().getToken());
                                    params.put("gcm_token", new Preferences(activity).getString("fcmToken"));
                                } catch (JSONException e) {
                                    return;
                                }

                                Session.register(activity, params, object.optString("id"));
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Alert.dialog(activity, "Error sign in by Facebook, Please try again later.");
            }

            @Override
            public void onError(FacebookException error) {
                Alert.dialog(activity, "Error sign in by Facebook, Please try again later.");
            }
        });
    }

    @OnClick(R.id.facebook_button)
    public void facebookButtonOnClick() {
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("user_photos", "email", "user_birthday", "public_profile"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                            Toast.makeText(getApplicationContext(),
                                    "Successfully Sign Up With Facebook",
                                    Toast.LENGTH_SHORT).show();
                        else Alert.dialog(getApplicationContext(), "Error sign in by Facebook, Please try again later.");
                    }
                });
    }

    @OnClick(R.id.register_button)
    public void registerButtonOnClick() {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("action", RegisterActivity.ADD);
        startActivity(intent);
    }

    @OnClick(R.id.forget_password)
    public void forgetPasswordOnClick() {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    @OnClick(R.id.signin_button)
    public void signinButtonOnClick() {
        if (!Validators.validateEmail(emailEditText))
            Alert.dialog(this, "Email not valid ");
        else if (!Validators.validateLength(passwordEditText, 1))
            Alert.dialog(this, "Password is required");
        else {
            final LoadingDialog loadingDialog = new LoadingDialog(this);
            loadingDialog.show();

            RequestParams params = API.getBaseParams(this);
            params.put("email", emailEditText.getText().toString());
            params.put("password", passwordEditText.getText().toString());
            params.put("gcm_token", new Preferences(activity).getString("fcmToken"));

            API.get(API.BASE_URL + "auth/login", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (!API.responseIsSuccess(activity, response)) return;
                    Session.bootstrap(activity, response);
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
