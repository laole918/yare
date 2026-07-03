package com.laole918.yare.sample.test;

import com.laole918.yare.Yare;

public final class ConstructorTest extends Test {
    public ConstructorTest() {
        super(Target.class, null, int.class);
    }

    @Override
    protected int testImpl() {
        Target target = new Target(114514);
        return target.success ? SUCCESS : FAILED;
    }

    @Override
    public void beforeCall(Yare.CallFrame callFrame) throws Throwable {
        super.beforeCall(callFrame);
        if (((int) callFrame.args[0]) != 114514) {
            callFrame.setThrowable(new IllegalArgumentException("unexpected constructor arg"));
            return;
        }
        callFrame.args[0] = 1919810;
    }

    @Override
    public void afterCall(Yare.CallFrame callFrame) throws Throwable {
        super.afterCall(callFrame);
        ((Target) callFrame.thisObject).success = true;
    }

    private static final class Target {
        boolean success;

        Target(int value) {
            if (value != 1919810) {
                throw new IllegalArgumentException("bad constructor arg " + value);
            }
        }
    }
}
