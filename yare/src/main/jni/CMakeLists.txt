cmake_minimum_required(VERSION 3.28)
project(yare)

cmake_policy(SET CMP0079 NEW)

find_program(CCACHE ccache)

set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

set(C_FLAGS "-Wall -Wextra -fvisibility=hidden -fvisibility-inlines-hidden")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} ${C_FLAGS}")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${C_FLAGS}")

if (CCACHE)
    set(CMAKE_CXX_COMPILER_LAUNCHER ${CCACHE})
    set(CMAKE_C_COMPILER_LAUNCHER ${CCACHE})
endif ()

set(XZ_EMBEDDED_SOURCE
    external/xz-embedded/linux/lib/xz/xz_crc32.c
    external/xz-embedded/linux/lib/xz/xz_dec_lzma2.c
    external/xz-embedded/linux/lib/xz/xz_dec_stream.c
)

include_directories(
    external/xz-embedded/linux/include/linux
    external/xz-embedded/userspace
)

set(DOBBY_DEBUG OFF CACHE INTERNAL "" FORCE)
set(DOBBY_GENERATE_SHARED OFF CACHE INTERNAL "" FORCE)
set(Plugin.SymbolResolver OFF CACHE INTERNAL "" FORCE)
set(NearBranch OFF CACHE INTERNAL "" FORCE)
add_subdirectory(external/dobby dobby)
get_property(DOBBY_INCLUDE_DIRECTORIES
    TARGET dobby
    PROPERTY INCLUDE_DIRECTORIES
)
include_directories(
    ${DOBBY_INCLUDE_DIRECTORIES}
    $<TARGET_PROPERTY:dobby,INCLUDE_DIRECTORIES>
)

set(LSPLANT_JNI_DIR external/lsplant/lsplant/src/main/jni)

set(LSPLANT_BUILD_SHARED OFF CACHE INTERNAL "" FORCE)
add_subdirectory(${LSPLANT_JNI_DIR} lsplant)

add_library(${PROJECT_NAME} SHARED
    elf_image.cpp
    hidden_api.cpp
    profile_saver.cpp
    yare.cpp
    ${XZ_EMBEDDED_SOURCE}
)

target_include_directories(${PROJECT_NAME}
    PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}
    ${CMAKE_CURRENT_SOURCE_DIR}/${LSPLANT_JNI_DIR}/include
)

target_link_libraries(${PROJECT_NAME}
    log
    dobby
    lsplant_static
)
