package com.laole918.yare.sample.test;

public final class Arg4488Test extends Test {
    public Arg4488Test() {
        super("target", int.class, int.class, long.class, long.class);
    }

    @Override
    protected int testImpl() {
        return target(1, 2, 3L, 4L);
    }

    private static int target(int a, int b, long c, long d) {
        return a == 1 && b == 2 && c == 3L && d == 4L ? SUCCESS : FAILED;
    }
}
