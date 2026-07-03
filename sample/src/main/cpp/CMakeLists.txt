cmake_minimum_required(VERSION 3.28)

project(examples)

add_library(examples SHARED examples.cpp)

find_library(log-lib log)

target_link_libraries(examples ${log-lib})
