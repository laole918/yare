package com.laole918.yare.sample.test;

import android.util.Log;

import com.laole918.yare.Yare;
import com.laole918.yare.sample.SampleApp;

public final class NonStaticTest extends Test {
    public NonStaticTest() {
        super("target");
    }

    @Override
    protected int testImpl() {
        return target() ? SUCCESS : FAILED;
    }

    public boolean target() {
        Log.i(SampleApp.TAG, "NonStaticTest.target()");
        return false;
    }

    @Override
    public void afterCall(Yare.CallFrame callFrame) throws Throwable {
        super.afterCall(callFrame);
        callFrame.setResultIfNoException(true);
    }
}
