package com.laole918.yare.sample.test;

public final class Arg488Test extends Test {
    public Arg488Test() {
        super("target", int.class, long.class, long.class);
    }

    @Override
    protected int testImpl() {
        return target(1, 2L, 3L);
    }

    private static int target(int a, long b, long c) {
        return a == 1 && b == 2L && c == 3L ? SUCCESS : FAILED;
    }
}
