package com.laole918.yare.sample.test;

public final class Arg8844Test extends Test {
    public Arg8844Test() {
        super("target", long.class, long.class, int.class, int.class);
    }

    @Override
    protected int testImpl() {
        return target(1L, 2L, 3, 4);
    }

    private static int target(long a, long b, int c, int d) {
        return a == 1L && b == 2L && c == 3 && d == 4 ? SUCCESS : FAILED;
    }
}
