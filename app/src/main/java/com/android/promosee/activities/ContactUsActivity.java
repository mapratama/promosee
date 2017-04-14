package com.android.promosee.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.android.promosee.R;
import com.android.promosee.core.Alert;
import com.android.promosee.core.Preferences;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactUsActivity extends BaseActivity {

    @BindView(R.id.name_edittext) EditText nameEditText;
    @BindView(R.id.comment_edittext) EditText commentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us_activity);
        ButterKnife.bind(this);
        setupBackButtonBar();
    }

    @OnClick(R.id.send_button)
    public void sendButtonOnClick() {
        if (nameEditText.getText().toString().isEmpty())
            Alert.dialog(this, "Please fill your name");
        else if (commentEditText.getText().toString().isEmpty())
            Alert.dialog(this, "Please fill your comment");
        else {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.email_contact_us)});
            intent.putExtra(Intent.EXTRA_SUBJECT, "From " + nameEditText.getText().toString());
            intent.putExtra(Intent.EXTRA_TEXT, commentEditText.getText().toString() );
            intent.setType("message/rfc822");

            startActivity(Intent.createChooser(intent, "Select Email Sending App :"));
        }
    }
}
