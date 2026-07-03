package com.laole918.yare.sample.test;

public final class Arg884Test extends Test {
    public Arg884Test() {
        super("target", long.class, long.class, int.class);
    }

    @Override
    protected int testImpl() {
        return target(1L, 2L, 3);
    }

    private static int target(long a, long b, int c) {
        return a == 1L && b == 2L && c == 3 ? SUCCESS : FAILED;
    }
}
