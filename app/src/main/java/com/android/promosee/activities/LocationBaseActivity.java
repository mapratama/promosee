package com.android.promosee.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.promosee.R;
import com.android.promosee.activities.vouchers.BuyVoucherActivity;
import com.android.promosee.core.Alert;
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
import com.google.android.gms.maps.model.LatLng;

import io.realm.Realm;


public class LocationBaseActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    public GoogleMap googleMap;
    private MapFragment mapFragment;
    private GoogleApiClient apiClient;
    public LatLng location, defaultLocation = new LatLng(-6.174796, 106.821959);
    private final static int PLAY_SERVICES_REQUEST = 123, LOCATION_PERMISSION_REQUEST = 345;
    public int viewID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(viewID);

        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            Toast.makeText(this, getResources().getString(R.string.error_load_map), Toast.LENGTH_LONG).show();
            finish();
        }

        if (playServicesIsAvailable()) {
            apiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        else {
            Toast.makeText(this, getResources().getString(R.string.error_google_play_services), Toast.LENGTH_LONG).show();
            finish();
        }

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mGoogleMap) {
                googleMap = mGoogleMap;
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 17));
                apiClient.connect();
            }
        });
    }

    public void setupBackButtonBar() {
        ImageView backButton =  (ImageView) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean playServicesIsAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result))
                googleAPI.getErrorDialog(this, result, PLAY_SERVICES_REQUEST).show();
            return false;
        }

        return true;
    }

    public void displayCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions =  {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST);
        }

        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);

        if (currentLocation != null) {
            location = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 17);
            googleMap.animateCamera(cameraUpdate);

            if (viewID == R.layout.activity_location)
                ((LocationActivity) this).showTenantLocations(null);
            else
                ((BuyVoucherActivity) this).showNearbyTenant();
        }
        else {
            location = defaultLocation;
            Toast.makeText(this, getResources().getString(R.string.error_get_current_location), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) displayCurrentLocation();
        else Alert.dialog(this, getResources().getString(R.string.ignore_location_permission));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        apiClient.disconnect();
        googleMap.clear();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onLocationChanged(Location location) {}
}
