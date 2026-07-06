package com.laole918.yare.sample.test;

import com.laole918.yare.Yare;
import com.laole918.yare.sample.utils.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public final class DelayHookTest extends Test {
    private static final Class<?> TARGET_CLASS = loadTargetClass();
    private static final Method CALL_STATIC_METHOD =
            ReflectionHelper.getMethod(TARGET_CLASS, "callStaticMethod");

    public DelayHookTest() {
        super(resolveTargetMethod());
    }

    @Override
    protected int testImpl() throws InvocationTargetException, IllegalAccessException {
        return Boolean.TRUE.equals(CALL_STATIC_METHOD.invoke(null)) ? SUCCESS : FAILED;
    }

    @Override
    public void afterCall(Yare.CallFrame callFrame) throws Throwable {
        super.afterCall(callFrame);
        callFrame.setResultIfNoException(true);
    }

    private static Member resolveTargetMethod() {
        return ReflectionHelper.getMethod(TARGET_CLASS, "target");
    }

    private static Class<?> loadTargetClass() {
        try {
            return Class.forName(DelayTarget.class.getName(), false, DelayHookTest.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Unable to load DelayTarget", e);
        }
    }

    private static final class DelayTarget {
        private static boolean callStaticMethod() {
            return target();
        }

        private static boolean target() {
            return false;
        }
    }
}
