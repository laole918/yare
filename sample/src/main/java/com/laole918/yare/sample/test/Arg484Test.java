package com.laole918.yare.sample.test;

public final class Arg484Test extends Test {
    public Arg484Test() {
        super("target", int.class, long.class, int.class);
    }

    @Override
    protected int testImpl() {
        return target(1, 2L, 3);
    }

    private static int target(int a, long b, int c) {
        return a == 1 && b == 2L && c == 3 ? SUCCESS : FAILED;
    }
}
