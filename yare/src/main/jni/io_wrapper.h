#ifndef YARE_IO_WRAPPER_H
#define YARE_IO_WRAPPER_H

#include <cerrno>
#include <cstring>
#include <cstdio>
#include <fcntl.h>
#include <unistd.h>

#include "log.h"
#include "macros.h"

namespace yare {

inline bool CanRetry(int error) {
    return error == EINTR || error == EIO;
}

inline int WrappedOpen(const char* pathname, int flags, int maxRetries = 2) {
    for (;;) {
        int fd = open(pathname, flags);
        if (LIKELY(fd != -1)) {
            return fd;
        }

        if (LIKELY(errno == ENOENT)) {
            return -1;
        }

        if (LIKELY(CanRetry(errno) && maxRetries-- > 0)) {
            LOGW("Retrying open %s: errno %d (%s)", pathname, errno, strerror(errno));
        } else {
            LOGE("Failed to open %s: errno %d (%s)", pathname, errno, strerror(errno));
            return -1;
        }
    }
}

inline FILE* WrappedFOpen(const char* pathname, const char* mode, int maxRetries = 2) {
    for (;;) {
        FILE* file = fopen(pathname, mode);
        if (LIKELY(file != nullptr)) {
            return file;
        }

        if (LIKELY(CanRetry(errno) && maxRetries-- > 0)) {
            LOGW("Retrying fopen %s: errno %d (%s)", pathname, errno, strerror(errno));
        } else {
            LOGE("Failed to fopen %s: errno %d (%s)", pathname, errno, strerror(errno));
            return nullptr;
        }
    }
}

}  // namespace yare

#endif
