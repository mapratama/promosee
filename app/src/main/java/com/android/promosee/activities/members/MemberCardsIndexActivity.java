package com.android.promosee.activities.members;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.promosee.R;
import com.android.promosee.activities.BaseActivity;
import com.android.promosee.activities.vouchers.MyVoucherDetailsActivity;
import com.android.promosee.activities.vouchers.MyVoucherIndexActivity;
import com.android.promosee.core.API;
import com.android.promosee.models.Membercard;
import com.android.promosee.models.Transaction;
import com.android.promosee.models.Voucher;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MemberCardsIndexActivity extends BaseActivity {

    @BindView(R.id.membercard_recyclerview) RecyclerView membercardRecyclerView;

    private Activity activity;
    private RealmResults<Membercard> membercards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_cards_index);
        ButterKnife.bind(this);
        setupBackButtonBar();
        activity = this;

        setupRecyclerView();

        API.get(API.BASE_URL + "membercard/create", API.getBaseParams(this), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    Membercard.fromJSONArray(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setupRecyclerView();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                API.handleFailure(activity, statusCode, errorResponse);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) setupRecyclerView();
    }

    @OnClick(R.id.plus_icon)
    public void plusIconOnClick() {
        startActivityForResult(new Intent(this, SearchMemberCardActivity.class), 1);
    }

    private void setupRecyclerView() {
        membercards = Realm.getDefaultInstance().where(Membercard.class).findAll();
        membercardRecyclerView.setAdapter(new MembercardAdapter());
        membercardRecyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
    }

    class MembercardAdapter extends RecyclerView.Adapter<MembercardAdapter.GridViewHolder> {

        @Override
        public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GridViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.membercard_item, parent, false));
        }

        @Override
        public void onBindViewHolder(GridViewHolder holder, final int position) {
            Membercard membercard = membercards.get(position);

            holder.photo.setImageURI(Uri.parse(membercard.getTenant().getLogoUrl()));
            holder.membercardID = membercard.getId();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return membercards.size();
        }

        class GridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            @BindView(R.id.photo) SimpleDraweeView photo;

            private int membercardID;

            public GridViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MemberCardDetailsActivity.class);
                intent.putExtra("membercardID", membercardID);
                startActivity(intent);
            }
        }
    }
}
