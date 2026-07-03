package com.laole918.yare.sample.test;

public final class Arg8444Test extends Test {
    public Arg8444Test() {
        super("target", long.class, int.class, int.class, int.class);
    }

    @Override
    protected int testImpl() {
        return target(1L, 2, 3, 4);
    }

    private static int target(long a, int b, int c, int d) {
        return a == 1L && b == 2 && c == 3 && d == 4 ? SUCCESS : FAILED;
    }
}
