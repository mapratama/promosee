package com.android.promosee.models;

import android.content.Context;

import com.android.promosee.core.API;
import com.android.promosee.core.ParseJSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Banner extends RealmObject {
    @PrimaryKey
    private int id;

    private String url, link;
    private Voucher voucher;

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public static void fromJSONArray(JSONArray response) throws JSONException {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (int i = 0; i < response.length(); i++)
            fromJSON(response.getJSONObject(i), realm);
        realm.commitTransaction();
    }

    public static Banner fromJSON(JSONObject response, Realm realm) throws JSONException {
        Banner banner = new Banner();
        banner.setId(ParseJSON.getInt(response.getString("id")));
        banner.setUrl(API.IMAGE_URL + response.optString("banner"));
        banner.setLink(response.optString("urllink"));

        Voucher voucher = Voucher.get(ParseJSON.getInt(response.getString("id_voucher")));
        banner.setVoucher(voucher);

        realm.copyToRealmOrUpdate(banner);
        return banner;
    }

    public static Banner get(int id) {
        return Realm.getDefaultInstance().where(Banner.class).equalTo("id", id).findFirst();
    }
}

