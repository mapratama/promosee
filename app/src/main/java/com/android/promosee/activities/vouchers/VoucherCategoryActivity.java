package com.android.promosee.activities.vouchers;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.promosee.R;
import com.android.promosee.activities.BaseActivity;
import com.android.promosee.activities.MainActivity;
import com.android.promosee.core.API;
import com.android.promosee.models.Category;
import com.android.promosee.models.Tenant;
import com.android.promosee.models.Transaction;
import com.android.promosee.models.Voucher;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class VoucherCategoryActivity extends BaseActivity {

    @BindView(R.id.category_recyclerview) RecyclerView categoryRecyclerView;

    private RealmResults<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_category);
        ButterKnife.bind(this);
        setupBackButtonBar();

        setupRecyclerView();
//        Log.e("###", "create");
//        API.get(API.BASE_URL + "categories/list", API.getBaseParams(this), new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                try {
//                    Category.fromJSONArray(response, true);
//                    Log.e("###", "oke");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                setupRecyclerView();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
//                                  JSONObject errorResponse) {
//                API.handleFailure(VoucherCategoryActivity.this, statusCode, errorResponse);
//            }
//        });
    }

    private void setupRecyclerView() {
        categories = Realm.getDefaultInstance().where(Category.class).findAllSorted("orderID");
        categoryRecyclerView.setAdapter(new CategoryAdapter());
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CategoryViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_item, parent, false));
        }

        @Override
        public void onBindViewHolder(CategoryViewHolder holder, final int position) {
            Category category = categories.get(position);

            holder.photo.setImageURI(Uri.parse(category.getImageUrl()));
            holder.titleTextView.setText(category.getName());
//            holder.summaryTextView.setText();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            @BindView(R.id.photo) SimpleDraweeView photo;
            @BindView(R.id.title_text) TextView titleTextView;

            public CategoryViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), VoucherIndexActivity.class);
                intent.putExtra("categoryID", categories.get(getAdapterPosition()).getId());
                startActivity(intent);
            }
        }
    }
}
