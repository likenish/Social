package com.example.nishtrack.social;

import android.app.Application;

import com.facebook.FacebookSdk;

public class sdk_Intializer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
    }
}
