package com.laole918.yare.callback;

import com.laole918.yare.Yare;

import java.lang.reflect.Member;

public abstract class MethodReplacement extends MethodHook {
    public static final MethodReplacement DO_NOTHING = new MethodReplacement() {
        @Override
        protected Object replaceCall(Yare.CallFrame callFrame) {
            return null;
        }
    };

    protected abstract Object replaceCall(Yare.CallFrame callFrame) throws Throwable;

    @Override
    public final void beforeCall(Yare.CallFrame callFrame) {
        try {
            callFrame.setResult(replaceCall(callFrame));
        } catch (Throwable e) {
            callFrame.setThrowable(e);
        }
    }

    @Override
    public final void afterCall(Yare.CallFrame callFrame) {
    }

    public static MethodReplacement returnConstant(final Object result) {
        return new MethodReplacement() {
            @Override
            protected Object replaceCall(Yare.CallFrame callFrame) {
                return result;
            }
        };
    }
}
