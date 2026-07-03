package com.laole918.yare.sample.test;

import com.laole918.yare.Yare;

public final class ThrowExceptionTest extends Test {
    public ThrowExceptionTest() {
        super("target");
    }

    @Override
    protected int testImpl() {
        try {
            target();
            return FAILED;
        } catch (IllegalStateException e) {
            return "hooked".equals(e.getMessage()) ? SUCCESS : FAILED;
        }
    }

    private void target() {
        throw new IllegalArgumentException("origin");
    }

    @Override
    public void afterCall(Yare.CallFrame callFrame) throws Throwable {
        super.afterCall(callFrame);
        if (callFrame.hasThrowable()) {
            callFrame.setThrowable(new IllegalStateException("hooked"));
        }
    }
}
