package com.android.promosee.activities.vouchers;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.promosee.R;
import com.android.promosee.activities.BaseActivity;
import com.android.promosee.core.Utils;
import com.android.promosee.dialogs.LocationListDialog;
import com.android.promosee.dialogs.RedemptionDialog;
import com.android.promosee.models.Tenant;
import com.android.promosee.models.Voucher;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyVoucherDetailsActivity extends BaseActivity {

    @BindView(R.id.banner) SimpleDraweeView banner;
    @BindView(R.id.partner_name) TextView partnerNameTextView;
    @BindView(R.id.valid_text) TextView validTextView;
    @BindView(R.id.minimum_pay_text) TextView minimumPayTextView;
    @BindView(R.id.title_text) TextView titleTextView;
    @BindView(R.id.description_text) TextView descriptionTextView;

    private Voucher voucher;
    private Tenant tenant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_voucher_details);
        ButterKnife.bind(this);
        setupBackButtonBar();

        voucher = Voucher.get(getIntent().getIntExtra("voucherID", 0));
        tenant = voucher.getTenant();

        partnerNameTextView.setText(tenant.getName());
        banner.setImageURI(Uri.parse(voucher.getVoucherImageUrl()));
        validTextView.setText(new SimpleDateFormat("dd MMM yyyy", Locale.US).format(voucher.getEndDate()));
        minimumPayTextView.setText("Rp. " + Utils.addThousandSeparator(voucher.getMinPayment()));
        titleTextView.setText(voucher.getName());
        descriptionTextView.setText(voucher.getDescription());

    }

    @OnClick(R.id.location_button)
    public void locationButtonOnClick() {
        LocationListDialog locationListDialog = new LocationListDialog(tenant);
        locationListDialog.show(getFragmentManager(), locationListDialog.getTag());
    }

    @OnClick(R.id.redeem_button)
    public void redeemButtonOnClick() {
        RedemptionDialog redemptionDialog = new RedemptionDialog(voucher);
        redemptionDialog.show(getFragmentManager(), redemptionDialog.getTag());
    }
}
