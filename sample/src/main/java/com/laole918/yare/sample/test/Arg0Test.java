package com.laole918.yare.sample.test;

import android.util.Log;

import com.laole918.yare.Yare;
import com.laole918.yare.sample.SampleApp;

public final class Arg0Test extends Test {
    public Arg0Test() {
        super("target");
    }

    @Override
    protected int testImpl() {
        return target();
    }

    @Override
    public void afterCall(Yare.CallFrame callFrame) throws Throwable {
        super.afterCall(callFrame);
        callFrame.setResultIfNoException(SUCCESS);
    }

    private static int target() {
        Log.i(SampleApp.TAG, "Arg0Test.target()");
        return FAILED;
    }
}
