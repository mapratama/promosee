package com.android.promosee.models;

import com.android.promosee.core.API;
import com.android.promosee.core.ParseJSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Category extends RealmObject {
    @PrimaryKey
    private int id;

    private int orderID;
    private String name, imageUrl;
    private RealmList<Tenant> tenants;

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public RealmList<Tenant> getTenants() {
        return tenants;
    }

    public void setTenants(RealmList<Tenant> tenants) {
        this.tenants = tenants;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public static void fromJSONArray(JSONArray response) throws JSONException {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (int i = 0; i < response.length(); i++)
            fromJSON(response.getJSONObject(i), realm);
        realm.commitTransaction();
    }

    public static Category fromJSON(JSONObject response, Realm realm) throws JSONException {
        Category category = new Category();
        category.setId(ParseJSON.getInt(response.getString("id")));
        category.setOrderID(ParseJSON.getInt(response.optString("order_id")));
        category.setName(response.optString("name"));
        category.setImageUrl(API.IMAGE_URL + response.optString("image_url"));
        category.setTenants(Tenant.fromJSONArray(realm, response.getJSONArray("tenants")));

        realm.copyToRealmOrUpdate(category);
        return category;
    }

    public static Category get(int id) {
        return Realm.getDefaultInstance().where(Category.class).equalTo("id", id).findFirst();
    }
}

