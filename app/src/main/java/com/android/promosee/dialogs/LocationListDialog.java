package com.android.promosee.dialogs;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.promosee.R;
import com.android.promosee.activities.partners.PartnerLocationActivity;
import com.android.promosee.models.Category;
import com.android.promosee.models.Location;
import com.android.promosee.models.Tenant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;

import static com.android.promosee.R.string.locations;

/**
 * Created by angga on 05/08/16.
 */
public class LocationListDialog extends DialogFragment {

    @BindView(R.id.location_recyclerview) RecyclerView locationRecyclerView;

    private RealmList<Location> locations;

    public LocationListDialog(Tenant tenant) {
        locations = new RealmList<>();
        for (Location locationTenant : tenant.getLocations())
            locations.add(locationTenant);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_list_layout, container);
        ButterKnife.bind(this, view);

        locationRecyclerView.setAdapter(new LocationAdapter());
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }

    class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

        @Override
        public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new LocationViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.location_dialog_item, parent, false));
        }

        @Override
        public void onBindViewHolder(LocationAdapter.LocationViewHolder holder, final int position) {
            Location location = locations.get(position);
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

        class LocationViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.name_text) TextView nameTextView;
            @BindView(R.id.address_text) TextView addressTextView;

            public LocationViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
