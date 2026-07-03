package com.laole918.yare.sample.test;

public final class Arg8884Test extends Test {
    public Arg8884Test() {
        super("target", long.class, long.class, long.class, int.class);
    }

    @Override
    protected int testImpl() {
        return target(1L, 2L, 3L, 4);
    }

    private static int target(long a, long b, long c, int d) {
        return a == 1L && b == 2L && c == 3L && d == 4 ? SUCCESS : FAILED;
    }
}
