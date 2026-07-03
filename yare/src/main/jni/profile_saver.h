#ifndef YARE_PROFILE_SAVER_H
#define YARE_PROFILE_SAVER_H

namespace yare {
class ElfImage;

bool DisableProfileSaver(const ElfImage& handle, int sdkLevel);
}

#endif
