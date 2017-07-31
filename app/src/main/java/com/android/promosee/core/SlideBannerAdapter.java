package com.android.promosee.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.promosee.R;
import com.android.promosee.activities.vouchers.BuyVoucherActivity;
import com.android.promosee.models.Banner;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class SlideBannerAdapter extends PagerAdapter {

    private Activity activity;
    private RealmResults<Banner> banners;

    public SlideBannerAdapter(Activity activity) {
        this.activity = activity;
        banners = Realm.getDefaultInstance().where(Banner.class).findAll();
    }

    @Override
    public int getCount() {
        return banners.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.banner_item, container, false);

        final Banner banner = banners.get(position);

        SimpleDraweeView bannerView = (SimpleDraweeView) view.findViewById(R.id.banner);
        bannerView.setImageURI(banner.getUrl());
        bannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, BuyVoucherActivity.class);
                intent.putExtra("voucherID", banner.getVoucher().getId());
                activity.startActivity(intent);
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
