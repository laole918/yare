package com.laole918.yare.callback;

import com.laole918.yare.Yare;

import java.lang.reflect.Member;

public abstract class MethodHook {
    public void beforeCall(Yare.CallFrame callFrame) throws Throwable {
    }

    public void afterCall(Yare.CallFrame callFrame) throws Throwable {
    }

    public class Unhook {
        private final Yare.HookRecord hookRecord;

        public Unhook(Yare.HookRecord hookRecord) {
            this.hookRecord = hookRecord;
        }

        public Member getTarget() {
            return hookRecord.target;
        }

        public MethodHook getCallback() {
            return MethodHook.this;
        }

        public void unhook() {
            Yare.getHookHandler().handleUnhook(hookRecord, MethodHook.this);
        }

        public void unhookPhysical() {
            Yare.getHookHandler().handleUnhookPhysical(hookRecord, MethodHook.this);
        }
    }
}
