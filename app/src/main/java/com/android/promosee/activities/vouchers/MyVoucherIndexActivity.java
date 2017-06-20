package com.android.promosee.activities.vouchers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.promosee.R;
import com.android.promosee.activities.BaseActivity;
import com.android.promosee.activities.MainActivity;
import com.android.promosee.core.API;
import com.android.promosee.models.Category;
import com.android.promosee.models.Tenant;
import com.android.promosee.models.Transaction;
import com.android.promosee.models.Voucher;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MyVoucherIndexActivity extends BaseActivity {

    @BindView(R.id.voucher_recyclerview) RecyclerView voucherRecyclerView;

    private Activity activity;
    private RealmList<Voucher> vouchers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_voucher_index);
        ButterKnife.bind(this);
        setupBackButtonBar();

        activity = this;
        setupRecyclerView();

        API.get(API.BASE_URL + "vouchers/created", API.getBaseParams(this), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    Transaction.fromJSONArray(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setupRecyclerView();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                API.handleFailure(activity, statusCode, errorResponse);
            }
        });
    }

    private void setupRecyclerView() {
        vouchers = new RealmList<>();
        RealmResults<Transaction> transactions = Realm.getDefaultInstance()
                .where(Transaction.class).findAllSorted("date");
        for (Transaction transaction : transactions)
            vouchers.add(transaction.getVoucher());

        voucherRecyclerView.setAdapter(new VoucherAdapter());
        voucherRecyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
    }

    class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.GridViewHolder> {

        @Override
        public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VoucherAdapter.GridViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.base_grid_item, parent, false));
        }

        @Override
        public void onBindViewHolder(GridViewHolder holder, final int position) {
            Voucher voucher = vouchers.get(position);

            holder.voucherID = voucher.getId();
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

        class GridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            @BindView(R.id.photo) SimpleDraweeView photo;
            @BindView(R.id.title_text) TextView titleTextView;

            private int voucherID;

            public GridViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MyVoucherDetailsActivity.class);
                intent.putExtra("voucherID", voucherID);
                startActivity(intent);
            }
        }
    }

}

