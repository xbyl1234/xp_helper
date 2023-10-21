#include <jni.h>
#include "third/utils/linux_helper.h"
#include "third/utils/log.h"

void pass_root() {
    MapsHelper maps;
    int count = maps.refresh("\\[stack\\]");
    if (count != 1) {
        loge("get memory [stack] error count: %d", count);
        return;
    }
    logi("find [stack] base: %p", maps.mapsInfo[0].region_start);
}