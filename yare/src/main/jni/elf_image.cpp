#include "elf_image.h"

#include <cerrno>
#include <cstdio>
#include <cstring>
#include <string>
#include <fcntl.h>
#include <sys/mman.h>
#include <unistd.h>

#include <xz.h>

#include "io_wrapper.h"
#include "log.h"
#include "macros.h"

namespace yare {

bool ElfImage::Open(const char* elf, bool warnIfNonexist, bool warnIfSymtabNotFound) {
    elfName_ = elf;
    return elf[0] == '/'
            ? OpenAbsolute(elf, warnIfNonexist, warnIfSymtabNotFound)
            : OpenRelative(elf, warnIfNonexist, warnIfSymtabNotFound);
}

bool ElfImage::OpenAbsolute(const char* path, bool warnIfNonexist, bool warnIfSymtabNotFound) {
    int fd = WrappedOpen(path, O_RDONLY | O_CLOEXEC);
    if (fd == -1) {
        if (warnIfNonexist) {
            LOGE("Failed to open %s", path);
        }
        return false;
    }

    size_ = lseek(fd, 0, SEEK_END);
    if (UNLIKELY(size_ <= 0)) {
        LOGE("lseek() failed for %s: errno %d (%s)", path, errno, strerror(errno));
        close(fd);
        return false;
    }

    base_ = GetModuleBase(path);
    if (UNLIKELY(base_ == nullptr)) {
        LOGE("Cannot find base loaded address of %s", path);
        close(fd);
        return false;
    }

    header_ = reinterpret_cast<ElfEhdr*>(mmap(nullptr, size_, PROT_READ, MAP_SHARED, fd, 0));
    close(fd);
    if (UNLIKELY(header_ == MAP_FAILED)) {
        LOGE("mmap() failed for %s: errno %d (%s)", path, errno, strerror(errno));
        header_ = nullptr;
        return false;
    }

    ParseMemory(header_, false);
    if (UNLIKELY(symtab_ == nullptr && symtabFromDebugData_ == nullptr && warnIfSymtabNotFound)) {
        LOGW("No symtab found in %s", path);
    }
    return true;
}

bool ElfImage::OpenRelative(const char* elf, bool warnIfNonexist, bool warnIfSymtabNotFound) {
    char buffer[128] = {0};

    if (androidVersion_ >= 29) {
        strcpy(buffer, kApexArtLibDir);
        strcat(buffer, elf);
        if (OpenAbsolute(buffer, false, warnIfSymtabNotFound)) {
            return true;
        }

        memset(buffer, 0, sizeof(buffer));
        strcpy(buffer, kApexRuntimeLibDir);
        strcat(buffer, elf);
        if (OpenAbsolute(buffer, false, warnIfSymtabNotFound)) {
            return true;
        }
    }

    memset(buffer, 0, sizeof(buffer));
    strcpy(buffer, kSystemLibDir);
    strcat(buffer, elf);
    return OpenAbsolute(buffer, warnIfNonexist, warnIfSymtabNotFound);
}

void ElfImage::ParseMemory(ElfEhdr* header, bool isDebugData) {
    auto headerAddress = reinterpret_cast<uintptr_t>(header);
    auto* sectionHeaders = reinterpret_cast<ElfShdr*>(headerAddress + header->e_shoff);
    auto sectionOffset = reinterpret_cast<uintptr_t>(sectionHeaders);
    auto* sectionStr = reinterpret_cast<char*>(
            headerAddress + sectionHeaders[header->e_shstrndx].sh_offset);

    for (int i = 0; i < header->e_shnum; ++i, sectionOffset += header->e_shentsize) {
        auto* section = reinterpret_cast<ElfShdr*>(sectionOffset);
        char* name = sectionStr + section->sh_name;
        ElfOff entsize = section->sh_entsize;

        switch (section->sh_type) {
            case SHT_DYNSYM:
                if (!isDebugData && bias_ == -4396) {
                    dynsym_ = reinterpret_cast<ElfSym*>(headerAddress + section->sh_offset);
                    dynsymCount_ = section->sh_size / entsize;
                }
                break;
            case SHT_SYMTAB:
                if (strcmp(name, ".symtab") == 0) {
                    if (isDebugData) {
                        symtabFromDebugData_ =
                                reinterpret_cast<ElfSym*>(headerAddress + section->sh_offset);
                        symtabCountFromDebugData_ = section->sh_size / entsize;
                    } else {
                        symtab_ = reinterpret_cast<ElfSym*>(headerAddress + section->sh_offset);
                        symtabCount_ = section->sh_size / entsize;
                    }
                }
                break;
            case SHT_STRTAB:
                if (!isDebugData && strcmp(name, ".dynstr") == 0) {
                    dynstr_ = reinterpret_cast<const char*>(headerAddress + section->sh_offset);
                } else if (strcmp(name, ".strtab") == 0) {
                    if (isDebugData) {
                        strtabFromDebugData_ = headerAddress + section->sh_offset;
                    } else {
                        strtab_ = headerAddress + section->sh_offset;
                    }
                }
                break;
            case SHT_PROGBITS:
                if (!isDebugData && dynstr_ != nullptr && dynsym_ != nullptr && bias_ == -4396) {
                    bias_ = static_cast<off_t>(section->sh_addr) - static_cast<off_t>(section->sh_offset);
                }
                if (!isDebugData && strcmp(name, ".gnu_debugdata") == 0) {
                    auto* debugData = reinterpret_cast<uint8_t*>(headerAddress + section->sh_offset);
                    ParseDebugData(debugData, section->sh_size);
                }
                break;
            default:
                break;
        }
    }
}

void ElfImage::ParseDebugData(uint8_t* debugData, size_t size) {
    uint8_t buffer[8192];
    xz_crc32_init();
    xz_dec* decoder = xz_dec_init(XZ_DYNALLOC, 1 << 20);
    if (decoder == nullptr) {
        LOGE("Failed to initialize xz decoder");
        return;
    }

    xz_buf xzBuffer{
            .in = debugData,
            .in_pos = 0,
            .in_size = size,
            .out = buffer,
            .out_pos = 0,
            .out_size = sizeof(buffer),
    };

    while (xzBuffer.in_pos != size) {
        xz_ret ret = xz_dec_run(decoder, &xzBuffer);
        if (ret != XZ_OK && ret != XZ_STREAM_END) {
            LOGE("Failed to decompress .gnu_debugdata: %d", ret);
            xz_dec_end(decoder);
            debugData_.clear();
            return;
        }
        debugData_.insert(debugData_.end(), buffer, buffer + xzBuffer.out_pos);
        xzBuffer.out_pos = 0;
    }

    xz_dec_end(decoder);
    ParseMemory(reinterpret_cast<ElfEhdr*>(debugData_.data()), true);
}

ElfAddr ElfImage::Lookup(std::string_view name, bool matchPrefix) const {
    if (dynsym_ != nullptr && dynstr_ != nullptr) {
        for (ElfOff i = 0; i < dynsymCount_; ++i) {
            std::string_view symbolName(dynstr_ + dynsym_[i].st_name);
            if (symbolName == name || (matchPrefix && symbolName.starts_with(name))) {
                return dynsym_[i].st_value;
            }
        }
    }

    if (symtab_ != nullptr && strtab_ != 0) {
        for (ElfOff i = 0; i < symtabCount_; ++i) {
            unsigned int type = ELF_ST_TYPE(symtab_[i].st_info);
            auto* symbolName = reinterpret_cast<const char*>(strtab_ + symtab_[i].st_name);
            if (type == STT_FUNC && symtab_[i].st_size) {
                std::string_view current(symbolName);
                if (current == name || (matchPrefix && current.starts_with(name))) {
                    return symtab_[i].st_value;
                }
            }
        }
    }

    if (symtabFromDebugData_ != nullptr && strtabFromDebugData_ != 0) {
        for (ElfOff i = 0; i < symtabCountFromDebugData_; ++i) {
            unsigned int type = ELF_ST_TYPE(symtabFromDebugData_[i].st_info);
            auto* symbolName =
                    reinterpret_cast<const char*>(strtabFromDebugData_ + symtabFromDebugData_[i].st_name);
            if (type == STT_FUNC && symtabFromDebugData_[i].st_size) {
                std::string_view current(symbolName);
                if (current == name || (matchPrefix && current.starts_with(name))) {
                    return symtabFromDebugData_[i].st_value;
                }
            }
        }
    }

    return 0;
}

void* ElfImage::GetSymbolAddress(std::string_view name, bool warnIfMissing, bool matchPrefix) const {
    if (base_ == nullptr) {
        return nullptr;
    }

    ElfAddr offset = Lookup(name, matchPrefix);
    if (offset == 0) {
        if (warnIfMissing) {
            LOGE("Symbol %.*s not found in %s", static_cast<int>(name.size()), name.data(), elfName_);
        }
        return nullptr;
    }

    return reinterpret_cast<void*>(reinterpret_cast<uintptr_t>(base_) + offset - bias_);
}

void* ElfImage::GetModuleBase(const char* name) const {
    FILE* maps = WrappedFOpen("/proc/self/maps", "re");
    if (maps == nullptr) {
        return nullptr;
    }

    char buffer[256];
    bool found = false;
    while (fgets(buffer, sizeof(buffer), maps) != nullptr) {
        if (strstr(buffer, name) != nullptr
                && (strstr(buffer, "r-xp") != nullptr || strstr(buffer, "r--p") != nullptr)) {
            found = true;
            break;
        }
    }

    if (!found) {
        fclose(maps);
        return nullptr;
    }

    off_t loadAddress;
    if (sscanf(buffer, "%lx", &loadAddress) != 1) {
        fclose(maps);
        return nullptr;
    }

    fclose(maps);
    return reinterpret_cast<void*>(loadAddress);
}

ElfImage::~ElfImage() {
    if (header_ != nullptr) {
        munmap(header_, size_);
    }
}

}  // namespace yare
