package com.android.promosee.dialogs;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.promosee.R;
import com.android.promosee.activities.vouchers.VoucherCategoryActivity;
import com.android.promosee.activities.vouchers.VoucherIndexActivity;
import com.android.promosee.models.Category;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by angga on 05/08/16.
 */
public class FilterCategoryDialog extends DialogFragment {

    @BindView(R.id.category_recyclerview) RecyclerView categoryRecyclerView;

    private String categorySelected;
    private Handler handler;
    private ArrayList<String> items;

    public static final String TAMPILKAN_SEMUA = "Tampilkan Semua";

    public interface Handler {
        void onSelected(String selected);
    }

    public FilterCategoryDialog(String categorySelected, Handler handler) {
        this.categorySelected = categorySelected;
        this.handler = handler;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_category_layout, container);
        ButterKnife.bind(this, view);

        items = new ArrayList<>();
        items.add(TAMPILKAN_SEMUA);
        for (Category category : Realm.getDefaultInstance().where(Category.class).findAll())
            items.add(category.getName());

        categoryRecyclerView.setAdapter(new CategoryAdapter());
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }

    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CategoryViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.filter_category_item, parent, false));
        }

        @Override
        public void onBindViewHolder(CategoryViewHolder holder, final int position) {
            String item = items.get(position);
            holder.categoryTextView.setText(item);

            if (categorySelected.equals(item))
                holder.circleIcon.setVisibility(View.VISIBLE);
            else
                holder.circleIcon.setVisibility(View.GONE);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            @BindView(R.id.circle_icon) ImageView circleIcon;
            @BindView(R.id.category_text) TextView categoryTextView;

            public CategoryViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                handler.onSelected(categoryTextView.getText().toString());
                dismiss();
            }
        }
    }
}
