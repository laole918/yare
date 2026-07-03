package com.laole918.yare.sample.test;

public final class Arg4Test extends Test {
    public Arg4Test() {
        super("target", int.class);
    }

    @Override
    protected int testImpl() {
        return target(2001361295);
    }

    private static int target(int i) {
        return i == 2001361295 ? SUCCESS : FAILED;
    }
}
