package com.android.promosee.activities.wallets;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.promosee.R;
import com.android.promosee.activities.BaseActivity;
import com.android.promosee.core.API;
import com.android.promosee.core.Alert;
import com.android.promosee.core.Session;
import com.android.promosee.core.Validators;
import com.android.promosee.dialogs.LoadingDialog;
import com.android.promosee.models.Bank;
import com.android.promosee.models.Transaction;
import com.android.promosee.models.Wallet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import cz.msebera.android.httpclient.Header;
import io.realm.Realm;

public class TopUpConfirmationActivity extends BaseActivity {

    @BindView(R.id.method_text) TextView methodTextView;
    @BindView(R.id.account_destination_text) TextView accountDestinationTextView;
    @BindView(R.id.amount_edittext) EditText amountEditText;
    @BindView(R.id.account_sender_edittext) EditText accountSenderEditText;
    @BindView(R.id.name_edittext) EditText nameEditText;
    @BindView(R.id.date_text) TextView dateTextView;
    @BindView(R.id.method_spinner) Spinner methodSpinner;
    @BindView(R.id.account_destination_spinner) Spinner accountDestinationSpinner;

    private Date selectedDate;
    private String[] methodOptions;
    private ArrayList<String> nameBanks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup_confirmation);
        ButterKnife.bind(this);
        setupBackButtonBar();

        selectedDate = new Date();
        methodOptions = new String[]{"Tunai", "ATM", "E-Banking"};

        for (Bank bank : Realm.getDefaultInstance().where(Bank.class).findAll())
            nameBanks.add(bank.getBankName());
    }

    @OnClick(R.id.method_text)
    public void methodTextOnClick() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner, methodOptions);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        methodSpinner.setAdapter(dataAdapter);
        methodSpinner.performClick();
    }

    @OnItemSelected(R.id.method_spinner)
    public void methodSpinnerSelected(int position) {
        methodTextView.setText(methodOptions[position]);
    }

    @OnClick(R.id.account_destination_text)
    public void accountDestinationTextOnClick() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner, nameBanks);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountDestinationSpinner.setAdapter(dataAdapter);
        accountDestinationSpinner.performClick();
    }

    @OnItemSelected(R.id.account_destination_spinner)
    public void accountDestinationSpinnerSelected(int position) {
        accountDestinationTextView.setText(nameBanks.get(position));
    }

    @OnClick(R.id.date_text)
    public void dateTextOnClick() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                selectedDate.setYear(year - 1900);
                selectedDate.setMonth(monthOfYear);
                selectedDate.setDate(dayOfMonth);
                dateTextView.setText(new SimpleDateFormat("dd MMM yyyy", Locale.US).format(selectedDate));
            }
        }, selectedDate.getYear() + 1900, selectedDate.getMonth(), selectedDate.getDate());
        datePickerDialog.show();
    }

    @OnClick(R.id.confirm_button)
    public void confirmButtonOnClick() {
        if (!Validators.validateLength(methodTextView, 1))
            Alert.dialog(this, "Silahkan pilih dahulu metode transfer");
        else if (!Validators.validateLength(accountDestinationTextView, 1))
            Alert.dialog(this, "Silahkan pilih rekening PROMOSEE tujuan");
        else if (!Validators.validateLength(amountEditText, 1))
            Alert.dialog(this, "Mohon isi jumlah yang dibayar");
        else if (!Validators.validateLength(dateTextView, 1))
            Alert.dialog(this, "Mohon isi tanggal pembayaran");
        else if (!Validators.validateLength(nameEditText, 1))
            Alert.dialog(this, "Mohon isi nama pemegang rekening");
        else if (!Validators.validateLength(accountSenderEditText, 1))
            Alert.dialog(this, "Mohon isi nomor rekening pengirim");
        else {
            final Realm realm = Realm.getDefaultInstance();
            int bankID = realm.where(Bank.class)
                    .equalTo("bankName", nameBanks.get(accountDestinationSpinner.getSelectedItemPosition())).findFirst().getId();

            JSONObject params = API.getBaseJSONParams(this);
            try {
                params.put("metode", methodTextView.getText().toString().toLowerCase());
                params.put("id_bank", bankID);
                params.put("jumlah", amountEditText.getText().toString());
                params.put("nama", nameEditText.getText().toString());
                params.put("no_rekening", accountSenderEditText.getText().toString());
            } catch (JSONException e) {
                return;
            }

            final LoadingDialog loadingDialog = new LoadingDialog(this);
            loadingDialog.show();

            final Activity activity = this;
            API.post(this, API.BASE_URL + "topups/confirmations", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    realm.beginTransaction();
                    try {
                        Wallet.fromJSON(response, realm);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    realm.commitTransaction();

                    Toast.makeText(activity, "Konfirmasi pembayaran anda akan segera diproses, " +
                            "akan ada notifikasi saat admin telah menyetujui pembayaran anda", Toast.LENGTH_LONG).show();
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
    }
}



