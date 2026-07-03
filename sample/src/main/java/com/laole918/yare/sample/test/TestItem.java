package com.laole918.yare.sample.test;

import android.util.Log;

import com.laole918.yare.sample.SampleApp;

public final class TestItem {
    public final String name;
    public final Test test;

    public TestItem(String name, Test test) {
        this.name = name;
        this.test = test;
    }

    public int run() {
        Log.i(SampleApp.TAG, "Executing " + name);
        int result = test.run();
        Log.i(SampleApp.TAG, "Result of " + name + " : " + result);
        if (result == Test.SUCCESS && test.shouldCheckCallback() && !test.isCallbackInvoked) {
            Log.e(SampleApp.TAG, "Test " + name + " is not hooked");
            result = Test.FAILED;
        }
        test.isCallbackInvoked = false;
        return result;
    }
}
