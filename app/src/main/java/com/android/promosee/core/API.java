package com.android.promosee.core;

import android.app.Activity;
import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by imam on 26/09/16.
 */
public class API {
    public static String BASE_URL = "http://admin-promosee.garansee.com/api/";
    public static String IMAGE_URL = "http://admin-promosee.garansee.com/";
    public static String TOKEN = "9090opop";

    private static AsyncHttpClient getHttpClient() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(1, 30);
        return client;
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        getHttpClient().get(url, params, responseHandler);
    }

    public static void post(Context context, String url, JSONObject params, AsyncHttpResponseHandler responseHandler) {
        StringEntity entity = null;
        try {
            entity = new StringEntity(params.toString());
        } catch (UnsupportedEncodingException e) {
            return;
        }
        getHttpClient().post(context, url, entity, "application/json", responseHandler);
    }

    public static void handleFailure(Context context, int statusCode, JSONObject errorResponse) {
        Alert.dialog(context, "Maaf terjadi kesalahan, silahkan coba kembali");

//        String errorMessage = getErrorMessage(statusCode, errorResponse);
//        if (errorMessage != null) Alert.dialog(context, errorMessage);
    }

    public static boolean responseIsSuccess(Activity activity, JSONObject response) {
        if (response.has("status")) {
            try {
                Alert.dialog(activity, response.getString("status"));
            } catch (JSONException e) {}
            return false;
        }

        return true;
    }

    public static String getErrorMessage(int statusCode, JSONObject errorResponse) {
        if (statusCode >= 500) return "Sorry, we had trouble processing your request, Please try again later";
        if (statusCode == 403) return "It seems like your session has expired. Please logout and login again.";
        if (statusCode == 0) return "No Network Connection Available";
        try {
            if (!errorResponse.isNull("detail")) return errorResponse.getString("detail");
        } catch (JSONException e) {}

        return null;
    }

    public static JSONObject getBaseJSONParams(Context context) {
        Preferences preferences = new Preferences(context);
        JSONObject params = new JSONObject();
        try {
            params.put("token", API.TOKEN);
            if (Session.isAuthenticated(context))
                params.put("id_user", preferences.getString("id"));
        } catch (JSONException e) {
            return null;
        }

        return params;
    }

    public static RequestParams getBaseParams(Context context) {
        Preferences preferences = new Preferences(context);
        RequestParams params = new RequestParams();
        params.put("token", API.TOKEN);
        params.put("id_user", preferences.getString("id"));

        return params;
    }

}
