package com.android.promosee.activities.members;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.promosee.R;
import com.android.promosee.models.Membercard;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MemberCardFragment extends Fragment  {

    @BindView(R.id.name_text) TextView nameTextView;
    @BindView(R.id.nomor_text) TextView nomorTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_membercards, container, false);
        ButterKnife.bind(this, view);

        Membercard membercard = ((MemberCardDetailsActivity) getActivity()).membercard;
        nameTextView.setText(membercard.getName());
        nomorTextView.setText(membercard.getNomor());

        return view;
    }
}
