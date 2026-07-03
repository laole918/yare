package com.laole918.yare.sample.test;

public final class Arg4888Test extends Test {
    public Arg4888Test() {
        super("target", int.class, long.class, long.class, long.class);
    }

    @Override
    protected int testImpl() {
        return target(1, 2L, 3L, 4L);
    }

    private static int target(int a, long b, long c, long d) {
        return a == 1 && b == 2L && c == 3L && d == 4L ? SUCCESS : FAILED;
    }
}
