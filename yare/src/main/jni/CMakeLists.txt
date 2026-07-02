cmake_minimum_required(VERSION 3.28)
project(yare)

find_program(CCACHE ccache)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

if (CCACHE)
    set(CMAKE_CXX_COMPILER_LAUNCHER ${CCACHE})
    set(CMAKE_C_COMPILER_LAUNCHER ${CCACHE})
endif ()

add_library(${PROJECT_NAME} SHARED
    yare.cpp
)

target_link_libraries(${PROJECT_NAME}
    log
)
