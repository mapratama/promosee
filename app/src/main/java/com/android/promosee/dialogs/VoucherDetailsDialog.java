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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.promosee.R;
import com.android.promosee.activities.WebViewActivity;
import com.android.promosee.activities.vouchers.BuyVoucherActivity;
import com.android.promosee.core.Utils;
import com.android.promosee.models.Category;
import com.android.promosee.models.Tenant;
import com.android.promosee.models.Voucher;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by angga on 05/08/16.
 */
public class VoucherDetailsDialog extends DialogFragment {

    @BindView(R.id.photo) SimpleDraweeView photo;
    @BindView(R.id.name_text) TextView nameTextView;
    @BindView(R.id.valid_text) TextView validTextView;
    @BindView(R.id.description_text) TextView descriptionTextView;
    @BindView(R.id.code_text) TextView codeTextView;
    @BindView(R.id.price_text) TextView priceTextView;
    @BindView(R.id.submit_button) Button submitButton;

    private Voucher voucher;
    private Tenant tenant;

    public VoucherDetailsDialog(Voucher voucher) {
        this.voucher = voucher;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.voucher_details_layout, container);
        ButterKnife.bind(this, view);

        photo.setImageURI(Uri.parse(voucher.getVoucherImageUrl()));
        nameTextView.setText(voucher.getName());
        validTextView.setText(new SimpleDateFormat("dd MMM", Locale.US).format(voucher.getStartDate())
                + " - " + new SimpleDateFormat("dd MMM yyyy", Locale.US).format(voucher.getEndDate()));
        descriptionTextView.setText(voucher.getDescription());

        tenant = voucher.getTenant();
        if (tenant.getType().equals("online")) {
            codeTextView.setText("Kode Voucher : ");
            priceTextView.setText(tenant.getCode());
            submitButton.setText("Lanjut ke Website");
        }
        else {
            codeTextView.setText("Harga : ");
            priceTextView.setText("Rp. " + Utils.addThousandSeparator(voucher.getPrice()));
            submitButton.setText("Beli Voucher");
        }

        return view;
    }

    @OnClick(R.id.submit_button)
    public void submitButtonOnClick() {
        if (tenant.getType().equals("online")) {
            Intent intent = new Intent(getActivity(), WebViewActivity.class);
            intent.putExtra("url", tenant.getWebsiteUrl());
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(getActivity(), BuyVoucherActivity.class);
            intent.putExtra("voucherID", voucher.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }
}
