package com.laole918.yare.sample.test;

public final class Arg444Test extends Test {
    public Arg444Test() {
        super("target", int.class, int.class, int.class);
    }

    @Override
    protected int testImpl() {
        return target(1, 2, 3);
    }

    private static int target(int a, int b, int c) {
        return a == 1 && b == 2 && c == 3 ? SUCCESS : FAILED;
    }
}
