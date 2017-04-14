package com.android.promosee.services;

import android.util.Log;

import com.android.promosee.core.Preferences;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
/**
 * Created by angga on 30/01/17.
 */

public class FirebaseInstanceService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        new Preferences(this).set("fcmToken", refreshedToken);
    }
}
