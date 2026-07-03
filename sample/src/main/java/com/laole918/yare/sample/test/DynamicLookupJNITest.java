package com.laole918.yare.sample.test;

public final class DynamicLookupJNITest extends Test {
    @Override
    public int run() {
        int input = 17;
        return NativeTargets.dynamicTarget(input) == input * input ? SUCCESS : FAILED;
    }

    @Override
    protected int testImpl() {
        throw new UnsupportedOperationException();
    }
}
