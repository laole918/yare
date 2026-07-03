package com.laole918.yare.sample.test;

public final class Arg4444Test extends Test {
    public Arg4444Test() {
        super("target", int.class, int.class, int.class, int.class);
    }

    @Override
    protected int testImpl() {
        return target(1, 2, 3, 4);
    }

    private static int target(int a, int b, int c, int d) {
        return a == 1 && b == 2 && c == 3 && d == 4 ? SUCCESS : FAILED;
    }
}
