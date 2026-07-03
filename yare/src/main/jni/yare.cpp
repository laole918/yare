#include <jni.h>

#include <dobby.h>
#include <sys/mman.h>
#include <unistd.h>

#include <memory>
#include <string_view>

#include "elf_image.h"
#include "hidden_api.h"
#include "log.h"
#include "profile_saver.h"

#include "lsplant.hpp"

namespace {

using yare::ElfImage;

std::unique_ptr<ElfImage> gArtElf;
bool gLsplantInitialized = false;
int gSdkLevel = -1;
size_t gPageSize = 0;

inline uintptr_t AlignDown(uintptr_t value, size_t align) {
    return value & -static_cast<intptr_t>(align);
}

inline uintptr_t AlignUp(uintptr_t value, size_t align) {
    return (value + align - 1) & ~(align - 1);
}

bool Unprotect(void* address) {
    auto addressValue = reinterpret_cast<uintptr_t>(address);
    void* pageAddress = reinterpret_cast<void*>(AlignDown(addressValue, gPageSize));
    size_t size = gPageSize;
    if (AlignUp(addressValue + gPageSize, gPageSize) != AlignUp(addressValue, gPageSize)) {
        size += gPageSize;
    }
    if (mprotect(pageAddress, size, PROT_READ | PROT_WRITE | PROT_EXEC) == -1) {
        LOGE("mprotect failed for %p", address);
        return false;
    }
    return true;
}

void* InlineHooker(void* target, void* hooker) {
    if (!Unprotect(target)) {
        return nullptr;
    }

    dobby_dummy_func_t origin = nullptr;
    if (DobbyHook(target, reinterpret_cast<dobby_dummy_func_t>(hooker), &origin) == RS_SUCCESS) {
        return reinterpret_cast<void*>(origin);
    }
    return nullptr;
}

bool InlineUnhooker(void* func) {
    return DobbyDestroy(func) == RT_SUCCESS;
}

bool EnsureArtElfInitialized(int sdkLevel) {
    if (gArtElf != nullptr) {
        return true;
    }

    auto artElf = std::make_unique<ElfImage>(sdkLevel);
    if (!artElf->Open("libart.so", true, true)) {
        return false;
    }
    gArtElf = std::move(artElf);
    return true;
}

bool EnsureLsplantInitialized(JNIEnv* env, int sdkLevel) {
    if (gLsplantInitialized) {
        return true;
    }
    if (!EnsureArtElfInitialized(sdkLevel)) {
        return false;
    }

    lsplant::InitInfo info{
            .inline_hooker = InlineHooker,
            .inline_unhooker = InlineUnhooker,
            .art_symbol_resolver = [](std::string_view symbol) -> void* {
                return gArtElf != nullptr ? gArtElf->GetSymbolAddress(symbol, false, false) : nullptr;
            },
            .art_symbol_prefix_resolver = [](std::string_view symbol) -> void* {
                return gArtElf != nullptr ? gArtElf->GetSymbolAddress(symbol, false, true) : nullptr;
            },
    };

    gLsplantInitialized = lsplant::Init(env, info);
    return gLsplantInitialized;
}

}  // namespace

extern "C" JNIEXPORT jboolean JNICALL
Java_com_laole918_yare_Yare_init0(JNIEnv* env, jclass, jint androidVersion, jboolean, jboolean,
                                  jboolean, jboolean disableHiddenApiPolicy,
                                  jboolean disableHiddenApiPolicyForPlatformDomain) {
    gSdkLevel = androidVersion;
    if (!EnsureArtElfInitialized(gSdkLevel)) {
        return JNI_FALSE;
    }

    if (disableHiddenApiPolicy || disableHiddenApiPolicyForPlatformDomain) {
        if (!yare::DisableHiddenApiPolicy(
                    env,
                    *gArtElf,
                    gSdkLevel,
                    disableHiddenApiPolicy == JNI_TRUE,
                    disableHiddenApiPolicyForPlatformDomain == JNI_TRUE)) {
            LOGW("Failed to disable hidden api policy");
        }
    }

    if (!EnsureLsplantInitialized(env, gSdkLevel)) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_laole918_yare_Yare_hook0(JNIEnv* env, jclass, jobject context, jobject original,
                                  jobject callback) {
    if (!EnsureLsplantInitialized(env, gSdkLevel)) {
        return nullptr;
    }
    return lsplant::Hook(env, original, context, callback);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_laole918_yare_Yare_unhook0(JNIEnv* env, jclass, jobject target) {
    return lsplant::UnHook(env, target);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_laole918_yare_Yare_deoptimize0(JNIEnv* env, jclass, jobject target) {
    return lsplant::Deoptimize(env, target);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_laole918_yare_Yare_disableProfileSaver0(JNIEnv* env, jclass) {
    if (!EnsureLsplantInitialized(env, gSdkLevel)) {
        return JNI_FALSE;
    }
    return yare::DisableProfileSaver(*gArtElf, gSdkLevel);
}

extern "C" JNIEXPORT void JNICALL
Java_com_laole918_yare_Yare_disableHiddenApiPolicy0(JNIEnv* env, jclass, jboolean application,
                                                    jboolean platform) {
    if (!EnsureArtElfInitialized(gSdkLevel)) {
        return;
    }
    yare::DisableHiddenApiPolicy(env, *gArtElf, gSdkLevel,
                                 application == JNI_TRUE, platform == JNI_TRUE);
}

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM* vm, void*) {
    JNIEnv* env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    gPageSize = static_cast<size_t>(sysconf(_SC_PAGESIZE));
    return JNI_VERSION_1_6;
}
