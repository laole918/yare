package com.laole918.yare.sample.test;

public final class Arg4884Test extends Test {
    public Arg4884Test() {
        super("target", int.class, long.class, long.class, int.class);
    }

    @Override
    protected int testImpl() {
        return target(1, 2L, 3L, 4);
    }

    private static int target(int a, long b, long c, int d) {
        return a == 1 && b == 2L && c == 3L && d == 4 ? SUCCESS : FAILED;
    }
}
