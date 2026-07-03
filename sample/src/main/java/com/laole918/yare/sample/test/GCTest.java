package com.laole918.yare.sample.test;

import android.widget.Toast;

import com.laole918.yare.sample.SampleApp;

import java.lang.ref.WeakReference;

public final class GCTest extends Test {
    @Override
    public int run() {
        WeakReference<Object> ref = new WeakReference<>(new Object());
        Runtime.getRuntime().gc();
        Toast.makeText(
                SampleApp.getInstance(),
                ref.get() == null ? "GC done." : "object is not recycled",
                Toast.LENGTH_SHORT
        ).show();
        return IGNORED;
    }

    @Override
    protected int testImpl() {
        throw new UnsupportedOperationException();
    }
}
