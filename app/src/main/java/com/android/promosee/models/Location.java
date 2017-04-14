package com.android.promosee.models;

import com.android.promosee.core.ParseJSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Location extends RealmObject {
    @PrimaryKey
    private int id;

    private String name, address;
    private Double latitude, longitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public static RealmList<Location> fromJSONArray(Realm realm, JSONArray response) throws JSONException {
        RealmList<Location> locations = new RealmList<>();
        for (int i = 0; i < response.length(); i++)
            locations.add(fromJSON(response.getJSONObject(i), realm));

        return locations;
    }

    public static Location fromJSON(JSONObject response, Realm realm) throws JSONException {
        Location location = new Location();
        location.setId(ParseJSON.getInt(response.getString("id")));
        location.setName(response.optString("name"));
        location.setAddress(response.optString("address"));
        location.setLatitude(ParseJSON.getDouble(response.optString("latitude")));
        location.setLongitude(ParseJSON.getDouble(response.optString("longitude")));
        realm.copyToRealmOrUpdate(location);

        return location;
    }

    public static Location get(int id) {
        return Realm.getDefaultInstance().where(Location.class).equalTo("id", id).findFirst();
    }
}

