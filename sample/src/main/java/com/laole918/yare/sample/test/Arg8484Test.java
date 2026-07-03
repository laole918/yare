package com.laole918.yare.sample.test;

public final class Arg8484Test extends Test {
    public Arg8484Test() {
        super("target", long.class, int.class, long.class, int.class);
    }

    @Override
    protected int testImpl() {
        return target(1L, 2, 3L, 4);
    }

    private static int target(long a, int b, long c, int d) {
        return a == 1L && b == 2 && c == 3L && d == 4 ? SUCCESS : FAILED;
    }
}
