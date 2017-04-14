package com.android.promosee.models;

import com.android.promosee.core.API;
import com.android.promosee.core.ParseJSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class News extends RealmObject {
    @PrimaryKey
    private int id;

    private String title, subject, imageUrl, description;
    private Voucher voucher;

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public static News fromJSON(JSONObject response, Realm realm) throws JSONException {
        News news = new News();
        news.setId(ParseJSON.getInt(response.getString("id")));
        news.setTitle(response.optString("title"));
        news.setSubject(response.optString("subject"));
        news.setImageUrl(API.IMAGE_URL + response.optString("image_url"));
        news.setDescription(response.optString("description"));

        if (!response.isNull("id_voucher")) {
            Voucher voucher = Voucher.get(ParseJSON.getInt(response.getString("id_voucher")));
            news.setVoucher(voucher);
        }

        realm.copyToRealmOrUpdate(news);
        return news;
    }

    public static News get(int id) {
        return Realm.getDefaultInstance().where(News.class).equalTo("id", id).findFirst();
    }
}

