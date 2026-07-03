package com.laole918.yare.sample.test;

public final class Arg8Test extends Test {
    public Arg8Test() {
        super("target", long.class);
    }

    @Override
    protected int testImpl() {
        return target(1145141919810L);
    }

    private static int target(long l) {
        return l == 1145141919810L ? SUCCESS : FAILED;
    }
}
