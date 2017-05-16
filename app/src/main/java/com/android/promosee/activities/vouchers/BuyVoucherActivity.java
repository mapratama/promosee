package com.android.promosee.activities.vouchers;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.promosee.R;
import com.android.promosee.activities.LocationBaseActivity;
import com.android.promosee.activities.TouchEnabledMapFragment;
import com.android.promosee.activities.partners.PartnerLocationActivity;
import com.android.promosee.core.API;
import com.android.promosee.core.Alert;
import com.android.promosee.core.Preferences;
import com.android.promosee.core.Session;
import com.android.promosee.core.Utils;
import com.android.promosee.dialogs.BankListDialog;
import com.android.promosee.dialogs.LoadingDialog;
import com.android.promosee.models.Tenant;
import com.android.promosee.models.Transaction;
import com.android.promosee.models.Voucher;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmList;

public class BuyVoucherActivity extends LocationBaseActivity {

    @BindView(R.id.banner) SimpleDraweeView banner;
    @BindView(R.id.logo) SimpleDraweeView logo;
    @BindView(R.id.name_text) TextView nameTextView;
    @BindView(R.id.price_text) TextView priceTextView;
    @BindView(R.id.description_text) TextView descriptionTextView;
    @BindView(R.id.valid_text) TextView validTextView;
    @BindView(R.id.minimum_pay_text) TextView minimumPayTextView;
    @BindView(R.id.saldo_text) TextView saldoTextView;
    @BindView(R.id.topup_text) TextView topupTextView;
    @BindView(R.id.not_enough_layout) LinearLayout notEnoughLayout;
    @BindView(R.id.rb_wallet) RadioButton walletRadioButton;
    @BindView(R.id.rb_pulsa) RadioButton pulsaRadioButton;
    @BindView(R.id.location_recyclerview) RecyclerView locationRecyclerView;
    @BindView(R.id.scroll_view) ScrollView scrollView;

    private Voucher voucher;
    private Tenant tenant;
    private double balance, price;
    private boolean modeWallet;
    private LoadingDialog loadingDialog;
    private Realm realm;
    private ArrayList<String> locationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        viewID = R.layout.activity_buy_voucher;
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setupBackButtonBar();

        topupTextView.setText(Html.fromHtml("Top-up <b>My Promosee Wallet</b> Kamu Disini"));
        balance = new Preferences(this).getLong("balance");
        loadingDialog = new LoadingDialog(this);
        modeWallet = true;
        realm = Realm.getDefaultInstance();
        setRadioButton();

        touchEnabledMapFragment.setListener(new TouchEnabledMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });

        voucher = Voucher.get(getIntent().getIntExtra("voucherID", 0));
        tenant = voucher.getTenant();

        banner.setImageURI(Uri.parse(voucher.getBigImageUrl()));
        logo.setImageURI(Uri.parse(tenant.getLogoUrl()));
        nameTextView.setText(voucher.getName());
        descriptionTextView.setText(voucher.getDescription());
        validTextView.setText(new SimpleDateFormat("dd MMM yyyy", Locale.US).format(voucher.getEndDate()));
        minimumPayTextView.setText("Rp. " + Utils.addThousandSeparator(voucher.getMinPayment()));

        price = voucher.getPrice();
        priceTextView.setText(Utils.addThousandSeparator(price));
        saldoTextView.setText("Rp. " + Utils.addThousandSeparator(balance));
        if (price > balance) notEnoughLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.topup_layout)
    public void topupLayoutOnClick() {
        BankListDialog bankListDialog = new BankListDialog();
        bankListDialog.show(getFragmentManager(), bankListDialog.getTag());
    }

    @OnClick(R.id.rb_wallet)
    public void walletRadioButtonOnClick() {
        modeWallet = true;
        setRadioButton();
    }

    @OnClick(R.id.rb_pulsa)
    public void pulsaRadioButtonOnClick() {
        modeWallet = false;
        setRadioButton();
    }

    @OnClick(R.id.buy_button)
    public void buyButtonOnClick() {
        if (modeWallet && price > balance) {
            Alert.dialog(this, "Maaf wallet PROMOSEE wallet yang anda miliki tidak mencukupi");
            return;
        }

        String type;
        if (modeWallet) type = "PROMOSEE wallet";
        else type = "pulsa";

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Yakin akan membeli voucher dengan " + type + " anda?");
        alertDialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (modeWallet) payWithPulse();
                else buyVoucher();
            }
        });
        alertDialog.setNegativeButton("Tidak", null);
        alertDialog.show();
    }

    private void payWithPulse() {
        loadingDialog.show();
        buyVoucher();
    }

    private void buyVoucher() {
        JSONObject params = API.getBaseJSONParams(this);

        try {
            params.put("id_voucher", voucher.getId());
            if (modeWallet) params.put("id_type_payment", 2);
            else params.put("id_type_payment", 1);
        } catch (JSONException e) {
            loadingDialog.dismiss();
        }

        final Activity activity = this;
        API.post(this, API.BASE_URL + "vouchers/buy", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (!API.responseIsSuccess(activity, response)) return;

                realm.beginTransaction();
                try {
                    Transaction.fromJSON(response.getJSONObject("transaction"), realm);
                    Session.saveUserData(activity, response.getJSONObject("customer"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                realm.commitTransaction();

                Toast.makeText(activity, "Pembelian voucher " + voucher.getName() + " berhasil dilakukan.",
                        Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                API.handleFailure(activity, statusCode, errorResponse);
            }

            @Override
            public void onFinish() {
                loadingDialog.dismiss();
            }
        });
    }

    private void setRadioButton() {
        if (modeWallet) {
            walletRadioButton.setChecked(true);
            pulsaRadioButton.setChecked(false);
        }
        else {
            walletRadioButton.setChecked(false);
            pulsaRadioButton.setChecked(true);
        }
    }

    public void showNearbyTenant() {
        googleMap.clear();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.current_location_icon));
        googleMap.addMarker(markerOptions.position(location));
        builder.include(location);

        RealmList<com.android.promosee.models.Location> locations = new RealmList<>();
        locationList = new ArrayList<>();
        for (com.android.promosee.models.Location location : tenant.getLocations()) {
            locations.add(location);
            locationList.add(location.getName());
        }
        locationList.add(" ... Lihat Semua Lokasi");

        int totalNearbyLocation = 0;
        for (com.android.promosee.models.Location location : locations) {
            LatLng voucherLocation = new LatLng(location.getLatitude(), location.getLongitude());
            if (Utils.getDistance(this.location, voucherLocation) < 10000) {
                markerOptions = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_location_icon))
                        .position(voucherLocation);

                googleMap.addMarker(markerOptions);
                builder.include(voucherLocation);
                totalNearbyLocation++;
            }
        }

        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 190));

        locationRecyclerView.setAdapter(new LocationAdapter());
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        locationRecyclerView.setVisibility(View.VISIBLE);

        if (totalNearbyLocation == 0) Alert.dialog(this, "Maaf tidak ada merchant terdekat dengan lokasi anda");
    }

    class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

        @Override
        public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new LocationViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.location_item, parent, false));
        }

        @Override
        public void onBindViewHolder(LocationViewHolder holder, final int position) {
            holder.locationTextView.setText(locationList.get(position));

            if (position == getItemCount() - 1) holder.pinIcon.setVisibility(View.GONE);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return locationList.size();
        }

        class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            @BindView(R.id.pin_icon) ImageView pinIcon;
            @BindView(R.id.location_text) TextView locationTextView;

            public LocationViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (getAdapterPosition() == getItemCount() - 1) {
                    Intent intent = new Intent(view.getContext(), PartnerLocationActivity.class);
                    intent.putExtra("tenantID", tenant.getId());
                    intent.putExtra("latitude", location.latitude);
                    intent.putExtra("longitude", location.longitude);
                    startActivity(intent);
                }
            }
        }
    }
}
