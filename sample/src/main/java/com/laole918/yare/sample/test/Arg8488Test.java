package com.laole918.yare.sample.test;

public final class Arg8488Test extends Test {
    public Arg8488Test() {
        super("target", long.class, int.class, long.class, long.class);
    }

    @Override
    protected int testImpl() {
        return target(1L, 2, 3L, 4L);
    }

    private static int target(long a, int b, long c, long d) {
        return a == 1L && b == 2 && c == 3L && d == 4L ? SUCCESS : FAILED;
    }
}
