package com.android.promosee.activities;

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
import com.android.promosee.models.Faq;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class FAQActivity extends BaseActivity {

    @BindView(R.id.faq_recyclerview) RecyclerView faqRecyclerView;

    private RealmResults<Faq> faqs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        ButterKnife.bind(this);
        setupBackButtonBar();

        faqs = Realm.getDefaultInstance().where(Faq.class).findAll();
        faqRecyclerView.setAdapter(new FAQAdapter());
        faqRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {

        @Override
        public FAQViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FAQViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.faq_item, parent, false));
        }

        @Override
        public void onBindViewHolder(FAQViewHolder holder, final int position) {
            Faq faq = faqs.get(position);

            holder.answerTextView.setText(faq.getAnswer());
            holder.questionTextView.setText(faq.getQuestion());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return faqs.size();
        }

        class FAQViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.answer_text) TextView answerTextView;
            @BindView(R.id.question_text) TextView questionTextView;

            public FAQViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
