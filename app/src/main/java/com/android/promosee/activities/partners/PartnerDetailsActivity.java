package com.android.promosee.activities.partners;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.promosee.R;
import com.android.promosee.activities.BaseActivity;
import com.android.promosee.activities.members.AddMemberCardActivity;
import com.android.promosee.activities.members.MemberCardsIndexActivity;
import com.android.promosee.activities.vouchers.BuyVoucherActivity;
import com.android.promosee.activities.vouchers.VoucherCategoryActivity;
import com.android.promosee.activities.vouchers.VoucherIndexActivity;
import com.android.promosee.core.Utils;
import com.android.promosee.dialogs.FilterCategoryDialog;
import com.android.promosee.dialogs.VoucherDetailsDialog;
import com.android.promosee.models.Category;
import com.android.promosee.models.Tenant;
import com.android.promosee.models.Voucher;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class PartnerDetailsActivity extends BaseActivity {

    @BindView(R.id.banner) SimpleDraweeView banner;
    @BindView(R.id.name_partner_text) TextView nameTextView;
    @BindView(R.id.name_membercard_text) TextView nameMembercardTextView;
    @BindView(R.id.category_partner_text) TextView categoryTextView;
    @BindView(R.id.address_partner_text) TextView addressTextView;
    @BindView(R.id.total_voucher) TextView totalVoucherTextView;
    @BindView(R.id.voucher_recyclerview) RecyclerView voucherRecyclerView;
    @BindView(R.id.offline_member_layout) RelativeLayout offlineMemberLayout;
    @BindView(R.id.online_member_layout) RelativeLayout onlineMemberLayout;

    private Tenant tenant;
    private RealmResults<Voucher> vouchers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_details);
        ButterKnife.bind(this);
        setupBackButtonBar();

        int tenantID = getIntent().getIntExtra("tenantID", 0);
        if (tenantID != 0)
            tenant = Tenant.get(tenantID);
        else {
            tenant = Realm.getDefaultInstance().where(Tenant.class)
                    .equalTo("locations.latitude", getIntent().getDoubleExtra("latitude", 0))
                    .equalTo("locations.longitude", getIntent().getDoubleExtra("longitude", 0))
                    .findFirst();
        }


        if (tenant.getType().equals("online"))
            onlineMemberLayout.setVisibility(View.VISIBLE);
        else
            offlineMemberLayout.setVisibility(View.VISIBLE);

        banner.setImageURI(Uri.parse(tenant.getBannerUrl()));
        nameTextView.setText(tenant.getName());
        nameMembercardTextView.setText(tenant.getName());
        categoryTextView.setText(tenant.getCategory().getName());
        addressTextView.setText(tenant.getAddress());
        totalVoucherTextView.setText(String.valueOf(tenant.getVouchers().size()));

        vouchers = tenant.getVouchers().where().findAllSorted("id", Sort.DESCENDING);
        voucherRecyclerView.setAdapter(new VoucherAdapter());
        voucherRecyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
    }

    @OnClick(R.id.add_membercard_layout)
    public void addMembercardLayoutOnClick() {
//        startActivity(new Intent(this, MemberCardsIndexActivity.class));
    }

    class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {

        @Override
        public VoucherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VoucherViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.voucher_tenant_item, parent, false));
        }

        @Override
        public void onBindViewHolder(VoucherViewHolder holder, final int position) {
            Voucher voucher = vouchers.get(position);

            holder.photo.setImageURI(Uri.parse(voucher.getVoucherImageUrl()));
            holder.titleTextView.setText(voucher.getName());
            holder.tenantTextView.setText(voucher.getDescription());

            double price = voucher.getPrice();
            if (price == 0) holder.priceTextView.setText("FREE");
            else holder.priceTextView.setText("Rp. " + Utils.addThousandSeparator(price));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return vouchers.size();
        }

        class VoucherViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            @BindView(R.id.photo) SimpleDraweeView photo;
            @BindView(R.id.title_text) TextView titleTextView;
            @BindView(R.id.tenant_text) TextView tenantTextView;
            @BindView(R.id.price_text) TextView priceTextView;

            public VoucherViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                VoucherDetailsDialog voucherDetailsDialog = new VoucherDetailsDialog(vouchers.get(getAdapterPosition()));
                voucherDetailsDialog.show(getFragmentManager(), "showVoucherDetailsDialog");
            }
        }
    }
}
