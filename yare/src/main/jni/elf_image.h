#ifndef YARE_ELF_IMAGE_H
#define YARE_ELF_IMAGE_H

#include <linux/elf.h>

#include <cstdint>
#include <string_view>
#include <vector>

#if defined(__LP64__)
using ElfEhdr = Elf64_Ehdr;
using ElfShdr = Elf64_Shdr;
using ElfAddr = Elf64_Addr;
using ElfSym = Elf64_Sym;
using ElfOff = Elf64_Off;
#else
using ElfEhdr = Elf32_Ehdr;
using ElfShdr = Elf32_Shdr;
using ElfAddr = Elf32_Addr;
using ElfSym = Elf32_Sym;
using ElfOff = Elf32_Off;
#endif

namespace yare {

class ElfImage {
public:
    explicit ElfImage(int androidVersion) : androidVersion_(androidVersion) {
    }

    bool Open(const char* elf, bool warnIfNonexist = true, bool warnIfSymtabNotFound = true);
    bool IsOpened() const {
        return header_ != nullptr;
    }

    void* GetSymbolAddress(std::string_view name,
                           bool warnIfMissing = true,
                           bool matchPrefix = false) const;

    bool HasSymbol(std::string_view name) const {
        return GetSymbolAddress(name, false, false) != nullptr;
    }

    ~ElfImage();

private:
    bool OpenAbsolute(const char* path, bool warnIfNonexist, bool warnIfSymtabNotFound);
    bool OpenRelative(const char* elf, bool warnIfNonexist, bool warnIfSymtabNotFound);
    void ParseMemory(ElfEhdr* header, bool isDebugData);
    void ParseDebugData(uint8_t* debugData, size_t size);
    void* GetModuleBase(const char* name) const;
    ElfAddr Lookup(std::string_view name, bool matchPrefix) const;

#ifdef __LP64__
    static constexpr const char* kSystemLibDir = "/system/lib64/";
    static constexpr const char* kApexRuntimeLibDir = "/apex/com.android.runtime/lib64/";
    static constexpr const char* kApexArtLibDir = "/apex/com.android.art/lib64/";
#else
    static constexpr const char* kSystemLibDir = "/system/lib/";
    static constexpr const char* kApexRuntimeLibDir = "/apex/com.android.runtime/lib/";
    static constexpr const char* kApexArtLibDir = "/apex/com.android.art/lib/";
#endif

    int androidVersion_;
    const char* elfName_ = nullptr;
    void* base_ = nullptr;
    off_t size_ = 0;
    off_t bias_ = -4396;
    ElfEhdr* header_ = nullptr;

    ElfSym* dynsym_ = nullptr;
    ElfOff dynsymCount_ = 0;
    const char* dynstr_ = nullptr;

    ElfSym* symtab_ = nullptr;
    ElfOff symtabCount_ = 0;
    ElfAddr strtab_ = 0;

    std::vector<uint8_t> debugData_;
    ElfSym* symtabFromDebugData_ = nullptr;
    ElfOff symtabCountFromDebugData_ = 0;
    ElfAddr strtabFromDebugData_ = 0;
};

}  // namespace yare

#endif
