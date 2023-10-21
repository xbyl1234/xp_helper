cmake_minimum_required(VERSION 3.22.1)
project("xphelper")

find_library(log-lib log)

add_subdirectory(./third)


file(GLOB xphelper_src
        "*.h"
        "*.hpp"
        "*.cpp"
        "*.c"
        )

add_library(
        xphelper
        SHARED
        ${xphelper_src}
)

target_link_libraries(
        xphelper
        dobby
        libutils
        ${log-lib}
)
