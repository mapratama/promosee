package com.android.promosee.activities.wallets;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.promosee.R;
import com.android.promosee.activities.BaseActivity;
import com.android.promosee.activities.RedemptionHistoryActivity;
import com.android.promosee.core.API;
import com.android.promosee.core.Preferences;
import com.android.promosee.core.Session;
import com.android.promosee.core.Utils;
import com.android.promosee.dialogs.BankListDialog;
import com.android.promosee.dialogs.LoadingDialog;
import com.android.promosee.models.Redemption;
import com.android.promosee.models.Transaction;
import com.android.promosee.models.Voucher;
import com.android.promosee.models.Wallet;
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
import io.realm.RealmResults;
import io.realm.Sort;

public class WalletHistoryActivity extends BaseActivity {

    @BindView(R.id.balance_text) TextView balanceTextView;
    @BindView(R.id.total_voucher_text) TextView totalVoucherTextView;
    @BindView(R.id.total_redeem_text) TextView totalRedeemTextView;
    @BindView(R.id.payment_recyclerview) RecyclerView paymentRecyclerView;

    private RealmResults<Wallet> wallets;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_history);
        ButterKnife.bind(this);
        activity = this;
        setupBackButtonBar();

        Preferences preferences = new Preferences(this);
        Realm realm = Realm.getDefaultInstance();

        balanceTextView.setText("Rp. " + Utils.addThousandSeparator(preferences.getLong("balance")));
        totalVoucherTextView.setText(realm.where(Transaction.class).equalTo("used", false).count() + " Voucher");
        totalRedeemTextView.setText(realm.where(Redemption.class).count() + " Voucher");

        setupRecyclerView();

        API.get(API.BASE_URL + "wallets/created", API.getBaseParams(this), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Wallet.fromJSONArray(response.getJSONArray("wallets"));
                    Session.saveUserData(activity, response.getJSONObject("user"));
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

    @Override
    protected void onResume() {
        super.onResume();
        setupRecyclerView();
    }

    @OnClick(R.id.topup_confirmation_button)
    public void topupConfirmationButtonOnClick() {
        BankListDialog bankListDialog = new BankListDialog();
        bankListDialog.show(getFragmentManager(), bankListDialog.getTag());
    }

    private void setupRecyclerView() {
        wallets = Realm.getDefaultInstance().where(Wallet.class).findAllSorted("date", Sort.DESCENDING);

        paymentRecyclerView.setAdapter(new PaymentAdapter());
        paymentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentHolder> {

        @Override
        public PaymentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PaymentHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.payment_item, parent, false));
        }

        @Override
        public void onBindViewHolder(PaymentHolder holder, final int position) {
            Wallet wallet = wallets.get(position);

            holder.dateTextView.setText(new SimpleDateFormat("dd/MM/yyyy - HH:MM", Locale.US).format(wallet.getDate()));

            String status = wallet.getStatus();
            holder.statusTextView.setText(status);
            if (status.equals("pending"))
                holder.statusTextView.setTextColor(ContextCompat.getColor(activity, R.color.Orange));
            else
                holder.statusTextView.setTextColor(ContextCompat.getColor(activity, R.color.Green));

            String description;
            if (wallet.getType().equals("deposit")) description = "Deposit";
            else description = "Buy Voucher";
            holder.descriptionTextView.setText(description + " Rp. " + Utils.addThousandSeparator(wallet.getAmount()));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return wallets.size();
        }

        class PaymentHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.date_text) TextView dateTextView;
            @BindView(R.id.description_text) TextView descriptionTextView;
            @BindView(R.id.status_text) TextView statusTextView;

            public PaymentHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
