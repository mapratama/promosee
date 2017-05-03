package com.android.promosee.activities.members;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.android.promosee.R;
import com.android.promosee.activities.BaseActivity;
import com.android.promosee.core.Alert;
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

    private Tenant tenant;
    private int action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member_card);
        ButterKnife.bind(this);
//        setupBackButtonBar();

        action = getIntent().getIntExtra("action", 0);
        if (action == SearchMemberCardActivity.REQUEST_NEW_MEMBER)
            codeEditText.setHint("Nomor ktp");
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
                Membercard.add(this, tenant.getId(), "", code);
            else
                Membercard.add(this, tenant.getId(), code, "");
        }
    }
}
