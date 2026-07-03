package com.laole918.yare.sample.test;

import com.laole918.yare.Yare;

public final class DirectMethodTest extends Test {
    public DirectMethodTest() {
        super("target");
    }

    @Override
    protected int testImpl() {
        return call() ? SUCCESS : FAILED;
    }

    private boolean call() {
        return target();
    }

    private boolean target() {
        return false;
    }

    @Override
    public void afterCall(Yare.CallFrame callFrame) throws Throwable {
        super.afterCall(callFrame);
        callFrame.setResultIfNoException(true);
    }
}
