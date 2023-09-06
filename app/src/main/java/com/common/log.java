package com.common;


import android.util.Log;

import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;

public class log {
    static String Tag = "xp_helper";

    static class LogLevel {
        final static int Debug = 0;
        final static int Info = 1;
        final static int Waring = 2;
        final static int Error = 3;
        final static String LogTag[] = {
                "Debug",
                "Info",
                "Waring",
                "Error"
        };
    }

    public static void AdbLog(int level, String msg) {
        switch (level) {
            case LogLevel.Debug:
                Log.d(Tag, msg);
                break;
            case LogLevel.Info:
                Log.i(Tag, msg);
                break;
            case LogLevel.Waring:
                Log.w(Tag, msg);
                break;
            case LogLevel.Error:
                Log.e(Tag, msg);
                break;
        }
    }

    public static void Log(int level, String msg) {
        msg = LogLevel.LogTag[level] + "\t" + Tag + ":\t\t\t" + msg;
        AdbLog(level, msg);
    }

    public static void d(String msg) {
        Log(LogLevel.Debug, msg);
    }

    public static void i(String msg) {
        Log(LogLevel.Info, msg);
    }

    public static void w(String msg) {
        Log(LogLevel.Waring, msg);
    }

    public static void e(String msg) {
        Log(LogLevel.Error, msg);
    }
}
