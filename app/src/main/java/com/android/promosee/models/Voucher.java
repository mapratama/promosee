package com.android.promosee.models;

import com.android.promosee.core.API;
import com.android.promosee.core.DateUtils;
import com.android.promosee.core.ParseJSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Voucher extends RealmObject {
    @PrimaryKey
    private int id;

    private String name, subject, description, voucherImageUrl, slideImageUrl, bigImageUrl, redeemCode;
    private Double price, minPayment;
    private Date startDate, endDate;

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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVoucherImageUrl() {
        return voucherImageUrl;
    }

    public void setVoucherImageUrl(String voucherImageUrl) {
        this.voucherImageUrl = voucherImageUrl;
    }

    public String getSlideImageUrl() {
        return slideImageUrl;
    }

    public void setSlideImageUrl(String slideImageUrl) {
        this.slideImageUrl = slideImageUrl;
    }

    public String getBigImageUrl() {
        return bigImageUrl;
    }

    public void setBigImageUrl(String bigImageUrl) {
        this.bigImageUrl = bigImageUrl;
    }

    public String getRedeemCode() {
        return redeemCode;
    }

    public void setRedeemCode(String redeemCode) {
        this.redeemCode = redeemCode;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getMinPayment() {
        return minPayment;
    }

    public void setMinPayment(Double minPayment) {
        this.minPayment = minPayment;
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

    public static void fromJSONArray(JSONArray response) throws JSONException {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (int i = 0; i < response.length(); i++)
            fromJSON(response.getJSONObject(i), realm);
        realm.commitTransaction();
    }

    public static Voucher fromJSON(JSONObject response, Realm realm) throws JSONException {
        Voucher voucher = new Voucher();
        voucher.setId(ParseJSON.getInt(response.getString("id")));
        voucher.setName(response.optString("name"));
        voucher.setSubject(response.optString("subject"));
        voucher.setDescription(response.optString("description"));
        voucher.setPrice(ParseJSON.getDouble(response.getString("price")));
        voucher.setStartDate(DateUtils.fromDateString(response.getString("start_date")));
        voucher.setEndDate(DateUtils.fromDateString(response.getString("end_date")));
        voucher.setVoucherImageUrl(API.IMAGE_URL + response.optString("voucher_image_url"));
        voucher.setSlideImageUrl(API.IMAGE_URL + response.optString("slide_image_url"));
        voucher.setBigImageUrl(API.IMAGE_URL + response.optString("big_image_url"));
        voucher.setMinPayment(ParseJSON.getDouble(response.optString("min_payment")));
        voucher.setRedeemCode(response.optString("redeem_code"));

        Tenant tenant = Tenant.get(ParseJSON.getInt(response.getString("id_tenant")));
        RealmList<Voucher> tenantVouchers = tenant.getVouchers();
        if (tenantVouchers.contains(voucher))
            tenantVouchers.remove(voucher);
        tenantVouchers.add(voucher);

        realm.copyToRealmOrUpdate(voucher);
        return voucher;
    }

    public static Voucher get(int id) {
        return Realm.getDefaultInstance().where(Voucher.class).equalTo("id", id).findFirst();
    }

    public Tenant getTenant() {
        return Realm.getDefaultInstance().where(Tenant.class).equalTo("vouchers.id", id).findFirst();
    }
}

