package com.android.promosee.activities.news;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.promosee.R;
import com.android.promosee.activities.BaseActivity;
import com.android.promosee.activities.vouchers.BuyVoucherActivity;
import com.android.promosee.models.News;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewsDetailsActivity extends BaseActivity {

    @BindView(R.id.image) SimpleDraweeView image;
    @BindView(R.id.title_text) TextView titleTextView;
    @BindView(R.id.user_text) TextView userTextView;
    @BindView(R.id.description_text) TextView descriptionTextView;
    @BindView(R.id.get_voucher_button) Button getVoucherButton;

    private News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        ButterKnife.bind(this);
        setupBackButtonBar();

        news = News.get(getIntent().getIntExtra("newsID", 0));

        image.setImageURI(Uri.parse(news.getImageUrl()));
        titleTextView.setText(news.getTitle());
        userTextView.setText("by " + news.getSubject());
        descriptionTextView.setText(news.getDescription());

        if (news.getVoucher() == null) getVoucherButton.setVisibility(View.GONE);

    }

    @OnClick(R.id.get_voucher_button)
    public void getVoucherButtonOnClick() {
        Intent intent = new Intent(this, BuyVoucherActivity.class);
        intent.putExtra("voucherID", news.getVoucher().getId());
        startActivity(intent);
    }
}
