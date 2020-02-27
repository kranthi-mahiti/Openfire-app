package com.sanjay.openfire;

import android.content.Context;

import androidx.multidex.MultiDexApplication;


public class MyApplication extends MultiDexApplication {

    private static MyApplication instance;

    /**
     * instance
     */
    public MyApplication() {
        instance = this;
    }

    /**
     * @return
     */
    public static Context getContext() {
        return instance;
    }

    /**
     * @return
     */
    public static synchronized MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;


    }



}
