package com;

import com.common.units;

public class NativeHelper {
    public static boolean LoadLib() {
        try {
            System.loadLibrary("xphelper");
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public static boolean CopyLib2Data(String nativeLibraryDir) {
        try {
            units.RunShell("cp " + nativeLibraryDir + "/libxphelper.so" + " /data/libxphelper.so");
            units.RunShell("chmod 777 /data/libxphelper.so");
            units.RunShell("chcon u:object_r:system_lib_file:s0 /data/libxphelper.so");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
