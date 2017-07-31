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
import com.android.promosee.activities.wallets.TopUpConfirmationActivity;
import com.android.promosee.models.Bank;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by angga on 05/08/16.
 */
public class BankListDialog extends DialogFragment {

    @BindView(R.id.bank_recyclerview) RecyclerView bankRecyclerView;

    private RealmResults<Bank> banks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_bank_layout, container);
        ButterKnife.bind(this, view);

        banks = Realm.getDefaultInstance().where(Bank.class).findAll();
        bankRecyclerView.setAdapter(new BankAdapter());
        bankRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }

    @OnClick(R.id.topup_confirmation_button)
    public void topupConfirmationButtonOnClick() {
        startActivityForResult(new Intent(getActivity(), TopUpConfirmationActivity.class), 1);
        dismiss();
    }

    class BankAdapter extends RecyclerView.Adapter<BankAdapter.BankViewHolder> {

        @Override
        public BankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BankViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bank_item, parent, false));
        }

        @Override
        public void onBindViewHolder(BankViewHolder holder, final int position) {
            Bank bank = banks.get(position);
            holder.logo.setImageURI(Uri.parse(bank.getLogoUrl()));
            holder.bankNameTextView.setText(bank.getBankName());
            holder.accountNameTextView.setText(bank.getRekeningName());
            holder.accountNumberTextView.setText(bank.getRekening());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return banks.size();
        }

        class BankViewHolder extends RecyclerView.ViewHolder{

            @BindView(R.id.logo) SimpleDraweeView logo;
            @BindView(R.id.bank_name) TextView bankNameTextView;
            @BindView(R.id.account_name) TextView accountNameTextView;
            @BindView(R.id.account_number) TextView accountNumberTextView;

            public BankViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
