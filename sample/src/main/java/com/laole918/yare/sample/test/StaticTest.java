package com.laole918.yare.sample.test;

import com.laole918.yare.Yare;

public final class StaticTest extends Test {
    public StaticTest() {
        super("target");
    }

    @Override
    protected int testImpl() {
        return target() ? SUCCESS : FAILED;
    }

    private static boolean target() {
        return false;
    }

    @Override
    public void afterCall(Yare.CallFrame callFrame) throws Throwable {
        super.afterCall(callFrame);
        callFrame.setResultIfNoException(true);
    }
}
