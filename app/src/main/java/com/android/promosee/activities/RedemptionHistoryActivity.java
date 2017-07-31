package com.android.promosee.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.promosee.R;
import com.android.promosee.activities.vouchers.MyVoucherDetailsActivity;
import com.android.promosee.activities.vouchers.MyVoucherIndexActivity;
import com.android.promosee.core.API;
import com.android.promosee.core.Session;
import com.android.promosee.dialogs.LoadingDialog;
import com.android.promosee.models.Redemption;
import com.android.promosee.models.Transaction;
import com.android.promosee.models.Voucher;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class RedemptionHistoryActivity extends BaseActivity {

    @BindView(R.id.redemption_recyclerview) RecyclerView redemptionRecyclerView;

    private Activity activity;
    private RealmResults<Redemption> redemptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redemption_history);
        ButterKnife.bind(this);
        setupBackButtonBar();

        activity = this;
        setupRecyclerView();

        API.get(API.BASE_URL + "redemptions/created", API.getBaseParams(this), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    Redemption.fromJSONArray(response);
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
        redemptions = Realm.getDefaultInstance().where(Redemption.class).equalTo("showRedeem", true)
                .findAllSorted("date", Sort.DESCENDING);

        redemptionRecyclerView.setAdapter(new RedemptionAdapter());
        redemptionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    class RedemptionAdapter extends RecyclerView.Adapter<RedemptionAdapter.RedemptionHolder> {

        @Override
        public RedemptionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RedemptionHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.redemption_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RedemptionHolder holder, final int position) {
            Redemption redemption = redemptions.get(position);
            Voucher voucher = redemption.getVoucher();

            holder.redemptionID = redemption.getId();
            holder.photo.setImageURI(Uri.parse(voucher.getVoucherImageUrl()));
            holder.nameTextView.setText(voucher.getName());
            holder.dateTextView.setText(new SimpleDateFormat("dd MMMM yyyy", Locale.US).format(redemption.getDate()));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return redemptions.size();
        }

        class RedemptionHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.photo) SimpleDraweeView photo;
            @BindView(R.id.name_text) TextView nameTextView;
            @BindView(R.id.date_text) TextView dateTextView;

            private int redemptionID;

            public RedemptionHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            @OnClick(R.id.delete_icon)
            public void deleteIconOnClick() {
                final LoadingDialog loadingDialog = new LoadingDialog(activity);
                JSONObject params = API.getBaseJSONParams(activity);

                try {
                    params.put("id_redeem", redemptionID);
                } catch (JSONException e) {
                    loadingDialog.dismiss();
                    return;
                }

                API.post(activity, API.BASE_URL + "redemptions/hide", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        if (!API.responseIsSuccess(activity, response)) return;

                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        try {
                            Redemption.fromJSON(response, realm);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        realm.commitTransaction();

                        setupRecyclerView();
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
        }
    }

}
