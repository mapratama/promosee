package com.android.promosee.activities.vouchers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.promosee.R;
import com.android.promosee.activities.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FreeVoucherActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_voucher);
        ButterKnife.bind(this);
        setupBackButtonBar();
    }

    @OnClick(R.id.send_button)
    public void sendButtonOnClick() {
        String message = getResources().getString(R.string.send_code) + " " + getResources()
                .getString(R.string.get_free_voucher_code) + " " + getResources().getString(R.string.get_free_voucher_info);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Share via"));
    }
}
