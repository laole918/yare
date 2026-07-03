package com.laole918.yare.sample.test;

public final class Arg888Test extends Test {
    public Arg888Test() {
        super("target", long.class, long.class, long.class);
    }

    @Override
    protected int testImpl() {
        return target(1L, 2L, 3L);
    }

    private static int target(long a, long b, long c) {
        return a == 1L && b == 2L && c == 3L ? SUCCESS : FAILED;
    }
}
