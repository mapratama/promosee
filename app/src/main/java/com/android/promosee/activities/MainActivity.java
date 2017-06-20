package com.android.promosee.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.promosee.R;
import com.android.promosee.activities.auth.EditProfileActivity;
import com.android.promosee.activities.auth.LoginActivity;
import com.android.promosee.activities.members.MemberCardsIndexActivity;
import com.android.promosee.activities.news.NewsIndexActivity;
import com.android.promosee.activities.partners.PartnerIndexActivity;
import com.android.promosee.activities.vouchers.BuyVoucherActivity;
import com.android.promosee.activities.vouchers.FreeVoucherActivity;
import com.android.promosee.activities.vouchers.MyVoucherIndexActivity;
import com.android.promosee.activities.vouchers.VoucherCategoryActivity;
import com.android.promosee.activities.wallets.WalletHistoryActivity;
import com.android.promosee.core.API;
import com.android.promosee.core.Preferences;
import com.android.promosee.core.Session;
import com.android.promosee.core.SlideBannerAdapter;
import com.android.promosee.core.Utils;
import com.android.promosee.models.Category;
import com.android.promosee.models.Tenant;
import com.android.promosee.models.Voucher;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.login.LoginManager;
import com.loopj.android.http.JsonHttpResponseHandler;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import cz.msebera.android.httpclient.Header;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends BaseActivity {

    @BindView(R.id.back_button) ImageView backButton;
    @BindView(R.id.fab_search) ImageView fabSearch;
    @BindView(R.id.fab_voucher) ImageView fabVoucher;
    @BindView(R.id.fab_icon) ImageView fabIcon;
    @BindView(R.id.search_edittext) EditText searchEditText;
    @BindView(R.id.view_pager) AutoScrollViewPager autoScrollViewPager;
    @BindView(R.id.photo) SimpleDraweeView photo;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.background_dimmer) View backgroundDimmer;
    @BindView(R.id.voucher_recyclerview) RecyclerView voucherRecyclerView;
    @BindView(R.id.subscribe_layout) LinearLayout subscribeLayout;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.name_text) TextView nameTextView;
    @BindView(R.id.balance_text) TextView balanceTextView;
    @BindView(R.id.fab_search_text) TextView fabSearchTextView;
    @BindView(R.id.fab_voucher_text) TextView fabVoucherTextView;
    @BindView(R.id.fab_layout) RelativeLayout fabLayout;
    @BindView(R.id.photo_profile) SimpleDraweeView photoProfile;

    private Preferences preferences;
    private Activity activity;
    private Animation showFabAnimation, hideFabAnimation, fadeIn, fadeOut;
    private boolean fabIsExpand = false;
    private Realm realm;
    private RealmResults<Category> categories;
    private VoucherAdapter voucherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        activity = this;
        realm = Realm.getDefaultInstance();
        preferences = new Preferences(this);

        showFabAnimation = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_show);
        hideFabAnimation = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_hide);
        fadeIn = AnimationUtils.loadAnimation(getApplication(), R.anim.fadein);
        fadeOut = AnimationUtils.loadAnimation(getApplication(), R.anim.fadeout);

        backButton.setImageResource(R.mipmap.menu_icon);
        Utils.setPhotoProfile(preferences, photo);

        SlideBannerAdapter mSlideBannerAdapter = new SlideBannerAdapter(this);
        autoScrollViewPager.setAdapter(mSlideBannerAdapter);
        autoScrollViewPager.startAutoScroll();
        autoScrollViewPager.setInterval(5000);

        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen) {
                    autoScrollViewPager.setVisibility(View.GONE);
                    subscribeLayout.setVisibility(View.GONE);
                    fabLayout.setVisibility(View.GONE);

                    RealmList<Voucher> vouchers = new RealmList<>();
                    RealmResults<Voucher> vouchersResult = realm.where(Voucher.class).findAllSorted("name");
                    for (Voucher voucher : vouchersResult) vouchers.add(voucher);

                    voucherAdapter = new VoucherAdapter(vouchers);
                    voucherRecyclerView.setAdapter(voucherAdapter);
                    voucherRecyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
                }
                else {
                    autoScrollViewPager.setVisibility(View.VISIBLE);
                    subscribeLayout.setVisibility(View.VISIBLE);
                    fabLayout.setVisibility(View.VISIBLE);
                    setupBaseVoucherRecyclerView();
                }
            }
        });

        int redColorID = ContextCompat.getColor(this, R.color.LightRed);
        swipeRefreshLayout.setColorSchemeColors(redColorID, redColorID, redColorID);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                API.get(API.BASE_URL + "vouchers/list", API.getBaseParams(activity), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Voucher.fromJSONArray(response.getJSONArray("vouchers"));
                            Session.saveUserData(activity, response.getJSONObject("user"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setupBaseVoucherRecyclerView();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                          JSONObject errorResponse) {
                        API.handleFailure(activity, statusCode, errorResponse);
                    }

                    @Override
                    public void onFinish() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        setupBaseVoucherRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.setPhotoProfile(preferences, photoProfile);
        nameTextView.setText(preferences.getString("name"));
        balanceTextView.setText("Rp. " + Utils.addThousandSeparator(preferences.getLong("balance")));
    }

    @OnClick(R.id.category_button)
    public void categoryButtonOnClick() {
        startActivity(new Intent(activity, VoucherCategoryActivity.class));
    }

    @OnClick(R.id.home_button)
    public void homeButtonOnClick() {
        startActivity(new Intent(activity, MainActivity.class));
    }

    @OnClick(R.id.partner_button)
    public void partnerButtonOnClick() {
        startActivity(new Intent(activity, PartnerIndexActivity.class));
    }

    @OnClick(R.id.news_button)
    public void newsButtonOnClick() {
        startActivity(new Intent(activity, NewsIndexActivity.class));
    }

    @OnClick(R.id.faq_button)
    public void faqButtonOnClick() {
        startActivity(new Intent(activity, FAQActivity.class));
    }

    @OnClick(R.id.contact_button)
    public void contactButtonOnClick() {
        startActivity(new Intent(activity, ContactUsActivity.class));
    }

    @OnClick(R.id.my_vouchers_button)
    public void myVouchersButtonOnClick() {
        startActivity(new Intent(activity, MyVoucherIndexActivity.class));
    }

    @OnClick(R.id.my_membercards_button)
    public void myMembercardsButtonOnClick() {
        startActivity(new Intent(activity, MemberCardsIndexActivity.class));
    }

    @OnClick(R.id.my_wallets_button)
    public void myWalletsButtonOnClick() {
        startActivity(new Intent(activity, WalletHistoryActivity.class));
    }

    @OnClick(R.id.my_redemptions_button)
    public void myRedemptionsButtonOnClick() {
        startActivity(new Intent(activity, RedemptionHistoryActivity.class));
    }

    @OnClick(R.id.share_button)
    public void shareButtonOnClick() {
        startActivity(new Intent(activity, FreeVoucherActivity.class));
    }

    @OnClick(R.id.edit_profile)
    public void editProfileLayoutOnClick() {
        Intent intent = new Intent(activity, EditProfileActivity.class);
        intent.putExtra("action", EditProfileActivity.EDIT);
        startActivityForResult(intent, 1);
    }

    @OnClick(R.id.name_text)
    public void nameTextOnClick() {
        Intent intent = new Intent(activity, EditProfileActivity.class);
        intent.putExtra("action", EditProfileActivity.VIEW);
        startActivity(intent);
    }

    @OnClick(R.id.photo_profile)
    public void photoProfileOnClick() {
        nameTextOnClick();
    }

    @OnClick(R.id.instagram_icon)
    public void instagramIconOnClick() {
        String instagramProfile = getResources().getString(R.string.instagram_profile);
        try {
            getPackageManager().getPackageInfo("com.instagram.android", 0);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/_u/"
                    + instagramProfile));
            intent.setPackage("com.instagram.android");
            startActivity(intent);
        }
        catch (PackageManager.NameNotFoundException e) {
            Utils.openWebView(activity, "https://instagram.com/" + instagramProfile);
        }
    }

    @OnClick(R.id.twitter_icon)
    public void twitterIconOnClick() {
        String twitterProfile = getResources().getString(R.string.twitter_profile);
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name="
                    + twitterProfile)));
        } catch (Exception e) {
            Utils.openWebView(activity, "https://twitter.com/" + twitterProfile);
        }
    }

    @OnClick(R.id.youtube_icon)
    public void youtubeIconOnClick() {
        String youtubeID = getResources().getString(R.string.youtube_id);
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeID)));
        } catch (ActivityNotFoundException ex) {
            Utils.openWebView(activity, "http://www.youtube.com/watch?v=" + youtubeID);
        }
    }

    @OnClick(R.id.facebook_icon)
    public void facebookIconOnClick() {
        String facebookUrl = getResources().getString(R.string.facebook_url);
        final int newVersionCodeFacebook = 3002850;

        try {
            int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            facebookUrl = (versionCode >= newVersionCodeFacebook) ? "fb://facewebmodal/f?href=" + facebookUrl :
                    "fb://facewebmodal/f?href=" + facebookUrl ;
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
        }
        catch (PackageManager.NameNotFoundException e) {
            Utils.openWebView(activity, facebookUrl);
        }
    }

    @OnClick(R.id.logout_button)
    public void logoutButtonOnClick() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Yakin akan keluar dari aplikasi?");
        alertDialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                preferences.clear();
                LoginManager.getInstance().logOut();

                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.deleteAll();
                realm.commitTransaction();

                startActivity(new Intent(activity, LoginActivity.class));
                finish();
            }
        });
        alertDialog.setNegativeButton("Tidak", null);
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) nameTextView.setText(preferences.getString("name"));
    }

    @OnClick(R.id.back_button)
    public void backButtonOnClick() {
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    @OnClick(R.id.photo)
    public void photoOnClick() {
        drawerLayout.openDrawer(Gravity.RIGHT);
    }

    @OnClick(R.id.subscribe_layout)
    public void subscribeLayoutOnClick() {
        startActivity(new Intent(this, SubscribeActivity.class));
    }

    @OnClick(R.id.fab_layout)
    public void fabOnClick() {
        if (fabIsExpand) hideFabAnimation();
        else expandFabAnimation();

        fabIsExpand = !fabIsExpand;
    }

    @OnTextChanged(R.id.search_edittext)
    public void searchEditTextOnTextChanged(CharSequence text) {
        RealmQuery<Voucher> voucherQuery = realm.where(Voucher.class);
        if (text.length() > 0) {
            voucherQuery.contains("name", text.toString(), Case.INSENSITIVE);
        }

        voucherAdapter.vouchers = new RealmList<>();
        RealmResults<Voucher> voucherResult = voucherQuery.findAllSorted("name");
        for (Voucher voucher : voucherResult) voucherAdapter.vouchers.add(voucher);

        voucherAdapter.notifyDataSetChanged();
    }

    private void setupBaseVoucherRecyclerView() {
        categories = realm.where(Category.class).findAllSorted("orderID");
        voucherRecyclerView.setAdapter(new VoucherGridAdapter());
        voucherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void expandFabAnimation() {
        backgroundDimmer.setVisibility(View.VISIBLE);
        fabIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.cross_icon));

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) fabSearch.getLayoutParams();
        layoutParams.bottomMargin = (int) (fabSearch.getHeight() * 3.5);
        fabSearch.setLayoutParams(layoutParams);
        fabSearch.startAnimation(showFabAnimation);
        fabSearchTextView.startAnimation(showFabAnimation);

        fabSearch.setClickable(true);
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, LocationActivity.class);
                intent.putExtra("nearby", true);
                startActivity(intent);
                fabIsExpand = false;
                hideFabAnimation();
            }
        });

        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) fabVoucher.getLayoutParams();
        layoutParams2.bottomMargin = fabVoucher.getHeight() * 5;
        fabVoucher.setLayoutParams(layoutParams2);
        fabVoucher.startAnimation(showFabAnimation);
        fabVoucherTextView.startAnimation(showFabAnimation);

        fabVoucher.setClickable(true);
        fabVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, LocationActivity.class);
                intent.putExtra("nearby", false);
                startActivity(intent);
                fabIsExpand = false;
                hideFabAnimation();
            }
        });

        backgroundDimmer.startAnimation(fadeIn);
    }


    private void hideFabAnimation() {
        backgroundDimmer.setVisibility(View.GONE);
        fabIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pin_location_icon));

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) fabSearch.getLayoutParams();
        layoutParams.bottomMargin = (int) (fabSearch.getHeight() * 3.5);
        fabSearch.setLayoutParams(layoutParams);
        fabSearch.startAnimation(hideFabAnimation);
        fabSearchTextView.startAnimation(hideFabAnimation);
        fabSearch.setClickable(false);

        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) fabVoucher.getLayoutParams();
        layoutParams2.bottomMargin = fabVoucher.getHeight() * 5;
        fabVoucher.setLayoutParams(layoutParams2);
        fabVoucher.startAnimation(hideFabAnimation);
        fabVoucherTextView.startAnimation(hideFabAnimation);
        fabVoucher.setClickable(false);

        backgroundDimmer.startAnimation(fadeOut);
    }

    class VoucherGridAdapter extends RecyclerView.Adapter<VoucherGridAdapter.VoucherGridViewHolder> {

        @Override
        public VoucherGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VoucherGridViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.voucher_grid_item, parent, false));
        }

        @Override
        public void onBindViewHolder(VoucherGridViewHolder holder, final int position) {
            Category category = categories.get(position);
            holder.headerTextView.setText(category.getName());

            VoucherAdapter voucherAdapter = new VoucherAdapter(category);
            holder.gridRecyclerView.setAdapter(voucherAdapter);
            holder.gridRecyclerView.setLayoutManager(new GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false));

            if (voucherAdapter.vouchers.size() > 0)
                holder.itemLayout.setVisibility(View.VISIBLE);
            else
                holder.itemLayout.setVisibility(View.GONE);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        class VoucherGridViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.item_layout) LinearLayout itemLayout;
            @BindView(R.id.header_text) TextView headerTextView;
            @BindView(R.id.grid_recyclerview) RecyclerView gridRecyclerView;

            public VoucherGridViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }

    class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.GridViewHolder> {

        public RealmList<Voucher> vouchers;

        public VoucherAdapter(Category category) {
            vouchers = new RealmList<>();
            for (Tenant tenant : category.getTenants()) {
                for (Voucher voucher : tenant.getVouchers().where().findAllSorted("id", Sort.DESCENDING))
                    vouchers.add(voucher);
            }
        }

        public VoucherAdapter(RealmList<Voucher> vouchers) {
            this.vouchers = vouchers;
        }


        @Override
        public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GridViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.voucher_item, parent, false));
        }

        @Override
        public void onBindViewHolder(GridViewHolder holder, final int position) {
            Voucher voucher = vouchers.get(position);

            holder.voucherID = voucher.getId();
            holder.photo.setImageURI(Uri.parse(voucher.getVoucherImageUrl()));
            holder.titleTextView.setText(voucher.getName());
            holder.tenantTextView.setText(voucher.getDescription());

            double price = voucher.getPrice();
            if (price == 0) holder.priceTextView.setText("FREE");
            else holder.priceTextView.setText("Rp. " + Utils.addThousandSeparator(price));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return vouchers.size();
        }

        class GridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            @BindView(R.id.photo) SimpleDraweeView photo;
            @BindView(R.id.title_text) TextView titleTextView;
            @BindView(R.id.tenant_text) TextView tenantTextView;
            @BindView(R.id.price_text) TextView priceTextView;

            private int voucherID;

            public GridViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, BuyVoucherActivity.class);
                intent.putExtra("voucherID", voucherID);
                startActivity(intent);
            }
        }
    }
}
