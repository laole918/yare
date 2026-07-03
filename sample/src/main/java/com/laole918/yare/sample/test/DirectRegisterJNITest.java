package com.laole918.yare.sample.test;

public final class DirectRegisterJNITest extends Test {
    @Override
    public int run() {
        int input = 23;
        return NativeTargets.directTarget(input) == input ? SUCCESS : FAILED;
    }

    @Override
    protected int testImpl() {
        throw new UnsupportedOperationException();
    }
}
