package com.laole918.yare.sample.test;

import com.laole918.yare.Yare;

public final class AccessHiddenApiTest extends Test {
    @Override
    public int run() {
        try {
            Yare.disableHiddenApiPolicy(true, true);
            return SUCCESS;
        } catch (Throwable ignored) {
            return FAILED;
        }
    }

    @Override
    protected int testImpl() {
        throw new UnsupportedOperationException();
    }
}
