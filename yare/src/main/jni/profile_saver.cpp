#include "profile_saver.h"

#include <dobby.h>

#include "elf_image.h"
#include "log.h"

namespace yare {

static bool FakeProcessProfilingInfo() {
    LOGI("Skipped ProcessProfilingInfo.");
    return true;
}

bool DisableProfileSaver(const ElfImage& handle, int sdkLevel) {
    void* processProfilingInfo =
            handle.GetSymbolAddress("_ZN3art12ProfileSaver20ProcessProfilingInfoEbPtb", false);
    if (processProfilingInfo == nullptr) {
        const char* symbol = sdkLevel < 26
                ? "_ZN3art12ProfileSaver20ProcessProfilingInfoEPt"
                : sdkLevel < 31
                ? "_ZN3art12ProfileSaver20ProcessProfilingInfoEbPt"
                : "_ZN3art12ProfileSaver20ProcessProfilingInfoEbbPt";
        processProfilingInfo = handle.GetSymbolAddress(symbol, false);
        if (processProfilingInfo == nullptr && sdkLevel >= 31) {
            processProfilingInfo =
                    handle.GetSymbolAddress("_ZN3art12ProfileSaver20ProcessProfilingInfoEbPt", false);
        }
    }

    if (processProfilingInfo == nullptr) {
        LOGE("Failed to disable ProfileSaver: art::ProfileSaver::ProcessProfilingInfo not found");
        return false;
    }

    dobby_dummy_func_t backup = nullptr;
    if (DobbyHook(processProfilingInfo,
                  reinterpret_cast<dobby_dummy_func_t>(FakeProcessProfilingInfo),
                  &backup)
            != RS_SUCCESS) {
        LOGE("Failed to hook ProfileSaver::ProcessProfilingInfo");
        return false;
    }

    return true;
}

}  // namespace yare
