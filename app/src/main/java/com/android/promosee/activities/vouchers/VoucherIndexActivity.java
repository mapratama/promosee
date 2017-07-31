package com.android.promosee.activities.vouchers;

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
import com.android.promosee.models.Category;
import com.android.promosee.models.Tenant;
import com.android.promosee.models.Voucher;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;

public class VoucherIndexActivity extends BaseActivity {

    @BindView(R.id.voucher_recyclerview) RecyclerView voucherRecyclerView;
    @BindView(R.id.title_text) TextView titleTextView;

    private RealmList<Voucher> vouchers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_index);
        ButterKnife.bind(this);
        setupBackButtonBar();

        Category category = Category.get(getIntent().getIntExtra("categoryID", 0));
        vouchers = new RealmList<>();
        for (Tenant tenant : category.getTenants()) {
            for (Voucher voucher : tenant.getVouchers())
                vouchers.add(voucher);
        }

        titleTextView.setText(category.getName() + " Voucher");
        voucherRecyclerView.setAdapter(new VoucherIndexAdapter());
        voucherRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    }

    class VoucherIndexAdapter extends RecyclerView.Adapter<VoucherIndexAdapter.VoucherIndexViewHolder> {

        @Override
        public VoucherIndexViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VoucherIndexViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.base_grid_item, parent, false));
        }

        @Override
        public void onBindViewHolder(VoucherIndexViewHolder holder, final int position) {
            Voucher voucher = vouchers.get(position);

            holder.photo.setImageURI(Uri.parse(voucher.getVoucherImageUrl()));
            holder.titleTextView.setText(voucher.getName());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return vouchers.size();
        }

        class VoucherIndexViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            @BindView(R.id.photo) SimpleDraweeView photo;
            @BindView(R.id.title_text) TextView titleTextView;

            public VoucherIndexViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), BuyVoucherActivity.class);
                intent.putExtra("voucherID", vouchers.get(getAdapterPosition()).getId());
                startActivity(intent);
            }
        }
    }
}
