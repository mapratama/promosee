package com.android.promosee.activities.partners;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.promosee.R;
import com.android.promosee.activities.BaseActivity;
import com.android.promosee.models.Location;
import com.android.promosee.models.Tenant;;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class PartnerLocationActivity extends BaseActivity {

    @BindView(R.id.location_recyclerview) RecyclerView locationRecyclerView;

    private Tenant tenant;
    private RealmList<Location> locations;
    private MapFragment mapFragment;
    private GoogleMap googleMap;
    private LatLng location, defaultLocation = new LatLng(-6.174796, 106.821959);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_location);
        ButterKnife.bind(this);
        setupBackButtonBar();

        tenant = Tenant.get(getIntent().getIntExtra("tenantID", 0));
        location = new LatLng(getIntent().getDoubleExtra("latitude", 0), getIntent().getDoubleExtra("longitude", 0));

        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            Toast.makeText(this, getResources().getString(R.string.error_load_map), Toast.LENGTH_LONG).show();
            finish();
        }

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mGoogleMap) {
                googleMap = mGoogleMap;
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 17));
                showLocation();
            }
        });
    }


    private void showLocation() {
        googleMap.clear();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.current_location_icon));
        googleMap.addMarker(markerOptions.position(location));
        builder.include(location);

        locations = new RealmList<>();
        for (Location locationTenant : tenant.getLocations())
            locations.add(locationTenant);

        for (Location location : locations) {
            LatLng tenantLocation = new LatLng(location.getLatitude(), location.getLongitude());
            markerOptions = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_location_icon))
                    .position(tenantLocation);

            googleMap.addMarker(markerOptions);
            builder.include(tenantLocation);
        }

        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 150));

        locationRecyclerView.setAdapter(new LocationAdapter());
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        googleMap.clear();
    }

    class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

        @Override
        public LocationAdapter.LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new LocationAdapter.LocationViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.location_details_item, parent, false));
        }

        @Override
        public void onBindViewHolder(LocationAdapter.LocationViewHolder holder, final int position) {
            Location location = locations.get(position);
            holder.location = location;
            holder.nameTextView.setText(location.getName());
            holder.addressTextView.setText(location.getAddress());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return locations.size();
        }

        class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            @BindView(R.id.name_text) TextView nameTextView;
            @BindView(R.id.address_text) TextView addressTextView;

            private Location location;

            public LocationViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Uri locationUri = Uri.parse("geo:0,0?q=" + location.getLatitude() + "," +
                        location.getLongitude() + "(" + location.getAddress() + ")");

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, locationUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        }
    }

}
