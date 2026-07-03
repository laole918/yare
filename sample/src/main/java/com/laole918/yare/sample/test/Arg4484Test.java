package com.laole918.yare.sample.test;

public final class Arg4484Test extends Test {
    public Arg4484Test() {
        super("target", int.class, int.class, long.class, int.class);
    }

    @Override
    protected int testImpl() {
        return target(1, 2, 3L, 4);
    }

    private static int target(int a, int b, long c, int d) {
        return a == 1 && b == 2 && c == 3L && d == 4 ? SUCCESS : FAILED;
    }
}
