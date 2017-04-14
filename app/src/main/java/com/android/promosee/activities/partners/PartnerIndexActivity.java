package com.android.promosee.activities.partners;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.promosee.R;
import com.android.promosee.activities.BaseActivity;
import com.android.promosee.activities.vouchers.VoucherIndexActivity;
import com.android.promosee.models.Tenant;
import com.android.promosee.models.Voucher;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class PartnerIndexActivity extends BaseActivity {

    @BindView(R.id.partner_recyclerview) RecyclerView partnerRecylerView;

    private RealmResults<Tenant> tenants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_index);
        ButterKnife.bind(this);
        setupBackButtonBar();

        tenants = Realm.getDefaultInstance().where(Tenant.class).findAllSorted("orderID");
        partnerRecylerView.setAdapter(new PartnerIndexAdapter());
        partnerRecylerView.setLayoutManager(new GridLayoutManager(this, 3));
    }

    class PartnerIndexAdapter extends RecyclerView.Adapter<PartnerIndexAdapter.PartnerIndexViewHolder> {

        @Override
        public PartnerIndexViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PartnerIndexViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.base_grid_item, parent, false));
        }

        @Override
        public void onBindViewHolder(PartnerIndexViewHolder holder, final int position) {
            Tenant tenant = tenants.get(position);

            holder.photo.setImageURI(Uri.parse(tenant.getLogoUrl()));
            holder.titleTextView.setText(tenant.getName());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return tenants.size();
        }

        class PartnerIndexViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            @BindView(R.id.photo) SimpleDraweeView photo;
            @BindView(R.id.title_text) TextView titleTextView;

            public PartnerIndexViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PartnerDetailsActivity.class);
                intent.putExtra("tenantID", tenants.get(getAdapterPosition()).getId());
                startActivity(intent);
            }
        }
    }
}
