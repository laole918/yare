package com.laole918.yare.sample;

import android.app.Application;

import com.laole918.yare.Yare;
import com.laole918.yare.YareConfig;

public final class SampleApp extends Application {
    public static final String TAG = "YareSample";
    private static SampleApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        YareConfig.debug = true;
        YareConfig.debuggable = BuildConfig.DEBUG;
        Yare.ensureInitialized();
    }

    public static SampleApp getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SampleApp not initialized");
        }
        return instance;
    }
}
