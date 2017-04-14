package com.android.promosee.models;

import com.android.promosee.core.DateUtils;
import com.android.promosee.core.ParseJSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Redemption extends RealmObject {
    @PrimaryKey
    private int id;

    private Voucher voucher;
    private Date date;
    private boolean showRedeem;

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isShowRedeem() {
        return showRedeem;
    }

    public void setShowRedeem(boolean showRedeem) {
        this.showRedeem = showRedeem;
    }

    public static void fromJSONArray(JSONArray response) throws JSONException {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (int i = 0; i < response.length(); i++)
            fromJSON(response.getJSONObject(i), realm);
        realm.commitTransaction();
    }

    public static Redemption fromJSON(JSONObject response, Realm realm) throws JSONException {
        Redemption redemption = new Redemption();
        redemption.setId(ParseJSON.getInt(response.getString("id")));
        redemption.setDate(DateUtils.fromDateString(response.optString("date")));

        boolean showRedeem = response.getString("status_redeem").equals("yes") ? true : false;
        redemption.setShowRedeem(showRedeem);

        Voucher voucher = Voucher.get(ParseJSON.getInt(response.optString("id_voucher")));
        redemption.setVoucher(voucher);

        realm.copyToRealmOrUpdate(redemption);
        return redemption;
    }

    public static Redemption get(int id) {
        return Realm.getDefaultInstance().where(Redemption.class).equalTo("id", id).findFirst();
    }
}

