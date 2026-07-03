package com.laole918.yare.sample.test;

public final class Arg88Test extends Test {
    public Arg88Test() {
        super("target", long.class, long.class);
    }

    @Override
    protected int testImpl() {
        return target(Long.MAX_VALUE, 0xffffffffL);
    }

    private static int target(long l1, long l2) {
        return l1 == Long.MAX_VALUE && l2 == 0xffffffffL ? SUCCESS : FAILED;
    }
}
