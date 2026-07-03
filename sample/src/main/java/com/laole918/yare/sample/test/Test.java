package com.laole918.yare.sample.test;

import android.util.Log;

import com.laole918.yare.Yare;
import com.laole918.yare.callback.MethodHook;
import com.laole918.yare.sample.SampleApp;
import com.laole918.yare.sample.utils.ReflectionHelper;

import java.lang.reflect.Member;
import java.util.Arrays;

public abstract class Test extends MethodHook {
    public static final int IGNORED = 0;
    public static final int SUCCESS = 1;
    public static final int FAILED = -1;

    protected Member target;
    public boolean isCallbackInvoked;
    private boolean hookEnabled = true;

    protected Test() {
    }

    protected Test(String targetName, Class<?>... paramTypes) {
        init(getClass(), targetName, paramTypes);
    }

    protected Test(Class<?> c, String targetName, Class<?>... paramTypes) {
        init(c, targetName, paramTypes);
    }

    protected Test(Member target) {
        this.target = target;
    }

    private void init(Class<?> c, String targetName, Class<?>... paramTypes) {
        if (targetName == null) {
            target = ReflectionHelper.getConstructor(c, paramTypes);
        } else {
            target = ReflectionHelper.getMethod(c, targetName, paramTypes);
        }
    }

    public void setHookEnabled(boolean enabled) {
        hookEnabled = enabled;
    }

    public boolean shouldCheckCallback() {
        return hookEnabled && target != null;
    }

    public int run() {
        if (!hookEnabled || target == null) {
            return runSafely();
        }

        MethodHook.Unhook unhook = Yare.hook(target, this);
        try {
            return runSafely();
        } finally {
            unhook.unhook();
        }
    }

    private int runSafely() {
        try {
            return testImpl();
        } catch (Throwable t) {
            Log.e(SampleApp.TAG, "Test failed: " + getClass().getSimpleName(), t);
            return FAILED;
        }
    }

    protected abstract int testImpl() throws Throwable;

    @Override
    public void beforeCall(Yare.CallFrame callFrame) throws Throwable {
        isCallbackInvoked = true;
        Log.i(SampleApp.TAG, "before " + target + " this=" + callFrame.thisObject
                + " args=" + Arrays.toString(callFrame.args));
    }

    @Override
    public void afterCall(Yare.CallFrame callFrame) throws Throwable {
        Log.i(SampleApp.TAG, "after " + target + " result=" + callFrame.getResult()
                + " throwable=" + callFrame.getThrowable());
    }
}
