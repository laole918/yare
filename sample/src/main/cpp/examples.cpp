#include <jni.h>

namespace {

jint DirectTarget(JNIEnv*, jclass, jint value) {
    return value;
}

static const JNINativeMethod kDirectMethods[] = {
        {"directTarget", "(I)I", reinterpret_cast<void*>(DirectTarget)},
};

}  // namespace

extern "C" JNIEXPORT jint JNICALL
Java_com_laole918_yare_sample_test_NativeTargets_dynamicTarget(JNIEnv*, jclass, jint value) {
    return value * value;
}

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM* vm, void*) {
    JNIEnv* env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    jclass clazz = env->FindClass("com/laole918/yare/sample/test/NativeTargets");
    if (clazz == nullptr) {
        return JNI_ERR;
    }
    if (env->RegisterNatives(clazz, kDirectMethods,
                             static_cast<jint>(sizeof(kDirectMethods) / sizeof(kDirectMethods[0]))) != JNI_OK) {
        env->DeleteLocalRef(clazz);
        return JNI_ERR;
    }
    env->DeleteLocalRef(clazz);
    return JNI_VERSION_1_6;
}
