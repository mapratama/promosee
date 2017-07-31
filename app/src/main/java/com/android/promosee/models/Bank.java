package com.android.promosee.models;

import com.android.promosee.core.API;
import com.android.promosee.core.ParseJSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Bank extends RealmObject {
    @PrimaryKey
    private int id;

    private String bankName, rekening, rekeningName, logoUrl;

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getRekening() {
        return rekening;
    }

    public void setRekening(String rekening) {
        this.rekening = rekening;
    }

    public String getRekeningName() {
        return rekeningName;
    }

    public void setRekeningName(String rekeningName) {
        this.rekeningName = rekeningName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public static void fromJSONArray(JSONArray response) throws JSONException {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (int i = 0; i < response.length(); i++)
            fromJSON(response.getJSONObject(i), realm);
        realm.commitTransaction();
    }

    public static Bank fromJSON(JSONObject response, Realm realm) throws JSONException {
        Bank bank = new Bank();
        bank.setId(ParseJSON.getInt(response.getString("id")));
        bank.setBankName(response.optString("nama_bank"));
        bank.setRekening(response.optString("norek"));
        bank.setRekeningName(response.optString("nama_rekening"));
        bank.setLogoUrl(API.IMAGE_URL + response.optString("logo_url"));

        realm.copyToRealmOrUpdate(bank);
        return bank;
    }

    public static Bank get(int id) {
        return Realm.getDefaultInstance().where(Bank.class).equalTo("id", id).findFirst();
    }
}

