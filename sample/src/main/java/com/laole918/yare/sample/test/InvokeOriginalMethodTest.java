package com.laole918.yare.sample.test;

import com.laole918.yare.Yare;

public final class InvokeOriginalMethodTest extends Test {
    private boolean originalCalled;

    public InvokeOriginalMethodTest() {
        super("target");
    }

    @Override
    protected int testImpl() {
        originalCalled = false;
        boolean result = target();
        return originalCalled && result ? SUCCESS : FAILED;
    }

    private boolean target() {
        return true;
    }

    @Override
    public void beforeCall(Yare.CallFrame callFrame) throws Throwable {
        super.beforeCall(callFrame);
        Object original = Yare.invokeOriginalMethod(callFrame.method, callFrame.thisObject, callFrame.args);
        if (!Boolean.TRUE.equals(original)) {
            callFrame.setThrowable(new IllegalStateException("unexpected original result " + original));
            return;
        }
        originalCalled = true;
        callFrame.setResult(false);
    }

    @Override
    public void afterCall(Yare.CallFrame callFrame) throws Throwable {
        super.afterCall(callFrame);
        callFrame.setResultIfNoException(true);
    }
}
