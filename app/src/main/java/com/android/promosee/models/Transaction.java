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


public class Transaction extends RealmObject {
    @PrimaryKey
    private int id;

    private Voucher voucher;
    private Date date;
    private String type;
    private boolean used;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public static void fromJSONArray(JSONArray response) throws JSONException {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (int i = 0; i < response.length(); i++)
            fromJSON(response.getJSONObject(i), realm);
        realm.commitTransaction();
    }

    public static Transaction fromJSON(JSONObject response, Realm realm) throws JSONException {
        Transaction transaction = new Transaction();
        transaction.setId(ParseJSON.getInt(response.getString("id")));
        transaction.setDate(DateUtils.fromDateString(response.getString("date")));
        transaction.setType(response.optString("name_type_payment"));

        boolean used = response.getString("used").equals("yes") ? true : false;
        transaction.setUsed(used);

        Voucher voucher = Voucher.get(ParseJSON.getInt(response.optString("id_voucher")));
        transaction.setVoucher(voucher);

        realm.copyToRealmOrUpdate(transaction);
        return transaction;
    }

    public static Transaction get(int id) {
        return Realm.getDefaultInstance().where(Transaction.class).equalTo("id", id).findFirst();
    }
}

