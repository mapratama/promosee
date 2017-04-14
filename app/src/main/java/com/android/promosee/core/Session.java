package com.android.promosee.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


import com.android.promosee.activities.MainActivity;
import com.android.promosee.dialogs.LoadingDialog;
import com.android.promosee.models.Bank;
import com.android.promosee.models.Banner;
import com.android.promosee.models.Category;
import com.android.promosee.models.Faq;
import com.android.promosee.models.News;
import com.android.promosee.models.Voucher;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


/**
 * Created by imam on 26/09/16.
 */
public class Session {

    public static void saveUserData(Context context, JSONObject userData) {
        Preferences preferences = new Preferences(context);
        try {
            preferences.set("id", userData.getString("id"));
            preferences.set("name", userData.getString("name"));
            preferences.set("email", userData.getString("email"));
            preferences.set("phone", userData.getString("phone"));
            preferences.set("city", userData.getString("city"));
            preferences.set("address", userData.getString("address"));
            preferences.set("imageUrl", userData.getString("image_url"));
            preferences.set("balance", (long) userData.getDouble("balance"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean isAuthenticated(Context context) {
        Preferences preferences = new Preferences(context);
        return !preferences.getString("id").equals("");
    }

    public static void bootstrap(Activity activity, JSONObject response) {
        try {
            Category.fromJSONArray(response.getJSONArray("categories"));
            Faq.fromJSONArray(response.getJSONArray("faqs"));
            Voucher.fromJSONArray(response.getJSONArray("vouchers"));
            Bank.fromJSONArray(response.getJSONArray("banks"));
            Banner.fromJSONArray(response.getJSONArray("banner"));
            News.fromJSONArray(response.getJSONArray("news_events"));
            saveUserData(activity, response.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    public static void register(final Activity activity, JSONObject params, final String facebookID) {
            final LoadingDialog loadingDialog = new LoadingDialog(activity);
            loadingDialog.show();

            API.post(activity, API.BASE_URL + "auth/register", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (!API.responseIsSuccess(activity, response)) return;

                    Session.bootstrap(activity, response);

                    if (facebookID != null)
                        new Preferences(activity).set("imageUrl"
                                , "http://graph.facebook.com/" + facebookID +"/picture?type=large");
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
