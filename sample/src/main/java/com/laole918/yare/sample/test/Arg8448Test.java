package com.laole918.yare.sample.test;

public final class Arg8448Test extends Test {
    public Arg8448Test() {
        super("target", long.class, int.class, int.class, long.class);
    }

    @Override
    protected int testImpl() {
        return target(1L, 2, 3, 4L);
    }

    private static int target(long a, int b, int c, long d) {
        return a == 1L && b == 2 && c == 3 && d == 4L ? SUCCESS : FAILED;
    }
}
