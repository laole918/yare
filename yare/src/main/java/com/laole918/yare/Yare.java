package com.laole918.yare;

public final class Yare {
    static {
        System.loadLibrary("yare");
    }

    private Yare() {
    }

    public static native String nativeGetVersion();
}
