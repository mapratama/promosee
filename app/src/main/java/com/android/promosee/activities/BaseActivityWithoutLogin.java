package com.android.promosee.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.promosee.R;
import com.android.promosee.activities.auth.LoginActivity;
import com.android.promosee.activities.auth.RegisterActivity;
import com.android.promosee.activities.members.MemberCardsIndexActivity;
import com.android.promosee.activities.news.NewsIndexActivity;
import com.android.promosee.activities.partners.PartnerIndexActivity;
import com.android.promosee.activities.vouchers.FreeVoucherActivity;
import com.android.promosee.activities.vouchers.MyVoucherIndexActivity;
import com.android.promosee.activities.vouchers.VoucherCategoryActivity;
import com.android.promosee.activities.wallets.WalletHistoryActivity;
import com.android.promosee.core.Preferences;
import com.android.promosee.core.Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.login.LoginManager;

import io.realm.Realm;


public class BaseActivityWithoutLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setupBackButtonBar() {
        final Activity activity = this;
        ImageView backButton = (ImageView) findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
