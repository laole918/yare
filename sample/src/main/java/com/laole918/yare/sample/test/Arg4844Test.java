package com.laole918.yare.sample.test;

public final class Arg4844Test extends Test {
    public Arg4844Test() {
        super("target", int.class, long.class, int.class, int.class);
    }

    @Override
    protected int testImpl() {
        return target(1, 2L, 3, 4);
    }

    private static int target(int a, long b, int c, int d) {
        return a == 1 && b == 2L && c == 3 && d == 4 ? SUCCESS : FAILED;
    }
}
