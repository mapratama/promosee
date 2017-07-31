package com.android.promosee.models;

import android.util.Log;

import com.android.promosee.core.API;
import com.android.promosee.core.ParseJSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Tenant extends RealmObject {
    @PrimaryKey
    private int id;

    private int orderID;
    private String code, name, type, phone, address, email, logoUrl, bannerUrl, websiteUrl;
    private RealmList<Location> locations;
    private RealmList<Voucher> vouchers;

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public RealmList<Location> getLocations() {
        return locations;
    }

    public RealmList<Voucher> getVouchers() {
        return vouchers;
    }

    public void setVouchers(RealmList<Voucher> vouchers) {
        this.vouchers = vouchers;
    }

    public void setLocations(RealmList<Location> locations) {
        this.locations = locations;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public static RealmList<Tenant> fromJSONArray(Realm realm, JSONArray response) throws JSONException {
        RealmList<Tenant> tenants = new RealmList<>();
        for (int i = 0; i < response.length(); i++)
            tenants.add(fromJSON(response.getJSONObject(i), realm));

        return tenants;
    }

    public static Tenant fromJSON(JSONObject response, Realm realm) throws JSONException {
        Tenant tenant = new Tenant();
        tenant.setId(ParseJSON.getInt(response.getString("id")));
        tenant.setOrderID(ParseJSON.getInt(response.optString("order_id")));
        tenant.setCode(response.optString("code"));
        tenant.setName(response.optString("name"));
        tenant.setType(response.optString("type"));
        tenant.setPhone(response.optString("phone"));
        tenant.setAddress(response.optString("address"));
        tenant.setEmail(response.optString("email"));
        tenant.setLogoUrl(API.IMAGE_URL + response.optString("logo_url"));
        tenant.setBannerUrl(API.IMAGE_URL + response.optString("banner_url"));
        tenant.setWebsiteUrl(API.IMAGE_URL + response.optString("website_url"));

        tenant.setLocations(Location.fromJSONArray(realm, response.getJSONArray("locations")));

        realm.copyToRealmOrUpdate(tenant);

        return tenant;
    }

    public static Tenant get(int id) {
        return Realm.getDefaultInstance().where(Tenant.class).equalTo("id", id).findFirst();
    }

    public Category getCategory() {
        return Realm.getDefaultInstance().where(Category.class).equalTo("tenants.id", id).findFirst();
    }
}

