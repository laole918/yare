#include <jni.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_laole918_yare_Yare_nativeGetVersion(JNIEnv* env, jclass) {
    return env->NewStringUTF("yare");
}
