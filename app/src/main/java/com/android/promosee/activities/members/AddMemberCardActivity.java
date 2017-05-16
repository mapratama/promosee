package com.android.promosee.activities.members;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.promosee.R;
import com.android.promosee.activities.BaseActivity;
import com.android.promosee.core.Alert;
import com.android.promosee.core.Preferences;
import com.android.promosee.core.Validators;
import com.android.promosee.models.Membercard;
import com.android.promosee.models.Tenant;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddMemberCardActivity extends BaseActivity {

    @BindView(R.id.banner) SimpleDraweeView banner;
    @BindView(R.id.code_edittext) EditText codeEditText;
    @BindView(R.id.name_edittext) EditText nameEditText;
    @BindView(R.id.email_edittext) EditText emailEditText;
    @BindView(R.id.mobile_number_edittext) EditText mobileNumberEditText;
    @BindView(R.id.address_edittext) EditText addressEditText;

    private Tenant tenant;
    private int action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member_card);
        ButterKnife.bind(this);
//        setupBackButtonBar();

        action = getIntent().getIntExtra("action", 0);
        if (action == SearchMemberCardActivity.REQUEST_NEW_MEMBER) {
            codeEditText.setHint("Nomor ktp");
            nameEditText.setVisibility(View.VISIBLE);
            emailEditText.setVisibility(View.VISIBLE);
            mobileNumberEditText.setVisibility(View.VISIBLE);
            addressEditText.setVisibility(View.VISIBLE);

            Preferences preferences = new Preferences(this);
            nameEditText.setText(preferences.getString("name"));
            emailEditText.setText(preferences.getString("email"));
            mobileNumberEditText.setText(preferences.getString("phone"));

            String address = "";
            if (!preferences.getString("address").equals("")) address = address + preferences.getString("address");
            if (!preferences.getString("city").equals("")) address = address + " " + preferences.getString("city");
            addressEditText.setText(address);
        }
        else
            codeEditText.setHint("Nomor membercard");

        tenant = Tenant.get(getIntent().getIntExtra("tenantID", 0));
        banner.setImageURI(Uri.parse(tenant.getBannerUrl()));
    }

    @OnClick(R.id.submit_button)
    public void submitButtonOnClick() {
        if (!Validators.validateLength(codeEditText, 1))
            Alert.dialog(this, "Maaf data yang anda input belum lengkap");
        else {
            String code = codeEditText.getText().toString();
            if (action == SearchMemberCardActivity.REQUEST_NEW_MEMBER)
                Membercard.add(this, tenant.getId(), "", code, addressEditText.getText().toString());
            else
                Membercard.add(this, tenant.getId(), code, "", "");
        }
    }
}
