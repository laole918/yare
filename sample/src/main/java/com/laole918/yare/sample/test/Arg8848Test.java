package com.laole918.yare.sample.test;

public final class Arg8848Test extends Test {
    public Arg8848Test() {
        super("target", long.class, long.class, int.class, long.class);
    }

    @Override
    protected int testImpl() {
        return target(1L, 2L, 3, 4L);
    }

    private static int target(long a, long b, int c, long d) {
        return a == 1L && b == 2L && c == 3 && d == 4L ? SUCCESS : FAILED;
    }
}
