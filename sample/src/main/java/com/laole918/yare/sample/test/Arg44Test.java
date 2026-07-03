package com.laole918.yare.sample.test;

public final class Arg44Test extends Test {
    public Arg44Test() {
        super("target", int.class, int.class);
    }

    @Override
    protected int testImpl() {
        return target(326646792, -790347563);
    }

    private static int target(int i, int i2) {
        return i == 326646792 && i2 == -790347563 ? SUCCESS : FAILED;
    }
}
