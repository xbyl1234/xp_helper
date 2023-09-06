#include <fcntl.h>
#include <unistd.h>
#include <sstream>
#include <iomanip>
#include <cstdlib>
#include <cstdio>
#include <ctime>
#include <fstream>
#include <string.h>
#include <sys/time.h>
#include <time.h>

#include "utils.h"

using std::stringstream;

string mid_string(const string &src, const string &start, const string &end) {
    int p = src.find(start);
    if (p == -1) {
        return "";
    }
    int pe = src.find(end, p + start.size());
    if (pe == -1) {
        return "";
    }
    return src.substr(p + start.size(), pe - p - start.size());
}

string get_uuid() {
    char uuid[37] = {0};
    int fd = open("/proc/sys/kernel/random/uuid", O_RDONLY);
    if (fd >= 0) {
        read(fd, uuid + 1, 36);
    }
    close(fd);
    return uuid;
}

int64_t get_time() {
    time_t timep;
    time(&timep);
    return timep;
}

string time_to_string(int64_t tick) {
    tm tm{};
#ifdef _WIN32
    localtime_s(&tm, &tick);
#else
    localtime_r((time_t *) &tick, &tm);
#endif

    std::stringstream stm;
    stm << std::put_time(&tm, "%Y-%m-%d %H:%M:%S");
    return stm.str();
}

int64_t string_to_time(const string &time_str, const string &fmt = "%Y-%m-%d %H:%M:%S") {
    tm tm{};
    std::stringstream stm;
    stm << time_str;
    stm >> std::get_time(&tm, "%Y-%m-%d %H:%M:%S");
    return mktime(&tm);
}

string format_string(const string &fmt, ...) {
    string buffer;
    buffer.resize(1024);
    va_list va;
    va_start(va, fmt);
    vsnprintf((char *) buffer.c_str(), buffer.size(), fmt.c_str(), va);
    va_end(va);
    return std::move(buffer);
}

bool WritFile(const string &path, const char *buf, int len) {
    std::fstream file(path.c_str(), std::ios::out | std::ios::binary);
    if (!file.is_open()) {
        return false;
    }
    file.write(buf, len);
    file.flush();
    file.close();
    return true;
}

char *hex2str(const char *hex, int hex_len, char *str, int buf_len) {
    const char *cHex = "0123456789ABCDEF";
    int i = 0;
    if (hex_len * 2 >= buf_len) {
        hex_len = buf_len / 2 - 1;
    }
    if (hex_len <= 0) {
        str[0] = 0;
        return nullptr;
    }
    for (int j = 0; j < hex_len; j++) {
        auto a = (unsigned int) hex[j];
        str[i++] = cHex[(a & 0xf0) >> 4];
        str[i++] = cHex[(a & 0x0f)];
//        if ((j + 1) % 16 == 0) {
//            str[i++] = '\n';
//        } else {
//            str[i++] = ' ';
//        }
    }
    str[i] = '\0';
    return str;
}

int hex2int(char c) {
    if (c >= '0' && c <= '9') {
        return (unsigned int) (c - 48);
    } else if (c >= 'A' && c <= 'B') {
        return (unsigned int) (c - 65);
    } else if (c >= 'a' && c <= 'b') {
        return (unsigned int) (c - 97);
    } else {
        return 0;
    }
}

char *str2hex(const char *str, int str_len, char *hex, int buf_len) {
    int i = 0;
    if (str_len / 2 > buf_len) {
        str_len = buf_len * 2;
    }
    for (int j = 0; j < str_len - 1;) {
        unsigned int a = hex2int(str[j++]);
        unsigned int b = hex2int(str[j++]);
        hex[i++] = char(a * 16 + b);
    }
    return hex;
}

//字符串分割函数
vector<string> string_split(const string &str, const string &pattern) {
    string::size_type pos;
    vector<string> result;
    int size = str.size();

    for (int i = 0; i < size; i++) {
        pos = str.find(pattern, i);
        if (pos == string::npos) {
            string s = str.substr(i);
            result.push_back(s);
            break;
        }
        if (pos < size) {
            string s = str.substr(i, pos - i);
            result.push_back(s);
            i = pos + pattern.size() - 1;
        }
    }
    return result;
}

//vector<string> string_split(const string &str, const string &pattern) {
//    vector<string> ret;
//    if (pattern.empty()) return ret;
//    size_t start = 0, index = str.find_first_of(pattern, 0);
//    while (index != std::string::npos) {
//        if (start != index)
//            ret.push_back(str.substr(start, index - start));
//        start = index + 1;
//        index = str.find_first_of(pattern, start);
//    }
//    if (!str.substr(start).empty())
//        ret.push_back(str.substr(start));
//    return ret;
//}

string replace_all(const string &str, const string &old_value, const string &new_value) {
    string cp = str;
    while (true) {
        string::size_type pos(0);
        if ((pos = cp.find(old_value)) != string::npos)
            cp.replace(pos, old_value.length(), new_value);
        else break;
    }
    return cp;
}

struct AutoInitRand {
    AutoInitRand() {
        srand(time(NULL));
    }
} _;

int gen_number(int min, int max) {
    return (rand() % (max - min + 1)) + min;
}

float gen_double(float min, float max) {
    return (rand() / (float) RAND_MAX) * (max - min) + min;
}


const char *CharSet_123 = "0123456789";
const char *CharSet_ABC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
const char *CharSet_abc = "abcdefghijklmnopqrstuvwxyz";
const char *CharSet_hex = "0123456789abcdef";
const char *CharSet_ABC123 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
const char *CharSet_all = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";


string gen_str(const char *charSet, int maxIdx, int len) {
    string buff;
    buff.reserve(len + 1);
    for (int i = 0; i < len; ++i) {
        buff += charSet[gen_number(0, maxIdx)];
    }
    return buff;
}

string gen_strABC123(int len) {
    return gen_str(CharSet_ABC123, 35, len);
}

string gen_hexstr(int len) {
    return gen_str(CharSet_hex, 15, len);
}

string gen_strabc(int len) {
    return gen_str(CharSet_abc, 25, len);
}

string gen_strABC(int len) {
    return gen_str(CharSet_ABC, 25, len);
}

string gen_str123(int len) {
    return gen_str(CharSet_123, 9, len);
}

string gen_strall(int len) {
    return gen_str(CharSet_all, 61, len);
}

string gen_str(const char *charSet, int len) {
    return gen_str(charSet, strlen(charSet) - 1, len);
}

string gen_uuid() {
    return gen_hexstr(8) + "-" +
           gen_hexstr(4) + "-" +
           gen_hexstr(4) + "-" +
           gen_hexstr(4) + "-" +
           gen_hexstr(12);
}

string to_lower(const string &str) {
    string cp = str;
    transform(cp.begin(), cp.end(), cp.begin(),
              [](unsigned char c) {
                  return std::tolower(c);
              });
    return cp;
}

string to_upper(const string &str) {
    string cp = str;
    transform(cp.begin(), cp.end(), cp.begin(),
              [](unsigned char c) {
                  return std::toupper(c);
              });
    return cp;
}


bool ReadFile(const string &path, char **data, int *len) {
    if (access(path.c_str(), R_OK) != 0) {
//        loge("read file %s no read permission", path.c_str());
        return false;
    }
    std::ifstream ifs(path, std::ios::binary);
    if (!ifs.is_open()) {
//        loge("read file %s open failed", path.c_str());
        return false;
    }
    ifs.seekg(0, ifs.end);
    auto fos = ifs.tellg();
    ifs.seekg(0, ifs.beg);
    char *buff = new char[fos];
    ifs.read(buff, fos);
    ifs.close();
    *data = buff;
    *len = fos;
    return true;
}

string ReadFile(const string &path) {
    if (access(path.c_str(), R_OK) != 0) {
//        loge("read file %s no read permission", path.c_str());
        return "";
    }
    std::ifstream ifs(path);
    if (!ifs.is_open()) {
//        loge("read file %s open failed", path.c_str());
        return "";
    }
    stringstream buffer;
    buffer << ifs.rdbuf();
    ifs.close();
    return buffer.str();
}


void StringAppendV(std::string *dst, const char *format, va_list ap) {
    // First try with a small fixed size buffer
    char space[1024];

    // It's possible for methods that use a va_list to invalidate
    // the data in it upon use.  The fix is to make a copy
    // of the structure before using it and use that copy instead.
    va_list backup_ap;
    va_copy(backup_ap, ap);
    int result = vsnprintf(space, sizeof(space), format, backup_ap);
    va_end(backup_ap);

    if (result < static_cast<int>(sizeof(space))) {
        if (result >= 0) {
            // Normal case -- everything fit.
            dst->append(space, result);
            return;
        }

        if (result < 0) {
            // Just an error.
            return;
        }
    }

    // Increase the buffer size to the size requested by vsnprintf,
    // plus one for the closing \0.
    int length = result + 1;
    char *buf = new char[length];

    // Restore the va_list before we use it again
    va_copy(backup_ap, ap);
    result = vsnprintf(buf, length, format, backup_ap);
    va_end(backup_ap);

    if (result >= 0 && result < length) {
        // It fit
        dst->append(buf, result);
    }
    delete[] buf;
}

std::string StringPrintf(const char *fmt, ...) {
    va_list ap;
    va_start(ap, fmt);
    std::string result;
    StringAppendV(&result, fmt, ap);
    va_end(ap);
    return result;
}

long get_system_time_nanosecond() {
    struct timespec timestamp = {};
    if (0 == clock_gettime(CLOCK_REALTIME, &timestamp))
        return timestamp.tv_sec * 1000000000 + timestamp.tv_nsec;
    else
        return 0;
}

long get_system_time_microsecond() {
    struct timeval timestamp = {};
    if (0 == gettimeofday(&timestamp, NULL))
        return timestamp.tv_sec * 1000000 + timestamp.tv_usec;
    else
        return 0;
}

long get_system_time_millisecond() {
    struct timeval timestamp = {};
    if (0 == gettimeofday(&timestamp, NULL))
        return timestamp.tv_sec * 1000 + timestamp.tv_usec / 1000;
    else
        return 0;
}
