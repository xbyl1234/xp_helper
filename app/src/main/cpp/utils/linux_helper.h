#pragma once

#include <vector>
#include <string>
#include <sys/stat.h>
#include <sys/statvfs.h>


int pid_by_process_name(char *task_name);

std::string Fd2Path(int fd);

bool Mount(const std::string &fakePath, const std::string &targetPath);

bool UnMount(const std::string &targetPath);

std::string GetCmdLine(pid_t pid);

std::string RunCmd(const std::string &strCmd);

struct MountInfo {
    std::string mnt_fsname;
    std::string mnt_dir;
    std::string mnt_type;
    std::string mnt_opts;
    int mnt_freq;
    int mnt_passno;
};

bool get_mount_list(const char *path, std::vector<MountInfo> &ret);

struct MapsInfo {
    void *region_start;
    void *region_end;
    void *region_offset;
    std::string permissions;
    std::string path;
};

bool get_process_maps(const std::string &libPath, std::vector<MapsInfo> &mapsInfo,
                      const std::string &wantPerm = "");

bool copy_file(const std::string &from, const std::string &to);

struct FilePerm {
    std::string ctx;
    mode_t st_mode;
    gid_t st_gid;
    uid_t st_uid;
};

bool
SetFilePerm(const std::string &path, const std::string &context, mode_t st_mode, gid_t st_gid,
            uid_t st_uid);

bool GetFilePerm(const std::string &path, FilePerm &perm);

bool GetFileSelinuxCtx(const std::string &path, std::string &ctx);

bool remove_dir(const std::string &dir_name);

unsigned long get_file_size(const std::string &path);

bool remove_files(const std::string &path);

bool UnMount2(const char *target, int flags);

bool UnMount(const char *target);

bool Mount(const char *source, const char *target, const char *fs_type, unsigned long flags,
           const void *data);

enum TraverseMark {
    _default,
    passDir,
};

bool
traverse_path(const std::string &path,
              std::function<TraverseMark(const std::string &rootPath, const std::string &fileName,
                                         int type, TraverseMark mark)> callback,
              TraverseMark mark = _default);