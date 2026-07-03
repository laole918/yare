package com.laole918.yare.sample.test;

public final class Arg48Test extends Test {
    public Arg48Test() {
        super("target", int.class, long.class);
    }

    @Override
    protected int testImpl() {
        return target(326646792, 5150256501661869116L);
    }

    private static int target(int i, long l) {
        return i == 326646792 && l == 5150256501661869116L ? SUCCESS : FAILED;
    }
}
