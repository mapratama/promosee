package com.android.promosee.activities.news;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.promosee.R;
import com.android.promosee.activities.BaseActivity;
import com.android.promosee.models.News;
import com.android.promosee.models.Tenant;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class NewsIndexActivity extends BaseActivity {

    @BindView(R.id.news_recyclerview) RecyclerView newsRecyclerView;

    private RealmResults<News> newses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_index);
        ButterKnife.bind(this);
        setupBackButtonBar();

        newses = Realm.getDefaultInstance().where(News.class).findAllSorted("id", Sort.DESCENDING);
        newsRecyclerView.setAdapter(new NewsAdapter());
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

        @Override
        public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NewsViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.news_item, parent, false));
        }

        @Override
        public void onBindViewHolder(NewsViewHolder holder, final int position) {
            News news = newses.get(position);

            holder.photo.setImageURI(Uri.parse(news.getImageUrl()));
            holder.titleTextView.setText(news.getTitle());
            holder.userTextView.setText("by " + news.getSubject());
            holder.descriptionTextView.setText(news.getDescription());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return newses.size();
        }

        class NewsViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.photo) SimpleDraweeView photo;
            @BindView(R.id.title_text) TextView titleTextView;
            @BindView(R.id.user_text) TextView userTextView;
            @BindView(R.id.description_text) TextView descriptionTextView;

            private Context context;

            public NewsViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                context = view.getContext();
            }

            @OnClick(R.id.arrow)
            public void arrowOnClick() {
                Intent intent = new Intent(context, NewsDetailsActivity.class);
                intent.putExtra("newsID", newses.get(getAdapterPosition()).getId());
                startActivity(intent);
            }
        }
    }
}
