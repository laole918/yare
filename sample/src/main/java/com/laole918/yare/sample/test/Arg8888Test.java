package com.laole918.yare.sample.test;

public final class Arg8888Test extends Test {
    public Arg8888Test() {
        super("target", long.class, long.class, long.class, long.class);
    }

    @Override
    protected int testImpl() {
        return target(1L, 2L, 3L, 4L);
    }

    private static int target(long a, long b, long c, long d) {
        return a == 1L && b == 2L && c == 3L && d == 4L ? SUCCESS : FAILED;
    }
}
