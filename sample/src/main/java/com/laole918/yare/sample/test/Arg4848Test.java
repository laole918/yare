package com.laole918.yare.sample.test;

public final class Arg4848Test extends Test {
    public Arg4848Test() {
        super("target", int.class, long.class, int.class, long.class);
    }

    @Override
    protected int testImpl() {
        return target(1, 2L, 3, 4L);
    }

    private static int target(int a, long b, int c, long d) {
        return a == 1 && b == 2L && c == 3 && d == 4L ? SUCCESS : FAILED;
    }
}
