package com.laole918.yare.sample.test;

import android.util.Log;

import com.laole918.yare.Yare;
import com.laole918.yare.sample.SampleApp;

public final class NotInitedTest extends Test {
    public NotInitedTest() {
        super(Target.class, "target", int.class);
    }

    @Override
    protected int testImpl() {
        return Target.target(114514);
    }

    @Override
    public void beforeCall(Yare.CallFrame callFrame) throws Throwable {
        super.beforeCall(callFrame);
        callFrame.args[0] = 1919810;
    }

    private static final class Target {
        static {
            Log.i(SampleApp.TAG, "NotInitedTarget initializing", new Throwable());
        }

        private static int target(int value) {
            return value == 1919810 ? SUCCESS : FAILED;
        }
    }
}
