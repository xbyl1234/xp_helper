#include <jni.h>
#include "third/utils/log.h"
#include <regex>

using namespace std;

void pass_root();

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    xbyl::init_log("xp_helper");
    pass_root();
    return JNI_VERSION_1_6;
}