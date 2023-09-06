#include <dirent.h>
#include <cstdio>
#include <cstring>
#include <string>
#include <sys/param.h>
#include <unistd.h>
#include <sys/mount.h>
#include <vector>
#include <sys/stat.h>

#include "log.h"
#include "linux_helper.h"

using namespace std;

#define BUF_SIZE 1024

int pid_by_process_name(char *task_name) {
    DIR *dir;
    dirent *ptr;
    FILE *fp;
    char filepath[50];
    char cur_task_name[50];
    char buf[BUF_SIZE];
    pid_t pid = 0;

    dir = opendir("/proc");
    if (NULL != dir) {
        while ((ptr = readdir(dir)) != NULL) {
            if ((strcmp(ptr->d_name, ".") == 0) || (strcmp(ptr->d_name, "..") == 0))
                continue;
            if (DT_DIR != ptr->d_type)
                continue;
            sprintf(filepath, "/proc/%s/status", ptr->d_name);
            fp = fopen(filepath, "r");
            if (NULL != fp) {
                if (fgets(buf, BUF_SIZE - 1, fp) == NULL) {
                    fclose(fp);
                    continue;
                }
                sscanf(buf, "%*s %s", cur_task_name);
                if (!strcmp(task_name, cur_task_name)) {
                    sscanf(ptr->d_name, "%d", &pid);
                    fclose(fp);
                    break;
                }
                fclose(fp);
            }
        }
        closedir(dir);
    }
    return pid;
}

//string process_name_by_pid(pid_t pid) {
//    char proc_pid_path[BUF_SIZE];
//    char buf[BUF_SIZE];
//
//    sprintf(proc_pid_path, "/proc/%d/status", pid);
//    FILE *fp = fopen(proc_pid_path, "r");
//    if (NULL != fp) {
//        if (fgets(buf, BUF_SIZE - 1, fp) == NULL) {
//            fclose(fp);
//        }
//        fclose(fp);
//        sscanf(buf, "%*s %s", task_name);
//    }
//}

bool Mount(const string &fakePath, const string &targetPath) {
    logi("mount %s to %s", fakePath.c_str(), targetPath.c_str());
    return TEMP_FAILURE_RETRY(mount(fakePath.c_str(), targetPath.c_str(), nullptr,
                                    MS_BIND | MS_REC, nullptr)) != -1;
}

bool UnMount(const string &targetPath) {
    logi("umount %s", targetPath.c_str());
    return umount(targetPath.c_str()) == 0;
}

bool Mount(const char *source, const char *target, const char *fs_type, unsigned long flags,
           const void *data) {
    logi("mount %s to %s", source, target);
    return TEMP_FAILURE_RETRY(mount(source, target, fs_type, flags, data)) != -1;
}

bool UnMount(const char *target) {
    logi("umount %s", target);
    return umount(target) == 0;
}

bool UnMount2(const char *target, int flags) {
    logi("umount2 %s", target);
    return umount2(target, flags) == 0;
}

string Fd2Path(int fd) {
    char filePath[MAXPATHLEN];
    string fdPath = "/proc/self/fd/" + to_string(fd);
    int rdLen = readlink(fdPath.c_str(), filePath, MAXPATHLEN);
    if (rdLen == -1) {
        loge("readlink %s error: %d", fdPath.c_str(), errno);
        return "";
    }
    filePath[rdLen] = 0;
    return filePath;
}

string GetCmdLine(pid_t pid) {
    char buff[256]{};
    sprintf(buff, "/proc/%d/cmdline", pid);
    FILE *file = fopen(buff, "r");
    if (file == nullptr) {
        return "";
    }
    if (!fgets(buff, 255, file)) {
        fclose(file);
        return "";
    }
    fclose(file);
    return string(buff);
}

string RunCmd(const string &strCmd) {
    char buf[1024] = {0};
    FILE *pf = NULL;
    if ((pf = popen(strCmd.c_str(), "r")) == NULL) {
        return "cant popen file " + strCmd;
    }
    string strResult;
    while (fgets(buf, sizeof buf, pf)) {
        strResult += buf;
    }
    pclose(pf);
    return strResult;
}

bool get_mount_list(const char *path, vector<MountInfo> &ret) {
    if (path == nullptr) {
        path = "/proc/mounts";
    }
    FILE *fp = fopen(path, "r");
    if (!fp) {
        return false;
    }
    char buf[1024]{0};
    while (fgets(buf, sizeof(buf), fp) != NULL) {
        MountInfo info;
        int fsname0, fsname1, dir0, dir1, type0, type1, opts0, opts1;
        if (sscanf(buf, " %n%*s%n %n%*s%n %n%*s%n %n%*s%n %d %d",
                   &fsname0, &fsname1, &dir0, &dir1, &type0, &type1, &opts0, &opts1,
                   &info.mnt_freq, &info.mnt_passno) == 2) {
            buf[fsname1] = '\0';
            info.mnt_fsname = &buf[fsname0];
            buf[dir1] = '\0';
            info.mnt_dir = &buf[dir0];
            buf[type1] = '\0';
            info.mnt_type = &buf[type0];
            buf[opts1] = '\0';
            info.mnt_opts = &buf[opts0];
            ret.push_back(move(info));
        }
    }
    fclose(fp);
    return true;
}


#define LINE_MAX 2048

#ifdef __LP64__
#define __PRI_64_prefix "l"
#define __PRI_PTR_prefix "l"
#else
#define __PRI_64_prefix "ll"
#define __PRI_PTR_prefix
#endif
#define PRIxPTR __PRI_PTR_prefix "x" /* uintptr_t */

bool get_process_maps(const string &libPath, vector<MapsInfo> &mapsInfo,
                      const string &wantPerm) {
    FILE *fp = fopen("/proc/self/maps", "r");
    if (fp == nullptr)
        return false;

    while (!feof(fp)) {
        char line_buffer[LINE_MAX + 1];
        fgets(line_buffer, LINE_MAX, fp);

        // ignore the rest of characters
        if (strlen(line_buffer) == LINE_MAX && line_buffer[LINE_MAX] != '\n') {
            // Entry not describing executable data. Skip to end of line to set up
            // reading the next entry.
            int c;
            do {
                c = getc(fp);
            } while ((c != EOF) && (c != '\n'));
            if (c == EOF)
                break;
        }

        void *region_start;
        void *region_end;
        void *region_offset;
        char permissions[5] = {'\0'}; // Ensure NUL-terminated string.
        uint8_t dev_major = 0;
        uint8_t dev_minor = 0;
        long inode = 0;
        int path_index = 0;

        // Sample format from man 5 proc:
        //
        // address           perms offset  dev   inode   pathname
        // 08048000-08056000 r-xp 00000000 03:0c 64593   /usr/sbin/gpm
        //
        // The final %n term captures the offset in the input string, which is used
        // to determine the path name. It *does not* increment the return value.
        // Refer to man 3 sscanf for details.
        if (sscanf(line_buffer,
                   "%" PRIxPTR "-%" PRIxPTR " %4c "
                   "%" PRIxPTR " %hhx:%hhx %ld %n",
                   &region_start, &region_end, permissions, &region_offset, &dev_major, &dev_minor,
                   &inode,
                   &path_index) < 7) {
            fclose(fp);
            return false;
        }

        string path = line_buffer + path_index;

        if (path.find(libPath) != -1 &&
            (wantPerm.length() == 0 || (wantPerm.length() != 0 && wantPerm == permissions))) {
            MapsInfo info;
            info.region_start = region_start;
            info.region_end = region_end;
            info.region_offset = region_offset;
            info.permissions = permissions;
            info.path = line_buffer + path_index;
            mapsInfo.push_back(move(info));
        }
    }
    fclose(fp);

    return mapsInfo.size() != 0;
}

bool copy_file(const string &from, const string &to) {
    FILE *in, *out;
    in = fopen(from.c_str(), "rb");
    if (in == nullptr) {
        loge("copy file can not open %s, error: %d", from.c_str(), errno);
        return false;
    }
    out = fopen(to.c_str(), "wb");
    if (out == nullptr) {
        loge("copy file can not open %s, error: %d", to.c_str(), errno);
        fclose(in);
        return false;
    }

    char buff[4096];
    size_t len = 0;
    while ((len = fread(buff, 1, sizeof(buff), in))) {
        fwrite(buff, 1, len, out);
    }

    fclose(in);
    fclose(out);
    return true;
}

bool remove_files(const string &path) {
    struct stat s_buf;
    if (stat(path.c_str(), &s_buf) != 0) {
        return false;
    }
    if (!S_ISDIR(s_buf.st_mode)) {
        return unlink(path.c_str()) == 0;
    } else {
        return remove_dir(path);
    }
}

bool remove_dir(const string &dir_name) {
    DIR *dir;
    struct dirent *entry;

    if (!(dir = opendir(dir_name.c_str())))
        return false;
    while ((entry = readdir(dir)) != NULL) {
        if (entry->d_type == DT_DIR) {
            if (strcmp(entry->d_name, ".") == 0 ||
                strcmp(entry->d_name, "..") == 0)
                continue;
            string path = dir_name + "/" + entry->d_name;
            if (!remove_dir(path)) {
                return false;
            }
        } else {
            string path = dir_name + "/" + entry->d_name;
            if (unlink(path.c_str()) != 0) {
                return false;
            }
        }
    }
    closedir(dir);
    return rmdir(dir_name.c_str()) == 0;
}

unsigned long get_file_size(const string &path) {
    struct stat buff;
    if (stat(path.c_str(), &buff) >= 0) {
        return buff.st_size;
    }
    return -1;
}

bool
traverse_path(const string &path,
              function<TraverseMark(const string &, const string &, int, TraverseMark)> callback,
              TraverseMark mark) {
    DIR *dir = opendir(path.c_str());
    if (dir == NULL) {
        loge("traverse_path opendir %s, error: %d", path.c_str(), errno);
        return false;
    }
    struct dirent *entry;
    while ((entry = readdir(dir)) != NULL) {
        if (entry->d_type == DT_DIR) {
            if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0) {
                continue;
            }
            mark = callback(path, entry->d_name, entry->d_type, mark);
            if (mark != passDir) {
                traverse_path(path + "/" + entry->d_name, callback, mark);
            }
        } else {
            callback(path, entry->d_name, entry->d_type, mark);
        }
    }
    closedir(dir);
    return true;
}