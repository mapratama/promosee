package com.android.promosee.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.promosee.R;
import com.android.promosee.core.API;
import com.android.promosee.dialogs.LoadingDialog;
import com.android.promosee.models.Banner;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import io.realm.Realm;

public class SubscribeActivity extends BaseActivity {

    @BindView(R.id.banner) SimpleDraweeView banner;
    @BindView(R.id.rb_two) RadioButton radioButtonTwo;

    private int lamaBerlangganan;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        ButterKnife.bind(this);
        setupBackButtonBar();

        radioButtonTwo.setChecked(true);
        lamaBerlangganan = 14;
        loadingDialog = new LoadingDialog(this);

        banner.setImageURI(Realm.getDefaultInstance().where(Banner.class).findFirst().getUrl());
    }

    @OnClick(R.id.subscribe_button)
    public void subscribeButtonOnClick() {
        String infoBerlangganan = "2 minggu dengan biaya IDR 5.500";

        if (radioButtonTwo.isChecked()) {
            lamaBerlangganan = 7;
            infoBerlangganan = "1 minggu dengan biaya IDR 3.300";
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Yakin akan berlangganan PROMOSEE selama " + infoBerlangganan + "?");
        alertDialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                payWithPulse();
            }
        });
        alertDialog.setNegativeButton("Tidak", null);
        alertDialog.show();
    }

    private void payWithPulse() {
        loadingDialog.show();
        reportSuccessToServer();
    }

    private void reportSuccessToServer() {
        JSONObject params = API.getBaseJSONParams(this);

        try {
            params.put("days", lamaBerlangganan);
        } catch (JSONException e) {
            loadingDialog.dismiss();
        }

        final Activity activity = this;
        API.post(this, API.BASE_URL + "subscribe/add", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (!API.responseIsSuccess(activity, response)) return;

                Toast.makeText(activity, "Proses berlangganan berhasil dilakukan. Terima kasih telah berlangganan PROMOSEE",
                        Toast.LENGTH_LONG).show();
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
