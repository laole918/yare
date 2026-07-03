package com.laole918.yare.sample.test;

import com.laole918.yare.Yare;
import com.laole918.yare.callback.MethodReplacement;
import com.laole918.yare.sample.utils.ReflectionHelper;

import java.lang.reflect.Member;

public final class MethodReplacementTest extends Test {
    private final Member targetMethod = ReflectionHelper.getMethod(Target.class, "target");
    private final Target receiver = new Target();

    @Override
    public int run() {
        MethodReplacement replacement = new MethodReplacement() {
            @Override
            protected Object replaceCall(Yare.CallFrame callFrame) {
                return 1234;
            }
        };
        var unhook = Yare.hook(targetMethod, replacement);
        try {
            return receiver.target() == 1234 ? SUCCESS : FAILED;
        } finally {
            unhook.unhook();
        }
    }

    @Override
    protected int testImpl() {
        throw new UnsupportedOperationException();
    }

    private static final class Target {
        int target() {
            return 7;
        }
    }
}
