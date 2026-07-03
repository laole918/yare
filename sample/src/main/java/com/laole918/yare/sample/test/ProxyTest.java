package com.laole918.yare.sample.test;

import android.util.Log;

import com.laole918.yare.Yare;
import com.laole918.yare.sample.SampleApp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;

@SuppressWarnings("unchecked")
public final class ProxyTest extends Test {
    private static final Callable<Long> CALLABLE = (Callable<Long>) Proxy.newProxyInstance(
            ProxyTest.class.getClassLoader(),
            new Class<?>[] {Callable.class},
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) {
                    switch (method.getName()) {
                        case "toString":
                            return proxy.getClass().getName() + "@" + Integer.toHexString(proxy.hashCode());
                        case "hashCode":
                            return System.identityHashCode(proxy);
                        case "equals":
                            return proxy == args[0];
                        default:
                            Log.i(SampleApp.TAG, "Proxy method called...");
                            return 114514L;
                    }
                }
            });

    public ProxyTest() {
        super(CALLABLE.getClass(), "call");
    }

    @Override
    protected int testImpl() throws Exception {
        return CALLABLE.call() == 1919810L ? SUCCESS : FAILED;
    }

    @Override
    public void afterCall(Yare.CallFrame callFrame) throws Throwable {
        super.afterCall(callFrame);
        if (Long.valueOf(114514L).equals(callFrame.getResult())) {
            callFrame.setResult(1919810L);
        }
    }
}
