package com.android.promosee.core;

import android.app.Application;
import com.facebook.drawee.backends.pipeline.Fresco;
import io.realm.Realm;

/**
 * Created by imam on 03/10/16.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        Fresco.initialize(this);
    }
}
