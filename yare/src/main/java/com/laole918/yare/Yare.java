package com.laole918.yare;

import android.util.Log;

import com.laole918.yare.callback.MethodHook;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public final class Yare {
    private static final String TAG = "Yare";

    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    private static final Map<Member, HookRecord> HOOK_RECORDS = new HashMap<>();
    private static final Object HOOK_LOCK = new Object();

    private static volatile boolean initialized;
    private static Method callbackMethod;
    private static HookHandler hookHandler = new HookHandler() {
        @Override
        public MethodHook.Unhook handleHook(HookRecord hookRecord, MethodHook hook, int modifiers,
                                            boolean newMethod, boolean canInitDeclaringClass) {
            if (newMethod) {
                hookNewMethod(hookRecord);
            }

            if (hook == null) {
                return null;
            }

            hookRecord.addCallback(hook);
            return hook.new Unhook(hookRecord);
        }

        @Override
        public void handleUnhook(HookRecord hookRecord, MethodHook hook) {
            synchronized (HOOK_LOCK) {
                hookRecord.removeCallback(hook);
            }
        }

        @Override
        public void handleUnhookPhysical(HookRecord hookRecord, MethodHook hook) {
            synchronized (HOOK_LOCK) {
                hookRecord.removeCallback(hook);
                if (hookRecord.emptyCallbacks()) {
                    HOOK_RECORDS.remove(hookRecord.target);
                    unhook0(hookRecord.target);
                }
            }
        }
    };
    private static HookListener hookListener;

    private Yare() {
        throw new AssertionError("No instances.");
    }

    public static void ensureInitialized() {
        if (initialized) {
            return;
        }

        synchronized (Yare.class) {
            if (initialized) {
                return;
            }

            try {
                YareConfig.libLoader.loadLib();
                callbackMethod = HookRecord.class.getMethod("callback", Object[].class);
                boolean result = init0(
                        YareConfig.sdkLevel,
                        YareConfig.debug,
                        YareConfig.debuggable,
                        YareConfig.antiChecks,
                        YareConfig.disableHiddenApiPolicy,
                        YareConfig.disableHiddenApiPolicyForPlatformDomain
                );
                if (!result) {
                    throw new IllegalStateException("Native initialization failed");
                }
                initialized = true;
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to initialize callback method", e);
            }
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static MethodHook.Unhook hook(Member method, MethodHook callback) {
        return hook(method, callback, true);
    }

    public static MethodHook.Unhook hook(Member method, MethodHook callback, boolean canInitDeclaringClass) {
        if (method == null) {
            throw new NullPointerException("method == null");
        }
        if (callback == null) {
            throw new NullPointerException("callback == null");
        }

        int modifiers = method.getModifiers();
        if (method instanceof Method) {
            if (Modifier.isAbstract(modifiers)) {
                throw new IllegalArgumentException("Cannot hook abstract methods: " + method);
            }
            ((Method) method).setAccessible(true);
        } else if (method instanceof Constructor) {
            if (Modifier.isStatic(modifiers)) {
                throw new IllegalArgumentException("Cannot hook class initializer: " + method);
            }
            ((Constructor<?>) method).setAccessible(true);
        } else {
            throw new IllegalArgumentException("Only methods and constructors can be hooked: " + method);
        }

        ensureInitialized();

        HookListener listener = hookListener;
        if (listener != null) {
            listener.beforeHook(method, callback);
        }

        HookRecord hookRecord;
        boolean newMethod = false;
        synchronized (HOOK_LOCK) {
            hookRecord = HOOK_RECORDS.get(method);
            if (hookRecord == null) {
                hookRecord = new HookRecord(method);
                HOOK_RECORDS.put(method, hookRecord);
                newMethod = true;
            }
        }

        MethodHook.Unhook unhook = hookHandler.handleHook(
                hookRecord,
                callback,
                modifiers,
                newMethod,
                canInitDeclaringClass
        );

        if (listener != null) {
            listener.afterHook(method, unhook);
        }
        return unhook;
    }

    public static boolean isHooked(Member method) {
        if (!(method instanceof Method || method instanceof Constructor)) {
            throw new IllegalArgumentException("Only methods and constructors can be hooked: " + method);
        }
        synchronized (HOOK_LOCK) {
            return HOOK_RECORDS.containsKey(method);
        }
    }

    public static Object invokeOriginalMethod(Member method, Object thisObject, Object... args)
            throws IllegalAccessException, InvocationTargetException {
        if (method == null) {
            throw new NullPointerException("method == null");
        }
        if (args == null) {
            args = EMPTY_OBJECT_ARRAY;
        }

        HookRecord hookRecord;
        synchronized (HOOK_LOCK) {
            hookRecord = HOOK_RECORDS.get(method);
        }
        if (hookRecord != null) {
            return invokeMember(hookRecord.backup, thisObject, args);
        }

        return invokeMember(method, thisObject, args);
    }

    public static boolean decompile(Member method, boolean disableJit) {
        checkMethod(method);
        ensureInitialized();
        return deoptimize0(method);
    }

    public static boolean disableProfileSaver() {
        ensureInitialized();
        return disableProfileSaver0();
    }

    public static void disableHiddenApiPolicy(boolean application, boolean platform) {
        if (initialized) {
            disableHiddenApiPolicy0(application, platform);
        } else {
            YareConfig.disableHiddenApiPolicy = application;
            YareConfig.disableHiddenApiPolicyForPlatformDomain = platform;
            ensureInitialized();
        }
    }

    public static HookHandler getHookHandler() {
        return hookHandler;
    }

    public static void setHookHandler(HookHandler handler) {
        if (handler == null) {
            throw new NullPointerException("handler == null");
        }
        hookHandler = handler;
    }

    public static HookListener getHookListener() {
        return hookListener;
    }

    public static void setHookListener(HookListener listener) {
        hookListener = listener;
    }

    public static void log(String message) {
        if (YareConfig.debug) {
            Log.i(TAG, message);
        }
    }

    public static void log(String format, Object... args) {
        if (YareConfig.debug) {
            Log.i(TAG, String.format(format, args));
        }
    }

    private static void hookNewMethod(HookRecord hookRecord) {
        Method backup = hook0(hookRecord, hookRecord.target, callbackMethod);
        if (backup == null) {
            throw new IllegalStateException("Failed to hook method " + hookRecord.target);
        }
        backup.setAccessible(true);
        hookRecord.backup = backup;
    }

    private static Object invokeMember(Member member, Object thisObject, Object[] args)
            throws IllegalAccessException, InvocationTargetException {
        if (member instanceof Method) {
            Method method = (Method) member;
            method.setAccessible(true);
            return method.invoke(thisObject, args);
        }

        Constructor<?> constructor = (Constructor<?>) member;
        constructor.setAccessible(true);
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Cannot instantiate " + constructor, e);
        }
    }

    private static void checkMethod(Member method) {
        if (method == null) {
            throw new NullPointerException("method == null");
        }
        if (!(method instanceof Method || method instanceof Constructor)) {
            throw new IllegalArgumentException("Only methods and constructors are supported: " + method);
        }
        if (Modifier.isAbstract(method.getModifiers())) {
            throw new IllegalArgumentException("Cannot operate on abstract methods: " + method);
        }
    }

    private static native boolean init0(int androidVersion, boolean debug, boolean debuggable,
                                        boolean antiChecks, boolean disableHiddenApiPolicy,
                                        boolean disableHiddenApiPolicyForPlatformDomain);

    private static native Method hook0(Object context, Member original, Method callback);

    private static native boolean unhook0(Member target);

    private static native boolean deoptimize0(Member target);

    private static native boolean disableProfileSaver0();

    private static native void disableHiddenApiPolicy0(boolean application, boolean platform);

    public interface HookListener {
        void beforeHook(Member method, MethodHook callback);

        void afterHook(Member method, MethodHook.Unhook unhook);
    }

    public interface LibLoader {
        void loadLib();
    }

    public interface HookHandler {
        MethodHook.Unhook handleHook(HookRecord hookRecord, MethodHook hook, int modifiers,
                                     boolean newMethod, boolean canInitDeclaringClass);

        void handleUnhook(HookRecord hookRecord, MethodHook hook);

        void handleUnhookPhysical(HookRecord hookRecord, MethodHook hook);
    }

    public static final class HookRecord {
        public final Member target;
        public Method backup;
        private final Set<MethodHook> callbacks = new HashSet<>();
        private final boolean isStatic;
        private final Class<?> returnType;

        public HookRecord(Member target) {
            this.target = target;
            this.isStatic = Modifier.isStatic(target.getModifiers());
            if (target instanceof Method) {
                Class<?> candidate = ((Method) target).getReturnType();
                this.returnType = candidate.isPrimitive() ? null : candidate;
            } else {
                this.returnType = null;
            }
        }

        public synchronized void addCallback(MethodHook callback) {
            callbacks.add(callback);
        }

        public synchronized void removeCallback(MethodHook callback) {
            callbacks.remove(callback);
        }

        public synchronized boolean emptyCallbacks() {
            return callbacks.isEmpty();
        }

        public synchronized MethodHook[] getCallbacks() {
            return callbacks.toArray(new MethodHook[0]);
        }

        public Object callBackup(Object thisObject, Object... args)
                throws InvocationTargetException, IllegalAccessException {
            return invokeMember(backup, thisObject, args);
        }

        public Object callback(Object[] args) throws Throwable {
            CallFrame callFrame = new CallFrame(this);
            if (isStatic) {
                callFrame.thisObject = null;
                callFrame.args = args != null ? args : EMPTY_OBJECT_ARRAY;
            } else {
                if (args == null || args.length == 0) {
                    throw new IllegalStateException("Missing receiver for " + target);
                }
                callFrame.thisObject = args[0];
                callFrame.args = new Object[args.length - 1];
                System.arraycopy(args, 1, callFrame.args, 0, callFrame.args.length);
            }

            MethodHook[] callbacksSnapshot = getCallbacks();
            if (YareConfig.disableHooks || callbacksSnapshot.length == 0) {
                try {
                    return invokeMember(backup, callFrame.thisObject, callFrame.args);
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }

            int beforeIdx = 0;
            do {
                try {
                    callbacksSnapshot[beforeIdx].beforeCall(callFrame);
                } catch (Throwable t) {
                    Log.e(TAG, "Unexpected exception in beforeCall", t);
                    callFrame.resetResult();
                    continue;
                }

                if (callFrame.returnEarly) {
                    beforeIdx++;
                    break;
                }
            } while (++beforeIdx < callbacksSnapshot.length);

            if (!callFrame.returnEarly) {
                try {
                    callFrame.setResult(callFrame.invokeOriginalMethod());
                } catch (InvocationTargetException e) {
                    callFrame.setThrowable(e.getCause());
                }
            }

            int afterIdx = beforeIdx - 1;
            while (afterIdx >= 0) {
                Object lastResult = callFrame.getResult();
                Throwable lastThrowable = callFrame.getThrowable();
                try {
                    callbacksSnapshot[afterIdx].afterCall(callFrame);
                } catch (Throwable t) {
                    Log.e(TAG, "Unexpected exception in afterCall", t);
                    if (lastThrowable == null) {
                        callFrame.setResult(lastResult);
                    } else {
                        callFrame.setThrowable(lastThrowable);
                    }
                }
                afterIdx--;
            }

            Object result = callFrame.getResultOrThrowable();
            if (returnType != null) {
                result = returnType.cast(result);
            }
            return result;
        }
    }

    public static class CallFrame {
        public final Member method;
        public Object thisObject;
        public Object[] args = EMPTY_OBJECT_ARRAY;

        private Object result;
        private Throwable throwable;
        private boolean returnEarly;
        private final HookRecord hookRecord;

        public CallFrame(HookRecord hookRecord) {
            this.hookRecord = hookRecord;
            this.method = hookRecord.target;
        }

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
            this.throwable = null;
            this.returnEarly = true;
        }

        public void setResultIfNoException(Object result) {
            if (throwable == null) {
                setResult(result);
            }
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public boolean hasThrowable() {
            return throwable != null;
        }

        public void setThrowable(Throwable throwable) {
            this.throwable = throwable;
            this.result = null;
            this.returnEarly = true;
        }

        public Object getResultOrThrowable() throws Throwable {
            if (throwable != null) {
                throw throwable;
            }
            return result;
        }

        public void resetResult() {
            this.result = null;
            this.throwable = null;
            this.returnEarly = false;
        }

        public Object invokeOriginalMethod() throws InvocationTargetException, IllegalAccessException {
            return hookRecord.callBackup(thisObject, args);
        }

        public Object invokeOriginalMethod(Object thisObject, Object... args)
                throws InvocationTargetException, IllegalAccessException {
            return hookRecord.callBackup(thisObject, args);
        }
    }
}
