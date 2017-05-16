package com.android.promosee.models;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.promosee.core.API;
import com.android.promosee.core.DateUtils;
import com.android.promosee.core.ParseJSON;
import com.android.promosee.dialogs.LoadingDialog;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Membercard extends RealmObject {
    @PrimaryKey
    private int id;

    private Date registserDate, startDate, endDate;
    private String nomor, name;
    private int totalStamp;
    private Tenant tenant;

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public Date getRegistserDate() {
        return registserDate;
    }

    public void setRegistserDate(Date registserDate) {
        this.registserDate = registserDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalStamp() {
        return totalStamp;
    }

    public void setTotalStamp(int totalStamp) {
        this.totalStamp = totalStamp;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public static void fromJSONArray(JSONArray response) throws JSONException {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (int i = 0; i < response.length(); i++)
            fromJSON(response.getJSONObject(i), realm);
        realm.commitTransaction();
    }

    public static Membercard fromJSON(JSONObject response, Realm realm) throws JSONException {
        Membercard membercard = new Membercard();
        membercard.setId(ParseJSON.getInt(response.getString("id")));
        membercard.setRegistserDate(DateUtils.fromDateString(response.optString("register_date")));
        membercard.setStartDate(DateUtils.fromDateString(response.optString("start_date")));
        membercard.setEndDate(DateUtils.fromDateString(response.optString("end_date")));
        membercard.setNomor(response.optString("nomor"));
        membercard.setName(response.optString("name"));
        membercard.setTotalStamp(ParseJSON.getInt(response.optString("total_stamp")));

        Tenant tenant = Tenant.get(ParseJSON.getInt(response.optString("id_tenant")));
        membercard.setTenant(tenant);

        realm.copyToRealmOrUpdate(membercard);
        return membercard;
    }

    public static Membercard get(int id) {
        return Realm.getDefaultInstance().where(Membercard.class).equalTo("id", id).findFirst();
    }

    public static void add(final Activity activity, final int tenantID, String code, String id, String address) {
        final LoadingDialog loadingDialog = new LoadingDialog(activity);
        loadingDialog.show();

        JSONObject params = API.getBaseJSONParams(activity);
        try {
            params.put("id_tenant", tenantID);
            params.put("nomor_membercard", code);
            params.put("nomor_ktp", id);
            params.put("address", address);
        } catch (JSONException e) {
            loadingDialog.dismiss();
        }

        API.post(activity, API.BASE_URL + "membercard/add", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (!API.responseIsSuccess(activity, response)) return;

                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                try {
                    fromJSON(response, realm);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                realm.commitTransaction();

                Toast.makeText(activity, "Pendaftaran membercard " + Tenant.get(tenantID).getName() + " berhasil dilakukan",
                        Toast.LENGTH_LONG).show();
                activity.setResult(Activity.RESULT_OK);
                activity.finish();
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

