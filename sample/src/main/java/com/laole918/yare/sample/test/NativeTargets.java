package com.laole918.yare.sample.test;

final class NativeTargets {
    static {
        System.loadLibrary("examples");
    }

    private NativeTargets() {
    }

    static native int dynamicTarget(int value);

    static native int directTarget(int value);
}
