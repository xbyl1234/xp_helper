cmake_minimum_required(VERSION 3.22.1)
project("xphelper")


set(DobbyHome "./dobby")
#set(GENERATE_SHARED OFF)
#set(DOBBY_DEBUG OFF)
add_subdirectory(${DobbyHome} dobby)


file(GLOB xphelper_src
        "*.h"
        "*.hpp"
        "*.cpp"
        "*.c"
        "utils/*.cpp"
        "utils/*.hpp"
        "utils/*.h"
        )

add_library(
        xphelper
        SHARED
        ${xphelper_src}
)

target_link_libraries(
        xphelper
        dobby
        ${log-lib}
)
