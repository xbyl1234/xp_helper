#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string>
#include <dlfcn.h>
#include <execinfo.h>

using namespace std;

#include "third/dobby/include/dobby.h"
#include "third/utils/utils.h"
#include "third/utils/log.h"
#include "third/utils/jni_helper.hpp"
#include "third/utils/linux_helper.h"

//string LogAddr(void *addr) {
//    Dl_info info;
//    if (dladdr(addr, &info) != 0) {
//        return format_string("module: %s, base: %p, addr: %p, offset: %p",
//                             info.dli_fname,
//                             info.dli_fbase,
//                             addr,
//                             (char *) addr - (char *) info.dli_fbase);
//    } else {
//        return format_string("addr: %p, errno: %d", addr, errno);
//    }
//}
//
////int is_socket(int fd) {
////    int flags = fcntl(fd, F_GETFL, 0);
////    if (flags == -1) {
////        // 处理错误
////        return -1;
////    }
////    return (flags & O_NONBLOCK) == O_NONBLOCK;
////}
//
//int is_socket(int fd) {
//    int type;
//    socklen_t len = sizeof(type);
//    if (getsockopt(fd, SOL_SOCKET, SO_TYPE, &type, &len) == -1) {
//        // 处理错误
//        return false;
//    }
//    return type == SOCK_STREAM || type == SOCK_DGRAM;
//}
//
//string LogSockAddr(const struct sockaddr_in *addr) {
//    if (addr == nullptr) {
//        return "socket: is null";
//    }
//    char ip_address[INET_ADDRSTRLEN]{};
//    inet_ntop(addr->sin_family, &(addr->sin_addr), ip_address, INET_ADDRSTRLEN);
//    return format_string("socket: %s:%d", ip_address, ntohs(addr->sin_port));
//}
//
//string LogFd(int fd) {
//    if (is_socket(fd)) {
//        sockaddr_in addr{};
//        socklen_t addrlen = sizeof(struct sockaddr_in);
//        if (getpeername(fd, (struct sockaddr *) &addr, &addrlen) != 0) {
//            return format_string("fd: %d, errno: %d", fd, errno);
//        }
//        return "fd: " + to_string(fd) + ", " + LogSockAddr(&addr);
//    } else {
//        char path[PATH_MAX]{};
//        ssize_t len = readlink(("/proc/self/fd/" + to_string(fd)).c_str(),
//                               path, sizeof(path) - 1);
//        return format_string("fd: %d, file: %s", fd, path);
//    }
//}
//
//int (*pconnect)(int fd, const struct sockaddr_in *addr, socklen_t addr_length);
//
//int Hook_connect(int fd, const struct sockaddr_in *addr, socklen_t addr_length) {
//    logi("pid: %d, connect: %s,fd: %d, %s", getpid(), LogSockAddr(addr).c_str(), fd,
//         LogAddr(__builtin_return_address(0)).c_str());
//    return pconnect(fd, addr, addr_length);
//}
//
//ssize_t (*psend)(int socket, const void *const buf, size_t len, int flags);
//
//ssize_t Hook_send(int socket, const void *const buf, size_t len, int flags) {
//    logi("pid: %d, send: %s, %s", getpid(), LogFd(socket).c_str(),
//         LogAddr(__builtin_return_address(0)).c_str());
//    return psend(socket, buf, len, flags);
//}
//
//ssize_t (*_pwrite)(int fd, const void *const buf, size_t count);
//
//ssize_t Hook_write(int fd, const void *const buf, size_t count) {
//    logi("pid: %d, write: %s, %s", getpid(), LogFd(fd).c_str(),
//         LogAddr(__builtin_return_address(0)).c_str());
//    return _pwrite(fd, buf, count);
//}
//
//ssize_t
//(*psendto)(int fd, const void *const buf, size_t len, int flags, const struct sockaddr *dest_addr,
//           socklen_t addr_len);
//
//ssize_t
//Hook_sendto(int fd, const void *const buf, size_t len, int flags, const struct sockaddr *dest_addr,
//            socklen_t addr_len) {
//    char *buff = new char[len * 2 + 1];
//    hex2str((char *) buf, len, buff, len * 2 + 1);
//    logi("pid: %d, sendto: %s, %s, len: %d, data: %s",
//         getpid(),
//         LogFd(fd).c_str(),
//         LogAddr(__builtin_return_address(0)).c_str(),
//         LogAddr(__builtin_return_address(0)).c_str(),
//         LogAddr(__builtin_return_address(0)).c_str(),
//         LogAddr(__builtin_return_address(0)).c_str(),
//         LogAddr(__builtin_return_address(0)).c_str(),
//         len,
//         buff);
//    return psendto(fd, buf, len, flags, dest_addr, addr_len);
//}
//
//
//void checkMemory(const void *ptr, int len) {
//    char buff[] = {0x78, 0xda};
//    bool find = false;
////    if (memmem(ptr, len, buff, 2)) {
////        find = true;
////    }
////    else
//    if (memmem(ptr, len, "appbase_report_log", sizeof("appbase_report_log"))) {
//        find = true;
//    }
//    if (find) {
//        char *hexBuff = new char[len * 2 + 1];
//        hex2str((char *) ptr, len, hexBuff, len * 2 + 1);
//        logi("pid: %d, free: %s, len: %d, data: %s", getpid(),
//             LogAddr(__builtin_return_address(0)).c_str(), len, hexBuff);
//    }
//}
//
//void (*pfree)(void *ptr);
//
//void Hook_free(void *ptr) {
//    int len = malloc_usable_size(ptr);
//    checkMemory(ptr, len);
//    pfree(ptr);
//}
//
//void *(*pmemcpy)(void *const dst, const void *src, size_t copy_amount);
//
//void *Hook_memcpy(void *const dst, const void *src, size_t copy_amount) {
//    checkMemory(src, copy_amount);
//    return pmemcpy(dst, src, copy_amount);
//}
//
//size_t (*pstrlen)(const char *const s);
//
//size_t Hook_strlen(const char *const s) {
//    int len = pstrlen(s);
//    checkMemory(s, len);
//    return len;
//}
//
//int (*pSSL_write)(void *ssl, void *buf, int num);
//
//int Hook_SSL_write(void *ssl, void *buf, int num) {
//    logi("SSL write %s", buf);
//    checkMemory(buf, num);
//    return pSSL_write(ssl, buf, num);
//}
//
//
//int (*pdes)(void *ssl, void *buf, int num);
//
//int Hook_des(void *ssl, void *buf, int num) {
//    logi("des %p %p %p", ssl, buf, num);
//    return pdes(ssl, buf, num);
//}
//
//
//void HookUcBHook() {
//    vector<MapsInfo> info;
//    get_process_maps("libuc_bhook_jni.so", info, "r-xp");
//    if (info.size() == 0) {
//        logi("not find target library module!");
//        return;
//    }
//    pdes = (int (*)(void *, void *, int)) ((char *) info[0].region_start + 0x028FAD4);
//    DobbyHook((void *) pdes,
//              (dobby_dummy_func_t) Hook_des, (dobby_dummy_func_t *) &pdes);
//
//
////    DobbyHook(DobbySymbolResolver("libssl.so", "SSL_write"),
////              (dobby_dummy_func_t) Hook_SSL_write, (dobby_dummy_func_t *) &pSSL_write);
//}
//
//static bool hadHook = false;
//using func_hook_dlopen = void *(*)(const char *filename, int flag);
//static func_hook_dlopen phook_dlopen;
//
//__attribute__((__weak__))
//void *hook_dlopen(const char *filename, int flag) {
//    void *ret = phook_dlopen(filename, flag);
//    if (!hadHook && strstr(filename, "libuc_bhook_jni.so")) {
//        logi("dlopen load target library finish!");
//        hadHook = true;
//        HookUcBHook();
//    } else {
//        logi("dlopen load library %s", filename);
//    }
//    return ret;
//}
//
//using func_hook_android_dlopen_ext = void *(*)(const char *filename, int flag, const void *extinfo);
//static func_hook_android_dlopen_ext phook_android_dlopen_ext;
//
//__attribute__((__weak__))
//void *hook_android_dlopen_ext(const char *filename, int flag, const void *extinfo) {
//    void *ret = phook_android_dlopen_ext(filename, flag, extinfo);
//    if (!hadHook && strstr(filename, "libunet.so")) {
//        logi("android_dlopen load target library finish!");
//        hadHook = true;
//        HookUcBHook();
//    } else {
//        logi("android_dlopen load library %s", filename);
//    }
//    return ret;
//}
//
//
//jclass logClass;
//jmethodID xposedLog;
//JNIEnv *env;
//JavaVM *jvm;
//
//void XposedLogFunc(const lstring &msg) {
//    JNIEnv *env;
//    jvm->AttachCurrentThread(&env, NULL);
//    lsplant::JNI_CallStaticVoidMethod(env, logClass, xposedLog, string2jstring(env, msg));
////    jvm->DetachCurrentThread();
//}
//
//JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
//    jvm = vm;
//    vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6);
//    logClass = lsplant::JNI_NewGlobalRef(env, lsplant::JNI_FindClass(env, "com/common/log"));
//    xposedLog = lsplant::JNI_GetStaticMethodID(env, logClass,
//                                               "xposed",
//                                               "(Ljava/lang/String;)V");
//    xbyl::log::set_custom_func(XposedLogFunc);
//    xbyl::log::init_log("", xbyl::log_adapt::use_custom_func);
//
//    logi("fake device JNI_OnLoad");
//
////    DobbyHook(DobbySymbolResolver("libc.so", "memcpy"),
////              (dobby_dummy_func_t) Hook_memcpy, (dobby_dummy_func_t *) &pmemcpy);
////    DobbyHook(DobbySymbolResolver("libc.so", "strlen"),
////              (dobby_dummy_func_t) Hook_strlen, (dobby_dummy_func_t *) &pstrlen);
////    DobbyHook(DobbySymbolResolver("libc.so", "free"),
////              (dobby_dummy_func_t) Hook_free, (dobby_dummy_func_t *) &pfree);
////    DobbyHook(DobbySymbolResolver("libc.so", "connect"),
////              (dobby_dummy_func_t) Hook_connect, (dobby_dummy_func_t *) &pconnect);
////    DobbyHook(DobbySymbolResolver("libc.so", "send"),
////              (dobby_dummy_func_t) Hook_send, (dobby_dummy_func_t *) &psend);
////    DobbyHook(DobbySymbolResolver("libc.so", "write"),
////              (dobby_dummy_func_t) Hook_write, (dobby_dummy_func_t *) &_pwrite);
//    DobbyHook(DobbySymbolResolver("libc.so", "sendto"),
//              (dobby_dummy_func_t) Hook_sendto, (dobby_dummy_func_t *) &psendto);
////    DobbyHook(DobbySymbolResolver("libdl.so", "dlopen"), (dobby_dummy_func_t) hook_dlopen,
////              (dobby_dummy_func_t *) &phook_dlopen);
////    DobbyHook(DobbySymbolResolver("linker64", "android_dlopen_ext"),
////              (dobby_dummy_func_t) hook_android_dlopen_ext,
////              (dobby_dummy_func_t *) &phook_android_dlopen_ext);
//    return JNI_VERSION_1_6;
//}
//
//extern "C"
//JNIEXPORT jboolean JNICALL
//Java_com_hook_MainHook_InitLib(JNIEnv *env, jobject thiz, jstring process_name,
//                               jstring log_file_path) {
//    return true;
//}