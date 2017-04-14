package com.android.promosee.dialogs;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.promosee.R;
import com.android.promosee.core.API;
import com.android.promosee.core.Alert;
import com.android.promosee.models.Location;
import com.android.promosee.models.Redemption;
import com.android.promosee.models.Tenant;
import com.android.promosee.models.Voucher;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by angga on 05/08/16.
 */
public class RedemptionDialog extends DialogFragment {

    @BindView(R.id.code) EditText codeEditText;

    private Voucher voucher;

    public RedemptionDialog(Voucher voucher) {
        this.voucher = voucher;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.redeem_dialog, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }

    @OnClick(R.id.submit_button)
    public void submitButtonOnClick() {
//        if (!codeEditText.getText().toString().equals(voucher.getTenant().getCode())) {
//            Alert.dialog(getActivity(), "Maaf kode admin yang diinputkan salah");
//            return;
//        }

        final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        JSONObject params = API.getBaseJSONParams(getActivity());

        try {
            params.put("id_voucher", voucher.getId());
        } catch (JSONException e) {
            loadingDialog.dismiss();
        }

        API.post(getActivity(), API.BASE_URL + "redemptions/add", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (!API.responseIsSuccess(getActivity(), response)) return;

                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                try {
                    Redemption.fromJSON(response, realm);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                realm.commitTransaction();

                Toast.makeText(getActivity(), "Redeem voucher " + voucher.getName() + " telah berhasil.",
                        Toast.LENGTH_LONG).show();
                dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                API.handleFailure(getActivity(), statusCode, errorResponse);
            }

            @Override
            public void onFinish() {
                loadingDialog.dismiss();
            }
        });
    }
}
