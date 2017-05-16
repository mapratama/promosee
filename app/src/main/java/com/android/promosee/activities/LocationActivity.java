package com.android.promosee.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.promosee.R;
import com.android.promosee.activities.partners.PartnerDetailsActivity;
import com.android.promosee.core.Alert;
import com.android.promosee.core.Utils;
import com.android.promosee.dialogs.FilterCategoryDialog;
import com.android.promosee.models.Category;
import com.android.promosee.models.Tenant;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class LocationActivity extends LocationBaseActivity implements GoogleMap.OnMarkerClickListener {

    private String categorySelected;
    private Realm realm;
    private boolean justNearby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        viewID = R.layout.activity_location;
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setupBackButtonBar();

        justNearby = getIntent().getBooleanExtra("nearby", false);
        realm = Realm.getDefaultInstance();
        categorySelected = FilterCategoryDialog.TAMPILKAN_SEMUA;
    }

    public void showTenantLocations(String categoryName) {
        googleMap.clear();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.current_location_icon));
        googleMap.addMarker(markerOptions.position(location));
        builder.include(location);

        RealmList<com.android.promosee.models.Location> locations = new RealmList<>();
        ArrayList<Integer> categoryIDs = new ArrayList<>();
        if (categoryName == null) {
            for (Category category : realm.where(Category.class).findAll()) {
                for (Tenant tenant : category.getTenants()) {
                    for (com.android.promosee.models.Location location : tenant.getLocations()) {
                        locations.add(location);
                        categoryIDs.add(category.getId());
                    }
                }
            }
        }
        else {
            Category category = realm.where(Category.class).equalTo("name", categoryName).findFirst();
            int categoryID = category.getId();
            for (Tenant tenant : category.getTenants()) {
                for (com.android.promosee.models.Location location : tenant.getLocations()) {
                    locations.add(location);
                    categoryIDs.add(categoryID);
                }
            }
        }

        for (int i = 0; i < locations.size(); i++) {
            com.android.promosee.models.Location location = locations.get(i);
            LatLng voucherLocation = new LatLng(location.getLatitude(), location.getLongitude());
            if (justNearby && Utils.getDistance(this.location, voucherLocation) > 10000)
                continue;

            googleMap.addMarker(getMarker(voucherLocation, categoryIDs.get(i)));
            builder.include(voucherLocation);
        }

        googleMap.setOnMarkerClickListener(this);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 150));
    }

    private MarkerOptions getMarker(LatLng location, int categoryID) {
        int icon;
        if (categoryID == 1) icon = R.mipmap.pin_location_food;
        else if (categoryID == 2) icon = R.mipmap.pin_location_game;
        else if (categoryID == 3) icon = R.mipmap.pin_location_sports;
        else if (categoryID == 4) icon = R.mipmap.pin_location_elektronik;
        else if (categoryID == 5) icon = R.mipmap.pin_location_lifestyle;
        else if (categoryID == 7) icon = R.mipmap.pin_location_beauty;
        else if (categoryID == 8) icon = R.mipmap.pin_location_home;
        else if (categoryID == 9) icon = R.mipmap.pin_location_food;
        else if (categoryID == 12) icon = R.mipmap.pin_location_medical;
        else if (categoryID == 13) icon = R.mipmap.pin_location_education;
        else icon = R.mipmap.pin_location_icon;

        return new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(icon))
                .position(location);
    }

    @OnClick(R.id.current_location_icon)
    public void currentLocationIconOnClick() {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 17);
        googleMap.animateCamera(cameraUpdate);
    }

    @OnClick(R.id.filter_layout)
    public void filterLayoutOnClick() {
        FilterCategoryDialog filterCategoryDialog = new FilterCategoryDialog(categorySelected, new FilterCategoryDialog.Handler() {
            @Override
            public void onSelected(String selected) {
                categorySelected = selected;
                if (selected.equals(FilterCategoryDialog.TAMPILKAN_SEMUA))
                    showTenantLocations(null);
                else
                    showTenantLocations(selected);
            }
        });
        filterCategoryDialog.show(getFragmentManager(), "showFilterCategoryDialog");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng location = marker.getPosition();
        Intent intent = new Intent(this, PartnerDetailsActivity.class);
        intent.putExtra("latitude", location.latitude);
        intent.putExtra("longitude", location.longitude);
        startActivity(intent);
        return false;
    }
}
