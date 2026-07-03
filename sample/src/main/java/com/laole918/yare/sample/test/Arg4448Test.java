package com.laole918.yare.sample.test;

public final class Arg4448Test extends Test {
    public Arg4448Test() {
        super("target", int.class, int.class, int.class, long.class);
    }

    @Override
    protected int testImpl() {
        return target(1, 2, 3, 4L);
    }

    private static int target(int a, int b, int c, long d) {
        return a == 1 && b == 2 && c == 3 && d == 4L ? SUCCESS : FAILED;
    }
}
