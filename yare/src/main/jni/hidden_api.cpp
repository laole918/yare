#include "hidden_api.h"

#include <dobby.h>

#include "elf_image.h"
#include "log.h"

namespace yare {

static int FakeHandleHiddenApi() {
    return 0;
}

static bool HookWithoutBackup(void* target, void* replacement) {
    dobby_dummy_func_t ignored = nullptr;
    return target != nullptr
            && DobbyHook(
                    target,
                    reinterpret_cast<dobby_dummy_func_t>(replacement),
                    &ignored) == RS_SUCCESS;
}

bool DisableHiddenApiPolicy(JNIEnv* env, const ElfImage& handle, int sdkLevel,
                            bool application, bool platform) {
    (void) env;
    if (sdkLevel < 28) {
        return true;
    }

    void* replacement = reinterpret_cast<void*>(FakeHandleHiddenApi);
    bool failed = false;

#define HOOK_SYMBOL(symbol, warn_if_missing)                                  \
    do {                                                                      \
        void* target = handle.GetSymbolAddress(symbol, warn_if_missing);      \
        if (target != nullptr) {                                              \
            if (!HookWithoutBackup(target, replacement)) {                    \
                failed = true;                                                \
            }                                                                 \
        } else {                                                              \
            failed = true;                                                    \
        }                                                                     \
    } while (false)

    if (sdkLevel >= 29) {
        if (application) {
            HOOK_SYMBOL("_ZN3art9hiddenapi6detail28ShouldDenyAccessToMemberImplINS_8ArtFieldEEEbPT_NS0_7ApiListENS0_12AccessMethodE", false);
            HOOK_SYMBOL("_ZN3art9hiddenapi6detail28ShouldDenyAccessToMemberImplINS_9ArtMethodEEEbPT_NS0_7ApiListENS0_12AccessMethodE", false);
        }
        if (platform) {
            HOOK_SYMBOL("_ZN3art9hiddenapi6detail30HandleCorePlatformApiViolationINS_8ArtFieldEEEbPT_RKNS0_13AccessContextENS0_12AccessMethodENS0_17EnforcementPolicyE", false);
            HOOK_SYMBOL("_ZN3art9hiddenapi6detail30HandleCorePlatformApiViolationINS_9ArtMethodEEEbPT_RKNS0_13AccessContextENS0_12AccessMethodENS0_17EnforcementPolicyE", false);
        }
        if (failed) {
            HOOK_SYMBOL("_ZN3art9hiddenapi24ShouldDenyAccessToMemberINS_8ArtFieldEEEbPT_RKNSt3__18functionIFNS0_13AccessContextEvEEENS0_12AccessMethodE", true);
            HOOK_SYMBOL("_ZN3art9hiddenapi24ShouldDenyAccessToMemberINS_9ArtMethodEEEbPT_RKNSt3__18functionIFNS0_13AccessContextEvEEENS0_12AccessMethodE", true);
        }
    } else if (application) {
        HOOK_SYMBOL("_ZN3art9hiddenapi6detail19GetMemberActionImplINS_8ArtFieldEEENS0_6ActionEPT_NS_20HiddenApiAccessFlags7ApiListES4_NS0_12AccessMethodE", true);
        HOOK_SYMBOL("_ZN3art9hiddenapi6detail19GetMemberActionImplINS_9ArtMethodEEENS0_6ActionEPT_NS_20HiddenApiAccessFlags7ApiListES4_NS0_12AccessMethodE", true);
    }

#undef HOOK_SYMBOL

    return !failed;
}

}  // namespace yare
