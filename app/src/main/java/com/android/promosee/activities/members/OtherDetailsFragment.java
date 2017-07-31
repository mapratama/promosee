package com.android.promosee.activities.members;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.promosee.R;
import com.android.promosee.models.Membercard;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OtherDetailsFragment extends Fragment  {

    @BindView(R.id.notes_text) TextView notesTextView;
    @BindView(R.id.stamps_recyclerview) RecyclerView stampsRecyclerView;

    private int totalStamps;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other_details, container, false);
        ButterKnife.bind(this, view);

        Membercard membercard = ((MemberCardDetailsActivity) getActivity()).membercard;
        totalStamps = membercard.getTotalStamp();

        stampsRecyclerView.setAdapter(new StampsAdapter());
        stampsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        return view;
    }

    class StampsAdapter extends RecyclerView.Adapter<StampsAdapter.GridViewHolder> {

        @Override
        public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GridViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.stamps_item, parent, false));
        }

        @Override
        public void onBindViewHolder(GridViewHolder holder, final int position) {
            if ((position + 1) > totalStamps)
                holder.stampsIcon.setVisibility(View.GONE);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return ((totalStamps / 5) + 1) * 5;
        }

        class GridViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.stamps_icon) ImageView stampsIcon;

            public GridViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
