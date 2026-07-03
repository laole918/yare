package com.laole918.yare.sample.test;

import android.content.Context;
import android.widget.Toast;

import com.laole918.yare.Yare;
import com.laole918.yare.sample.SampleApp;

public final class ToastHookTest extends Test {
    public ToastHookTest() {
        super(Toast.class, "makeText", Context.class, CharSequence.class, int.class);
    }

    @Override
    protected int testImpl() {
        Toast.makeText(SampleApp.getInstance(), "ToastHookTest failed", Toast.LENGTH_SHORT).show();
        return IGNORED;
    }

    @Override
    public void beforeCall(Yare.CallFrame callFrame) throws Throwable {
        super.beforeCall(callFrame);
        callFrame.args[1] = "ToastHookTest success";
    }
}
