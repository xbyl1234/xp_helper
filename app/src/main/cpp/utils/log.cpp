#include "log.h"

#include "jni_helper.hpp"

namespace xbyl {
    FILE *log::log_file;
    mutex log::lock;
    log_adapt log::adapt = log_adapt::use_adb;
    func_log log::flog;
    lstring log::tag;
    lstring log::log_file_path;
    lstring log::level_string[4]{
            _T("Debug"),
            _T("Info"),
            _T("Waring"),
            _T("Error"),
    };

    bool log::init_log(const string &_tag, log_adapt adapt, const string &file_path) {
        if (!set_adapt(adapt, file_path)) {
            return false;
        }
        setTag(_tag);

        return true;
    }

    void log::setTag(const lstring &_tag) {
        log::tag = XbylLogTag "-Native" XbylLogTagArch "-";
        log::tag += _tag;
    }

    void log::_logd(const TCHAR *msg, ...) {
        va_list va;
        va_start(va, msg);
        output_log(ANDROID_LOG_DEBUG, format_log(log_level::debug, msg, va));
        va_end(va);
    }

    void log::_logi(const TCHAR *msg, ...) {
        va_list va;
        va_start(va, msg);
        output_log(ANDROID_LOG_INFO, format_log(log_level::info, msg, va));
        va_end(va);
    }

    void log::_logw(const TCHAR *msg, ...) {
        va_list va;
        va_start(va, msg);
        output_log(ANDROID_LOG_WARN, format_log(log_level::warn, msg, va));
        va_end(va);
    }

    void log::_loge(const TCHAR *msg, ...) {
        va_list va;
        va_start(va, msg);
        output_log(ANDROID_LOG_ERROR, format_log(log_level::error, msg, va));
        va_end(va);
    }

    string log::format_log(log_level level, const lstring &fmt, va_list va) {
        lstring buffer;
        //buffer += time_to_string(get_time());
        //buffer += " ";
        //auto tid = std::this_thread::get_id();
        //lstring stid = std::lto_string((*(_Thrd_t*)(char*)&tid)._Id);
        //lstrncpy(&buf[pos], stid.c_str(), stid.length());
        //pos += stid.length();
        buffer += level_string[(int) level];
        buffer += "\t";
        buffer += tag;
        buffer += ":\t\t\t";

        int len = buffer.length();
        buffer.resize(len + buffer_size);
        lvsnprintf(&buffer[len], buffer_size, fmt.c_str(), va);

        lstrncpy(&buffer[lstrlen(buffer.c_str())], _T("\n\r\x00"), 3);
        return std::move(buffer);
    }

    void log::output_log(int level, const lstring &buf) {
        switch (adapt) {
            case log_adapt::use_none:
                return;
            case log_adapt::use_file:
                log2file(buf);
                break;
            case log_adapt::use_printf:
                lprintf("%s", buf.c_str());
                break;
            case log_adapt::use_custom_func:
                flog(buf);
                break;
            case log_adapt::use_adb:
                android_log_print(level, tag.c_str(), buf.c_str(), _T(""));
                break;
        }
    }

    void log::log2file(const lstring &msg) {
        lock.lock();
        fwrite(msg.c_str(), lstrlen(msg.c_str()), 1, log_file);
        fflush(log_file);
        lock.unlock();
    }
}