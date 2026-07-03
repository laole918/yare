package com.laole918.yare.sample.test;

public final class Arg848Test extends Test {
    public Arg848Test() {
        super("target", long.class, int.class, long.class);
    }

    @Override
    protected int testImpl() {
        return target(1L, 2, 3L);
    }

    private static int target(long a, int b, long c) {
        return a == 1L && b == 2 && c == 3L ? SUCCESS : FAILED;
    }
}
