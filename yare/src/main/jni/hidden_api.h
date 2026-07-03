#ifndef YARE_HIDDEN_API_H
#define YARE_HIDDEN_API_H

#include <jni.h>

namespace yare {
class ElfImage;

bool DisableHiddenApiPolicy(JNIEnv* env, const ElfImage& handle, int sdkLevel,
                            bool application, bool platform);
}

#endif
