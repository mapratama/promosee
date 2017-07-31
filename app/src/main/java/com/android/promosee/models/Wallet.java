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


public class Wallet extends RealmObject {
    @PrimaryKey
    private int id;

    private Double amount, balance;
    private Date date;
    private String nomor, status, remark, type;

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static void fromJSONArray(JSONArray response) throws JSONException {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (int i = 0; i < response.length(); i++)
            fromJSON(response.getJSONObject(i), realm);
        realm.commitTransaction();
    }

    public static Wallet fromJSON(JSONObject response, Realm realm) throws JSONException {
        Wallet wallet = new Wallet();
        wallet.setId(ParseJSON.getInt(response.getString("id")));
        wallet.setDate(DateUtils.fromDateString(response.getString("date")));
        wallet.setStatus(response.optString("status"));
        wallet.setType(response.optString("type"));
        wallet.setRemark(response.optString("remark"));
        wallet.setNomor(response.optString("nomor"));
        wallet.setAmount(ParseJSON.getDouble(response.optString("amount")));
        wallet.setBalance(ParseJSON.getDouble(response.optString("balance")));

        realm.copyToRealmOrUpdate(wallet);
        return wallet;
    }

    public static Wallet get(int id) {
        return Realm.getDefaultInstance().where(Wallet.class).equalTo("id", id).findFirst();
    }
}

