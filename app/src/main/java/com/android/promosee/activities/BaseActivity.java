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
import com.android.promosee.activities.auth.EditProfileActivity;
import com.android.promosee.activities.auth.LoginActivity;
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


public class BaseActivity extends AppCompatActivity {

    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = new Preferences(this);
        super.onCreate(savedInstanceState);
    }

    public void setupBackButtonBar() {
        final Activity activity = this;
        ImageView backButton = (ImageView) findViewById(R.id.back_button);
        SimpleDraweeView photo = (SimpleDraweeView) findViewById(R.id.photo);
        TextView nameTextView = (TextView) findViewById(R.id.name_text);
        TextView balanceTextView = (TextView) findViewById(R.id.balance_text);
        SimpleDraweeView photoProfile = (SimpleDraweeView) findViewById(R.id.photo_profile);

        setPhotoProfile(photoProfile);
        nameTextView.setText(preferences.getString("name"));
        balanceTextView.setText("Rp. " + Utils.addThousandSeparator(preferences.getLong("balance")));

        setPhotoProfile(photo);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.home_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, MainActivity.class));
            }
        });

        findViewById(R.id.category_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, VoucherCategoryActivity.class));
            }
        });

        findViewById(R.id.partner_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, PartnerIndexActivity.class));
            }
        });

        findViewById(R.id.news_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, NewsIndexActivity.class));
            }
        });

        findViewById(R.id.faq_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, FAQActivity.class));
            }
        });

        findViewById(R.id.contact_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, ContactUsActivity.class));
            }
        });

        findViewById(R.id.my_vouchers_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, MyVoucherIndexActivity.class));
            }
        });

        findViewById(R.id.my_membercards_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, MemberCardsIndexActivity.class));
            }
        });

        findViewById(R.id.my_wallets_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, WalletHistoryActivity.class));
            }
        });

        findViewById(R.id.my_redemptions_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, RedemptionHistoryActivity.class));
            }
        });

        findViewById(R.id.share_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, FreeVoucherActivity.class));
            }
        });

        findViewById(R.id.edit_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, EditProfileActivity.class);
                intent.putExtra("action", EditProfileActivity.EDIT);
                startActivityForResult(intent, 1);
            }
        });

        findViewById(R.id.name_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, EditProfileActivity.class);
                intent.putExtra("action", EditProfileActivity.VIEW);
                startActivity(intent);
            }
        });

        findViewById(R.id.photo_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, EditProfileActivity.class);
                intent.putExtra("action", EditProfileActivity.VIEW);
                startActivity(intent);
            }
        });

        findViewById(R.id.instagram_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        findViewById(R.id.twitter_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String twitterProfile = getResources().getString(R.string.twitter_profile);
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name="
                            + twitterProfile)));
                } catch (Exception e) {
                    Utils.openWebView(activity, "https://twitter.com/" + twitterProfile);
                }
            }
        });

        findViewById(R.id.youtube_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String youtubeID = getResources().getString(R.string.youtube_id);
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeID)));
                } catch (ActivityNotFoundException ex) {
                    Utils.openWebView(activity, "http://www.youtube.com/watch?v=" + youtubeID);
                }
            }
        });

        findViewById(R.id.facebook_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
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
        });
    }

    private void setPhotoProfile(SimpleDraweeView simpleDraweeView) {
        Preferences preferences = new Preferences(this);
        simpleDraweeView.setImageURI(Uri.parse(preferences.getString("imageUrl")));
        simpleDraweeView.getHierarchy().setRoundingParams(
                Utils.setCircleImage(simpleDraweeView));
    }
}
