package com.laole918.yare.sample.test;

public final class Arg448Test extends Test {
    public Arg448Test() {
        super("target", int.class, int.class, long.class);
    }

    @Override
    protected int testImpl() {
        return target(1, 2, 3L);
    }

    private static int target(int a, int b, long c) {
        return a == 1 && b == 2 && c == 3L ? SUCCESS : FAILED;
    }
}
