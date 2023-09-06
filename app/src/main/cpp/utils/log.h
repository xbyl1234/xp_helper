#pragma once

#include <cstdarg>
#include <cstdio>
#include <mutex>
#include <time.h>
#include <string>
#include <stdio.h>
#include <iomanip>
#include <sstream>
#include "utils.h"

#define ANDROID_SYSTEM

#ifdef ANDROID_SYSTEM
#define android_log_print __android_log_print

#include <android/log.h>
#include <jni.h>

#else
#define android_log_print(x) while(0){};
#endif

#ifdef UNICODE

using std::wstring;

#ifndef lstring
#define lstring wstring
#endif

#ifndef TCHAR
#define TCHAR wchar_t
#endif // !1

#ifndef _T
#define _T(x) L##x
#endif

#ifndef  lstrlen
#define lstrlen wcslen
#endif

#ifndef lstrftime
#define lstrftime wcsftime
#endif

#ifndef lstrncpy
#define lstrncpy wcsncpy
#endif

#ifndef lvsnprintf
#define lvsnprintf _vsnwprintf
#endif

#ifndef lto_string
#define lto_string to_wstring
#endif

#ifndef lprintf
#define lprintf wprintf
#endif

#else
using std::string;

#ifndef lstring
#define lstring string
#endif


#ifndef TCHAR
#define TCHAR char
#endif // !1

#ifndef _T
#define _T(x) x
#endif

#ifndef  lstrlen
#define lstrlen strlen
#endif

#ifndef lstrftime
#define lstrftime strftime
#endif

#ifndef lstrncpy
#define lstrncpy strncpy
#endif

#ifndef lvsnprintf
#define lvsnprintf vsnprintf
#endif

#ifndef lto_string
#define lto_string to_string
#endif

#ifndef lprintf
#define lprintf printf
#endif

#endif

#if defined(__arm64__) || defined(__aarch64__)
#define XbylLogTagArch  "Arm64"
#elif defined(__arm__ )
#define XbylLogTagArch  "Arm32"
#elif defined(__i386__ )
#define XbylLogTagArch  "X86_32"
#elif defined(__x86_64__ )
#define XbylLogTagArch  "X86_64"
#endif

#define logd(fmt, ...) xbyl::log::_logd(fmt, ##__VA_ARGS__)
#define logi(fmt, ...) xbyl::log::_logi(fmt, ##__VA_ARGS__)
#define logw(fmt, ...) xbyl::log::_logw(fmt, ##__VA_ARGS__)
#define loge(fmt, ...) xbyl::log::_loge(fmt, ##__VA_ARGS__)

#define XbylLogTag "xphelper"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, XbylLogTag, __VA_ARGS__)

namespace xbyl {


    using func_log = void (*)(const lstring &msg);

    using std::mutex;

    enum class log_adapt {
        use_file,
        use_printf,
        use_custom_func,
        use_adb,
        use_none,
        use_xp
    };

    enum class log_level {
        debug,
        info,
        warn,
        error
    };

    class log {
        static const int buffer_size = 1024;
        static FILE *log_file;
        static std::mutex lock;
        static log_adapt adapt;
        static lstring level_string[4];
        static func_log flog;
        static lstring log_file_path;
        static lstring tag;

    private:
        log() {}

    public:
        static bool init_log(const lstring &tag, log_adapt adapt, const lstring &file_path = "");
        static void setTag(const lstring &tag );

        ~log() {
            close_file();
        }

        static void close_file() {
            if (log_file) {
                fclose(log_file);
            }
            log_file = nullptr;
        }

        static bool set_adapt(log_adapt adapt, const lstring &file_path = "") {
            log::adapt = adapt;
            log::log_file_path = file_path;
            if (adapt == log_adapt::use_file) {
                log_file = fopen(file_path.c_str(), "w+");
                if (log_file == nullptr) {
                    return false;
                }
            } else {
                close_file();
            }
            return true;
        }

        static void set_custom_func(func_log flog) {
            log::flog = flog;
        }

        //__FILE__, __LINE__

        static void _logd(const TCHAR *msg, ...);

        static void _logi(const TCHAR *msg, ...);

        static void _logw(const TCHAR *msg, ...);

        static void _loge(const TCHAR *msg, ...);

        static string format_log(log_level level, const lstring &fmt, va_list va);

        static void output_log(int level, const lstring &buf);

        static void log2file(const lstring &msg);
    };
}


//#undef lprintf
//#undef lto_string
//#undef lvsnprintf
//#undef lstrncpy
//#undef lstrftime
//#undef lstrlen
//#undef _T
//#undef TCHAR
//#undef lstring